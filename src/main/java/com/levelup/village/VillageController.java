package com.levelup.village;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.persistence.PersistentDataType;

import com.levelup.LevelUp;
import com.levelup.LevelUpIcon;
import com.levelup.chunk.ChunkController;
import com.levelup.player.PlayerController;
import com.levelup.player.PlayerData;

import net.md_5.bungee.api.ChatColor;

public class VillageController {

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

		return villages;
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

			plugin.getLogger().info("새로운 마을 [" + villageName + "] 을(를) 데이터베이스에 추가되었습니다.");

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
		plugin.getLogger().info("마을 [" + vd.getName() + "] 이(가) 삭제되었습니다.");

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

			plugin.getLogger().info("유저 [" + pd.getUsername() + "] 이(가) 마을 [" + villageName + "] 에 가입되었습니다.");

			pstmt.executeUpdate();
			pstmt.close();

			pd.setVillage(villageId);

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

			plugin.getLogger().info("유저 [" + pd.getUsername() + "] 이(가) 마을 [" + vd.getName() + "] 에 가입되었습니다.");

			pstmt.executeUpdate();
			pstmt.close();

			pd.setVillage(villageId);

		} 
	}

	public static void deleteUser(LevelUp plugin, UUID uuid) throws SQLException {
		PlayerData pd = plugin.players.get(uuid);
		VillageData vd = plugin.villages.get(pd.getVillage());
		
		Connection conn = plugin.mysql.getConnection();

		String sql = "UPDATE player SET village = NULL WHERE uuid = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, pd.getUuid().toString());
		pstmt.executeUpdate();

		plugin.getLogger().info("유저 [" + pd.getUsername() + "] 이(가) 마을 [" + vd.getName() + "] 에서 탈퇴되었습니다.");
		
		pd.setVillage(0);
		
		if (vd != null) {
			if (vd.getPresident() != null && vd.getPresident().equals(pd.getUuid())) {
				pstmt.clearParameters();
				sql = "UPDATE village SET president = NULL WHERE id = ?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, pd.getVillage());
				pstmt.executeUpdate();

				plugin.getLogger().info("유저 [" + pd.getUsername() + "] 은(는) 이제 마을 [" + vd.getName() + "] 의 이장이 아닙니다.");

				vd.setPresident(null);
			}
			
			int count = countVillageMembers(plugin, vd.getId());

			if (count < 3) {
				updateDeletionPeriod(plugin, vd.getId(), LocalDate.now());

				for (UUID u : plugin.players.keySet()) {
					PlayerData p = plugin.players.get(u);
					if (p.getVillage() == pd.getVillage()) {
						OfflinePlayer op = plugin.getServer().getOfflinePlayer(u);
						if (op.isOnline()) {
							((Player) op).sendMessage(LevelUpIcon.MAIL.val() + " " + ChatColor.RED
									+ "마을의 총 인원이 3명보다 적습니다. 7일 이내에 새로운 마을원을 모집하세요.");
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

		plugin.getLogger().info("유저 [" + pd.getUsername() + "] 이(가) 마을 [" + vd.getName() + "] 의 이장이 되었습니다.");
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

		plugin.getLogger().info("유저 [" + pd.getUsername() + "] 은(는) 이제 마을 [" + vd.getName() + "] 의 이장이 아닙니다.");
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

		plugin.getLogger().info("마을 [" + oldName + "] 이 [" + newName + "] 로 변경되었습니다.");
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

	public static ItemStack getVillageBook(LevelUp plugin, String context) {
		ItemStack book = new ItemStack(Material.WRITABLE_BOOK);
		BookMeta bookMeta = (BookMeta) book.getItemMeta();
		bookMeta.setDisplayName(ChatColor.GOLD + "마을 신청서");

		List<String> lore = Arrays.asList(ChatColor.WHITE + "♦ 마을 정보를 기입한 후 서명하세요",
				ChatColor.WHITE + "♦ 책을 우클릭하여 마을을 생성하세요");
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

	public static void updateTax(LevelUp plugin) throws SQLException {
		LocalDateTime now = LocalDateTime.now();
		if (now.getDayOfWeek().equals(DayOfWeek.SUNDAY) && now.getHour() == 0 && now.getMinute() == 0) {
			for (int villageId : plugin.villageChunks.keySet()) {
				VillageData vd = plugin.villages.get(villageId);
				int currTax = vd.getLastTax();
				int count = countVillageMembers(plugin, villageId);
				int newTax = count * TAX_RATE;
				setLastTax(plugin, villageId, currTax + newTax);
			}
		}
	}

	public static void taxUpdateMessage(LevelUp plugin, Player player) {
		PlayerData pd = plugin.players.get(player.getUniqueId());

		if (pd.getVillage() > 0 && plugin.villageChunks.containsKey(pd.getVillage())) {
			VillageData vd = plugin.villages.get(pd.getVillage());
			LocalDate today = LocalDate.now();
			LocalDate lastPaidWeek = vd.getLastTaxPaid().minusDays(vd.getLastTaxPaid().getDayOfWeek().getValue());
			int dayOfWeek = today.getDayOfWeek().getValue();
			if (today.getDayOfWeek().equals(DayOfWeek.SUNDAY))
				dayOfWeek = 0;
			LocalDate thisWeek = today.minusDays(dayOfWeek);
			LocalDate dueDate = today.plusDays(6 - dayOfWeek);

			if (vd.getLastTaxPaid() == null || lastPaidWeek.until(thisWeek, ChronoUnit.DAYS) > 0) {

				long overdue = lastPaidWeek.until(today, ChronoUnit.DAYS) - 6;

				if (overdue < 8) {
					player.sendMessage(LevelUpIcon.MAIL.val() + " " + ChatColor.GOLD + "내야할 마을 세금이 있습니다 "
							+ ChatColor.RED + "(" + dueDate.toString() + " 까지)");

				} else if (overdue < 15) {
					player.sendMessage(LevelUpIcon.MAIL.val() + " " + ChatColor.RED + "연체된 마을 세금이 있습니다 " + ChatColor.RED
							+ "(연체 " + (overdue - 7) + "일차)");
					player.sendMessage(
							LevelUpIcon.MAIL.val() + " " + ChatColor.RED + "마을 세금이 7일 이상 연체될 시 마을 청크 보호가 사라집니다!");
				}
			}
		}
	}

	public static void checkTaxOverdue(LevelUp plugin) throws SQLException {
		for (int villageId : plugin.villageChunks.keySet()) {
			VillageData vd = plugin.villages.get(villageId);
			LocalDate today = LocalDate.now();
			LocalDate lastPaidWeek = vd.getLastTaxPaid().minusDays(vd.getLastTaxPaid().getDayOfWeek().getValue());

			long overdue = lastPaidWeek.until(today, ChronoUnit.DAYS) - 6;

			if (overdue > 14) {
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
