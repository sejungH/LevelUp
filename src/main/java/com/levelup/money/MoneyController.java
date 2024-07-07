package com.levelup.money;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.levelup.LevelUp;
import com.levelup.menu.MenuController;
import com.levelup.menu.MenuIcon;
import com.levelup.menu.MenuUnicode;
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
	
	public static Inventory getBankHomeInventory(LevelUp plugin, Player player) {
		Inventory bankHomeInv = Bukkit.createInventory((InventoryHolder) player, 54,
				MenuController.getInventoryTitle(MenuUnicode.BANK_HOME.val()));

		PlayerData pd = plugin.players.get(player.getUniqueId());
		String balance = Integer.toString(pd.getBalance());
		if (balance.length() > 6) {
			ItemStack nine = MoneyController.getWhiteNumberItem(9).getItemStack();
			for (int i = 1; i < 7; i++) {
				bankHomeInv.setItem(MenuController.slot(1, i), nine.clone());
			}

		} else {
			for (int i = 0; i < balance.length(); i++) {
				int index = i + (7 - balance.length());
				int num = Character.getNumericValue(balance.charAt(i));
				bankHomeInv.setItem(MenuController.slot(1, index), MoneyController.getWhiteNumberItem(num).getItemStack().clone());
			}
		}

		ItemStack coin = MoneyController.GOLD.getItemStack().clone();
		ItemMeta coinMeta = coin.getItemMeta();
		coinMeta.setDisplayName(ChatColor.GOLD + "개굴 코인");
		coin.setItemMeta(coinMeta);
		bankHomeInv.setItem(MenuController.slot(1, 7), coin);

		ItemStack depositMenu = MenuController.BLANK.getItemStack().clone();
		ItemMeta depositIM = depositMenu.getItemMeta();
		depositIM.setDisplayName(ChatColor.of("#8AC687") + "입금");
		depositMenu.setItemMeta(depositIM);
		bankHomeInv.setItem(MenuController.slot(4, 0), depositMenu);
		bankHomeInv.setItem(MenuController.slot(4, 1), depositMenu);
		bankHomeInv.setItem(MenuController.slot(4, 2), depositMenu);

		ItemStack withdrawMenu = MenuController.BLANK.getItemStack().clone();
		ItemMeta withdrawIM = withdrawMenu.getItemMeta();
		withdrawIM.setDisplayName(ChatColor.of("#8AC687") + "출금");
		withdrawMenu.setItemMeta(withdrawIM);
		bankHomeInv.setItem(MenuController.slot(4, 3), withdrawMenu);
		bankHomeInv.setItem(MenuController.slot(4, 4), withdrawMenu);
		bankHomeInv.setItem(MenuController.slot(4, 5), withdrawMenu);

		ItemStack taxMenu = MenuController.BLANK.getItemStack().clone();
		ItemMeta taxIM = taxMenu.getItemMeta();
		taxIM.setDisplayName(ChatColor.of("#8AC687") + "세금");
		taxMenu.setItemMeta(taxIM);
		bankHomeInv.setItem(MenuController.slot(4, 6), taxMenu);
		bankHomeInv.setItem(MenuController.slot(4, 7), taxMenu);
		bankHomeInv.setItem(MenuController.slot(4, 8), taxMenu);

		return bankHomeInv;
	}

	public static Inventory getBankDepositInventory(Player player) {
		Inventory bankDepositInv = Bukkit.createInventory((InventoryHolder) player, 54,
				MenuController.getInventoryTitle(MenuUnicode.BANK_DEPOSIT.val()));

		ItemStack depositBtn = MenuController.BLANK.getItemStack().clone();
		ItemMeta depositIM = depositBtn.getItemMeta();
		depositIM.setDisplayName(ChatColor.of("#8AC687") + "입금하기");
		depositBtn.setItemMeta(depositIM);
		bankDepositInv.setItem(MenuController.slot(4, 3), depositBtn);
		bankDepositInv.setItem(MenuController.slot(4, 4), depositBtn);
		bankDepositInv.setItem(MenuController.slot(4, 5), depositBtn);
		
		MoneyController.updateDepositLore(bankDepositInv);

		return bankDepositInv;
	}

	public static Inventory getBankWithdrawInventory(Player player) {
		Inventory bankWithdrawInv = Bukkit.createInventory((InventoryHolder) player, 54,
				MenuController.getInventoryTitle(MenuUnicode.BANK_WITHDRAW.val()));

		ItemStack coin = MoneyController.GOLD.getItemStack().clone();
		ItemMeta coinMeta = coin.getItemMeta();
		coinMeta.setDisplayName(ChatColor.GOLD + "개굴 코인");
		coin.setItemMeta(coinMeta);
		bankWithdrawInv.setItem(MenuController.slot(0, 7), coin);

		ItemStack zeroBtn = MenuController.BLANK.getItemStack().clone();
		ItemMeta zeroIM = zeroBtn.getItemMeta();
		zeroIM.setDisplayName(ChatColor.WHITE + "0");
		zeroBtn.setItemMeta(zeroIM);
		bankWithdrawInv.setItem(MenuController.slot(5, 1), zeroBtn);
		bankWithdrawInv.setItem(MenuController.slot(5, 2), zeroBtn);

		ItemStack oneBtn = MenuController.BLANK.getItemStack().clone();
		ItemMeta oneIM = oneBtn.getItemMeta();
		oneIM.setDisplayName(ChatColor.WHITE + "1");
		oneBtn.setItemMeta(oneIM);
		bankWithdrawInv.setItem(MenuController.slot(2, 1), oneBtn);

		ItemStack twoBtn = MenuController.BLANK.getItemStack().clone();
		ItemMeta twoIM = twoBtn.getItemMeta();
		twoIM.setDisplayName(ChatColor.WHITE + "2");
		twoBtn.setItemMeta(twoIM);
		bankWithdrawInv.setItem(MenuController.slot(2, 2), twoBtn);

		ItemStack threeBtn = MenuController.BLANK.getItemStack().clone();
		ItemMeta threeIM = threeBtn.getItemMeta();
		threeIM.setDisplayName(ChatColor.WHITE + "3");
		threeBtn.setItemMeta(threeIM);
		bankWithdrawInv.setItem(MenuController.slot(2, 3), threeBtn);

		ItemStack fourBtn = MenuController.BLANK.getItemStack().clone();
		ItemMeta fourIM = fourBtn.getItemMeta();
		fourIM.setDisplayName(ChatColor.WHITE + "4");
		fourBtn.setItemMeta(fourIM);
		bankWithdrawInv.setItem(MenuController.slot(3, 1), fourBtn);

		ItemStack fiveBtn = MenuController.BLANK.getItemStack().clone();
		ItemMeta fiveIM = fiveBtn.getItemMeta();
		fiveIM.setDisplayName(ChatColor.WHITE + "5");
		fiveBtn.setItemMeta(fiveIM);
		bankWithdrawInv.setItem(MenuController.slot(3, 2), fiveBtn);

		ItemStack sixBtn = MenuController.BLANK.getItemStack().clone();
		ItemMeta sixIM = sixBtn.getItemMeta();
		sixIM.setDisplayName(ChatColor.WHITE + "6");
		sixBtn.setItemMeta(sixIM);
		bankWithdrawInv.setItem(MenuController.slot(3, 3), sixBtn);

		ItemStack sevenBtn = MenuController.BLANK.getItemStack().clone();
		ItemMeta sevenIM = sevenBtn.getItemMeta();
		sevenIM.setDisplayName(ChatColor.WHITE + "7");
		sevenBtn.setItemMeta(sevenIM);
		bankWithdrawInv.setItem(MenuController.slot(4, 1), sevenBtn);

		ItemStack eightBtn = MenuController.BLANK.getItemStack().clone();
		ItemMeta eightIM = eightBtn.getItemMeta();
		eightIM.setDisplayName(ChatColor.WHITE + "8");
		eightBtn.setItemMeta(eightIM);
		bankWithdrawInv.setItem(MenuController.slot(4, 2), eightBtn);

		ItemStack nineBtn = MenuController.BLANK.getItemStack().clone();
		ItemMeta nineIM = nineBtn.getItemMeta();
		nineIM.setDisplayName(ChatColor.WHITE + "9");
		nineBtn.setItemMeta(nineIM);
		bankWithdrawInv.setItem(MenuController.slot(4, 3), nineBtn);

		ItemStack backBtn = MenuController.BLANK.getItemStack().clone();
		ItemMeta backIM = backBtn.getItemMeta();
		backIM.setDisplayName(ChatColor.WHITE + "지우기");
		backBtn.setItemMeta(backIM);
		bankWithdrawInv.setItem(MenuController.slot(5, 3), backBtn);

		ItemStack withdrawBtn = MenuController.BLANK.getItemStack().clone();
		ItemMeta withdrawIM = withdrawBtn.getItemMeta();
		withdrawIM.setDisplayName(ChatColor.of("#8AC687") + "출금하기");
		List<String> itemLore = new ArrayList<String>();
		itemLore.add(ChatColor.WHITE + "총 0 코인을 출금합니다");
		withdrawIM.setLore(itemLore);
		withdrawBtn.setItemMeta(withdrawIM);
		bankWithdrawInv.setItem(MenuController.slot(2, 5), withdrawBtn);
		bankWithdrawInv.setItem(MenuController.slot(2, 6), withdrawBtn);
		bankWithdrawInv.setItem(MenuController.slot(2, 7), withdrawBtn);
		bankWithdrawInv.setItem(MenuController.slot(3, 5), withdrawBtn);
		bankWithdrawInv.setItem(MenuController.slot(3, 6), withdrawBtn);
		bankWithdrawInv.setItem(MenuController.slot(3, 7), withdrawBtn);
		bankWithdrawInv.setItem(MenuController.slot(4, 5), withdrawBtn);
		bankWithdrawInv.setItem(MenuController.slot(4, 6), withdrawBtn);
		bankWithdrawInv.setItem(MenuController.slot(4, 7), withdrawBtn);

		return bankWithdrawInv;
	}

	public static Inventory getBankTaxInventory(Player player) {
		Inventory bankTaxInv = Bukkit.createInventory((InventoryHolder) player, 54,
				MenuController.getInventoryTitle(MenuUnicode.BANK_TAX.val()));

		return bankTaxInv;
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
						inv.setItem(MenuController.slot(0, 6), getNumberItem(num).getItemStack());
					}

				} else {
					inv.setItem(MenuController.slot(0, 6), getNumberItem(num).getItemStack());
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
				amount += getNumber(customStack) * digit;
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

	public static CustomStack getNumberItem(int num) {

		if (num == 0) {
			return MenuIcon.ZERO.val();

		} else if (num == 1) {
			return MenuIcon.ONE.val();

		} else if (num == 2) {
			return MenuIcon.TWO.val();

		} else if (num == 3) {
			return MenuIcon.THREE.val();

		} else if (num == 4) {
			return MenuIcon.FOUR.val();

		} else if (num == 5) {
			return MenuIcon.FIVE.val();

		} else if (num == 6) {
			return MenuIcon.SIX.val();

		} else if (num == 7) {
			return MenuIcon.SEVEN.val();

		} else if (num == 8) {
			return MenuIcon.EIGHT.val();

		} else if (num == 9) {
			return MenuIcon.NINE.val();

		}

		return null;
	}

	public static CustomStack getWhiteNumberItem(int num) {

		if (num == 0) {
			return MenuIcon.ZERO_WHITE.val();

		} else if (num == 1) {
			return MenuIcon.ONE_WHITE.val();

		} else if (num == 2) {
			return MenuIcon.TWO_WHITE.val();

		} else if (num == 3) {
			return MenuIcon.THREE_WHITE.val();

		} else if (num == 4) {
			return MenuIcon.FOUR_WHITE.val();

		} else if (num == 5) {
			return MenuIcon.FIVE_WHITE.val();

		} else if (num == 6) {
			return MenuIcon.SIX_WHITE.val();

		} else if (num == 7) {
			return MenuIcon.SEVEN_WHITE.val();

		} else if (num == 8) {
			return MenuIcon.EIGHT_WHITE.val();

		} else if (num == 9) {
			return MenuIcon.NINE_WHITE.val();

		}

		return null;
	}

	public static int getNumber(CustomStack item) {
		if (item != null) {
			if (item.getNamespacedID().equals(MenuIcon.ZERO.val().getNamespacedID())
					|| item.getNamespacedID().equals(MenuIcon.ZERO_WHITE.val().getNamespacedID())) {
				return 0;

			} else if (item.getNamespacedID().equals(MenuIcon.ONE.val().getNamespacedID())
					|| item.getNamespacedID().equals(MenuIcon.ONE_WHITE.val().getNamespacedID())) {
				return 1;

			} else if (item.getNamespacedID().equals(MenuIcon.TWO.val().getNamespacedID())
					|| item.getNamespacedID().equals(MenuIcon.TWO_WHITE.val().getNamespacedID())) {
				return 2;

			} else if (item.getNamespacedID().equals(MenuIcon.THREE.val().getNamespacedID())
					|| item.getNamespacedID().equals(MenuIcon.THREE_WHITE.val().getNamespacedID())) {
				return 3;

			} else if (item.getNamespacedID().equals(MenuIcon.FOUR.val().getNamespacedID())
					|| item.getNamespacedID().equals(MenuIcon.FOUR_WHITE.val().getNamespacedID())) {
				return 4;

			} else if (item.getNamespacedID().equals(MenuIcon.FIVE.val().getNamespacedID())
					|| item.getNamespacedID().equals(MenuIcon.FIVE_WHITE.val().getNamespacedID())) {
				return 5;

			} else if (item.getNamespacedID().equals(MenuIcon.SIX.val().getNamespacedID())
					|| item.getNamespacedID().equals(MenuIcon.SIX_WHITE.val().getNamespacedID())) {
				return 6;

			} else if (item.getNamespacedID().equals(MenuIcon.SEVEN.val().getNamespacedID())
					|| item.getNamespacedID().equals(MenuIcon.SEVEN_WHITE.val().getNamespacedID())) {
				return 7;

			} else if (item.getNamespacedID().equals(MenuIcon.EIGHT.val().getNamespacedID())
					|| item.getNamespacedID().equals(MenuIcon.EIGHT_WHITE.val().getNamespacedID())) {
				return 8;

			} else if (item.getNamespacedID().equals(MenuIcon.NINE.val().getNamespacedID())
					|| item.getNamespacedID().equals(MenuIcon.NINE_WHITE.val().getNamespacedID())) {
				return 9;

			} else {
				return -1;

			}
		} else {

			return -1;
		}
	}

}
