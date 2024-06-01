package com.levelup.bag;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import com.levelup.LevelUp;
import com.levelup.player.PlayerData;

import dev.lone.itemsadder.api.CustomStack;
import net.md_5.bungee.api.ChatColor;

public class BagController {

	public static List<String> getBags() {
		List<String> bags = new ArrayList<String>();

		for (String id : CustomStack.getNamespacedIdsInRegistry()) {
			if (id.contains("bag_")) {
				bags.add(id.substring(id.indexOf(":") + 1));
			}
		}

		return bags;
	}
	
	public static CustomStack getBag(LevelUp plugin, UUID uuid, String id) {
		PlayerData pd = plugin.players.get(uuid);
		
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.WHITE + "소유자: " + pd.getUsername());
		
		CustomStack bag = CustomStack.getInstance("customitems:" + id);
		NamespacedKey namespacedKey = new NamespacedKey(plugin, "owner");
		ItemMeta meta = bag.getItemStack().getItemMeta();
		meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, uuid.toString());
		meta.setLore(lore);
		
		bag.getItemStack().setItemMeta(meta);
		
		return bag;
	}

}
