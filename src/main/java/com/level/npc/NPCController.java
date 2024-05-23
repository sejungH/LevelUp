package com.level.npc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.levelup.main.LevelUp;

import net.md_5.bungee.api.ChatColor;

public class NPCController {

	public static Map<UUID, List<NPCTrade>> getNPCs(LevelUp plugin) throws SQLException {
		Connection conn = plugin.mysql.getConnection();

		String sql = "SELECT * FROM npc";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();

		Map<UUID, List<NPCTrade>> npcs = new HashMap<UUID, List<NPCTrade>>();

		int count = 0;
		while (rs.next()) {
			UUID uuid = UUID.fromString(rs.getString("uuid"));
			npcs.put(uuid, getTradeList(rs.getString("trades")));
			count++;
		}
		
		rs.close();
		pstmt.close();
		
		plugin.getLogger()
		.info(ChatColor.GREEN + "Loaded " + ChatColor.YELLOW + count + ChatColor.GREEN + " NPC Data");

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

}
