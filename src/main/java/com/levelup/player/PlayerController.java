package com.levelup.player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.levelup.LevelUp;
import com.levelup.LevelUpIcon;
import com.levelup.chat.ChatController;
import com.levelup.chunk.ChunkController;
import com.levelup.village.VillageData;

import net.md_5.bungee.api.ChatColor;

public class PlayerController {

	public static Set<UUID> invinciblePlayers = new HashSet<UUID>();
	public static Map<UUID, List<Integer>> playerScheduler = new HashMap<UUID, List<Integer>>();

	public static Map<UUID, PlayerData> getPlayers(LevelUp plugin) throws SQLException {
		Connection conn = plugin.mysql.getConnection();

		String sql = "SELECT * FROM player";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();

		Map<UUID, PlayerData> players = new HashMap<UUID, PlayerData>();

		while (rs.next()) {
			OffsetDateTime dateTime = rs.getObject("last_online", OffsetDateTime.class);
			PlayerData pd = new PlayerData(UUID.fromString(rs.getString("uuid")), rs.getString("username"),
					rs.getString("nickname"), rs.getInt("balance"), rs.getInt("village"),
					dateTime == null ? null : dateTime.toLocalDateTime());
			players.put(UUID.fromString(rs.getString("uuid")), pd);
		}

		rs.close();
		pstmt.close();

		plugin.getServer().getConsoleSender().sendMessage("[" + plugin.getName() + "] " + ChatColor.GREEN + "Loaded "
				+ ChatColor.YELLOW + players.size() + ChatColor.GREEN + " Player Data");

		return players;
	}

	public static void addPlayer(LevelUp plugin, Player player) throws SQLException {
		Connection conn = plugin.mysql.getConnection();
		String sql = "INSERT INTO player (uuid, username, balance, last_online) VALUES (?, ?, ?, ?)";
		PreparedStatement pstmt = conn.prepareStatement(sql);

		Date date = new Date();
		Timestamp param = new Timestamp(date.getTime());

		pstmt.setString(1, player.getUniqueId().toString());
		pstmt.setString(2, player.getName());
		pstmt.setInt(3, 0);
		pstmt.setObject(4, param);

		plugin.getLogger().info("새로운 유저 [" + player.getName() + "] 을(를) 데이터베이스에 추가합니다");

		pstmt.executeUpdate();
		pstmt.close();

		PlayerData pd = new PlayerData(player.getUniqueId(), player.getName(), null, 0, 0, param.toLocalDateTime());
		plugin.players.put(player.getUniqueId(), pd);
	}

	public static void updatePlayer(LevelUp plugin, Player player) throws SQLException {
		Connection conn = plugin.mysql.getConnection();
		String sql = "UPDATE player SET username = ? WHERE uuid = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);

		pstmt.setString(1, player.getName());
		pstmt.setString(2, player.getUniqueId().toString());
		pstmt.executeUpdate();
		pstmt.close();

		plugin.getLogger().info("유저 [" + player.getName() + "] 의 닉네임이 변경되었습니다");

		PlayerData pd = plugin.players.get(player.getUniqueId());
		pd.setUsername(player.getName());
	}

	public static void updateLastOnline(LevelUp plugin, UUID uuid) throws SQLException {
		PlayerData pd = plugin.players.get(uuid);

		Connection conn = plugin.mysql.getConnection();
		String sql = "UPDATE player SET last_online = ? WHERE uuid = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);

		Date date = new Date();
		Timestamp param = new Timestamp(date.getTime());

		pstmt.setObject(1, param);
		pstmt.setString(2, uuid.toString());

		pstmt.executeUpdate();
		pstmt.close();

		pd.setLastOnline(param.toLocalDateTime());
	}

