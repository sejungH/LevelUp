package com.levelup.village;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import com.levelup.main.LevelUp;
import com.levelup.player.PlayerData;
import com.levelup.scoreboard.ScoreboardController;

public class VillageController {

	public static Map<Integer, VillageData> getVillages(LevelUp levelUp, Connection conn) throws SQLException {
		String sql = "SELECT * FROM village";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();

		Map<Integer, VillageData> villages = new HashMap<Integer, VillageData>();

		while (rs.next()) {
			String spawn = rs.getString("spawn");

			int[] arr = null;
			if (spawn != null) {
				arr = new int[3];
				String sub = spawn.substring(1, spawn.length() - 1);
				for (int i = 0; i < 3; i++) {
					arr[i] = Integer.parseInt(sub.split(",")[i].trim());
				}
			}

			VillageData vd = new VillageData(rs.getInt("id"), rs.getString("name"),
					rs.getString("president") == null ? null : UUID.fromString(rs.getString("president")), arr);
			villages.put(rs.getInt("id"), vd);
		}

		return villages;
	}

	public static int getVillageId(LevelUp plugin, String villageName) {
		for (int id : plugin.villages.keySet()) {
			VillageData vd = plugin.villages.get(id);
			if (vd.getName().equalsIgnoreCase(villageName)) {
				return id;
			}
		}

		return -1;
	}

	public static String getVillageName(LevelUp plugin, Connection conn, int villageId) {
		VillageData vd = plugin.villages.get(villageId);
		if (vd == null) {
			return null;

		} else {
			return vd.getName();
		}
	}

	public static List<PlayerData> getVillageMembers(LevelUp plugin, Connection conn, String villageName) {
		int id = getVillageId(plugin, villageName);

		List<PlayerData> players = new ArrayList<PlayerData>();

		for (UUID uuid : plugin.players.keySet()) {
			PlayerData pd = plugin.players.get(uuid);
			if (pd.getVillage() == id) {
				players.add(pd);
			}
		}

		return players;
	}

	public static int countVillageMembers(LevelUp plugin, String villageName) {
		int id = getVillageId(plugin, villageName);

		int count = 0;
		for (UUID uuid : plugin.players.keySet()) {
			PlayerData pd = plugin.players.get(uuid);
			if (pd.getVillage() == id) {
				count++;
			}
		}

		return count;
	}

