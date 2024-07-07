package com.levelup.tool;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.levelup.LevelUp;
import com.levelup.player.PlayerController;

public class ToolTabCompleter implements TabCompleter {

	private LevelUp plugin;

	public ToolTabCompleter(LevelUp plugin) {
		this.plugin = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> list = new ArrayList<String>();

		try {
			if (sender.isOp() && sender instanceof Player) {
				
				if (args.length == 1) {
					list.add("get");
					
					return list;

				} else if (args.length == 2) {
					
					if (args[0].equals("get")) {
						list.add("pickaxe");
						list.add("axe");
						list.add("sword");
						list.add("shovel");
						return list;
					}
					
				} else if (args.length == 3) {
					
					if (args[0].equals("get")) {
						
						return PlayerController.getOnlinePlayerNames(plugin);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

}
