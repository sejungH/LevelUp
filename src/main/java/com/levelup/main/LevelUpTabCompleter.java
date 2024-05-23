package com.levelup.main;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class LevelUpTabCompleter implements TabCompleter {
	
	private LevelUp plugin;
	private Connection conn;
	
	public LevelUpTabCompleter(LevelUp plugin) {
		this.plugin = plugin;
		this.conn = plugin.mysql.getConnection();
	}

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