	public static int addVillage(LevelUp plugin, Connection conn, String villageName) {

		try {
			String sql = "INSERT INTO village (name) VALUES (?)";

			PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, villageName);
			pstmt.executeUpdate();

			plugin.getLogger().info("새로운 마을 [" + villageName + "] 을(를) 데이터베이스에 추가되었습니다.");

			ResultSet rs = pstmt.getGeneratedKeys();

			if (rs.next()) {
				int id = rs.getInt(1);

				rs.close();
				pstmt.close();

				VillageData vd = new VillageData(id, villageName, null, null);
				plugin.villages.put(id, vd);

				ScoreboardManager sbmgr = plugin.getServer().getScoreboardManager();
				Scoreboard sb = sbmgr.getMainScoreboard();
				Team team = sb.registerNewTeam(villageName);
				team.setPrefix("[" + villageName + "] ");

				return id;

			} else {
				rs.close();
				pstmt.close();

				return -2;
			}

		} catch (SQLException e) {
			return -1;
		}
	}

	public static void deleteVillage(LevelUp plugin, Connection conn, String villageName) throws SQLException {
		String sql = "DELETE FROM village WHERE name = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, villageName);
		pstmt.executeUpdate();
		pstmt.close();

		int villageId = getVillageId(plugin, villageName);
		plugin.villages.remove(villageId);

		ScoreboardManager sbmgr = plugin.getServer().getScoreboardManager();
		Scoreboard sb = sbmgr.getMainScoreboard();
		Team team = sb.getTeam(villageName);
		if (team != null)
			team.unregister();

		plugin.getLogger().info("마을 [" + villageName + "] 이(가) 삭제되었습니다.");
	}

	public static int addUser(LevelUp plugin, Connection conn, String villageName, PlayerData pd) throws SQLException {
		int villageId = getVillageId(plugin, villageName);

		if (villageId > 0) {

			String sql = "UPDATE player SET village = ? WHERE uuid = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, villageId);
			pstmt.setString(2, pd.getUuid().toString());

			plugin.getLogger().info("유저 [" + pd.getUsername() + "] 이(가) 마을 [" + villageName + "] 에 가입되었습니다.");

			pstmt.executeUpdate();
			pstmt.close();

			pd.setVillage(villageId);

			ScoreboardManager sbmgr = plugin.getServer().getScoreboardManager();
			Scoreboard sb = sbmgr.getMainScoreboard();
			Team team = sb.getTeam(villageName);
			if (team == null) {
				team = sb.registerNewTeam(villageName);
				team.setPrefix("[" + villageName + "] ");
			}
			team.addEntry(pd.getUsername());
			
			OfflinePlayer op = plugin.getServer().getOfflinePlayer(pd.getUuid());
			if (op.isOnline()) {
				Player player = (Player) op;
				ScoreboardController.updateScoreboard(plugin, player);
			}

			return villageId;

		} else {
			return -1;
		}
	}

	public static String addUser(LevelUp plugin, Connection conn, int villageId, PlayerData pd) throws SQLException {
		String villageName = getVillageName(plugin, conn, villageId);

		if (villageName != null) {

			String sql = "UPDATE player SET village = ? WHERE uuid = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, villageId);
			pstmt.setString(2, pd.getUuid().toString());

			plugin.getLogger().info("유저 [" + pd.getUsername() + "] 이(가) 마을 [" + villageName + "] 에 가입되었습니다.");

			pstmt.executeUpdate();
			pstmt.close();

			pd.setVillage(villageId);

			ScoreboardManager sbmgr = plugin.getServer().getScoreboardManager();
			Scoreboard sb = sbmgr.getMainScoreboard();
			Team team = sb.getTeam(villageName);
			if (team == null) {
				team = sb.registerNewTeam(villageName);
				team.setPrefix("[" + villageName + "] ");
			}
			team.addEntry(pd.getUsername());
			
			OfflinePlayer op = plugin.getServer().getOfflinePlayer(pd.getUuid());
			if (op.isOnline()) {
				Player player = (Player) op;
				ScoreboardController.updateScoreboard(plugin, player);
			}

			return villageName;

		} else {

			return null;
		}
	}

	public static String deleteUser(LevelUp plugin, Connection conn, PlayerData pd) throws SQLException {

		VillageData vd = plugin.villages.get(pd.getVillage());

		String sql = "UPDATE player SET village = NULL WHERE uuid = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, pd.getUuid().toString());
		pstmt.executeUpdate();

		plugin.getLogger().info("유저 [" + pd.getUsername() + "] 이(가) 마을 [" + vd.getName() + "] 에서 탈퇴되었습니다.");

		pd.setVillage(0);

		if (vd.getPresident() != null && vd.getPresident().equals(pd.getUuid())) {
			pstmt.clearParameters();
			sql = "UPDATE village SET president = NULL WHERE id = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, pd.getVillage());
			pstmt.executeUpdate();

			plugin.getLogger().info("유저 [" + pd.getUsername() + "] 은(는) 이제 마을 [" + vd.getName() + "] 의 이장이 아닙니다.");

			vd.setPresident(null);

			pstmt.close();
		}

		ScoreboardManager sbmgr = plugin.getServer().getScoreboardManager();
		Scoreboard sb = sbmgr.getMainScoreboard();
		Team team = sb.getTeam(vd.getName());
		if (team != null) {
			team.removeEntry(pd.getUsername());
		}
		
		OfflinePlayer op = plugin.getServer().getOfflinePlayer(pd.getUuid());
		if (op.isOnline()) {
			Player player = (Player) op;
			ScoreboardController.updateScoreboard(plugin, player);
		}

		return vd.getName();
	}

	public static void registerPresident(LevelUp plugin, Connection conn, PlayerData pd) throws SQLException {
		VillageData vd = plugin.villages.get(pd.getVillage());

		String sql = "UPDATE village SET president = ? WHERE id = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, pd.getUuid().toString());
		pstmt.setInt(2, pd.getVillage());
		pstmt.executeUpdate();
		pstmt.close();

		plugin.getLogger().info("유저 [" + pd.getUsername() + "] 이(가) 마을 [" + vd.getName() + "] 의 이장이 되었습니다.");
		vd.setPresident(pd.getUuid());
	}

	public static void dropPresident(LevelUp plugin, Connection conn, PlayerData pd) throws SQLException {
		VillageData vd = plugin.villages.get(pd.getVillage());

		String sql = "UPDATE village SET president = NULL WHERE id = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, pd.getVillage());
		pstmt.executeUpdate();
		pstmt.close();

		plugin.getLogger().info("유저 [" + pd.getUsername() + "] 은(는) 이제 마을 [" + vd.getName() + "] 의 이장이 아닙니다.");
		vd.setPresident(null);
	}

	public static void setVillageSpawn(LevelUp plugin, Connection conn, String villageName, int[] coordinate)
			throws SQLException {
		int villageId = getVillageId(plugin, villageName);
		VillageData vd = plugin.villages.get(villageId);

		String sql = "UPDATE village SET spawn = ? WHERE name = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, Arrays.toString(coordinate));
		pstmt.setString(2, villageName);
		pstmt.executeUpdate();
		pstmt.close();

		plugin.getLogger().info("마을 [" + villageName + "] 의 스폰 좌표가 (" + coordinate[0] + ", " + coordinate[1] + ", "
				+ coordinate[2] + ") 로 변경되었습니다.");
		vd.setSpawn(coordinate);
	}

	public static void renameVillage(LevelUp plugin, Connection conn, String oldName, String newName) throws SQLException {
		// TODO Auto-generated method stub
		int villageId = getVillageId(plugin, oldName);
		VillageData vd = plugin.villages.get(villageId);
		
		String sql = "UPDATE village SET name = ? WHERE id = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, newName);
		pstmt.setInt(2, villageId);
		pstmt.executeUpdate();
		pstmt.close();

		plugin.getLogger().info("마을 [" + oldName + "] 이 [" + newName + "] 로 변경되었습니다.");
		vd.setName(newName);
		
		ScoreboardManager sbmgr = plugin.getServer().getScoreboardManager();
		Scoreboard sb = sbmgr.getMainScoreboard();
		Team oldTeam = sb.getTeam(oldName);
		Team newTeam = sb.registerNewTeam(newName);
		newTeam.setPrefix("[" + newName + "] ");
		
		for (String entry : oldTeam.getEntries()) {
			oldTeam.removeEntry(entry);
			newTeam.addEntry(entry);
		}
		
		for (UUID uuid : plugin.players.keySet()) {
			PlayerData pd = plugin.players.get(uuid);
			if (pd.getVillage() == vd.getId()) {
				
				OfflinePlayer op = plugin.getServer().getOfflinePlayer(pd.getUuid());
				if (op.isOnline()) {
					Player player = (Player) op;
					ScoreboardController.updateScoreboard(plugin, player);
				}
				
			}
		}

		oldTeam.unregister();
	}

}
