package com.levelup.tool;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class ToolTabCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> list = new ArrayList<String>();

		try {
			if (sender.isOp() && sender instanceof Player) {

				if (args.length == 1) {
					list.add("pickaxe");
					list.add("axe");
					list.add("sword");
					list.add("shovel");
					list.add("boost");

					return list;

				} else if (args.length == 2) {

					if (args[0].equalsIgnoreCase("pickaxe") || args[0].equalsIgnoreCase("axe")
							|| args[0].equalsIgnoreCase("sword") || args[0].equalsIgnoreCase("shovel")) {
						list.add("exp");
						list.add("level");
						list.add("stat");
						list.add("reset");
						return list;
						
					}

				} else if (args.length == 3) {

					if (args[1].equals("exp")) {
						list.add("set");
						list.add("add");
						return list;
						
					} else if (args[1].equals("level")) {
						list.add("set");
						return list;
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

}
