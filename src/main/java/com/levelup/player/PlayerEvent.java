package com.levelup.player;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import com.levelup.LevelUp;
import com.levelup.LevelUpIcon;
import com.levelup.message.MessageController;
import com.levelup.post.PostController;
import com.levelup.scoreboard.ScoreboardController;
import com.levelup.seasonpass.SeasonPassController;
import com.levelup.seasonpass.SeasonPassController.SeasonPass;
import com.levelup.tool.ToolController;
import com.levelup.tool.ToolData;
import com.levelup.village.VillageController;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;

import dev.lone.itemsadder.api.CustomStack;
import net.md_5.bungee.api.ChatColor;

public class PlayerEvent implements Listener {

	private LevelUp plugin;

	public static final String HOME_TICKET = "customitems:home_ticket";
	public static final String SKIN_TICKET = "customitems:skin_ticket";

	private Map<Player, List<ItemStack>> items;

	public PlayerEvent(LevelUp plugin) {
		this.plugin = plugin;
		this.items = new HashMap<Player, List<ItemStack>>();
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) throws SQLException {
		final Player player = event.getPlayer();
		PlayerData pd = plugin.players.get(player.getUniqueId());

		if (pd == null) {
			PlayerController.addPlayer(plugin, player);

			MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
			MVWorldManager worldManager = core.getMVWorldManager();
			MultiverseWorld world = worldManager.getMVWorld("tutorial");
			Location loc = world.getCBWorld().getBlockAt(96, 66, 176).getLocation();
			player.teleport(loc);

		} else {
			if (!player.getName().equalsIgnoreCase(pd.getUsername())) {
				PlayerController.updatePlayer(plugin, player);
			}
			PlayerController.updateLastOnline(plugin, player.getUniqueId());
		}

		// tool box
		CustomStack toolbox = CustomStack.getInstance(ToolController.TOOLBOX_ID);
		player.getInventory().setItem(ToolController.TOOLBOX_SLOT, toolbox.getItemStack().clone());
		if (!plugin.tools.containsKey(player.getUniqueId()))
			ToolController.getNewTools(plugin, player);

		if (!plugin.quests.containsKey(player.getUniqueId()))
			ToolController.initQuest(plugin, player.getUniqueId());

		if (!plugin.toolSkins.containsKey(player.getUniqueId()))
			ToolController.initToolSkin(plugin, player.getUniqueId());

		// tool bossbar
		ToolController.getNewBossBars(plugin, player);
		ToolController.getNewBoostBars(plugin, player);

		// tax message
		VillageController.taxUpdateMessage(plugin, player);
		VillageController.deletionUpdateMessage(plugin, player);

		// seasonpass
		if (!plugin.seasonPassData.containsKey(player.getUniqueId()))
			SeasonPassController.initSeasonPass(plugin, player);

		// player list
		PlayerController.updateListName(plugin, player);

		String listHeader = Character.toString(LevelUpIcon.LOGO.val()) + "\n\n\n\n";
		player.setPlayerListHeader(listHeader);

		int tick = (int) plugin.getServer().getServerTickManager().getTickRate();
		int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

			@Override
			public void run() {
				ScoreboardController.displayScoreboard(plugin, player);
				PlayerController.updateListFooter(plugin, player);
			}

		}, 0, tick);

		if (!PlayerController.playerScheduler.containsKey(player.getUniqueId()))
			PlayerController.playerScheduler.put(player.getUniqueId(), new ArrayList<Integer>());
		PlayerController.playerScheduler.get(player.getUniqueId()).add(taskId);

		// SeasonPass
		SeasonPass seasonPass = plugin.seasonPassData.get(player.getUniqueId());
		if (!seasonPass.getLastDate().equals(LocalDate.now()) && seasonPass.getAvailable() < 20) {
			SeasonPassController.checkAttendance(plugin, player);
		}

		// Message
		MessageController.sendPendingMessages(plugin, player);

		// Post
		PostController.alertPlayer(plugin, player);

		// Invincible
		PlayerController.invinciblePlayers.add(player.getUniqueId());
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

			@Override
			public void run() {
				PlayerController.invinciblePlayers.remove(player.getUniqueId());
			}

		}, tick * 20);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) throws SQLException {
		Player player = event.getPlayer();
		PlayerController.updateLastOnline(plugin, player.getUniqueId());
		ToolController.updateToolExp(plugin, player.getUniqueId());
		for (int taskId : PlayerController.playerScheduler.get(player.getUniqueId())) {
			Bukkit.getScheduler().cancelTask(taskId);
		}
	}

	@EventHandler
	public void onPlayerHat(InventoryClickEvent event) {
		if (event.getView().getTopInventory().getType() != null
				&& event.getView().getTopInventory().getType().equals(InventoryType.CRAFTING)
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
	public void onPlayerDamaged(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player player) {
			if (PlayerController.invinciblePlayers.contains(player.getUniqueId())) {
				event.setCancelled(true);
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

			if (tool.getPickaxe().equals(item) || tool.getAxe().equals(item) || tool.getSword().equals(item)
					|| tool.getShovel().equals(item)) {
				items.get(player).add(item);
				event.getDrops().remove(item);

			} else if (custom != null) {
				if (custom.getNamespacedID().equals(ToolController.TOOLBOX_ID)) {
					event.getDrops().remove(item);

				} else if (custom.getNamespacedID().contains("_key")) {
					items.get(player).add(item);
					event.getDrops().remove(item);

				} else if (custom.getNamespacedID().contains("_bag")) {
					items.get(player).add(item);
					event.getDrops().remove(item);

				} else if (plugin.cashItems.contains(custom.getNamespacedID())) {
					items.get(player).add(item);
					event.getDrops().remove(item);

				} else {
					ItemMeta itemMeta = item.getItemMeta();
					itemMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "last_drop"),
							PersistentDataType.STRING, LocalDateTime.now().toString());
					item.setItemMeta(itemMeta);
				}

			} else {
				ItemMeta itemMeta = item.getItemMeta();
				itemMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "last_drop"),
						PersistentDataType.STRING, LocalDateTime.now().toString());
				item.setItemMeta(itemMeta);
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
	public void onPreparedCraft(PrepareItemCraftEvent event) {
		List<Material> banned = Arrays.asList(Material.BLACK_SHULKER_BOX, Material.BLUE_SHULKER_BOX,
				Material.BROWN_SHULKER_BOX, Material.CYAN_SHULKER_BOX, Material.GRAY_SHULKER_BOX,
				Material.GREEN_SHULKER_BOX, Material.LIGHT_BLUE_SHULKER_BOX, Material.LIGHT_GRAY_SHULKER_BOX,
				Material.LIME_SHULKER_BOX, Material.MAGENTA_SHULKER_BOX, Material.ORANGE_SHULKER_BOX,
				Material.PINK_SHULKER_BOX, Material.PURPLE_SHULKER_BOX, Material.RED_SHULKER_BOX,
				Material.WHITE_SHULKER_BOX, Material.YELLOW_SHULKER_BOX);
		ItemStack result = event.getInventory().getResult();
		if (result != null && banned.contains(result.getType())) {
			event.getInventory().setResult(null);
		}
	}

	@EventHandler
	public void onPlayerCraft(CraftItemEvent event) {
		List<Material> banned = Arrays.asList(Material.WOODEN_PICKAXE, Material.WOODEN_AXE, Material.WOODEN_SWORD,
				Material.WOODEN_SHOVEL, Material.STONE_PICKAXE, Material.STONE_AXE, Material.STONE_SWORD,
				Material.STONE_SHOVEL, Material.IRON_PICKAXE, Material.IRON_AXE, Material.IRON_SWORD,
				Material.IRON_SHOVEL, Material.GOLDEN_PICKAXE, Material.GOLDEN_AXE, Material.GOLDEN_SWORD,
				Material.GOLDEN_SHOVEL, Material.DIAMOND_PICKAXE, Material.DIAMOND_AXE, Material.DIAMOND_SWORD,
				Material.DIAMOND_SHOVEL, Material.NETHERITE_PICKAXE, Material.NETHERITE_AXE, Material.NETHERITE_SWORD,
				Material.NETHERITE_SHOVEL, Material.ENDER_CHEST);

		if (event.getRecipe().getResult().getType().equals(Material.FLINT_AND_STEEL)
				|| event.getRecipe().getResult().getType().equals(Material.ARROW)
				|| event.getRecipe().getResult().getType().equals(Material.FLETCHING_TABLE)) {
			Inventory inv = event.getInventory();
			List<ItemStack> items = Arrays.asList(inv.getContents());

			for (ItemStack item : items) {
				CustomStack custom = CustomStack.byItemStack(item);

				if (custom != null) {
					event.setCancelled(true);
				}
			}

		} else if (banned.contains(event.getRecipe().getResult().getType())) {
			event.setCancelled(true);

		}
	}

	@EventHandler
	public void onPlayerUseItem(PlayerInteractEvent event) {
		if (event.getHand() == EquipmentSlot.OFF_HAND)
			return;

		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player player = event.getPlayer();
			CustomStack customItem = CustomStack.byItemStack(event.getItem());
			if (customItem != null) {
				if (customItem.getNamespacedID().equals(HOME_TICKET)) {
					event.setCancelled(true);
					if (player.getRespawnLocation() != null) {
						Location respawn = player.getRespawnLocation();
						player.teleport(respawn);
						ItemStack item = event.getItem();
						item.setAmount(item.getAmount() - 1);
					} else {
						player.sendMessage(ChatColor.RED + "마지막으로 사용한 침대가 파괴되었거나 존재하지 않습니다");
					}

				} else if (customItem.getNamespacedID().equals(SKIN_TICKET)) {
					event.setCancelled(true);
					Inventory ticketInv = ToolController.getSkinTicketInventory(plugin, player);
					player.openInventory(ticketInv);
				}

			}
		}
	}

	@EventHandler
	public void onPlayerPortal(PlayerPortalEvent event) {
		event.setCancelled(true);
	}

}
