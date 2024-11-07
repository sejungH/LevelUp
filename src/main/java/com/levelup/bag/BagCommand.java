package com.levelup.bag;

import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.levelup.LevelUp;
import com.levelup.player.PlayerController;
import com.levelup.player.PlayerData;

import dev.lone.itemsadder.api.CustomStack;
import net.md_5.bungee.api.ChatColor;

public class BagCommand implements CommandExecutor {

	private LevelUp plugin;

	public BagCommand(LevelUp plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			if (sender.isOp()) {
				if (args.length > 0) {

					if (args[0].equalsIgnoreCase("get")) {

						if (args.length == 2) {

							if (sender instanceof Player) {
								Player player = (Player) sender;
								List<String> bags = BagController.getBags();

								if (bags.contains(args[1])) {
									CustomStack bag = BagController.getBag(plugin, player.getUniqueId(), args[1]);
									Map<Integer, ItemStack> remain = player.getInventory().addItem(bag.getItemStack());

									if (remain.isEmpty()) {
										sender.sendMessage(ChatColor.GREEN + "[" + ChatColor.GOLD
												+ ChatColor.stripColor(bag.getDisplayName()) + ChatColor.GREEN
												+ "] 을(를) 획득했습니다");

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
								List<String> bags = BagController.getBags();

								if (bags.contains(args[1])) {
									PlayerData pd = PlayerController.getPlayerData(plugin, args[2]);

									if (pd != null) {
										CustomStack bag = BagController.getBag(plugin, pd.getUuid(), args[1]);
										Map<Integer, ItemStack> remain = player.getInventory()
												.addItem(bag.getItemStack());

										if (remain.isEmpty()) {
											sender.sendMessage(ChatColor.GOLD + pd.getUsername() + ChatColor.GREEN
													+ " 님의 [" + ChatColor.GOLD
													+ ChatColor.stripColor(bag.getDisplayName()) + ChatColor.GREEN
													+ "] 을(를) 획득했습니다");

										} else {
											sender.sendMessage(ChatColor.RED + "인벤토리에 공간이 부족합니다");
										}

									} else {
										sender.sendMessage(ChatColor.RED + args[2] + " 은(는) 존재하지 않는 유저입니다");
									}

								} else {
									sender.sendMessage(ChatColor.RED + args[1] + " 은(는) 등록되어 있지 않은 탈것입니다");
								}

							} else {
								sender.sendMessage(ChatColor.RED + "이 명령어를 실행할 권한이 없습니다");
							}

						} else {
							sender.sendMessage(ChatColor.RED + "사용법: /bag get <id> [<username>]");
						}

					} else {
						sender.sendMessage(ChatColor.RED + "사용법: /bag get <id> [<username>]");
					}
					
				} else {
					sender.sendMessage(ChatColor.RED + "사용법: /bag get <id> [<username>]");
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

}
