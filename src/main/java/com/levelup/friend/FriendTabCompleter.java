package com.levelup.friend;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.levelup.LevelUp;
import com.levelup.player.PlayerController;
import com.levelup.player.PlayerData;

public class FriendTabCompleter implements TabCompleter {

	private LevelUp plugin;

	public FriendTabCompleter(LevelUp plugin) {
		this.plugin = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> list = new ArrayList<String>();

		try {
			if (sender instanceof Player player) {
				if (label.equalsIgnoreCase("친구")) {
					if (args.length == 1) {
						list.add("신청");
						return list;

					} else if (args.length == 2 && args[0].equalsIgnoreCase("신청")) {
						list.addAll(PlayerController.getOnlinePlayerNames(plugin));
						PlayerData pd = plugin.players.get(player.getUniqueId());
						list.remove(pd.getName());
						return list;
					}
					
				} else if (label.equalsIgnoreCase("차단")) {
					if (args.length == 1) {
						list.addAll(PlayerController.getOnlinePlayerNames(plugin));
						PlayerData pd = plugin.players.get(player.getUniqueId());
						list.remove(pd.getName());
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
