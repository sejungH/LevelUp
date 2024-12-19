package com.levelup;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.data.type.Chest;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.inventory.ItemStack;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;

import dev.lone.itemsadder.api.CustomFurniture;

public class LevelUpEvent implements Listener {

	@EventHandler
	public void onServerLoad(ServerLoadEvent event) {
		MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
		MVWorldManager worldManager = core.getMVWorldManager();
		List<MultiverseWorld> worlds = new ArrayList<MultiverseWorld>();
		worlds.add(worldManager.getMVWorld("world"));
		worlds.add(worldManager.getMVWorld("spawn"));
		worlds.add(worldManager.getMVWorld("world_nether"));
		worlds.add(worldManager.getMVWorld("wild"));
		worlds.add(worldManager.getMVWorld("tutorial"));

		long time = 0;
		for (MultiverseWorld world : worlds) {
			if (time == 0)
				time = world.getCBWorld().getTime();
			else
				world.getCBWorld().setTime(time);
		}
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

		if (!player.isOp()) {
			if (world.getAlias().equalsIgnoreCase("spawn") || world.getAlias().equalsIgnoreCase("tutorial")) {
				Block block = event.getBlock();
				if (block.getState() instanceof ShulkerBox) {
					Block below = block.getRelative(BlockFace.DOWN);
					if (below.getType() == LevelUpController.SHULKER_PLACEABLE) {
						return;
					}
				}
				event.setCancelled(true);

			} else {
				Chunk curr = event.getBlock().getChunk();
				Chunk spawn = world.getSpawnLocation().getChunk();

				int distX = Math.abs(curr.getX() - spawn.getX());
				int distZ = Math.abs(curr.getZ() - spawn.getZ());

				if (distX <= 3 && distZ <= 3) {
					event.setCancelled(true);
				}
			}
		}

	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();

		MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
		MVWorldManager worldManager = core.getMVWorldManager();
		MultiverseWorld world = worldManager.getMVWorld(player.getWorld());

		if (!player.isOp()) {
			if (world.getAlias().equalsIgnoreCase("spawn") || world.getAlias().equalsIgnoreCase("tutorial")) {
				Block placedOn = event.getBlockAgainst();
				if (!(event.getBlock().getState() instanceof ShulkerBox
						&& placedOn.getType() == LevelUpController.SHULKER_PLACEABLE)) {
					event.setBuild(false);
					event.setCancelled(true);
				}
			} else {
				Chunk curr = event.getBlock().getChunk();
				Chunk spawn = world.getSpawnLocation().getChunk();

				int distX = Math.abs(curr.getX() - spawn.getX());
				int distZ = Math.abs(curr.getZ() - spawn.getZ());

				if (distX <= 3 && distZ <= 3) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		if (block == null)
			return;
		ItemStack held = event.getItem();

		MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
		MVWorldManager worldManager = core.getMVWorldManager();
		MultiverseWorld world = worldManager.getMVWorld(player.getWorld());

		if (world.getAlias().equalsIgnoreCase("spawn") || world.getAlias().equalsIgnoreCase("tutorial")) {
			if (!player.isOp()) {
				if (block.getBlockData() instanceof Door || block.getBlockData() instanceof Chest
						|| block.getType().equals(Material.CRAFTING_TABLE)
						|| CustomFurniture.byAlreadySpawned(block) != null) {
					return;

				} else if (block.getState() instanceof ShulkerBox) {
					Block below = block.getRelative(BlockFace.DOWN);
					if (below.getType() == LevelUpController.SHULKER_PLACEABLE) {
						return;
					}
				} else if (held != null && held.getType().toString().endsWith("SHULKER_BOX")) {
					return;
				}

				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onInteractArmorStand(PlayerArmorStandManipulateEvent event) {
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
	public void onBreakHangingEntity(HangingBreakByEntityEvent event) {
		if (event.getRemover() instanceof Player player) {
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

	@EventHandler
	public void onBreakEntity(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player player) {
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

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (event.getEntityType() == EntityType.WANDERING_TRADER) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEnderOpen(InventoryOpenEvent event) {
		if (!event.getPlayer().isOp() && event.getInventory().getType().equals(InventoryType.ENDER_CHEST)) {
			event.setCancelled(true);
		}
	}

}
