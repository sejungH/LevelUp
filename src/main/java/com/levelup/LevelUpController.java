package com.levelup;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import com.levelup.ride.RideController;

import dev.lone.itemsadder.api.CustomStack;

public class LevelUpController {

	public static final Material SHULKER_PLACEABLE = Material.CHISELED_QUARTZ_BLOCK;

	public static List<String> parseCashItems(List<Object> yaml) {
		List<String> cashItems = new ArrayList<String>();
		for (Object obj : yaml) {
			cashItems.add(obj.toString());
		}

		return cashItems;
	}

	public static void cleanUpScheduler(LevelUp plugin) {
		int tick = (int) plugin.getServer().getServerTickManager().getTickRate();

		final int task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

			int counter = 0;

			@Override
			public void run() {
				if (counter == 0) {
					plugin.getServer().broadcastMessage(ChatColor.GOLD + "1분 뒤 땅에 떨어진 아이템을 청소합니다");
					broadcastSound(plugin, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);

				} else if (counter == 30) {
					plugin.getServer().broadcastMessage(ChatColor.GOLD + "30초 뒤 땅에 떨어진 아이템을 청소합니다");

				} else if (counter == 50) {
					plugin.getServer().broadcastMessage(ChatColor.GOLD + "10 초 뒤 땅에 떨어진 아이템을 청소합니다");

				} else if (counter >= 55 && counter < 60) {
					plugin.getServer().broadcastMessage((60 - counter) + "초 뒤 땅에 떨어진 아이템을 청소합니다");
					if (counter >= 57) {
						broadcastSound(plugin, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
					}
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
			for (Item item : world.getEntitiesByClass(Item.class)) {
				ItemStack itemStack = item.getItemStack();
				CustomStack custom = CustomStack.byItemStack(itemStack);

				if (custom == null || !plugin.cashItems.contains(custom.getNamespacedID())) {
					ItemMeta itemMeta = itemStack.getItemMeta();
					NamespacedKey key = new NamespacedKey(plugin, "last_drop");
					
					if (itemMeta.getPersistentDataContainer().has(key)) {
						LocalDateTime lastDrop = LocalDateTime.parse(itemMeta.getPersistentDataContainer()
								.get(key, PersistentDataType.STRING));
						Duration duration = Duration.between(lastDrop, LocalDateTime.now());
						if (duration.toMinutes() > 10) {
							item.remove();
							count++;
						}
						
					} else {
						item.remove();
						count++;
					}
				}
			}
		}
		plugin.getServer().broadcastMessage(ChatColor.GREEN + Integer.toString(count) + " 개의 아이템을 청소했습니다");
	}

	public static String sanitizeString(String str) {
		if (str.contains("§")) {
			int index = str.indexOf("§");
			return sanitizeString(str.substring(0, index) + str.substring(index, index + 2));

		} else {
			return str.trim();
		}
	}

	public static void broadcastSound(LevelUp plugin, Sound sound, float volume, float pitch) {
		for (Player player : plugin.getServer().getOnlinePlayers()) {
			player.playSound(player, sound, volume, pitch);
		}
	}

}
