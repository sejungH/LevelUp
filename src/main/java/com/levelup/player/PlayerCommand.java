package com.levelup.player;

import java.sql.Connection;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.levelup.chat.ChatType;
import com.levelup.main.LevelUp;
import com.levelup.scoreboard.ScoreboardController;

import net.md_5.bungee.api.ChatColor;

public class PlayerCommand implements CommandExecutor {

	private LevelUp plugin;
	private Connection conn;

	public PlayerCommand(LevelUp plugin) {
		this.plugin = plugin;
		this.conn = plugin.mysql.getConnection();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				PlayerData pd = plugin.players.get(player.getUniqueId());
				
				if (label.equalsIgnoreCase("전체채팅")) {
					pd.setChatType(ChatType.DEFAULT);
					sender.sendMessage(ChatColor.GREEN + "전체채팅 모드로 전환되었습니다.");
					ScoreboardController.updateScoreboard(plugin, player);
					
				} else if (label.equalsIgnoreCase("마을채팅")) {
					pd.setChatType(ChatType.VILLAGE);
					sender.sendMessage(ChatColor.GREEN + "마을채팅 모드로 전환되었습니다.");
					ScoreboardController.updateScoreboard(plugin, player);
					
				} else if (label.equalsIgnoreCase("c") || label.equalsIgnoreCase("ㅊ")) {
					
					if (pd.getChatType() == ChatType.DEFAULT) {
						pd.setChatType(ChatType.VILLAGE);
						sender.sendMessage(ChatColor.GREEN + "마을채팅 모드로 전환되었습니다.");
						ScoreboardController.updateScoreboard(plugin, player);
						
					} else {
						pd.setChatType(ChatType.DEFAULT);
						sender.sendMessage(ChatColor.GREEN + "전체채팅 모드로 전환되었습니다.");
						ScoreboardController.updateScoreboard(plugin, player);
					}
					
				} else {
					sender.sendMessage(ChatColor.RED + "/전체채팅, /지역채팅, /마을채팅 으로 채팅 모드를 전환하세요.");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}