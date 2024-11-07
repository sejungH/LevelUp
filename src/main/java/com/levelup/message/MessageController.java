package com.levelup.message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.levelup.LevelUp;
import com.levelup.LevelUpIcon;

import net.md_5.bungee.api.ChatColor;

public class MessageController {
	public static List<Message> pendingMessages;

	public static void getPendingMessages(LevelUp plugin) throws SQLException {
		Connection conn = plugin.mysql.getConnection();
		List<Message> pendingMessages = new ArrayList<Message>();
		String sql = "SELECT * FROM message";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();

		while (rs.next()) {
			int id = rs.getInt("id");
			UUID uuid = UUID.fromString(rs.getString("uuid"));
			String message = rs.getString("message");
			LocalDateTime datetime = LocalDateTime.parse(rs.getString("datetime"));
			boolean isRead = rs.getInt("id_read") == 0 ? false : true;

			pendingMessages.add(new Message(id, uuid, message, datetime, isRead));
		}

		rs.close();
		pstmt.close();

		MessageController.pendingMessages = pendingMessages;
	}

	public static void addPendingMessage(LevelUp plugin, UUID uuid, String message) throws SQLException {
		Connection conn = plugin.mysql.getConnection();
		String sql = "INSERT INTO message (uuid, message) VALUES (?, ?)";
		PreparedStatement pstmt = conn.prepareStatement(sql);

		pstmt.setString(1, uuid.toString());
		pstmt.setString(2, message);

		pstmt.executeUpdate();
		pstmt.close();
	}

	public static void readMessage(LevelUp plugin, int id) throws SQLException {
		Connection conn = plugin.mysql.getConnection();
		String sql = "UPDATE message SET is_read = 1 WHERE id = ?";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, id);
		pstmt.executeUpdate();
		pstmt.close();
	}

	public static List<Message> getPlayerMessages(UUID uuid) {
		List<Message> messages = new ArrayList<Message>();
		for (Message msg : pendingMessages) {
			if (msg.getUuid().equals(uuid) && !msg.isRead()) {
				messages.add(msg);
			}
		}

		return messages;
	}

	public static void sendMessage(LevelUp plugin, UUID uuid, String message) {
		OfflinePlayer player = plugin.getServer().getOfflinePlayer(uuid);
		if (player.isOnline()) {
			((Player) player).sendMessage(LevelUpIcon.MAIL + " " + ChatColor.GOLD + message);
			((Player) player).playSound((Player) player, Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
		} else {
			try {
				addPendingMessage(plugin, uuid, message);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void sendPendingMessages(LevelUp plugin, Player player) throws SQLException {
		List<Message> messages = MessageController.getPlayerMessages(player.getUniqueId());
		for (Message msg : messages) {
			player.sendMessage(LevelUpIcon.MAIL + " " + ChatColor.GOLD + msg.getMessage());
			readMessage(plugin, msg.getId());
		}
	}
}
