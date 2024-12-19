package com.levelup.warp;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import net.md_5.bungee.api.ChatColor;

public class WarpEvent implements Listener {

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (WarpController.warpingPlayers.containsKey(player.getUniqueId())) {
			int task = WarpController.warpingPlayers.get(player.getUniqueId())[0];
			Bukkit.getScheduler().cancelTask(task);
			WarpController.warpingPlayers.remove(player.getUniqueId());
			player.sendMessage(ChatColor.RED + "이동이 취소되었습니다");
			player.playSound(player, Sound.BLOCK_GLASS_BREAK, 1.0F, task);
		}
	}
	
	@EventHandler
	public void onPlayerDamaged(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player player) {
			if (WarpController.warpingPlayers.containsKey(player.getUniqueId())) {
				int task = WarpController.warpingPlayers.get(player.getUniqueId())[0];
				Bukkit.getScheduler().cancelTask(task);
				WarpController.warpingPlayers.remove(player.getUniqueId());
				player.sendMessage(ChatColor.RED + "이동이 취소되었습니다");
				player.playSound(player, Sound.BLOCK_GLASS_BREAK, 1.0F, task);
			}
		}
	}
	
}
