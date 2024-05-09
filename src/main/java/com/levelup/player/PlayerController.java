package com.levelup.player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.levelup.main.LevelUp;

public class PlayerController {
	
	public static Map<UUID, PlayerData> getPlayers(LevelUp plugin, Connection conn) throws SQLException {
		String sql = "SELECT * FROM player";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();
		
		Map<UUID, PlayerData> players = new HashMap<UUID, PlayerData>();
		
		while (rs.next()) {
			OffsetDateTime dateTime = rs.getObject("last_online", OffsetDateTime.class);
			PlayerData pd = new PlayerData(UUID.fromString(rs.getString("uuid")), rs.getString("username"),
					rs.getInt("balance"), rs.getInt("village"), dateTime == null ? null : dateTime.toLocalDateTime());
			System.out.println(pd.toString());
			players.put(UUID.fromString(rs.getString("uuid")), pd);
		}
		
		return players;
	}
	
	public static UUID getPlayerUUID(LevelUp plugin, String username) {
		for (UUID uuid : plugin.players.keySet()) {
			PlayerData pd = plugin.players.get(uuid);
			if (pd.getUsername().equals(username)) {
				return uuid;
			}
		}
		return null;
	}
	
	public static void addPlayer(LevelUp plugin, Connection conn, Player player) throws SQLException {
		String sql = "INSERT INTO player (uuid, username, balance, last_online) VALUES (?, ?, ?, ?)";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		
		Date date = new Date();
		Timestamp param = new Timestamp(date.getTime());
		
		pstmt.setString(1, player.getUniqueId().toString());
		pstmt.setString(2, player.getName());
		pstmt.setInt(3, 0);
		pstmt.setObject(4, param);
		
		plugin.getLogger().info("새로운 유저 [" + player.getName() + "] 을(를) 데이터베이스에 추가합니다.");
		
		pstmt.executeUpdate();
		pstmt.close();
		
		PlayerData pd = new PlayerData(player.getUniqueId(), player.getName(), 0, 0, param.toLocalDateTime());
		plugin.players.put(player.getUniqueId(), pd);
	}
	
	public static void updatePlayer(LevelUp plugin, Connection conn, Player player) throws SQLException {
		String sql = "UPDATE player SET username = ? WHERE uuid = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		
		pstmt.setString(1, player.getName());
		pstmt.setString(2, player.getUniqueId().toString());
		pstmt.executeUpdate();
		pstmt.close();
		
		plugin.getLogger().info("유저 [" + player.getName() + "] 의 닉네임이 변경되었습니다.");
		
		PlayerData pd = plugin.players.get(player.getUniqueId());
		pd.setUsername(player.getName());
	}
	
	public static void updateListOnline(LevelUp plugin, Connection conn, PlayerData pd) throws SQLException {
		String sql = "UPDATE player SET last_online = ? WHERE uuid = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);

		Date date = new Date();
		Timestamp param = new Timestamp(date.getTime());

		pstmt.setObject(1, param);
		pstmt.setString(2, pd.getUuid().toString());
		
		pstmt.executeUpdate();
		pstmt.close();
		
		pd.setLastOnline(param.toLocalDateTime());
	}

	public static PlayerData getPlayerData(LevelUp plugin, String username) {
		for (UUID uuid : plugin.players.keySet()) {
			PlayerData pd = plugin.players.get(uuid);
			if (pd.getUsername().equalsIgnoreCase(username)) {
				return pd;
			}
		}
		
		return null;
	}
	
	public static String getPlayerUsername(LevelUp plugin, UUID uuid)  {
		PlayerData pd = plugin.players.get(uuid);
		return pd.getUsername();
	}
}
