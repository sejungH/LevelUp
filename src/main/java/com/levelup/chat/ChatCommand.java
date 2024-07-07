package com.levelup.chat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.levelup.LevelUp;
import com.levelup.player.PlayerData;

import net.md_5.bungee.api.ChatColor;

public class ChatCommand implements CommandExecutor {
	
	private LevelUp plugin;
	
	public ChatCommand(LevelUp plugin) {
		this.plugin = plugin;
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
					
				} else if (label.equalsIgnoreCase("마을채팅")) {
					
					if (pd.getVillage() > 0) {
						pd.setChatType(ChatType.VILLAGE);
						sender.sendMessage(ChatColor.GREEN + "마을채팅 모드로 전환되었습니다.");
					}
					
				} else if (label.equalsIgnoreCase("c") || label.equalsIgnoreCase("ㅊ")) {
					
					if (pd.getVillage() > 0) {
						
						if (pd.getChatType() == ChatType.DEFAULT) {
							pd.setChatType(ChatType.VILLAGE);
							sender.sendMessage(ChatColor.GREEN + "마을채팅 모드로 전환되었습니다.");
							
						} else {
							pd.setChatType(ChatType.DEFAULT);
							sender.sendMessage(ChatColor.GREEN + "전체채팅 모드로 전환되었습니다.");
						}
						
					} else {
						sender.sendMessage(ChatColor.RED + "현재 가입되어있는 마을이 없습니다.");
					}
					
				} else if (label.equalsIgnoreCase("귓")) {
					
					if (args.length > 1) {
						String message = "";
						for (int i = 1; i < args.length; i++) {
							if (i == args.length - 1) {
								message += args[i];
							} else {
								message += args[i] + " ";
							}
						}
						ChatController.sendWhisper(plugin, player, args[0], message);
						
					} else {
						sender.sendMessage(ChatColor.RED + "사용법: /귓 <유저> <메세지>");
					}
				}
				
			} else {
				sender.sendMessage(ChatColor.RED + "이 명령어를 실행할 권한이 없습니다");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

}
