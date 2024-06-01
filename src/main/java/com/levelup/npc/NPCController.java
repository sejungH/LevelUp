package com.levelup.npc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
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
import com.levelup.menu.MenuController;
import com.levelup.menu.MenuUnicode;

import dev.lone.itemsadder.api.CustomStack;
import net.md_5.bungee.api.ChatColor;

public class NPCController {

	public static Map<UUID, List<NPCTrade>> getNPCs(LevelUp plugin) throws SQLException {
		Connection conn = plugin.mysql.getConnection();

		String sql = "SELECT * FROM npc";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();

		Map<UUID, List<NPCTrade>> npcs = new HashMap<UUID, List<NPCTrade>>();

		while (rs.next()) {
			UUID uuid = UUID.fromString(rs.getString("uuid"));
			npcs.put(uuid, getTradeList(rs.getString("trades")));
		}
		
		rs.close();
		pstmt.close();
		
		plugin.getServer().getConsoleSender().sendMessage("[" + plugin.getName() + "] " + ChatColor.GREEN + "Loaded " + ChatColor.YELLOW + npcs.size() + ChatColor.GREEN + " NPC Data");
		
		return npcs;
	}

	public static void addNPC(LevelUp plugin, UUID uuid) throws SQLException {
		Connection conn = plugin.mysql.getConnection();
		
		String sql = "INSERT INTO npc (uuid) VALUES (?)";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, uuid.toString());
		pstmt.executeUpdate();
		pstmt.close();
		
