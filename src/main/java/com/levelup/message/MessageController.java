package com.levelup.message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			LocalDateTime datetime = LocalDateTime.parse(rs.getString("datetime"), formatter);
			boolean isRead = rs.getInt("is_read") == 0 ? false : true;

			pendingMessages.add(new Message(id, uuid, message, datetime, isRead));
		}

		rs.close();
		pstmt.close();

		MessageController.pendingMessages = pendingMessages;
	}

	public static void addPendingMessage(LevelUp plugin, UUID uuid, String message) throws SQLException {
		Connection conn = plugin.mysql.getConnection();
		String sql = "INSERT INTO message (uuid, message) VALUES (?, ?)";
		PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

		pstmt.setString(1, uuid.toString());
		pstmt.setString(2, message);
		pstmt.executeUpdate();
		
		ResultSet rs = pstmt.getGeneratedKeys();
		
		if (rs.next()) {
			int id = rs.getInt(1);
			pendingMessages.add(new Message(id, uuid, message, LocalDateTime.now(), false));
		}
		rs.close();
		pstmt.close();
		
		
	}

	public static void readMessage(LevelUp plugin, int id) throws SQLException {
		Connection conn = plugin.mysql.getConnection();
		String sql = "UPDATE message SET is_read = 1 WHERE id = ?";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, id);
		pstmt.executeUpdate();
		pstmt.close();
		
		for (Message msg : pendingMessages) {
			if (msg.getId() == id) {
				msg.setRead(true);
				break;
			}
		}
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
				addPendingMessage(plugin, uuid, LevelUpIcon.MAIL + " " + ChatColor.GOLD + message);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void sendPendingMessages(LevelUp plugin, Player player) throws SQLException {
		List<Message> messages = MessageController.getPlayerMessages(player.getUniqueId());
		for (Message msg : messages) {
			player.sendMessage(msg.getMessage());
			readMessage(plugin, msg.getId());
		}
	}
}
