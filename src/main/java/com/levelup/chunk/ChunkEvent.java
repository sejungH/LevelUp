package com.levelup.chunk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.metadata.FixedMetadataValue;

import com.levelup.LevelUp;
import com.levelup.player.PlayerData;
import com.levelup.village.VillageData;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class ChunkEvent implements Listener {

	private final List<EntityType> bannedEntities = new ArrayList<EntityType>(Arrays.asList(EntityType.ARMOR_STAND,
			EntityType.ITEM_FRAME, EntityType.GLOW_ITEM_FRAME, EntityType.PAINTING));

	private LevelUp plugin;

	public ChunkEvent(LevelUp plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();

		MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
		MVWorldManager worldManager = core.getMVWorldManager();
		MultiverseWorld world = worldManager.getMVWorld(player.getWorld());

		if (world.getAlias().equalsIgnoreCase("world")) {
			Block block = event.getBlock();

			UUID owner = ChunkController.getChunkOwnerPlayer(plugin, block.getChunk());
			if (owner != null && !owner.equals(player.getUniqueId())) {
				event.setCancelled(true);
				PlayerData pd = plugin.players.get(owner);
				player.sendMessage(ChatColor.RED + pd.getUsername() + " 님이 소유 중인 청크입니다");
				return;
			}

			PlayerData pd = plugin.players.get(player.getUniqueId());
			int villageOwner = ChunkController.getChunkOwnerVillage(plugin, block.getChunk());
			if (villageOwner > 0 && villageOwner != pd.getVillage()) {
				event.setCancelled(true);
				VillageData vd = plugin.villages.get(villageOwner);
				player.sendMessage(ChatColor.RED + "마을 [" + vd.getName() + "] 에서 소유 중인 청크입니다");
				return;
			}
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();

		MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
		MVWorldManager worldManager = core.getMVWorldManager();
		MultiverseWorld world = worldManager.getMVWorld(player.getWorld());

		if (world.getAlias().equalsIgnoreCase("world")) {
			Block block = event.getBlock();

			UUID owner = ChunkController.getChunkOwnerPlayer(plugin, block.getChunk());
			if (owner != null) {
				if (owner.equals(player.getUniqueId())) {
					if (block.getType().equals(Material.CHEST)) {
						block.setMetadata("ownerPlayer", new FixedMetadataValue(plugin, owner.toString()));
					}

				} else {
					event.setCancelled(true);
					PlayerData pd = plugin.players.get(owner);
					player.sendMessage(ChatColor.RED + pd.getUsername() + " 님이 소유 중인 청크입니다");
					return;
				}
			}

			PlayerData pd = plugin.players.get(player.getUniqueId());
			int villageOwner = ChunkController.getChunkOwnerVillage(plugin, block.getChunk());
			if (villageOwner > 0) {
				if (villageOwner == pd.getVillage()) {
					if (block.getType().equals(Material.CHEST)) {
						block.setMetadata("ownerVillage", new FixedMetadataValue(plugin, villageOwner));
					}

				} else {
					event.setCancelled(true);
					VillageData vd = plugin.villages.get(villageOwner);
					player.sendMessage(ChatColor.RED + "마을 [" + vd.getName() + "] 에서 소유 중인 청크입니다");
					return;
				}
			}

			if (owner == null && villageOwner < 0) {
				if (block.getType().equals(Material.CHEST)) {
					event.setCancelled(true);
					player.sendMessage(ChatColor.RED + "본인이 소유중인 청크에서만 상자를 설치할 수 있습니다");
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

			MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
			MVWorldManager worldManager = core.getMVWorldManager();
			MultiverseWorld world = worldManager.getMVWorld(player.getWorld());

			if (world.getAlias().equalsIgnoreCase("world")) {
				Block block = event.getClickedBlock();

				for (String material : ChunkController.BANNED_BLOCKS) {
					if (block.getType().toString().toUpperCase().contains(material)) {
						PlayerData pd = plugin.players.get(player.getUniqueId());
						
						UUID chunkOwnerPlayer = ChunkController.getChunkOwnerPlayer(plugin, block.getChunk());						
						if (chunkOwnerPlayer != null && chunkOwnerPlayer.equals(player.getUniqueId())) {
							if (block.getType().equals(Material.CHEST)) {
								if (block.hasMetadata("ownerPlayer")) {
									String ownerPlayer = block.getMetadata("ownerPlayer").get(0).asString();
									System.out.println(block.getMetadata("ownerPlayer"));
									if (!ownerPlayer.equals(player.getUniqueId().toString())) {
										Chest chest = (Chest) block.getState();
										chest.getInventory().clear();
										block.setMetadata("ownerPlayer",
												new FixedMetadataValue(plugin, player.getUniqueId().toString()));
										return;
									}

								} else if (block.hasMetadata("ownerVillage")) {
									System.out.println(block.getMetadata("ownerVillage"));
									Chest chest = (Chest) block.getState();
									chest.getInventory().clear();
									block.removeMetadata("ownerVillage", plugin);
									block.setMetadata("ownerPlayer",
											new FixedMetadataValue(plugin, player.getUniqueId().toString()));
									return;
								}
							}
							
						} else if (chunkOwnerPlayer != null && !chunkOwnerPlayer.equals(player.getUniqueId())) {
							event.setCancelled(true);
							PlayerData ownerPlayerData = plugin.players.get(chunkOwnerPlayer);
							player.sendMessage(ChatColor.RED + ownerPlayerData.getUsername() + " 님이 소유 중인 청크입니다");
							return;
						}
						
						int chunkOwnerVillage = ChunkController.getChunkOwnerVillage(plugin, block.getChunk());
						if (chunkOwnerVillage > 0 && chunkOwnerVillage == pd.getVillage()) {
							if (block.getType().equals(Material.CHEST)) {
								if (block.hasMetadata("ownerPlayer")) {
									System.out.println(block.getMetadata("ownerPlayer"));
									Chest chest = (Chest) block.getState();
									chest.getInventory().clear();
									block.removeMetadata("ownerPlayer", plugin);
									block.setMetadata("ownerVillage", new FixedMetadataValue(plugin, pd.getVillage()));
									return;
									
								} else if (block.hasMetadata("ownerVillage")) {
									System.out.println(block.getMetadata("ownerVillage"));
									int ownerVillage = block.getMetadata("ownerVillage").get(0).asInt();
									if (ownerVillage != pd.getVillage()) {
										Chest chest = (Chest) block.getState();
										chest.getInventory().clear();
										block.setMetadata("ownerVillage", new FixedMetadataValue(plugin, pd.getVillage()));
									}
								}
							}
							
							
						} else if (chunkOwnerVillage > 0 && chunkOwnerVillage != pd.getVillage()) {
							event.setCancelled(true);
							VillageData vd = plugin.villages.get(chunkOwnerVillage);
							player.sendMessage(ChatColor.RED + "마을 [" + vd.getName() + "] 에서 소유 중인 청크입니다");
							return;
						}
						
						if (chunkOwnerPlayer == null && chunkOwnerVillage <= 0) {
							if (block.getType().equals(Material.CHEST)) {
								if (block.hasMetadata("ownerPlayer")) {
									System.out.println(block.getMetadata("ownerPlayer"));
									String ownerPlayer = block.getMetadata("ownerPlayer").get(0).asString();
									if (!ownerPlayer.equals(player.getUniqueId().toString())) {
										event.setCancelled(true);
										PlayerData ownerPlayerData = plugin.players.get(UUID.fromString(ownerPlayer));
										player.sendMessage(ChatColor.RED + ownerPlayerData.getUsername() + " 님이 소유 중인 상자입니다");
										return;
									}

								} else if (block.hasMetadata("ownerVillage")) {
									System.out.println(block.getMetadata("ownerVillage"));
									int ownerVillage = block.getMetadata("ownerVillage").get(0).asInt();
									if (pd.getVillage() != ownerVillage) {
										event.setCancelled(true);
										VillageData vd = plugin.villages.get(ownerVillage);
										if (vd == null) {
											player.sendMessage(ChatColor.RED + "삭제된 마을에서 소유했던 상자입니다");

										} else {
											player.sendMessage(ChatColor.RED + "마을 [" + vd.getName() + "] 에서 소유 중인 상자입니다");
										}
										return;
									}
								}
							}
						}
						
						break;
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInteractEntity(PlayerInteractEntityEvent event) {
		if (event.isCancelled())
			return;
		
		Player player = event.getPlayer();

		MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
		MVWorldManager worldManager = core.getMVWorldManager();
		MultiverseWorld world = worldManager.getMVWorld(player.getWorld());

		if (world.getAlias().equalsIgnoreCase("world")) {
			Entity entity = event.getRightClicked();

			if (bannedEntities.contains(entity.getType())) {

				UUID owner = ChunkController.getChunkOwnerPlayer(plugin, entity.getLocation().getChunk());
				if (owner != null && !owner.equals(player.getUniqueId())) {
					event.setCancelled(true);
					PlayerData pd = plugin.players.get(owner);
					player.sendMessage(ChatColor.RED + pd.getUsername() + " 님이 소유 중인 청크입니다");
					return;
				}

				PlayerData pd = plugin.players.get(player.getUniqueId());
				int villageOwner = ChunkController.getChunkOwnerVillage(plugin, entity.getLocation().getChunk());
				if (villageOwner > 0 && villageOwner != pd.getVillage()) {
					event.setCancelled(true);
					VillageData vd = plugin.villages.get(villageOwner);
					player.sendMessage(ChatColor.RED + "마을 [" + vd.getName() + "] 에서 소유 중인 청크입니다");
					return;
				}
			}
		}
	}

	@EventHandler
	public void onInteractArmorStand(PlayerArmorStandManipulateEvent event) {
		Player player = event.getPlayer();

		MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
		MVWorldManager worldManager = core.getMVWorldManager();
		MultiverseWorld world = worldManager.getMVWorld(player.getWorld());

		if (world.getAlias().equalsIgnoreCase("world")) {
			Entity entity = event.getRightClicked();

			UUID owner = ChunkController.getChunkOwnerPlayer(plugin, entity.getLocation().getChunk());
			if (owner != null && !owner.equals(player.getUniqueId())) {
				event.setCancelled(true);
				PlayerData pd = plugin.players.get(owner);
				player.sendMessage(ChatColor.RED + pd.getUsername() + " 님이 소유 중인 청크입니다");
				return;
			}

			PlayerData pd = plugin.players.get(player.getUniqueId());
			int villageOwner = ChunkController.getChunkOwnerVillage(plugin, entity.getLocation().getChunk());
			if (villageOwner > 0 && villageOwner != pd.getVillage()) {
				event.setCancelled(true);
				VillageData vd = plugin.villages.get(villageOwner);
				player.sendMessage(ChatColor.RED + "마을 [" + vd.getName() + "] 에서 소유 중인 청크입니다");
				return;
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

				UUID owner = ChunkController.getChunkOwnerPlayer(plugin, entity.getLocation().getChunk());
				if (owner != null && !owner.equals(player.getUniqueId())) {
					event.setCancelled(true);
					PlayerData pd = plugin.players.get(owner);
					player.sendMessage(ChatColor.RED + pd.getUsername() + " 님이 소유 중인 청크입니다");
					return;
				}

				PlayerData pd = plugin.players.get(player.getUniqueId());
				int villageOwner = ChunkController.getChunkOwnerVillage(plugin, entity.getLocation().getChunk());
				if (villageOwner > 0 && villageOwner != pd.getVillage()) {
					event.setCancelled(true);
					VillageData vd = plugin.villages.get(villageOwner);
					player.sendMessage(ChatColor.RED + "마을 [" + vd.getName() + "] 에서 소유 중인 청크입니다");
					return;
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

				if (bannedEntities.contains(entity.getType())) {

					UUID owner = ChunkController.getChunkOwnerPlayer(plugin, entity.getLocation().getChunk());
					if (owner != null && !owner.equals(player.getUniqueId())) {
						event.setCancelled(true);
						PlayerData pd = plugin.players.get(owner);
						player.sendMessage(ChatColor.RED + pd.getUsername() + " 님이 소유 중인 청크입니다");
					}

					PlayerData pd = plugin.players.get(player.getUniqueId());
					int villageOwner = ChunkController.getChunkOwnerVillage(plugin, entity.getLocation().getChunk());
					if (villageOwner > 0 && villageOwner != pd.getVillage()) {
						event.setCancelled(true);
						VillageData vd = plugin.villages.get(villageOwner);
						player.sendMessage(ChatColor.RED + "마을 [" + vd.getName() + "] 에서 소유 중인 청크입니다");
						return;
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerEnter(PlayerMoveEvent event) {
		Player player = event.getPlayer();

		MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
		MVWorldManager worldManager = core.getMVWorldManager();
		MultiverseWorld world = worldManager.getMVWorld(player.getWorld());

		if (world.getAlias().equalsIgnoreCase("world")) {
			Chunk fromChunk = event.getFrom().getChunk();
			Chunk toChunk = event.getTo().getChunk();

			if (!fromChunk.equals(toChunk)) {
				UUID fromPlayer = ChunkController.getChunkOwnerPlayer(plugin, fromChunk);
				UUID toPlayer = ChunkController.getChunkOwnerPlayer(plugin, toChunk);

				if (fromPlayer == null && toPlayer != null) {
					PlayerData pd = plugin.players.get(toPlayer);
					TextComponent playerName = new TextComponent(pd.getName());
					String color = pd.getNicknameColor();
					if (color != null) {
						playerName.setColor(ChatColor.of(color));
					}
					TextComponent enterMessage = new TextComponent(" 님의 청크");
					player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
							new TextComponent(playerName, enterMessage));
					return;
				}

				int fromVillage = ChunkController.getChunkOwnerVillage(plugin, fromChunk);
				int toVillage = ChunkController.getChunkOwnerVillage(plugin, toChunk);

				if (fromVillage < 0 && toVillage > 0) {
					VillageData vd = plugin.villages.get(toVillage);
					String villageName;
					String color = vd.getColor();
					if (color != null) {
						villageName = ChatColor.of(color) + vd.getName();
					} else {
						villageName = vd.getName();
					}
					player.sendTitle(villageName, null, 20, 20, 20);
					return;
				}
			}
		}
	}

}
