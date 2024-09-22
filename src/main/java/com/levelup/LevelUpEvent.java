package com.levelup;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;

public class LevelUpEvent implements Listener {

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
				event.setBuild(false);
				event.setCancelled(true);
			}
		}
	}

}
