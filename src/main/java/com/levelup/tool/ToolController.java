package com.levelup.tool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.Registry;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.levelup.LevelUp;
import com.levelup.LevelUpIcon;
import com.levelup.chat.ChatController;
import com.levelup.menu.MenuController;
import com.levelup.menu.MenuIcon;
import com.levelup.menu.MenuUnicode;
import com.levelup.player.PlayerData;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadableNBTList;
import dev.lone.itemsadder.api.CustomStack;
import net.md_5.bungee.api.ChatColor;

public class ToolController {

	public static final String TOOLBOX_ID = "customitems:toolbox";
	public static final int TOOLBOX_SLOT = 8;

	public static Map<UUID, Map<ToolType, BossBar>> bossBars;
	public static Map<UUID, List<ToolType>> MsgShown;

	public static Map<UUID, ToolData> getTools(LevelUp plugin) throws SQLException {
		Connection conn = plugin.mysql.getConnection();

		String sql = "SELECT p.uuid, "
				+ "pickaxe.name AS pickaxe_name, pickaxe.material AS pickaxe_material, pickaxe.level AS pickaxe_level, pickaxe.exp AS pickaxe_exp, pickaxe.enchantment AS pickaxe_enchantment, pickaxe.customskin AS pickaxe_customskin, "
				+ "axe.name AS axe_name, axe.material AS axe_material, axe.level AS axe_level, axe.exp AS axe_exp, axe.enchantment AS axe_enchantment, axe.customskin AS axe_customskin, "
				+ "sword.name AS sword_name, sword.material AS sword_material, sword.level AS sword_level, sword.exp AS sword_exp, sword.enchantment AS sword_enchantment, sword.customskin AS sword_customskin, "
				+ "shovel.name AS shovel_name, shovel.material AS shovel_material, shovel.level AS shovel_level, shovel.exp AS shovel_exp, shovel.enchantment AS shovel_enchantment, shovel.customskin AS shovel_customskin "
				+ "FROM player p " + "JOIN pickaxe ON p.uuid = pickaxe.uuid " + "JOIN axe ON p.uuid = axe.uuid "
				+ "JOIN sword ON p.uuid = sword.uuid " + "JOIN shovel ON p.uuid = shovel.uuid";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();

		Map<UUID, ToolData> tools = new HashMap<UUID, ToolData>();

		while (rs.next()) {
			UUID uuid = UUID.fromString(rs.getString("uuid"));
			ToolData tool = new ToolData();

			if (rs.getString("pickaxe_material") != null) {
				String name = rs.getString("pickaxe_name");
				Material material = Material.getMaterial(rs.getString("pickaxe_material"));
				int level = rs.getInt("pickaxe_level");
				int exp = rs.getInt("pickaxe_exp");
				Map<Enchantment, Integer> enchantment = parseEnchantment(rs.getString("pickaxe_enchantment"));
				String customskin = rs.getString("pickaxe_customskin");
				tool.setPickaxe(new PickaxeData(plugin, uuid, name, material, level, exp, enchantment, customskin));
			}

			if (rs.getString("axe_material") != null) {
				String name = rs.getString("axe_name");
				Material material = Material.getMaterial(rs.getString("axe_material"));
				int level = rs.getInt("axe_level");
				int exp = rs.getInt("axe_exp");
				Map<Enchantment, Integer> enchantment = parseEnchantment(rs.getString("axe_enchantment"));
				String customskin = rs.getString("axe_customskin");
				tool.setAxe(new AxeData(plugin, uuid, name, material, level, exp, enchantment, customskin));
			}

			if (rs.getString("sword_material") != null) {
				String name = rs.getString("sword_name");
				Material material = Material.getMaterial(rs.getString("sword_material"));
				int level = rs.getInt("sword_level");
				int exp = rs.getInt("sword_exp");
				Map<Enchantment, Integer> enchantment = parseEnchantment(rs.getString("sword_enchantment"));
				String customskin = rs.getString("sword_customskin");
				tool.setSword(new SwordData(plugin, uuid, name, material, level, exp, enchantment, customskin));
			}

			if (rs.getString("shovel_material") != null) {
				String name = rs.getString("shovel_name");
				Material material = Material.getMaterial(rs.getString("shovel_material"));
				int level = rs.getInt("shovel_level");
				int exp = rs.getInt("shovel_exp");
				Map<Enchantment, Integer> enchantment = parseEnchantment(rs.getString("shovel_enchantment"));
				String customskin = rs.getString("shovel_customskin");
				tool.setShovel(new ShovelData(plugin, uuid, name, material, level, exp, enchantment, customskin));
			}

			tools.put(uuid, tool);
		}

		rs.close();
		pstmt.close();

		plugin.getServer().getConsoleSender().sendMessage("[" + plugin.getName() + "] " + ChatColor.GREEN + "Loaded "
				+ ChatColor.YELLOW + tools.size() + ChatColor.GREEN + " Tool Data");

		return tools;
	}

