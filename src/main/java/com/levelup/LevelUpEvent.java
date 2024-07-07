package com.levelup;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.server.PluginEnableEvent;

import com.levelup.player.PlayerController;
import com.levelup.village.VillageController;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;

public class LevelUpEvent implements Listener {

	private LevelUp plugin;

	public LevelUpEvent(LevelUp plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onServerLoaded(PluginEnableEvent event) {
		// Run every 10 mins
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

			@Override
			public void run() {
				try {
					VillageController.checkDeletionPeriod(plugin);
					VillageController.checkTaxOverdue(plugin);
					PlayerController.checkRestUser(plugin);
					VillageController.updateTax(plugin);

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}, 0, 20 * 60 * 1);
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockExplode(BlockExplodeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();

		MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
		MVWorldManager worldManager = core.getMVWorldManager();
		MultiverseWorld world = worldManager.getMVWorld(player.getWorld());

		if (world.getAlias().equalsIgnoreCase("spawn") || world.getAlias().equalsIgnoreCase("tutorial")) {
			if (!player.isOp()) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();

		MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
		MVWorldManager worldManager = core.getMVWorldManager();
		MultiverseWorld world = worldManager.getMVWorld(player.getWorld());
		
		if (world.getAlias().equalsIgnoreCase("spawn") || world.getAlias().equalsIgnoreCase("tutorial")) {
			if (!player.isOp()) {
				event.setCancelled(true);
			}
		}
	}

}
