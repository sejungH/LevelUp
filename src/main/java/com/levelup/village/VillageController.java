package com.levelup.village;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.persistence.PersistentDataType;

import com.levelup.LevelUp;
import com.levelup.LevelUpController;
import com.levelup.LevelUpIcon;
import com.levelup.chat.ChatController;
import com.levelup.chunk.ChunkController;
import com.levelup.money.MoneyController;
import com.levelup.player.PlayerController;
import com.levelup.player.PlayerData;

import net.md_5.bungee.api.ChatColor;

public class VillageController {

	public static LocalDate taxLastUpdate;
	public static final int TAX_RATE = 10;

	public static Map<Integer, VillageData> getVillages(LevelUp plugin) throws SQLException {
		Connection conn = plugin.mysql.getConnection();

		String sql = "SELECT * FROM village";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();

		Map<Integer, VillageData> villages = new HashMap<Integer, VillageData>();

		while (rs.next()) {
			int id = rs.getInt("id");
			UUID president = rs.getString("president") == null ? null : UUID.fromString(rs.getString("president"));

			String spawn = rs.getString("spawn");
			int[] spawnArr = null;
			if (spawn != null) {
				spawnArr = new int[3];
				String sub = spawn.substring(1, spawn.length() - 1);
				for (int i = 0; i < 3; i++) {
					spawnArr[i] = Integer.parseInt(sub.split(",")[i].trim());
				}
			}

			LocalDate lastTaxPaid = LocalDate.parse(rs.getString("last_tax_paid"));
			LocalDate deletionPeriod = rs.getString("deletion_period") == null ? null
					: LocalDate.parse(rs.getString("deletion_period"));

			villages.put(id, new VillageData(id, rs.getString("name"), president, spawnArr, rs.getInt("last_tax"),
					lastTaxPaid, deletionPeriod));
		}

		rs.close();
		pstmt.close();

		plugin.getServer().getConsoleSender().sendMessage("[" + plugin.getName() + "] " + ChatColor.GREEN + "Loaded "
				+ ChatColor.YELLOW + villages.size() + ChatColor.GREEN + " Village Data");

		getTaxLastUpdate(plugin);

		return villages;
	}

	public static Map<UUID, Integer> getVillageApplies(LevelUp plugin) throws SQLException {
		Connection conn = plugin.mysql.getConnection();

		String sql = "SELECT * FROM village_apply";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();

		Map<UUID, Integer> villageApplies = new HashMap<UUID, Integer>();

		while (rs.next()) {
			UUID uuid = UUID.fromString(rs.getString("uuid"));
			int village = rs.getInt("village");
			villageApplies.put(uuid, village);
		}

		rs.close();
		pstmt.close();

		plugin.getServer().getConsoleSender().sendMessage("[" + plugin.getName() + "] " + ChatColor.GREEN + "Loaded "
				+ ChatColor.YELLOW + villageApplies.size() + ChatColor.GREEN + " Village Apply Data");

		return villageApplies;
	}

	public static int getVillageId(LevelUp plugin, String villageName) {
		for (int id : plugin.villages.keySet()) {
			VillageData vd = plugin.villages.get(id);
			if (vd.getName().equalsIgnoreCase(villageName)) {
				return id;
			}
		}

		return -1;
	}

	public static String getVillageName(LevelUp plugin, int villageId) {
		VillageData vd = plugin.villages.get(villageId);
		if (vd == null) {
			return null;

		} else {
			return vd.getName();
		}
	}

	public static List<PlayerData> getVillageMembers(LevelUp plugin, int villageId) {

		List<PlayerData> players = new ArrayList<PlayerData>();

		for (UUID uuid : plugin.players.keySet()) {
			PlayerData pd = plugin.players.get(uuid);
			if (pd.getVillage() == villageId) {
				players.add(pd);
			}
		}

		return players;
	}

	public static int countVillageMembers(LevelUp plugin, int villageId) {
		int count = 0;
		for (UUID uuid : plugin.players.keySet()) {
			PlayerData pd = plugin.players.get(uuid);
			if (pd.getVillage() == villageId) {
				count++;
			}
		}

		return count;
	}

