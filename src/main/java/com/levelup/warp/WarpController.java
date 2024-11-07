package com.levelup.warp;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.levelup.LevelUp;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class WarpController {

	public static Map<UUID, Integer[]> warpingPlayers = new HashMap<UUID, Integer[]>();

	public static void warp(LevelUp plugin, Player player, Location loc) {
		Integer[] task = new Integer[1];
		warpingPlayers.put(player.getUniqueId(), task);

		task[0] = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

			int counter = 3;

			@Override
			public void run() {
				if (counter > 0) {
					TextComponent enterMessage = new TextComponent(
							ChatColor.GOLD + Integer.toString(counter) + "초 뒤 이동합니다");
					player.spigot().sendMessage(ChatMessageType.ACTION_BAR, enterMessage);
					player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
					
				} else {
					player.teleport(loc);
					Bukkit.getScheduler().cancelTask(task[0]);
				}

				counter--;
			}

		}, 0, 20);
	}

}
