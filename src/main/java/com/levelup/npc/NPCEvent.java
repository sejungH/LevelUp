package com.levelup.npc;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
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
import com.levelup.npc.NPCController.NPCMythic;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.CitizensEnableEvent;
import net.citizensnpcs.api.npc.NPC;

public class NPCEvent implements Listener {

	private LevelUp plugin;
	private Map<UUID, List<Inventory>> npcInv;
	private final Set<UUID> interactedPlayers = new HashSet<>();

	public NPCEvent(LevelUp plugin) {
		this.plugin = plugin;
		this.npcInv = new HashMap<UUID, List<Inventory>>();
	}

	@EventHandler
	public void onEntityLoad(EntitiesLoadEvent event) {
		NamespacedKey npcKey = new NamespacedKey(plugin, "levelup_npc");
		for (Entity entity : event.getEntities()) {
			if (entity instanceof LivingEntity livingEntity) {
				if (entity.getPersistentDataContainer().has(npcKey)) {
					NamespacedKey equipmentKey = new NamespacedKey(plugin, "levelup_npc_equipment");
					if (entity.getPersistentDataContainer().has(equipmentKey, PersistentDataType.STRING)) {
						String equipment = entity.getPersistentDataContainer().get(equipmentKey,
								PersistentDataType.STRING);
						JsonObject jsonObject = JsonParser.parseString(equipment).getAsJsonObject();

						for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
							EquipmentSlot slot = EquipmentSlot.valueOf(entry.getKey());
							LevelUpItem lvItem = new LevelUpItem(entry.getValue().getAsJsonObject());
							livingEntity.getEquipment().setItem(slot, lvItem.getItemStack());
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onNPCLoad(CitizensEnableEvent event) {
		Iterator<NPC> npcs = CitizensAPI.getNPCRegistry().iterator();
		while (npcs.hasNext()) {
			NPC npc = npcs.next();
			if (npc != null) {
				LivingEntity entity = (LivingEntity) npc.getEntity();
				if (entity != null && npc.data().has("levelup_npc_equipment")) {
					String equipment = npc.data().get("levelup_npc_equipment");
					JsonObject jsonObject = JsonParser.parseString(equipment).getAsJsonObject();

					for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
						EquipmentSlot slot = EquipmentSlot.valueOf(entry.getKey());
						LevelUpItem lvItem = new LevelUpItem(entry.getValue().getAsJsonObject());
						entity.getEquipment().setItem(slot, lvItem.getItemStack());
					}
				}
			}
		}
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
				if (entity.getPersistentDataContainer().has(typeKey)) {
					String type = entity.getPersistentDataContainer().get(typeKey, PersistentDataType.STRING);

					if (NPCMythic.contains(type)) {
						NPCMythic npcType = NPCMythic.valueOf(type.toUpperCase());
						if (npcType == NPCMythic.BLACKSMITH) {
							event.setCancelled(true);
							NPCController.showDefaultMessage(plugin, player);
							interactedPlayers.add(player.getUniqueId());
							return;

						} else if (npcType == NPCMythic.FISHING_SHOP || npcType == NPCMythic.FARMER_SHOP) {
							event.setCancelled(true);
							NPCController.showShopInventory(plugin, player, npcType);
							return;
						}
					}
				}

				NamespacedKey tradeKey = new NamespacedKey(plugin, "levelup_npc_trade");
				if (entity.getPersistentDataContainer().has(tradeKey)
						|| (npc != null && npc.data().has("levelup_npc_trade"))) {
					event.setCancelled(true);
					openTradeGUI(player, entity.getUniqueId());
				}
			}
		}
	}

	@EventHandler
	public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();

		if (interactedPlayers.contains(player.getUniqueId())) {
			event.setCancelled(true);
			interactedPlayers.remove(player.getUniqueId());
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
		} else if (event.getView().getTitle().equals(MenuController.getInventoryTitle(MenuUnicode.NPC_SHOP.val()))) {
			if (inv != null) {
				clickShopInventory(event);
			}
		}
	}

	@EventHandler
	public void onInventoryDrag(InventoryDragEvent event) {
		if (event.getView().getTitle().equals(MenuController.getInventoryTitle(MenuUnicode.NPC_1.val()))
				|| event.getView().getTitle().equals(MenuController.getInventoryTitle(MenuUnicode.NPC_2.val()))
				|| event.getView().getTitle().equals(MenuController.getInventoryTitle(MenuUnicode.NPC_3.val()))
				|| event.getView().getTitle().equals(MenuController.getInventoryTitle(MenuUnicode.NPC_SHOP.val()))) {
			event.setCancelled(true);
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
				if (!NPCController.INV_SLOT.containsValue(slot)) {
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

	public void clickThirdNPCInventory(InventoryClickEvent event) {
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

	public void clickShopInventory(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		Inventory inv = event.getClickedInventory();
		Inventory topInv = event.getView().getTopInventory();
		Inventory playerInv = player.getInventory();

		NPCMythic type = NPCMythic.valueOf(topInv.getItem(MenuController.slot(3, 4)).getItemMeta()
				.getPersistentDataContainer().get(new NamespacedKey(plugin, "type"), PersistentDataType.STRING));

		if (plugin.npcShopItems.containsKey(type)) {
			if (inv.equals(topInv)) {
				if (event.getAction() == InventoryAction.PLACE_ALL || event.getAction() == InventoryAction.PLACE_ONE
						|| event.getAction() == InventoryAction.PLACE_SOME) {
					LevelUpItem lvItem = new LevelUpItem(event.getCursor());

					if (!plugin.npcShopItems.get(type).containsKey(lvItem))
						event.setCancelled(true);
				}

				if (event.getSlot() > MenuController.slot(3, 0) && event.getSlot() < MenuController.slot(3, 8)) {
					event.setCancelled(true);

					ItemStack btn = event.getCurrentItem();
					if (btn != null && btn.getItemMeta().getDisplayName().contains("판매하기"))
						NPCController.sellItems(plugin, player, type, inv);
				}

			} else if (inv.equals(playerInv)) {
				ItemStack item = event.getCurrentItem();
				if (item != null && event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
					LevelUpItem lvItem = new LevelUpItem(item);

					if (plugin.npcShopItems.get(type).containsKey(lvItem)) {
						Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
							@Override
							public void run() {
								int[] cols = { 0, 1, 2, 6, 7, 8 };
								for (int col : cols) {
									int slot = MenuController.slot(3, col);
									ItemStack added = topInv.getItem(slot);
									if (added != null) {
										playerInv.addItem(added);
										topInv.setItem(slot, null);
									}
								}
							}
						});

					} else {
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) throws SQLException {
		Inventory inv = event.getInventory();

		if (event.getView().getTitle().equals(MenuController.getInventoryTitle(MenuUnicode.NPC_1.val()))) {
			ItemStack npcID = inv.getItem(0);
			UUID uuid = UUID.fromString(npcID.getItemMeta().getDisplayName());

			LivingEntity entity = (LivingEntity) plugin.getServer().getEntity(uuid);
			if (entity == null)
				return;

			JsonObject jsonObject = new JsonObject();

			for (Entry<EquipmentSlot, Integer> entry : NPCController.INV_SLOT.entrySet()) {
				ItemStack item = inv.getItem(entry.getValue());
				if (item != null) {
					entity.getEquipment().setItem(entry.getKey(), item);
					LevelUpItem lvItem = new LevelUpItem(item);
					jsonObject.add(entry.getKey().toString(), lvItem.createItemJson());

				} else {
					entity.getEquipment().setItem(entry.getKey(), null);
				}
			}

			NPC npc = CitizensAPI.getNPCRegistry().getNPC(entity);
			if (npc == null) {
				NamespacedKey equipmentKey = new NamespacedKey(plugin, "levelup_npc_equipment");
				if (jsonObject.isEmpty()) {
					if (entity.getPersistentDataContainer().has(equipmentKey)) {
						entity.getPersistentDataContainer().remove(equipmentKey);
					}
				} else {
					entity.getPersistentDataContainer().set(equipmentKey, PersistentDataType.STRING,
							jsonObject.toString());
				}

			} else {
				if (jsonObject.isEmpty()) {
					if (npc.data().has("levelup_npc_equipment")) {
						npc.data().remove("levelup_npc_equipment");
					}
				} else {
					npc.data().setPersistent("levelup_npc_equipment", jsonObject.toString());
				}
			}

			ItemStack nameTag = inv.getItem(49);
			if (nameTag != null) {
				String name = nameTag.getItemMeta().getDisplayName();

				entity.setCustomName(name);
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

		} else if (event.getView().getTitle().equals(MenuController.getInventoryTitle(MenuUnicode.NPC_SHOP.val()))) {
			for (int i = 0; i < 27; i++) {
				ItemStack item = inv.getItem(i);
				if (item != null) {
					event.getPlayer().getInventory().addItem(item);
				}
			}
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

			NPC npc = CitizensAPI.getNPCRegistry().getNPC(entity);

			if (npc == null) {
				NamespacedKey tradeKey = new NamespacedKey(plugin, "levelup_npc_trade");
				if (jsonArray.isEmpty()) {
					if (entity.getPersistentDataContainer().has(tradeKey)) {
						entity.getPersistentDataContainer().remove(tradeKey);
					}
				} else {
					entity.getPersistentDataContainer().set(tradeKey, PersistentDataType.STRING, jsonArray.toString());
				}
			} else {
				if (jsonArray.isEmpty()) {
					if (npc.data().has("levelup_npc_trade")) {
						npc.data().remove("levelup_npc_trade");
					}
				} else {
					npc.data().setPersistent("levelup_npc_trade", jsonArray.toString());
				}
			}
		}
	}

	private void openTradeGUI(Player player, UUID uuid) {
		Entity entity = plugin.getServer().getEntity(uuid);
		NPC npc = CitizensAPI.getNPCRegistry().getByUniqueId(uuid);
		Merchant merchant = plugin.getServer().createMerchant(entity.getCustomName());
		List<MerchantRecipe> recipes = new ArrayList<MerchantRecipe>();

		String json = null;
		NamespacedKey tradeKey = new NamespacedKey(plugin, "levelup_npc_trade");
		if (entity.getPersistentDataContainer().has(tradeKey, PersistentDataType.STRING))
			json = entity.getPersistentDataContainer().get(tradeKey, PersistentDataType.STRING);

		else if (npc != null && npc.data().has("levelup_npc_trade"))
			json = npc.data().get("levelup_npc_trade");

		if (json != null) {
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

			merchant.setRecipes(recipes);
			player.openMerchant(merchant, true);
		}
	}

}
