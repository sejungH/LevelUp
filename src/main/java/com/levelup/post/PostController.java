package com.levelup.post;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.levelup.LevelUp;
import com.levelup.LevelUpItem;
import com.levelup.menu.MenuController;
import com.levelup.menu.MenuIcon;
import com.levelup.menu.MenuUnicode;
import com.levelup.message.MessageController;

import net.md_5.bungee.api.ChatColor;

public class PostController {

	public static final String POSTBOX = "customitems:postbox";
	public static Map<UUID, JsonArray> postItems;

	public static void getPostItems(LevelUp plugin) throws SQLException {
		Connection conn = plugin.mysql.getConnection();
		Map<UUID, JsonArray> postItems = new HashMap<UUID, JsonArray>();

		String sql = "SELECT * FROM post";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();

		while (rs.next()) {
			UUID uuid = UUID.fromString(rs.getString("uuid"));
			JsonArray items = JsonParser.parseString(rs.getString("items")).getAsJsonArray();
			postItems.put(uuid, items);
		}

		rs.close();
		pstmt.close();

		PostController.postItems = postItems;
	}

	public static void initPostItem(LevelUp plugin, UUID uuid) throws SQLException {
		Connection conn = plugin.mysql.getConnection();
		JsonArray jsonArray = new JsonArray();
		String sql = "INSERT INTO post (uuid, items) VALUES (?, ?)";
		PreparedStatement pstmt = conn.prepareStatement(sql);

		pstmt.setString(1, uuid.toString());
		pstmt.setString(2, jsonArray.toString());
		pstmt.executeUpdate();
		pstmt.close();

		postItems.put(uuid, jsonArray);
	}

	public static void addPostItem(LevelUp plugin, UUID uuid, ItemStack item) throws SQLException {
		JsonArray jsonArray = postItems.get(uuid);
		JsonObject itemObject = convertItemToJson(item);
		jsonArray.add(itemObject);

		Connection conn = plugin.mysql.getConnection();
		String sql = "UPDATE post SET items = ? WHERE uuid = ?";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, jsonArray.toString());
		pstmt.setString(2, uuid.toString());
		pstmt.executeUpdate();
		pstmt.close();
	}

	public static JsonObject convertItemToJson(ItemStack item) {
		JsonObject jsonObject = new JsonObject();
		LevelUpItem lvItem = new LevelUpItem(item);
		jsonObject.add("item", lvItem.createItemJson());

		ItemMeta itemMeta = item.getItemMeta();
		if (!itemMeta.getPersistentDataContainer().isEmpty()) {
			JsonObject nbtObject = new JsonObject();
			for (NamespacedKey key : itemMeta.getPersistentDataContainer().getKeys()) {
				String value = itemMeta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
				nbtObject.addProperty("key", key.getKey());
				nbtObject.addProperty("value", value);
			}
			jsonObject.add("nbt", nbtObject);
		}

		return jsonObject;
	}

	public static ItemStack convertJsonToItem(LevelUp plugin, JsonObject json) {
		LevelUpItem item = new LevelUpItem(json.get("item").getAsJsonObject());
		ItemStack itemStack = item.getItemStack();

		if (json.has("nbt")) {
			ItemMeta itemMeta = itemStack.getItemMeta();

			for (JsonElement nbt : json.get("nbt").getAsJsonArray()) {
				JsonObject nbtObj = nbt.getAsJsonObject();
				NamespacedKey key = new NamespacedKey(plugin, nbtObj.get("key").toString());
				String value = nbtObj.get("value").toString();
				itemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING, value);
			}
			itemStack.setItemMeta(itemMeta);
		}
		return itemStack;
	}

	public static void alertPlayer(LevelUp plugin, Player player) {
		if (postItems.containsKey(player.getUniqueId())) {
			MessageController.sendMessage(plugin, player.getUniqueId(), "우편함에 아이템이 도착했습니다!");
		}
	}

	public static void openPostInventory(LevelUp plugin, Player player, int page) {
		Inventory postInv = Bukkit.createInventory((InventoryHolder) player, 36,
				MenuController.getInventoryTitle(MenuUnicode.POST.val()));

		if (postItems.containsKey(player.getUniqueId())) {
			JsonArray jsonArray = postItems.get(player.getUniqueId());
			for (int i = 0; i < 27; i++) {
				int index = page * 27 + i;
				if (jsonArray.size() <= index) {
					break;
				} else if (i == 26) {
					ItemStack next = MenuIcon.NEXT.val().getItemStack();
					ItemMeta nextIM = next.getItemMeta();
					nextIM.setDisplayName(ChatColor.WHITE + "다음으로");
					nextIM.getPersistentDataContainer().set(new NamespacedKey(plugin, "page"), PersistentDataType.INTEGER, page + 1);
					next.setItemMeta(nextIM);
					postInv.setItem(MenuController.slot(3, 8), next);
				}
				postInv.setItem(i, convertJsonToItem(plugin, jsonArray.get(index).getAsJsonObject()));
			}
		}

		if (page > 0) {
			ItemStack prev = MenuIcon.NEXT.val().getItemStack();
			ItemMeta prevIM = prev.getItemMeta();
			prevIM.setDisplayName(ChatColor.WHITE + "이전으로");
			prevIM.getPersistentDataContainer().set(new NamespacedKey(plugin, "page"), PersistentDataType.INTEGER, page - 1);
			prev.setItemMeta(prevIM);
			postInv.setItem(MenuController.slot(3, 8), prev);
		}
		
		player.openInventory(postInv);
	}

}
