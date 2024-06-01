package com.levelup.ride;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.levelup.LevelUp;

public class RideTabCompleter implements TabCompleter {
	
	private LevelUp plugin;
	
	public RideTabCompleter(LevelUp plugin) {
		this.plugin = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> list = new ArrayList<String>();
		
		try {
			if (sender instanceof Player) {
				
				if (args.length == 1) {
					list.add("key");
					
					return list;
					
				} else if (args.length == 2) {
					
					if (args[0].equalsIgnoreCase("key")) {
						return RideController.getRides();
					}
					
				} else if (args.length == 3) {
					
					if (args[0].equalsIgnoreCase("key")) {
						
						for (Player p : plugin.getServer().getOnlinePlayers()) {
							list.add(p.getName());
						}
						
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
