package com.levelup.menu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.levelup.LevelUp;
import com.levelup.LevelUpIcon;
import com.levelup.money.MoneyController;
import com.levelup.player.PlayerData;
import com.levelup.tool.ToolAbstract;
import com.levelup.tool.ToolData;
import com.levelup.tool.ToolType;

import dev.lone.itemsadder.api.CustomStack;
import net.md_5.bungee.api.ChatColor;

public class MenuController {

	public static String getInventoryTitle(char code) {
		StringBuilder sb = new StringBuilder();
		sb.append(ChatColor.WHITE);
		sb.append(MenuUnicode.SPACE.val());
		sb.append(code);

		return sb.toString();
	}

	public static Inventory getMenuInventory(Player player) {
		Inventory menuInv = Bukkit.createInventory((InventoryHolder) player, 54,
				MenuController.getInventoryTitle(MenuUnicode.MENU.val()));

		ItemStack toolMenu = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta toolIM = toolMenu.getItemMeta();
		toolIM.setDisplayName(ChatColor.of("#d25656") + "도구");
		List<String> toolLore = new ArrayList<String>();
		toolLore.add(ChatColor.WHITE + "♦ 도구 스탯 변경");
		toolLore.add(ChatColor.WHITE + "♦ 도구 스킨 변경");
		toolIM.setLore(toolLore);
		toolMenu.setItemMeta(toolIM);
		menuInv.setItem(slot(0, 2), toolMenu);
		menuInv.setItem(slot(1, 2), toolMenu);

		ItemStack villageMenu = MenuIcon.BLANK.val().getItemStack().clone();
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

		ItemStack calendarMenu = MenuIcon.BLANK.val().getItemStack().clone();
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

		ItemStack friendMenu = MenuIcon.BLANK.val().getItemStack().clone();
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

		ItemStack warpMenu = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta warpIM = warpMenu.getItemMeta();
		warpIM.setDisplayName(ChatColor.of("#3395ff") + "열기구");
		List<String> warpLore = new ArrayList<String>();
		warpLore.add(ChatColor.WHITE + "♦ 광장 행");
		warpLore.add(ChatColor.WHITE + "♦ 야생 행");
		warpLore.add(ChatColor.WHITE + "♦ 지옥 행");
		warpLore.add(ChatColor.WHITE + "♦ 건축 행");
		warpIM.setLore(warpLore);
		warpMenu.setItemMeta(warpIM);
		menuInv.setItem(slot(4, 1), warpMenu);
		menuInv.setItem(slot(4, 2), warpMenu);
		menuInv.setItem(slot(5, 1), warpMenu);
		menuInv.setItem(slot(5, 2), warpMenu);

		ItemStack marketMenu = MenuIcon.BLANK.val().getItemStack().clone();
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

		ItemStack bankMenu = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta bankIM = bankMenu.getItemMeta();
		bankIM.setDisplayName(ChatColor.of("#f1c14c") + "은행");
		List<String> bankLore = new ArrayList<String>();
		bankLore.add(ChatColor.WHITE + "♦ 출금/입금");
		bankLore.add(ChatColor.WHITE + "♦ 세금 관리");
		bankIM.setLore(bankLore);
		bankMenu.setItemMeta(bankIM);
		menuInv.setItem(slot(4, 5), bankMenu);
		menuInv.setItem(slot(4, 6), bankMenu);

		ItemStack guideMenu = MenuIcon.BLANK.val().getItemStack().clone();
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

	/* Tool Inventory */
	public static Inventory getToolHomeInventory(LevelUp plugin, Player player) {
		Inventory toolHomeInv = Bukkit.createInventory((InventoryHolder) player, 54,
				MenuController.getInventoryTitle(MenuUnicode.TOOL_HOME.val()));

		ToolData toolData = plugin.tools.get(player.getUniqueId());
		LevelUpIcon pickaxeIcon = LevelUpIcon.valueOf(toolData.getPickaxe().getMaterial().toString());
		LevelUpIcon axeIcon = LevelUpIcon.valueOf(toolData.getAxe().getMaterial().toString());
		LevelUpIcon swordIcon = LevelUpIcon.valueOf(toolData.getSword().getMaterial().toString());
		LevelUpIcon shovelIcon = LevelUpIcon.valueOf(toolData.getShovel().getMaterial().toString());

		ItemStack pickaxeBtn = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta pickaxeIM = pickaxeBtn.getItemMeta();
		pickaxeIM.setDisplayName(ChatColor.WHITE + Character.toString(pickaxeIcon.val()) + "곡괭이");
		pickaxeBtn.setItemMeta(pickaxeIM);
		toolHomeInv.setItem(slot(0, 1), pickaxeBtn);
		toolHomeInv.setItem(slot(0, 2), pickaxeBtn);
		toolHomeInv.setItem(slot(0, 3), pickaxeBtn);
		toolHomeInv.setItem(slot(1, 1), pickaxeBtn);
		toolHomeInv.setItem(slot(1, 2), pickaxeBtn);
		toolHomeInv.setItem(slot(1, 3), pickaxeBtn);
		toolHomeInv.setItem(slot(2, 1), pickaxeBtn);
		toolHomeInv.setItem(slot(2, 2), pickaxeBtn);
		toolHomeInv.setItem(slot(2, 3), pickaxeBtn);

		ItemStack axeBtn = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta axeIM = axeBtn.getItemMeta();
		axeIM.setDisplayName(ChatColor.WHITE + Character.toString(axeIcon.val()) + "도끼");
		axeBtn.setItemMeta(axeIM);
		toolHomeInv.setItem(slot(3, 1), axeBtn);
		toolHomeInv.setItem(slot(3, 2), axeBtn);
		toolHomeInv.setItem(slot(3, 3), axeBtn);
		toolHomeInv.setItem(slot(4, 1), axeBtn);
		toolHomeInv.setItem(slot(4, 2), axeBtn);
		toolHomeInv.setItem(slot(4, 3), axeBtn);
		toolHomeInv.setItem(slot(5, 1), axeBtn);
		toolHomeInv.setItem(slot(5, 2), axeBtn);
		toolHomeInv.setItem(slot(5, 3), axeBtn);

		ItemStack swordBtn = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta swordIM = swordBtn.getItemMeta();
		swordIM.setDisplayName(ChatColor.WHITE + Character.toString(swordIcon.val()) + "검");
		swordBtn.setItemMeta(swordIM);
		toolHomeInv.setItem(slot(0, 5), swordBtn);
		toolHomeInv.setItem(slot(0, 6), swordBtn);
		toolHomeInv.setItem(slot(0, 7), swordBtn);
		toolHomeInv.setItem(slot(1, 5), swordBtn);
		toolHomeInv.setItem(slot(1, 6), swordBtn);
		toolHomeInv.setItem(slot(1, 7), swordBtn);
		toolHomeInv.setItem(slot(2, 5), swordBtn);
		toolHomeInv.setItem(slot(2, 6), swordBtn);
		toolHomeInv.setItem(slot(2, 7), swordBtn);

		ItemStack shovelBtn = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta shovelIM = shovelBtn.getItemMeta();
		shovelIM.setDisplayName(ChatColor.WHITE + Character.toString(shovelIcon.val()) + "삽");
		shovelBtn.setItemMeta(shovelIM);
		toolHomeInv.setItem(slot(3, 5), shovelBtn);
		toolHomeInv.setItem(slot(3, 6), shovelBtn);
		toolHomeInv.setItem(slot(3, 7), shovelBtn);
		toolHomeInv.setItem(slot(4, 5), shovelBtn);
		toolHomeInv.setItem(slot(4, 6), shovelBtn);
		toolHomeInv.setItem(slot(4, 7), shovelBtn);
		toolHomeInv.setItem(slot(5, 5), shovelBtn);
		toolHomeInv.setItem(slot(5, 6), shovelBtn);
		toolHomeInv.setItem(slot(5, 7), shovelBtn);
		
		ItemStack prev = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta prevIM = prev.getItemMeta();
		prevIM.setDisplayName(ChatColor.WHITE + "이전으로");
		prev.setItemMeta(prevIM);
		toolHomeInv.setItem(slot(5, 0), prev);

		return toolHomeInv;
	}

	public static Inventory getToolStatInventory(LevelUp plugin, Player player, ToolType type, int page) {
		MenuUnicode title = MenuUnicode.TOOL_STAT_1;

		if (type == ToolType.AXE && page == 1 || type == ToolType.SWORD && page == 0)
			title = MenuUnicode.TOOL_STAT_2;

		else if (type == ToolType.SWORD && page == 1)
			title = MenuUnicode.TOOL_STAT_3;

		Inventory toolStatInv = Bukkit.createInventory((InventoryHolder) player, 54,
				MenuController.getInventoryTitle(title.val()));

		ToolAbstract toolData = plugin.tools.get(player.getUniqueId()).getTool(type);

		ItemStack character = MenuIcon.valueOf(type.toString()).val().getItemStack();
		ItemMeta characterMeta = character.getItemMeta();
		characterMeta.setDisplayName(ChatColor.WHITE
				+ Character.toString(LevelUpIcon.valueOf(toolData.getMaterial().toString()).val()) + type.valueKor());
		character.setItemMeta(characterMeta);
		toolStatInv.setItem(slot(1, 1), character);

		ItemStack characterBlank = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta characterBlankMeta = characterBlank.getItemMeta();
		characterBlankMeta.setDisplayName(ChatColor.WHITE
				+ Character.toString(LevelUpIcon.valueOf(toolData.getMaterial().toString()).val()) + type.valueKor());
		characterBlank.setItemMeta(characterBlankMeta);
		toolStatInv.setItem(slot(0, 1), characterBlank);
		toolStatInv.setItem(slot(0, 2), characterBlank);
		toolStatInv.setItem(slot(1, 2), characterBlank);
		toolStatInv.setItem(slot(2, 1), characterBlank);
		toolStatInv.setItem(slot(2, 2), characterBlank);

		int totalStat = toolData.getTotalStat();
		int totalStatFirst = totalStat / 10;
		int totalStatSecond = totalStat % 10;

		toolStatInv.setItem(slot(3, 1), getNumberItem(totalStatFirst).getItemStack());
		toolStatInv.setItem(slot(3, 2), getNumberItem(totalStatSecond).getItemStack());

		ItemStack minus = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta minusIM = minus.getItemMeta();
		minusIM.setDisplayName(ChatColor.WHITE + "-");
		minus.setItemMeta(minusIM);

		ItemStack plus = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta plusIM = plus.getItemMeta();
		plusIM.setDisplayName(ChatColor.WHITE + "+");
		plus.setItemMeta(plusIM);

		if (title == MenuUnicode.TOOL_STAT_1) {
			ItemStack efficiency = MenuIcon.BLANK.val().getItemStack().clone();
			ItemMeta efficiencyIM = efficiency.getItemMeta();
			efficiencyIM.setDisplayName(ChatColor.WHITE + "효율");
			efficiency.setItemMeta(efficiencyIM);
			toolStatInv.setItem(slot(1, 5), efficiency);
			toolStatInv.setItem(slot(1, 6), minus);
			toolStatInv.setItem(slot(1, 7),
					getNumberItem(toolData.getEnchantLevel(Enchantment.DIG_SPEED)).getItemStack());
			toolStatInv.setItem(slot(1, 8), plus);

			ItemStack fortune = MenuIcon.BLANK.val().getItemStack().clone();
			ItemMeta fortuneIM = fortune.getItemMeta();
			fortuneIM.setDisplayName(ChatColor.WHITE + "행운");
			fortune.setItemMeta(fortuneIM);
			toolStatInv.setItem(slot(2, 5), fortune);
			toolStatInv.setItem(slot(2, 6), minus);
			toolStatInv.setItem(slot(2, 7),
					getNumberItem(toolData.getEnchantLevel(Enchantment.LOOT_BONUS_BLOCKS)).getItemStack());
			toolStatInv.setItem(slot(2, 8), plus);

			ItemStack silkTouch = MenuIcon.BLANK.val().getItemStack().clone();
			ItemMeta silkTouchIM = silkTouch.getItemMeta();
			silkTouchIM.setDisplayName(ChatColor.WHITE + "섬세한 손길");
			silkTouch.setItemMeta(silkTouchIM);
			toolStatInv.setItem(slot(3, 5), silkTouch);
			toolStatInv.setItem(slot(3, 6), minus);
			toolStatInv.setItem(slot(3, 7),
					getNumberItem(toolData.getEnchantLevel(Enchantment.SILK_TOUCH)).getItemStack());
			toolStatInv.setItem(slot(3, 8), plus);

		} else if (title == MenuUnicode.TOOL_STAT_2) {
			ItemStack sharpness = MenuIcon.BLANK.val().getItemStack().clone();
			ItemMeta sharpnessIM = sharpness.getItemMeta();
			sharpnessIM.setDisplayName(ChatColor.WHITE + "날카로움");
			sharpness.setItemMeta(sharpnessIM);
			toolStatInv.setItem(slot(1, 5), sharpness);
			toolStatInv.setItem(slot(1, 6), minus);
			toolStatInv.setItem(slot(1, 7),
					getNumberItem(toolData.getEnchantLevel(Enchantment.DAMAGE_ALL)).getItemStack());
			toolStatInv.setItem(slot(1, 8), plus);

			ItemStack smite = MenuIcon.BLANK.val().getItemStack().clone();
			ItemMeta smiteIM = smite.getItemMeta();
			smiteIM.setDisplayName(ChatColor.WHITE + "강타");
			smite.setItemMeta(smiteIM);
			toolStatInv.setItem(slot(2, 5), smite);
			toolStatInv.setItem(slot(2, 6), minus);
			toolStatInv.setItem(slot(2, 7),
					getNumberItem(toolData.getEnchantLevel(Enchantment.DAMAGE_UNDEAD)).getItemStack());
			toolStatInv.setItem(slot(2, 8), plus);

			ItemStack arthropods = MenuIcon.BLANK.val().getItemStack().clone();
			ItemMeta arthropodsIM = arthropods.getItemMeta();
			arthropodsIM.setDisplayName(ChatColor.WHITE + "살충");
			arthropods.setItemMeta(arthropodsIM);
			toolStatInv.setItem(slot(3, 5), arthropods);
			toolStatInv.setItem(slot(3, 6), minus);
			toolStatInv.setItem(slot(3, 7),
					getNumberItem(toolData.getEnchantLevel(Enchantment.DAMAGE_ARTHROPODS)).getItemStack());
			toolStatInv.setItem(slot(3, 8), plus);

		} else if (title == MenuUnicode.TOOL_STAT_3) {
			ItemStack looting = MenuIcon.BLANK.val().getItemStack().clone();
			ItemMeta lootingIM = looting.getItemMeta();
			lootingIM.setDisplayName(ChatColor.WHITE + "약탈");
			looting.setItemMeta(lootingIM);
			toolStatInv.setItem(slot(1, 5), looting);
			toolStatInv.setItem(slot(1, 6), minus);
			toolStatInv.setItem(slot(1, 7),
					getNumberItem(toolData.getEnchantLevel(Enchantment.LOOT_BONUS_MOBS)).getItemStack());
			toolStatInv.setItem(slot(1, 8), plus);

			ItemStack flame = MenuIcon.BLANK.val().getItemStack().clone();
			ItemMeta flameIM = flame.getItemMeta();
			flameIM.setDisplayName(ChatColor.WHITE + "발화");
			flame.setItemMeta(flameIM);
			toolStatInv.setItem(slot(2, 5), flame);
			toolStatInv.setItem(slot(2, 6), minus);
			toolStatInv.setItem(slot(2, 7),
					getNumberItem(toolData.getEnchantLevel(Enchantment.FIRE_ASPECT)).getItemStack());
			toolStatInv.setItem(slot(2, 8), plus);

			ItemStack knockback = MenuIcon.BLANK.val().getItemStack().clone();
			ItemMeta knockbackIM = knockback.getItemMeta();
			knockbackIM.setDisplayName(ChatColor.WHITE + "밀치기");
			knockback.setItemMeta(knockbackIM);
			toolStatInv.setItem(slot(3, 5), knockback);
			toolStatInv.setItem(slot(3, 6), minus);
			toolStatInv.setItem(slot(3, 7),
					getNumberItem(toolData.getEnchantLevel(Enchantment.KNOCKBACK)).getItemStack());
			toolStatInv.setItem(slot(3, 8), plus);

			ItemStack sweeping = MenuIcon.BLANK.val().getItemStack().clone();
			ItemMeta sweepingIM = sweeping.getItemMeta();
			sweepingIM.setDisplayName(ChatColor.WHITE + "휩쓸기");
			sweeping.setItemMeta(sweepingIM);
			toolStatInv.setItem(slot(4, 5), sweeping);
			toolStatInv.setItem(slot(4, 6), minus);
			toolStatInv.setItem(slot(4, 7),
					getNumberItem(toolData.getEnchantLevel(Enchantment.SWEEPING_EDGE)).getItemStack());
			toolStatInv.setItem(slot(4, 8), plus);
		}
		
		ItemStack ok = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta okIM = ok.getItemMeta();
		okIM.setDisplayName(ChatColor.of("#9787A8") + "적용하기");
		ok.setItemMeta(okIM);
		toolStatInv.setItem(slot(4, 1), ok);
		toolStatInv.setItem(slot(4, 2), ok);

		ItemStack prev = MenuIcon.PREV.val().getItemStack().clone();
		ItemMeta prevIM = prev.getItemMeta();
		prevIM.setDisplayName(ChatColor.WHITE + "이전으로");
		prev.setItemMeta(prevIM);
		toolStatInv.setItem(slot(5, 0), prev);

		if ((type == ToolType.AXE || type == ToolType.SWORD) && page == 0) {
			ItemStack next = MenuIcon.NEXT.val().getItemStack().clone();
			ItemMeta nextIM = next.getItemMeta();
			nextIM.setDisplayName(ChatColor.WHITE + "다음으로");
			next.setItemMeta(nextIM);
			toolStatInv.setItem(slot(5, 8), next);
		}

		return toolStatInv;
	}

	public static Inventory getToolSkinInventory(LevelUp plugin, Player player) {
		Inventory toolSkinInv = Bukkit.createInventory((InventoryHolder) player, 54,
				MenuController.getInventoryTitle(MenuUnicode.TOOL_HOME.val()));

		return toolSkinInv;
	}

	/* Warp Inventory */
	public static Inventory getWarpHomeInventory(LevelUp plugin, Player player) {
		Inventory warpHomeInv = Bukkit.createInventory((InventoryHolder) player, 54,
				MenuController.getInventoryTitle(MenuUnicode.WARP_HOME.val()));
		
		ItemStack spawnBtn = new ItemStack(Material.STONE_BRICKS);
//		ItemStack spawnBtn = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta spawnIM = spawnBtn.getItemMeta();
		spawnIM.setDisplayName(ChatColor.WHITE + "광장 행");
		spawnBtn.setItemMeta(spawnIM);
		warpHomeInv.setItem(MenuController.slot(3, 1), spawnBtn);

		ItemStack wildBtn = new ItemStack(Material.GRASS_BLOCK);
//		ItemStack wildBtn = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta wildIM = wildBtn.getItemMeta();
		wildIM.setDisplayName(ChatColor.WHITE + "야생 행");
		wildBtn.setItemMeta(wildIM);
		warpHomeInv.setItem(MenuController.slot(3, 3), wildBtn);

		ItemStack worldBtn = new ItemStack(Material.BRICKS);
//		ItemStack worldBtn = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta worldIM = worldBtn.getItemMeta();
		worldIM.setDisplayName(ChatColor.WHITE + "건축 행");
		worldBtn.setItemMeta(worldIM);
		warpHomeInv.setItem(MenuController.slot(3, 5), worldBtn);

		ItemStack netherBtn = new ItemStack(Material.NETHERRACK);
//		ItemStack netherBtn = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta netherIM = netherBtn.getItemMeta();
		netherIM.setDisplayName(ChatColor.WHITE + "지옥 행");
		netherBtn.setItemMeta(netherIM);
		warpHomeInv.setItem(MenuController.slot(3, 7), netherBtn);
		
		return warpHomeInv;
	}
	
	/* Bank Inventory */
	public static Inventory getBankHomeInventory(LevelUp plugin, Player player) {
		Inventory bankHomeInv = Bukkit.createInventory((InventoryHolder) player, 54,
				MenuController.getInventoryTitle(MenuUnicode.BANK_HOME.val()));

		PlayerData pd = plugin.players.get(player.getUniqueId());
		String balance = Integer.toString(pd.getBalance());
		if (balance.length() > 6) {
			ItemStack nine = getNumberItem(9).getItemStack();
			for (int i = 1; i < 7; i++) {
				bankHomeInv.setItem(MenuController.slot(1, i), nine.clone());
			}

		} else {
			for (int i = 0; i < balance.length(); i++) {
				int index = i + (7 - balance.length());
				int num = Character.getNumericValue(balance.charAt(i));
				bankHomeInv.setItem(MenuController.slot(1, index), getNumberItem(num).getItemStack().clone());
			}
		}

		ItemStack coin = MoneyController.GOLD.getItemStack().clone();
		ItemMeta coinMeta = coin.getItemMeta();
		coinMeta.setDisplayName(ChatColor.GOLD + "개굴 코인");
		coin.setItemMeta(coinMeta);
		bankHomeInv.setItem(MenuController.slot(1, 7), coin);

		ItemStack depositMenu = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta depositIM = depositMenu.getItemMeta();
		depositIM.setDisplayName(ChatColor.of("#8AC687") + "입금");
		depositMenu.setItemMeta(depositIM);
		bankHomeInv.setItem(MenuController.slot(4, 0), depositMenu);
		bankHomeInv.setItem(MenuController.slot(4, 1), depositMenu);
		bankHomeInv.setItem(MenuController.slot(4, 2), depositMenu);

		ItemStack withdrawMenu = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta withdrawIM = withdrawMenu.getItemMeta();
		withdrawIM.setDisplayName(ChatColor.of("#8AC687") + "출금");
		withdrawMenu.setItemMeta(withdrawIM);
		bankHomeInv.setItem(MenuController.slot(4, 3), withdrawMenu);
		bankHomeInv.setItem(MenuController.slot(4, 4), withdrawMenu);
		bankHomeInv.setItem(MenuController.slot(4, 5), withdrawMenu);

		ItemStack taxMenu = MenuIcon.BLANK.val().getItemStack().clone();
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

		ItemStack depositBtn = MenuIcon.BLANK.val().getItemStack().clone();
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

		ItemStack zeroBtn = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta zeroIM = zeroBtn.getItemMeta();
		zeroIM.setDisplayName(ChatColor.WHITE + "0");
		zeroBtn.setItemMeta(zeroIM);
		bankWithdrawInv.setItem(MenuController.slot(5, 1), zeroBtn);
		bankWithdrawInv.setItem(MenuController.slot(5, 2), zeroBtn);

		ItemStack oneBtn = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta oneIM = oneBtn.getItemMeta();
		oneIM.setDisplayName(ChatColor.WHITE + "1");
		oneBtn.setItemMeta(oneIM);
		bankWithdrawInv.setItem(MenuController.slot(2, 1), oneBtn);

		ItemStack twoBtn = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta twoIM = twoBtn.getItemMeta();
		twoIM.setDisplayName(ChatColor.WHITE + "2");
		twoBtn.setItemMeta(twoIM);
		bankWithdrawInv.setItem(MenuController.slot(2, 2), twoBtn);

		ItemStack threeBtn = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta threeIM = threeBtn.getItemMeta();
		threeIM.setDisplayName(ChatColor.WHITE + "3");
		threeBtn.setItemMeta(threeIM);
		bankWithdrawInv.setItem(MenuController.slot(2, 3), threeBtn);

		ItemStack fourBtn = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta fourIM = fourBtn.getItemMeta();
		fourIM.setDisplayName(ChatColor.WHITE + "4");
		fourBtn.setItemMeta(fourIM);
		bankWithdrawInv.setItem(MenuController.slot(3, 1), fourBtn);

		ItemStack fiveBtn = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta fiveIM = fiveBtn.getItemMeta();
		fiveIM.setDisplayName(ChatColor.WHITE + "5");
		fiveBtn.setItemMeta(fiveIM);
		bankWithdrawInv.setItem(MenuController.slot(3, 2), fiveBtn);

		ItemStack sixBtn = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta sixIM = sixBtn.getItemMeta();
		sixIM.setDisplayName(ChatColor.WHITE + "6");
		sixBtn.setItemMeta(sixIM);
		bankWithdrawInv.setItem(MenuController.slot(3, 3), sixBtn);

		ItemStack sevenBtn = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta sevenIM = sevenBtn.getItemMeta();
		sevenIM.setDisplayName(ChatColor.WHITE + "7");
		sevenBtn.setItemMeta(sevenIM);
		bankWithdrawInv.setItem(MenuController.slot(4, 1), sevenBtn);

		ItemStack eightBtn = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta eightIM = eightBtn.getItemMeta();
		eightIM.setDisplayName(ChatColor.WHITE + "8");
		eightBtn.setItemMeta(eightIM);
		bankWithdrawInv.setItem(MenuController.slot(4, 2), eightBtn);

		ItemStack nineBtn = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta nineIM = nineBtn.getItemMeta();
		nineIM.setDisplayName(ChatColor.WHITE + "9");
		nineBtn.setItemMeta(nineIM);
		bankWithdrawInv.setItem(MenuController.slot(4, 3), nineBtn);

		ItemStack backBtn = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta backIM = backBtn.getItemMeta();
		backIM.setDisplayName(ChatColor.WHITE + "지우기");
		backBtn.setItemMeta(backIM);
		bankWithdrawInv.setItem(MenuController.slot(5, 3), backBtn);

		ItemStack withdrawBtn = MenuIcon.BLANK.val().getItemStack().clone();
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

	public static int slot(int row, int col) {
		return row * 9 + col;
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

	public static int getNumber(CustomStack item) {
		if (item != null) {
			if (item.getNamespacedID().equals(MenuIcon.ZERO.val().getNamespacedID())) {
				return 0;

			} else if (item.getNamespacedID().equals(MenuIcon.ONE.val().getNamespacedID())) {
				return 1;

			} else if (item.getNamespacedID().equals(MenuIcon.TWO.val().getNamespacedID())) {
				return 2;

			} else if (item.getNamespacedID().equals(MenuIcon.THREE.val().getNamespacedID())) {
				return 3;

			} else if (item.getNamespacedID().equals(MenuIcon.FOUR.val().getNamespacedID())) {
				return 4;

			} else if (item.getNamespacedID().equals(MenuIcon.FIVE.val().getNamespacedID())) {
				return 5;

			} else if (item.getNamespacedID().equals(MenuIcon.SIX.val().getNamespacedID())) {
				return 6;

			} else if (item.getNamespacedID().equals(MenuIcon.SEVEN.val().getNamespacedID())) {
				return 7;

			} else if (item.getNamespacedID().equals(MenuIcon.EIGHT.val().getNamespacedID())) {
				return 8;

			} else if (item.getNamespacedID().equals(MenuIcon.NINE.val().getNamespacedID())) {
				return 9;

			} else {
				return -1;

			}
		} else {

			return -1;
		}
	}
	
	public static Enchantment getEnchant(ItemStack item) {
		if (item.getItemMeta().getDisplayName().contains("효율"))
			return Enchantment.DIG_SPEED;

		else if (item.getItemMeta().getDisplayName().contains("행운"))
			return Enchantment.LOOT_BONUS_BLOCKS;

		else if (item.getItemMeta().getDisplayName().contains("섬세한 손길"))
			return Enchantment.SILK_TOUCH;

		else if (item.getItemMeta().getDisplayName().contains("날카로움"))
			return Enchantment.DAMAGE_ALL;

		else if (item.getItemMeta().getDisplayName().contains("강타"))
			return Enchantment.DAMAGE_UNDEAD;

		else if (item.getItemMeta().getDisplayName().contains("살충"))
			return Enchantment.DAMAGE_ARTHROPODS;

		else if (item.getItemMeta().getDisplayName().contains("약탈"))
			return Enchantment.LOOT_BONUS_MOBS;

		else if (item.getItemMeta().getDisplayName().contains("발화"))
			return Enchantment.FIRE_ASPECT;

		else if (item.getItemMeta().getDisplayName().contains("밀치기"))
			return Enchantment.KNOCKBACK;

		else if (item.getItemMeta().getDisplayName().contains("휩쓸기"))
			return Enchantment.SWEEPING_EDGE;
		
		return null;
	}

}
