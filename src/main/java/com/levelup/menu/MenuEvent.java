package com.levelup.menu;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import com.levelup.LevelUp;
import com.levelup.money.MoneyController;
import com.levelup.player.PlayerData;
import com.levelup.tool.ToolAbstract;
import com.levelup.tool.ToolController;
import com.levelup.tool.ToolType;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;

import dev.lone.itemsadder.api.CustomStack;
import net.md_5.bungee.api.ChatColor;

public class MenuEvent implements Listener {

	private LevelUp plugin;

	public MenuEvent(LevelUp plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerOpenMenu(PlayerSwapHandItemsEvent event) {
		Player player = event.getPlayer();

		if (player.isSneaking()) {
			event.setCancelled(true);
			Inventory menuInv = MenuController.getMenuInventory(player);
			player.openInventory(menuInv);
		}
	}

	@EventHandler
	public void onPlayerClickMenu(InventoryClickEvent event) throws SQLException {
		ItemStack currItem = event.getCurrentItem();

		if (currItem != null) {
			if (event.getView().getTitle().equals(MenuController.getInventoryTitle(MenuUnicode.MENU.val()))) {
				mainMenuEvent(event);

			} else if (event.getView().getTitle()
					.equals(MenuController.getInventoryTitle(MenuUnicode.WARP_HOME.val()))) {
				warpHomeEvent(event);

			} else if (event.getView().getTitle()
					.equals(MenuController.getInventoryTitle(MenuUnicode.TOOL_HOME.val()))) {
				toolHomeEvent(event);

			} else if (event.getView().getTitle()
					.equals(MenuController.getInventoryTitle(MenuUnicode.TOOL_STAT_1.val()))) {
				toolStatEvent(event, MenuUnicode.TOOL_STAT_1);

			} else if (event.getView().getTitle()
					.equals(MenuController.getInventoryTitle(MenuUnicode.TOOL_STAT_2.val()))) {
				toolStatEvent(event, MenuUnicode.TOOL_STAT_2);

			} else if (event.getView().getTitle()
					.equals(MenuController.getInventoryTitle(MenuUnicode.TOOL_STAT_3.val()))) {
				toolStatEvent(event, MenuUnicode.TOOL_STAT_3);

			} else if (event.getView().getTitle()
					.equals(MenuController.getInventoryTitle(MenuUnicode.BANK_HOME.val()))) {
				bankHomeEvent(event);

			} else if (event.getView().getTitle()
					.equals(MenuController.getInventoryTitle(MenuUnicode.BANK_DEPOSIT.val()))) {
				bankDepositEvent(event);

			} else if (event.getView().getTitle()
					.equals(MenuController.getInventoryTitle(MenuUnicode.BANK_WITHDRAW.val()))) {
				bankWithdrawEvent(event);
			}
		}
	}

	@EventHandler
	public void onPlayerCloseMenu(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		if (event.getView().getTitle().equals(MenuController.getInventoryTitle(MenuUnicode.BANK_DEPOSIT.val()))) {
			Inventory topInv = event.getView().getTopInventory();

			for (ItemStack item : topInv) {
				CustomStack customStack = CustomStack.byItemStack(item);

				if (customStack != null) {
					if (customStack.getNamespacedID().equals(MoneyController.GOLD.getNamespacedID())
							|| customStack.getNamespacedID().equals(MoneyController.SILVER.getNamespacedID())
							|| customStack.getNamespacedID().equals(MoneyController.COPPER.getNamespacedID())) {
						player.getInventory().addItem(item);
					}
				}
			}
		}
	}

	public void mainMenuEvent(InventoryClickEvent event) {
		event.setCancelled(true);

		Player player = (Player) event.getWhoClicked();
		ItemStack currItem = event.getCurrentItem();

		if (currItem.getItemMeta().getDisplayName().contains("도구")) {
			player.openInventory(MenuController.getToolHomeInventory(plugin, player));

		} else if (currItem.getItemMeta().getDisplayName().contains("마을")) {

		} else if (currItem.getItemMeta().getDisplayName().contains("달력")) {

		} else if (currItem.getItemMeta().getDisplayName().contains("친구")) {

		} else if (currItem.getItemMeta().getDisplayName().contains("열기구")) {
			player.openInventory(MenuController.getWarpHomeInventory(plugin, player));

		} else if (currItem.getItemMeta().getDisplayName().contains("마켓")) {

		} else if (currItem.getItemMeta().getDisplayName().contains("은행")) {
			player.openInventory(MenuController.getBankHomeInventory(plugin, player));

		} else if (currItem.getItemMeta().getDisplayName().contains("가이드북")) {

		}
	}

	private void warpHomeEvent(InventoryClickEvent event) {
		event.setCancelled(true);

		Player player = (Player) event.getWhoClicked();
		ItemStack currItem = event.getCurrentItem();

		MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
		MVWorldManager worldManager = core.getMVWorldManager();

		MultiverseWorld fromWorld = worldManager.getMVWorld(player.getWorld());
		MultiverseWorld toWorld = null;

		if (currItem.getItemMeta().getDisplayName().contains("광장 행")) {
			toWorld = worldManager.getMVWorld("spawn");
			player.sendMessage(ChatColor.GREEN + "광장으로 이동합니다");

		} else if (currItem.getItemMeta().getDisplayName().contains("야생 행")) {
			toWorld = worldManager.getMVWorld("wild");
			player.sendMessage(ChatColor.GREEN + "야생월드로 이동합니다");

		} else if (currItem.getItemMeta().getDisplayName().contains("건축 행")) {
			toWorld = worldManager.getMVWorld("world");
			player.sendMessage(ChatColor.GREEN + "건축월드로 이동합니다");

		} else if (currItem.getItemMeta().getDisplayName().contains("지옥 행")) {
			toWorld = worldManager.getMVWorld("world_nether");
			player.sendMessage(ChatColor.GREEN + "지옥월드로 이동합니다");

		}

		if (toWorld != null) {
			NamespacedKey key = new NamespacedKey(plugin, player.getUniqueId().toString());
			Location loc;
			if (toWorld.getAlias().equalsIgnoreCase("world")) {

				if (toWorld.getCBWorld().getPersistentDataContainer().has(key)) {
					int[] coordinate = toWorld.getCBWorld().getPersistentDataContainer().get(key,
							PersistentDataType.INTEGER_ARRAY);
					loc = new Location(toWorld.getCBWorld(), coordinate[0], coordinate[1], coordinate[2]);

				} else {
					loc = toWorld.getSpawnLocation();
				}

			} else {
				loc = toWorld.getSpawnLocation();
			}

			if (fromWorld.getAlias().equalsIgnoreCase("world")) {
				int[] coordinate = { (int) player.getLocation().getX(), (int) player.getLocation().getY(),
						(int) player.getLocation().getZ() };
				fromWorld.getCBWorld().getPersistentDataContainer().set(key, PersistentDataType.INTEGER_ARRAY,
						coordinate);
			}

			player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
			player.teleport(loc);

		}
	}

	public void toolHomeEvent(InventoryClickEvent event) {
		event.setCancelled(true);

		Player player = (Player) event.getWhoClicked();
		ItemStack currItem = event.getCurrentItem();

		if (currItem.getItemMeta().getDisplayName().contains("곡괭이")) {
			player.openInventory(MenuController.getToolStatInventory(plugin, player, ToolType.PICKAXE, 0));

		} else if (currItem.getItemMeta().getDisplayName().contains("도끼")) {
			player.openInventory(MenuController.getToolStatInventory(plugin, player, ToolType.AXE, 0));

		} else if (currItem.getItemMeta().getDisplayName().contains("검")) {
			player.openInventory(MenuController.getToolStatInventory(plugin, player, ToolType.SWORD, 0));

		} else if (currItem.getItemMeta().getDisplayName().contains("삽")) {
			player.openInventory(MenuController.getToolStatInventory(plugin, player, ToolType.SHOVEL, 0));

		} else if (currItem.getItemMeta().getDisplayName().contains("이전으로")) {
			player.openInventory(MenuController.getMenuInventory(player));
		}
	}

	public void toolStatEvent(InventoryClickEvent event, MenuUnicode title) throws SQLException {
		event.setCancelled(true);
		Inventory inv = event.getView().getTopInventory();

		if (event.getClickedInventory().equals(inv)) {
			Player player = (Player) event.getWhoClicked();
			ItemStack currItem = event.getCurrentItem();
			int currSlot = event.getSlot();

			ToolType type = null;
			CustomStack customStack = CustomStack.byItemStack(inv.getItem(MenuController.slot(1, 1)));

			if (customStack != null) {

				if (customStack.getNamespacedID().equals(MenuIcon.PICKAXE.val().getNamespacedID()))
					type = ToolType.PICKAXE;
				else if (customStack.getNamespacedID().equals(MenuIcon.AXE.val().getNamespacedID()))
					type = ToolType.AXE;
				else if (customStack.getNamespacedID().equals(MenuIcon.SWORD.val().getNamespacedID()))
					type = ToolType.SWORD;
				else if (customStack.getNamespacedID().equals(MenuIcon.SHOVEL.val().getNamespacedID()))
					type = ToolType.SHOVEL;

			}

			ToolAbstract toolData = plugin.tools.get(player.getUniqueId()).getTool(type);
			int totalStat = MenuController.getNumber(CustomStack.byItemStack(inv.getItem(MenuController.slot(3, 1))))
					* 10 + MenuController.getNumber(CustomStack.byItemStack(inv.getItem(MenuController.slot(3, 2))));

			if (currItem.getItemMeta().getDisplayName().contains("-")) {
				int number = MenuController.getNumber(CustomStack.byItemStack(inv.getItem(currSlot + 1)));
				if (number > 0) {
					inv.setItem(currSlot + 1, MenuController.getNumberItem(number - 1).getItemStack());

					totalStat++;
					int totalStatFirst = totalStat / 10;
					int totalStatSecond = totalStat % 10;

					inv.setItem(MenuController.slot(3, 1), MenuController.getNumberItem(totalStatFirst).getItemStack());
					inv.setItem(MenuController.slot(3, 2),
							MenuController.getNumberItem(totalStatSecond).getItemStack());
					player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 0.8F);
				}

			} else if (currItem.getItemMeta().getDisplayName().contains("+")) {
				int number = MenuController.getNumber(CustomStack.byItemStack(inv.getItem(currSlot - 1)));

				Enchantment enchant = MenuController.getEnchant(inv.getItem(currSlot - 3));

				int limit = toolData.getEnchantLimit(enchant);

				if (totalStat > 0 && number < limit) {

					if (enchant == Enchantment.LOOT_BONUS_BLOCKS) {
						int slickTouch = MenuController
								.getNumber(CustomStack.byItemStack(inv.getItem(MenuController.slot(3, 7))));

						if (slickTouch > 0)
							return;

					} else if (enchant == Enchantment.SILK_TOUCH) {
						int fortune = MenuController
								.getNumber(CustomStack.byItemStack(inv.getItem(MenuController.slot(2, 7))));

						if (fortune > 0)
							return;

					} else if (enchant == Enchantment.DAMAGE_ALL) {
						int smite = MenuController
								.getNumber(CustomStack.byItemStack(inv.getItem(MenuController.slot(2, 7))));
						int arthropods = MenuController
								.getNumber(CustomStack.byItemStack(inv.getItem(MenuController.slot(3, 7))));

						if (smite > 0 || arthropods > 0)
							return;

					} else if (enchant == Enchantment.DAMAGE_UNDEAD) {
						int sharpness = MenuController
								.getNumber(CustomStack.byItemStack(inv.getItem(MenuController.slot(1, 7))));
						int arthropods = MenuController
								.getNumber(CustomStack.byItemStack(inv.getItem(MenuController.slot(3, 7))));

						if (sharpness > 0 || arthropods > 0)
							return;

					} else if (enchant == Enchantment.DAMAGE_ARTHROPODS) {
						int sharpness = MenuController
								.getNumber(CustomStack.byItemStack(inv.getItem(MenuController.slot(1, 7))));
						int smite = MenuController
								.getNumber(CustomStack.byItemStack(inv.getItem(MenuController.slot(2, 7))));

						if (sharpness > 0 || smite > 0)
							return;

					}

					inv.setItem(currSlot - 1, MenuController.getNumberItem(number + 1).getItemStack());

					totalStat--;
					int totalStatFirst = totalStat / 10;
					int totalStatSecond = totalStat % 10;

					inv.setItem(MenuController.slot(3, 1), MenuController.getNumberItem(totalStatFirst).getItemStack());
					inv.setItem(MenuController.slot(3, 2),
							MenuController.getNumberItem(totalStatSecond).getItemStack());
					player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 0.9F);

				}

			} else if (currItem.getItemMeta().getDisplayName().contains("적용하기")) {
				Map<Enchantment, Integer> enchantIcons = new HashMap<Enchantment, Integer>();
				enchantIcons.put(MenuController.getEnchant(inv.getItem(MenuController.slot(1, 5))),
						MenuController.getNumber(CustomStack.byItemStack(inv.getItem(MenuController.slot(1, 7)))));
				enchantIcons.put(MenuController.getEnchant(inv.getItem(MenuController.slot(2, 5))),
						MenuController.getNumber(CustomStack.byItemStack(inv.getItem(MenuController.slot(2, 7)))));
				enchantIcons.put(MenuController.getEnchant(inv.getItem(MenuController.slot(3, 5))),
						MenuController.getNumber(CustomStack.byItemStack(inv.getItem(MenuController.slot(3, 7)))));

				if (inv.getItem(MenuController.slot(4, 5)) != null)
					enchantIcons.put(MenuController.getEnchant(inv.getItem(MenuController.slot(4, 5))),
							MenuController.getNumber(CustomStack.byItemStack(inv.getItem(MenuController.slot(4, 7)))));

				Map<Enchantment, Integer> enchantment = new HashMap<Enchantment, Integer>(toolData.getEnchantment());

				for (Entry<Enchantment, Integer> enchantInv : enchantIcons.entrySet()) {
					if (enchantInv.getValue() == 0) {
						if (enchantment.containsKey(enchantInv.getKey())) {
							enchantment.remove(enchantInv.getKey());
						}

					} else {
						enchantment.put(enchantInv.getKey(), enchantInv.getValue());
					}

				}

				ToolController.toolEnchantment(plugin, player, type, enchantment);
				player.playSound(player, Sound.BLOCK_ANVIL_USE, 1.0F, 1.0F);

			} else if (currItem.getItemMeta().getDisplayName().contains("이전으로")) {
				if (type == ToolType.PICKAXE || type == ToolType.SHOVEL) {
					player.openInventory(MenuController.getToolHomeInventory(plugin, player));

				} else if (type == ToolType.AXE) {
					if (title == MenuUnicode.TOOL_STAT_1) {
						player.openInventory(MenuController.getToolHomeInventory(plugin, player));
					} else if (title == MenuUnicode.TOOL_STAT_2) {
						player.openInventory(MenuController.getToolStatInventory(plugin, player, type, 0));
					}
				} else if (type == ToolType.SWORD) {
					if (title == MenuUnicode.TOOL_STAT_2) {
						player.openInventory(MenuController.getToolHomeInventory(plugin, player));
					} else if (title == MenuUnicode.TOOL_STAT_3) {
						player.openInventory(MenuController.getToolStatInventory(plugin, player, type, 0));
					}
				}

			} else if (currItem.getItemMeta().getDisplayName().contains("다음으로")) {
				player.openInventory(MenuController.getToolStatInventory(plugin, player, type, 1));
			}
		}

	}

	public void bankHomeEvent(InventoryClickEvent event) {
		event.setCancelled(true);

		Player player = (Player) event.getWhoClicked();
		ItemStack currItem = event.getCurrentItem();

		if (currItem.getItemMeta().getDisplayName().contains("입금")) {
			player.openInventory(MenuController.getBankDepositInventory(player));

		} else if (currItem.getItemMeta().getDisplayName().contains("출금")) {
			player.openInventory(MenuController.getBankWithdrawInventory(player));

		} else if (currItem.getItemMeta().getDisplayName().contains("세금")) {
			player.openInventory(MenuController.getBankTaxInventory(player));

		}
	}

	public void bankDepositEvent(InventoryClickEvent event) throws SQLException {
		event.setCancelled(true);

		Player player = (Player) event.getWhoClicked();
		ItemStack currItem = event.getCurrentItem();
		Inventory topInv = event.getView().getTopInventory();
		CustomStack customStack = CustomStack.byItemStack(currItem);

		if (event.getClickedInventory().equals(player.getInventory())) {

			if (customStack != null && (customStack.getNamespacedID().equals(MoneyController.GOLD.getNamespacedID())
					|| customStack.getNamespacedID().equals(MoneyController.SILVER.getNamespacedID())
					|| customStack.getNamespacedID().equals(MoneyController.COPPER.getNamespacedID()))) {

				for (int i = 0; i < 5; i++) {
					ItemStack item = topInv.getItem(MenuController.slot(1, 2 + i));

					if (item == null || item.getType() == Material.AIR) {
						topInv.setItem(MenuController.slot(1, 2 + i), currItem);
						player.getInventory().setItem(event.getSlot(), null);
						MoneyController.updateDepositLore(topInv);
						break;
					}
				}
			}

		} else {
			if (customStack != null && (customStack.getNamespacedID().equals(MoneyController.GOLD.getNamespacedID())
					|| customStack.getNamespacedID().equals(MoneyController.SILVER.getNamespacedID())
					|| customStack.getNamespacedID().equals(MoneyController.COPPER.getNamespacedID()))) {

				player.getInventory().addItem(currItem);
				topInv.setItem(event.getSlot(), null);
				MoneyController.updateDepositLore(topInv);

			} else if (currItem.getItemMeta().getDisplayName().contains("입금하기")) {

				int amount = 0;
				for (int i = 0; i < 5; i++) {
					ItemStack item = topInv.getItem(MenuController.slot(1, 2 + i));
					CustomStack cs = CustomStack.byItemStack(item);

					if (cs != null) {
						if (cs.getNamespacedID().equals(MoneyController.GOLD.getNamespacedID())) {
							amount += item.getAmount() * 100;

						} else if (cs.getNamespacedID().equals(MoneyController.SILVER.getNamespacedID())) {
							amount += item.getAmount() * 10;

						} else if (cs.getNamespacedID().equals(MoneyController.COPPER.getNamespacedID())) {
							amount += item.getAmount();

						}
					}

					topInv.setItem(MenuController.slot(1, 2 + i), null);
				}

				if (amount > 0) {
					MoneyController.depoistMoeny(plugin, amount, player.getUniqueId());
					player.closeInventory();
					player.sendMessage(
							ChatColor.GOLD + "총 " + MoneyController.withLargeIntegers(amount) + " 코인을 입금했습니다.");
					player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
				}
			}
		}
	}

	public void bankWithdrawEvent(InventoryClickEvent event) {
		event.setCancelled(true);

		Player player = (Player) event.getWhoClicked();
		ItemStack currItem = event.getCurrentItem();
		Inventory topInv = event.getView().getTopInventory();
		PlayerData pd = plugin.players.get(player.getUniqueId());

		try {
			int number = Integer.parseInt(ChatColor.stripColor(currItem.getItemMeta().getDisplayName()));
			if (number >= 0 && number < 10) {
				MoneyController.withdrawInput(topInv, number);
				MoneyController.updateWithdrawLore(topInv, pd);
			}

		} catch (NumberFormatException e) {
			if (currItem.getItemMeta().getDisplayName().contains("지우기")) {
				MoneyController.withdrawInput(topInv, -1);
				MoneyController.updateWithdrawLore(topInv, pd);
			}

			if (currItem.getItemMeta().getDisplayName().contains("출금하기")) {
				int amount = MoneyController.getWithdrawAmount(topInv);
				if (amount <= pd.getBalance() && amount > 0) {
					player.performCommand("출금 " + amount);
					player.closeInventory();
				}
			}
		}

	}

}
