package com.levelup.npc;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.levelup.LevelUp;

public class NPCTabCompleter implements TabCompleter {

	private LevelUp plugin;

	public NPCTabCompleter(LevelUp plugin) {
		this.plugin = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> list = new ArrayList<String>();

		try {
			if (sender instanceof Player) {
				if (args.length == 1) {
					list.add("spawn");

				} else if (args.length == 2) {

					if (args[0].equalsIgnoreCase("spawn")) {

						for (OfflinePlayer p : plugin.getServer().getOperators()) {
							list.add(p.getName());
						}
						
						list.add("BLACKSMITH");
						
						return list;
					}
					
				} else if (args.length == 3) {

					if (args[0].equalsIgnoreCase("spawn")) {
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
