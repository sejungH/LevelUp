package com.levelup.friend;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.levelup.LevelUp;
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
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (args.length == 1) {
					list.add("신청");
					list.add("수락");
					list.add("거절");
					list.add("삭제");
					list.add("신청함");
					list.add("목록");

					return list;

				} else if (args.length == 2) {

					if (args[0].equalsIgnoreCase("신청")) {
						
						for (Player p : plugin.getServer().getOnlinePlayers()) {
							list.add(p.getName());
						}
						
						return list;
						
					} else if (args[0].equalsIgnoreCase("수락") || args[0].equalsIgnoreCase("거절")) {
						List<PlayerData> requests = FriendController.getRequests(plugin, player.getName());
						for (PlayerData pd : requests) {
							list.add(pd.getUsername());
						}
						
						return list;
						
					} else if (args[0].equalsIgnoreCase("삭제")) {
						List<PlayerData> friends = FriendController.getFriendList(plugin, player.getName());
						for (PlayerData pd : friends) {
							list.add(pd.getUsername());
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