	public static int addVillage(LevelUp plugin, String villageName) {
		Connection conn = plugin.mysql.getConnection();

		try {
			LocalDate today = LocalDate.now();
			LocalDate lastTaxPaid = today.minusDays(today.getDayOfWeek().getValue() + 1);

			String sql = "INSERT INTO village (name, last_tax_paid) VALUES (?, ?)";

			PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, villageName);
			pstmt.setString(2, lastTaxPaid.toString());
			pstmt.executeUpdate();

			plugin.getLogger().info("새로운 마을 [" + villageName + "] 을(를) 데이터베이스에 추가되었습니다");

			ResultSet rs = pstmt.getGeneratedKeys();

			if (rs.next()) {
				int id = rs.getInt(1);

				rs.close();
				pstmt.close();

				VillageData vd = new VillageData(id, villageName, null, null, 0, lastTaxPaid, null);
				plugin.villages.put(id, vd);

				return id;

			} else {
				rs.close();
				pstmt.close();

				return -2;
			}

		} catch (SQLException e) {
			return -1;
		}
	}

	public static void deleteVillage(LevelUp plugin, int villageId) throws SQLException {
		Connection conn = plugin.mysql.getConnection();

		String sql = "DELETE FROM village WHERE id = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, villageId);
		pstmt.executeUpdate();
		pstmt.close();

		VillageData vd = plugin.villages.get(villageId);
		plugin.getLogger().info("마을 [" + vd.getName() + "] 이(가) 삭제되었습니다");

		plugin.villages.remove(villageId);
		if (plugin.villageChunks.containsKey(villageId))
			plugin.villageChunks.remove(villageId);

		for (UUID uuid : plugin.players.keySet()) {
			PlayerData pd = plugin.players.get(uuid);
			if (pd.getVillage() == villageId)
				deleteUser(plugin, uuid);
		}
	}

	public static int addUser(LevelUp plugin, String villageName, PlayerData pd) throws SQLException {
		Connection conn = plugin.mysql.getConnection();
		int villageId = getVillageId(plugin, villageName);

		if (villageId > 0) {

			String sql = "UPDATE player SET village = ? WHERE uuid = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, villageId);
			pstmt.setString(2, pd.getUuid().toString());

			plugin.getLogger().info("유저 [" + pd.getUsername() + "] 이(가) 마을 [" + villageName + "] 에 가입되었습니다");

			pstmt.executeUpdate();
			pstmt.close();

			pd.setVillage(villageId);

			VillageData vd = plugin.villages.get(villageId);
			if (vd.getDeletionPeriod() != null && countVillageMembers(plugin, villageId) >= 3) {
				updateDeletionPeriod(plugin, villageId, null);
			}

			return villageId;

		} else {
			return -1;
		}
	}

	public static void addUser(LevelUp plugin, int villageId, UUID uuid) throws SQLException {
		PlayerData pd = plugin.players.get(uuid);
		VillageData vd = plugin.villages.get(villageId);
		Connection conn = plugin.mysql.getConnection();

		if (vd != null) {

			String sql = "UPDATE player SET village = ? WHERE uuid = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, villageId);
			pstmt.setString(2, pd.getUuid().toString());

			plugin.getLogger().info("유저 [" + pd.getUsername() + "] 이(가) 마을 [" + vd.getName() + "] 에 가입되었습니다");

			pstmt.executeUpdate();
			pstmt.close();

			pd.setVillage(villageId);

			if (vd.getDeletionPeriod() != null && countVillageMembers(plugin, villageId) >= 3) {
				updateDeletionPeriod(plugin, villageId, null);
			}
		}
	}

	public static void deleteUser(LevelUp plugin, UUID uuid) throws SQLException {
		PlayerData pd = plugin.players.get(uuid);

		Connection conn = plugin.mysql.getConnection();

		int villageId = pd.getVillage();

		String sql = "UPDATE player SET village = NULL WHERE uuid = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, pd.getUuid().toString());
		pstmt.executeUpdate();

		plugin.getLogger().info("유저 [" + pd.getUsername() + "] 이(가) 마을 에서 탈퇴되었습니다");

		pd.setVillage(0);

		VillageData vd = plugin.villages.get(villageId);
		if (vd != null) {
			if (vd.getPresident() != null && vd.getPresident().equals(pd.getUuid())) {
				pstmt.clearParameters();
				sql = "UPDATE village SET president = NULL WHERE id = ?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, pd.getVillage());
				pstmt.executeUpdate();

				plugin.getLogger().info("유저 [" + pd.getUsername() + "] 은(는) 이제 마을 [" + vd.getName() + "] 의 이장이 아닙니다");

				vd.setPresident(null);
			}

			int count = countVillageMembers(plugin, vd.getId());

			if (count < 3) {
				updateDeletionPeriod(plugin, vd.getId(), LocalDate.now());

				for (UUID u : plugin.players.keySet()) {
					PlayerData p = plugin.players.get(u);
					if (p.getVillage() == villageId) {
						OfflinePlayer op = plugin.getServer().getOfflinePlayer(u);
						if (op.isOnline()) {
							((Player) op).sendMessage(LevelUpIcon.MAIL.val() + " " + ChatColor.RED
									+ "마을의 총 인원이 3명보다 적습니다 7일 이내에 새로운 마을원을 모집하세요.");
						}
					}
				}
			}

		}

		pstmt.close();
	}

	public static void registerPresident(LevelUp plugin, PlayerData pd) throws SQLException {
		Connection conn = plugin.mysql.getConnection();
		VillageData vd = plugin.villages.get(pd.getVillage());

		String sql = "UPDATE village SET president = ? WHERE id = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, pd.getUuid().toString());
		pstmt.setInt(2, pd.getVillage());
		pstmt.executeUpdate();
		pstmt.close();

		plugin.getLogger().info("유저 [" + pd.getUsername() + "] 이(가) 마을 [" + vd.getName() + "] 의 이장이 되었습니다");
		vd.setPresident(pd.getUuid());
	}

	public static void dropPresident(LevelUp plugin, PlayerData pd) throws SQLException {
		Connection conn = plugin.mysql.getConnection();
		VillageData vd = plugin.villages.get(pd.getVillage());

		String sql = "UPDATE village SET president = NULL WHERE id = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, pd.getVillage());
		pstmt.executeUpdate();
		pstmt.close();

		plugin.getLogger().info("유저 [" + pd.getUsername() + "] 은(는) 이제 마을 [" + vd.getName() + "] 의 이장이 아닙니다");
		vd.setPresident(null);
	}

	public static void setVillageSpawn(LevelUp plugin, int villageId, int[] coordinate) throws SQLException {
		Connection conn = plugin.mysql.getConnection();
		VillageData vd = plugin.villages.get(villageId);

		String sql = "UPDATE village SET spawn = ? WHERE id = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);

		if (coordinate == null) {
			pstmt.setNull(1, java.sql.Types.NULL);
		} else {
			pstmt.setString(1, Arrays.toString(coordinate));
		}
		pstmt.setInt(2, villageId);
		pstmt.executeUpdate();
		pstmt.close();

		if (coordinate == null) {
			plugin.getLogger().info("마을 [" + vd.getName() + "] 의 스폰이 삭제되었습니다");
		} else {
			plugin.getLogger().info("마을 [" + vd.getName() + "] 의 스폰 좌표가 (" + coordinate[0] + ", " + coordinate[1] + ", "
					+ coordinate[2] + ") 로 변경되었습니다");
		}
		vd.setSpawn(coordinate);
	}

	public static void renameVillage(LevelUp plugin, String oldName, String newName) throws SQLException {
		Connection conn = plugin.mysql.getConnection();
		int villageId = getVillageId(plugin, oldName);
		VillageData vd = plugin.villages.get(villageId);

		String sql = "UPDATE village SET name = ? WHERE id = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, newName);
		pstmt.setInt(2, villageId);
		pstmt.executeUpdate();
		pstmt.close();

		plugin.getLogger().info("마을 [" + oldName + "] 이 [" + newName + "] 로 변경되었습니다");
		vd.setName(newName);

		for (UUID uuid : plugin.players.keySet()) {
			OfflinePlayer player = plugin.getServer().getOfflinePlayer(uuid);
			if (player != null && player.isOnline()) {
				PlayerController.updateListName(plugin, (Player) player);
			}
		}
	}

	public static void setLastTax(LevelUp plugin, int villageId, int tax) throws SQLException {
		Connection conn = plugin.mysql.getConnection();
		String sql = "UPDATE village SET last_tax = ? where id = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, tax);
		pstmt.setInt(2, villageId);
		pstmt.executeUpdate();
		pstmt.close();

		VillageData vd = plugin.villages.get(villageId);
		vd.setLastTax(tax);
	}

	public static void updateLastTaxPaid(LevelUp plugin, int villageId, LocalDate lastTax) throws SQLException {
		Connection conn = plugin.mysql.getConnection();
		String sql = "UPDATE village SET last_tax_paid = ? where id = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, lastTax.toString());
		pstmt.setInt(2, villageId);
		pstmt.executeUpdate();
		pstmt.close();

		VillageData vd = plugin.villages.get(villageId);
		vd.setLastTaxPaid(lastTax);
	}

	public static void updateDeletionPeriod(LevelUp plugin, int villageId, LocalDate deletionPeriod)
			throws SQLException {
		Connection conn = plugin.mysql.getConnection();
		String sql = "UPDATE village SET deletion_period = ? where id = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);

		if (deletionPeriod == null) {
			pstmt.setNull(1, java.sql.Types.NULL);
		} else {
			pstmt.setString(1, deletionPeriod.toString());
		}
		pstmt.setInt(2, villageId);
		pstmt.executeUpdate();
		pstmt.close();

		VillageData vd = plugin.villages.get(villageId);
		vd.setDeletionPeriod(deletionPeriod);
	}

	public static void applyVillage(LevelUp plugin, UUID uuid, int villageId) throws SQLException {

		Connection conn = plugin.mysql.getConnection();
		String sql = "INSERT INTO village_apply (uuid, village) VALUES (?, ?)";
		PreparedStatement pstmt = conn.prepareStatement(sql);

		pstmt.setString(1, uuid.toString());
		pstmt.setInt(2, villageId);
		pstmt.executeUpdate();
		pstmt.close();

		plugin.villageApplies.put(uuid, villageId);
	}

	public static void updateVillageApply(LevelUp plugin, UUID uuid, int villageId) throws SQLException {
		Connection conn = plugin.mysql.getConnection();
		String sql = "UPDATE village_apply SET village = ? where uuid = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);

		pstmt.setInt(1, villageId);
		pstmt.setString(2, uuid.toString());
		pstmt.executeUpdate();
		pstmt.close();

		plugin.villageApplies.put(uuid, villageId);
	}

	public static void deleteVillageApply(LevelUp plugin, UUID uuid) throws SQLException {
		Connection conn = plugin.mysql.getConnection();
		String sql = "DELETE FROM village_apply WHERE uuid = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);

		pstmt.setString(1, uuid.toString());
		pstmt.executeUpdate();
		pstmt.close();

		plugin.villageApplies.remove(uuid);
	}

	public static void createVillage(LevelUp plugin, Player player, BookMeta book) throws SQLException {
		player.sendMessage(ChatColor.GREEN + "마을을 생성하는 중입니다..");

		String context = ChatColor.stripColor(book.getPage(1));
		String villageName = null;
		String president = null;
		List<String> villager = new ArrayList<String>();

		for (String line : context.split("\n")) {
			if (line.startsWith("마을:")) {
				int index = line.indexOf("마을:") + 3;
				villageName = line.substring(index).trim();

			} else if (line.startsWith("이장:")) {
				int index = line.indexOf("이장:") + 3;
				president = line.substring(index).trim();

			} else if (line.startsWith("마을원:")) {
				int index = line.indexOf("마을원:") + 4;
				for (String p : line.substring(index).split(",")) {
					villager.add(LevelUpController.sanitizeString(p.trim()));
				}
			}
		}

		PlayerData presidentData = PlayerController.getPlayerData(plugin, president);

		int[] coordinate = new int[3];
		coordinate[0] = (int) player.getLocation().getX();
		coordinate[1] = (int) player.getLocation().getY();
		coordinate[2] = (int) player.getLocation().getZ();

		Chunk chunk = player.getLocation().getChunk();

		int villageId = VillageController.addVillage(plugin, villageName);
		VillageController.addUser(plugin, villageId, presidentData.getUuid());
		VillageController.registerPresident(plugin, presidentData);
		for (String username : villager) {
			if (!username.equalsIgnoreCase(president)) {
				PlayerData playerData = PlayerController.getPlayerData(plugin, username);
				VillageController.addUser(plugin, villageId, playerData.getUuid());
			}
		}
		VillageController.setVillageSpawn(plugin, villageId, coordinate);
		VillageController.updateTax(plugin, villageId);

		if (!plugin.villageChunks.containsKey(villageId))
			plugin.villageChunks.put(villageId, new ArrayList<Chunk>());

		ChunkController.addVillageChunk(plugin, villageId, chunk);
		ChunkController.displayVillageChunkBorder(plugin, player, chunk, Color.GREEN, 5);

		player.sendMessage(
				ChatColor.GREEN + "축하합니다! 마을 [" + ChatColor.GOLD + villageName + ChatColor.GREEN + "] 이(가) 생성되었습니다!");
		player.performCommand("마을 정보 " + villageName);
		player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
	}

	public static ItemStack getVillageBook(LevelUp plugin, String context) {
		ItemStack book = new ItemStack(Material.WRITABLE_BOOK);
		BookMeta bookMeta = (BookMeta) book.getItemMeta();
		bookMeta.setDisplayName(ChatController.gradient("마을 신청서", ChatColor.of("#724c35"), ChatColor.of("#e2d1c1")));

		List<String> lore = Arrays.asList(ChatColor.GRAY + "♦ 마을 정보를 기입한 후 서명하세요",
				ChatColor.GRAY + "♦ 마을을 생성하고 싶은 곳에 서서 책을 우클릭하세요");
		bookMeta.setLore(lore);

		NamespacedKey namespacedKey = new NamespacedKey(plugin, "village_application");
		bookMeta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, "");

		if (context == null) {
			String text = ChatColor.BOLD + "\n      < 마을신청서 >" + ChatColor.RESET + "\n\n";
			text += ChatColor.BLACK + "마을:  \n";
			text += ChatColor.BLACK + "이장:  \n";
			text += ChatColor.BLACK + "마을원:  \n\n";
			text += ChatColor.RED + "※ 마을이름에 " + ChatColor.DARK_RED + ChatColor.UNDERLINE + "공백" + ChatColor.RESET
					+ ChatColor.RED + "을 포함할 수 없습니다\n";
			text += "※ 마을원 아이디를 " + ChatColor.DARK_RED + ChatColor.UNDERLINE + "콤마(,)" + ChatColor.RESET + ChatColor.RED
					+ "로 구분해서 적어주세요";
			bookMeta.addPage(text);

		} else {
			bookMeta.addPage(context);
		}

		book.setItemMeta(bookMeta);

		return book;
	}

	public static void getTaxLastUpdate(LevelUp plugin) throws SQLException {
		Connection conn = plugin.mysql.getConnection();

		String sql = "SELECT * FROM village_tax ORDER BY updated DESC LIMIT 1";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();

		if (rs.next()) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			taxLastUpdate = LocalDateTime.parse(rs.getString("updated"), formatter).toLocalDate();
		} else {
			taxLastUpdate = LocalDate.of(1990, 1, 1);
		}

		rs.close();
		pstmt.close();
	}

	public static void setTaxLastUpdate(LevelUp plugin, String log) throws SQLException {
		Connection conn = plugin.mysql.getConnection();

		String sql = "INSERT INTO village_tax (updated, log) VALUES (?, ?)";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, LocalDateTime.now().toString());
		pstmt.setString(2, log);
		pstmt.executeUpdate();
		pstmt.close();

		taxLastUpdate = LocalDate.now();
	}

	public static void updateTax(LevelUp plugin, int villageId) throws SQLException {
		VillageData vd = plugin.villages.get(villageId);
		int currTax = vd.getLastTax();
		int count = countVillageMembers(plugin, villageId);
		int newTax = count * TAX_RATE;
		setLastTax(plugin, villageId, currTax + newTax);
	}

	public static void updateTaxWeekly(LevelUp plugin) throws SQLException {
		LocalDate now = LocalDate.now();

		long weeksBetween = ChronoUnit.WEEKS.between(
				taxLastUpdate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)),
				now.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)));
		if (weeksBetween > 0) {
			int count = 0;
			for (int villageId : plugin.villageChunks.keySet()) {
				updateTax(plugin, villageId);
				count++;
			}
			setTaxLastUpdate(plugin, "villages applied: " + count);
			for (Player player : plugin.getServer().getOnlinePlayers()) {
				VillageController.taxUpdateMessage(plugin, player);
				player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
			}
		}
	}

	public static void payTax(LevelUp plugin, VillageData vd, Player player) throws SQLException {
		PlayerData pd = plugin.players.get(player.getUniqueId());
		if (pd.getBalance() >= vd.getLastTax()) {
			MoneyController.withdrawMoeny(plugin, vd.getLastTax(), player.getUniqueId());
			setLastTax(plugin, vd.getId(), 0);
		}
	}

	public static void taxUpdateMessage(LevelUp plugin, Player player) {
		PlayerData pd = plugin.players.get(player.getUniqueId());

		if (pd.getVillage() > 0 && plugin.villageChunks.containsKey(pd.getVillage())) {
			VillageData vd = plugin.villages.get(pd.getVillage());

			if (vd.getLastTaxPaid() != null) {
				LocalDate now = LocalDate.now();
				long weeksBetween = ChronoUnit.WEEKS.between(
						vd.getLastTaxPaid().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)),
						now.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)));

				if (weeksBetween == 1) {
					LocalDate dueDate = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
					player.sendMessage(LevelUpIcon.MAIL.val() + " " + ChatColor.GOLD + "내야할 마을 세금이 있습니다 "
							+ ChatColor.RED + "(" + dueDate.toString() + " 까지)");

				} else if (weeksBetween == 2) {
					LocalDate lastWeekSaturday = now.minusWeeks(1)
							.with(TemporalAdjusters.previousOrSame(DayOfWeek.SATURDAY));
					long overdueDays = ChronoUnit.DAYS.between(lastWeekSaturday, now);

					player.sendMessage(LevelUpIcon.MAIL.val() + " " + ChatColor.RED + "연체된 마을 세금이 있습니다 " + ChatColor.RED
							+ "(연체 " + overdueDays + "일차)");
					player.sendMessage(
							LevelUpIcon.MAIL.val() + " " + ChatColor.RED + "마을 세금이 7일 이상 연체될 시 마을 청크 보호가 사라집니다!");
				}
			}
		}
	}

	public static void deletionUpdateMessage(LevelUp plugin, Player player) {
		PlayerData pd = plugin.players.get(player.getUniqueId());

		if (pd.getVillage() > 0) {
			VillageData vd = plugin.villages.get(pd.getVillage());
			LocalDate today = LocalDate.now();

			if (vd.getDeletionPeriod() != null) {
				long period = 7 - vd.getDeletionPeriod().until(today, ChronoUnit.DAYS);
				player.sendMessage(LevelUpIcon.MAIL.val() + " " + ChatColor.RED + "마을의 총 인원이 3명보다 적습니다 " + period
						+ "일 이내에 새로운 마을원을 모집하세요.");
			}
		}

	}

	public static void checkTaxOverdue(LevelUp plugin) throws SQLException {
		for (int villageId : plugin.villageChunks.keySet()) {
			VillageData vd = plugin.villages.get(villageId);
			LocalDate now = LocalDate.now();

			long weeksBetween = ChronoUnit.WEEKS.between(
					vd.getLastTaxPaid().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)),
					now.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)));

			if (weeksBetween > 2) {
				List<Chunk> chunks = plugin.villageChunks.get(villageId);

				for (Chunk c : new ArrayList<Chunk>(chunks)) {
					try {
						ChunkController.deleteVillageChunk(plugin, villageId, c);

					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				plugin.villageChunks.remove(villageId);
				plugin.getLogger().info("마을 [" + vd.getName() + "] 의 세금이 장기 연체되어 마을에서 소유한 청크가 삭제되었습니다");

				for (UUID uuid : plugin.players.keySet()) {
					PlayerData pd = plugin.players.get(uuid);
					if (pd.getVillage() == villageId) {
						OfflinePlayer p = plugin.getServer().getOfflinePlayer(uuid);
						if (p.isOnline()) {
							((Player) p).sendMessage(LevelUpIcon.MAIL.val() + " " + ChatColor.RED
									+ "마을 세금이 7일 이상 연체되어 마을 청크 보호가 모두 사라졌습니다");
						}
					}
				}

				setVillageSpawn(plugin, villageId, null);
			}
		}
	}

	public static void checkDeletionPeriod(LevelUp plugin) throws SQLException {
		Set<Integer> keySet = new HashSet<Integer>(plugin.villages.keySet());

		for (int villageId : keySet) {
			VillageData vd = plugin.villages.get(villageId);
			LocalDate today = LocalDate.now();

			if (vd.getDeletionPeriod() != null) {
				long period = vd.getDeletionPeriod().until(today, ChronoUnit.DAYS);

				if (period > 7) {
					plugin.getLogger().info("마을 [" + vd.getName() + "] 의 인원이 7일 이상 3명 이하로 유지되어 마을이 삭제되었습니다");
					deleteVillage(plugin, villageId);
				}
			}
		}
	}

}
