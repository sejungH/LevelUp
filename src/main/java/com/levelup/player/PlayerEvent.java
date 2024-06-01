package com.levelup.player;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.levelup.LevelUp;
import com.levelup.scoreboard.ScoreboardController;
import com.levelup.tool.ToolController;
import com.levelup.tool.ToolData;

import dev.lone.itemsadder.api.CustomStack;

public class PlayerEvent implements Listener {

	private LevelUp plugin;
	private Connection conn;
	
	private Map<Player, List<ItemStack>> items;

	public PlayerEvent(LevelUp plugin) {
		this.plugin = plugin;
		this.conn = plugin.mysql.getConnection();
		this.items = new HashMap<Player, List<ItemStack>>();
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) throws SQLException {
		final Player player = event.getPlayer();
		PlayerData pd = plugin.players.get(player.getUniqueId());

		if (pd == null) {
			PlayerController.addPlayer(plugin, conn, player);

		} else {
			if (!player.getName().equalsIgnoreCase(pd.getUsername())) {
				PlayerController.updatePlayer(plugin, conn, player);
			}
			PlayerController.updateListOnline(plugin, conn, pd);
		}
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

			@Override
			public void run() {
				ScoreboardController.displayScoreboard(plugin, player);
			}

		}, 0, 20);

	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) throws SQLException {
		Player player = event.getPlayer();
		PlayerData pd = plugin.players.get(player.getUniqueId());
		PlayerController.updateListOnline(plugin, conn, pd);
	}

	@EventHandler
	public void onPlayerHat(InventoryClickEvent event) {
		if (event.getView().getTopInventory().getType().equals(InventoryType.CRAFTING)
				&& event.getSlotType().equals(SlotType.ARMOR) && event.getSlot() == 39) {

			if (event.getCursor() != null) {
				event.setCancelled(true);
				Player player = (Player) event.getWhoClicked();
				ItemStack current = event.getCurrentItem();

				player.getEquipment().setHelmet(event.getCursor());
				event.getWhoClicked().setItemOnCursor(current);
			}

		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		ToolData tool = plugin.tools.get(player.getUniqueId());
		List<ItemStack> list = new ArrayList<ItemStack>(event.getDrops());
		
		for (ItemStack item : list) {
			CustomStack custom = CustomStack.byItemStack(item);
			
			if (!items.containsKey(player))
				items.put(player, new ArrayList<ItemStack>());
			
			if (tool.getPickaxe().equals(plugin, item) || tool.getAxe().equals(plugin, item)
					|| tool.getSword().equals(plugin, item) || tool.getShovel().equals(plugin, item)) {
				items.get(player).add(item);
				event.getDrops().remove(item);
				
			} else if (custom != null) {
				if (custom.getNamespacedID().equals(ToolController.TOOLBOX_ID)) {
					event.getDrops().remove(item);
					
				} else if (custom.getNamespacedID().contains("_key")) {
					items.get(player).add(item);
					event.getDrops().remove(item);
					
				} else if (custom.getNamespacedID().contains("bag_")) {
					items.get(player).add(item);
					event.getDrops().remove(item);
				}
			}
		}
		
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		CustomStack toolbox = CustomStack.getInstance(ToolController.TOOLBOX_ID);
		player.getInventory().setItem(ToolController.TOOLBOX_SLOT, toolbox.getItemStack().clone());
		for (ItemStack item : items.get(player)) {
			player.getInventory().addItem(item);
		}
		items.get(player).clear();
	}

	@EventHandler
	public void onPlayerCraft(CraftItemEvent event) {
		List<Material> tool = Arrays.asList(Material.WOODEN_PICKAXE, Material.WOODEN_AXE, Material.WOODEN_SWORD,
				Material.WOODEN_SHOVEL, Material.STONE_PICKAXE, Material.STONE_AXE, Material.STONE_SWORD,
				Material.STONE_SHOVEL, Material.IRON_PICKAXE, Material.IRON_AXE, Material.IRON_SWORD,
				Material.IRON_SHOVEL, Material.DIAMOND_PICKAXE, Material.DIAMOND_AXE, Material.DIAMOND_SWORD,
				Material.DIAMOND_SHOVEL, Material.NETHERITE_PICKAXE, Material.NETHERITE_AXE, Material.NETHERITE_SWORD,
				Material.NETHERITE_SHOVEL);
		
		if (event.getRecipe().getResult().getType().equals(Material.FLINT_AND_STEEL)) {
			Inventory inv = event.getInventory();
			List<ItemStack> items = Arrays.asList(inv.getContents());
			
			for (ItemStack item : items) {
				CustomStack custom = CustomStack.byItemStack(item);
				
				if (custom != null) {
					event.setCancelled(true);
				}
			}
			
		} else if (tool.contains(event.getRecipe().getResult().getType())) {
			event.setCancelled(true);
			
		} else if (event.getRecipe().getResult().getType().equals(Material.ENDER_CHEST)) {
			event.setCancelled(true);
		}
	}

}
