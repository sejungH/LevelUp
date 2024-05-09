package com.levelup.player;

import java.sql.Connection;
import java.sql.SQLException;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.levelup.chat.ChatType;
import com.levelup.main.LevelUp;
import com.levelup.scoreboard.ScoreboardController;
import com.levelup.village.VillageData;

import net.md_5.bungee.api.ChatColor;

public class PlayerEvent implements Listener {

	private LevelUp plugin;
	private Connection conn;

	public PlayerEvent(LevelUp plugin) {
		this.plugin = plugin;
		this.conn = plugin.mysql.getConnection();
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) throws SQLException {
		Player player = event.getPlayer();
		PlayerData pd = plugin.players.get(player.getUniqueId());
		
		if (pd == null) {
			PlayerController.addPlayer(plugin, conn, player);
			
		} else {
			if (!player.getName().equalsIgnoreCase(pd.getUsername())) {
				PlayerController.updatePlayer(plugin, conn, player);
			}
			PlayerController.updateListOnline(plugin, conn, pd);
		}
		
		if (player.getScoreboard().getObjective(player.getName()) == null) {
			ScoreboardController.newScoreboard(plugin, player);
			
		} else {
			ScoreboardController.updateScoreboard(plugin, player);
		}

	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) throws SQLException {
		Player player = event.getPlayer();
		PlayerData pd = plugin.players.get(player.getUniqueId());
		PlayerController.updateListOnline(plugin, conn, pd);
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		PlayerData pd = plugin.players.get(player.getUniqueId());
		VillageData vd = plugin.villages.get(pd.getVillage());
		
		String displayName;
		if (vd == null) {
			displayName = pd.getUsername();
			
		} else {
			displayName = "[" + vd.getName() +  "] " + pd.getUsername();
		}
		
		if (pd.getChatType() == ChatType.DEFAULT) {
			event.setFormat(displayName + ": " + event.getMessage());
			
		} else if (pd.getChatType() == ChatType.VILLAGE) {
			event.setCancelled(true);
			for (Player op : plugin.getServer().getOnlinePlayers()) {
				PlayerData opd = plugin.players.get(op.getUniqueId());
				
				if (pd.getVillage() == opd.getVillage()) {
					op.sendMessage(ChatColor.of("#FFBCE1") + displayName + ": " + event.getMessage());
				}
			}
		}
	}
}
