package com.levelup.chunk;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.levelup.LevelUp;

public class ChunkTabCompleter implements TabCompleter {
	
	private LevelUp plugin;

	public ChunkTabCompleter(LevelUp plugin) {
		this.plugin = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> list = new ArrayList<String>();
		
		try {
			
			if (args.length == 1 && sender instanceof Player) {
				list.add("구매");
				list.add("판매");
				list.add("확인");
				list.add("목록");
				
				return list;
				
			} else if (args.length == 2 && sender.isOp()) {
				if (args[0].equalsIgnoreCase("확인") && sender instanceof Player) {
					
					for (Player p : plugin.getServer().getOnlinePlayers()) {
						list.add(p.getName());
					}
					
					return list;
					
				} else if (args[0].equalsIgnoreCase("목록")) {
					
					for (Player p : plugin.getServer().getOnlinePlayers()) {
						list.add(p.getName());
					}
					
					return list;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}

}
