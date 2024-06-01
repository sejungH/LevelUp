package com.levelup.tool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.levelup.LevelUp;

import net.md_5.bungee.api.ChatColor;

public class ToolController {

	public static final String TOOLBOX_ID = "customitems:toolbox";
	public static final int TOOLBOX_SLOT = 8;

	public static Map<UUID, ToolData> getTools(LevelUp plugin) throws SQLException {
		Connection conn = plugin.mysql.getConnection();

		String sql = "SELECT p.uuid, "
				+ "pickaxe.name AS pickaxe_name, pickaxe.material AS pickaxe_material, pickaxe.enchantment AS pickaxe_enchantment, pickaxe.customskin AS pickaxe_customskin, "
				+ "axe.name AS axe_name, axe.material AS axe_material, axe.enchantment AS axe_enchantment, axe.customskin AS axe_customskin, "
				+ "sword.name AS sword_name, sword.material AS sword_material, sword.enchantment AS sword_enchantment, sword.customskin AS sword_customskin, "
				+ "shovel.name AS shovel_name, shovel.material AS shovel_material, shovel.enchantment AS shovel_enchantment, shovel.customskin AS shovel_customskin "
				+ "FROM player p " + "JOIN pickaxe ON p.uuid = pickaxe.uuid "
				+ "JOIN axe ON p.uuid = axe.uuid " + "JOIN sword ON p.uuid = sword.uuid "
				+ "JOIN shovel ON p.uuid = shovel.uuid";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();

		Map<UUID, ToolData> tools = new HashMap<UUID, ToolData>();

		while (rs.next()) {
			UUID uuid = UUID.fromString(rs.getString("uuid"));
			ToolData tool = new ToolData();

			if (rs.getString("pickaxe_material") != null) {
				String name = rs.getString("pickaxe_name");
				Material material = Material.getMaterial(rs.getString("pickaxe_material"));
				Map<Enchantment, Integer> enchantment = parseEnchantment(rs.getString("pickaxe_enchantment"));
				String customskin = rs.getString("pickaxe_customskin");
				tool.setPickaxe(new PickaxeData(uuid, name, material, enchantment, customskin));
			}

			if (rs.getString("axe_material") != null) {
				String name = rs.getString("axe_name");
				Material material = Material.getMaterial(rs.getString("axe_material"));
				Map<Enchantment, Integer> enchantment = parseEnchantment(rs.getString("axe_enchantment"));
				String customskin = rs.getString("axe_customskin");
				tool.setAxe(new AxeData(uuid, name, material, enchantment, customskin));
			}

			if (rs.getString("sword_material") != null) {
				String name = rs.getString("sword_name");
				Material material = Material.getMaterial(rs.getString("sword_material"));
				Map<Enchantment, Integer> enchantment = parseEnchantment(rs.getString("sword_enchantment"));
				String customskin = rs.getString("sword_customskin");
				tool.setSword(new SwordData(uuid, name, material, enchantment, customskin));
			}

			if (rs.getString("shovel_material") != null) {
				String name = rs.getString("shovel_name");
				Material material = Material.getMaterial(rs.getString("shovel_material"));
				Map<Enchantment, Integer> enchantment = parseEnchantment(rs.getString("shovel_enchantment"));
				String customskin = rs.getString("shovel_customskin");
				tool.setShovel(new ShovelData(uuid, name, material, enchantment, customskin));
			}

			tools.put(uuid, tool);
		}

		rs.close();
		pstmt.close();
		
		plugin.getServer().getConsoleSender().sendMessage("[" + plugin.getName() + "] " + ChatColor.GREEN + "Loaded " + ChatColor.YELLOW + tools.size() + ChatColor.GREEN + " Tool Data");

		return tools;
	}

	public static void getNewTools(LevelUp plugin, Player player) throws SQLException {
		ToolData tool = new ToolData();
		tool.setPickaxe(new PickaxeData(player.getUniqueId(), Material.WOODEN_PICKAXE));
		tool.setAxe(new AxeData(player.getUniqueId(), Material.WOODEN_AXE));
		tool.setSword(new SwordData(player.getUniqueId(), Material.WOODEN_SWORD));
		tool.setShovel(new ShovelData(player.getUniqueId(), Material.WOODEN_SHOVEL));
		plugin.tools.put(player.getUniqueId(), tool);
		
		addTools(plugin, tool);
	}

	public static void addTools(LevelUp plugin, ToolData tool) throws SQLException {
		Connection conn = plugin.mysql.getConnection();

		PickaxeData pickaxe = tool.getPickaxe();
		String sql = "INSERT INTO pickaxe (uuid, name, material, enchantment, customskin) VALUES (?, ?, ?, ?, ?)";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, pickaxe.getUuid().toString());
		pstmt.setString(2, pickaxe.getName());
		pstmt.setString(3, pickaxe.getMaterial().toString());
		pstmt.setString(4, pickaxe.getEnchantmentJSON());
		pstmt.setString(5, pickaxe.getCustomskin());
		pstmt.executeUpdate();

		AxeData axe = tool.getAxe();
		sql = "INSERT INTO axe (uuid, name, material, enchantment, customskin) VALUES (?, ?, ?, ?, ?)";
		pstmt.clearParameters();
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, axe.getUuid().toString());
		pstmt.setString(2, axe.getName());
		pstmt.setString(3, axe.getMaterial().toString());
		pstmt.setString(4, axe.getEnchantmentJSON());
		pstmt.setString(5, axe.getCustomskin());
		pstmt.executeUpdate();

		SwordData sword = tool.getSword();
		sql = "INSERT INTO sword (uuid, name, material, enchantment, customskin) VALUES (?, ?, ?, ?, ?)";
		pstmt.clearParameters();
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, sword.getUuid().toString());
		pstmt.setString(2, sword.getName());
		pstmt.setString(3, sword.getMaterial().toString());
		pstmt.setString(4, sword.getEnchantmentJSON());
		pstmt.setString(5, sword.getCustomskin());
		pstmt.executeUpdate();

		ShovelData shovel = tool.getShovel();
		sql = "INSERT INTO shovel (uuid, name, material, enchantment, customskin) VALUES (?, ?, ?, ?, ?)";
		pstmt.clearParameters();
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, shovel.getUuid().toString());
		pstmt.setString(2, shovel.getName());
		pstmt.setString(3, shovel.getMaterial().toString());
		pstmt.setString(4, shovel.getEnchantmentJSON());
		pstmt.setString(5, shovel.getCustomskin());
		pstmt.executeUpdate();

		pstmt.close();
	}

	private static Map<Enchantment, Integer> parseEnchantment(String json) {
		if (json == null) {
			return new HashMap<Enchantment, Integer>();

		} else {
			Map<Enchantment, Integer> enchantment = new HashMap<Enchantment, Integer>();
			JsonArray jsonArray = JsonParser.parseString(json).getAsJsonArray();

			for (JsonElement element : jsonArray) {
				JsonObject obj = element.getAsJsonObject();
				NamespacedKey namespacedKey = NamespacedKey.minecraft(obj.get("type").getAsString());
				Enchantment type = Registry.ENCHANTMENT.get(namespacedKey);
				int level = obj.get("level").getAsInt();
				enchantment.put(type, level);
			}

			return enchantment;
		}
	}
}
