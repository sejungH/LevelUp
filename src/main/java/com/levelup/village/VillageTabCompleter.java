package com.levelup.village;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.levelup.main.LevelUp;

public class VillageTabCompleter implements TabCompleter {

	private LevelUp plugin;
	private Connection conn;

	public VillageTabCompleter(LevelUp plugin) {
		this.plugin = plugin;
		this.conn = plugin.mysql.getConnection();
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> list = new ArrayList<String>();

		try {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (args.length == 1) {
					if (sender.isOp()) {
						list.add("생성");
						list.add("이름변경");
						list.add("가입");
						list.add("탈퇴");
						list.add("삭제");
						list.add("이장");
						list.add("스폰");
						list.add("정보");
						list.add("목록");

					} else if (isPresident(player) > 0) {
						list.add("가입");
						list.add("탈퇴");
						list.add("이장");
						list.add("정보");

					} else {
						list.add("탈퇴");
						list.add("정보");
					}

					return list;

				} else if (args.length == 2) {
					
					if (sender.isOp()) {
						if (args[0].equalsIgnoreCase("이름변경") || args[0].equalsIgnoreCase("가입") || args[0].equalsIgnoreCase("삭제")
								|| args[0].equalsIgnoreCase("스폰") || args[0].equalsIgnoreCase("정보")) {
							
							return getVillages();
							
						} else if (args[0].equalsIgnoreCase("탈퇴") || args[0].equalsIgnoreCase("이장")) {
							String sql = "SELECT p1.username FROM player p1\r\n"
									+ "INNER JOIN player p2 ON p2.village = p1.village\r\n"
									+ "WHERE p2.username = ?";
							PreparedStatement pstmt = conn.prepareStatement(sql);
							pstmt.setString(1, player.getName());
							ResultSet rs = pstmt.executeQuery();
							
							while (rs.next()) {
								list.add(rs.getString("username"));
							}
							
							rs.close();
							pstmt.close();
							
							return list;
						}
						
					} else if (isPresident(player) > 0) {
						if (args[0].equalsIgnoreCase("가입")) {
							
							for (Player p : plugin.getServer().getOnlinePlayers()) {
								list.add(p.getName());
							}
							
							return list;
							
						} else if (args[0].equalsIgnoreCase("탈퇴") || args[0].equalsIgnoreCase("이장")) {
							String sql = "SELECT p1.username FROM player p1\r\n"
									+ "INNER JOIN player p2 ON p2.village = p1.village\r\n"
									+ "WHERE p2.username = ?";
							PreparedStatement pstmt = conn.prepareStatement(sql);
							pstmt.setString(1, player.getName());
							ResultSet rs = pstmt.executeQuery();
							
							while (rs.next()) {
								list.add(rs.getString("username"));
							}
							
							rs.close();
							pstmt.close();
							
							return list;
						}
					}
					
				} else if (args.length == 3) {
					if (sender.isOp()) {
						if (args[0].equalsIgnoreCase("가입")) {
							
							for (Player p : plugin.getServer().getOnlinePlayers()) {
								list.add(p.getName());
							}
							
							return list;
							
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public int isPresident(Player player) throws SQLException {
		String sql = "SELECT * FROM village WHERE president = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, player.getUniqueId().toString());
		ResultSet rs = pstmt.executeQuery();

		if (rs.next()) {
			int village = rs.getInt("id");
			rs.close();
			pstmt.close();

			return village;

		} else {
			rs.close();
			pstmt.close();

			return -1;
		}
	}

	public List<String> getVillages() throws SQLException {
		List<String> villages = new ArrayList<String>();
		String sql = "SELECT * FROM village WHERE name IS NOT NULL ORDER BY name";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();

		while (rs.next()) {
			villages.add(rs.getString("name"));
		}

		return villages;
	}
	
	public List<String> getPlayers() throws SQLException {
		List<String> players = new ArrayList<String>();
		String sql = "SELECT * FROM player";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();

		while (rs.next()) {
			players.add(rs.getString("username"));
		}

		return players;
	}

}
