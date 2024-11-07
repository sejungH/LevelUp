package com.levelup.menu;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.levelup.LevelUp;
import com.levelup.LevelUpIcon;
import com.levelup.LevelUpItem;
import com.levelup.chunk.ChunkController;
import com.levelup.friend.FriendController;
import com.levelup.money.MoneyController;
import com.levelup.money.MoneyController.MoneyItem;
import com.levelup.player.PlayerData;
import com.levelup.seasonpass.SeasonPassController;
import com.levelup.seasonpass.SeasonPassController.SeasonPass;
import com.levelup.tool.ToolAbstract;
import com.levelup.tool.ToolController;
import com.levelup.tool.ToolType;
import com.levelup.village.VillageController;
import com.levelup.village.VillageData;
import com.levelup.warp.WarpController;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;

import dev.lone.itemsadder.api.CustomStack;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

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

		// Main Menu
		if (event.getView().getTitle().equals(MenuController.getInventoryTitle(MenuUnicode.MENU.val()))) {
			mainMenuEvent(event);
		}

		// Tool Event
		else if (event.getView().getTitle().equals(MenuController.getInventoryTitle(MenuUnicode.TOOL_HOME.val()))) {
			toolHomeEvent(event);

		} else if (event.getView().getTitle().equals(MenuController.getInventoryTitle(MenuUnicode.TOOL_STAT_1.val()))) {
			toolStatEvent(event, MenuUnicode.TOOL_STAT_1);

		} else if (event.getView().getTitle().equals(MenuController.getInventoryTitle(MenuUnicode.TOOL_STAT_2.val()))) {
			toolStatEvent(event, MenuUnicode.TOOL_STAT_2);

		} else if (event.getView().getTitle().equals(MenuController.getInventoryTitle(MenuUnicode.TOOL_STAT_3.val()))) {
			toolStatEvent(event, MenuUnicode.TOOL_STAT_3);
		}

		// Bank Event
		else if (event.getView().getTitle().equals(MenuController.getInventoryTitle(MenuUnicode.BANK_HOME.val()))) {
			bankHomeEvent(event);

		} else if (event.getView().getTitle()
				.equals(MenuController.getInventoryTitle(MenuUnicode.BANK_ACCOUNT.val()))) {
			bankAccountEvent(event);

		} else if (event.getView().getTitle()
				.equals(MenuController.getInventoryTitle(MenuUnicode.BANK_DEPOSIT.val()))) {
			bankDepositEvent(event);

		} else if (event.getView().getTitle()
				.equals(MenuController.getInventoryTitle(MenuUnicode.BANK_WITHDRAW.val()))) {
			bankWithdrawEvent(event);

		} else if (event.getView().getTitle().equals(MenuController.getInventoryTitle(MenuUnicode.BANK_TAX.val()))) {
			bankTaxEvent(event);
		}

		// Village Event
		else if (event.getView().getTitle().equals(MenuController.getInventoryTitle(MenuUnicode.VILLAGE_HOME.val()))) {
			villageHomeEvent(event);

		} else if (event.getView().getTitle()
				.equals(MenuController.getInventoryTitle(MenuUnicode.VILLAGE_MANAGE.val()))) {
			villageManageEvent(event);

		} else if (event.getView().getTitle()
				.equals(MenuController.getInventoryTitle(MenuUnicode.VILLAGE_APPLY.val()))) {
			villageApplyEvent(event);
		}

		// Calendar Event
		else if (event.getView().getTitle().equals(MenuController.getInventoryTitle(MenuUnicode.CALENDAR_HOME.val()))) {
			calendarHomeEvent(event);

		} else if (event.getView().getTitle()
				.equals(MenuController.getInventoryTitle(MenuUnicode.CALENDAR_DAILY_QUEST.val()))) {
			calendarQuestEvent(event);

		} else if (event.getView().getTitle()
				.equals(MenuController.getInventoryTitle(MenuUnicode.CALENDAR_SEASONPASS_1.val()))) {
			calendarSeasonPassEvent(event, 1);

		} else if (event.getView().getTitle()
				.equals(MenuController.getInventoryTitle(MenuUnicode.CALENDAR_SEASONPASS_2.val()))) {
			calendarSeasonPassEvent(event, 2);

		} else if (event.getView().getTitle()
				.equals(MenuController.getInventoryTitle(MenuUnicode.CALENDAR_SEASONPASS_3.val()))) {
			calendarSeasonPassEvent(event, 3);

		} else if (event.getView().getTitle()
				.equals(MenuController.getInventoryTitle(MenuUnicode.CALENDAR_SEASONPASS_4.val()))) {
			calendarSeasonPassEvent(event, 4);
		}

		// Friend Event
		else if (event.getView().getTitle().equals(MenuController.getInventoryTitle(MenuUnicode.FRIEND_HOME.val()))) {
			friendHomeEvent(event);

		} else if (event.getView().getTitle()
				.equals(MenuController.getInventoryTitle(MenuUnicode.FRIEND_REQUEST.val()))) {
			friendRequestEvent(event);

		} else if (event.getView().getTitle()
				.equals(MenuController.getInventoryTitle(MenuUnicode.FRIEND_BLOCK.val()))) {
			friendBlockEvent(event);

		} else if (event.getView().getTitle().equals(MenuController.getInventoryTitle(MenuUnicode.FRIEND_INFO.val()))) {
			friendInfoEvent(event);
		}

		// Warp Event
		else if (event.getView().getTitle().equals(MenuController.getInventoryTitle(MenuUnicode.WARP_HOME.val()))) {
			warpHomeEvent(event);
		}

		// Friend Event
		else if (event.getView().getTitle().equals(MenuController.getInventoryTitle(MenuUnicode.SHOPPING_HOME.val()))) {
			shoppingHomeEvent(event);
		}

		// Guide Event
		else if (event.getView().getTitle().equals(MenuController.getInventoryTitle(MenuUnicode.GUIDE_HOME.val()))) {
			guideHomeEvent(event);
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
					if (customStack.getNamespacedID().equals(MoneyItem.GOLD.getNamespacedID())
							|| customStack.getNamespacedID().equals(MoneyItem.SILVER.getNamespacedID())
							|| customStack.getNamespacedID().equals(MoneyItem.COPPER.getNamespacedID())) {
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
		if (currItem != null) {
			if (currItem.getItemMeta().getDisplayName().contains("도구")) {
				player.openInventory(MenuController.getToolHomeInventory(plugin, player));

			} else if (currItem.getItemMeta().getDisplayName().contains("은행")) {
				player.openInventory(MenuController.getBankHomeInventory(player));

			} else if (currItem.getItemMeta().getDisplayName().contains("마을")) {
				player.openInventory(MenuController.getVillageHomeInventory(plugin, player));

			} else if (currItem.getItemMeta().getDisplayName().contains("달력")) {
				player.openInventory(MenuController.getCalendarHomeInventory(plugin, player));

			} else if (currItem.getItemMeta().getDisplayName().contains("친구")) {
				player.openInventory(MenuController.getFriendHomeInventory(plugin, player));

			} else if (currItem.getItemMeta().getDisplayName().contains("열기구")) {
				player.openInventory(MenuController.getWarpHomeInventory(plugin, player));

			} else if (currItem.getItemMeta().getDisplayName().contains("개굴상점")) {
				player.openInventory(MenuController.getShoppingHomeInventory(plugin, player));

			} else if (currItem.getItemMeta().getDisplayName().contains("가이드북")) {
				player.openInventory(MenuController.getGuideHomeInventory(plugin, player));
			}
		}
	}

	// Tool Event
	public void toolHomeEvent(InventoryClickEvent event) {
		event.setCancelled(true);

		Player player = (Player) event.getWhoClicked();
		ItemStack currItem = event.getCurrentItem();
		if (currItem != null) {
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
	}

	public void toolStatEvent(InventoryClickEvent event, MenuUnicode title) throws SQLException {
		event.setCancelled(true);
		Player player = (Player) event.getWhoClicked();
		ItemStack currItem = event.getCurrentItem();

		if (currItem != null) {
			Inventory inv = event.getView().getTopInventory();

			if (event.getClickedInventory().equals(inv)) {
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
				int totalStat = MenuController
						.getNumber(CustomStack.byItemStack(inv.getItem(MenuController.slot(3, 1)))) * 10
						+ MenuController.getNumber(CustomStack.byItemStack(inv.getItem(MenuController.slot(3, 2))));

				if (currItem.getItemMeta().getDisplayName().contains("-")) {
					int number = MenuController.getNumber(CustomStack.byItemStack(inv.getItem(currSlot + 1)));
					if (number > 0) {
						inv.setItem(currSlot + 1, MenuController.getNumberItem(number - 1).getItemStack());

						totalStat++;
						int totalStatFirst = totalStat / 10;
						int totalStatSecond = totalStat % 10;

						inv.setItem(MenuController.slot(3, 1),
								MenuController.getNumberItem(totalStatFirst).getItemStack());
						inv.setItem(MenuController.slot(3, 2),
								MenuController.getNumberItem(totalStatSecond).getItemStack());
						player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5F, 0.8F);

						ItemStack plus = inv.getItem(currSlot + 2);
						if (plus.getItemMeta().hasLore()) {
							ItemMeta plusIM = plus.getItemMeta();
							plusIM.setLore(null);
							plus.setItemMeta(plusIM);
						}

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

						inv.setItem(currSlot - 1, MenuController.getNumberItem(++number).getItemStack());

						if (number == limit) {
							List<String> lore = Arrays.asList(ChatColor.RED + "업그레이드 할 수 있는 한계에 도달했습니다");
							ItemMeta newPlusIM = currItem.getItemMeta();
							newPlusIM.setLore(lore);
							currItem.setItemMeta(newPlusIM);
						} else {
							ItemMeta newPlusIM = currItem.getItemMeta();
							newPlusIM.setLore(null);
							currItem.setItemMeta(newPlusIM);
						}

						totalStat--;
						int totalStatFirst = totalStat / 10;
						int totalStatSecond = totalStat % 10;

						inv.setItem(MenuController.slot(3, 1),
								MenuController.getNumberItem(totalStatFirst).getItemStack());
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
								MenuController
										.getNumber(CustomStack.byItemStack(inv.getItem(MenuController.slot(4, 7)))));

					Map<Enchantment, Integer> enchantment = new HashMap<Enchantment, Integer>(
							toolData.getEnchantment());

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

	}

	// Bank Event
	public void bankHomeEvent(InventoryClickEvent event) {
		event.setCancelled(true);

		Player player = (Player) event.getWhoClicked();
		ItemStack currItem = event.getCurrentItem();

		if (currItem != null && currItem.getItemMeta().getDisplayName().contains("이전으로")) {
			player.openInventory(MenuController.getMenuInventory(player));
			return;
		}

		int slot = event.getRawSlot();

		if (MenuController.col(slot) < 4) {
			player.openInventory(MenuController.getBankTaxInventory(plugin, player));

		} else if (MenuController.col(slot) > 4) {
			player.openInventory(MenuController.getBankAccountInventory(player));
		}
	}

	public void bankAccountEvent(InventoryClickEvent event) {
		event.setCancelled(true);

		Player player = (Player) event.getWhoClicked();
		ItemStack currItem = event.getCurrentItem();

		if (currItem != null) {
			if (currItem.getItemMeta().getDisplayName().contains("출금할래")) {
				player.openInventory(MenuController.getBankWithdrawInventory(player));

			} else if (currItem.getItemMeta().getDisplayName().contains("입금할래")) {
				player.openInventory(MenuController.getBankDepositInventory(player));

			} else if (currItem.getItemMeta().getDisplayName().contains("아무것도아냐")) {
				player.openInventory(MenuController.getBankHomeInventory(player));

			} else if (currItem.getItemMeta().getDisplayName().contains("이전으로")) {
				player.openInventory(MenuController.getMenuInventory(player));

			}
		}
	}

	public void bankTaxEvent(InventoryClickEvent event) throws SQLException {
		event.setCancelled(true);

		Player player = (Player) event.getWhoClicked();
		ItemStack currItem = event.getCurrentItem();

		if (currItem != null) {
			if (currItem.getItemMeta().getDisplayName().contains("이전으로")) {
				player.openInventory(MenuController.getMenuInventory(player));
				return;
			}

			PlayerData pd = plugin.players.get(player.getUniqueId());

			if (currItem.getItemMeta().getDisplayName().contains("마을세금")) {

				if (pd.getVillage() > 0) {
					VillageData vd = plugin.villages.get(pd.getVillage());

					if (vd.getLastTax() > 0 && pd.getBalance() >= vd.getLastTax()) {
						player.closeInventory();
						MoneyController.withdrawMoeny(plugin, vd.getLastTax(), player.getUniqueId());
						VillageController.setLastTax(plugin, pd.getVillage(), 0);
						VillageController.updateLastTaxPaid(plugin, pd.getVillage(), LocalDate.now());
						player.sendMessage(ChatColor.GOLD + "주간 마을 세금을 납부하셨습니다");
						player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
					}
				}

			} else if (currItem.getItemMeta().getDisplayName().contains("마켓세금")) {

			} else if (currItem.getItemMeta().getDisplayName().contains("아무것도아냐")) {
				player.openInventory(MenuController.getBankHomeInventory(player));

			} else if (currItem.getItemMeta().getDisplayName().contains("이전으로")) {
				player.openInventory(MenuController.getMenuInventory(player));
			}
		}
	}

	public void bankDepositEvent(InventoryClickEvent event) throws SQLException {
		event.setCancelled(true);

		Player player = (Player) event.getWhoClicked();
		ItemStack currItem = event.getCurrentItem();

		if (currItem != null) {
			Inventory topInv = event.getView().getTopInventory();
			CustomStack customStack = CustomStack.byItemStack(currItem);

			if (event.getClickedInventory().equals(player.getInventory())) {

				if (customStack != null && (customStack.getNamespacedID().equals(MoneyItem.GOLD.getNamespacedID())
						|| customStack.getNamespacedID().equals(MoneyItem.SILVER.getNamespacedID())
						|| customStack.getNamespacedID().equals(MoneyItem.COPPER.getNamespacedID()))) {

					for (int i = 0; i < 7; i++) {
						ItemStack item = topInv.getItem(MenuController.slot(2, 1 + i));

						if (item == null || item.getType() == Material.AIR) {
							topInv.setItem(MenuController.slot(2, 1 + i), currItem);
							player.getInventory().setItem(event.getSlot(), null);
							MoneyController.updateDepositLore(topInv);
							break;
						}
					}
				}

			} else {
				if (customStack != null && (customStack.getNamespacedID().equals(MoneyItem.GOLD.getNamespacedID())
						|| customStack.getNamespacedID().equals(MoneyItem.SILVER.getNamespacedID())
						|| customStack.getNamespacedID().equals(MoneyItem.COPPER.getNamespacedID()))) {

					player.getInventory().addItem(currItem);
					topInv.setItem(event.getSlot(), null);
					MoneyController.updateDepositLore(topInv);

				} else if (currItem.getItemMeta().getDisplayName().contains("입금")) {

					int amount = 0;
					for (int i = 0; i < 7; i++) {
						ItemStack item = topInv.getItem(MenuController.slot(2, 1 + i));
						CustomStack cs = CustomStack.byItemStack(item);

						if (cs != null) {
							if (cs.getNamespacedID().equals(MoneyItem.GOLD.getNamespacedID())) {
								amount += item.getAmount() * 100;

							} else if (cs.getNamespacedID().equals(MoneyItem.SILVER.getNamespacedID())) {
								amount += item.getAmount() * 10;

							} else if (cs.getNamespacedID().equals(MoneyItem.COPPER.getNamespacedID())) {
								amount += item.getAmount();

							}
						}

						topInv.setItem(MenuController.slot(2, 1 + i), null);
					}

					if (amount > 0) {
						player.closeInventory();
						MoneyController.depoistMoeny(plugin, amount, player.getUniqueId());
						player.sendMessage(
								ChatColor.GOLD + "총 " + MoneyController.withLargeIntegers(amount) + " 코인을 입금했습니다");
						player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
					}

				} else if (currItem.getItemMeta().getDisplayName().contains("이전으로")) {
					player.openInventory(MenuController.getBankHomeInventory(player));

				}
			}
		}
	}

	public void bankWithdrawEvent(InventoryClickEvent event) {
		event.setCancelled(true);

		Player player = (Player) event.getWhoClicked();
		ItemStack currItem = event.getCurrentItem();

		if (currItem != null) {
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

				} else if (currItem.getItemMeta().getDisplayName().contains("출금")) {
					int amount = MoneyController.getWithdrawAmount(topInv);
					if (amount <= pd.getBalance() && amount > 0) {
						player.closeInventory();
						player.performCommand("출금 " + amount);
					}
				} else if (currItem.getItemMeta().getDisplayName().contains("이전으로")) {
					player.openInventory(MenuController.getBankHomeInventory(player));

				}
			}
		}
	}

	// Village Event
	public void villageHomeEvent(InventoryClickEvent event) {
		event.setCancelled(true);

		Player player = (Player) event.getWhoClicked();
		ItemStack currItem = event.getCurrentItem();

		if (currItem != null) {
			PlayerData pd = plugin.players.get(player.getUniqueId());

			if (currItem.getItemMeta().getDisplayName().contains("마을 정보 보기")) {
				if (pd.getVillage() > 0) {
					player.openInventory(MenuController.getVillageManageInventory(plugin, player, pd.getVillage()));
				}

			} else if (currItem.getItemMeta().getDisplayName().contains("내 마을로 이동")) {
				if (pd.getVillage() > 0) {
					VillageData vd = plugin.villages.get(pd.getVillage());
					MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager()
							.getPlugin("Multiverse-Core");
					MVWorldManager worldManager = core.getMVWorldManager();
					MultiverseWorld world = worldManager.getMVWorld("world");
					Location villageLoc = new Location(world.getCBWorld(), vd.getSpawn()[0], vd.getSpawn()[1],
							vd.getSpawn()[2]);

					player.sendMessage(ChatColor.GREEN + "마을 [" + ChatColor.GOLD + vd.getName() + ChatColor.GREEN
							+ "] (으)로 이동합니다");
					player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
					player.teleport(villageLoc);
				}

			} else if (currItem.getItemMeta() instanceof SkullMeta skull) {
				NamespacedKey villageKey = new NamespacedKey(plugin, "village");
				int villageId = skull.getPersistentDataContainer().get(villageKey, PersistentDataType.INTEGER);

				if (event.getClick() == ClickType.LEFT) {
					player.openInventory(MenuController.getVillageManageInventory(plugin, player, villageId));

				} else if (event.getClick() == ClickType.RIGHT) {
					VillageData vd = plugin.villages.get(villageId);
					MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager()
							.getPlugin("Multiverse-Core");
					MVWorldManager worldManager = core.getMVWorldManager();
					MultiverseWorld world = worldManager.getMVWorld("world");
					Location villageLoc = new Location(world.getCBWorld(), vd.getSpawn()[0], vd.getSpawn()[1],
							vd.getSpawn()[2]);

					player.sendMessage(ChatColor.GREEN + "마을 [" + ChatColor.GOLD + vd.getName() + ChatColor.GREEN
							+ "] (으)로 이동합니다");
					player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
					player.teleport(villageLoc);
				}

			} else if (currItem.getItemMeta().getDisplayName().contains("이전으로")) {
				player.openInventory(MenuController.getMenuInventory(player));

			}
		}

	}

	public void villageManageEvent(InventoryClickEvent event) throws SQLException {
		event.setCancelled(true);

		Player player = (Player) event.getWhoClicked();
		ItemStack currItem = event.getCurrentItem();

		if (currItem != null) {
			PlayerData pd = plugin.players.get(player.getUniqueId());

			if (currItem.getItemMeta().getDisplayName().contains("스폰 설정")) {
				VillageData vd = plugin.villages.get(pd.getVillage());
				if (vd != null && vd.getPresident().equals(player.getUniqueId())) {
					player.closeInventory();
					Chunk chunk = player.getLocation().getChunk();

					if (ChunkController.getChunkOwnerVillage(plugin, chunk) == vd.getId()) {
						int[] coordinate = new int[3];
						coordinate[0] = (int) player.getLocation().getX();
						coordinate[1] = (int) player.getLocation().getY();
						coordinate[2] = (int) player.getLocation().getZ();

						VillageController.setVillageSpawn(plugin, vd.getId(), coordinate);
						player.sendMessage(ChatColor.GREEN + "마을 [" + ChatColor.GOLD + vd.getName() + ChatColor.GREEN
								+ "] 의 스폰 좌표가 (" + coordinate[0] + ", " + coordinate[1] + ", " + coordinate[2]
								+ ") 로 변경되었습니다");
						player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);

					} else {
						player.sendMessage(ChatColor.RED + "마을 청크 내에서 스폰을 설정해야 합니다");
					}
				}

			} else if (currItem.getItemMeta().getDisplayName().contains("마을 신청 관리")) {
				player.openInventory(MenuController.getVillageApplyInventory(plugin, player));

			} else if (currItem.getItemMeta().getDisplayName().contains("마을 탈퇴")) {
				player.closeInventory();

				player.sendMessage(ChatColor.GREEN + "정말로 마을에서 탈퇴하겠습니까?");

				TextComponent yes = new TextComponent(ChatColor.GREEN + "> " + ChatColor.BOLD + "예");
				yes.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("마을에서 탈퇴합니다")));
				yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/마을 탈퇴 confirm"));

				TextComponent no = new TextComponent(ChatColor.GREEN + "> " + ChatColor.BOLD + "아니오");
				no.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("마을에서 탈퇴하지 않습니다")));
				no.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/마을 탈퇴 deny"));

				player.spigot().sendMessage(yes);
				player.spigot().sendMessage(no);

			} else if (currItem.getItemMeta().getDisplayName().contains("마을 신청")) {

				if (pd.getVillage() == 0) {
					player.closeInventory();
					int villageId = currItem.getItemMeta().getPersistentDataContainer()
							.get(new NamespacedKey(plugin, "villageId"), PersistentDataType.INTEGER);
					VillageController.applyVillage(plugin, player.getUniqueId(), villageId);

					VillageData vd = plugin.villages.get(villageId);
					player.sendMessage(ChatColor.GREEN + "마을 [" + ChatColor.GOLD + vd.getName() + ChatColor.GREEN
							+ "] 에 가입신청을 넣었습니다");
					player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);

					OfflinePlayer president = plugin.getServer().getOfflinePlayer(vd.getPresident());
					if (president.isOnline()) {
						((Player) president).sendMessage(
								Character.toString(LevelUpIcon.MAIL.val()) + ChatColor.GOLD + " 새로운 마을 가입 신청이 있습니다");
						player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
					}
				}

			} else if (currItem.getItemMeta() instanceof SkullMeta skull) {
				VillageData vd = plugin.villages.get(pd.getVillage());
				if (vd != null && vd.getPresident().equals(player.getUniqueId())) {
					OfflinePlayer op = skull.getOwningPlayer();
					PlayerData villager = plugin.players.get(op.getUniqueId());

					if (event.getClick() == ClickType.LEFT) {
						player.closeInventory();

						player.sendMessage(ChatColor.GREEN + "[" + ChatColor.GOLD + villager.getName() + ChatColor.GREEN
								+ "] 에게 이장을 위임합니까?");

						TextComponent yes = new TextComponent(ChatColor.GREEN + "> " + ChatColor.BOLD + "예");
						yes.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("이장을 위임합니다")));
						yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/마을 이장 " + villager.getName() + " confirm"));

						TextComponent no = new TextComponent(ChatColor.GREEN + "> " + ChatColor.BOLD + "아니오");
						no.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("이장을 위임하지 않습니다")));
						no.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/마을 이장 " + villager.getName() + " deny"));

						player.spigot().sendMessage(yes);
						player.spigot().sendMessage(no);

					} else if (event.getClick() == ClickType.RIGHT) {
						player.closeInventory();

						player.sendMessage(ChatColor.GREEN + "정말로 [" + ChatColor.GOLD + villager.getName()
								+ ChatColor.GREEN + "] 을(를) 마을에서 탈퇴시키겠습니까?");
						TextComponent yes = new TextComponent(ChatColor.GREEN + "> " + ChatColor.BOLD + "예");
						yes.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("마을에서 탈퇴시킵니다")));
						yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/마을 탈퇴 " + villager.getName() + " confirm"));

						TextComponent no = new TextComponent(ChatColor.GREEN + "> " + ChatColor.BOLD + "아니오");
						no.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("마을에서 탈퇴시키지 않습니다")));
						no.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
								"/마을 탈퇴 " + villager.getName() + " deny"));

						player.spigot().sendMessage(yes);
						player.spigot().sendMessage(no);
					}
				}

			} else if (currItem.getItemMeta().getDisplayName().contains("이전으로")) {
				player.openInventory(MenuController.getVillageHomeInventory(plugin, player));

			}
		}
	}

	public void villageApplyEvent(InventoryClickEvent event) throws SQLException {
		event.setCancelled(true);

		Player player = (Player) event.getWhoClicked();
		ItemStack currItem = event.getCurrentItem();

		if (currItem != null) {
			PlayerData pd = plugin.players.get(player.getUniqueId());
			VillageData vd = plugin.villages.get(pd.getVillage());

			if (currItem.getItemMeta() instanceof SkullMeta skull) {
				OfflinePlayer op = skull.getOwningPlayer();
				PlayerData opd = plugin.players.get(op.getUniqueId());

				if (event.getClick() == ClickType.LEFT) {
					player.closeInventory();
					VillageController.deleteVillageApply(plugin, op.getUniqueId());
					VillageController.addUser(plugin, vd.getId(), op.getUniqueId());

					for (PlayerData villager : plugin.players.values()) {
						if (villager.getVillage() == vd.getId()) {
							OfflinePlayer vp = plugin.getServer().getOfflinePlayer(villager.getUuid());
							if (vp.isOnline()) {
								((Player) vp).sendMessage(ChatColor.GREEN + "유저 [" + ChatColor.GOLD + opd.getName()
										+ ChatColor.GREEN + "] 님이 마을에 가입하셨습니다");
								((Player) vp).playSound(((Player) vp), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
							}
						}
					}

				} else if (event.getClick() == ClickType.RIGHT) {
					player.closeInventory();
					VillageController.deleteVillageApply(plugin, op.getUniqueId());
					if (op.isOnline()) {
						((Player) op).sendMessage(ChatColor.RED + "마을 [" + vd.getName() + "] 의 가입신청이 거절되었습니다");
						((Player) op).playSound(((Player) op), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
					}
				}
			}
		}
	}

	// Calendar Event
	public void calendarHomeEvent(InventoryClickEvent event) {
		event.setCancelled(true);

		Player player = (Player) event.getWhoClicked();
		ItemStack currItem = event.getCurrentItem();

		if (currItem != null) {
			if (currItem.getItemMeta().getDisplayName().contains("일일퀘스트")) {
				player.openInventory(MenuController.getCalendarQuestInventory(player));

			} else if (currItem.getItemMeta().getDisplayName().contains("시즌패스")) {
				player.openInventory(MenuController.getCalendarSeasonPassInventory(plugin, player, 1));

			} else if (currItem.getItemMeta().getDisplayName().contains("서버추천")) {
				// 서버 추천 링크

			} else if (currItem.getItemMeta().getDisplayName().contains("이전으로")) {
				player.openInventory(MenuController.getMenuInventory(player));

			}
		}
	}

	public void calendarQuestEvent(InventoryClickEvent event) {
		event.setCancelled(true);

		Player player = (Player) event.getWhoClicked();
		ItemStack currItem = event.getCurrentItem();

		if (currItem != null) {
			if (currItem.getItemMeta().getDisplayName().contains("이전으로")) {
				player.openInventory(MenuController.getMenuInventory(player));
			}
		}
	}

	public void calendarSeasonPassEvent(InventoryClickEvent event, int page) throws SQLException {
		event.setCancelled(true);

		Player player = (Player) event.getWhoClicked();
		ItemStack currItem = event.getCurrentItem();
		Inventory topInv = event.getView().getTopInventory();

		if (currItem != null && currItem.getItemMeta() != null) {
			if (currItem.getItemMeta().getDisplayName().contains("이전으로")) {
				if (page == 1) {
					player.openInventory(MenuController.getCalendarHomeInventory(plugin, player));
					return;

				} else {
					player.openInventory(MenuController.getCalendarSeasonPassInventory(plugin, player, page - 1));
					return;
				}

			} else if (currItem.getItemMeta().getDisplayName().contains("다음으로")) {
				player.openInventory(MenuController.getCalendarSeasonPassInventory(plugin, player, page + 1));
				return;
			}
		}

		SeasonPass seasonPass = plugin.seasonPassData.get(player.getUniqueId());
		if (seasonPass.getPosition() < seasonPass.getAvailable()) {
			int frogPos = seasonPass.getPosition() % 5;
			System.out.println("frogPos: " + frogPos);
			ItemStack item;
			if (frogPos == 0) {
				Inventory nextPage = MenuController.getCalendarSeasonPassInventory(plugin, player, page + 1);
				item = nextPage.getItem(SeasonPassController.REWARD_SLOTS[frogPos]);
			} else {
				item = topInv.getItem(SeasonPassController.REWARD_SLOTS[frogPos]);
			}

			SeasonPassController.jumpOneStep(plugin, player);
			seasonPass = plugin.seasonPassData.get(player.getUniqueId());

			if (seasonPass.getPosition() % 5 == 1 && seasonPass.getPosition() > 1) {
				player.openInventory(MenuController.getCalendarSeasonPassInventory(plugin, player, page + 1));
			} else {
				player.openInventory(MenuController.getCalendarSeasonPassInventory(plugin, player, page));
			}
			player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);

			if (item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "premium"))) {
				if (seasonPass.isPremium()) {
					player.getInventory().addItem(item);
				}
			} else {
				player.getInventory().addItem(item);
			}
		}
	}

	// Friend Event
	public void friendHomeEvent(InventoryClickEvent event) throws SQLException {
		event.setCancelled(true);

		Player player = (Player) event.getWhoClicked();
		ItemStack currItem = event.getCurrentItem();

		if (currItem != null) {
			if (currItem.getItemMeta() instanceof SkullMeta skull) {
				OfflinePlayer friend = skull.getOwningPlayer();
				PlayerData friendData = plugin.players.get(friend.getUniqueId());

				if (event.isLeftClick()) {
					player.openInventory(MenuController.getFriendInfoInventory(plugin, player, friend.getUniqueId()));

				} else if (event.isRightClick()) {
					player.closeInventory();
					FriendController.deleteFriend(plugin, player.getUniqueId(), friend.getUniqueId());
					player.sendMessage(ChatColor.RED + "[" + friendData.getName() + "] 님을 친구 목록에서 삭제했습니다");
				}

			} else if (currItem.getItemMeta().getDisplayName().contains("친구 신청함")) {
				player.openInventory(MenuController.getFriendRequestInventory(plugin, player));

			} else if (currItem.getItemMeta().getDisplayName().contains("차단 유저 관리")) {
				player.openInventory(MenuController.getFriendBlockInventory(plugin, player));

			} else if (currItem.getItemMeta().getDisplayName().contains("이전으로")) {
				player.openInventory(MenuController.getMenuInventory(player));

			}
		}
	}

	public void friendRequestEvent(InventoryClickEvent event) throws SQLException {
		event.setCancelled(true);

		Player player = (Player) event.getWhoClicked();
		ItemStack currItem = event.getCurrentItem();

		if (currItem != null) {
			if (currItem.getItemMeta() instanceof SkullMeta skull) {
				OfflinePlayer friend = skull.getOwningPlayer();
				PlayerData friendData = plugin.players.get(friend.getUniqueId());
				// 보낸 신청
				if (MenuController.col(event.getSlot()) < 4) {
					if (event.isRightClick()) {
						player.closeInventory();
						FriendController.deleteFriend(plugin, player.getUniqueId(), friend.getUniqueId());
						player.sendMessage(ChatColor.RED + "[" + friendData.getName() + "] 님에게 보낸 친구 신청을 취소했습니다");
					}

					// 받은 신청
				} else {
					if (event.isLeftClick()) {
						player.closeInventory();
						FriendController.acceptFriend(plugin, friend.getUniqueId(), player.getUniqueId());
						player.sendMessage(ChatColor.GREEN + "이제 [" + ChatColor.GOLD + friendData.getName()
								+ ChatColor.GREEN + "] 님과 친구입니다");
						player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);

					} else if (event.isRightClick()) {
						player.closeInventory();
						FriendController.rejectFriend(plugin, friend.getUniqueId(), player.getUniqueId());
						player.sendMessage(ChatColor.RED + "[" + friendData.getName() + "] 님의 친구 신청을 거절했습니다");
					}
				}
			}

			if (currItem.getItemMeta().getDisplayName().contains("이전으로")) {
				player.openInventory(MenuController.getFriendHomeInventory(plugin, player));
			}
		}
	}

	public void friendBlockEvent(InventoryClickEvent event) throws SQLException {
		event.setCancelled(true);

		Player player = (Player) event.getWhoClicked();
		ItemStack currItem = event.getCurrentItem();

		if (currItem != null) {
			if (currItem.getItemMeta() instanceof SkullMeta skull) {
				if (event.isRightClick()) {
					OfflinePlayer block = skull.getOwningPlayer();
					PlayerData blockData = plugin.players.get(block.getUniqueId());

					player.closeInventory();
					FriendController.unblockUser(plugin, player, block.getUniqueId());
					player.sendMessage(ChatColor.GREEN + "[" + ChatColor.GOLD + blockData.getName() + ChatColor.GREEN
							+ "] 님의 차단을 해제했습니다");
				}

			} else if (currItem.getItemMeta().getDisplayName().contains("이전으로")) {
				player.openInventory(MenuController.getFriendHomeInventory(plugin, player));
			}
		}
	}

	public void friendInfoEvent(InventoryClickEvent event) {
		event.setCancelled(true);

		Player player = (Player) event.getWhoClicked();
		ItemStack currItem = event.getCurrentItem();

		if (currItem != null) {
			if (CustomStack.byItemStack(currItem) != null
					&& CustomStack.byItemStack(currItem).getNamespacedID().equals(MenuIcon.VILLAGE.namespacedID())) {
				if (currItem.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "village"))) {
					int villageId = currItem.getItemMeta().getPersistentDataContainer()
							.get(new NamespacedKey(plugin, "village"), PersistentDataType.INTEGER);

					if (event.isLeftClick()) {
						player.openInventory(MenuController.getVillageManageInventory(plugin, player, villageId));

					} else if (event.isRightClick()) {
						VillageData vd = plugin.villages.get(villageId);
						MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager()
								.getPlugin("Multiverse-Core");
						MVWorldManager worldManager = core.getMVWorldManager();
						MultiverseWorld world = worldManager.getMVWorld("world");
						Location villageLoc = new Location(world.getCBWorld(), vd.getSpawn()[0], vd.getSpawn()[1],
								vd.getSpawn()[2]);

						player.sendMessage(ChatColor.GREEN + "마을 [" + ChatColor.GOLD + vd.getName() + ChatColor.GREEN
								+ "] (으)로 이동합니다");
						player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
						player.teleport(villageLoc);
					}
				}

			} else if (currItem.getItemMeta().getDisplayName().contains("이전으로")) {
				player.openInventory(MenuController.getFriendHomeInventory(plugin, player));
			}
		}
	}

	// Warp Event
	public void warpHomeEvent(InventoryClickEvent event) {
		event.setCancelled(true);

		Player player = (Player) event.getWhoClicked();
		ItemStack currItem = event.getCurrentItem();

		if (currItem != null) {
			MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
			MVWorldManager worldManager = core.getMVWorldManager();

			MultiverseWorld fromWorld = worldManager.getMVWorld(player.getWorld());
			MultiverseWorld toWorld = null;

			if (currItem.getItemMeta().getDisplayName().contains("광장 행")) {
				toWorld = worldManager.getMVWorld("spawn");
				player.sendMessage(ChatColor.GREEN + "광장으로 이동합니다...");

			} else if (currItem.getItemMeta().getDisplayName().contains("야생 행")) {
				toWorld = worldManager.getMVWorld("wild");
				player.sendMessage(ChatColor.GREEN + "야생월드로 이동합니다...");

			} else if (currItem.getItemMeta().getDisplayName().contains("건축 행")) {
				toWorld = worldManager.getMVWorld("world");
				player.sendMessage(ChatColor.GREEN + "건축월드로 이동합니다...");

			} else if (currItem.getItemMeta().getDisplayName().contains("지옥 행")) {
				toWorld = worldManager.getMVWorld("world_nether");
				player.sendMessage(ChatColor.GREEN + "지옥월드로 이동합니다...");

			}

			if (toWorld != null) {
				NamespacedKey key = new NamespacedKey(plugin, player.getUniqueId().toString());
				Location loc;
				if (toWorld.getAlias().equalsIgnoreCase("world")) {
					if (!fromWorld.getAlias().equalsIgnoreCase("world")) {
						if (toWorld.getCBWorld().getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
							JsonObject obj = JsonParser.parseString(toWorld.getCBWorld().getPersistentDataContainer()
									.get(key, PersistentDataType.STRING)).getAsJsonObject();
							loc = new Location(toWorld.getCBWorld(), obj.get("x").getAsDouble(),
									obj.get("y").getAsDouble(), obj.get("z").getAsDouble(), obj.get("yaw").getAsFloat(),
									obj.get("pitch").getAsFloat());

						} else {
							loc = toWorld.getSpawnLocation();
						}
					} else {
						return;
					}

				} else {
					loc = toWorld.getSpawnLocation();
				}

				if (fromWorld.getAlias().equalsIgnoreCase("world")) {
					JsonObject obj = new JsonObject();
					obj.addProperty("x", player.getLocation().getX());
					obj.addProperty("y", player.getLocation().getY());
					obj.addProperty("z", player.getLocation().getZ());
					obj.addProperty("yaw", player.getLocation().getYaw());
					obj.addProperty("pitch", player.getLocation().getPitch());
					fromWorld.getCBWorld().getPersistentDataContainer().set(key, PersistentDataType.STRING,
							obj.toString());
				}
				
				player.closeInventory();
				WarpController.warp(plugin, player, loc);
			}
		}
	}

	// Shopping Event
	public void shoppingHomeEvent(InventoryClickEvent event) throws SQLException {
		event.setCancelled(true);

		Player player = (Player) event.getWhoClicked();
		ItemStack currItem = event.getCurrentItem();

		if (currItem != null) {
			Inventory topInv = event.getView().getTopInventory();
			PlayerData pd = plugin.players.get(player.getUniqueId());

			int rowMin = 0, rowMax = 4;
			int colMin = 1, colMax = 7;
			if (event.getClickedInventory().equals(topInv) && event.getSlot() >= MenuController.slot(rowMin, colMin)
					&& event.getSlot() <= MenuController.slot(rowMax, colMax) && event.isLeftClick()) {

				int price = currItem.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(plugin, "price"),
						PersistentDataType.INTEGER);

				if (pd.getBalance() >= price) {

					if (player.getInventory().firstEmpty() == -1) {
						player.closeInventory();
						player.sendMessage(ChatColor.RED + "인벤토리가 가득찼습니다! 인벤토리를 비우고 다시 시도해주세요");

					} else {
						MoneyController.withdrawMoeny(plugin, price, player.getUniqueId());
						LevelUpItem item = new LevelUpItem(currItem);
						player.getInventory().addItem(item.getItemStack());
						player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
					}
				}

			} else if (currItem.getItemMeta().getDisplayName().contains("이전으로")) {
				player.openInventory(MenuController.getMenuInventory(player));

			}
		}
	}

	// Guide Event
	public void guideHomeEvent(InventoryClickEvent event) {
		event.setCancelled(true);

		Player player = (Player) event.getWhoClicked();
		ItemStack currItem = event.getCurrentItem();

		if (currItem != null) {
			if (currItem.getItemMeta().getDisplayName().contains("이전으로")) {
				player.openInventory(MenuController.getMenuInventory(player));

			}
		}
	}
}
