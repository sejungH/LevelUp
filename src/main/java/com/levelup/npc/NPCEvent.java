package com.levelup.npc;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.persistence.PersistentDataType;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.levelup.LevelUp;
import com.levelup.LevelUpItem;
import com.levelup.menu.MenuController;
import com.levelup.menu.MenuUnicode;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;

public class NPCEvent implements Listener {

	private LevelUp plugin;
	private Map<UUID, List<Inventory>> npcInv;

	public NPCEvent(LevelUp plugin) {
		this.plugin = plugin;
		this.npcInv = new HashMap<UUID, List<Inventory>>();
	}

	@EventHandler
	public void onPlayerRightClick(PlayerInteractEntityEvent event) throws SQLException {
		Player player = (Player) event.getPlayer();
		Entity entity = event.getRightClicked();

		if (event.getHand() == EquipmentSlot.OFF_HAND)
			return;

		NPC npc = CitizensAPI.getNPCRegistry().getNPC(entity);
		NamespacedKey npcKey = new NamespacedKey(plugin, "levelup_npc");
		if (entity.getPersistentDataContainer().has(npcKey) || (npc != null && npc.data().has("levelup_npc"))) {
			event.setCancelled(true);

			if (player.isOp() && player.isSneaking()) {
				player.openInventory(NPCController.getFirstNPCInventory(plugin, player, entity.getUniqueId()));

			} else {
				NamespacedKey typeKey = new NamespacedKey(plugin, "levelup_npc_type");
				NamespacedKey tradeKey = new NamespacedKey(plugin, "levelup_npc_trade");
				if (entity.getPersistentDataContainer().has(typeKey)) {
					String type = entity.getPersistentDataContainer().get(typeKey, PersistentDataType.STRING);

					if (type.equalsIgnoreCase("blacksmith")) {
						NPCController.showDefaultMessage(plugin, player);
					}

				} else if (entity.getPersistentDataContainer().has(tradeKey)) {
					openTradeGUI(player, entity.getUniqueId());
				}
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
			npcInv.remove(npc.getUniqueId());
			NPC citizenNPC = CitizensAPI.getNPCRegistry().getByUniqueId(npc.getUniqueId());

			if (citizenNPC != null) {
				citizenNPC.destroy();
			}
			npc.remove();

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

				NPC npc = CitizensAPI.getNPCRegistry().getNPC(entity);
				if (npc != null) {
					npc.setName(name);
				}
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

	private void saveTradeList(UUID uuid) throws SQLException {
		Entity entity = plugin.getServer().getEntity(uuid);

		if (npcInv.containsKey(uuid)) {
			JsonArray jsonArray = new JsonArray();

			int row = 0;
			int col = 0;

			while (!npcInv.get(uuid).isEmpty()) {
				JsonObject jsonObject = new JsonObject();

				ItemStack firstSlot = npcInv.get(uuid).get(0).getItem(MenuController.slot(row, col));
				ItemStack secondSlot = npcInv.get(uuid).get(0).getItem(MenuController.slot(row, col + 1));
				ItemStack resultSlot = npcInv.get(uuid).get(0).getItem(MenuController.slot(row, col + 3));

				if ((firstSlot != null || secondSlot != null) && resultSlot != null) {
					LevelUpItem item1 = null;
					if (firstSlot != null)
						item1 = new LevelUpItem(firstSlot);

					LevelUpItem item2 = null;
					if (secondSlot != null)
						item2 = new LevelUpItem(secondSlot);

					LevelUpItem result = new LevelUpItem(resultSlot);

					if (item1 != null && item2 != null) {
						jsonObject.add("item1", item1.createItemJson());
						jsonObject.add("item2", item2.createItemJson());

					} else if (item1 != null && item2 == null) {
						jsonObject.add("item1", item1.createItemJson());

					} else if (item1 == null && item2 != null) {
						jsonObject.add("item1", item2.createItemJson());
					}

					jsonObject.add("result", result.createItemJson());
					jsonArray.add(jsonObject);
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
				JsonObject jsonObject = new JsonObject();

				ItemStack firstSlot = npcInv.get(uuid).get(1).getItem(MenuController.slot(row, col));
				ItemStack secondSlot = npcInv.get(uuid).get(1).getItem(MenuController.slot(row, col + 1));
				ItemStack resultSlot = npcInv.get(uuid).get(1).getItem(MenuController.slot(row, col + 3));

				if ((firstSlot != null || secondSlot != null) && resultSlot != null) {
					LevelUpItem item1 = null;
					if (firstSlot != null)
						item1 = new LevelUpItem(firstSlot);

					LevelUpItem item2 = null;
					item2 = new LevelUpItem(secondSlot);

					LevelUpItem result = new LevelUpItem(resultSlot);

					if (item1 != null && item2 != null) {
						jsonObject.add("item1", item1.createItemJson());
						jsonObject.add("item2", item2.createItemJson());

					} else if (item1 != null && item2 == null) {
						jsonObject.add("item1", item1.createItemJson());

					} else if (item1 == null && item2 != null) {
						jsonObject.add("item1", item2.createItemJson());
					}

					jsonObject.add("result", result.createItemJson());
					jsonArray.add(jsonObject);
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

			NamespacedKey tradeKey = new NamespacedKey(plugin, "levelup_npc_trade");
			if (jsonArray.isEmpty()) {
				if (entity.getPersistentDataContainer().has(tradeKey)) {
					entity.getPersistentDataContainer().remove(tradeKey);
				}
			} else {
				entity.getPersistentDataContainer().set(tradeKey, PersistentDataType.STRING, jsonArray.toString());
			}
		}
	}

	private void openTradeGUI(Player player, UUID uuid) {
		Entity entity = plugin.getServer().getEntity(uuid);
		Merchant merchant = plugin.getServer().createMerchant(entity.getCustomName());
		List<MerchantRecipe> recipes = new ArrayList<MerchantRecipe>();

		NamespacedKey tradeKey = new NamespacedKey(plugin, "levelup_npc_trade");
		if (entity.getPersistentDataContainer().has(tradeKey, PersistentDataType.STRING)) {
			String json = entity.getPersistentDataContainer().get(tradeKey, PersistentDataType.STRING);
			JsonArray jsonArray = JsonParser.parseString(json).getAsJsonArray();

			for (JsonElement e : jsonArray) {
				JsonObject jsonObject = e.getAsJsonObject();
				ItemStack item1 = new LevelUpItem(jsonObject.get("item1").getAsJsonObject()).getItemStack();

				ItemStack item2 = null;
				if (jsonObject.has("item2"))
					item2 = new LevelUpItem(jsonObject.get("item2").getAsJsonObject()).getItemStack();

				ItemStack result = new LevelUpItem(jsonObject.get("result").getAsJsonObject()).getItemStack();

				MerchantRecipe recipe = new MerchantRecipe(result, 10000);
				recipe.addIngredient(item1);
				if (item2 != null)
					recipe.addIngredient(item2);

				recipes.add(recipe);
			}
		}

		merchant.setRecipes(recipes);
		player.openMerchant(merchant, true);
	}

}
