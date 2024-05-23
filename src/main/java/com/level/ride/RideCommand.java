package com.level.ride;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.levelup.main.LevelUp;
import com.levelup.player.PlayerController;
import com.levelup.player.PlayerData;

import dev.lone.itemsadder.api.CustomStack;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.MythicBukkit;
import net.md_5.bungee.api.ChatColor;

public class RideCommand implements CommandExecutor {

	private LevelUp plugin;

	public RideCommand(LevelUp plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			if (sender.isOp()) {
				if (args.length > 0) {

					if (args[0].equalsIgnoreCase("key")) {

						if (args.length == 2) {

							if (sender instanceof Player) {
								Player player = (Player) sender;
								List<String> rides = RideController.getRides();

								if (rides.contains(args[1])) {
									CustomStack key = RideController.getKey(plugin, player.getUniqueId(), args[1]);
									Map<Integer, ItemStack> remain = player.getInventory().addItem(key.getItemStack());

									if (remain.isEmpty()) {
										sender.sendMessage(ChatColor.GREEN + "[" + ChatColor.GOLD
												+ ChatColor.stripColor(key.getDisplayName()) + ChatColor.GREEN
												+ "] 를 획득했습니다");

									} else {
										sender.sendMessage(ChatColor.RED + "인벤토리에 공간이 부족합니다");
									}

								} else {
									sender.sendMessage(ChatColor.RED + args[1] + " 은(는) 등록되어 있지 않은 탈것입니다");
								}

							} else {
								sender.sendMessage(ChatColor.RED + "이 명령어를 실행할 권한이 없습니다");
							}

						} else if (args.length == 3) {

							if (sender instanceof Player && sender.isOp()) {
								Player player = (Player) sender;
								List<String> rides = RideController.getRides();

								if (rides.contains(args[1])) {
									PlayerData pd = PlayerController.getPlayerData(plugin, args[2]);

									if (pd != null) {
										CustomStack key = RideController.getKey(plugin, pd.getUuid(), args[1]);
										Map<Integer, ItemStack> remain = player.getInventory()
												.addItem(key.getItemStack());

										if (remain.isEmpty()) {
											sender.sendMessage(ChatColor.GOLD + pd.getUsername() + ChatColor.GREEN
													+ " 님의 [" + ChatColor.GOLD
													+ ChatColor.stripColor(key.getDisplayName()) + ChatColor.GREEN
													+ "] 를 획득했습니다");

										} else {
											sender.sendMessage(ChatColor.RED + "인벤토리에 공간이 부족합니다");
										}

									} else {
										sender.sendMessage(ChatColor.RED + args[2] + " 은(는) 존재하지 않는 유저입니다.");
									}

								} else {
									sender.sendMessage(ChatColor.RED + args[1] + " 은(는) 등록되어 있지 않은 탈것입니다");
								}

							} else {
								sender.sendMessage(ChatColor.RED + "이 명령어를 실행할 권한이 없습니다");
							}

						} else {
							sender.sendMessage(ChatColor.RED + "사용법: /ride key <id> [<username>]");
						}

					}
				}
			} else {
				sender.sendMessage(ChatColor.RED + "이 명령어를 실행할 권한이 없습니다");
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public String isMythicMob(String input) {
		Collection<MythicMob> mobs = MythicBukkit.inst().getMobManager().getMobTypes();
		String id = null;
		for (MythicMob m : mobs) {
			if (input.equalsIgnoreCase(m.getInternalName())) {
				id = m.getInternalName();
				break;
			}
		}

		return id;
	}

}
