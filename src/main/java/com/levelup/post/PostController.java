package com.levelup.post;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import com.levelup.LevelUp;
import com.levelup.LevelUpIcon;
import com.levelup.message.MessageController;
import com.levelup.player.PlayerData;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import net.md_5.bungee.api.ChatColor;

public class PostController {

	public static final String POSTBOX = "customitems:postbox";

	public static Inventory getPostInventory(LevelUp plugin, UUID uuid) {
		PlayerData pd = plugin.players.get(uuid);
		Inventory postInv = Bukkit.createInventory(null, 54, ChatColor.WHITE
				+ Character.toString(LevelUpIcon.POST.val()) + ChatColor.RESET + " " + pd.getName() + " 님의 우편함");

		OfflinePlayer user = plugin.getServer().getOfflinePlayer(uuid);

		if (user.isOnline()) {
			Inventory ender = ((Player) user).getEnderChest();
			if (ender.getItem(2) == null) {
				ender.setItem(2, new ItemStack(Material.SHULKER_BOX));
				ender.setItem(3, new ItemStack(Material.SHULKER_BOX));
			}

			BlockStateMeta box1Meta = (BlockStateMeta) ender.getItem(2).getItemMeta();
			BlockStateMeta box2Meta = (BlockStateMeta) ender.getItem(3).getItemMeta();
			ShulkerBox box1 = (ShulkerBox) box1Meta.getBlockState();
			ShulkerBox box2 = (ShulkerBox) box2Meta.getBlockState();
			
			for (int i = 0; i < 27; i++) {
				postInv.setItem(i, box1.getInventory().getItem(i));
				postInv.setItem(i + 27, box2.getInventory().getItem(i));
			}

		} else {
			try {
				File file = new File(LevelUp.CONFIG_PATH + "/post/" + uuid.toString() + ".nbt");
				if (file.exists()) {
					ReadWriteNBT nbt = NBT.readFile(file);
					ItemStack[] items = NBT.itemStackArrayFromNBT(nbt);
					for (int i = 0; i < 54; i++) {
						postInv.setItem(i, items[i]);
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return postInv;
	}

	public static void savePostInventory(Player player) {
		try {
			File directory = new File(LevelUp.CONFIG_PATH + "/post");
			if (!directory.exists())
				directory.mkdir();

			File file = new File(directory, player.getUniqueId().toString() + ".nbt");

			Inventory ender = player.getEnderChest();
			if (ender.getItem(2) != null) {
				BlockStateMeta box1Meta = (BlockStateMeta) ender.getItem(2).getItemMeta();
				BlockStateMeta box2Meta = (BlockStateMeta) ender.getItem(3).getItemMeta();
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

	public static void savePostInventory(UUID uuid, Inventory inv) {
		try {
			File directory = new File(LevelUp.CONFIG_PATH + "/post");
			if (!directory.exists())
				directory.mkdir();

			File file = new File(directory, uuid.toString() + ".nbt");
			ReadWriteNBT nbt = NBT.itemStackArrayToNBT(inv.getContents());
			NBT.writeFile(file, nbt);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void restorePostInventory(Player player) {
		try {
			File file = new File(LevelUp.CONFIG_PATH + "/post/" + player.getUniqueId().toString() + ".nbt");
			if (file.exists()) {
				Inventory ender = player.getEnderChest();

				if (ender.getItem(2) == null) {
					ender.setItem(2, new ItemStack(Material.SHULKER_BOX));
					ender.setItem(3, new ItemStack(Material.SHULKER_BOX));
				}

				BlockStateMeta box1Meta = (BlockStateMeta) ender.getItem(2).getItemMeta();
				BlockStateMeta box2Meta = (BlockStateMeta) ender.getItem(3).getItemMeta();
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
				
				ender.getItem(2).setItemMeta(box1Meta);
				ender.getItem(3).setItemMeta(box2Meta);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void alertPlayer(LevelUp plugin, UUID uuid) {
		OfflinePlayer player = plugin.getServer().getOfflinePlayer(uuid);
		if (player.isOnline()) {
			((Player) player).sendMessage(LevelUpIcon.POST.val() + " " + ChatColor.GOLD + "우편함에 아이템이 도착했습니다!");
			((Player) player).playSound((Player) player, Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
		} else {
			try {
				MessageController.addPendingMessage(plugin, uuid,
						LevelUpIcon.POST.val() + " " + ChatColor.GOLD + "우편함에 아이템이 도착했습니다!");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static boolean hasAdditionalItems(Inventory first, Inventory second) {
		for (ItemStack item : second.getContents()) {
			if (item != null) {
				if (!first.containsAtLeast(item, item.getAmount())) {
					return true;
				}
			}
		}
		
		return false;
	}

}
