package com.levelup.chunk;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import com.levelup.LevelUp;
import com.levelup.player.PlayerData;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;

import net.md_5.bungee.api.ChatColor;

public class ChunkEvent implements Listener {

	private LevelUp plugin;

	public ChunkEvent(LevelUp plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();

		MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
		MVWorldManager worldManager = core.getMVWorldManager();
		MultiverseWorld world = worldManager.getMVWorld(player.getWorld());

		if (world.getAlias().equalsIgnoreCase("world")) {

			for (UUID uuid : plugin.playerChunks.keySet()) {
				List<Chunk> chunks = plugin.playerChunks.get(uuid);

				if (chunks.contains(block.getChunk()) && !uuid.equals(player.getUniqueId())) {
					if (block.getY() >= ChunkController.MIN_Y) {
						event.setCancelled(true);
						PlayerData pd = plugin.players.get(uuid);
						player.sendMessage(ChatColor.RED + pd.getUsername() + " 님이 소유 중인 청크입니다");
						break;
					}
				}
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();

		MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
		MVWorldManager worldManager = core.getMVWorldManager();
		MultiverseWorld world = worldManager.getMVWorld(player.getWorld());

		if (world.getAlias().equalsIgnoreCase("world")) {

			for (UUID uuid : plugin.playerChunks.keySet()) {
				List<Chunk> chunks = plugin.playerChunks.get(uuid);

				if (chunks.contains(block.getChunk()) && !uuid.equals(player.getUniqueId())) {
					if (block.getY() >= ChunkController.MIN_Y) {
						event.setCancelled(true);
						PlayerData pd = plugin.players.get(uuid);
						player.sendMessage(ChatColor.RED + pd.getUsername() + " 님이 소유 중인 청크입니다");
						break;
					}
				}
			}
		}
	}

	@EventHandler
	public void onInteractBlock(PlayerInteractEvent event) {
		if (event.getHand() == EquipmentSlot.OFF_HAND)
			return;

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player player = event.getPlayer();
			Block block = event.getClickedBlock();

			MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
			MVWorldManager worldManager = core.getMVWorldManager();
			MultiverseWorld world = worldManager.getMVWorld(player.getWorld());

			if (world.getAlias().equalsIgnoreCase("world")) {

				for (UUID uuid : plugin.playerChunks.keySet()) {
					List<Chunk> chunks = plugin.playerChunks.get(uuid);

					if (chunks.contains(block.getChunk()) && !uuid.equals(player.getUniqueId())) {
						if (block.getY() >= ChunkController.MIN_Y) {

							if (block.getType().toString().toUpperCase().contains("CHEST")
									|| block.getType().toString().toUpperCase().contains("BOX")
									|| block.getType().toString().toUpperCase().contains("FURNACE")
									|| block.getType().equals(Material.SMOKER)
									|| block.getType().toString().toUpperCase().contains("HOPPER")
									|| block.getType().equals(Material.BARREL)
									|| block.getType().toString().toUpperCase().contains("SIGN")
									|| block.getType().equals(Material.BREWING_STAND)
									|| block.getType().toString().toUpperCase().contains("ANVIL")) {

								event.setCancelled(true);
								PlayerData pd = plugin.players.get(uuid);
								player.sendMessage(ChatColor.RED + pd.getUsername() + " 님이 소유 중인 청크입니다");
								break;
							}

						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onInteractEntity(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();

		MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
		MVWorldManager worldManager = core.getMVWorldManager();
		MultiverseWorld world = worldManager.getMVWorld(player.getWorld());

		if (world.getAlias().equalsIgnoreCase("world")) {
			Entity entity = event.getRightClicked();

			if (entity.getType().equals(EntityType.ARMOR_STAND) || entity.getType().equals(EntityType.ITEM_FRAME)) {

				for (UUID uuid : plugin.playerChunks.keySet()) {
					List<Chunk> chunks = plugin.playerChunks.get(uuid);

					if (chunks.contains(entity.getLocation().getChunk()) && !uuid.equals(player.getUniqueId())) {

						if (entity.getLocation().getY() >= ChunkController.MIN_Y) {
							event.setCancelled(true);
							PlayerData pd = plugin.players.get(uuid);
							player.sendMessage(ChatColor.RED + pd.getUsername() + " 님이 소유 중인 청크입니다");
							break;
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onInteractEntity(PlayerArmorStandManipulateEvent event) {
		Player player = event.getPlayer();

		MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
		MVWorldManager worldManager = core.getMVWorldManager();
		MultiverseWorld world = worldManager.getMVWorld(player.getWorld());

		if (world.getAlias().equalsIgnoreCase("world")) {
			Entity entity = event.getRightClicked();

			for (UUID uuid : plugin.playerChunks.keySet()) {
				List<Chunk> chunks = plugin.playerChunks.get(uuid);

				if (chunks.contains(entity.getLocation().getChunk()) && !uuid.equals(player.getUniqueId())) {

					if (entity.getLocation().getY() >= ChunkController.MIN_Y) {
						event.setCancelled(true);
						PlayerData pd = plugin.players.get(uuid);
						player.sendMessage(ChatColor.RED + pd.getUsername() + " 님이 소유 중인 청크입니다");
						break;
					}
				}
			}
		}
	}

	@EventHandler
	public void onBreakHangingEntity(HangingBreakByEntityEvent event) {
		if (event.getRemover() instanceof Player) {
			Player player = (Player) event.getRemover();

			MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
			MVWorldManager worldManager = core.getMVWorldManager();
			MultiverseWorld world = worldManager.getMVWorld(player.getWorld());

			if (world.getAlias().equalsIgnoreCase("world")) {
				Entity entity = event.getEntity();

				for (UUID uuid : plugin.playerChunks.keySet()) {
					List<Chunk> chunks = plugin.playerChunks.get(uuid);

					if (chunks.contains(entity.getLocation().getChunk()) && !uuid.equals(player.getUniqueId())) {
						if (entity.getLocation().getY() >= ChunkController.MIN_Y) {
							event.setCancelled(true);
							PlayerData pd = plugin.players.get(uuid);
							player.sendMessage(ChatColor.RED + pd.getUsername() + " 님이 소유 중인 청크입니다");
							break;
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onBreakEntity(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();

			MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
			MVWorldManager worldManager = core.getMVWorldManager();
			MultiverseWorld world = worldManager.getMVWorld(player.getWorld());

			if (world.getAlias().equalsIgnoreCase("world")) {
				Entity entity = event.getEntity();

				if (entity.getType().equals(EntityType.ARMOR_STAND) || entity.getType().equals(EntityType.ITEM_FRAME)) {

					for (UUID uuid : plugin.playerChunks.keySet()) {
						List<Chunk> chunks = plugin.playerChunks.get(uuid);

						if (chunks.contains(entity.getLocation().getChunk()) && !uuid.equals(player.getUniqueId())) {

							if (entity.getLocation().getY() >= ChunkController.MIN_Y) {
								event.setCancelled(true);
								PlayerData pd = plugin.players.get(uuid);
								player.sendMessage(ChatColor.RED + pd.getUsername() + " 님이 소유 중인 청크입니다");
								break;
							}
						}
					}
				}
			}
		}
	}

}
