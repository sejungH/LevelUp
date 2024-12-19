package com.levelup.bag;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import com.levelup.LevelUp;
import com.levelup.LevelUpIcon;
import com.levelup.player.PlayerData;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import dev.lone.itemsadder.api.CustomStack;
import net.md_5.bungee.api.ChatColor;

public class BagController {

	public static List<String> getBags() {
		List<String> bags = new ArrayList<String>();

		for (String id : CustomStack.getNamespacedIdsInRegistry()) {
			if (id.contains("_bag")) {
				bags.add(id.substring(id.indexOf(":") + 1));
			}
		}

		return bags;
	}

	public static CustomStack getBag(LevelUp plugin, UUID uuid, String id) {
		PlayerData pd = plugin.players.get(uuid);

		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GRAY + "소유자: " + pd.getUsername());

		CustomStack bag = CustomStack.getInstance("customitems:" + id);
		NamespacedKey namespacedKey = new NamespacedKey(plugin, "owner");
		ItemMeta meta = bag.getItemStack().getItemMeta();
		meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, uuid.toString());
		meta.setLore(lore);

		bag.getItemStack().setItemMeta(meta);

		return bag;
	}

	public static Inventory getBagInventory(LevelUp plugin, UUID uuid, String name, int rows) {
		PlayerData pd = plugin.players.get(uuid);

		if (name == null) {
			name = ChatColor.WHITE + Character.toString(LevelUpIcon.BAG.val()) + ChatColor.RESET + " " + pd.getName()
					+ " 님의 가방";
		}

		Inventory bagInv = Bukkit.createInventory(null, 9 * rows, name);
		OfflinePlayer player = plugin.getServer().getOfflinePlayer(uuid);

		if (player.isOnline()) {
			Inventory ender = ((Player) player).getEnderChest();

			if (ender.getItem(0) == null) {
				ender.setItem(0, new ItemStack(Material.SHULKER_BOX));
				ender.setItem(1, new ItemStack(Material.SHULKER_BOX));
			}

			BlockStateMeta box1Meta = (BlockStateMeta) ender.getItem(0).getItemMeta();
			BlockStateMeta box2Meta = (BlockStateMeta) ender.getItem(1).getItemMeta();
			ShulkerBox box1 = (ShulkerBox) box1Meta.getBlockState();
			ShulkerBox box2 = (ShulkerBox) box2Meta.getBlockState();

			for (int i = 0; i < 27; i++) {
				if (i < 9 * rows)
					bagInv.setItem(i, box1.getInventory().getItem(i));
				if (i + 27 < 9 * rows)
					bagInv.setItem(i + 27, box2.getInventory().getItem(i));
			}

		} else {
			try {
				File file = new File(LevelUp.CONFIG_PATH + "/bag/" + uuid.toString() + ".nbt");
				if (file.exists()) {
					ReadWriteNBT nbt;
					nbt = NBT.readFile(file);
					ItemStack[] items = NBT.itemStackArrayFromNBT(nbt);
					for (int i = 0; i < 9 * rows; i++) {
						bagInv.setItem(i, items[i]);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return bagInv;
	}

	public static void saveBagInventory(Player player) {
		try {
			File directory = new File(LevelUp.CONFIG_PATH + "/bag");
			if (!directory.exists())
				directory.mkdir();

			File file = new File(directory, player.getUniqueId().toString() + ".nbt");

			Inventory ender = player.getEnderChest();

			if (ender.getItem(0) != null) {
				BlockStateMeta box1Meta = (BlockStateMeta) ender.getItem(0).getItemMeta();
				BlockStateMeta box2Meta = (BlockStateMeta) ender.getItem(1).getItemMeta();
				ShulkerBox box1 = (ShulkerBox) box1Meta.getBlockState();
				ShulkerBox box2 = (ShulkerBox) box2Meta.getBlockState();

				ItemStack[] items = ArrayUtils.addAll(box1.getInventory().getContents(),
						box2.getInventory().getContents());
				ReadWriteNBT nbt = NBT.itemStackArrayToNBT(items);
				NBT.writeFile(file, nbt);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void saveBagInventory(UUID uuid, Inventory inv) {
		try {
			File directory = new File(LevelUp.CONFIG_PATH + "/bag");
			if (!directory.exists())
				directory.mkdir();

			File file = new File(directory, uuid.toString() + ".nbt");
			ReadWriteNBT nbt = NBT.itemStackArrayToNBT(inv.getContents());
			NBT.writeFile(file, nbt);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void restoreBagInventory(Player player) {
		try {
			File file = new File(LevelUp.CONFIG_PATH + "/bag/" + player.getUniqueId().toString() + ".nbt");
			if (file.exists()) {
				Inventory ender = player.getEnderChest();
	
				if (ender.getItem(0) == null) {
					ender.setItem(0, new ItemStack(Material.SHULKER_BOX));
					ender.setItem(1, new ItemStack(Material.SHULKER_BOX));
				}
	
				BlockStateMeta box1Meta = (BlockStateMeta) ender.getItem(0).getItemMeta();
				BlockStateMeta box2Meta = (BlockStateMeta) ender.getItem(1).getItemMeta();
				ShulkerBox box1 = (ShulkerBox) box1Meta.getBlockState();
				ShulkerBox box2 = (ShulkerBox) box2Meta.getBlockState();
				box1.getInventory().clear();
				box2.getInventory().clear();
				
				ReadWriteNBT nbt = NBT.readFile(file);
				ItemStack[] items = NBT.itemStackArrayFromNBT(nbt);
				
				for (int i = 0; i < items.length; i++) {
					if (i < 27) {
						box1.getInventory().setItem(i, items[i]);
					} else {
						box2.getInventory().setItem(i - 27, items[i]);
					}
				}
				
				box1Meta.setBlockState(box1);
				box2Meta.setBlockState(box2);
				
				ender.getItem(0).setItemMeta(box1Meta);
				ender.getItem(1).setItemMeta(box2Meta);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
