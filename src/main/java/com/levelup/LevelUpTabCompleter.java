package com.levelup;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class LevelUpTabCompleter implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> list = new ArrayList<String>();
		
		try {
			list.add("reload");
			return list;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
}
