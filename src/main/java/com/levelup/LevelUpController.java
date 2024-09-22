package com.levelup;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;

import com.levelup.ride.RideController;

public class LevelUpController {

	public static void cleanUpScheduler(LevelUp plugin) {
		int tick = (int) plugin.getServer().getServerTickManager().getTickRate();
		
		final int task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			
			int counter = 0;

			@Override
			public void run() {
				if (counter == 0) {
					plugin.getServer().broadcastMessage(ChatColor.GOLD + "1분 뒤 땅에 떨어진 아이템을 청소합니다");
					
				} else if (counter == 30) {
					plugin.getServer().broadcastMessage(ChatColor.GOLD + "30초 뒤 땅에 떨어진 아이템을 청소합니다");
					
				} else if (counter == 50) {
					plugin.getServer().broadcastMessage(ChatColor.GOLD + "10 초 뒤 땅에 떨어진 아이템을 청소합니다");
					
				} else if (counter >= 55 && counter < 60) {
					plugin.getServer().broadcastMessage((60 - counter) + "초 뒤 땅에 떨어진 아이템을 청소합니다");
				}

				counter++;
			}

		}, 0, tick);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

			@Override
			public void run() {
				plugin.getServer().getScheduler().cancelTask(task);
				cleanUpItems(plugin);
				RideController.removeAllVehicles();
			}

		}, tick * 60);
		
	}

	public static void cleanUpItems(LevelUp plugin) {
		int count = 0;
		for (World world : plugin.getServer().getWorlds()) {
			for (Entity entity : world.getEntitiesByClass(Item.class)) {
				entity.remove();
				count++;
			}
		}
		plugin.getServer().broadcastMessage(ChatColor.GREEN + "" + count + " 개의 아이템을 청소했습니다");
	}

}
