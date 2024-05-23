package com.levelup.player;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import com.levelup.main.LevelUp;
import com.levelup.scoreboard.ScoreboardController;
import com.levelup.tool.ToolController;
import com.levelup.tool.ToolData;

import dev.lone.itemsadder.api.CustomStack;

public class PlayerEvent implements Listener {

	private LevelUp plugin;
	private Connection conn;

	public PlayerEvent(LevelUp plugin) {
		this.plugin = plugin;
		this.conn = plugin.mysql.getConnection();
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) throws SQLException {
		Player player = event.getPlayer();
		PlayerData pd = plugin.players.get(player.getUniqueId());

		if (pd == null) {
			PlayerController.addPlayer(plugin, conn, player);

		} else {
			if (!player.getName().equalsIgnoreCase(pd.getUsername())) {
				PlayerController.updatePlayer(plugin, conn, player);
			}
			PlayerController.updateListOnline(plugin, conn, pd);
		}

		if (player.getScoreboard().getObjective(player.getName()) == null) {
			ScoreboardController.newScoreboard(plugin, player);

		} else {
			ScoreboardController.updateScoreboard(plugin, player);
		}

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
		List<ItemStack> list = event.getDrops();
		for (ItemStack item : list) {
			CustomStack custom = CustomStack.byItemStack(item);
			if (tool.getPickaxe().equals(plugin, item) || tool.getAxe().equals(plugin, item)
					|| tool.getSword().equals(plugin, item) || tool.getShovel().equals(plugin, item)) {
				list.remove(item);
			} else if (custom != null) {
				if (custom.getNamespacedID().equals(ToolController.TOOLBOX_ID)) {
					list.remove(item);
				}
			}
		}
	}

}
