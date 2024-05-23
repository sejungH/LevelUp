package com.levelup.menu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.levelup.main.LevelUp;
import com.levelup.money.MoneyController;
import com.levelup.player.PlayerData;

import dev.lone.itemsadder.api.CustomStack;
import net.md_5.bungee.api.ChatColor;

public class MenuController {

	public static final CustomStack BLANK = CustomStack.getInstance("customitems:blank");

	public static String getInventoryTitle(char code) {
		StringBuilder sb = new StringBuilder();
		sb.append(ChatColor.WHITE);
		sb.append(MenuUnicode.SPACE.val());
		sb.append(code);

		return sb.toString();
	}

	public static int slot(int row, int col) {
		return row * 9 + col;
	}

	public static Inventory getMenuInventory(Player player) {
		Inventory menuInv = Bukkit.createInventory((InventoryHolder) player, 54,
				MenuController.getInventoryTitle(MenuUnicode.MENU.val()));

		ItemStack toolMenu = BLANK.getItemStack().clone();
		ItemMeta toolIM = toolMenu.getItemMeta();
		toolIM.setDisplayName(ChatColor.of("#d25656") + "도구");
		List<String> toolLore = new ArrayList<String>();
		toolLore.add(ChatColor.WHITE + "♦ 도구 스탯 변경");
		toolLore.add(ChatColor.WHITE + "♦ 도구 스킨 변경");
		toolIM.setLore(toolLore);
		toolMenu.setItemMeta(toolIM);
		menuInv.setItem(slot(0, 2), toolMenu);
		menuInv.setItem(slot(1, 2), toolMenu);

		ItemStack villageMenu = BLANK.getItemStack().clone();
		ItemMeta villageIM = villageMenu.getItemMeta();
		villageIM.setDisplayName(ChatColor.of("#00b27d") + "마을");
		List<String> vilageLore = new ArrayList<String>();
		vilageLore.add(ChatColor.WHITE + "♦ 마을 관리");
		vilageLore.add(ChatColor.WHITE + "♦ 마을 정보");
		villageIM.setLore(vilageLore);
		villageMenu.setItemMeta(villageIM);
		menuInv.setItem(slot(0, 3), villageMenu);
		menuInv.setItem(slot(0, 4), villageMenu);
		menuInv.setItem(slot(1, 3), villageMenu);
		menuInv.setItem(slot(1, 4), villageMenu);

		ItemStack calendarMenu = BLANK.getItemStack().clone();
		ItemMeta calendarIM = calendarMenu.getItemMeta();
		calendarIM.setDisplayName(ChatColor.of("#88cb2e") + "달력");
		List<String> calendarLore = new ArrayList<String>();
		calendarLore.add(ChatColor.WHITE + "♦ 일일퀘스트");
		calendarLore.add(ChatColor.WHITE + "♦ 시즌패스");
		calendarLore.add(ChatColor.WHITE + "♦ 서버추천");
		calendarIM.setLore(calendarLore);
		calendarMenu.setItemMeta(calendarIM);
		menuInv.setItem(slot(0, 6), calendarMenu);
		menuInv.setItem(slot(0, 7), calendarMenu);
		menuInv.setItem(slot(0, 8), calendarMenu);
		menuInv.setItem(slot(1, 6), calendarMenu);
		menuInv.setItem(slot(1, 7), calendarMenu);
		menuInv.setItem(slot(1, 8), calendarMenu);
		menuInv.setItem(slot(2, 6), calendarMenu);
		menuInv.setItem(slot(2, 7), calendarMenu);
		menuInv.setItem(slot(2, 8), calendarMenu);
		menuInv.setItem(slot(3, 6), calendarMenu);
		menuInv.setItem(slot(3, 7), calendarMenu);
		menuInv.setItem(slot(3, 8), calendarMenu);

		ItemStack friendMenu = BLANK.getItemStack().clone();
		ItemMeta friendIM = friendMenu.getItemMeta();
		friendIM.setDisplayName(ChatColor.of("#aa9470") + "친구");
		List<String> friendLore = new ArrayList<String>();
		friendLore.add(ChatColor.WHITE + "♦ 친구 신청");
		friendLore.add(ChatColor.WHITE + "♦ 친구 관리");
		friendIM.setLore(friendLore);
		friendMenu.setItemMeta(friendIM);
		menuInv.setItem(slot(2, 0), friendMenu);
		menuInv.setItem(slot(2, 1), friendMenu);
		menuInv.setItem(slot(3, 0), friendMenu);
		menuInv.setItem(slot(3, 1), friendMenu);

		ItemStack warpMenu = BLANK.getItemStack().clone();
		ItemMeta warpIM = warpMenu.getItemMeta();
		warpIM.setDisplayName(ChatColor.of("#3395ff") + "열기구");
		List<String> warpLore = new ArrayList<String>();
		warpLore.add(ChatColor.WHITE + "♦ 광장 행");
		warpLore.add(ChatColor.WHITE + "♦ 야생 행");
		warpLore.add(ChatColor.WHITE + "♦ 지옥 행");
		warpLore.add(ChatColor.WHITE + "♦ 건축 행");
		warpLore.add(ChatColor.WHITE + "♦ 마을 행");
		warpIM.setLore(warpLore);
		warpMenu.setItemMeta(warpIM);
		menuInv.setItem(slot(4, 1), warpMenu);
		menuInv.setItem(slot(4, 2), warpMenu);
		menuInv.setItem(slot(5, 1), warpMenu);
		menuInv.setItem(slot(5, 2), warpMenu);

		ItemStack marketMenu = BLANK.getItemStack().clone();
		ItemMeta marketIM = marketMenu.getItemMeta();
		marketIM.setDisplayName(ChatColor.of("#9de19a") + "개굴상점");
		List<String> marketLore = new ArrayList<String>();
		marketLore.add(ChatColor.WHITE + "♦ 물품 구매/판매");
		marketIM.setLore(marketLore);
		marketMenu.setItemMeta(marketIM);
		menuInv.setItem(slot(3, 3), marketMenu);
		menuInv.setItem(slot(3, 4), marketMenu);
		menuInv.setItem(slot(4, 3), marketMenu);
		menuInv.setItem(slot(4, 4), marketMenu);

		ItemStack bankMenu = BLANK.getItemStack().clone();
		ItemMeta bankIM = bankMenu.getItemMeta();
		bankIM.setDisplayName(ChatColor.of("#f1c14c") + "은행");
		List<String> bankLore = new ArrayList<String>();
		bankLore.add(ChatColor.WHITE + "♦ 출금/입금");
		bankLore.add(ChatColor.WHITE + "♦ 세금 관리");
		bankIM.setLore(bankLore);
		bankMenu.setItemMeta(bankIM);
		menuInv.setItem(slot(4, 5), bankMenu);
		menuInv.setItem(slot(4, 6), bankMenu);

		ItemStack guideMenu = BLANK.getItemStack().clone();
		ItemMeta guideIM = guideMenu.getItemMeta();
		guideIM.setDisplayName(ChatColor.of("#92affa") + "가이드북");
		List<String> guideLore = new ArrayList<String>();
		guideLore.add(ChatColor.WHITE + "♦ 초심자 가이드");
		guideIM.setLore(guideLore);
		guideMenu.setItemMeta(guideIM);
		menuInv.setItem(slot(4, 7), guideMenu);
		menuInv.setItem(slot(4, 8), guideMenu);

		return menuInv;
	}

