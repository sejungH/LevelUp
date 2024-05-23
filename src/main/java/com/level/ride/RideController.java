package com.level.ride;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import com.levelup.main.LevelUp;
import com.levelup.player.PlayerData;

import dev.lone.itemsadder.api.CustomStack;
import net.md_5.bungee.api.ChatColor;

public class RideController {

	public static List<String> getRides() throws SQLException {
		List<String> rides = new ArrayList<String>();
		
		for (String id : CustomStack.getNamespacedIdsInRegistry()) {
//			CustomStack key = CustomStack.getInstance(id);
			if (id.contains("_key")) {
				rides.add(id.substring(id.indexOf(":") + 1, id.indexOf("_key")));
			}
		}
		
		return rides;
	}

	public static CustomStack getKey(LevelUp plugin, UUID uuid, String id) {
		PlayerData pd = plugin.players.get(uuid);
		
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.WHITE + "소유자: " + pd.getUsername());
		
		CustomStack key = CustomStack.getInstance("customitems:" + id.toLowerCase() + "_key");
		NamespacedKey namespacedKey = new NamespacedKey(plugin, "owner");
		ItemMeta meta = key.getItemStack().getItemMeta();
		meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, uuid.toString());
		meta.setLore(lore);
		
		key.getItemStack().setItemMeta(meta);
		
		return key;
	}

}
