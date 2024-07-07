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
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (args.length == 1) {
					if (sender.isOp()) {
						list.add("생성");
						list.add("이름변경");
						list.add("가입");
						list.add("탈퇴");
						list.add("삭제");
						list.add("이장");
						list.add("스폰");
						list.add("정보");
						list.add("목록");
						list.add("신청서");
						list.add("청크");

					} else if (isPresident(player) > 0) {
						list.add("가입");
						list.add("탈퇴");
						list.add("이장");
						list.add("정보");
						list.add("청크");

					} else {
						list.add("탈퇴");
						list.add("정보");
					}

					return list;

				} else if (args.length == 2) {
					
					if (sender.isOp()) {
						if (args[0].equalsIgnoreCase("이름변경") || args[0].equalsIgnoreCase("가입") || args[0].equalsIgnoreCase("삭제")
								|| args[0].equalsIgnoreCase("스폰") || args[0].equalsIgnoreCase("정보")) {
							
							return getVillages();
							
						} else if (args[0].equalsIgnoreCase("탈퇴") || args[0].equalsIgnoreCase("이장")) {
							
							for (UUID uuid : plugin.players.keySet()) {
								PlayerData p = plugin.players.get(uuid);
								if (p.getVillage() > 0) {
									list.add(p.getName());
								}
							}
							
							return list;
							
						} else if (args[0].equalsIgnoreCase("청크")) {
							list.add("구매");
							list.add("판매");
							list.add("확인");
							list.add("목록");
							
							return list;
						}
						
					} else if (isPresident(player) > 0) {
						if (args[0].equalsIgnoreCase("가입")) {
							
							return PlayerController.getOnlinePlayerNames(plugin);
							
						} else if (args[0].equalsIgnoreCase("탈퇴") || args[0].equalsIgnoreCase("이장")) {
							PlayerData pd = plugin.players.get(player.getUniqueId());
							
							for (UUID uuid : plugin.players.keySet()) {
								PlayerData p = plugin.players.get(uuid);
								if (p.getVillage() == pd.getVillage()) {
									list.add(p.getName());
								}
							}
							
							return list;
							
						} else if (args[0].equalsIgnoreCase("청크")) {
							list.add("구매");
							list.add("판매");
							list.add("확인");
							list.add("목록");
							
							return list;
						}
					}
					
				} else if (args.length == 3) {
					if (sender.isOp()) {
						if (args[0].equalsIgnoreCase("가입")) {
							
							return PlayerController.getOnlinePlayerNames(plugin);
							
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	public int isPresident(Player player) {
		PlayerData pd = plugin.players.get(player.getUniqueId());
		VillageData vd = plugin.villages.get(pd.getVillage());
		
		if (vd.getPresident().equals(pd.getUuid())) {
			return vd.getId();
		}
		
		return -1;
	}

	public List<String> getVillages() {
		List<String> villages = new ArrayList<String>();
		
		for (int villageId : plugin.villages.keySet()) {
			VillageData vd = plugin.villages.get(villageId);
			villages.add(vd.getName());
		}

		return villages;
	}

}
