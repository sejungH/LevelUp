package com.levelup.tool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.levelup.main.LevelUp;
import com.levelup.menu.MenuController;
import com.levelup.menu.MenuUnicode;

import dev.lone.itemsadder.api.CustomStack;

public class ToolEvent implements Listener {

	private LevelUp plugin;
	private Connection conn;

	public ToolEvent(LevelUp plugin) {
		this.plugin = plugin;
		this.conn = plugin.mysql.getConnection();
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) throws SQLException {
		Player player = event.getPlayer();
		CustomStack toolbox = CustomStack.getInstance(ToolController.TOOLBOX_ID);
		player.getInventory().setItem(ToolController.TOOLBOX_SLOT, toolbox.getItemStack().clone());
		
		if (!plugin.tools.containsKey(player.getUniqueId())) {
			ToolController.getNewTools(plugin, player);
		}
	}

	@EventHandler
	public void onPlayerOpenToolInv(PlayerInteractEvent event) {
		if (event.getHand() == EquipmentSlot.OFF_HAND)
			return;

		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player player = event.getPlayer();
			CustomStack toolbox = CustomStack.byItemStack(event.getItem());
			if (toolbox != null && toolbox.getNamespacedID().equals(ToolController.TOOLBOX_ID)) {
				event.setCancelled(true);
				Inventory toolInv = Bukkit.createInventory(player, 36,
						MenuController.getInventoryTitle(MenuUnicode.TOOLBOX.val()));
				ToolData tool = plugin.tools.get(player.getUniqueId());

				ItemStack pickaxe = tool.getPickaxe().getAsItemStack(plugin);
				ItemStack axe = tool.getAxe().getAsItemStack(plugin);
				ItemStack sword = tool.getSword().getAsItemStack(plugin);
				ItemStack shovel = tool.getShovel().getAsItemStack(plugin);

				if (!player.getInventory().contains(pickaxe))
					toolInv.setItem(1, pickaxe);

				if (!player.getInventory().contains(axe))
					toolInv.setItem(3, axe);

				if (!player.getInventory().contains(sword))
					toolInv.setItem(5, sword);

				if (!player.getInventory().contains(shovel))
					toolInv.setItem(7, shovel);

				player.openInventory(toolInv);
			}
		}
	}

	@EventHandler
	public void onPlayerClickToolInv(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		ItemStack item = event.getCurrentItem();
		ToolData tool = plugin.tools.get(player.getUniqueId());

		if (event.getClickedInventory() != null && event.getClickedInventory().getType().equals(InventoryType.PLAYER)
				&& event.getSlot() == ToolController.TOOLBOX_SLOT) {
			event.setCancelled(true);
			return;
		}

		if (event.getView().getTitle().equals(MenuController.getInventoryTitle(MenuUnicode.TOOLBOX.val()))) {
			event.setCancelled(true);
			Inventory toolInv = event.getView().getTopInventory();
			Inventory playerInv = player.getInventory();

			if (item != null) {

				if (event.getClickedInventory() != null && event.getClickedInventory().equals(playerInv)) {

					if (tool.getPickaxe().equals(plugin, item)) {
						toolInv.setItem(1, item);
						playerInv.remove(item);

					} else if (tool.getAxe().equals(plugin, item)) {
						toolInv.setItem(3, item);
						playerInv.remove(item);

					} else if (tool.getSword().equals(plugin, item)) {
						toolInv.setItem(5, item);
						playerInv.remove(item);

					} else if (tool.getShovel().equals(plugin, item)) {
						toolInv.setItem(7, item);
						playerInv.remove(item);
					}

				} else {
					Map<Integer, ItemStack> remain = playerInv.addItem(item);
					if (remain.isEmpty()) {
						toolInv.setItem(event.getSlot(), null);
					}
				}
			}

		} else {
			List<InventoryType> allowed = Arrays.asList(InventoryType.CRAFTING, InventoryType.CREATIVE);

			if (!allowed.contains(event.getView().getTopInventory().getType())) {

				if (tool.getPickaxe().equals(plugin, item) || tool.getAxe().equals(plugin, item)
						|| tool.getSword().equals(plugin, item) || tool.getShovel().equals(plugin, item)) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItemDrop().getItemStack();
		CustomStack customStack = CustomStack.byItemStack(item);
		ToolData tool = plugin.tools.get(player.getUniqueId());

		if (customStack != null && customStack.getNamespacedID().equals(ToolController.TOOLBOX_ID)) {
			event.setCancelled(true);

		} else if (tool.getPickaxe().equals(plugin, item) || tool.getAxe().equals(plugin, item)
				|| tool.getSword().equals(plugin, item) || tool.getShovel().equals(plugin, item)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerSawpHand(PlayerSwapHandItemsEvent event) {
		ItemStack item = event.getOffHandItem();
		CustomStack toolbox = CustomStack.byItemStack(item);
		if (toolbox != null && toolbox.getNamespacedID().equals(ToolController.TOOLBOX_ID)) {
			event.setCancelled(true);
		}
	}

}
