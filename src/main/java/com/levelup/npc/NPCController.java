package com.levelup.npc;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
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
import com.levelup.money.MoneyController.MoneyItem;
import com.levelup.tool.ToolAbstract;
import com.levelup.tool.ToolController;
import com.levelup.tool.ToolData;
import com.levelup.tool.ToolQuest;
import com.levelup.tool.ToolQuestMessage;
import com.levelup.tool.ToolType;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class NPCController {

	public static enum NPCMythic {
		BLACKSMITH, CITYHALL, FLOWER_SHOP, BLOCK_SHOP, DECO_SHOP, RIDING_SHOP, FISHING_SHOP, FURNITURE_SHOP, FARMER_SHOP, FARMER_2_SHOP,
		MINING_SHOP, COOK_SHOP, COOK_2_SHOP;

		public static boolean contains(String name) {
			try {
				NPCMythic.valueOf(name.toUpperCase());
				return true;
			} catch (IllegalArgumentException e) {
				return false;
			}
		}

		@Override
		public String toString() {
			return "NPC_" + this.name().toUpperCase();
		}
	}

	public static final String SPACE = "                        ";
	public static final Map<EquipmentSlot, Integer> INV_SLOT = Map.of(EquipmentSlot.HEAD, MenuController.slot(0, 4),
			EquipmentSlot.CHEST, MenuController.slot(1, 4), EquipmentSlot.LEGS, MenuController.slot(2, 4),
			EquipmentSlot.FEET, MenuController.slot(3, 4), EquipmentSlot.OFF_HAND, MenuController.slot(1, 2),
			EquipmentSlot.HAND, MenuController.slot(1, 6));

	@SuppressWarnings({ "unchecked" })
	public static Map<NPCMythic, Map<LevelUpItem, Integer>> parseNPCShopItems(Map<String, Object> yaml) {
		Map<NPCMythic, Map<LevelUpItem, Integer>> npcShopItems = new LinkedHashMap<NPCMythic, Map<LevelUpItem, Integer>>();

		for (Entry<String, Object> entry : yaml.entrySet()) {
			NPCMythic type = NPCMythic.valueOf(entry.getKey().toUpperCase());
			List<Object> itemObjs = (List<Object>) entry.getValue();
			Map<LevelUpItem, Integer> shopItems = new HashMap<LevelUpItem, Integer>();

			for (Object itemObj : itemObjs) {
				Map<String, Object> item = (Map<String, Object>) itemObj;
				String material = item.get("material") == null ? null : item.get("material").toString().toUpperCase();
				String namespacedID = item.get("namespacedID") == null ? null : item.get("namespacedID").toString();
				int amount = Integer.parseInt(item.get("amount").toString());
				int price = Integer.parseInt(item.get("price").toString());
				shopItems.put(new LevelUpItem(material, namespacedID, amount), price);

				if (namespacedID != null) {
					shopItems.put(new LevelUpItem(material, namespacedID + "_silver_star", amount), price * 2);
					shopItems.put(new LevelUpItem(material, namespacedID + "_golden_star", amount), price * 3);
				}
			}
			npcShopItems.put(type, shopItems);
		}

		return npcShopItems;
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

		String json = null;
		NPC npc = CitizensAPI.getNPCRegistry().getNPC(entity);
		NamespacedKey tradeKey = new NamespacedKey(plugin, "levelup_npc_trade");
		if (entity.getPersistentDataContainer().has(tradeKey, PersistentDataType.STRING)) {
			json = entity.getPersistentDataContainer().get(tradeKey, PersistentDataType.STRING);

		} else if (npc != null && npc.data().has("levelup_npc_trade")) {
			json = npc.data().get("levelup_npc_trade");
		}

		if (json != null) {
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

		String json = null;
		NPC npc = CitizensAPI.getNPCRegistry().getNPC(entity);
		NamespacedKey tradeKey = new NamespacedKey(plugin, "levelup_npc_trade");
		if (entity.getPersistentDataContainer().has(tradeKey, PersistentDataType.STRING)) {
			json = entity.getPersistentDataContainer().get(tradeKey, PersistentDataType.STRING);

		} else if (npc != null && npc.data().has("levelup_npc_trade")) {
			json = npc.data().get("levelup_npc_trade");
		}

		if (json != null) {
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

		showBlankBefore(player);
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
					if (count > 0 && count < 6) {
						msg[2].addExtra("  ");
					} else if (count > 6) {
						msg[3].addExtra("  ");
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

				player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
				ToolController.updateBossBar(plugin, player, type);

				quest.clear();
				ToolController.updateQuest(plugin, player.getUniqueId());
				showQuestMessage(plugin, player, type);

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
					if (count > 0 && count < 6) {
						msg[2].addExtra("  ");
					} else if (count > 6) {
						msg[3].addExtra("  ");
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

	public static void showBlankBefore(Player player) {
		player.sendMessage("");
		player.sendMessage("");
		player.sendMessage("");
		player.sendMessage("");
		player.sendMessage("");
		player.sendMessage("");
		player.sendMessage("");
		player.sendMessage("");
		player.sendMessage("");
		player.sendMessage("");
	}

	public static void showBlacksmithMessage(Player player, TextComponent[] msg) {
		player.sendMessage("");
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
		player.sendMessage("");
	}

	public static void showShopInventory(LevelUp plugin, Player player, NPCMythic npcType) {
		Inventory shopInv = Bukkit.createInventory((InventoryHolder) player, 36,
				MenuController.getInventoryTitle(MenuUnicode.NPC_SHOP.val()));

		ItemStack sellBtn = new ItemStack(Material.GREEN_TERRACOTTA);
		ItemMeta sellIM = sellBtn.getItemMeta();
		sellIM.setDisplayName(ChatColor.WHITE + "판매하기");
		sellIM.getPersistentDataContainer().set(new NamespacedKey(plugin, "type"), PersistentDataType.STRING,
				npcType.name());
		sellBtn.setItemMeta(sellIM);
		shopInv.setItem(MenuController.slot(3, 3), sellBtn);
		shopInv.setItem(MenuController.slot(3, 4), sellBtn);
		shopInv.setItem(MenuController.slot(3, 5), sellBtn);

		player.openInventory(shopInv);
	}

	public static void sellItems(LevelUp plugin, Player player, NPCMythic type, Inventory inv) {
		List<LevelUpItem> items = new ArrayList<LevelUpItem>();
		for (int i = 0; i < 27; i++) {
			if (inv.getItem(i) != null) {
				LevelUpItem newItem = new LevelUpItem(inv.getItem(i));

				if (items.contains(newItem)) {
					LevelUpItem lv = items.get(items.indexOf(newItem));
					lv.setAmount(lv.getAmount() + newItem.getAmount());
				} else {
					items.add(newItem);
				}
			}
		}

		if (!items.isEmpty()) {
			Map<LevelUpItem, Integer> priceMap = plugin.npcShopItems.get(type);
			Map<LevelUpItem, Integer> purchased = new HashMap<LevelUpItem, Integer>();
			char space = '\u3000';

			int totalPrice = 0;
			for (Entry<LevelUpItem, Integer> entry : priceMap.entrySet()) {

				if (items.contains(entry.getKey())) {
					LevelUpItem item = items.get(items.indexOf(entry.getKey()));

					if (item.getAmount() >= entry.getKey().getAmount()) {
						int set = item.getAmount() / entry.getKey().getAmount();
						int price = entry.getValue() * set;
						int totalAmount = entry.getKey().getAmount() * set;
						totalPrice += price;
						purchased.put(entry.getKey(), totalAmount);

						String name, spaces;
						if (item.getNamespacedID() == null) {
							name = item.getMaterial().toString().replace("_", " ");
							if (name.length() % 3 == 0)
								spaces = Character.toString(space).repeat(Math.max(0, 9 - (name.length() * 2 / 3)));
							else if (name.length() % 3 == 1)
								spaces = Character.toString(space).repeat(Math.max(0, 8 - (name.length() * 2 / 3)))
										+ "  ";
							else
								spaces = Character.toString(space).repeat(Math.max(0, 8 - (name.length() * 2 / 3)))
										+ " ";

						} else {
							name = ChatColor.stripColor(item.getItemStack().getItemMeta().getDisplayName());

							if (item.getNamespacedID().contains("_star"))
								spaces = Character.toString(space).repeat(Math.max(0, 10 - name.length())) + " ";
							else
								spaces = Character.toString(space).repeat(Math.max(0, 9 - name.length()));
						}
						player.sendMessage(
								ChatColor.GOLD + name + spaces + String.format("%4s개: %3s코인", totalAmount, price));
					}
				}
			}

			if (!purchased.isEmpty()) {
				player.sendMessage(ChatColor.GOLD + "------------------------");
				String spaces = Character.toString(space).repeat(Math.max(0, 12));
				player.sendMessage(ChatColor.GOLD + "총:" + spaces + totalPrice + "코인");

				for (int i = 0; i < 27; i++) {
					ItemStack item = inv.getItem(i);
					if (item != null) {
						LevelUpItem lvItem = new LevelUpItem(inv.getItem(i));

						if (purchased.containsKey(lvItem)) {
							int totalAmount = purchased.get(lvItem);
							if (item.getAmount() >= totalAmount) {
								item.setAmount(item.getAmount() - totalAmount);
								purchased.remove(lvItem);
							} else {
								totalAmount -= item.getAmount();
								item.setAmount(0);
								purchased.put(lvItem, totalAmount);
							}
						}
					}
				}

				if (totalPrice >= 100) {
					int count = totalPrice / 100;
					ItemStack gold = MoneyItem.GOLD.getItemStack();
					gold.setAmount(count);
					player.getInventory().addItem(gold);
					totalPrice -= count * 100;
				}

				if (totalPrice >= 10) {
					int count = totalPrice / 10;
					ItemStack silver = MoneyItem.SILVER.getItemStack();
					silver.setAmount(count);
					player.getInventory().addItem(silver);
					totalPrice -= count * 10;
				}

				if (totalPrice > 0) {
					ItemStack copper = MoneyItem.COPPER.getItemStack();
					copper.setAmount(totalPrice);
					player.getInventory().addItem(copper);
				}

				player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
			}
		}
	}

}