	public static Inventory getBankHomeInventory(LevelUp plugin, Player player) {
		Inventory bankHomeInv = Bukkit.createInventory((InventoryHolder) player, 54,
				MenuController.getInventoryTitle(MenuUnicode.BANK_HOME.val()));

		PlayerData pd = plugin.players.get(player.getUniqueId());
		String balance = Integer.toString(pd.getBalance());
		if (balance.length() > 6) {
			ItemStack nine = MoneyController.getNumberItem(9).getItemStack();
			for (int i = 1; i < 7; i++) {
				bankHomeInv.setItem(slot(1, i), nine.clone());
			}

		} else {
			for (int i = 0; i < balance.length(); i++) {
				int index = i + (7 - balance.length());
				int num = Character.getNumericValue(balance.charAt(i));
				bankHomeInv.setItem(slot(1, index), MoneyController.getNumberItem(num).getItemStack().clone());
			}
		}

		ItemStack coin = MoneyController.GOLD.getItemStack().clone();
		ItemMeta coinMeta = coin.getItemMeta();
		coinMeta.setDisplayName(ChatColor.GOLD + "개굴 코인");
		coin.setItemMeta(coinMeta);
		bankHomeInv.setItem(slot(1, 7), coin);

		ItemStack depositMenu = BLANK.getItemStack().clone();
		ItemMeta depositIM = depositMenu.getItemMeta();
		depositIM.setDisplayName(ChatColor.RED + "입금");
		depositMenu.setItemMeta(depositIM);
		bankHomeInv.setItem(slot(4, 0), depositMenu);
		bankHomeInv.setItem(slot(4, 1), depositMenu);
		bankHomeInv.setItem(slot(4, 2), depositMenu);

		ItemStack withdrawMenu = BLANK.getItemStack().clone();
		ItemMeta withdrawIM = withdrawMenu.getItemMeta();
		withdrawIM.setDisplayName(ChatColor.BLUE + "출금");
		withdrawMenu.setItemMeta(withdrawIM);
		bankHomeInv.setItem(slot(4, 3), withdrawMenu);
		bankHomeInv.setItem(slot(4, 4), withdrawMenu);
		bankHomeInv.setItem(slot(4, 5), withdrawMenu);

		ItemStack taxMenu = BLANK.getItemStack().clone();
		ItemMeta taxIM = taxMenu.getItemMeta();
		taxIM.setDisplayName(ChatColor.GREEN + "세금");
		taxMenu.setItemMeta(taxIM);
		bankHomeInv.setItem(slot(4, 6), taxMenu);
		bankHomeInv.setItem(slot(4, 7), taxMenu);
		bankHomeInv.setItem(slot(4, 8), taxMenu);

		return bankHomeInv;
	}

