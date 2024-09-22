package com.levelup.tool;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import com.levelup.LevelUp;
import com.levelup.LevelUpIcon;
import com.levelup.chat.ChatController;
import com.levelup.menu.MenuController;
import com.levelup.menu.MenuUnicode;
import com.levelup.player.PlayerEvent;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadableNBTList;
import dev.lone.itemsadder.api.CustomStack;
import net.md_5.bungee.api.ChatColor;

public class ToolEvent implements Listener {

	private LevelUp plugin;
	private Map<BossBar, Long> cooldowns;
	private final int cool = 10;

	public ToolEvent(LevelUp plugin) {
		this.plugin = plugin;
		this.cooldowns = new HashMap<BossBar, Long>();
		ToolController.bossBars = new HashMap<UUID, Map<ToolType, BossBar>>();
		ToolController.MsgShown = new HashMap<UUID, List<ToolType>>();
	}

	@EventHandler
	public void onPlayerBreakBlock(BlockBreakEvent event) {
		Player player = event.getPlayer();

		MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
		MVWorldManager worldManager = core.getMVWorldManager();
		MultiverseWorld world = worldManager.getMVWorld(player.getWorld());

		if (world.getAlias().equalsIgnoreCase("wild") || world.getAlias().equalsIgnoreCase("world_nether")) {
			Block block = event.getBlock();

			ItemStack item = player.getInventory().getItemInMainHand();
			ToolType type = ToolType.get(item.getType());

			if (type == null)
				return;

			ToolData toolData = plugin.tools.get(player.getUniqueId());
			ToolAbstract tool = toolData.getTool(type);

			if (!tool.equals(item))
				return;

			if (tool.getMaterial().toString().toUpperCase().contains("NETHERITE")
					&& tool.getLevel() == plugin.toolQuest.get(type).get(tool.getMaterial()).size())
				return;

			Map<Material, Integer> exp = plugin.toolExp.get(type);
			if (exp != null && exp.keySet().contains(block.getType())) {
				Chunk chunk = block.getChunk();
				NamespacedKey blockData = new NamespacedKey(plugin,
						"block_" + block.getX() + "_" + block.getY() + "_" + block.getZ());
				if (chunk.getPersistentDataContainer().has(blockData)) {
					chunk.getPersistentDataContainer().remove(blockData);
					
				} else {
					tool.addExp(exp.get(block.getType()));

					int maxExp = 0;
					for (ToolQuest q : plugin.toolQuest.get(type).get(item.getType())) {
						if (q.getLevel() == tool.getLevel()) {
							maxExp = q.getExp();
							break;
						}
					}

					double percent = (double) tool.getExp() / (double) maxExp;
					if (percent >= 1.0) {
						if (!ToolController.MsgShown.containsKey(player.getUniqueId())) {
							ToolController.MsgShown.put(player.getUniqueId(), new ArrayList<ToolType>());
						}

						if (!ToolController.MsgShown.get(player.getUniqueId()).contains(type)) {
							player.sendMessage("(경험치 100% 메세지)");
							player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
							ToolController.MsgShown.get(player.getUniqueId()).add(type);
						}

						percent = 1.0;
					}

					BossBar bossBar = ToolController.bossBars.get(player.getUniqueId()).get(type);
					bossBar.setProgress(percent);
					if (!bossBar.isVisible())
						bossBar.setVisible(true);
					activateCooldown(bossBar);

					Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

						@Override
						public void run() {
							if (hasCooldown(bossBar)) {
								bossBar.setVisible(false);
							}
						}

					}, 20 * cool);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerPlaceBlock(BlockPlaceEvent event) {
		Player player = event.getPlayer();

		MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
		MVWorldManager worldManager = core.getMVWorldManager();
		MultiverseWorld world = worldManager.getMVWorld(player.getWorld());

		if (world.getAlias().equalsIgnoreCase("wild") || world.getAlias().equalsIgnoreCase("world_nether")) {
			Block block = event.getBlock();
			Chunk chunk = block.getChunk();

			if (plugin.toolExp.get(ToolType.PICKAXE).keySet().contains(block.getType())
					|| plugin.toolExp.get(ToolType.AXE).keySet().contains(block.getType())
					|| plugin.toolExp.get(ToolType.SHOVEL).keySet().contains(block.getType())) {

				NamespacedKey blockData = new NamespacedKey(plugin,
						"block_" + block.getX() + "_" + block.getY() + "_" + block.getZ());

				chunk.getPersistentDataContainer().set(blockData, PersistentDataType.BOOLEAN, true);
			}
		}
	}

	@EventHandler
	public void onPlayerKillEntity(EntityDeathEvent event) {
		Entity entity = event.getEntity();

		MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
		MVWorldManager worldManager = core.getMVWorldManager();
		MultiverseWorld world = worldManager.getMVWorld(entity.getWorld());

		if (world.getAlias().equalsIgnoreCase("wild") || world.getAlias().equalsIgnoreCase("world_nether")) {
			if (entity instanceof LivingEntity && event.getEntity().getKiller() != null) {
				LivingEntity livingEntity = (LivingEntity) entity;
				Player player = event.getEntity().getKiller();

				ItemStack item = player.getInventory().getItemInMainHand();
				ToolType type = ToolType.get(item.getType());

				if (type == null)
					return;

				ToolData toolData = plugin.tools.get(player.getUniqueId());
				ToolAbstract tool = toolData.getTool(type);

				if (!tool.equals(item))
					return;

				int exp = (int) livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
				if (type == ToolType.AXE) {
					tool.addExp(exp);
					System.out.println(exp);

				} else if (type == ToolType.SWORD) {
					tool.addExp(exp * 2);
					System.out.println(exp * 2);
				}

				int maxExp = 0;
				for (ToolQuest q : plugin.toolQuest.get(type).get(item.getType())) {
					if (q.getLevel() == tool.getLevel()) {
						maxExp = q.getExp();
						break;
					}
				}

				double percent = (double) tool.getExp() / (double) maxExp;
				if (percent >= 1.0) {
					if (!ToolController.MsgShown.containsKey(player.getUniqueId())) {
						ToolController.MsgShown.put(player.getUniqueId(), new ArrayList<ToolType>());
					}

					if (!ToolController.MsgShown.get(player.getUniqueId()).contains(type)) {
						player.sendMessage("(경험치 100% 메세지)");
						player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
						ToolController.MsgShown.get(player.getUniqueId()).add(type);
					}

					percent = 1.0;
				}

				BossBar bossBar = ToolController.bossBars.get(player.getUniqueId()).get(type);
				bossBar.setProgress(percent);
				if (!bossBar.isVisible())
					bossBar.setVisible(true);
				activateCooldown(bossBar);

				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

					@Override
					public void run() {
						if (hasCooldown(bossBar)) {
							bossBar.setVisible(false);
						}
					}

				}, 20 * cool);
			}
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

				ItemStack pickaxe = tool.getPickaxe().getAsItemStack();
				ItemStack axe = tool.getAxe().getAsItemStack();
				ItemStack sword = tool.getSword().getAsItemStack();
				ItemStack shovel = tool.getShovel().getAsItemStack();

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
	public void onPlayerClickToolInv(InventoryClickEvent event) throws SQLException {
		Player player = (Player) event.getWhoClicked();
		ItemStack item = event.getCurrentItem();
		ToolData toolData = plugin.tools.get(player.getUniqueId());

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

				if (event.getClickedInventory() != null) {

					if (event.getClickedInventory().equals(playerInv)) {

						if (toolData.getPickaxe().equals(item)) {
							toolInv.setItem(1, item);
							playerInv.remove(item);

						} else if (toolData.getAxe().equals(item)) {
							toolInv.setItem(3, item);
							playerInv.remove(item);

						} else if (toolData.getSword().equals(item)) {
							toolInv.setItem(5, item);
							playerInv.remove(item);

						} else if (toolData.getShovel().equals(item)) {
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
			}

		} else if (event.getView().getTitle().equals(MenuController.getInventoryTitle(MenuUnicode.SKIN_TICKET.val()))) {
			Inventory ticketInv = event.getView().getTopInventory();
			Inventory playerInv = player.getInventory();

			if (event.getClickedInventory() != null) {
				if (event.getClickedInventory().equals(playerInv) && item != null
						&& (event.getClick().equals(ClickType.SHIFT_LEFT)
								|| event.getClick().equals(ClickType.SHIFT_RIGHT))) {
					event.setCancelled(true);
					if (ticketInv.getItem(MenuController.slot(1, 3)) != null) {
						playerInv.addItem(ticketInv.getItem(MenuController.slot(1, 3)));
					}

					ticketInv.setItem(MenuController.slot(1, 3), item);
					playerInv.remove(item);
					return;
				}

				if (event.getClickedInventory().equals(ticketInv)) {

					if (event.getSlot() == MenuController.slot(1, 4) || event.getSlot() == MenuController.slot(1, 5)
							|| event.getSlot() == MenuController.slot(1, 6)) {
						event.setCancelled(true);

						ItemStack skin = ticketInv.getItem(MenuController.slot(1, 3));
						if (skin != null) {
							CustomStack customItem = CustomStack.byItemStack(skin);
							String regex = "(\\w+):(\\w+)_(pickaxe|axe|sword|shovel)";
							Pattern pattern = Pattern.compile(regex);
							Matcher matcher = pattern.matcher(customItem.getNamespacedID());
							if (customItem != null && customItem.getItemStack().getType().equals(Material.STICK)
									&& matcher.matches()) {

								if (ToolController.applyToolSkin(plugin, player.getUniqueId(),
										customItem.getNamespacedID())) {
									ticketInv.clear(MenuController.slot(1, 3));
									player.closeInventory();

									ItemStack ticket = player.getInventory().getItemInMainHand();
									CustomStack customTicket = CustomStack.byItemStack(ticket);
									if (customTicket != null
											&& customTicket.getNamespacedID().equals(PlayerEvent.SKIN_TICKET)) {
										ticket.setAmount(ticket.getAmount() - 1);
									}

									player.playSound(player, Sound.BLOCK_ANVIL_USE, 1.0F, 1.0F);
								}
							}
						}

					} else if (event.getSlot() != MenuController.slot(1, 3)) {
						event.setCancelled(true);
					}
				}
			}

		} else if (event.getView().getTopInventory().getType().equals(InventoryType.ANVIL)) {
			AnvilInventory anvilInv = (AnvilInventory) event.getView().getTopInventory();

			if (toolData.getPickaxe().equals(anvilInv.getItem(0)) || toolData.getAxe().equals(anvilInv.getItem(0))
					|| toolData.getSword().equals(anvilInv.getItem(0))
					|| toolData.getShovel().equals(anvilInv.getItem(0))) {

				if (event.getClickedInventory() != null && event.getClickedInventory().equals(anvilInv)
						&& event.getSlot() == 2) {
					event.setCancelled(true);

					if (anvilInv.getItem(2) != null && player.getItemOnCursor().getType().equals(Material.AIR)) {
						ToolType type = ToolType.get(anvilInv.getItem(0).getType());
						ToolAbstract tool = toolData.getTool(type);

						if (tool.getCustomskin() != null) {

							NBT.get(anvilInv.getItem(0), nbt -> {
								try {
									String newName = anvilInv.getRenameText();
									LevelUpIcon icon = tool.getIcon();
									newName = newName.replace(String.valueOf(icon.val()), "").strip();

									if (nbt.hasTag("ToolColor")) {
										ReadableNBTList<String> toolColor = (ReadableNBTList<String>) nbt
												.getStringList("ToolColor");
										List<ChatColor> gradient = new ArrayList<ChatColor>();

										for (String colorCode : toolColor) {
											gradient.add(ChatColor.of(colorCode));
										}

										newName = ChatController.gradient(newName, gradient);
									}

									if (icon != null) {
										newName = ChatColor.WHITE + Character.toString(icon.val()) + " " + newName;
									}

									ToolController.updateToolName(plugin, player.getUniqueId(), newName, type);

								} catch (SQLException e) {
									e.printStackTrace();
								}
							});

						} else {
							ToolController.updateToolName(plugin, player.getUniqueId(), anvilInv.getRenameText(), type);
						}

						anvilInv.setItem(0, null);
						anvilInv.setItem(2, null);
						player.setItemOnCursor(tool.getAsItemStack());
						player.playSound(player, Sound.BLOCK_ANVIL_USE, 1.0F, 1.0F);
					}

				} else if (event.getClickedInventory() != null && event.getClickedInventory().equals(anvilInv)
						&& event.getSlot() != 0) {
					event.setCancelled(true);
				}

			}

		} else {
			List<InventoryType> allowed = Arrays.asList(InventoryType.CRAFTING, InventoryType.CREATIVE);

			if (!allowed.contains(event.getView().getTopInventory().getType())) {

				if (toolData.getPickaxe().equals(item) || toolData.getAxe().equals(item)
						|| toolData.getSword().equals(item) || toolData.getShovel().equals(item)) {
					event.setCancelled(true);
				}

			}
		}
	}

	@EventHandler
	public void onPlayerDragToolInv(InventoryDragEvent event) {
		if (event.getView().getTitle().equals(MenuController.getInventoryTitle(MenuUnicode.SKIN_TICKET.val()))) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerCloseInv(InventoryCloseEvent event) {
		if (event.getView().getTitle().equals(MenuController.getInventoryTitle(MenuUnicode.SKIN_TICKET.val()))) {
			Player player = (Player) event.getPlayer();
			Inventory ticketInv = event.getView().getTopInventory();
			Inventory playerInv = player.getInventory();
			ItemStack skin = ticketInv.getItem(MenuController.slot(1, 3));
			if (skin != null) {
				playerInv.addItem(skin);
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

		} else if (tool.getPickaxe().equals(item) || tool.getAxe().equals(item) || tool.getSword().equals(item)
				|| tool.getShovel().equals(item)) {
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

	@EventHandler
	public void onInteractEntity(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();
		CustomStack toolbox = CustomStack.byItemStack(item);
		if (toolbox != null && toolbox.getNamespacedID().equals(ToolController.TOOLBOX_ID)) {
			event.setCancelled(true);
		}
	}

	public boolean hasCooldown(BossBar bossBar) {
		if (cooldowns.get(bossBar) < (System.currentTimeMillis() - (cool - 1) * 1000)) {
			return true;
		} else {
			return false;
		}
	}

	public void activateCooldown(BossBar bossBar) {
		cooldowns.put(bossBar, System.currentTimeMillis());
	}

}