	public static Map<UUID, Map<ToolType, Map<Material, Integer>>> getQuests(LevelUp plugin) throws SQLException {
		Connection conn = plugin.mysql.getConnection();

		String sql = "SELECT * FROM quest";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();

		Map<UUID, Map<ToolType, Map<Material, Integer>>> quests = new HashMap<UUID, Map<ToolType, Map<Material, Integer>>>();

		while (rs.next()) {
			UUID uuid = UUID.fromString(rs.getString("uuid"));

			JsonObject json = JsonParser.parseString(rs.getString("items")).getAsJsonObject();
			Map<ToolType, Map<Material, Integer>> tools = parseJsonToQuests(json);

			quests.put(uuid, tools);
		}

		rs.close();
		pstmt.close();

		plugin.getServer().getConsoleSender().sendMessage("[" + plugin.getName() + "] " + ChatColor.GREEN + "Loaded "
				+ ChatColor.YELLOW + quests.size() + ChatColor.GREEN + " Quest Data");

		return quests;
	}

	@SuppressWarnings("unchecked")
	public static Map<ToolType, Map<Material, List<ToolQuest>>> parseToolQuest(Map<String, Object> yaml) {
		Map<ToolType, Map<Material, List<ToolQuest>>> toolQuest = new HashMap<ToolType, Map<Material, List<ToolQuest>>>();

		for (String toolType : yaml.keySet()) {
			Map<Material, List<ToolQuest>> tool = new HashMap<Material, List<ToolQuest>>();

			for (Entry<String, Object> material : ((Map<String, Object>) yaml.get(toolType)).entrySet()) {

				List<ToolQuest> levels = new ArrayList<ToolQuest>();
				for (Object levelObj : (List<Object>) material.getValue()) {
					Map<String, Object> l = (Map<String, Object>) levelObj;
					int level = Integer.parseInt(l.get("level").toString());
					int exp = Integer.parseInt(l.get("exp").toString());
					int required = Integer.parseInt(l.get("required").toString());
					Map<Material, Integer> quests = new HashMap<Material, Integer>();
					for (Object questObj : (List<Object>) l.get("quest")) {
						Map<String, Object> q = (Map<String, Object>) questObj;
						quests.put(Material.getMaterial(q.get("material").toString().toUpperCase()),
								Integer.parseInt(q.get("quantity").toString()));
					}
					levels.add(new ToolQuest(level, exp, required, quests));
				}

				tool.put(Material.getMaterial(material.getKey().toUpperCase()), levels);
			}

			toolQuest.put(ToolType.get(toolType), tool);
		}

		return toolQuest;
	}

	@SuppressWarnings("unchecked")
	public static Map<ToolType, Map<Material, Integer>> parseToolExp(Map<String, Object> yaml) {
		Map<ToolType, Map<Material, Integer>> exps = new HashMap<ToolType, Map<Material, Integer>>();
		for (String toolType : yaml.keySet()) {
			Map<Material, Integer> tool = new HashMap<Material, Integer>();
			for (Object obj : (List<Object>) yaml.get(toolType)) {
				Map<String, Object> exp = (Map<String, Object>) obj;
				tool.put(Material.getMaterial(exp.get("material").toString().toUpperCase()),
						Integer.parseInt(exp.get("exp").toString()));
			}

			exps.put(ToolType.get(toolType), tool);
		}

		return exps;
	}

