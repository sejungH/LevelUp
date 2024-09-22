package com.levelup.cooking;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.levelup.LevelUp;
import com.levelup.chunk.ChunkController;
import com.levelup.menu.MenuController;
import com.levelup.menu.MenuUnicode;
import com.levelup.tool.ToolController;
import com.levelup.tool.ToolData;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;

import dev.lone.itemsadder.api.CustomStack;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.core.mobs.ActiveMob;

public class CookingEvent implements Listener {

	private LevelUp plugin;

	public CookingEvent(LevelUp plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onInteractBlock(PlayerInteractEvent event) {
		if (event.getHand() == EquipmentSlot.OFF_HAND)
			return;

		Player player = event.getPlayer();

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getBlockFace() == BlockFace.UP) {
			MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
			MVWorldManager worldManager = core.getMVWorldManager();
			MultiverseWorld world = worldManager.getMVWorld(player.getWorld());

			if (world.getAlias().equalsIgnoreCase("world")) {
				ItemStack item = player.getInventory().getItemInMainHand();
				CustomStack customItem = CustomStack.byItemStack(item);

				if (customItem != null) {
					MythicMob mythicMob = null;

					if (customItem.getNamespacedID().equals(CookingController.POT)) {
						mythicMob = MythicBukkit.inst().getMobManager().getMythicMob(CookingController.POT_MYTHIC)
								.orElse(null);

					} else if (customItem.getNamespacedID().equals(CookingController.CHOPPING_BOARD)) {
						mythicMob = MythicBukkit.inst().getMobManager()
								.getMythicMob(CookingController.CHOPPING_BOARD_MYTHIC).orElse(null);
					}

					if (mythicMob != null
							&& ChunkController.canInteract(plugin, player, event.getClickedBlock().getChunk())) {

						Location loc = event.getClickedBlock().getLocation().add(0.5, 1, 0.5);

						if (player.getWorld().getNearbyEntities(loc, 0.5, 1, 0.5).isEmpty()) {
							ActiveMob mob = mythicMob.spawn(BukkitAdapter.adapt(loc), 1);

							Location playerLoc = player.getLocation();
							float playerYaw = playerLoc.getYaw();

							float mobYaw = 0; // North
							if (playerYaw >= -45 && playerYaw < 45)
								mobYaw = 180; // South
							else if (playerYaw >= 45 && playerYaw < 135)
								mobYaw = -90; // West
							else if (playerYaw >= -135 && playerYaw < -45)
								mobYaw = 90; // East

							Location mobLoc = mob.getEntity().getBukkitEntity().getLocation();
							mobLoc.setYaw(mobYaw);
							mobLoc.setPitch(0);

							mob.getEntity().getBukkitEntity().teleport(mobLoc);
							player.playSound(mobLoc, Sound.BLOCK_STONE_PLACE, 1.0F, 1.0F);

							item.setAmount(item.getAmount() - 1);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onInteractEntity(PlayerInteractEntityEvent event) {
		if (event.isCancelled())
			return;

		Player player = (Player) event.getPlayer();
		Entity entity = event.getRightClicked();

		if (MythicBukkit.inst().getMobManager().isMythicMob(entity)) {
			ActiveMob mob = MythicBukkit.inst().getMobManager().getMythicMobInstance(entity);

			if ((mob.getType().getInternalName().equalsIgnoreCase(CookingController.POT_MYTHIC)
					|| mob.getType().getInternalName().equalsIgnoreCase(CookingController.CHOPPING_BOARD_MYTHIC)
							&& mob.getStance().equals("default"))) {
				event.setCancelled(true);

				if (mob.getType().getInternalName().equalsIgnoreCase(CookingController.POT_MYTHIC)) {
					player.openInventory(CookingController.getPotInventory(plugin, player, entity));

				} else if (mob.getType().getInternalName().equalsIgnoreCase(CookingController.CHOPPING_BOARD_MYTHIC)) {
					CookingController.chopIngredient(plugin, player, mob);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerClickPotInv(InventoryClickEvent event) {
		if (event.getView().getTitle().equals(MenuController.getInventoryTitle(MenuUnicode.COOKING_POT.val()))) {
			Player player = (Player) event.getWhoClicked();
			Inventory potInv = event.getView().getTopInventory();
			Inventory playerInv = player.getInventory();
			ItemStack item = event.getCurrentItem();

			if (event.getClickedInventory() != null) {

				if (event.getClickedInventory().equals(potInv)) {

					if (CookingController.POT_INGREDIENT.contains(event.getSlot()))
						return;

					if (CookingController.POT_FUEL == event.getSlot()
							&& (event.getCursor() == null || event.getCursor().getType() == Material.AIR
									|| CookingController.FUELS.contains(event.getCursor().getType())))
						return;

					event.setCancelled(true);

					if (item != null && item.getItemMeta().getDisplayName().contains("요리하기")) {
						CookingController.cookRecipe(plugin, player, potInv);
					}

				} else if (event.getClickedInventory().equals(playerInv)) {

					if (event.getSlot() == ToolController.TOOLBOX_SLOT) {
						event.isCancelled();
						return;
					}

					if (item != null && (event.getClick().equals(ClickType.SHIFT_LEFT)
							|| event.getClick().equals(ClickType.SHIFT_RIGHT))) {
						event.setCancelled(true);

						ToolData toolData = plugin.tools.get(player.getUniqueId());
						if (toolData.getPickaxe().equals(item) || toolData.getAxe().equals(item)
								|| toolData.getSword().equals(item) || toolData.getSword().equals(item))
							return;

						if (CookingController.FUELS.contains(item.getType())) {
							ItemStack potFuel = potInv.getItem(CookingController.POT_FUEL);

							if (potFuel == null) {
								potInv.setItem(CookingController.POT_FUEL, item);
								item.setAmount(0);

							} else if (potFuel.getType().equals(item.getType())
									&& potFuel.getItemMeta().equals(item.getItemMeta()) && potFuel.getAmount() < 64) {

								if (item.getAmount() + potFuel.getAmount() > 64) {
									item.setAmount(64 - potFuel.getAmount());
									potFuel.setAmount(64);
									return;

								} else {
									potFuel.setAmount(potFuel.getAmount() + item.getAmount());
									item.setAmount(0);
								}
							}
						}

						for (Integer s : CookingController.POT_INGREDIENT) {
							ItemStack potItem = potInv.getItem(s);

							if (potItem != null && potItem.getType().equals(item.getType())
									&& potItem.getItemMeta().equals(item.getItemMeta()) && potItem.getAmount() < 64) {

								if (item.getAmount() + potItem.getAmount() > 64) {
									item.setAmount(64 - potItem.getAmount());
									potItem.setAmount(64);

								} else {
									potItem.setAmount(potItem.getAmount() + item.getAmount());
									item.setAmount(0);
									break;
								}
							}
						}

						if (item.getAmount() > 0) {
							for (Integer s : CookingController.POT_INGREDIENT) {
								ItemStack potItem = potInv.getItem(s);

								if (potItem == null) {
									potInv.setItem(s, item);
									item.setAmount(0);
									break;
								}

							}
						}

					}

				}
			}

		}
	}

	@EventHandler
	public void onPlayerDragPotInv(InventoryDragEvent event) {
		if (event.getView().getTitle().equals(MenuController.getInventoryTitle(MenuUnicode.COOKING_POT.val()))) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerClosePotInv(InventoryCloseEvent event) {

		if (event.getView().getTitle().equals(MenuController.getInventoryTitle(MenuUnicode.COOKING_POT.val()))) {

			Inventory potInv = event.getView().getTopInventory();

			ItemStack potID = potInv.getItem(0);
			NamespacedKey uuidKey = new NamespacedKey(plugin, "uuid");
			UUID uuid = UUID.fromString(
					potID.getItemMeta().getPersistentDataContainer().get(uuidKey, PersistentDataType.STRING));

			Entity entity = plugin.getServer().getEntity(uuid);

			JsonArray jsonArray = new JsonArray();

			for (Integer s : CookingController.POT_INGREDIENT) {
				ItemStack item = potInv.getItem(s);
				if (item != null) {
					JsonObject jsonObject = new JsonObject();
					jsonObject.addProperty("slot", s);

					if (CustomStack.byItemStack(item) != null) {
						jsonObject.addProperty("namespacedID", CustomStack.byItemStack(item).getNamespacedID());
					} else {
						jsonObject.addProperty("material", item.getType().toString());
					}
					jsonObject.addProperty("amount", item.getAmount());
					jsonArray.add(jsonObject);
				}
			}

			ItemStack fuel = potInv.getItem(CookingController.POT_FUEL);
			if (fuel != null) {
				JsonObject fuelObject = new JsonObject();
				fuelObject.addProperty("slot", CookingController.POT_FUEL);
				if (CustomStack.byItemStack(fuel) != null) {
					fuelObject.addProperty("namespacedID", CustomStack.byItemStack(fuel).getNamespacedID());
				} else {
					fuelObject.addProperty("material", fuel.getType().toString());
				}
				fuelObject.addProperty("amount", fuel.getAmount());
				jsonArray.add(fuelObject);
			}

			NamespacedKey invKey = new NamespacedKey(plugin, "inventory");
			entity.getPersistentDataContainer().set(invKey, PersistentDataType.STRING, jsonArray.toString());
		}
	}

	@EventHandler
	public void onPlayerBreakPot(MythicMobDeathEvent event) {
		if (event.getMobType().getInternalName().equalsIgnoreCase("pot")) {
			Entity entity = event.getEntity();

			NamespacedKey invKey = new NamespacedKey(plugin, "inventory");
			if (entity.getPersistentDataContainer().has(invKey, PersistentDataType.STRING)) {
				String json = entity.getPersistentDataContainer().get(invKey, PersistentDataType.STRING);
				JsonArray jsonArray = JsonParser.parseString(json).getAsJsonArray();
				List<ItemStack> drops = event.getDrops();

				for (JsonElement e : jsonArray) {
					JsonObject jsonObject = e.getAsJsonObject();
					ItemStack item;

					if (jsonObject.has("namespacedID"))
						item = CustomStack.getInstance(jsonObject.get("namespacedID").getAsString()).getItemStack();
					else
						item = new ItemStack(Material.getMaterial(jsonObject.get("material").getAsString()));

					item.setAmount(jsonObject.get("amount").getAsInt());
					drops.add(item);
				}

				event.setDrops(drops);
			}
		}
	}

	@EventHandler
	public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();
		if (player.getOpenInventory() != null) {
			event.setCancelled(true);
		}
	}

}
