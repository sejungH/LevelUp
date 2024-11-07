package com.levelup.seasonpass;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.levelup.LevelUp;
import com.levelup.player.PlayerController;

public class SeasonPassTabCompleter implements TabCompleter {

	private LevelUp plugin;

	public SeasonPassTabCompleter(LevelUp plugin) {
		this.plugin = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> list = new ArrayList<String>();

		try {
			if (sender.isOp()) {
				if (args.length == 1) {
					list.add("set");
					list.add("info");
					return list;

				} else if (args.length == 2) {
					return PlayerController.getOnlinePlayerNames(plugin);

				} else if (args.length == 3) {
					if (args[0].equalsIgnoreCase("set")) {
						list.add("true");
						list.add("false");
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
