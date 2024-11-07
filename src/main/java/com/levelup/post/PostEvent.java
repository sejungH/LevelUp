package com.levelup.post;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import com.levelup.LevelUp;
import com.levelup.menu.MenuController;
import com.levelup.menu.MenuUnicode;

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
			PostController.openPostInventory(plugin, player, 0);
		}
	}

	@EventHandler
	public void onPostInvClick(InventoryClickEvent event) {
		if (event.getView().getTitle().equals(MenuController.getInventoryTitle(MenuUnicode.POST.val()))) {
			Player player = (Player) event.getWhoClicked();
			Inventory inv = event.getClickedInventory();
			Inventory topInv = event.getView().getTopInventory();
			Inventory playerInv = player.getInventory();

			if (inv != null) {
				if (inv.equals(topInv)) {
					if (event.getAction() == InventoryAction.PLACE_ALL || event.getAction() == InventoryAction.PLACE_ONE
							|| event.getAction() == InventoryAction.PLACE_SOME) {
						event.setCancelled(true);
					}

					ItemStack item = event.getCurrentItem();
					if (item != null) {
						if (item.getItemMeta().getDisplayName().equals("이전으로")
								|| item.getItemMeta().getDisplayName().equals("다음으로")) {
							int page = item.getItemMeta().getPersistentDataContainer()
									.get(new NamespacedKey(plugin, "page"), PersistentDataType.INTEGER);
							// save item;
							PostController.openPostInventory(plugin, player, page);
						}
					}
				} else if (inv.equals(playerInv)) {
					ItemStack item = event.getCurrentItem();
					if (item != null && event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void onPostInvClose(InventoryCloseEvent event) {
		if (event.getView().getTitle().equals(MenuController.getInventoryTitle(MenuUnicode.POST.val()))) {
			Player player = (Player) event.getPlayer();
			Inventory topInv = event.getInventory();
			for (int i = 0; i < 27; i++) {
				topInv.getItem(i);
			}
		}
	}
}