	public static Inventory getBankDepositInventory(Player player) {
		Inventory bankDepositInv = Bukkit.createInventory((InventoryHolder) player, 54,
				MenuController.getInventoryTitle(MenuUnicode.BANK_DEPOSIT.val()));

		ItemStack depositBtn = BLANK.getItemStack().clone();
		ItemMeta depositIM = depositBtn.getItemMeta();
		depositIM.setDisplayName(ChatColor.RED + "입금하기");
		depositBtn.setItemMeta(depositIM);
		bankDepositInv.setItem(slot(4, 3), depositBtn);
		bankDepositInv.setItem(slot(4, 4), depositBtn);
		bankDepositInv.setItem(slot(4, 5), depositBtn);

		return bankDepositInv;
	}

	public static Inventory getBankWithdrawInventory(Player player) {
		Inventory bankWithdrawInv = Bukkit.createInventory((InventoryHolder) player, 54,
				MenuController.getInventoryTitle(MenuUnicode.BANK_WITHDRAW.val()));

		ItemStack coin = MoneyController.GOLD.getItemStack().clone();
		ItemMeta coinMeta = coin.getItemMeta();
		coinMeta.setDisplayName(ChatColor.GOLD + "개굴 코인");
		coin.setItemMeta(coinMeta);
		bankWithdrawInv.setItem(slot(0, 7), coin);

		ItemStack zeroBtn = BLANK.getItemStack().clone();
		ItemMeta zeroIM = zeroBtn.getItemMeta();
		zeroIM.setDisplayName(ChatColor.WHITE + "0");
		zeroBtn.setItemMeta(zeroIM);
		bankWithdrawInv.setItem(slot(5, 1), zeroBtn);
		bankWithdrawInv.setItem(slot(5, 2), zeroBtn);

		ItemStack oneBtn = BLANK.getItemStack().clone();
		ItemMeta oneIM = oneBtn.getItemMeta();
		oneIM.setDisplayName(ChatColor.WHITE + "1");
		oneBtn.setItemMeta(oneIM);
		bankWithdrawInv.setItem(slot(2, 1), oneBtn);

		ItemStack twoBtn = BLANK.getItemStack().clone();
		ItemMeta twoIM = twoBtn.getItemMeta();
		twoIM.setDisplayName(ChatColor.WHITE + "2");
		twoBtn.setItemMeta(twoIM);
		bankWithdrawInv.setItem(slot(2, 2), twoBtn);

		ItemStack threeBtn = BLANK.getItemStack().clone();
		ItemMeta threeIM = threeBtn.getItemMeta();
		threeIM.setDisplayName(ChatColor.WHITE + "3");
		threeBtn.setItemMeta(threeIM);
		bankWithdrawInv.setItem(slot(2, 3), threeBtn);

		ItemStack fourBtn = BLANK.getItemStack().clone();
		ItemMeta fourIM = fourBtn.getItemMeta();
		fourIM.setDisplayName(ChatColor.WHITE + "4");
		fourBtn.setItemMeta(fourIM);
		bankWithdrawInv.setItem(slot(3, 1), fourBtn);

		ItemStack fiveBtn = BLANK.getItemStack().clone();
		ItemMeta fiveIM = fiveBtn.getItemMeta();
		fiveIM.setDisplayName(ChatColor.WHITE + "5");
		fiveBtn.setItemMeta(fiveIM);
		bankWithdrawInv.setItem(slot(3, 2), fiveBtn);

		ItemStack sixBtn = BLANK.getItemStack().clone();
		ItemMeta sixIM = sixBtn.getItemMeta();
		sixIM.setDisplayName(ChatColor.WHITE + "6");
		sixBtn.setItemMeta(sixIM);
		bankWithdrawInv.setItem(slot(3, 3), sixBtn);

		ItemStack sevenBtn = BLANK.getItemStack().clone();
		ItemMeta sevenIM = sevenBtn.getItemMeta();
		sevenIM.setDisplayName(ChatColor.WHITE + "7");
		sevenBtn.setItemMeta(sevenIM);
		bankWithdrawInv.setItem(slot(4, 1), sevenBtn);

		ItemStack eightBtn = BLANK.getItemStack().clone();
		ItemMeta eightIM = eightBtn.getItemMeta();
		eightIM.setDisplayName(ChatColor.WHITE + "8");
		eightBtn.setItemMeta(eightIM);
		bankWithdrawInv.setItem(slot(4, 2), eightBtn);

		ItemStack nineBtn = BLANK.getItemStack().clone();
		ItemMeta nineIM = nineBtn.getItemMeta();
		nineIM.setDisplayName(ChatColor.WHITE + "9");
		nineBtn.setItemMeta(nineIM);
		bankWithdrawInv.setItem(slot(4, 3), nineBtn);

		ItemStack backBtn = BLANK.getItemStack().clone();
		ItemMeta backIM = backBtn.getItemMeta();
		backIM.setDisplayName(ChatColor.WHITE + "지우기");
		backBtn.setItemMeta(backIM);
		bankWithdrawInv.setItem(slot(5, 3), backBtn);

		ItemStack withdrawBtn = BLANK.getItemStack().clone();
		ItemMeta withdrawIM = withdrawBtn.getItemMeta();
		withdrawIM.setDisplayName(ChatColor.WHITE + "출금하기");
		withdrawBtn.setItemMeta(withdrawIM);
		bankWithdrawInv.setItem(slot(3, 5), withdrawBtn);
		bankWithdrawInv.setItem(slot(3, 6), withdrawBtn);
		bankWithdrawInv.setItem(slot(3, 7), withdrawBtn);

		return bankWithdrawInv;
	}

	public static Inventory getBankTaxInventory(Player player) {
		Inventory bankTaxInv = Bukkit.createInventory((InventoryHolder) player, 54,
				MenuController.getInventoryTitle(MenuUnicode.BANK_TAX.val()));

		return bankTaxInv;
	}
}
