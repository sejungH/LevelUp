package com.levelup.money;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.levelup.LevelUp;
import com.levelup.menu.MenuController;
import com.levelup.player.PlayerData;

import dev.lone.itemsadder.api.CustomStack;
import net.md_5.bungee.api.ChatColor;

public class MoneyController {

	public static final CustomStack GOLD = CustomStack.getInstance("customitems:gold");
	public static final CustomStack SILVER = CustomStack.getInstance("customitems:silver");
	public static final CustomStack COPPER = CustomStack.getInstance("customitems:copper");

	public static int depositAll(LevelUp plugin, Player player) {

		int countGold = 0;
		int countSilver = 0;
		int countCopper = 0;

		for (ItemStack i : player.getInventory()) {
			CustomStack customStack = CustomStack.byItemStack(i);
			if (customStack != null) {
				if (customStack.getNamespacedID().equals(GOLD.getNamespacedID())) {
					countGold += i.getAmount();
					player.getInventory().remove(i);

				} else if (customStack.getNamespacedID().equals(SILVER.getNamespacedID())) {
					countSilver += i.getAmount();
					player.getInventory().remove(i);

				} else if (customStack.getNamespacedID().equals(COPPER.getNamespacedID())) {
					countCopper += i.getAmount();
					player.getInventory().remove(i);
				}
			}
		}

		return countGold * 100 + countSilver * 10 + countCopper;
	}
	
	public static void depoistMoeny(LevelUp plugin, int amount, UUID uuid) throws SQLException {
		Connection conn = plugin.mysql.getConnection();

		PlayerData pd = plugin.players.get(uuid);

		String sql = "UPDATE player SET balance = ? WHERE uuid = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);

		pstmt.setInt(1, pd.getBalance() + amount);
		pstmt.setString(2, pd.getUuid().toString());
		pstmt.executeUpdate();
		pstmt.close();

		pd.setBalance(pd.getBalance() + amount);
	}

	public static void withdrawMoeny(LevelUp plugin, int amount, UUID uuid) throws SQLException {
		Connection conn = plugin.mysql.getConnection();

		PlayerData pd = plugin.players.get(uuid);
		String sql = "UPDATE player SET balance = ? WHERE uuid = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);

		pstmt.setInt(1, pd.getBalance() - amount);
		pstmt.setString(2, pd.getUuid().toString());
		pstmt.executeUpdate();
		pstmt.close();

		pd.setBalance(pd.getBalance() - amount);
	}
	
	public static String withLargeIntegers(double value) {
		DecimalFormat df = new DecimalFormat("###,###,###");
		return df.format(value);
	}

	public static void withdrawInput(Inventory inv, int num) {

		if (num < 0) {
			for (int i = 5; i >= 0; i--) {
				inv.setItem(MenuController.slot(0, 1 + i), inv.getItem(MenuController.slot(0, i)));
			}

		} else {
			if (inv.getItem(MenuController.slot(0, 1)) == null) {
				for (int i = 0; i < 5; i++) {
					inv.setItem(MenuController.slot(0, 1 + i), inv.getItem(MenuController.slot(0, 2 + i)));
				}

				if (num == 0) {
					if (inv.getItem(MenuController.slot(0, 6)) != null) {
						inv.setItem(MenuController.slot(0, 6), MenuController.getNumberItem(num).getItemStack());
					}

				} else {
					inv.setItem(MenuController.slot(0, 6), MenuController.getNumberItem(num).getItemStack());
				}
			}
		}
	}

	public static int getWithdrawAmount(Inventory inv) {
		int amount = 0;

		for (int i = 0; i < 6; i++) {
			ItemStack item = inv.getItem(MenuController.slot(0, 1 + i));
			CustomStack customStack = CustomStack.byItemStack(item);
			int digit = (int) (100000 / Math.pow(10, i));

			if (customStack != null) {
				amount += MenuController.getNumber(customStack) * digit;
			}
		}

		return amount;
	}
	
	public static void updateDepositLore(Inventory inv) {
		int amount = 0;

		for (int i = 0; i < 5; i++) {
			ItemStack item = inv.getItem(MenuController.slot(1, 2 + i));
			CustomStack customStack = CustomStack.byItemStack(item);

			if (customStack != null) {
				if (customStack.getNamespacedID().equals(MoneyController.GOLD.getNamespacedID())) {
					amount += item.getAmount() * 100;

				} else if (customStack.getNamespacedID().equals(MoneyController.SILVER.getNamespacedID())) {
					amount += item.getAmount() * 10;

				} else if (customStack.getNamespacedID().equals(MoneyController.COPPER.getNamespacedID())) {
					amount += item.getAmount();

				}
			}
		}

		for (ItemStack item : inv) {
			if (item != null && item.getItemMeta().getDisplayName().contains("입금하기")) {
				ItemMeta itemMeta = item.getItemMeta();
				List<String> itemLore = new ArrayList<String>();
				itemLore.add(ChatColor.WHITE + "총 " + amount + " 코인을 입금합니다");
				itemMeta.setLore(itemLore);
				item.setItemMeta(itemMeta);
			}
		}
	}

	public static void updateWithdrawLore(Inventory inv, PlayerData pd) {
		int amount = getWithdrawAmount(inv);

		for (ItemStack item : inv) {
			if (item != null && item.getItemMeta().getDisplayName().contains("출금하기")) {
				ItemMeta itemMeta = item.getItemMeta();
				List<String> itemLore = new ArrayList<String>();
				itemLore.add(ChatColor.WHITE + "총 " + amount + " 코인을 출금합니다");

				if (amount > pd.getBalance()) {
					itemLore.add(ChatColor.RED + "출금 액수가 진고 액수보다 많을 수 없습니다");
				}

				itemMeta.setLore(itemLore);
				item.setItemMeta(itemMeta);
			}
		}
	}

}