	@SuppressWarnings("unchecked")
	public static Map<Material, Entry<Character, String>> parseToolQuestItems(Map<String, Object> yaml) {
		Map<Material, Entry<Character, String>> toolQuestItems = new HashMap<Material, Entry<Character, String>>();
		for (Object obj : (List<Object>) yaml.get("Items")) {
			Map<String, Object> item = (Map<String, Object>) obj;
			Material material = Material.getMaterial(item.get("material").toString().toUpperCase());
			char character = decode(item.get("char").toString()).charAt(0);
			String korean = item.get("kr").toString();
			toolQuestItems.put(material, Map.entry(character, korean));
		}

		return toolQuestItems;
	}

	@SuppressWarnings("unchecked")
	public static Map<ToolType, Map<Material, List<ToolQuestMessage>>> parseToolQuestMessage(Map<String, Object> yaml) {
		Map<ToolType, Map<Material, List<ToolQuestMessage>>> toolQuestMessage = new HashMap<ToolType, Map<Material, List<ToolQuestMessage>>>();

		for (String toolType : yaml.keySet()) {
			Map<Material, List<ToolQuestMessage>> tool = new HashMap<Material, List<ToolQuestMessage>>();

			for (Entry<String, Object> material : ((Map<String, Object>) yaml.get(toolType)).entrySet()) {
				List<ToolQuestMessage> levels = new ArrayList<ToolQuestMessage>();

				for (Object levelObj : (List<Object>) material.getValue()) {
					Map<String, Object> l = (Map<String, Object>) levelObj;
					int level = Integer.parseInt(l.get("level").toString());
					List<String> quest = new ArrayList<String>();
					for (Object questObj : (List<Object>) l.get("quest")) {
						quest.add(questObj.toString());
					}
					List<String> complete = new ArrayList<String>();
					for (Object completeObj : (List<Object>) l.get("complete")) {
						complete.add(completeObj.toString());
					}

					levels.add(new ToolQuestMessage(level, quest, complete));
				}

				tool.put(Material.getMaterial(material.getKey().toUpperCase()), levels);
			}

			toolQuestMessage.put(ToolType.get(toolType), tool);
		}

		return toolQuestMessage;
	}

	public static void getNewTools(LevelUp plugin, Player player) throws SQLException {
		ToolData tool = new ToolData();
		tool.setPickaxe(new PickaxeData(plugin, player.getUniqueId(), Material.WOODEN_PICKAXE));
		tool.setAxe(new AxeData(plugin, player.getUniqueId(), Material.WOODEN_AXE));
		tool.setSword(new SwordData(plugin, player.getUniqueId(), Material.WOODEN_SWORD));
		tool.setShovel(new ShovelData(plugin, player.getUniqueId(), Material.WOODEN_SHOVEL));
		plugin.tools.put(player.getUniqueId(), tool);

		addTools(plugin, tool);
	}

	public static void addTools(LevelUp plugin, ToolData tool) throws SQLException {
		Connection conn = plugin.mysql.getConnection();

		PickaxeData pickaxe = tool.getPickaxe();
		String sql = "INSERT INTO pickaxe (uuid, name, material, level, exp, enchantment, customskin) VALUES (?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, pickaxe.getUuid().toString());
		pstmt.setString(2, pickaxe.getName());
		pstmt.setString(3, pickaxe.getMaterial().toString());
		pstmt.setInt(4, pickaxe.getLevel());
		pstmt.setInt(5, pickaxe.getExp());
		pstmt.setString(6, pickaxe.getEnchantmentJSON());
		pstmt.setString(7, pickaxe.getCustomskin());
		pstmt.executeUpdate();

		AxeData axe = tool.getAxe();
		sql = "INSERT INTO axe (uuid, name, material, level, exp, enchantment, customskin) VALUES (?, ?, ?, ?, ?, ?, ?)";
		pstmt.clearParameters();
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, axe.getUuid().toString());
		pstmt.setString(2, axe.getName());
		pstmt.setString(3, axe.getMaterial().toString());
		pstmt.setInt(4, axe.getLevel());
		pstmt.setInt(5, axe.getExp());
		pstmt.setString(6, axe.getEnchantmentJSON());
		pstmt.setString(7, axe.getCustomskin());
		pstmt.executeUpdate();