	public static void updateListName(LevelUp plugin, Player player) {
		PlayerData pd = plugin.players.get(player.getUniqueId());

		String playerColor = pd.getNicknameColor();
		String playerName;
		if (playerColor != null) {
			playerName = ChatColor.of(playerColor) + pd.getName();
		} else {
			playerName = ChatColor.WHITE + pd.getName();
		}

		String villageName;
		if (pd.getVillage() > 0) {
			VillageData vd = plugin.villages.get(pd.getVillage());
			String villageColor = vd.getColor();

			if (villageColor != null) {
				villageName = ChatColor.of(villageColor) + "[" + vd.getName() + "] ";

			} else {
				villageName = "[" + vd.getName() + "] ";
			}

			player.setPlayerListName(villageName + playerName);

		} else {
			player.setPlayerListName(playerName);
		}

		if (player.isOp()) {
			player.setPlayerListName(LevelUpIcon.ADMIN.val() + " " + player.getPlayerListName());
		}

	}

	public static void updateListFooter(LevelUp plugin, Player player) {
		String onlineCount = ChatController.gradient("   동접자 수", ChatColor.of("#00A2E8"), ChatColor.of("#99D9EA"))
				+ ChatColor.WHITE + ": " + plugin.getServer().getOnlinePlayers().size() + " / "
				+ plugin.getServer().getMaxPlayers();
		String ping = ChatColor.BOLD + ChatController.gradient("지연시간", ChatColor.of("#FFF200"), ChatColor.of("#EFE4B0"))
				+ ChatColor.WHITE + ": " + player.getPing();
		player.setPlayerListFooter("\n" + onlineCount + "       " + ping + "   \n");
	}

	public static void updateNickname(LevelUp plugin, UUID uuid, String nickname) throws SQLException {
		PlayerData pd = plugin.players.get(uuid);

		Connection conn = plugin.mysql.getConnection();
		String sql = "UPDATE player SET nickname = ? WHERE uuid = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);

		pstmt.setString(1, nickname);
		pstmt.setString(2, uuid.toString());

		pstmt.executeUpdate();
		pstmt.close();

		pd.setNickname(nickname);
	}

	public static PlayerData getPlayerData(LevelUp plugin, String name) {
		for (UUID uuid : plugin.players.keySet()) {
			PlayerData pd = plugin.players.get(uuid);
			if (pd.getUsername().equalsIgnoreCase(name) || pd.getName().equalsIgnoreCase(name)) {
				return pd;
			}
		}

		return null;
	}

	public static boolean isNicknameUnique(LevelUp plugin, PlayerData playerData, String nickname) {
		for (UUID uuid : plugin.players.keySet()) {
			PlayerData pd = plugin.players.get(uuid);
			if (!playerData.getUuid().equals(pd.getUuid()) && pd.getNickname() != null
					&& pd.getNicknameWithoutColor().equalsIgnoreCase(nickname)) {
				return false;
			}
		}

		return true;
	}

	public static List<String> getOnlinePlayerNames(LevelUp plugin) {
		List<String> names = new ArrayList<String>();
		for (Player p : plugin.getServer().getOnlinePlayers()) {
			PlayerData pd = plugin.players.get(p.getUniqueId());
			names.add(pd.getName());
		}

		return names;
	}

	public static void checkRestUser(LevelUp plugin) throws SQLException {
		Set<UUID> keySet = new HashSet<UUID>(plugin.playerChunks.keySet());

		for (UUID uuid : keySet) {
			PlayerData pd = plugin.players.get(uuid);
			LocalDate today = LocalDate.now();
			long rest = pd.getLastOnline().toLocalDate().until(today, ChronoUnit.DAYS);

			if (rest > 14) {
				if (plugin.playerChunks.containsKey(uuid)) {
					ChunkController.deleteAllPlayerChunks(plugin, uuid);
					plugin.getLogger().info(pd.getUsername() + " 님이 장기 미접속하여 소유한 청크가 삭제되었습니다");
				}
			}
		}
	}

	public static boolean hasItem(Player player, ItemStack item) {
		if (player.getInventory().contains(item)) {
			return true;
		} else {
			for (ItemStack armor : player.getInventory().getArmorContents()) {
				if (armor != null && armor.isSimilar(item)) {
					return true;
				}
			}

			ItemStack offhand = player.getInventory().getItemInOffHand();
			if (offhand != null && offhand.isSimilar(item)) {
				return true;
			}
		}

		return false;
	}
}
