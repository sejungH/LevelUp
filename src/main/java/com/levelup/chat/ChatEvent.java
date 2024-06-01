package com.levelup.chat;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.levelup.LevelUp;
import com.levelup.player.PlayerData;
import com.levelup.village.VillageData;

import net.md_5.bungee.api.ChatColor;

public class ChatEvent implements Listener {
	
	private LevelUp plugin;

	public ChatEvent(LevelUp plugin) {
		this.plugin = plugin;
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
