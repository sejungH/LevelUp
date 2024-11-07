package com.levelup.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import com.google.gson.JsonArray;
import com.levelup.LevelUp;
import com.levelup.LevelUpIcon;
import com.levelup.LevelUpItem;
import com.levelup.friend.FriendData;
import com.levelup.money.MoneyController;
import com.levelup.money.MoneyController.MoneyItem;
import com.levelup.player.PlayerData;
import com.levelup.seasonpass.SeasonPassController;
import com.levelup.seasonpass.SeasonPassController.SeasonPass;
import com.levelup.tool.ToolAbstract;
import com.levelup.tool.ToolData;
import com.levelup.tool.ToolType;
import com.levelup.village.VillageData;

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
		Map<Material, Integer> pickaxeQuest = plugin.quests.get(player.getUniqueId()).get(ToolType.PICKAXE);
		if (!pickaxeQuest.isEmpty()) {
			List<String> pickaxeLore = new ArrayList<String>();
			int count = 0;
			for (Entry<Material, Integer> q : pickaxeQuest.entrySet()) {
				Entry<Character, String> item = plugin.toolQuestItems.get(q.getKey());
				String amount;
				if (q.getValue() >= 64) {
					amount = String.valueOf((int) (q.getValue() / 64)) + "세트";
				} else {
					amount = String.valueOf(q.getValue()) + '개';
				}
				if (count % 4 == 0) {
					pickaxeLore.add(ChatColor.WHITE + Character.toString(item.getKey()) + " " + ChatColor.GRAY
							+ item.getValue() + " " + amount);
				} else {
					int index = count / 4;
					pickaxeLore.set(index,
							pickaxeLore.get(index) + ", " + ChatColor.WHITE + Character.toString(item.getKey()) + " "
									+ ChatColor.GRAY + item.getValue() + " " + amount);
				}
				count++;
			}
			pickaxeIM.setLore(pickaxeLore);
		}
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
			Enchantment enchant = Enchantment.DIG_SPEED;
			int level = toolData.getEnchantLevel(enchant);
			efficiencyIM.setDisplayName(ChatColor.WHITE + "효율");
			efficiency.setItemMeta(efficiencyIM);
			toolStatInv.setItem(slot(1, 5), efficiency);
			toolStatInv.setItem(slot(1, 6), minus);
			toolStatInv.setItem(slot(1, 7), getNumberItem(level).getItemStack());
			if (level == toolData.getEnchantLimit(enchant)) {
				List<String> lore = Arrays.asList(ChatColor.RED + "업그레이드 할 수 있는 한계에 도달했습니다");
				ItemMeta newPlusIM = plus.getItemMeta();
				newPlusIM.setLore(lore);
				plus.setItemMeta(newPlusIM);
			}
			toolStatInv.setItem(slot(1, 8), plus);

			ItemStack fortune = MenuIcon.BLANK.val().getItemStack().clone();
			ItemMeta fortuneIM = fortune.getItemMeta();
			enchant = Enchantment.LOOT_BONUS_BLOCKS;
			level = toolData.getEnchantLevel(enchant);
			fortuneIM.setDisplayName(ChatColor.WHITE + "행운");
			fortune.setItemMeta(fortuneIM);
			toolStatInv.setItem(slot(2, 5), fortune);
			toolStatInv.setItem(slot(2, 6), minus);
			toolStatInv.setItem(slot(2, 7), getNumberItem(level).getItemStack());
			if (level == toolData.getEnchantLimit(enchant)) {
				List<String> lore = Arrays.asList(ChatColor.RED + "업그레이드 할 수 있는 한계에 도달했습니다");
				ItemMeta newPlusIM = plus.getItemMeta();
				newPlusIM.setLore(lore);
				plus.setItemMeta(newPlusIM);
			}
			plus.setItemMeta(plusIM);

			toolStatInv.setItem(slot(2, 8), plus);

			ItemStack silkTouch = MenuIcon.BLANK.val().getItemStack().clone();
			ItemMeta silkTouchIM = silkTouch.getItemMeta();
			enchant = Enchantment.SILK_TOUCH;
			level = toolData.getEnchantLevel(enchant);
			silkTouchIM.setDisplayName(ChatColor.WHITE + "섬세한 손길");
			silkTouch.setItemMeta(silkTouchIM);
			toolStatInv.setItem(slot(3, 5), silkTouch);
			toolStatInv.setItem(slot(3, 6), minus);
			toolStatInv.setItem(slot(3, 7), getNumberItem(toolData.getEnchantLevel(enchant)).getItemStack());
			if (level == toolData.getEnchantLimit(enchant)) {
				List<String> lore = Arrays.asList(ChatColor.RED + "업그레이드 할 수 있는 한계에 도달했습니다");
				ItemMeta newPlusIM = plus.getItemMeta();
				newPlusIM.setLore(lore);
				plus.setItemMeta(newPlusIM);
			}
			toolStatInv.setItem(slot(3, 8), plus);

		} else if (title == MenuUnicode.TOOL_STAT_2) {
			ItemStack sharpness = MenuIcon.BLANK.val().getItemStack().clone();
			ItemMeta sharpnessIM = sharpness.getItemMeta();
			Enchantment enchant = Enchantment.DAMAGE_ALL;
			int level = toolData.getEnchantLevel(enchant);
			sharpnessIM.setDisplayName(ChatColor.WHITE + "날카로움");
			sharpness.setItemMeta(sharpnessIM);
			toolStatInv.setItem(slot(1, 5), sharpness);
			toolStatInv.setItem(slot(1, 6), minus);
			toolStatInv.setItem(slot(1, 7), getNumberItem(toolData.getEnchantLevel(enchant)).getItemStack());
			if (level == toolData.getEnchantLimit(enchant)) {
				List<String> lore = Arrays.asList(ChatColor.RED + "업그레이드 할 수 있는 한계에 도달했습니다");
				ItemMeta newPlusIM = plus.getItemMeta();
				newPlusIM.setLore(lore);
				plus.setItemMeta(newPlusIM);
			}
			toolStatInv.setItem(slot(1, 8), plus);

			ItemStack smite = MenuIcon.BLANK.val().getItemStack().clone();
			ItemMeta smiteIM = smite.getItemMeta();
			enchant = Enchantment.DAMAGE_UNDEAD;
			level = toolData.getEnchantLevel(enchant);
			smiteIM.setDisplayName(ChatColor.WHITE + "강타");
			smite.setItemMeta(smiteIM);
			toolStatInv.setItem(slot(2, 5), smite);
			toolStatInv.setItem(slot(2, 6), minus);
			toolStatInv.setItem(slot(2, 7), getNumberItem(toolData.getEnchantLevel(enchant)).getItemStack());
			if (level == toolData.getEnchantLimit(enchant)) {
				List<String> lore = Arrays.asList(ChatColor.RED + "업그레이드 할 수 있는 한계에 도달했습니다");
				ItemMeta newPlusIM = plus.getItemMeta();
				newPlusIM.setLore(lore);
				plus.setItemMeta(newPlusIM);
			}
			toolStatInv.setItem(slot(2, 8), plus);

			ItemStack arthropods = MenuIcon.BLANK.val().getItemStack().clone();
			ItemMeta arthropodsIM = arthropods.getItemMeta();
			enchant = Enchantment.DAMAGE_ARTHROPODS;
			level = toolData.getEnchantLevel(enchant);
			arthropodsIM.setDisplayName(ChatColor.WHITE + "살충");
			arthropods.setItemMeta(arthropodsIM);
			toolStatInv.setItem(slot(3, 5), arthropods);
			toolStatInv.setItem(slot(3, 6), minus);
			toolStatInv.setItem(slot(3, 7), getNumberItem(toolData.getEnchantLevel(enchant)).getItemStack());
			if (level == toolData.getEnchantLimit(enchant)) {
				List<String> lore = Arrays.asList(ChatColor.RED + "업그레이드 할 수 있는 한계에 도달했습니다");
				ItemMeta newPlusIM = plus.getItemMeta();
				newPlusIM.setLore(lore);
				plus.setItemMeta(newPlusIM);
			}
			toolStatInv.setItem(slot(3, 8), plus);

		} else if (title == MenuUnicode.TOOL_STAT_3) {
			ItemStack looting = MenuIcon.BLANK.val().getItemStack().clone();
			ItemMeta lootingIM = looting.getItemMeta();
			Enchantment enchant = Enchantment.LOOT_BONUS_MOBS;
			int level = toolData.getEnchantLevel(enchant);
			lootingIM.setDisplayName(ChatColor.WHITE + "약탈");
			looting.setItemMeta(lootingIM);
			toolStatInv.setItem(slot(1, 5), looting);
			toolStatInv.setItem(slot(1, 6), minus);
			toolStatInv.setItem(slot(1, 7), getNumberItem(toolData.getEnchantLevel(enchant)).getItemStack());
			if (level == toolData.getEnchantLimit(enchant)) {
				List<String> lore = Arrays.asList(ChatColor.RED + "업그레이드 할 수 있는 한계에 도달했습니다");
				ItemMeta newPlusIM = plus.getItemMeta();
				newPlusIM.setLore(lore);
				plus.setItemMeta(newPlusIM);
			}
			toolStatInv.setItem(slot(1, 8), plus);

			ItemStack flame = MenuIcon.BLANK.val().getItemStack().clone();
			ItemMeta flameIM = flame.getItemMeta();
			enchant = Enchantment.FIRE_ASPECT;
			level = toolData.getEnchantLevel(enchant);
			flameIM.setDisplayName(ChatColor.WHITE + "발화");
			flame.setItemMeta(flameIM);
			toolStatInv.setItem(slot(2, 5), flame);
			toolStatInv.setItem(slot(2, 6), minus);
			toolStatInv.setItem(slot(2, 7), getNumberItem(toolData.getEnchantLevel(enchant)).getItemStack());
			if (level == toolData.getEnchantLimit(enchant)) {
				List<String> lore = Arrays.asList(ChatColor.RED + "업그레이드 할 수 있는 한계에 도달했습니다");
				ItemMeta newPlusIM = plus.getItemMeta();
				newPlusIM.setLore(lore);
				plus.setItemMeta(newPlusIM);
			}
			toolStatInv.setItem(slot(2, 8), plus);

			ItemStack knockback = MenuIcon.BLANK.val().getItemStack().clone();
			ItemMeta knockbackIM = knockback.getItemMeta();
			enchant = Enchantment.KNOCKBACK;
			level = toolData.getEnchantLevel(enchant);
			knockbackIM.setDisplayName(ChatColor.WHITE + "밀치기");
			knockback.setItemMeta(knockbackIM);
			toolStatInv.setItem(slot(3, 5), knockback);
			toolStatInv.setItem(slot(3, 6), minus);
			toolStatInv.setItem(slot(3, 7), getNumberItem(toolData.getEnchantLevel(enchant)).getItemStack());
			if (level == toolData.getEnchantLimit(enchant)) {
				List<String> lore = Arrays.asList(ChatColor.RED + "업그레이드 할 수 있는 한계에 도달했습니다");
				ItemMeta newPlusIM = plus.getItemMeta();
				newPlusIM.setLore(lore);
				plus.setItemMeta(newPlusIM);
			}
			toolStatInv.setItem(slot(3, 8), plus);

			ItemStack sweeping = MenuIcon.BLANK.val().getItemStack().clone();
			ItemMeta sweepingIM = sweeping.getItemMeta();
			enchant = Enchantment.SWEEPING_EDGE;
			level = toolData.getEnchantLevel(enchant);
			sweepingIM.setDisplayName(ChatColor.WHITE + "휩쓸기");
			sweeping.setItemMeta(sweepingIM);
			toolStatInv.setItem(slot(4, 5), sweeping);
			toolStatInv.setItem(slot(4, 6), minus);
			toolStatInv.setItem(slot(4, 7), getNumberItem(toolData.getEnchantLevel(enchant)).getItemStack());
			if (level == toolData.getEnchantLimit(enchant)) {
				List<String> lore = Arrays.asList(ChatColor.RED + "업그레이드 할 수 있는 한계에 도달했습니다");
				ItemMeta newPlusIM = plus.getItemMeta();
				newPlusIM.setLore(lore);
				plus.setItemMeta(newPlusIM);
			}
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

	/* Bank Inventory */
	public static Inventory getBankHomeInventory(Player player) {
		Inventory bankHomeInv = Bukkit.createInventory((InventoryHolder) player, 54,
				MenuController.getInventoryTitle(MenuUnicode.BANK_HOME.val()));

		ItemStack prev = MenuIcon.PREV.val().getItemStack().clone();
		ItemMeta prevIM = prev.getItemMeta();
		prevIM.setDisplayName(ChatColor.WHITE + "이전으로");
		prev.setItemMeta(prevIM);
		bankHomeInv.setItem(slot(5, 0), prev);

		return bankHomeInv;
	}

	public static Inventory getBankAccountInventory(Player player) {
		Inventory bankAcountInv = Bukkit.createInventory((InventoryHolder) player, 54,
				MenuController.getInventoryTitle(MenuUnicode.BANK_ACCOUNT.val()));

		ItemStack withdrawBtn = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta withdrawIM = withdrawBtn.getItemMeta();
		withdrawIM.setDisplayName(ChatColor.of("#BF9669") + "출금할래");
		withdrawBtn.setItemMeta(withdrawIM);
		bankAcountInv.setItem(MenuController.slot(1, 0), withdrawBtn);
		bankAcountInv.setItem(MenuController.slot(1, 1), withdrawBtn);
		bankAcountInv.setItem(MenuController.slot(1, 2), withdrawBtn);
		bankAcountInv.setItem(MenuController.slot(1, 3), withdrawBtn);
		bankAcountInv.setItem(MenuController.slot(1, 4), withdrawBtn);

		ItemStack depositBtn = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta depositIM = depositBtn.getItemMeta();
		depositIM.setDisplayName(ChatColor.of("#BF9669") + "입금할래");
		depositBtn.setItemMeta(depositIM);
		bankAcountInv.setItem(MenuController.slot(2, 0), depositBtn);
		bankAcountInv.setItem(MenuController.slot(2, 1), depositBtn);
		bankAcountInv.setItem(MenuController.slot(2, 2), depositBtn);
		bankAcountInv.setItem(MenuController.slot(2, 3), depositBtn);
		bankAcountInv.setItem(MenuController.slot(2, 4), depositBtn);

		ItemStack nothingBtn = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta nothingIM = nothingBtn.getItemMeta();
		nothingIM.setDisplayName(ChatColor.of("#BF9669") + "아무것도아냐");
		nothingBtn.setItemMeta(nothingIM);
		bankAcountInv.setItem(MenuController.slot(3, 0), nothingBtn);
		bankAcountInv.setItem(MenuController.slot(3, 1), nothingBtn);
		bankAcountInv.setItem(MenuController.slot(3, 2), nothingBtn);
		bankAcountInv.setItem(MenuController.slot(3, 3), nothingBtn);
		bankAcountInv.setItem(MenuController.slot(3, 4), nothingBtn);

		ItemStack prev = MenuIcon.PREV.val().getItemStack().clone();
		ItemMeta prevIM = prev.getItemMeta();
		prevIM.setDisplayName(ChatColor.WHITE + "이전으로");
		prev.setItemMeta(prevIM);
		bankAcountInv.setItem(slot(5, 0), prev);

		return bankAcountInv;
	}

	public static Inventory getBankTaxInventory(LevelUp plugin, Player player) {
		Inventory bankTaxInv = Bukkit.createInventory((InventoryHolder) player, 54,
				MenuController.getInventoryTitle(MenuUnicode.BANK_TAX.val()));

		PlayerData pd = plugin.players.get(player.getUniqueId());
		int villageId = pd.getVillage();

		int villageTax = 0;
		if (villageId > 0) {
			VillageData vd = plugin.villages.get(villageId);
			villageTax = vd.getLastTax();
		}

		int marketTax = 0;
		// to do

		ItemStack marketTaxBtn = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta marketTaxIM = marketTaxBtn.getItemMeta();
		marketTaxIM.setDisplayName(ChatColor.of("#BF9669") + "마켓세금");
		List<String> marketTaxLore = new ArrayList<String>();
		if (marketTax == 0) {
			marketTaxLore.add(ChatColor.WHITE + "이미 모든 세금을 납부하셨거나,");
			marketTaxLore.add(ChatColor.WHITE + "추가로 납부할 세금이 없습니다");

		} else {
			marketTaxLore.add(ChatColor.WHITE + "총 " + LevelUpIcon.COIN + " " + marketTax + " 코인을 지불합니다");
			if (pd.getBalance() < marketTax)
				marketTaxLore.add(ChatColor.RED + "보유한 자금이 부족합니다");
		}
		marketTaxIM.setLore(marketTaxLore);
		marketTaxBtn.setItemMeta(marketTaxIM);
		bankTaxInv.setItem(MenuController.slot(2, 4), marketTaxBtn);
		bankTaxInv.setItem(MenuController.slot(2, 5), marketTaxBtn);
		bankTaxInv.setItem(MenuController.slot(2, 6), marketTaxBtn);
		bankTaxInv.setItem(MenuController.slot(2, 7), marketTaxBtn);
		bankTaxInv.setItem(MenuController.slot(2, 8), marketTaxBtn);

		ItemStack villageTaxBtn = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta villageTaxIM = villageTaxBtn.getItemMeta();
		villageTaxIM.setDisplayName(ChatColor.of("#BF9669") + "마을세금");
		List<String> villageTaxLore = new ArrayList<String>();
		if (villageId > 0) {
			if (villageTax == 0) {
				villageTaxLore.add(ChatColor.WHITE + "이미 모든 세금을 납부하셨거나,");
				villageTaxLore.add(ChatColor.WHITE + "추가로 납부할 세금이 없습니다");
			} else {
				villageTaxLore.add(ChatColor.WHITE + "총 " + LevelUpIcon.COIN + " " + villageTax + " 코인을 지불합니다");
				if (pd.getBalance() < villageTax)
					villageTaxLore.add(ChatColor.RED + "보유한 자금이 부족합니다");
			}
		} else {
			villageTaxLore.add(ChatColor.WHITE + "가입된 마을이 없습니다");
		}
		villageTaxIM.setLore(villageTaxLore);
		villageTaxBtn.setItemMeta(villageTaxIM);
		bankTaxInv.setItem(MenuController.slot(1, 4), villageTaxBtn);
		bankTaxInv.setItem(MenuController.slot(1, 5), villageTaxBtn);
		bankTaxInv.setItem(MenuController.slot(1, 6), villageTaxBtn);
		bankTaxInv.setItem(MenuController.slot(1, 7), villageTaxBtn);
		bankTaxInv.setItem(MenuController.slot(1, 8), villageTaxBtn);

		ItemStack nothingBtn = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta nothingIM = nothingBtn.getItemMeta();
		nothingIM.setDisplayName(ChatColor.of("#BF9669") + "아무것도아냐");
		nothingBtn.setItemMeta(nothingIM);
		bankTaxInv.setItem(MenuController.slot(3, 4), nothingBtn);
		bankTaxInv.setItem(MenuController.slot(3, 5), nothingBtn);
		bankTaxInv.setItem(MenuController.slot(3, 6), nothingBtn);
		bankTaxInv.setItem(MenuController.slot(3, 7), nothingBtn);
		bankTaxInv.setItem(MenuController.slot(3, 8), nothingBtn);

		ItemStack prev = MenuIcon.PREV.val().getItemStack().clone();
		ItemMeta prevIM = prev.getItemMeta();
		prevIM.setDisplayName(ChatColor.WHITE + "이전으로");
		prev.setItemMeta(prevIM);
		bankTaxInv.setItem(slot(5, 0), prev);

		return bankTaxInv;
	}

	public static Inventory getBankDepositInventory(Player player) {
		Inventory bankDepositInv = Bukkit.createInventory((InventoryHolder) player, 54,
				MenuController.getInventoryTitle(MenuUnicode.BANK_DEPOSIT.val()));

		ItemStack depositBtn = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta depositIM = depositBtn.getItemMeta();
		depositIM.setDisplayName(ChatColor.of("#8AC687") + "입금");
		depositBtn.setItemMeta(depositIM);
		bankDepositInv.setItem(MenuController.slot(4, 3), depositBtn);
		bankDepositInv.setItem(MenuController.slot(4, 4), depositBtn);
		bankDepositInv.setItem(MenuController.slot(4, 5), depositBtn);

		ItemStack prev = MenuIcon.PREV.val().getItemStack().clone();
		ItemMeta prevIM = prev.getItemMeta();
		prevIM.setDisplayName(ChatColor.WHITE + "이전으로");
		prev.setItemMeta(prevIM);
		bankDepositInv.setItem(slot(5, 0), prev);

		MoneyController.updateDepositLore(bankDepositInv);

		return bankDepositInv;
	}

	public static Inventory getBankWithdrawInventory(Player player) {
		Inventory bankWithdrawInv = Bukkit.createInventory((InventoryHolder) player, 54,
				MenuController.getInventoryTitle(MenuUnicode.BANK_WITHDRAW.val()));

		ItemStack coin = new ItemStack(MoneyItem.GOLD.getItemStack());
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
		withdrawIM.setDisplayName(ChatColor.of("#8AC687") + "출금");
		List<String> itemLore = new ArrayList<String>();
		itemLore.add(ChatColor.WHITE + "총 0 코인을 출금합니다");
		withdrawIM.setLore(itemLore);
		withdrawBtn.setItemMeta(withdrawIM);
		bankWithdrawInv.setItem(MenuController.slot(3, 5), withdrawBtn);
		bankWithdrawInv.setItem(MenuController.slot(3, 6), withdrawBtn);
		bankWithdrawInv.setItem(MenuController.slot(3, 7), withdrawBtn);
		bankWithdrawInv.setItem(MenuController.slot(4, 5), withdrawBtn);
		bankWithdrawInv.setItem(MenuController.slot(4, 6), withdrawBtn);
		bankWithdrawInv.setItem(MenuController.slot(4, 7), withdrawBtn);

		ItemStack prev = MenuIcon.PREV.val().getItemStack().clone();
		ItemMeta prevIM = prev.getItemMeta();
		prevIM.setDisplayName(ChatColor.WHITE + "이전으로");
		prev.setItemMeta(prevIM);
		bankWithdrawInv.setItem(slot(5, 0), prev);

		return bankWithdrawInv;
	}

	/* Village Inventory */
	public static Inventory getVillageHomeInventory(LevelUp plugin, Player player) {
		Inventory villageHomeInv = Bukkit.createInventory((InventoryHolder) player, 54,
				MenuController.getInventoryTitle(MenuUnicode.VILLAGE_HOME.val()));

		PlayerData pd = plugin.players.get(player.getUniqueId());

		ItemStack village = MenuIcon.VILLAGE.val().getItemStack();
		ItemMeta villageIM = village.getItemMeta();

		ItemStack infoBtn = new ItemStack(Material.YELLOW_TERRACOTTA);
		ItemMeta infoIM = infoBtn.getItemMeta();
		infoIM.setDisplayName(ChatColor.WHITE + "마을 정보 보기");

		ItemStack warpBtn = new ItemStack(Material.GREEN_TERRACOTTA);
		ItemMeta warpIM = warpBtn.getItemMeta();
		warpIM.setDisplayName(ChatColor.WHITE + "내 마을로 이동");

		if (pd.getVillage() > 0) {
			VillageData vd = plugin.villages.get(pd.getVillage());
			villageIM.setDisplayName(ChatColor.WHITE + vd.getName());

		} else {
			villageIM.setDisplayName(ChatColor.GRAY + "가입된 마을 없음");

			List<String> warpLore = new ArrayList<String>();
			warpLore.add(ChatColor.GRAY + "현재 가입되어 있는 마을이 없습니다");
			warpIM.setLore(warpLore);

			List<String> infoLore = new ArrayList<String>();
			infoLore.add(ChatColor.GRAY + "현재 가입되어 있는 마을이 없습니다");
			infoIM.setLore(infoLore);
		}

		village.setItemMeta(villageIM);
		villageHomeInv.setItem(slot(1, 1), village);

		infoBtn.setItemMeta(infoIM);
		villageHomeInv.setItem(slot(3, 0), infoBtn);
		villageHomeInv.setItem(slot(3, 1), infoBtn);
		villageHomeInv.setItem(slot(3, 2), infoBtn);

		warpBtn.setItemMeta(warpIM);
		villageHomeInv.setItem(slot(4, 0), warpBtn);
		villageHomeInv.setItem(slot(4, 1), warpBtn);
		villageHomeInv.setItem(slot(4, 2), warpBtn);

		int row = 1;
		int col = 5;
		for (VillageData vd : plugin.villages.values()) {

			ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
			SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

			OfflinePlayer president = plugin.getServer().getOfflinePlayer(vd.getPresident());
			if (president.hasPlayedBefore()) {
				skullMeta.setOwningPlayer(president);
			}
			skullMeta.setDisplayName(ChatColor.WHITE + vd.getName());

			NamespacedKey villageKey = new NamespacedKey(plugin, "village");
			skullMeta.getPersistentDataContainer().set(villageKey, PersistentDataType.INTEGER, vd.getId());

			List<String> skullLore = new ArrayList<String>();
			skullLore.add(ChatColor.GRAY + "좌클릭: 마을 정보 보기");
			skullLore.add(ChatColor.GRAY + "우클릭: 마을로 이동");
			skullMeta.setLore(skullLore);
			skull.setItemMeta(skullMeta);

			villageHomeInv.setItem(slot(row, col), skull);

			col++;

			if (col == 9) {
				col = 5;
				row++;
			}

			if (row == 4) {
				ItemStack villageNext = MenuIcon.NEXT.val().getItemStack();
				ItemMeta villageNextIM = villageNext.getItemMeta();
				villageNextIM.setDisplayName(ChatColor.WHITE + "다음으로");
				villageNext.setItemMeta(villageNextIM);
				villageHomeInv.setItem(slot(5, 8), villageNext);
				break;
			}

		}

		ItemStack prev = MenuIcon.PREV.val().getItemStack();
		ItemMeta prevIM = prev.getItemMeta();
		prevIM.setDisplayName(ChatColor.WHITE + "이전으로");
		prev.setItemMeta(prevIM);
		villageHomeInv.setItem(slot(5, 0), prev);

		return villageHomeInv;
	}

	public static Inventory getVillageManageInventory(LevelUp plugin, Player player, int villageId) {
		Inventory villageManageInv = Bukkit.createInventory((InventoryHolder) player, 54,
				MenuController.getInventoryTitle(MenuUnicode.VILLAGE_MANAGE.val()));

		PlayerData pd = plugin.players.get(player.getUniqueId());
		VillageData vd = plugin.villages.get(villageId);

		if (pd.getVillage() == villageId) {
			if (vd.getPresident().equals(player.getUniqueId())) {
				ItemStack spawnBtn = new ItemStack(Material.YELLOW_TERRACOTTA);
				ItemMeta spawnIM = spawnBtn.getItemMeta();
				spawnIM.setDisplayName(ChatColor.WHITE + "스폰 설정");
				List<String> spawnLore = new ArrayList<String>();
				spawnLore.add(ChatColor.GRAY + "현재 위치를 마을 스폰으로 변경합니다");
				spawnIM.setLore(spawnLore);
				spawnBtn.setItemMeta(spawnIM);
				villageManageInv.setItem(slot(4, 2), spawnBtn);

				ItemStack applicationBtn = new ItemStack(Material.RED_TERRACOTTA);
				ItemMeta applicationIM = applicationBtn.getItemMeta();
				applicationIM.setDisplayName(ChatColor.WHITE + "마을 신청 관리");
				List<String> applicationLore = new ArrayList<String>();
				applicationLore.add(ChatColor.GRAY + "마을 신청을 수락하거나 거절합니다");
				applicationIM.setLore(applicationLore);
				applicationBtn.setItemMeta(applicationIM);
				villageManageInv.setItem(slot(4, 6), applicationBtn);

			} else {
				ItemStack leaveBtn = new ItemStack(Material.YELLOW_TERRACOTTA);
				ItemMeta leaveIM = leaveBtn.getItemMeta();
				leaveIM.setDisplayName(ChatColor.WHITE + "마을 탈퇴");
				List<String> leaveLore = new ArrayList<String>();
				leaveLore.add(ChatColor.GRAY + "마을을 떠납니다");
				leaveIM.setLore(leaveLore);
				leaveBtn.setItemMeta(leaveIM);
				villageManageInv.setItem(slot(4, 4), leaveBtn);
			}

		} else {
			ItemStack applyBtn = new ItemStack(Material.YELLOW_TERRACOTTA);
			ItemMeta applyIM = applyBtn.getItemMeta();
			applyIM.setDisplayName(ChatColor.WHITE + "마을 신청");
			List<String> applyLore = new ArrayList<String>();
			if (pd.getVillage() > 0) {
				applyLore.add(ChatColor.RED + "이미 다른 마을에 가입되어 있습니다");
			} else {
				applyLore.add(ChatColor.GRAY + "이 마을에 가입 신청을 넣습니다");
			}
			applyIM.setLore(applyLore);
			applyIM.getPersistentDataContainer().set(new NamespacedKey(plugin, "villageId"), PersistentDataType.INTEGER,
					villageId);
			applyBtn.setItemMeta(applyIM);
			villageManageInv.setItem(slot(4, 4), applyBtn);
		}

		int row = 0;
		int col = 1;
		for (PlayerData p : plugin.players.values()) {
			if (p.getVillage() == villageId) {
				ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
				SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

				OfflinePlayer op = plugin.getServer().getOfflinePlayer(p.getUuid());
				if (op.hasPlayedBefore()) {
					skullMeta.setOwningPlayer(op);
				}
				skullMeta.setDisplayName(ChatColor.WHITE + p.getName());

				NamespacedKey villageKey = new NamespacedKey(plugin, "village");
				skullMeta.getPersistentDataContainer().set(villageKey, PersistentDataType.INTEGER, vd.getId());

				if (pd.getVillage() == villageId && vd.getPresident().equals(player.getUniqueId())) {
					List<String> skullLore = new ArrayList<String>();
					skullLore.add(ChatColor.GRAY + "좌클릭: 이장 위임하기");
					skullLore.add(ChatColor.GRAY + "우클릭: 마을 탈퇴시키기");
					skullMeta.setLore(skullLore);
				}

				skull.setItemMeta(skullMeta);

				villageManageInv.setItem(slot(row, col), skull);

				col++;

				if (col == 8) {
					col = 1;
					row++;
				}

				if (row == 4) {
					ItemStack villageNext = MenuIcon.NEXT.val().getItemStack();
					ItemMeta villageNextIM = villageNext.getItemMeta();
					villageNextIM.setDisplayName(ChatColor.WHITE + "다음으로");
					villageNext.setItemMeta(villageNextIM);
					villageManageInv.setItem(slot(5, 8), villageNext);
					break;
				}
			}
		}

		ItemStack prev = MenuIcon.PREV.val().getItemStack().clone();
		ItemMeta prevIM = prev.getItemMeta();
		prevIM.setDisplayName(ChatColor.WHITE + "이전으로");
		prev.setItemMeta(prevIM);
		villageManageInv.setItem(slot(5, 0), prev);

		return villageManageInv;
	}

	public static Inventory getVillageApplyInventory(LevelUp plugin, Player player) {
		Inventory villageApplyInv = Bukkit.createInventory((InventoryHolder) player, 54,
				MenuController.getInventoryTitle(MenuUnicode.VILLAGE_APPLY.val()));

		PlayerData pd = plugin.players.get(player.getUniqueId());

		int row = 0;
		int col = 1;
		for (Entry<UUID, Integer> apply : plugin.villageApplies.entrySet()) {
			if (apply.getValue() == pd.getVillage()) {
				ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
				SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

				OfflinePlayer op = plugin.getServer().getOfflinePlayer(apply.getKey());

				if (op.hasPlayedBefore()) {
					skullMeta.setOwningPlayer(op);
				}

				PlayerData p = plugin.players.get(apply.getKey());
				skullMeta.setDisplayName(ChatColor.WHITE + p.getName());

				List<String> skullLore = new ArrayList<String>();
				skullLore.add(ChatColor.GRAY + "좌클릭: 수락하기");
				skullLore.add(ChatColor.GRAY + "우클릭: 거절하기");
				skullMeta.setLore(skullLore);

				skull.setItemMeta(skullMeta);

				villageApplyInv.setItem(slot(row, col), skull);

				col++;

				if (col == 8) {
					col = 1;
					row++;
				}

				if (row == 4) {
					ItemStack villageNext = MenuIcon.NEXT.val().getItemStack();
					ItemMeta villageNextIM = villageNext.getItemMeta();
					villageNextIM.setDisplayName(ChatColor.WHITE + "다음으로");
					villageNext.setItemMeta(villageNextIM);
					villageApplyInv.setItem(slot(5, 8), villageNext);
					break;
				}
			}
		}

		ItemStack prev = MenuIcon.PREV.val().getItemStack().clone();
		ItemMeta prevIM = prev.getItemMeta();
		prevIM.setDisplayName(ChatColor.WHITE + "이전으로");
		prev.setItemMeta(prevIM);
		villageApplyInv.setItem(slot(5, 0), prev);

		return villageApplyInv;
	}

	/* Calendar Inventory */
	public static Inventory getCalendarHomeInventory(LevelUp plugin, Player player) {
		Inventory calendarHomeInv = Bukkit.createInventory((InventoryHolder) player, 54,
				MenuController.getInventoryTitle(MenuUnicode.CALENDAR_HOME.val()));

		ItemStack dailyQuestBtn = new ItemStack(Material.GREEN_TERRACOTTA);
		ItemMeta dailyQuestIM = dailyQuestBtn.getItemMeta();
		dailyQuestIM.setDisplayName(ChatColor.WHITE + "일일퀘스트");
		dailyQuestBtn.setItemMeta(dailyQuestIM);
		calendarHomeInv.setItem(slot(3, 2), dailyQuestBtn);

		ItemStack seasonPassBtn = new ItemStack(Material.YELLOW_TERRACOTTA);
		ItemMeta seasonPassIM = seasonPassBtn.getItemMeta();
		seasonPassIM.setDisplayName(ChatColor.WHITE + "시즌패스");
		seasonPassBtn.setItemMeta(seasonPassIM);
		calendarHomeInv.setItem(slot(3, 4), seasonPassBtn);

		ItemStack recommendBtn = new ItemStack(Material.RED_TERRACOTTA);
		ItemMeta recommendIM = recommendBtn.getItemMeta();
		recommendIM.setDisplayName(ChatColor.WHITE + "서버추천");
		recommendBtn.setItemMeta(recommendIM);
		calendarHomeInv.setItem(slot(3, 6), recommendBtn);

		ItemStack prev = MenuIcon.PREV.val().getItemStack().clone();
		ItemMeta prevIM = prev.getItemMeta();
		prevIM.setDisplayName(ChatColor.WHITE + "이전으로");
		prev.setItemMeta(prevIM);
		calendarHomeInv.setItem(slot(5, 0), prev);

		return calendarHomeInv;
	}

	public static Inventory getCalendarQuestInventory(Player player) {
		Inventory calendarQuestInv = Bukkit.createInventory((InventoryHolder) player, 54,
				MenuController.getInventoryTitle(MenuUnicode.CALENDAR_DAILY_QUEST.val()));

		ItemStack prev = MenuIcon.PREV.val().getItemStack().clone();
		ItemMeta prevIM = prev.getItemMeta();
		prevIM.setDisplayName(ChatColor.WHITE + "이전으로");
		prev.setItemMeta(prevIM);
		calendarQuestInv.setItem(slot(5, 0), prev);

		return calendarQuestInv;
	}

	public static Inventory getCalendarSeasonPassInventory(LevelUp plugin, Player player, int page) {
		MenuUnicode menu;
		switch (page) {
		case 1:
			menu = MenuUnicode.CALENDAR_SEASONPASS_1;
			break;
		case 2:
			menu = MenuUnicode.CALENDAR_SEASONPASS_2;
			break;
		case 3:
			menu = MenuUnicode.CALENDAR_SEASONPASS_3;
			break;
		case 4:
			menu = MenuUnicode.CALENDAR_SEASONPASS_4;
			break;
		default:
			menu = MenuUnicode.CALENDAR_SEASONPASS_1;
		}

		Inventory calendarSeasonPassInv = Bukkit.createInventory((InventoryHolder) player, 54,
				MenuController.getInventoryTitle(menu.val()));

		SeasonPass seasonPass = plugin.seasonPassData.get(player.getUniqueId());

		int frogPos = seasonPass.getPosition() - ((page - 1) * 5) - 1;

		int i = 0, j = 0;
		for (Entry<LevelUpItem, Boolean> reward : plugin.seasonPassItems) {
			if (i >= (page - 1) * 5 && j < 5) {
				if (frogPos == j) {
					ItemStack frog = MenuIcon.FROG.val().getItemStack();
					calendarSeasonPassInv.setItem(SeasonPassController.REWARD_SLOTS[j], frog);

				} else if (frogPos < j) {
					ItemStack item = reward.getKey().getItemStack();
					if (reward.getValue()) {
						ItemMeta itemMeta = item.getItemMeta();
						itemMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "premium"),
								PersistentDataType.BOOLEAN, true);
						item.setItemMeta(itemMeta);
					}
					calendarSeasonPassInv.setItem(SeasonPassController.REWARD_SLOTS[j], item);
				}
				j++;
			}
			i++;
		}

		List<ItemStack> unobtainedItems = SeasonPassController.getUnobtainedItems(plugin, player.getUniqueId());
		if (!unobtainedItems.isEmpty()) {
			ItemStack chest = MenuIcon.CHEST.val().getItemStack();
			ItemMeta chestMeta = chest.getItemMeta();
			chestMeta.setDisplayName(ChatColor.GOLD + "시즌 패스를 구매하고 아래 보상을 받으세요!");
			JsonArray jsonArray = new JsonArray();
			List<String> chestLore = new ArrayList<String>();
			for (ItemStack item : unobtainedItems) {
				LevelUpItem lvItem = new LevelUpItem(item);
				jsonArray.add(lvItem.createItemJson());
				if (item.getItemMeta().hasDisplayName()) {
					chestLore.add(ChatColor.GRAY + "- " + ChatColor.stripColor(item.getItemMeta().getDisplayName())
							+ " x " + item.getAmount());
				} else {
					chestLore.add(ChatColor.GRAY + "- " + item.getType().name() + " x " + item.getAmount());
				}

			}
			chestMeta.setLore(chestLore);
			chestMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "unobtained"),
					PersistentDataType.STRING, jsonArray.toString());
			chest.setItemMeta(chestMeta);
			calendarSeasonPassInv.setItem(slot(0, 8), chest);
		}

		ItemStack prev = MenuIcon.PREV.val().getItemStack();
		ItemMeta prevIM = prev.getItemMeta();
		prevIM.setDisplayName(ChatColor.WHITE + "이전으로");
		prev.setItemMeta(prevIM);
		calendarSeasonPassInv.setItem(slot(5, 0), prev);

		if (page < 4) {
			ItemStack next = MenuIcon.NEXT.val().getItemStack();
			ItemMeta nextIM = next.getItemMeta();
			nextIM.setDisplayName(ChatColor.WHITE + "다음으로");
			next.setItemMeta(nextIM);
			calendarSeasonPassInv.setItem(slot(5, 8), next);
		}

		return calendarSeasonPassInv;
	}

	/* Friend Inventory */
	public static Inventory getFriendHomeInventory(LevelUp plugin, Player player) {
		Inventory friendHomeInv = Bukkit.createInventory((InventoryHolder) player, 54,
				MenuController.getInventoryTitle(MenuUnicode.FRIEND_HOME.val()));

		int row = 0;
		int col = 1;
		for (FriendData fd : plugin.friends) {
			if (fd.areFriends()) {
				UUID friend = null;
				if (fd.getFromPlayer().equals(player.getUniqueId()))
					friend = fd.getToPlayer();
				if (fd.getToPlayer().equals(player.getUniqueId()))
					friend = fd.getFromPlayer();

				if (friend != null) {
					PlayerData friendData = plugin.players.get(friend);
					ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
					SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

					skullMeta.setDisplayName(ChatColor.WHITE + friendData.getName());

					OfflinePlayer op = plugin.getServer().getOfflinePlayer(friend);
					if (op.hasPlayedBefore()) {
						skullMeta.setOwningPlayer(op);
					}

					List<String> skullLore = new ArrayList<String>();

					if (op.isOnline()) {
						skullLore.add(ChatColor.WHITE + Character.toString(LevelUpIcon.ONLINE.val()) + ChatColor.GREEN
								+ " 온라인");
					} else {
						skullLore.add(ChatColor.WHITE + Character.toString(LevelUpIcon.OFFLINE.val()) + ChatColor.RED
								+ " 마지막접속: " + friendData.getLastOnlineAsString());
					}

					skullLore.add(ChatColor.GRAY + "좌클릭: 친구 정보");
					skullLore.add(ChatColor.GRAY + "우클릭: 친구 삭제");
					skullMeta.setLore(skullLore);

					skull.setItemMeta(skullMeta);
					friendHomeInv.setItem(slot(row, col), skull);

					col++;

					if (col == 8) {
						col = 1;
						row++;
					}

					if (row == 4) {
						ItemStack villageNext = MenuIcon.NEXT.val().getItemStack();
						ItemMeta villageNextIM = villageNext.getItemMeta();
						villageNextIM.setDisplayName(ChatColor.WHITE + "다음으로");
						villageNext.setItemMeta(villageNextIM);
						friendHomeInv.setItem(slot(5, 8), villageNext);
						break;
					}
				}
			}
		}

		ItemStack friendRequest = new ItemStack(Material.GREEN_TERRACOTTA);
		ItemMeta friendRequestIM = friendRequest.getItemMeta();
		friendRequestIM.setDisplayName(ChatColor.WHITE + "친구 신청함");
		friendRequest.setItemMeta(friendRequestIM);
		friendHomeInv.setItem(slot(4, 2), friendRequest);

		ItemStack friendBlock = new ItemStack(Material.RED_TERRACOTTA);
		ItemMeta friendBlockIM = friendBlock.getItemMeta();
		friendBlockIM.setDisplayName(ChatColor.WHITE + "차단 유저 관리");
		friendBlock.setItemMeta(friendBlockIM);
		friendHomeInv.setItem(slot(4, 6), friendBlock);

		ItemStack prev = MenuIcon.PREV.val().getItemStack().clone();
		ItemMeta prevIM = prev.getItemMeta();
		prevIM.setDisplayName(ChatColor.WHITE + "이전으로");
		prev.setItemMeta(prevIM);
		friendHomeInv.setItem(slot(5, 0), prev);

		return friendHomeInv;
	}

	public static Inventory getFriendRequestInventory(LevelUp plugin, Player player) {
		Inventory friendRequestInv = Bukkit.createInventory((InventoryHolder) player, 54,
				MenuController.getInventoryTitle(MenuUnicode.FRIEND_REQUEST.val()));

		int fromRow = 1, fromCol = 0;
		int toRow = 1, toCol = 5;
		for (FriendData fd : plugin.friends) {
			if (!fd.areFriends()) {
				if (fd.getFromPlayer().equals(player.getUniqueId())) {
					UUID friend = fd.getToPlayer();
					PlayerData friendData = plugin.players.get(friend);
					ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
					SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

					skullMeta.setDisplayName(ChatColor.WHITE + friendData.getName());

					OfflinePlayer op = plugin.getServer().getOfflinePlayer(friend);
					if (op.hasPlayedBefore()) {
						skullMeta.setOwningPlayer(op);
					}

					List<String> skullLore = new ArrayList<String>();

					if (op.isOnline()) {
						skullLore.add(ChatColor.WHITE + Character.toString(LevelUpIcon.ONLINE.val()) + ChatColor.GREEN
								+ " 온라인");
					} else {
						skullLore.add(ChatColor.WHITE + Character.toString(LevelUpIcon.OFFLINE.val()) + ChatColor.RED
								+ " 마지막접속: " + friendData.getLastOnlineAsString());
					}

					skullLore.add(ChatColor.GRAY + "우클릭: 친구 신청 취소");
					skullMeta.setLore(skullLore);

					skull.setItemMeta(skullMeta);
					friendRequestInv.setItem(slot(fromRow, fromCol), skull);

					fromCol++;

					if (fromCol == 4) {
						fromCol = 0;
						fromRow++;
					}

					if (fromRow == 5) {
						ItemStack villageNext = MenuIcon.NEXT.val().getItemStack();
						ItemMeta villageNextIM = villageNext.getItemMeta();
						villageNextIM.setDisplayName(ChatColor.WHITE + "다음으로");
						villageNext.setItemMeta(villageNextIM);
						friendRequestInv.setItem(slot(0, 3), villageNext);
						break;
					}

				} else if (fd.getToPlayer().equals(player.getUniqueId())) {
					UUID friend = fd.getFromPlayer();
					PlayerData friendData = plugin.players.get(friend);
					ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
					SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

					skullMeta.setDisplayName(ChatColor.WHITE + friendData.getName());

					OfflinePlayer op = plugin.getServer().getOfflinePlayer(friend);
					if (op.hasPlayedBefore()) {
						skullMeta.setOwningPlayer(op);
					}

					List<String> skullLore = new ArrayList<String>();

					if (op.isOnline()) {
						skullLore.add(ChatColor.WHITE + Character.toString(LevelUpIcon.ONLINE.val()) + ChatColor.GREEN
								+ " 온라인");
					} else {
						skullLore.add(ChatColor.WHITE + Character.toString(LevelUpIcon.OFFLINE.val()) + ChatColor.RED
								+ " 마지막접속: " + friendData.getLastOnlineAsString());
					}

					skullLore.add(ChatColor.GRAY + "좌클릭: 수락하기");
					skullLore.add(ChatColor.GRAY + "우클릭: 거절하기");
					skullMeta.setLore(skullLore);

					skull.setItemMeta(skullMeta);
					friendRequestInv.setItem(slot(toRow, toCol), skull);

					toCol++;

					if (toCol == 9) {
						toCol = 5;
						toRow++;
					}

					if (toRow == 5) {
						ItemStack villageNext = MenuIcon.NEXT.val().getItemStack();
						ItemMeta villageNextIM = villageNext.getItemMeta();
						villageNextIM.setDisplayName(ChatColor.WHITE + "다음으로");
						villageNext.setItemMeta(villageNextIM);
						friendRequestInv.setItem(slot(0, 8), villageNext);
						break;
					}
				}
			}
		}

		ItemStack prev = MenuIcon.PREV.val().getItemStack().clone();
		ItemMeta prevIM = prev.getItemMeta();
		prevIM.setDisplayName(ChatColor.WHITE + "이전으로");
		prev.setItemMeta(prevIM);
		friendRequestInv.setItem(slot(5, 0), prev);

		return friendRequestInv;
	}

	public static Inventory getFriendBlockInventory(LevelUp plugin, Player player) {
		Inventory friendBlockInv = Bukkit.createInventory((InventoryHolder) player, 54,
				MenuController.getInventoryTitle(MenuUnicode.FRIEND_BLOCK.val()));

		if (plugin.userBlocks.containsKey(player.getUniqueId())) {
			int row = 0;
			int col = 1;
			for (UUID block : plugin.userBlocks.get(player.getUniqueId())) {
				PlayerData friendData = plugin.players.get(block);
				ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
				SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

				skullMeta.setDisplayName(ChatColor.WHITE + friendData.getName());

				OfflinePlayer op = plugin.getServer().getOfflinePlayer(block);
				if (op.hasPlayedBefore()) {
					skullMeta.setOwningPlayer(op);
				}

				List<String> skullLore = new ArrayList<String>();

				if (op.isOnline()) {
					skullLore.add(
							ChatColor.WHITE + Character.toString(LevelUpIcon.ONLINE.val()) + ChatColor.GREEN + " 온라인");
				} else {
					skullLore.add(ChatColor.WHITE + Character.toString(LevelUpIcon.OFFLINE.val()) + ChatColor.RED
							+ " 마지막접속: " + friendData.getLastOnlineAsString());
				}

				skullLore.add(ChatColor.GRAY + "우클릭: 차단 해제");
				skullMeta.setLore(skullLore);

				skull.setItemMeta(skullMeta);
				friendBlockInv.setItem(slot(row, col), skull);

				col++;

				if (col == 8) {
					col = 1;
					row++;
				}

				if (row == 5) {
					ItemStack villageNext = MenuIcon.NEXT.val().getItemStack();
					ItemMeta villageNextIM = villageNext.getItemMeta();
					villageNextIM.setDisplayName(ChatColor.WHITE + "다음으로");
					villageNext.setItemMeta(villageNextIM);
					friendBlockInv.setItem(slot(5, 8), villageNext);
					break;
				}
			}
		}

		ItemStack prev = MenuIcon.PREV.val().getItemStack().clone();
		ItemMeta prevIM = prev.getItemMeta();
		prevIM.setDisplayName(ChatColor.WHITE + "이전으로");
		prev.setItemMeta(prevIM);
		friendBlockInv.setItem(slot(5, 0), prev);

		return friendBlockInv;
	}

	public static Inventory getFriendInfoInventory(LevelUp plugin, Player player, UUID friend) {
		Inventory friendInfoInv = Bukkit.createInventory((InventoryHolder) player, 54,
				MenuController.getInventoryTitle(MenuUnicode.FRIEND_INFO.val()));

		PlayerData friendData = plugin.players.get(friend);

		ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
		skullMeta.setDisplayName(ChatColor.WHITE + friendData.getName());

		OfflinePlayer op = plugin.getServer().getOfflinePlayer(friend);
		if (op.hasPlayedBefore()) {
			skullMeta.setOwningPlayer(op);
		}

		List<String> skullLore = new ArrayList<String>();

		if (op.isOnline()) {
			skullLore.add(ChatColor.WHITE + Character.toString(LevelUpIcon.ONLINE.val()) + ChatColor.GREEN + " 온라인");
		} else {
			skullLore.add(ChatColor.WHITE + Character.toString(LevelUpIcon.OFFLINE.val()) + ChatColor.RED + " 마지막접속: "
					+ friendData.getLastOnlineAsString());
		}

		skullMeta.setLore(skullLore);
		skull.setItemMeta(skullMeta);

		friendInfoInv.setItem(slot(1, 1), skull);

		ItemStack village = MenuIcon.VILLAGE.val().getItemStack();
		ItemMeta villageIM = village.getItemMeta();

		if (friendData.getVillage() > 0) {
			VillageData vd = plugin.villages.get(friendData.getVillage());
			villageIM.setDisplayName(ChatColor.WHITE + vd.getName());
			villageIM.getPersistentDataContainer().set(new NamespacedKey(plugin, "village"), PersistentDataType.INTEGER,
					vd.getId());
			List<String> villageLore = new ArrayList<String>();
			villageLore.add(ChatColor.GRAY + "좌클릭: 마을 정보 보기");
			villageLore.add(ChatColor.GRAY + "좌클릭: 마을로 이동");
			villageIM.setLore(villageLore);
		} else {
			villageIM.setDisplayName(ChatColor.GRAY + "가입된 마을 없음");
		}

		village.setItemMeta(villageIM);
		friendInfoInv.setItem(slot(3, 1), village);

		ToolData tool = plugin.tools.get(friend);
		ItemStack pickaxe = tool.getPickaxe().getAsItemStack();
		friendInfoInv.setItem(slot(1, 5), pickaxe);

		ItemStack axe = tool.getAxe().getAsItemStack();
		friendInfoInv.setItem(slot(1, 7), axe);

		ItemStack sword = tool.getSword().getAsItemStack();
		friendInfoInv.setItem(slot(3, 5), sword);

		ItemStack shovel = tool.getShovel().getAsItemStack();
		friendInfoInv.setItem(slot(3, 7), shovel);

		ItemStack prev = MenuIcon.PREV.val().getItemStack().clone();
		ItemMeta prevIM = prev.getItemMeta();
		prevIM.setDisplayName(ChatColor.WHITE + "이전으로");
		prev.setItemMeta(prevIM);
		friendInfoInv.setItem(slot(5, 0), prev);

		return friendInfoInv;
	}

	/* Warp Inventory */
	public static Inventory getWarpHomeInventory(LevelUp plugin, Player player) {
		Inventory warpHomeInv = Bukkit.createInventory((InventoryHolder) player, 54,
				MenuController.getInventoryTitle(MenuUnicode.WARP_HOME.val()));

		ItemStack spawnBtn = MenuIcon.BLANK.val().getItemStack();
		ItemMeta spawnIM = spawnBtn.getItemMeta();
		spawnIM.setDisplayName(ChatColor.of("#E1D9CA") + "광장 행");
		spawnBtn.setItemMeta(spawnIM);
		for (int row = 0; row < 6; row++) {
			for (int col = 0; col <= 1; col++) {
				warpHomeInv.setItem(MenuController.slot(row, col), spawnBtn);
			}
		}

		ItemStack wildBtn = MenuIcon.BLANK.val().getItemStack();
		ItemMeta wildIM = wildBtn.getItemMeta();
		wildIM.setDisplayName(ChatColor.of("#87B46A") + "야생 행");
		wildBtn.setItemMeta(wildIM);
		for (int row = 0; row < 6; row++) {
			for (int col = 2; col <= 3; col++) {
				warpHomeInv.setItem(MenuController.slot(row, col), wildBtn);
			}
		}

		ItemStack worldBtn = MenuIcon.BLANK.val().getItemStack();
		ItemMeta worldIM = worldBtn.getItemMeta();
		worldIM.setDisplayName(ChatColor.of("#6CABE5") + "건축 행");
		worldBtn.setItemMeta(worldIM);
		for (int row = 0; row < 6; row++) {
			for (int col = 5; col <= 6; col++) {
				warpHomeInv.setItem(MenuController.slot(row, col), worldBtn);
			}
		}

		ItemStack netherBtn = MenuIcon.BLANK.val().getItemStack();
		ItemMeta netherIM = netherBtn.getItemMeta();
		netherIM.setDisplayName(ChatColor.of("#FF0000") + "지옥 행");
		netherBtn.setItemMeta(netherIM);
		for (int row = 0; row < 6; row++) {
			for (int col = 7; col <= 8; col++) {
				warpHomeInv.setItem(MenuController.slot(row, col), netherBtn);
			}
		}

		return warpHomeInv;
	}

	/* Shopping Inventory */
	public static Inventory getShoppingHomeInventory(LevelUp plugin, Player player) {
		Inventory shoppingHomeInv = Bukkit.createInventory((InventoryHolder) player, 54,
				MenuController.getInventoryTitle(MenuUnicode.SHOPPING_HOME.val()));

		PlayerData pd = plugin.players.get(player.getUniqueId());

		int row = 1;
		int col = 1;
		for (Entry<LevelUpItem, Integer> levelUpItem : plugin.shoppingItems.entrySet()) {
			ItemStack item = levelUpItem.getKey().getItemStack();
			int price = levelUpItem.getValue();

			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "price"), PersistentDataType.INTEGER,
					price);
			List<String> itemLore = new ArrayList<String>();
			itemLore.add(ChatColor.WHITE + Character.toString(LevelUpIcon.COIN.val()) + ChatColor.GOLD + " "
					+ Integer.toString(price));
			if (pd.getBalance() >= price) {
				itemLore.add(ChatColor.GRAY + "좌클릭으로 구매합니다");
			} else {
				itemLore.add(ChatColor.RED + "보유한 자금이 부족합니다");
			}
			itemMeta.setLore(itemLore);
			item.setItemMeta(itemMeta);

			shoppingHomeInv.setItem(slot(row, col), item);

			col++;

			if (col == 8) {
				col = 1;
				row++;
			}

			if (row == 5) {
				ItemStack villageNext = MenuIcon.NEXT.val().getItemStack();
				ItemMeta villageNextIM = villageNext.getItemMeta();
				villageNextIM.setDisplayName(ChatColor.WHITE + "다음으로");
				villageNext.setItemMeta(villageNextIM);
				shoppingHomeInv.setItem(slot(5, 8), villageNext);
				break;
			}
		}

		ItemStack prev = MenuIcon.PREV.val().getItemStack().clone();
		ItemMeta prevIM = prev.getItemMeta();
		prevIM.setDisplayName(ChatColor.WHITE + "이전으로");
		prev.setItemMeta(prevIM);
		shoppingHomeInv.setItem(slot(5, 0), prev);

		return shoppingHomeInv;
	}

	/* Guide Inventory */
	public static Inventory getGuideHomeInventory(LevelUp plugin, Player player) {
		Inventory guideHomeInv = Bukkit.createInventory((InventoryHolder) player, 54,
				MenuController.getInventoryTitle(MenuUnicode.GUIDE_HOME.val()));

		ItemStack prev = MenuIcon.PREV.val().getItemStack().clone();
		ItemMeta prevIM = prev.getItemMeta();
		prevIM.setDisplayName(ChatColor.WHITE + "이전으로");
		prev.setItemMeta(prevIM);
		guideHomeInv.setItem(slot(5, 0), prev);

		return guideHomeInv;
	}

	public static int slot(int row, int col) {
		return row * 9 + col;
	}

	public static int row(int slot) {
		return slot / 9;
	}

	public static int col(int slot) {
		return slot % 9;
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