		SwordData sword = tool.getSword();
		sql = "INSERT INTO sword (uuid, name, material, level, exp, enchantment, customskin) VALUES (?, ?, ?, ?, ?, ?, ?)";
		pstmt.clearParameters();
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, sword.getUuid().toString());
		pstmt.setString(2, sword.getName());
		pstmt.setString(3, sword.getMaterial().toString());
		pstmt.setInt(4, sword.getLevel());
		pstmt.setInt(5, sword.getExp());
		pstmt.setString(6, sword.getEnchantmentJSON());
		pstmt.setString(7, sword.getCustomskin());
		pstmt.executeUpdate();

		ShovelData shovel = tool.getShovel();
		sql = "INSERT INTO shovel (uuid, name, material, level, exp, enchantment, customskin) VALUES (?, ?, ?, ?, ?, ?, ?)";
		pstmt.clearParameters();
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, shovel.getUuid().toString());
		pstmt.setString(2, shovel.getName());
		pstmt.setString(3, shovel.getMaterial().toString());
		pstmt.setInt(4, shovel.getLevel());
		pstmt.setInt(5, shovel.getExp());
		pstmt.setString(6, shovel.getEnchantmentJSON());
		pstmt.setString(7, shovel.getCustomskin());
		pstmt.executeUpdate();

		pstmt.close();
	}

	public static void initQuest(LevelUp plugin, UUID uuid) throws SQLException {
		Connection conn = plugin.mysql.getConnection();

		Map<ToolType, Map<Material, Integer>> quests = new HashMap<ToolType, Map<Material, Integer>>();
		quests.put(ToolType.PICKAXE, new HashMap<Material, Integer>());
		quests.put(ToolType.AXE, new HashMap<Material, Integer>());
		quests.put(ToolType.SWORD, new HashMap<Material, Integer>());
		quests.put(ToolType.SHOVEL, new HashMap<Material, Integer>());

		JsonObject json = convertQuestsToJson(quests);

		String sql = "INSERT INTO quest (uuid, items) VALUES (?, ?)";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, uuid.toString());
		pstmt.setString(2, json.toString());
		pstmt.executeUpdate();

		pstmt.close();

		plugin.quests.put(uuid, quests);
	}

	public static void updateQuest(LevelUp plugin, UUID uuid) throws SQLException {
		Connection conn = plugin.mysql.getConnection();
		JsonObject json = convertQuestsToJson(plugin.quests.get(uuid));

		String sql = "UPDATE quest SET items = ? WHERE uuid = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, json.toString());
		pstmt.setString(2, uuid.toString());
		pstmt.executeUpdate();

		pstmt.close();
	}

	public static JsonObject convertQuestsToJson(Map<ToolType, Map<Material, Integer>> quests) {
		JsonObject json = new JsonObject();

		for (Entry<ToolType, Map<Material, Integer>> quest : quests.entrySet()) {
			JsonObject materials = new JsonObject();

			for (Entry<Material, Integer> material : quest.getValue().entrySet()) {
				materials.addProperty(material.getKey().toString(), material.getValue());
			}

			json.add(quest.getKey().toString(), materials);
		}

		return json;
	}

	public static Map<ToolType, Map<Material, Integer>> parseJsonToQuests(JsonObject json) {
		Map<ToolType, Map<Material, Integer>> quests = new HashMap<ToolType, Map<Material, Integer>>();

		for (Entry<String, JsonElement> quest : json.entrySet()) {
			Map<Material, Integer> materials = new HashMap<Material, Integer>();

			for (Entry<String, JsonElement> material : quest.getValue().getAsJsonObject().entrySet()) {
				materials.put(Material.valueOf(material.getKey()), material.getValue().getAsInt());
			}

			quests.put(ToolType.get(quest.getKey()), materials);
		}

		return quests;
	}

	public static Map<Enchantment, Integer> parseEnchantment(String json) {
		if (json == null) {
			return new HashMap<Enchantment, Integer>();

		} else {
			Map<Enchantment, Integer> enchantment = new HashMap<Enchantment, Integer>();
			JsonArray jsonArray = JsonParser.parseString(json).getAsJsonArray();

			for (JsonElement element : jsonArray) {
				JsonObject obj = element.getAsJsonObject();
				NamespacedKey namespacedKey = NamespacedKey.minecraft(obj.get("type").getAsString().split(":")[1]);
				Enchantment type = Registry.ENCHANTMENT.get(namespacedKey);
				int level = obj.get("level").getAsInt();
				enchantment.put(type, level);
			}

			return enchantment;
		}
	}

	public static void updateToolData(LevelUp plugin, UUID uuid, ToolAbstract tool) throws SQLException {
		if (ToolType.get(tool.getMaterial()) != null) {
			Connection conn = plugin.mysql.getConnection();

			String sql = "UPDATE " + ToolType.get(tool.getMaterial()).toString()
					+ " SET name = ?, material = ?, level = ?, exp = ?, enchantment = ?, customskin = ? WHERE uuid = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);

			if (tool.getName() != null)
				pstmt.setString(1, tool.getName());
			else
				pstmt.setNull(1, java.sql.Types.NULL);

			pstmt.setString(2, tool.getMaterial().toString());
			pstmt.setInt(3, tool.getLevel());
			pstmt.setInt(4, tool.getExp());
			pstmt.setString(5, tool.getEnchantmentJSON());

			if (tool.getCustomskin() != null)
				pstmt.setString(6, tool.getCustomskin());
			else
				pstmt.setNull(6, java.sql.Types.NULL);

			pstmt.setString(7, uuid.toString());

			pstmt.executeUpdate();
		}
	}

	public static void updateToolName(LevelUp plugin, UUID uuid, String name, ToolType type) throws SQLException {
		if (type != null) {
			Connection conn = plugin.mysql.getConnection();

			String sql = "UPDATE " + type.toString() + " SET name = ? WHERE uuid = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, name);
			pstmt.setString(2, uuid.toString());
			pstmt.executeUpdate();

			ToolData toolData = plugin.tools.get(uuid);
			ToolAbstract tool = toolData.getTool(type);
			tool.setName(name);
		}
	}

	public static void updateToolExp(LevelUp plugin) throws SQLException {
		int count = 0;
		for (UUID uuid : plugin.tools.keySet()) {
			PlayerData pd = plugin.players.get(uuid);
			OfflinePlayer player = plugin.getServer().getOfflinePlayer(uuid);

			if (player.isOnline() || pd.getLastOnline().until(LocalDateTime.now(), ChronoUnit.MINUTES) < 10) {
				updateToolExp(plugin, pd.getUuid());
				count++;
			}
		}
		plugin.getLogger().info("Updated " + count + " Tool Exp");
	}

	public static void updateToolExp(LevelUp plugin, UUID uuid) throws SQLException {
		Connection conn = plugin.mysql.getConnection();

		ToolData toolData = plugin.tools.get(uuid);

		PickaxeData pickaxe = toolData.getPickaxe();
		String sql = "UPDATE pickaxe SET exp = ? WHERE uuid = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, pickaxe.getExp());
		pstmt.setString(2, uuid.toString());
		pstmt.executeUpdate();

		pstmt.clearParameters();
		AxeData axe = toolData.getAxe();
		sql = "UPDATE axe SET exp = ? WHERE uuid = ?";
		pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, axe.getExp());
		pstmt.setString(2, uuid.toString());
		pstmt.executeUpdate();

		pstmt.clearParameters();
		SwordData sword = toolData.getSword();
		sql = "UPDATE sword SET exp = ? WHERE uuid = ?";
		pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, sword.getExp());
		pstmt.setString(2, uuid.toString());
		pstmt.executeUpdate();

		pstmt.clearParameters();
		ShovelData shovel = toolData.getShovel();
		sql = "UPDATE shovel SET exp = ? WHERE uuid = ?";
		pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, shovel.getExp());
		pstmt.setString(2, uuid.toString());
		pstmt.executeUpdate();

		pstmt.close();
	}

	public static void updateToolEnchantment(LevelUp plugin, UUID uuid, ToolType type) throws SQLException {
		Connection conn = plugin.mysql.getConnection();
		ToolAbstract tool = plugin.tools.get(uuid).getTool(type);

		String sql = "UPDATE " + type.toString().toLowerCase() + " SET enchantment = ? WHERE uuid = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, tool.getEnchantmentJSON());
		pstmt.setString(2, uuid.toString());
		pstmt.executeUpdate();

		pstmt.close();
	}

	public static void getNewBossBars(LevelUp plugin, Player player) {
		Map<ToolType, BossBar> bars = new HashMap<ToolType, BossBar>();
		ToolType[] types = { ToolType.PICKAXE, ToolType.AXE, ToolType.SWORD, ToolType.SHOVEL };
		for (ToolType type : types) {
			BossBar bossBar = Bukkit.createBossBar(type.valueKor() + " 경험치", BarColor.BLUE, BarStyle.SOLID,
					new BarFlag[0]);
			bossBar.setVisible(false);
			bossBar.addPlayer(player);
			bars.put(type, bossBar);
		}
		bossBars.put(player.getUniqueId(), bars);
	}

	public static Inventory getSkinTicketInventory(LevelUp plugin, Player player) {
		Inventory ticketInv = Bukkit.createInventory((InventoryHolder) player, 27,
				MenuController.getInventoryTitle(MenuUnicode.SKIN_TICKET.val()));

		ItemStack checkBtn = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta checkIM = checkBtn.getItemMeta();
		checkIM.setDisplayName(ChatColor.of("#fc739e") + "스킨 적용하기");
		checkBtn.setItemMeta(checkIM);
		ticketInv.setItem(MenuController.slot(1, 4), checkBtn);
		ticketInv.setItem(MenuController.slot(1, 5), checkBtn);
		ticketInv.setItem(MenuController.slot(1, 6), checkBtn);

		return ticketInv;
	}

	public static boolean applyToolSkin(LevelUp plugin, UUID uuid, String namespacedID) throws SQLException {
		String regex = "(\\w+):(\\w+)_(pickaxe|axe|sword|shovel)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(namespacedID);

		if (matcher.matches()) {
			String namespace = matcher.group(1);
			String prefix = matcher.group(2);
			String toolType = matcher.group(3);

			ToolAbstract tool = plugin.tools.get(uuid).getTool(ToolType.get(toolType));

			String newNamespacedID = null;
			if (tool.getMaterial().toString().toUpperCase().contains("WOODEN")) {
				newNamespacedID = namespace + ":" + prefix + "_wooden_" + toolType;

			} else if (tool.getMaterial().toString().toUpperCase().contains("STONE")) {
				newNamespacedID = namespace + ":" + prefix + "_stone_" + toolType;

			} else if (tool.getMaterial().toString().toUpperCase().contains("IRON")) {
				newNamespacedID = namespace + ":" + prefix + "_iron_" + toolType;

			} else if (tool.getMaterial().toString().toUpperCase().contains("DIAMOND")) {
				newNamespacedID = namespace + ":" + prefix + "_diamond_" + toolType;

			} else if (tool.getMaterial().toString().toUpperCase().contains("NETHERITE")) {
				newNamespacedID = namespace + ":" + prefix + "_netherite_" + toolType;
			}

			if (newNamespacedID != null) {
				OfflinePlayer player = plugin.getServer().getOfflinePlayer(uuid);
				Inventory playerInv = ((Player) player).getInventory();
				int slot = -1;

				if (player.isOnline()) {
					if (playerInv.contains(tool.getAsItemStack())) {
						for (int i = 0; i < playerInv.getContents().length; i++) {
							if (tool.equals(playerInv.getItem(i))) {
								slot = i;
								break;
							}
						}
					}
				}
				LevelUpIcon oldIcon = tool.getIcon();
				tool.setCustomstkin(newNamespacedID);

				CustomStack customItem = CustomStack.getInstance(namespacedID);
				NBT.get(customItem.getItemStack(), nbt -> {
					String newName = "";
					if (tool.getName() == null) {
						newName = tool.getMaterial().toString().toUpperCase();
						newName = newName.replace("WOODEN_", "나무 ").replace("STONE_", "돌 ").replace("IRON_", "철 ")
								.replace("DIAMOND_", "다이아몬드 ").replace("NETHERITE_", "네더라이트 ");
						newName = newName.replace("PICKAXE", "곡괭이").replace("AXE", "도끼").replace("SWORD", "검")
								.replace("SHOVEL", "삽");

					} else {
						newName = tool.getName();

						if (oldIcon != null) {
							newName = newName.replace(String.valueOf(oldIcon.val()), "").strip();
						}
					}

					if (nbt.hasTag("ToolColor")) {
						ReadableNBTList<String> toolColor = (ReadableNBTList<String>) nbt.getStringList("ToolColor");
						List<ChatColor> gradient = new ArrayList<ChatColor>();

						for (String colorCode : toolColor) {
							gradient.add(ChatColor.of(colorCode));
						}

						newName = ChatController.gradient(newName, gradient);
					}

					LevelUpIcon icon = LevelUpIcon.valueOf(prefix.toUpperCase());
					if (icon != null) {
						newName = ChatColor.WHITE + Character.toString(icon.val()) + " " + newName;
					}

					tool.setName(newName);
				});

				updateToolData(plugin, uuid, tool);

				if (slot >= 0) {
					playerInv.clear(slot);
					playerInv.setItem(slot, tool.getAsItemStack());
				}

				return true;
			}
		}

		return false;
	}

	public static void toolUpgrade(LevelUp plugin, Player player, ToolType type) throws SQLException {
		ToolAbstract tool = plugin.tools.get(player.getUniqueId()).getTool(type);

		if (tool.getLevel() + 1 == plugin.toolQuest.get(type).get(tool.getMaterial()).size()
				&& !tool.getMaterial().toString().toUpperCase().contains("NETHERITE")) {
			ItemStack toolItem = tool.getAsItemStack();

			if (tool.getMaterial().toString().toUpperCase().contains("WOODEN")) {
				tool.setMaterial(
						Material.valueOf(tool.getMaterial().toString().toUpperCase().replace("WOODEN", "STONE")));

			} else if (tool.getMaterial().toString().toUpperCase().contains("STONE")) {
				tool.setMaterial(
						Material.valueOf(tool.getMaterial().toString().toUpperCase().replace("STONE", "IRON")));

			} else if (tool.getMaterial().toString().toUpperCase().contains("IRON")) {
				tool.setMaterial(
						Material.valueOf(tool.getMaterial().toString().toUpperCase().replace("IRON", "DIAMOND")));

			} else if (tool.getMaterial().toString().toUpperCase().contains("DIAMOND")) {
				tool.setMaterial(
						Material.valueOf(tool.getMaterial().toString().toUpperCase().replace("DIAMOND", "NETHERITE")));
			}

			if (tool.getCustomskin() != null) {
				String regex = "(\\w+):(\\w+)_(wooden|stone|iron|diamond|netherite)_(pickaxe|axe|sword|shovel)";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(tool.getCustomskin());

				if (matcher.matches()) {
					String namespace = matcher.group(1);
					String prefix = matcher.group(2);
					String material = matcher.group(3);
					String toolType = matcher.group(4);

					String newToolSkin = null;
					if (material.equalsIgnoreCase("wooden")) {
						newToolSkin = namespace + ":" + prefix + "_stone_" + toolType;

					} else if (material.equalsIgnoreCase("stone")) {
						newToolSkin = namespace + ":" + prefix + "_iron_" + toolType;

					} else if (material.equalsIgnoreCase("iron")) {
						newToolSkin = namespace + ":" + prefix + "_diamond_" + toolType;

					} else if (material.equalsIgnoreCase("diamond")) {
						newToolSkin = namespace + ":" + prefix + "_netherite_" + toolType;
					}

					if (newToolSkin != null) {
						tool.setCustomstkin(newToolSkin);

						CustomStack customItem = CustomStack.getInstance(newToolSkin);
						NBT.get(customItem.getItemStack(), nbt -> {
							String name = ChatColor.stripColor(tool.getName());
							String regexName = "(.) (나무|돌|철|다이아몬드|네더라이트) (곡괭이|도끼|검|삽)";
							Pattern patternName = Pattern.compile(regexName);
							Matcher matcherName = patternName.matcher(name);

							if (matcherName.matches()) {
								String icon = matcherName.group(1);
								String materialKr = matcherName.group(2);
								String toolTypeKr = matcherName.group(3);

								String newName = null;
								if (materialKr.equals("나무")) {
									newName = "돌 " + toolTypeKr;

								} else if (materialKr.equals("돌")) {
									newName = "철 " + toolTypeKr;

								} else if (materialKr.equals("철")) {
									newName = "다이아몬드 " + toolTypeKr;

								} else if (materialKr.equals("다이아몬드")) {
									newName = "네더라이트 " + toolTypeKr;
								}

								if (newName != null) {
									if (nbt.hasTag("ToolColor")) {
										ReadableNBTList<String> toolColor = (ReadableNBTList<String>) nbt
												.getStringList("ToolColor");
										List<ChatColor> gradient = new ArrayList<ChatColor>();

										for (String colorCode : toolColor) {
											gradient.add(ChatColor.of(colorCode));
										}

										newName = ChatController.gradient(newName, gradient);
									}

									tool.setName(icon + " " + newName);
								}
							}
						});
					}
				}
			}

			if (player.getInventory().contains(toolItem)) {
				player.getInventory().remove(toolItem);
				player.getInventory().addItem(tool.getAsItemStack());
			}

			tool.setLevel(0);

			ToolController.updateToolData(plugin, player.getUniqueId(), tool);

		} else {
			tool.setLevel(tool.getLevel() + 1);
			ToolController.updateToolData(plugin, player.getUniqueId(), tool);
		}
	}

	public static void toolEnchantment(LevelUp plugin, Player player, ToolType type,
			Map<Enchantment, Integer> enchantment) throws SQLException {
		ToolAbstract tool = plugin.tools.get(player.getUniqueId()).getTool(type);
		ItemStack toolItem = tool.getAsItemStack();

		tool.setEnchantment(enchantment);
		updateToolEnchantment(plugin, player.getUniqueId(), type);

		if (player.getInventory().contains(toolItem)) {
			player.getInventory().remove(toolItem);
			player.getInventory().addItem(tool.getAsItemStack());
		}

	}

	private static String decode(String input) {
		Pattern pattern = Pattern.compile("\\\\u[0-9a-fA-F]{4}");
		Matcher matcher = pattern.matcher(input);

		StringBuilder decodedString = new StringBuilder();

		while (matcher.find()) {
			String unicodeSequence = matcher.group();
			char unicodeChar = (char) Integer.parseInt(unicodeSequence.substring(2), 16);
			matcher.appendReplacement(decodedString, Character.toString(unicodeChar));
		}

		matcher.appendTail(decodedString);
		return decodedString.toString();
	}
}