		plugin.getLogger().info("새로운 NPC [" + uuid + "] 을(를) 데이터베이스에 추가되었습니다.");
	}

	public static void deleteNPC(LevelUp plugin, UUID uuid) throws SQLException {
		Connection conn = plugin.mysql.getConnection();
		
		String sql = "DELETE FROM npc WHERE uuid = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, uuid.toString());
		pstmt.executeUpdate();
		pstmt.close();

		plugin.getLogger().info("NPC [" + uuid + "] 이(가) 삭제되었습니다.");
		
		plugin.npcs.remove(uuid);
	}

	public static void setTradeList(LevelUp plugin, UUID uuid, List<NPCTrade> tradeList) throws SQLException {
		Connection conn = plugin.mysql.getConnection();
		
		String sql = "UPDATE npc SET trades = ? WHERE uuid = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, tradeList.toString());
		pstmt.setString(2, uuid.toString());
		pstmt.executeUpdate();
		pstmt.close();
	}
	
	public static List<NPCTrade> getTradeList(String json) {
		List<NPCTrade> tradeList = new ArrayList<NPCTrade>();
		JsonArray jsonArray = JsonParser.parseString(json).getAsJsonArray();

		for (JsonElement element : jsonArray) {
			JsonObject obj = element.getAsJsonObject();
			
			JsonObject item1JSON = obj.get("item1").getAsJsonObject();
			NPCTradeItem item1 = new NPCTradeItem(
					item1JSON.get("namespacedID").isJsonNull() ? null : item1JSON.get("namespacedID").getAsString(),
					item1JSON.get("material").isJsonNull() ? null : item1JSON.get("material").getAsString(),
					item1JSON.get("count").getAsInt());

			NPCTradeItem item2 = null;
			if (!obj.get("item2").isJsonNull()) {
				JsonObject item2JSON = obj.get("item2").getAsJsonObject();
				item2 = new NPCTradeItem(
						item2JSON.get("namespacedID").isJsonNull() ? null
								: item2JSON.get("namespacedID").getAsString(),
						item2JSON.get("material").isJsonNull() ? null : item2JSON.get("material").getAsString(),
						item2JSON.get("count").getAsInt());
			}

			JsonObject resultJSON = obj.get("result").getAsJsonObject();
			NPCTradeItem result = new NPCTradeItem(
					resultJSON.get("namespacedID").isJsonNull() ? null : resultJSON.get("namespacedID").getAsString(),
					resultJSON.get("material").isJsonNull() ? null : resultJSON.get("material").getAsString(),
					resultJSON.get("count").getAsInt());
			
			tradeList.add(new NPCTrade(item1, item2, result));
		}
		
		return tradeList;
	}
	
	public static Inventory getFirstNPCInventory(LevelUp plugin, Player player, UUID uuid) {
		Entity entity = plugin.getServer().getEntity(uuid);
		LivingEntity le = (LivingEntity) entity;

		Inventory npcInv = Bukkit.createInventory((InventoryHolder) player, 54,
				MenuController.getInventoryTitle(MenuUnicode.NPC_1.val()));
		ItemStack npcID = MenuController.BLANK.getItemStack().clone();
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

		ItemStack nextPage = MenuController.BLANK.getItemStack().clone();
		ItemMeta nextPageIM = nextPage.getItemMeta();
		nextPageIM.setDisplayName("거래 설정");
		nextPage.setItemMeta(nextPageIM);
		npcInv.setItem(53, nextPage);

		ItemStack delete = MenuController.BLANK.getItemStack().clone();
		ItemMeta deleteIM = delete.getItemMeta();
		deleteIM.setDisplayName("NPC 삭제");
		delete.setItemMeta(deleteIM);
		npcInv.setItem(8, delete);

		return npcInv;
	}
	
	public static Inventory getSecondNPCInventory(LevelUp plugin, Player player, UUID uuid) {
		Inventory npcInv = Bukkit.createInventory((InventoryHolder) player, 54,
				MenuController.getInventoryTitle(MenuUnicode.NPC_2.val()));
		ItemStack npcID = MenuController.BLANK.getItemStack().clone();
		ItemMeta npcIM = npcID.getItemMeta();
		npcIM.setDisplayName(uuid.toString());
		npcID.setItemMeta(npcIM);
		npcInv.setItem(49, npcID);

		ItemStack prevPage = MenuController.BLANK.getItemStack().clone();
		ItemMeta prevPageIM = prevPage.getItemMeta();
		prevPageIM.setDisplayName("코스튬 설정");
		prevPage.setItemMeta(prevPageIM);
		npcInv.setItem(45, prevPage);
		
		ItemStack nextPage = MenuController.BLANK.getItemStack().clone();
		ItemMeta nextPageIM = nextPage.getItemMeta();
		nextPageIM.setDisplayName("거래 설정 2");
		nextPage.setItemMeta(nextPageIM);
		npcInv.setItem(53, nextPage);

		List<NPCTrade> tradeList = plugin.npcs.get(uuid);
		if (tradeList == null) {
			tradeList = new ArrayList<NPCTrade>();
		}
		
		int row = 0;
		int col = 0;
		
		int max = tradeList.size() > 10 ? 10 : tradeList.size();
		for (int i = 0; i < max; i++) {
			NPCTrade trade = tradeList.get(i);
			
			ItemStack item1 = null;
			if (trade.getItem1().getNamespacedID() != null) {
				item1 = CustomStack.getInstance(trade.getItem1().getNamespacedID()).getItemStack().clone();
			} else {
				item1 = new ItemStack(Material.getMaterial(trade.getItem1().getMaterial()));
			}
			item1.setAmount(trade.getItem1().getCount());
			npcInv.setItem(MenuController.slot(row, col), item1);

			ItemStack item2 = null;
			if (trade.getItem2() != null) {
				if (trade.getItem2().getNamespacedID() != null) {
					item2 = CustomStack.getInstance(trade.getItem2().getNamespacedID()).getItemStack().clone();
				} else {
					item2 = new ItemStack(Material.getMaterial(trade.getItem2().getMaterial()));
				}
				item2.setAmount(trade.getItem2().getCount());
				npcInv.setItem(MenuController.slot(row, col + 1), item2);
			}

			ItemStack result = null;
			if (trade.getResult().getNamespacedID() != null) {
				result = CustomStack.getInstance(trade.getResult().getNamespacedID()).getItemStack().clone();
			} else {
				result = new ItemStack(Material.getMaterial(trade.getResult().getMaterial()));
			}
			result.setAmount(trade.getResult().getCount());
			npcInv.setItem(MenuController.slot(row, col + 3), result);

			row++;
			if (row == 5) {
				row = 0;
				col = 5;
			}
		}

		return npcInv;
	}
	
	public static Inventory getThirdNPCInventory(LevelUp plugin, Player player, UUID uuid) {
		Inventory npcInv = Bukkit.createInventory((InventoryHolder) player, 54,
				MenuController.getInventoryTitle(MenuUnicode.NPC_3.val()));
		ItemStack npcID = MenuController.BLANK.getItemStack().clone();
		ItemMeta npcIM = npcID.getItemMeta();
		npcIM.setDisplayName(uuid.toString());
		npcID.setItemMeta(npcIM);
		npcInv.setItem(49, npcID);

		ItemStack prevPage = MenuController.BLANK.getItemStack().clone();
		ItemMeta prevPageIM = prevPage.getItemMeta();
		prevPageIM.setDisplayName("거래 설정 1");
		prevPage.setItemMeta(prevPageIM);
		npcInv.setItem(45, prevPage);
		
		List<NPCTrade> tradeList = plugin.npcs.get(uuid);
		if (tradeList == null) {
			tradeList = new ArrayList<NPCTrade>();
		}
		
		int row = 0;
		int col = 0;
		
		if (tradeList.size() > 10) {
			for (int i = 10; i < tradeList.size(); i++) {
				NPCTrade trade = tradeList.get(i);
				
				ItemStack item1 = null;
				if (trade.getItem1().getNamespacedID() != null) {
					item1 = CustomStack.getInstance(trade.getItem1().getNamespacedID()).getItemStack().clone();
				} else {
					item1 = new ItemStack(Material.getMaterial(trade.getItem1().getMaterial()));
				}
				item1.setAmount(trade.getItem1().getCount());
				npcInv.setItem(MenuController.slot(row, col), item1);

				ItemStack item2 = null;
				if (trade.getItem2() != null) {
					if (trade.getItem2().getNamespacedID() != null) {
						item2 = CustomStack.getInstance(trade.getItem2().getNamespacedID()).getItemStack().clone();
					} else {
						item2 = new ItemStack(Material.getMaterial(trade.getItem2().getMaterial()));
					}
					item2.setAmount(trade.getItem2().getCount());
					npcInv.setItem(MenuController.slot(row, col + 1), item2);
				}

				ItemStack result = null;
				if (trade.getResult().getNamespacedID() != null) {
					result = CustomStack.getInstance(trade.getResult().getNamespacedID()).getItemStack().clone();
				} else {
					result = new ItemStack(Material.getMaterial(trade.getResult().getMaterial()));
				}
				result.setAmount(trade.getResult().getCount());
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

}
