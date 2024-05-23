package com.levelup.friend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.levelup.main.LevelUp;
import com.levelup.player.PlayerController;
import com.levelup.player.PlayerData;

import net.md_5.bungee.api.ChatColor;

public class FriendController {
	
	public static List<FriendData> getFriends(LevelUp plugin) throws SQLException {
		Connection conn = plugin.mysql.getConnection();
		
		List<FriendData> friends = new ArrayList<FriendData>();

		String sql = "SELECT * FROM friend";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();

		int count = 0;
		while (rs.next()) {
			boolean are_friends = rs.getInt("are_friends") == 0 ? false : true;
			friends.add(new FriendData(UUID.fromString(rs.getString("from_player")), UUID.fromString(rs.getString("to_player")), are_friends));
			count++;
		}

		rs.close();
		pstmt.close();
		
		plugin.getLogger()
		.info(ChatColor.GREEN + "Loaded " + ChatColor.YELLOW + count + ChatColor.GREEN + " Friend Data");

		return friends;
	}
	
	public static FriendData getFriendship(LevelUp plugin, String p1, String p2) {
		for (FriendData fd : plugin.friends) {
			PlayerData fp = plugin.players.get(fd.getFromPlayer());
			PlayerData tp = plugin.players.get(fd.getToPlayer());
			
			if ((fp.getUsername().equalsIgnoreCase(p1) && tp.getUsername().equalsIgnoreCase(p2)) 
					|| (fp.getUsername().equalsIgnoreCase(p2) && tp.getUsername().equalsIgnoreCase(p1))) {
				return fd;
			}
		}
		
		return null;
	}

	public static void requestFriend(LevelUp plugin, Connection conn, String fromPlayer, String toPlayer)
			throws SQLException {
		String sql = "INSERT INTO friend (from_player, to_player) "
				+ "(SELECT p1.uuid AS from_player, p2.uuid AS to_player FROM player p1 " + "INNER JOIN player p2 "
				+ "WHERE p1.username = ? AND p2.username = ?)";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, fromPlayer);
		pstmt.setString(2, toPlayer);
		pstmt.executeUpdate();
		pstmt.close();
		
		PlayerData fp = PlayerController.getPlayerData(plugin, fromPlayer);
		PlayerData tp = PlayerController.getPlayerData(plugin, toPlayer);
		
		plugin.friends.add(new FriendData(fp.getUuid(), tp.getUuid(), false));
	}

	public static void acceptFriend(LevelUp plugin, Connection conn, String fromPlayer, String toPlayer)
			throws SQLException {
		String sql = "UPDATE friend " + "INNER JOIN player p1 ON friend.from_player = p1.uuid "
				+ "INNER JOIN player p2 ON friend.to_player = p2.uuid " + "SET are_friends = 1 "
				+ "WHERE p1.username = ? AND p2.username = ?";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, fromPlayer);
		pstmt.setString(2, toPlayer);
		pstmt.executeUpdate();
		pstmt.close();
		
		FriendData fd = getFriendship(plugin, fromPlayer, toPlayer);
		fd.setAreFriends(true);
	}

	public static void rejectFriend(LevelUp plugin, Connection conn, String fromPlayer, String toPlayer)
			throws SQLException {
		String sql = "DELETE friend FROM friend " + "INNER JOIN player p1 ON p1.uuid = friend.from_player "
				+ "INNER JOIN player p2 ON p2.uuid = friend.to_player " + "WHERE p1.username = ? AND p2.username = ?";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, fromPlayer);
		pstmt.setString(2, toPlayer);
		pstmt.executeUpdate();
		pstmt.close();
		
		FriendData fd = getFriendship(plugin, fromPlayer, toPlayer);
		plugin.friends.remove(fd);
	}

	public static void deleteFriend(LevelUp plugin, Connection conn, String p1, String p2) throws SQLException {
		String sql = "DELETE friend FROM friend " + "INNER JOIN player p1 ON friend.from_player = p1.uuid "
				+ "INNER JOIN player p2 ON friend.to_player = p2.uuid " + "WHERE (p1.username = ? AND p2.username = ?) "
				+ "OR (p1.username = ? AND p2.username = ?)";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, p1);
		pstmt.setString(2, p2);
		pstmt.setString(3, p2);
		pstmt.setString(4, p1);
		pstmt.executeUpdate();
		pstmt.close();
		
		FriendData fd = getFriendship(plugin, p1, p2);
		plugin.friends.remove(fd);
	}

	public static List<PlayerData> getRequests(LevelUp plugin, String toPlayer) {	
		List<PlayerData> requests = new ArrayList<PlayerData>();
		
		for (FriendData fd : plugin.friends) {
			if (!fd.areFriends()) {
				PlayerData fp = plugin.players.get(fd.getFromPlayer());
				PlayerData tp = plugin.players.get(fd.getToPlayer());
				if (tp.getUsername().equalsIgnoreCase(toPlayer)) {
					requests.add(fp);
				}
			}
		}
		
		return requests;
	}

	public static List<PlayerData> getFriendList(LevelUp plugin, String username) {
		List<PlayerData> friends = new ArrayList<PlayerData>();
		
		for (FriendData fd : plugin.friends) {
			if (fd.areFriends()) {
				PlayerData fp = plugin.players.get(fd.getFromPlayer());
				PlayerData tp = plugin.players.get(fd.getToPlayer());
				
				if (fp.getUsername().equalsIgnoreCase(username)) {
					friends.add(tp);
					
				} else if (tp.getUsername().equalsIgnoreCase(username)) {
					friends.add(fp);
				}
			}
		}
		
		return friends;
	}

}
