package com.levelup.post;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import com.levelup.LevelUp;
import com.levelup.LevelUpIcon;
import com.levelup.player.PlayerController;
import com.levelup.player.PlayerData;

import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;

public class PostEvent implements Listener {

	private LevelUp plugin;

	public PostEvent(LevelUp plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPostBoxClick(FurnitureInteractEvent event) {
		Player player = event.getPlayer();
		CustomFurniture postbox = event.getFurniture();
		if (postbox.getNamespacedID().equals(PostController.POSTBOX)) {
			player.openInventory(PostController.getPostInventory(plugin, player.getUniqueId()));
		}
	}

	@EventHandler
	public void onPostInvClick(InventoryClickEvent event) {
		if (event.getView().getTitle().contains(Character.toString(LevelUpIcon.POST.val()))) {
			Player player = (Player) event.getWhoClicked();
			Inventory inv = event.getClickedInventory();
			Inventory topInv = event.getView().getTopInventory();
			Inventory playerInv = player.getInventory();

			if (!player.isOp() && inv != null) {
				if (inv.equals(topInv)) {
					if (event.getAction() != InventoryAction.PICKUP_ALL
							&& event.getAction() != InventoryAction.PICKUP_HALF
							&& event.getAction() != InventoryAction.PICKUP_ONE
							&& event.getAction() != InventoryAction.PICKUP_SOME
							&& event.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY) {
						event.setCancelled(true);
						return;
					}
				} else if (inv.equals(playerInv)) {
					ItemStack item = event.getCurrentItem();
					if (item != null && event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
						event.setCancelled(true);
						return;
					}
				}
			}
		}
	}

	@EventHandler
	public void onPostInvDrag(InventoryDragEvent event) {
		if (event.getView().getTitle().contains(Character.toString(LevelUpIcon.POST.val()))) {
			Player player = (Player) event.getWhoClicked();
			Inventory inv = event.getInventory();
			Inventory topInv = event.getView().getTopInventory();

			if (!player.isOp() && inv != null && inv.equals(topInv)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPostInvClose(InventoryCloseEvent event) throws SQLException {
		if (event.getView().getTitle().contains(Character.toString(LevelUpIcon.POST.val()))) {
			String regex = "(?<=\\s).+?(?= 님의 우편함)";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(event.getView().getTitle());

			if (matcher.find()) {
				Inventory inv = event.getInventory();
				PlayerData pd = PlayerController.getPlayerData(plugin, matcher.group());
				OfflinePlayer player = plugin.getServer().getOfflinePlayer(pd.getUuid());
				Inventory prev = PostController.getPostInventory(plugin, pd.getUuid());

				if (player.isOnline()) {
					Inventory ender = ((Player) player).getEnderChest();

					if (ender.getItem(2) == null) {
						ender.setItem(2, new ItemStack(Material.SHULKER_BOX));
						ender.setItem(3, new ItemStack(Material.SHULKER_BOX));
					}

					BlockStateMeta box1Meta = (BlockStateMeta) ender.getItem(2).getItemMeta();
					BlockStateMeta box2Meta = (BlockStateMeta) ender.getItem(3).getItemMeta();
					ShulkerBox box1 = (ShulkerBox) box1Meta.getBlockState();
					ShulkerBox box2 = (ShulkerBox) box2Meta.getBlockState();

					for (int i = 0; i < 27; i++) {
						box1.getInventory().setItem(i, inv.getItem(i));
						box2.getInventory().setItem(i, inv.getItem(i + 27));
					}

					box1Meta.setBlockState(box1);
					box2Meta.setBlockState(box2);

					ender.getItem(2).setItemMeta(box1Meta);
					ender.getItem(3).setItemMeta(box2Meta);

				} else {
					PostController.savePostInventory(pd.getUuid(), inv);
				}

				if (PostController.hasAdditionalItems(prev, inv)) {
					PostController.alertPlayer(plugin, pd.getUuid());
				}

			}
		}
	}
}
