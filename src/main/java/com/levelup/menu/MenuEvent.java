package com.levelup.menu;

import java.sql.Connection;
import java.sql.SQLException;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.levelup.main.LevelUp;
import com.levelup.money.MoneyController;
import com.levelup.player.PlayerData;

import dev.lone.itemsadder.api.CustomStack;
import net.md_5.bungee.api.ChatColor;

public class MenuEvent implements Listener {

	private LevelUp plugin;
	private Connection conn;

	public MenuEvent(LevelUp plugin) {
		this.plugin = plugin;
		this.conn = plugin.mysql.getConnection();
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

		} else if (currItem.getItemMeta().getDisplayName().contains("마을")) {

		} else if (currItem.getItemMeta().getDisplayName().contains("달력")) {

		} else if (currItem.getItemMeta().getDisplayName().contains("친구")) {

		} else if (currItem.getItemMeta().getDisplayName().contains("열기구")) {

		} else if (currItem.getItemMeta().getDisplayName().contains("마켓")) {

		} else if (currItem.getItemMeta().getDisplayName().contains("은행")) {
			player.openInventory(MenuController.getBankHomeInventory(plugin, player));

		} else if (currItem.getItemMeta().getDisplayName().contains("가이드북")) {

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

	private void bankDepositEvent(InventoryClickEvent event) throws SQLException {
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
					MoneyController.depoistMoeny(plugin, conn, amount, player);
					player.closeInventory();
					player.sendMessage(
							ChatColor.GOLD + "총 " + MoneyController.withLargeIntegers(amount) + " 코인을 입금했습니다.");
					player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
				}
			}
		}
	}

	private void bankWithdrawEvent(InventoryClickEvent event) {
		event.setCancelled(true);

		Player player = (Player) event.getWhoClicked();
		ItemStack currItem = event.getCurrentItem();
		Inventory topInv = event.getView().getTopInventory();
		PlayerData pd = plugin.players.get(player.getUniqueId());

		for (int i = 0; i < 10; i++) {
			if (currItem.getItemMeta().getDisplayName().contains(Integer.toString(i))) {
				MoneyController.withdrawInput(topInv, i);
				MoneyController.updateWithdrawLore(topInv, pd);
			}
		}

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
