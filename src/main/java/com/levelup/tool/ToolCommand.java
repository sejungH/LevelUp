package com.levelup.tool;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.levelup.LevelUp;
import com.levelup.player.PlayerController;
import com.levelup.player.PlayerData;

import net.md_5.bungee.api.ChatColor;

public class ToolCommand implements CommandExecutor {
	
	private LevelUp plugin;
	
	public ToolCommand(LevelUp plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			if (sender.isOp() && sender instanceof Player) {
				Player player = (Player) sender;
				if (args.length == 3 && args[0].equalsIgnoreCase("get")) {
					
					PlayerData pd = PlayerController.getPlayerData(plugin, args[2]);
					
					if (pd != null) {
						if (plugin.tools.get(pd.getUuid()) != null) {
							ToolData tool = plugin.tools.get(pd.getUuid());
							
							if (args[1].equalsIgnoreCase("pickaxe")) {
								player.getInventory().addItem(tool.getPickaxe().getAsItemStack());
								sender.sendMessage(ChatColor.GREEN + "[" + ChatColor.GOLD + args[2] + ChatColor.GREEN
										+ "] 님의 곡괭이를 소환했습니다");
								
							} else if (args[1].equalsIgnoreCase("axe")) {
								player.getInventory().addItem(tool.getAxe().getAsItemStack());
								sender.sendMessage(ChatColor.GREEN + "[" + ChatColor.GOLD + args[2] + ChatColor.GREEN
										+ "] 님의 도끼를 소환했습니다");
								
							} else if (args[1].equalsIgnoreCase("sword")) {
								player.getInventory().addItem(tool.getSword().getAsItemStack());
								sender.sendMessage(ChatColor.GREEN + "[" + ChatColor.GOLD + args[2] + ChatColor.GREEN
										+ "] 님의 검을 소환했습니다");
								
							} else if (args[1].equalsIgnoreCase("shovel")) {
								player.getInventory().addItem(tool.getShovel().getAsItemStack());
								sender.sendMessage(ChatColor.GREEN + "[" + ChatColor.GOLD + args[2] + ChatColor.GREEN
										+ "] 님의 삽을 소환했습니다");
								
							} else {
								sender.sendMessage(ChatColor.RED + "사용법: /t get <pickaxe/axe/sword/shovel> <username>");
							}
							
						} else {
							sender.sendMessage(ChatColor.RED + "유저 " + args[2] + " 의 도구데이터가 존재하지 않습니다");
						}
						
					} else {
						sender.sendMessage(ChatColor.RED + args[2] + " 은(는) 존재하지 않는 유저입니다");
					}
					
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	

}
