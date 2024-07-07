package com.levelup.player;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.levelup.LevelUp;

public class PlayerTabCompleter implements TabCompleter {

	private LevelUp plugin;

	public PlayerTabCompleter(LevelUp plugin) {
		this.plugin = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> list = new ArrayList<String>();

		try {
			if (sender.isOp()) {
				if (args.length == 1) {
					
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
