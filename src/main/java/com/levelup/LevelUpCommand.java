package com.levelup;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

import net.md_5.bungee.api.ChatColor;

public class LevelUpCommand implements CommandExecutor {

	private LevelUp plugin;

	public LevelUpCommand(LevelUp plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			if (sender.isOp()) {
				if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
					plugin.loadDB();
					PluginDescriptionFile pdFile = plugin.getDescription();
					sender.sendMessage(ChatColor.GREEN + "[" + ChatColor.GOLD + pdFile.getName() + ChatColor.GREEN
							+ "] 플러그인이 리로드 되었습니다");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

}
