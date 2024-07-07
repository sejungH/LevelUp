package com.levelup.friend;

import java.sql.Connection;
import java.util.List;

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
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class FriendCommand implements CommandExecutor {

	private LevelUp plugin;
	private Connection conn;

	public FriendCommand(LevelUp plugin) {
		this.plugin = plugin;
		this.conn = plugin.mysql.getConnection();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			if (sender instanceof Player) {
				Player player = (Player) sender;

				if (args.length == 0) {
					sender.sendMessage(ChatColor.GREEN + "------------ 친구 명령어 ------------");
					sender.sendMessage(ChatColor.GOLD + "/친구 신청 <유저>");
					sender.sendMessage(ChatColor.GOLD + "/친구 수락 <유저>");
					sender.sendMessage(ChatColor.GOLD + "/친구 거절 <유저>");
					sender.sendMessage(ChatColor.GOLD + "/친구 삭제 <유저>");
					sender.sendMessage(ChatColor.GOLD + "/친구 신청함");
					sender.sendMessage(ChatColor.GOLD + "/친구 목록");
					sender.sendMessage(ChatColor.GREEN + "--------------------------------");

				} else if (args[0].equalsIgnoreCase("신청함")) {
					if (args.length == 1) {
						List<PlayerData> requests = FriendController.getRequests(plugin, player.getName());
						sender.sendMessage(ChatColor.GREEN + "------------ 친구 신청함 ------------");

						for (PlayerData p : requests) {
							TextComponent playerName = new TextComponent(
									ChatColor.GOLD + " - " + p.getUsername() + ChatColor.GREEN + " [ ");
							TextComponent yes = new TextComponent(ChatColor.GOLD + "" + ChatColor.BOLD + "수락");
							yes.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("친구 신청을 수락합니다.")));
							yes.setClickEvent(
									new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/친구 수락 " + p.getName()));
							TextComponent slash = new TextComponent(ChatColor.GREEN + " / ");
							TextComponent no = new TextComponent(ChatColor.GOLD + "" + ChatColor.BOLD + "거절");
							no.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("친구 신청을 거절합니다.")));
							no.setClickEvent(
									new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/친구 거절 " + p.getName()));
							TextComponent close = new TextComponent(ChatColor.GREEN + " ]");

							player.spigot().sendMessage(new TextComponent(playerName, yes, slash, no, close));
						}

						sender.sendMessage(ChatColor.GREEN + "--------------------------------");

					} else {
						sender.sendMessage(ChatColor.RED + "사용법: /친구 신청함");
					}

				} else if (args[0].equalsIgnoreCase("신청")) {

					if (args.length == 2) {
						PlayerData pd = PlayerController.getPlayerData(plugin, args[1]);

						if (pd != null) {

							if (pd.getUuid().equals(player.getUniqueId())) {
								sender.sendMessage(ChatColor.RED + "자신에게는 친구 신청을 할 수 없습니다.");

							} else {
								FriendData fd = FriendController.getFriendship(plugin, player.getName(), args[1]);

								if (fd == null) {
									FriendController.requestFriend(plugin, conn, player.getName(), args[1]);
									sender.sendMessage(ChatColor.GREEN + "[" + ChatColor.GOLD + args[1]
											+ ChatColor.GREEN + "] 님께 친구 신청을 보냈습니다.");

									OfflinePlayer p = plugin.getServer().getOfflinePlayer(pd.getUuid());
									if (p.isOnline()) {
										Player pp = (Player) p;
										PlayerData senderPD = plugin.players.get(player.getUniqueId());
										pp.sendMessage(ChatColor.GREEN + "[" + ChatColor.GOLD + senderPD.getName()
												+ ChatColor.GREEN + "] 님이 친구 신청을 보냈습니다.");
										pp.playSound(pp, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
									}

								} else {
									if (fd.areFriends()) {
										sender.sendMessage(ChatColor.RED + "이미 [" + args[1] + "] 님과 친구입니다.");
									} else {
										sender.sendMessage(ChatColor.RED + "이미 [" + args[1] + "] 님과 대기중인 친구 신청이 있습니다.");
									}
								}
							}
						} else {
							sender.sendMessage(ChatColor.RED + args[1] + " 은(는) 존재하지 않는 유저입니다.");
						}

					} else {
						sender.sendMessage(ChatColor.RED + "사용법: /친구 신청 <유저>");
					}

				} else if (args[0].equalsIgnoreCase("수락")) {

					if (args.length == 2) {
						PlayerData pd = PlayerController.getPlayerData(plugin, args[1]);

						if (pd == null) {
							sender.sendMessage(ChatColor.RED + args[1] + " 은(는) 존재하지 않는 유저입니다.");

						} else {
							List<PlayerData> requests = FriendController.getRequests(plugin, player.getName());
							boolean found = false;
							for (PlayerData p : requests) {
								if (p.getUuid().equals(pd.getUuid())) {
									FriendController.acceptFriend(plugin, conn, args[1], player.getName());
									sender.sendMessage(ChatColor.GREEN + "이제 [" + ChatColor.GOLD + args[1]
											+ ChatColor.GREEN + "] 님과 친구입니다.");
									found = true;
									break;
								}
							}

							if (!found) {
								sender.sendMessage(ChatColor.RED + "[" + args[1] + "] 님께 받은 친구 신청이 없습니다.");
							}
						}

					} else {
						sender.sendMessage(ChatColor.RED + "사용법: /친구 수락 <유저>");
					}

				} else if (args[0].equalsIgnoreCase("거절")) {

					if (args.length == 2) {
						PlayerData pd = PlayerController.getPlayerData(plugin, args[1]);

						if (pd == null) {
							sender.sendMessage(ChatColor.RED + args[2] + " 은(는) 존재하지 않는 유저입니다.");

						} else {
							List<PlayerData> requests = FriendController.getRequests(plugin, player.getName());
							boolean found = false;
							for (PlayerData p : requests) {
								if (p.getUuid().equals(pd.getUuid())) {
									FriendController.rejectFriend(plugin, conn, args[1], player.getName());
									sender.sendMessage(ChatColor.GREEN + "[" + ChatColor.GOLD + args[1]
											+ ChatColor.GREEN + "] 님의 친구 신청을 거절했습니다.");
									found = true;
									break;
								}
							}

							if (!found) {
								sender.sendMessage(ChatColor.RED + "[" + args[1] + "] 님께 받은 친구 신청이 없습니다.");
							}
						}

					} else {
						sender.sendMessage(ChatColor.RED + "사용법: /친구 거절 <유저>");
					}

				} else if (args[0].equalsIgnoreCase("삭제")) {

					if (args.length == 2) {
						PlayerData pd = PlayerController.getPlayerData(plugin, args[1]);

						if (pd == null) {
							sender.sendMessage(ChatColor.RED + args[2] + " 은(는) 존재하지 않는 유저입니다.");

						} else {
							FriendData fd = FriendController.getFriendship(plugin, player.getName(), args[1]);

							if (fd == null || !fd.areFriends()) {
								sender.sendMessage(ChatColor.RED + "[" + args[1] + "] 님과 친구가 아닙니다.");

							} else if (fd.areFriends()) {
								FriendController.deleteFriend(plugin, conn, sender.getName(), args[1]);
								sender.sendMessage(ChatColor.GREEN + "[" + ChatColor.GOLD + args[1] + ChatColor.GREEN
										+ "] 님이 친구 목록에서 삭제되었습니다.");
							}
						}

					} else {
						sender.sendMessage(ChatColor.RED + "사용법: /친구 삭제 <유저>");
					}

				} else if (args[0].equalsIgnoreCase("목록")) {
					List<PlayerData> friends = FriendController.getFriendList(plugin, player.getName());
					sender.sendMessage(ChatColor.GREEN + "------------- 친구 목록 ------------");

					for (PlayerData p : friends) {
						sender.sendMessage(ChatColor.GOLD + " - " + p.getName());
					}

					sender.sendMessage(ChatColor.GREEN + "--------------------------------");

				} else {
					sender.sendMessage(ChatColor.RED + "/친구 로 도움말을 확인하세요.");
				}

			} else {
				sender.sendMessage(ChatColor.RED + "이 명령어를 실행할 권한이 없습니다.");
			}

		} catch (

		Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

}
