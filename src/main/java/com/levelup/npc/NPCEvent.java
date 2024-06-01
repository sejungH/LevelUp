package com.levelup.npc;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;

import com.levelup.LevelUp;
import com.levelup.menu.MenuController;
import com.levelup.menu.MenuUnicode;

import dev.lone.itemsadder.api.CustomStack;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;

public class NPCEvent implements Listener {

	private LevelUp plugin;
	private Map<UUID, List<Inventory>> npcInv;

	public NPCEvent(LevelUp plugin) {
		this.plugin = plugin;
		this.npcInv = new HashMap<UUID, List<Inventory>>();
	}

	@EventHandler
	public void onNPCDeath(EntityDeathEvent event) throws SQLException {
		Entity entity = event.getEntity();

		if (MythicBukkit.inst().getMobManager().isMythicMob(entity)) {
			UUID uuid = entity.getUniqueId();
			NPCController.deleteNPC(plugin, uuid);
			npcInv.remove(uuid);
		}
	}

	@EventHandler
	public void onPlayerRightClick(PlayerInteractEntityEvent event) {
		Player player = (Player) event.getPlayer();
		Entity entity = event.getRightClicked();
		ActiveMob mythicMob = MythicBukkit.inst().getMobManager().getActiveMob(entity.getUniqueId()).orElse(null);

		if (player.isOp() && player.isSneaking() && mythicMob != null
				&& mythicMob.getType().getInternalName().toUpperCase().startsWith("NPC")) {
			event.setCancelled(true);
			if (npcInv.containsKey(entity.getUniqueId())) {
				npcInv.remove(entity.getUniqueId());
			}
			player.openInventory(NPCController.getFirstNPCInventory(plugin, player, entity.getUniqueId()));

		} else {
			if (plugin.npcs.containsKey(entity.getUniqueId())) {
				openTradeGUI(player, entity.getUniqueId());
			}
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) throws SQLException {
		Inventory inv = event.getClickedInventory();

		if (event.getView().getTitle().equals(MenuController.getInventoryTitle(MenuUnicode.NPC_1.val()))) {
			if (inv != null && inv.equals(event.getView().getTopInventory())) {
				clickFirstNPCInventory(event);
			}
		} else if (event.getView().getTitle().equals(MenuController.getInventoryTitle(MenuUnicode.NPC_2.val()))) {
			if (inv != null && inv.equals(event.getView().getTopInventory())) {
				clickSecondNPCInventory(event);
			}
		} else if (event.getView().getTitle().equals(MenuController.getInventoryTitle(MenuUnicode.NPC_3.val()))) {
			if (inv != null && inv.equals(event.getView().getTopInventory())) {
				clickThirdNPCInventory(event);
			}
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) throws SQLException {
		Inventory inv = event.getInventory();

		if (event.getView().getTitle().equals(MenuController.getInventoryTitle(MenuUnicode.NPC_1.val()))) {
			ItemStack npcID = inv.getItem(0);
			UUID uuid = UUID.fromString(npcID.getItemMeta().getDisplayName());

			ItemStack nameTag = inv.getItem(49);
			if (nameTag != null) {
				String name = nameTag.getItemMeta().getDisplayName();

				Entity entity = plugin.getServer().getEntity(uuid);
				entity.setCustomName(name);
			}
			saveTradeList(uuid);

		} else if (event.getView().getTitle().equals(MenuController.getInventoryTitle(MenuUnicode.NPC_2.val()))) {
			ItemStack npcID = inv.getItem(49);
			UUID uuid = UUID.fromString(npcID.getItemMeta().getDisplayName());
			npcInv.get(uuid).set(0, inv);
			saveTradeList(uuid);

		} else if (event.getView().getTitle().equals(MenuController.getInventoryTitle(MenuUnicode.NPC_3.val()))) {
			ItemStack npcID = inv.getItem(49);
			UUID uuid = UUID.fromString(npcID.getItemMeta().getDisplayName());
			npcInv.get(uuid).set(1, inv);
			saveTradeList(uuid);
		}
	}

	public void clickFirstNPCInventory(InventoryClickEvent event) throws SQLException {
		Player player = (Player) event.getWhoClicked();
		Inventory inv = event.getInventory();

		ItemStack npcID = inv.getItem(0);
		UUID uuid = UUID.fromString(npcID.getItemMeta().getDisplayName());
		LivingEntity npc = (LivingEntity) plugin.getServer().getEntity(uuid);

		ItemStack item = event.getCursor();
		int slot = event.getSlot();

		if (slot == 8) {
			event.setCancelled(true);
			npc.setHealth(0);
			event.getWhoClicked().closeInventory();

		} else if (slot == 49) {
			if (item == null || !item.getType().equals(Material.NAME_TAG)) {
				event.setCancelled(true);
			}

		} else if (slot == 53) {
			event.setCancelled(true);
			if (!npcInv.containsKey(uuid) || npcInv.get(uuid).isEmpty()) {
				npcInv.put(uuid, new ArrayList<Inventory>());
				npcInv.get(uuid).add(NPCController.getSecondNPCInventory(plugin, player, uuid));
				npcInv.get(uuid).add(NPCController.getThirdNPCInventory(plugin, player, uuid));
			}
			player.openInventory(npcInv.get(uuid).get(0));

		} else {
			if (item != null) {
				if (slot == MenuController.slot(0, 4)) {
					npc.getEquipment().setHelmet(item);

				} else if (slot == MenuController.slot(1, 4)) {
					npc.getEquipment().setChestplate(item);

				} else if (slot == MenuController.slot(2, 4)) {
					npc.getEquipment().setLeggings(item);

				} else if (slot == MenuController.slot(3, 4)) {
					npc.getEquipment().setBoots(item);

				} else if (slot == MenuController.slot(1, 2)) {
					npc.getEquipment().setItemInOffHand(item);

				} else if (slot == MenuController.slot(1, 6)) {
					npc.getEquipment().setItemInMainHand(item);

				} else {
					event.setCancelled(true);
				}

			} else {
				event.setCancelled(true);
			}
		}
	}

	public void clickSecondNPCInventory(InventoryClickEvent event) throws SQLException {
		Player player = (Player) event.getWhoClicked();
		Inventory inv = event.getInventory();
		ItemStack npcID = inv.getItem(49);
		UUID uuid = UUID.fromString(npcID.getItemMeta().getDisplayName());

		ItemStack item = event.getCursor();
		int slot = event.getSlot();

		if (slot == 45) {
			event.setCancelled(true);
			npcInv.get(uuid).set(0, inv);
			player.openInventory(NPCController.getFirstNPCInventory(plugin, player, uuid));

		} else if (slot == 53) {
			event.setCancelled(true);
			npcInv.get(uuid).set(0, inv);
			player.openInventory(npcInv.get(uuid).get(1));
		}

		List<Integer> lastRow = Arrays.asList(46, 47, 48, 49, 50, 51, 52);
		if (item != null) {
			if (slot % 9 == 2 || slot % 9 == 4 || slot % 9 == 7 || lastRow.contains(slot)) {
				event.setCancelled(true);
			}
		} else {
			event.setCancelled(true);
		}
	}

	private void clickThirdNPCInventory(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		Inventory inv = event.getInventory();
		ItemStack npcID = inv.getItem(49);
		UUID uuid = UUID.fromString(npcID.getItemMeta().getDisplayName());

		ItemStack item = event.getCursor();
		int slot = event.getSlot();

		if (slot == 45) {
			event.setCancelled(true);
			npcInv.get(uuid).set(1, inv);
			player.openInventory(npcInv.get(uuid).get(0));
		}

		List<Integer> lastRow = Arrays.asList(46, 47, 48, 49, 50, 51, 52, 53);
		if (item != null) {
			if (slot % 9 == 2 || slot % 9 == 4 || slot % 9 == 7 || lastRow.contains(slot)) {
				event.setCancelled(true);
			}
		} else {
			event.setCancelled(true);
		}
	}

	private void saveTradeList(UUID uuid) throws SQLException {
		if (npcInv.containsKey(uuid)) {
			List<NPCTrade> tradeList = new ArrayList<NPCTrade>();

			int row = 0;
			int col = 0;

			while (!npcInv.get(uuid).isEmpty()) {
				ItemStack firstSlot = npcInv.get(uuid).get(0).getItem(MenuController.slot(row, col));
				ItemStack secondSlot = npcInv.get(uuid).get(0).getItem(MenuController.slot(row, col + 1));
				ItemStack resultSlot = npcInv.get(uuid).get(0).getItem(MenuController.slot(row, col + 3));

				if ((firstSlot != null || secondSlot != null) && resultSlot != null) {
					NPCTradeItem item1 = firstSlot == null ? null : new NPCTradeItem(firstSlot);
					NPCTradeItem item2 = secondSlot == null ? null : new NPCTradeItem(secondSlot);
					NPCTradeItem result = new NPCTradeItem(resultSlot);

					if (item1 != null && item2 != null) {
						tradeList.add(new NPCTrade(item1, item2, result));

					} else if (item1 != null && item2 == null) {
						tradeList.add(new NPCTrade(item1, result));

					} else if (item1 == null && item2 != null) {
						tradeList.add(new NPCTrade(item2, result));
					}
				}

				row++;

				if (row == 5) {

					if (col == 5) {
						break;

					} else {
						row = 0;
						col = 5;
					}
				}
			}

			row = 0;
			col = 0;

			while (npcInv.get(uuid).size() > 1) {
				ItemStack firstSlot = npcInv.get(uuid).get(1).getItem(MenuController.slot(row, col));
				ItemStack secondSlot = npcInv.get(uuid).get(1).getItem(MenuController.slot(row, col + 1));
				ItemStack resultSlot = npcInv.get(uuid).get(1).getItem(MenuController.slot(row, col + 3));

				if ((firstSlot != null || secondSlot != null) && resultSlot != null) {
					NPCTradeItem item1 = firstSlot == null ? null : new NPCTradeItem(firstSlot);
					NPCTradeItem item2 = secondSlot == null ? null : new NPCTradeItem(secondSlot);
					NPCTradeItem result = new NPCTradeItem(resultSlot);

					if (item1 != null && item2 != null) {
						tradeList.add(new NPCTrade(item1, item2, result));

					} else if (item1 != null && item2 == null) {
						tradeList.add(new NPCTrade(item1, result));

					} else if (item1 == null && item2 != null) {
						tradeList.add(new NPCTrade(item2, result));
					}
				}

				row++;

				if (row == 5) {

					if (col == 5) {
						break;

					} else {
						row = 0;
						col = 5;
					}
				}
			}

			if (tradeList.isEmpty()) {

				if (plugin.npcs.containsKey(uuid)) {
					plugin.npcs.remove(uuid);
					NPCController.deleteNPC(plugin, uuid);
				}

			} else {

				if (plugin.npcs.containsKey(uuid)) {
					plugin.npcs.replace(uuid, tradeList);

				} else {
					plugin.npcs.put(uuid, tradeList);
					NPCController.addNPC(plugin, uuid);
				}

				NPCController.setTradeList(plugin, uuid, tradeList);
			}
		}
	}

	private void openTradeGUI(Player player, UUID uuid) {
		Entity entity = plugin.getServer().getEntity(uuid);
		Merchant merchant = plugin.getServer().createMerchant(entity.getCustomName());
		List<MerchantRecipe> recipes = new ArrayList<MerchantRecipe>();

		List<NPCTrade> tradeList = plugin.npcs.get(uuid);

		for (NPCTrade trade : tradeList) {

			ItemStack item1 = null;
			if (trade.getItem1().getNamespacedID() != null) {
				item1 = CustomStack.getInstance(trade.getItem1().getNamespacedID()).getItemStack().clone();
			} else {
				item1 = new ItemStack(Material.getMaterial(trade.getItem1().getMaterial()));
			}
			item1.setAmount(trade.getItem1().getCount());

			ItemStack item2 = null;
			if (trade.getItem2() != null) {
				if (trade.getItem2().getNamespacedID() != null) {
					item2 = CustomStack.getInstance(trade.getItem2().getNamespacedID()).getItemStack().clone();
				} else {
					item2 = new ItemStack(Material.getMaterial(trade.getItem2().getMaterial()));
				}
				item2.setAmount(trade.getItem2().getCount());
			}

			ItemStack result = null;
			if (trade.getResult().getNamespacedID() != null) {
				result = CustomStack.getInstance(trade.getResult().getNamespacedID()).getItemStack().clone();
			} else {
				result = new ItemStack(Material.getMaterial(trade.getResult().getMaterial()));
			}
			result.setAmount(trade.getResult().getCount());

			MerchantRecipe recipe = new MerchantRecipe(result, 10000);
			recipe.addIngredient(item1);
			if (item2 != null) {
				recipe.addIngredient(item2);
			}

			recipes.add(recipe);
		}

		merchant.setRecipes(recipes);
		player.openMerchant(merchant, true);
	}

}
