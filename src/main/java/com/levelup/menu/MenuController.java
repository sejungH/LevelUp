package com.levelup.menu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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


}
