package com.levelup.friend;

import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.levelup.LevelUp;
import com.levelup.player.PlayerController;
import com.levelup.player.PlayerData;

import net.md_5.bungee.api.ChatColor;

public class FriendCommand implements CommandExecutor {

	private LevelUp plugin;

	public FriendCommand(LevelUp plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			if (sender instanceof Player player) {

				if (label.equalsIgnoreCase("친구")) {

					if (args.length == 2 && args[0].equalsIgnoreCase("신청")) {
						String username = args[1];
						PlayerData pd = PlayerController.getPlayerData(plugin, username);

						if (pd != null) {

							if (pd.getUuid().equals(player.getUniqueId())) {
								sender.sendMessage(ChatColor.RED + "자신에게는 친구 신청을 할 수 없습니다");

							} else {
								FriendData fd = FriendController.getFriendship(plugin, player.getUniqueId(),
										pd.getUuid());

								if (fd == null) {
									FriendController.requestFriend(plugin, player.getUniqueId(), pd.getUuid());
									sender.sendMessage(ChatColor.GREEN + "[" + ChatColor.GOLD + username
											+ ChatColor.GREEN + "] 님께 친구 신청을 보냈습니다");

									OfflinePlayer p = plugin.getServer().getOfflinePlayer(pd.getUuid());
									if (p.isOnline()) {
										Player pp = (Player) p;
										PlayerData senderPD = plugin.players.get(player.getUniqueId());
										pp.sendMessage(ChatColor.GREEN + "[" + ChatColor.GOLD + senderPD.getName()
												+ ChatColor.GREEN + "] 님이 친구 신청을 보냈습니다");
										pp.playSound(pp, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
									}

								} else {

									if (fd.areFriends()) {
										sender.sendMessage(ChatColor.RED + "이미 [" + username + "] 님과 친구입니다");

									} else {
										sender.sendMessage(ChatColor.RED + "이미 [" + username + "] 님과 대기중인 친구 신청이 있습니다");
									}
								}
							}

						} else {
							sender.sendMessage(ChatColor.RED + username + " 은(는) 존재하지 않는 유저입니다");
						}

					} else {
						player.sendMessage(ChatColor.RED + "사용법: /친구 신청 <유저>");
					}

				} else if (label.equalsIgnoreCase("차단")) {
					if (args.length == 1) {
						String username = args[0];
						PlayerData pd = PlayerController.getPlayerData(plugin, username);

						if (pd != null) {

							if (pd.getUuid().equals(player.getUniqueId())) {
								sender.sendMessage(ChatColor.RED + "자신을 차단할 수 없습니다");

							} else {
								FriendController.blockUser(plugin, player, pd.getUuid());
								sender.sendMessage(ChatColor.GREEN + "[" + ChatColor.GOLD + pd.getName()
										+ ChatColor.GREEN + "] 님을 차단했습니다.");
							}

						} else {
							sender.sendMessage(ChatColor.RED + username + " 은(는) 존재하지 않는 유저입니다");
						}
					}
				}

			} else {
				sender.sendMessage(ChatColor.RED + "이 명령어를 실행할 권한이 없습니다");
			}

		} catch (

		Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

}
