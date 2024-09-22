package com.levelup.player;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.levelup.LevelUp;
import com.levelup.chat.ChatController;
import com.levelup.scoreboard.ScoreboardController;
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

		// tool bossbar
		ToolController.getNewBossBars(plugin, player);

		// tax message
		VillageController.taxUpdateMessage(plugin, player);

		// player list
		PlayerController.updateListName(plugin, player);

		String listHeader = "\n            "
				+ ChatController.gradient(" LEVEL UP ", ChatColor.of("#22B14C"), ChatColor.of("#B5E61D"))
				+ "            \n";
		player.setPlayerListHeader(listHeader);

		int tick = (int) plugin.getServer().getServerTickManager().getTickRate();
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

			@Override
			public void run() {
				ScoreboardController.displayScoreboard(plugin, player);
				PlayerController.updateListFooter(plugin, player);
			}

		}, 0, tick);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) throws SQLException {
		Player player = event.getPlayer();
		PlayerController.updateLastOnline(plugin, player.getUniqueId());
		ToolController.updateToolExp(plugin, player.getUniqueId());
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
				Material.IRON_SHOVEL, Material.GOLDEN_PICKAXE, Material.GOLDEN_AXE, Material.GOLDEN_SWORD,
				Material.GOLDEN_SHOVEL, Material.DIAMOND_PICKAXE, Material.DIAMOND_AXE, Material.DIAMOND_SWORD,
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

}
