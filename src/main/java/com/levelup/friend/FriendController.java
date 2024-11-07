package com.levelup.friend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.levelup.LevelUp;
import com.levelup.player.PlayerData;

import net.md_5.bungee.api.ChatColor;

public class FriendController {

	public static List<FriendData> getFriends(LevelUp plugin) throws SQLException {
		Connection conn = plugin.mysql.getConnection();

		List<FriendData> friends = new ArrayList<FriendData>();

		String sql = "SELECT * FROM friend";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();

		while (rs.next()) {
			boolean are_friends = rs.getInt("are_friends") == 0 ? false : true;
			friends.add(new FriendData(UUID.fromString(rs.getString("from_player")),
					UUID.fromString(rs.getString("to_player")), are_friends));
		}

		rs.close();
		pstmt.close();

		plugin.getServer().getConsoleSender().sendMessage("[" + plugin.getName() + "] " + ChatColor.GREEN + "Loaded "
				+ ChatColor.YELLOW + friends.size() + ChatColor.GREEN + " Friend Data");

		return friends;
	}

	public static FriendData getFriendship(LevelUp plugin, UUID p1, UUID p2) {
		for (FriendData fd : plugin.friends) {
			if ((fd.getFromPlayer().equals(p1) && fd.getToPlayer().equals(p2))
					|| (fd.getFromPlayer().equals(p2) && fd.getToPlayer().equals(p1))) {
				return fd;
			}
		}

		return null;
	}

	public static void requestFriend(LevelUp plugin, UUID fromPlayer, UUID toPlayer) throws SQLException {
		Connection conn = plugin.mysql.getConnection();
		String sql = "INSERT INTO friend (from_player, to_player) VALUES (?, ?)";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, fromPlayer.toString());
		pstmt.setString(2, toPlayer.toString());
		pstmt.executeUpdate();
		pstmt.close();

		plugin.friends.add(new FriendData(fromPlayer, toPlayer, false));
	}

	public static void acceptFriend(LevelUp plugin, UUID fromPlayer, UUID toPlayer) throws SQLException {
		Connection conn = plugin.mysql.getConnection();

		String sql = "UPDATE friend SET are_friends = 1 WHERE from_player = ? AND to_player = ?";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, fromPlayer.toString());
		pstmt.setString(2, toPlayer.toString());
		pstmt.executeUpdate();
		pstmt.close();

		FriendData fd = getFriendship(plugin, fromPlayer, toPlayer);
		fd.setAreFriends(true);
	}

	public static void rejectFriend(LevelUp plugin, UUID fromPlayer, UUID toPlayer) throws SQLException {
		Connection conn = plugin.mysql.getConnection();
		String sql = "DELETE FROM friend WHERE from_player = ? AND to_player = ?";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, fromPlayer.toString());
		pstmt.setString(2, toPlayer.toString());
		pstmt.executeUpdate();
		pstmt.close();

		FriendData fd = getFriendship(plugin, fromPlayer, toPlayer);
		plugin.friends.remove(fd);
	}

	public static void deleteFriend(LevelUp plugin, UUID p1, UUID p2) throws SQLException {
		Connection conn = plugin.mysql.getConnection();
		String sql = "DELETE FROM friend WHERE (from_player = ? AND to_player = ?) "
				+ "OR (from_player = ? AND to_player = ?)";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, p1.toString());
		pstmt.setString(2, p2.toString());
		pstmt.setString(3, p2.toString());
		pstmt.setString(4, p1.toString());
		pstmt.executeUpdate();
		pstmt.close();

		FriendData fd = getFriendship(plugin, p1, p2);
		plugin.friends.remove(fd);
	}

	public static List<PlayerData> getRequests(LevelUp plugin, UUID toPlayer) {
		List<PlayerData> requests = new ArrayList<PlayerData>();

		for (FriendData fd : plugin.friends) {
			if (!fd.areFriends()) {
				if (fd.getToPlayer().equals(toPlayer)) {
					PlayerData fp = plugin.players.get(fd.getFromPlayer());
					requests.add(fp);
				}
			}
		}

		return requests;
	}

	public static List<PlayerData> getFriendList(LevelUp plugin, UUID uuid) {
		List<PlayerData> friends = new ArrayList<PlayerData>();

		for (FriendData fd : plugin.friends) {
			if (fd.areFriends()) {
				PlayerData fp = plugin.players.get(fd.getFromPlayer());
				PlayerData tp = plugin.players.get(fd.getToPlayer());

				if (fd.getFromPlayer().equals(uuid)) {
					friends.add(tp);

				} else if (fd.getToPlayer().equals(uuid)) {
					friends.add(fp);
				}
			}
		}

		return friends;
	}

	public static Map<UUID, List<UUID>> getUserBlocks(LevelUp plugin) throws SQLException {
		Connection conn = plugin.mysql.getConnection();

		Map<UUID, List<UUID>> userBlocks = new HashMap<UUID, List<UUID>>();

		String sql = "SELECT * FROM user_block";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();

		while (rs.next()) {
			UUID uuid = UUID.fromString(rs.getString("uuid"));
			UUID block = UUID.fromString(rs.getString("block"));
			
			if (!userBlocks.containsKey(uuid)) {
				userBlocks.put(uuid, new ArrayList<UUID>());
			}

			userBlocks.get(uuid).add(block);
		}

		rs.close();
		pstmt.close();

		plugin.getServer().getConsoleSender().sendMessage("[" + plugin.getName() + "] " + ChatColor.GREEN + "Loaded "
				+ ChatColor.YELLOW + userBlocks.size() + ChatColor.GREEN + " User Block Data");

		return userBlocks;
	}
	
	public static void blockUser(LevelUp plugin, Player player, UUID block) throws SQLException {
		Connection conn = plugin.mysql.getConnection();
		String sql = "INSERT INTO user_block (uuid, block) VALUE (?, ?)";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, player.getUniqueId().toString());
		pstmt.setString(2, block.toString());
		pstmt.executeUpdate();
		pstmt.close();
		
		if (!plugin.userBlocks.containsKey(player.getUniqueId())) {
			plugin.userBlocks.put(player.getUniqueId(), new ArrayList<UUID>());
		}
		plugin.userBlocks.get(player.getUniqueId()).add(block);
	}
	
	public static void unblockUser(LevelUp plugin, Player player, UUID block) throws SQLException {
		Connection conn = plugin.mysql.getConnection();
		String sql = "DELETE FROM user_block WHERE uuid = ? AND block = ?";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, player.getUniqueId().toString());
		pstmt.setString(2, block.toString());
		pstmt.executeUpdate();
		pstmt.close();
		
		plugin.userBlocks.get(player.getUniqueId()).remove(block);
	}

}
