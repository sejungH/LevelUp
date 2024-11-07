package com.levelup.village;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.levelup.LevelUp;
import com.levelup.player.PlayerController;
import com.levelup.player.PlayerData;

public class VillageTabCompleter implements TabCompleter {

	private LevelUp plugin;

	public VillageTabCompleter(LevelUp plugin) {
		this.plugin = plugin;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> list = new ArrayList<String>();

		try {
			if (sender instanceof Player && sender.isOp()) {
				if (args.length == 1) {
					list.add("신청서");
					list.add("가입");
					list.add("삭제");
					list.add("이장");
					list.add("탈퇴");
					list.add("이름변경");

					return list;

				} else if (args.length == 2) {

					if (args[0].equalsIgnoreCase("이름변경") || args[0].equalsIgnoreCase("가입")
							|| args[0].equalsIgnoreCase("삭제")) {

						for (int villageId : plugin.villages.keySet()) {
							VillageData vd = plugin.villages.get(villageId);
							list.add(vd.getName());
						}

						return list;

					} else if (args[0].equalsIgnoreCase("탈퇴") || args[0].equalsIgnoreCase("이장")) {

						for (UUID uuid : plugin.players.keySet()) {
							PlayerData p = plugin.players.get(uuid);
							if (p.getVillage() > 0) {
								list.add(p.getName());
							}
						}

						return list;
					}

				} else if (args.length == 3) {
					if (args[0].equalsIgnoreCase("가입")) {
						return PlayerController.getOnlinePlayerNames(plugin);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

}
