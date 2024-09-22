package com.levelup.npc;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.levelup.LevelUp;
import com.levelup.LevelUpIcon;
import com.levelup.LevelUpItem;
import com.levelup.menu.MenuController;
import com.levelup.menu.MenuIcon;
import com.levelup.menu.MenuUnicode;
import com.levelup.tool.ToolAbstract;
import com.levelup.tool.ToolController;
import com.levelup.tool.ToolData;
import com.levelup.tool.ToolQuest;
import com.levelup.tool.ToolQuestMessage;
import com.levelup.tool.ToolType;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class NPCController {

	public static final String BLACKSMITH = "NPC_BLACKSMITH";
	public static final String SPACE = "                        ";

	public static void configureNPC(LevelUp plugin, LivingEntity entity, boolean setAI, String disguise) {

		entity.setAI(setAI);
		entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0);
		entity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(1);
		entity.setCanPickupItems(false);
		entity.setInvulnerable(true);
		entity.setPersistent(true);
		entity.setCollidable(false);
		entity.setSilent(true);

		NamespacedKey npcKey = new NamespacedKey(plugin, "npc");
		entity.getPersistentDataContainer().set(npcKey, PersistentDataType.BOOLEAN, true);
	}

	public static Inventory getFirstNPCInventory(LevelUp plugin, Player player, UUID uuid) {
		Entity entity = plugin.getServer().getEntity(uuid);
		LivingEntity le = (LivingEntity) entity;

		Inventory npcInv = Bukkit.createInventory((InventoryHolder) player, 54,
				MenuController.getInventoryTitle(MenuUnicode.NPC_1.val()));
		ItemStack npcID = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta npcIM = npcID.getItemMeta();
		npcIM.setDisplayName(entity.getUniqueId().toString());
		npcID.setItemMeta(npcIM);
		npcInv.setItem(0, npcID);

		ItemStack helmet = le.getEquipment().getHelmet();
		npcInv.setItem(MenuController.slot(0, 4), helmet);

		ItemStack chestPlate = le.getEquipment().getChestplate();
		npcInv.setItem(MenuController.slot(1, 4), chestPlate);

		ItemStack leggings = le.getEquipment().getLeggings();
		npcInv.setItem(MenuController.slot(2, 4), leggings);

		ItemStack boots = le.getEquipment().getBoots();
		npcInv.setItem(MenuController.slot(3, 4), boots);

		ItemStack offHand = le.getEquipment().getItemInOffHand();
		npcInv.setItem(MenuController.slot(1, 2), offHand);

		ItemStack mainHand = le.getEquipment().getItemInMainHand();
		npcInv.setItem(MenuController.slot(1, 6), mainHand);

		ItemStack nextPage = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta nextPageIM = nextPage.getItemMeta();
		nextPageIM.setDisplayName("거래 설정");
		nextPage.setItemMeta(nextPageIM);
		npcInv.setItem(53, nextPage);

		ItemStack delete = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta deleteIM = delete.getItemMeta();
		deleteIM.setDisplayName("NPC 삭제");
		delete.setItemMeta(deleteIM);
		npcInv.setItem(8, delete);

		return npcInv;
	}

	public static Inventory getSecondNPCInventory(LevelUp plugin, Player player, UUID uuid) {
		Entity entity = plugin.getServer().getEntity(uuid);

		Inventory npcInv = Bukkit.createInventory((InventoryHolder) player, 54,
				MenuController.getInventoryTitle(MenuUnicode.NPC_2.val()));
		ItemStack npcID = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta npcIM = npcID.getItemMeta();
		npcIM.setDisplayName(uuid.toString());
		npcID.setItemMeta(npcIM);
		npcInv.setItem(49, npcID);

		ItemStack prevPage = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta prevPageIM = prevPage.getItemMeta();
		prevPageIM.setDisplayName("코스튬 설정");
		prevPage.setItemMeta(prevPageIM);
		npcInv.setItem(45, prevPage);

		ItemStack nextPage = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta nextPageIM = nextPage.getItemMeta();
		nextPageIM.setDisplayName("거래 설정 2");
		nextPage.setItemMeta(nextPageIM);
		npcInv.setItem(53, nextPage);

		NamespacedKey tradeKey = new NamespacedKey(plugin, "levelup_npc_trade");
		if (entity.getPersistentDataContainer().has(tradeKey, PersistentDataType.STRING)) {
			String json = entity.getPersistentDataContainer().get(tradeKey, PersistentDataType.STRING);
			JsonArray jsonArray = JsonParser.parseString(json).getAsJsonArray();

			int row = 0;
			int col = 0;

			int max = jsonArray.size() > 10 ? 10 : jsonArray.size();
			for (int i = 0; i < max; i++) {
				JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();

				ItemStack item1 = new LevelUpItem(jsonObject.get("item1").getAsJsonObject()).getItemStack();
				npcInv.setItem(MenuController.slot(row, col), item1);

				if (jsonObject.has("item2")) {
					ItemStack item2 = new LevelUpItem(jsonObject.get("item2").getAsJsonObject()).getItemStack();
					npcInv.setItem(MenuController.slot(row, col + 1), item2);
				}

				ItemStack result = new LevelUpItem(jsonObject.get("result").getAsJsonObject()).getItemStack();
				npcInv.setItem(MenuController.slot(row, col + 3), result);

				row++;
				if (row == 5) {
					row = 0;
					col = 5;
				}
			}

		}

		return npcInv;
	}

	public static Inventory getThirdNPCInventory(LevelUp plugin, Player player, UUID uuid) {
		Entity entity = plugin.getServer().getEntity(uuid);

		Inventory npcInv = Bukkit.createInventory((InventoryHolder) player, 54,
				MenuController.getInventoryTitle(MenuUnicode.NPC_3.val()));
		ItemStack npcID = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta npcIM = npcID.getItemMeta();
		npcIM.setDisplayName(uuid.toString());
		npcID.setItemMeta(npcIM);
		npcInv.setItem(49, npcID);

		ItemStack prevPage = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta prevPageIM = prevPage.getItemMeta();
		prevPageIM.setDisplayName("거래 설정 1");
		prevPage.setItemMeta(prevPageIM);
		npcInv.setItem(45, prevPage);

		NamespacedKey tradeKey = new NamespacedKey(plugin, "levelup_npc_trade");
		if (entity.getPersistentDataContainer().has(tradeKey, PersistentDataType.STRING)) {
			String json = entity.getPersistentDataContainer().get(tradeKey, PersistentDataType.STRING);
			JsonArray jsonArray = JsonParser.parseString(json).getAsJsonArray();

			int row = 0;
			int col = 0;

			if (jsonArray.size() > 10) {
				for (int i = 10; i < jsonArray.size(); i++) {
					JsonObject jsonObject = jsonArray.get(i).getAsJsonObject();

					ItemStack item1 = new LevelUpItem(jsonObject.get("item1").getAsJsonObject()).getItemStack();
					npcInv.setItem(MenuController.slot(row, col), item1);

					if (jsonObject.has("item2")) {
						ItemStack item2 = new LevelUpItem(jsonObject.get("item2").getAsJsonObject()).getItemStack();
						npcInv.setItem(MenuController.slot(row, col + 1), item2);
					}

					ItemStack result = new LevelUpItem(jsonObject.get("result").getAsJsonObject()).getItemStack();
					npcInv.setItem(MenuController.slot(row, col + 3), result);

					row++;
					if (row == 5) {
						row = 0;
						col = 5;
					}
				}
			}
		}

		return npcInv;
	}

	public static void showDefaultMessage(LevelUp plugin, Player player) {

		ToolData toolData = plugin.tools.get(player.getUniqueId());
		LevelUpIcon pickaxeIcon = LevelUpIcon.valueOf(toolData.getPickaxe().getMaterial().toString());
		LevelUpIcon axeIcon = LevelUpIcon.valueOf(toolData.getAxe().getMaterial().toString());
		LevelUpIcon swordIcon = LevelUpIcon.valueOf(toolData.getSword().getMaterial().toString());
		LevelUpIcon shovelIcon = LevelUpIcon.valueOf(toolData.getShovel().getMaterial().toString());

		TextComponent[] msg = new TextComponent[4];
		msg[0] = new TextComponent("(기본 메세지)");
		msg[0].setColor(ChatColor.of("#5D4612"));
		msg[1] = new TextComponent();
		msg[2] = new TextComponent();

		TextComponent pickaxe = new TextComponent(
				"" + pickaxeIcon.val() + ChatColor.BLACK + ChatColor.UNDERLINE + "곡괭이");
		pickaxe.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("곡괭이 퀘스트")));
		pickaxe.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lvnpc quest pickaxe"));

		TextComponent axe = new TextComponent("    " + axeIcon.val() + ChatColor.BLACK + ChatColor.UNDERLINE + "도끼");
		axe.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("도끼 퀘스트")));
		axe.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lvnpc quest axe"));

		TextComponent sword = new TextComponent("    " + swordIcon.val() + ChatColor.BLACK + ChatColor.UNDERLINE + "검");
		sword.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("검 퀘스트")));
		sword.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lvnpc quest sword"));

		TextComponent shovel = new TextComponent(
				"    " + shovelIcon.val() + ChatColor.BLACK + ChatColor.UNDERLINE + "삽");
		shovel.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("삽 퀘스트")));
		shovel.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lvnpc quest shovel"));

		msg[3] = new TextComponent(pickaxe, axe, sword, shovel);

		showBlacksmithMessage(player, msg);
	}

	public static void showQuestMessage(LevelUp plugin, Player player, ToolType type) throws SQLException {

		ToolData toolData = plugin.tools.get(player.getUniqueId());
		ToolAbstract tool = toolData.getTool(type);

		Map<Material, Integer> quest = plugin.quests.get(player.getUniqueId()).get(type);
		if (quest.isEmpty()) {
			if (tool.getMaterial().toString().toUpperCase().contains("NETHERITE")
					&& tool.getLevel() == plugin.toolQuest.get(type).get(tool.getMaterial()).size()) {
				TextComponent[] msg = new TextComponent[4];
				msg[0] = new TextComponent("(도구 최대 강화 메세지)");
				msg[0].setColor(ChatColor.of("#5D4612"));
				showBlacksmithMessage(player, msg);

			} else {
				for (ToolQuest toolQuest : plugin.toolQuest.get(type).get(tool.getMaterial())) {
					if (toolQuest.getLevel() == tool.getLevel()) {
						int required = toolQuest.getRequired();
						Map<Material, Integer> clonedQuests = new HashMap<Material, Integer>(toolQuest.getQuest());
						for (int i = 0; i < required; i++) {
							Random rand = new Random();
							int randomNum = rand.nextInt(clonedQuests.size());
							Material material = new ArrayList<Material>(clonedQuests.keySet()).get(randomNum);
							quest.put(material, clonedQuests.get(material));
							clonedQuests.remove(material);
						}
						break;
					}
				}
				ToolController.updateQuest(plugin, player.getUniqueId());

				TextComponent[] msg = new TextComponent[4];

				for (ToolQuestMessage toolQuestMsg : plugin.toolQuestMessage.get(type).get(tool.getMaterial())) {
					if (toolQuestMsg.getLevel() == tool.getLevel()) {
						int count = 0;
						for (String q : toolQuestMsg.getQuest()) {
							msg[count] = new TextComponent(q);
							msg[count].setColor(ChatColor.of("#5D4612"));
							count++;
						}
						break;
					}
				}

				int count = 0;
				msg[2] = new TextComponent();
				msg[3] = new TextComponent();
				for (Entry<Material, Integer> q : quest.entrySet()) {
					if (count != 0 && count != 6) {
						msg[2].addExtra(", ");
					}
					Entry<Character, String> item = plugin.toolQuestItems.get(q.getKey());

					String amount;
					if (q.getValue() >= 64) {
						amount = String.valueOf((int) (q.getValue() / 64)) + "세트";
					} else {
						amount = String.valueOf(q.getValue()) + '개';
					}

					TextComponent text = new TextComponent(" " + amount);
					text.setColor(ChatColor.BLACK);
					TextComponent itemComponent = new TextComponent(new TextComponent(String.valueOf(item.getKey())),
							text);
					itemComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(item.getValue())));

					if (count < 6) {
						msg[2].addExtra(itemComponent);
					} else {
						msg[3].addExtra(itemComponent);
					}

					count++;
				}

				showBlacksmithMessage(player, msg);
			}

		} else {
			int requiredExp = 0;
			for (ToolQuest tq : plugin.toolQuest.get(type).get(tool.getMaterial())) {
				if (tq.getLevel() == tool.getLevel()) {
					requiredExp = tq.getExp();
				}
			}

			boolean hasItems = true;
			for (Entry<Material, Integer> q : quest.entrySet()) {
				if (!player.getInventory().contains(q.getKey(), q.getValue())) {
					hasItems = false;
				}
			}

			if (tool.getExp() >= requiredExp && hasItems) {

				TextComponent[] msg = new TextComponent[4];
				for (ToolQuestMessage toolQuestMsg : plugin.toolQuestMessage.get(type).get(tool.getMaterial())) {
					if (toolQuestMsg.getLevel() == tool.getLevel()) {
						int count = 0;
						for (String q : toolQuestMsg.getComplete()) {
							msg[count] = new TextComponent(q);
							msg[count].setColor(ChatColor.of("#5D4612"));
							count++;
						}
						break;
					}
				}

				showBlacksmithMessage(player, msg);
				player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);

				for (Entry<Material, Integer> q : quest.entrySet()) {
					int amount = q.getValue();
					for (ItemStack item : player.getInventory()) {
						if (item != null && item.getType() == q.getKey()) {
							if (item.getAmount() >= amount) {
								item.setAmount(item.getAmount() - amount);
								amount = 0;
							} else {
								amount -= item.getAmount();
								item.setAmount(0);
							}

							if (amount == 0)
								break;
						}
					}
				}

				tool.setExp(tool.getExp() - requiredExp);
				ToolController.toolUpgrade(plugin, player, type);

				quest.clear();
				ToolController.updateQuest(plugin, player.getUniqueId());

				if (ToolController.MsgShown.get(player.getUniqueId()).contains(type)) {
					ToolController.MsgShown.get(player.getUniqueId()).remove(type);
				}

			} else {
				TextComponent[] msg = new TextComponent[4];
				msg[0] = new TextComponent("경험치와 아래 재료들을 모두 구해오면 도구를 ");
				msg[0].setColor(ChatColor.of("#5D4612"));
				msg[1] = new TextComponent("강화시켜주겠개굴!");
				msg[1].setColor(ChatColor.of("#5D4612"));

				int count = 0;
				msg[2] = new TextComponent();
				msg[3] = new TextComponent();
				for (Entry<Material, Integer> q : quest.entrySet()) {
					if (count != 0 && count != 6) {
						msg[2].addExtra("  ");
					}
					Entry<Character, String> item = plugin.toolQuestItems.get(q.getKey());

					String amount;
					if (q.getValue() >= 64) {
						amount = String.valueOf((int) (q.getValue() / 64)) + "세트";
					} else {
						amount = String.valueOf(q.getValue()) + '개';
					}

					TextComponent text = new TextComponent(" " + amount);
					text.setColor(ChatColor.BLACK);
					TextComponent itemComponent = new TextComponent(new TextComponent(String.valueOf(item.getKey())),
							text);
					itemComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(item.getValue())));

					if (count < 6) {
						msg[2].addExtra(itemComponent);
					} else {
						msg[3].addExtra(itemComponent);
					}

					count++;
				}

				showBlacksmithMessage(player, msg);
			}
		}
	}

	public static void showBlacksmithMessage(Player player, TextComponent[] msg) {
		player.sendMessage("");
		player.sendMessage(SPACE + ChatColor.BLACK + ChatColor.BOLD + "대장장이");
		for (int i = 0; i < msg.length; i++) {
			if (msg[i] == null) {
				player.sendMessage("");
			} else {
				player.spigot().sendMessage(new TextComponent(new TextComponent(SPACE), msg[i]));
			}
		}
		player.sendMessage("   " + String.valueOf(LevelUpIcon.BLACKSMITH_DEFAULT.val()));
		player.sendMessage(String.valueOf(MenuUnicode.TEXTBOX.val()));
	}

}
