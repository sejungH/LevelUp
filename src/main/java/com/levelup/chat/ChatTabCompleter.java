package com.levelup.chat;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.levelup.LevelUp;
import com.levelup.player.PlayerController;

public class ChatTabCompleter implements TabCompleter {

	private LevelUp plugin;

	public ChatTabCompleter(LevelUp plugin) {
		this.plugin = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> list = new ArrayList<String>();

		try {
			if (args.length == 1 && sender instanceof Player) {
				return PlayerController.getOnlinePlayerNames(plugin);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

}
