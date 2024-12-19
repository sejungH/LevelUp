package com.levelup.post;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.levelup.LevelUp;
import com.levelup.player.PlayerController;

public class PostTabCompleter implements TabCompleter {

	private LevelUp plugin;

	public PostTabCompleter(LevelUp plugin) {
		this.plugin = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> list = new ArrayList<String>();

		try {
			if (sender.isOp() && sender instanceof Player && args.length == 1) {
				return PlayerController.getOnlinePlayerNames(plugin);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

}
