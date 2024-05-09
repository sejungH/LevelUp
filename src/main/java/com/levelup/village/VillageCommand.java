package com.levelup.village;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.levelup.main.LevelUp;
import com.levelup.player.PlayerController;
import com.levelup.player.PlayerData;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class VillageCommand implements CommandExecutor {

	private LevelUp plugin;
	private Connection conn;

	public VillageCommand(LevelUp plugin) {
		this.plugin = plugin;
		this.conn = plugin.mysql.getConnection();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {

			if (args.length == 0) {

				sender.sendMessage(ChatColor.GREEN + "------------ 마을 명령어 ------------");

				if (sender.isOp()) {
					sender.sendMessage(ChatColor.GOLD + "/마을 생성 <마을이름>");
					sender.sendMessage(ChatColor.GOLD + "/마을 가입 <마을이름> <유저>");
					sender.sendMessage(ChatColor.GOLD + "/마을 탈퇴 <유저>");
					sender.sendMessage(ChatColor.GOLD + "/마을 삭제 <마을이름>");
					sender.sendMessage(ChatColor.GOLD + "/마을 이장 <유저>");
					sender.sendMessage(ChatColor.GOLD + "/마을 스폰 <마을이름>");
					sender.sendMessage(ChatColor.GOLD + "/마을 정보 <마을이름>");
					sender.sendMessage(ChatColor.GOLD + "/마을 목록");

				} else if (sender instanceof Player && isPresident((Player) sender) > 0) {
					sender.sendMessage(ChatColor.GOLD + "/마을 가입 <유저>");
					sender.sendMessage(ChatColor.GOLD + "/마을 탈퇴 <유저>");
					sender.sendMessage(ChatColor.GOLD + "/마을 이장 <유저>");
					sender.sendMessage(ChatColor.GOLD + "/마을 정보");

				} else {
					sender.sendMessage(ChatColor.GOLD + "/마을 탈퇴");
					sender.sendMessage(ChatColor.GOLD + "/마을 정보");
				}

				sender.sendMessage(ChatColor.GREEN + "--------------------------------");

			} else if (args[0].equalsIgnoreCase("생성")) {

				if (sender.isOp()) {

					if (args.length == 2) {
						int village = VillageController.addVillage(plugin, conn, args[1]);

						if (village > 0) {
							sender.sendMessage(ChatColor.GREEN + "새로운 마을 [" + ChatColor.GOLD + args[1] + ChatColor.GREEN
									+ "] 이(가) 생성되었습니다.");

						} else if (village == -1) {
							sender.sendMessage(ChatColor.RED + "마을 [" + args[1] + "] 은(는) 이미 존재하는 마을입니다.");
						}

					} else {
						sender.sendMessage(ChatColor.RED + "사용법: /마을 생성 <마을이름>");
					}

				} else {
					sender.sendMessage(ChatColor.RED + "이 명령어를 실행할 권한이 없습니다.");
				}
				
			} else if (args[0].equalsIgnoreCase("이름변경")) {
				
				if (sender.isOp()) {

					if (args.length == 3) {
						int villageId = VillageController.getVillageId(plugin, args[1]);
						
						if (villageId > 0) {
							VillageController.renameVillage(plugin, conn, args[1], args[2]);
							sender.sendMessage(ChatColor.GREEN + "마을 [" + ChatColor.GOLD + args[1] + ChatColor.GREEN
									+ "] 이 [" + ChatColor.GOLD + args[2] + ChatColor.GREEN + "] 로 변경되었습니다.");
							
						} else {
							sender.sendMessage(ChatColor.RED + "마을 [" + args[1] + "] 은(는) 존재하지 않는 마을입니다.");
						}

					} else {
						sender.sendMessage(ChatColor.RED + "사용법: /마을 이름변경 <이전이름> <새이름>");
					}

				} else {
					sender.sendMessage(ChatColor.RED + "이 명령어를 실행할 권한이 없습니다.");
				}

			} else if (args[0].equalsIgnoreCase("가입")) {

				if (args.length == 3) {

					if (sender.isOp()) {
						UUID uuid = PlayerController.getPlayerUUID(plugin, args[2]);

						if (uuid == null) {
							sender.sendMessage(ChatColor.RED + args[2] + " 은(는) 존재하지 않는 유저입니다.");

						} else {
							PlayerData pd = plugin.players.get(uuid);
							addUser(sender, args[1], pd);
						}

					} else {
						sender.sendMessage(ChatColor.RED + "이 명령어를 실행할 권한이 없습니다.");
					}

				} else if (args.length == 2) {

					if (sender instanceof Player) {
						Player player = (Player) sender;
						int village = isPresident(player);

						if (village > 0) {
							UUID uuid = PlayerController.getPlayerUUID(plugin, args[1]);
							
							if (uuid == null) {
								sender.sendMessage(ChatColor.RED + args[1] + " 은(는) 존재하지 않는 유저입니다.");

							} else {
								PlayerData pd = plugin.players.get(uuid);
								addUser(sender, args[1], pd);
							}

						} else {
							sender.sendMessage(ChatColor.RED + "이 명령어를 실행할 권한이 없습니다.");
						}

					} else {
						sender.sendMessage(ChatColor.RED + "이 명령어를 실행할 권한이 없습니다.");
					}

				} else {
					sender.sendMessage(ChatColor.RED + "/마을 으로 도움말을 확인하세요.");

				}

			} else if (args[0].equalsIgnoreCase("삭제")) {

				if (sender.isOp()) {

					if (args.length == 2) {
						int count = VillageController.countVillageMembers(plugin, args[1]);

						if (count > 0) {

							if (sender instanceof Player) {
								sender.sendMessage(ChatColor.GREEN + "마을에 " + count + "명의 유저가 존재합니다. 삭제하시겠습니까?");
								Player player = (Player) sender;

								TextComponent yes = new TextComponent(ChatColor.GREEN + "> " + ChatColor.BOLD + "예");
								yes.setHoverEvent(
										new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("마을을 강제로 삭제합니다.")));
								yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
										"/마을 삭제 " + args[1] + " confirm"));

								TextComponent no = new TextComponent(ChatColor.GREEN + "> " + ChatColor.BOLD + "아니오");
								no.setHoverEvent(
										new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("마을을 삭제하지 않습니다.")));
								no.setClickEvent(
										new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/마을 삭제 " + args[1] + " deny"));

								player.spigot().sendMessage(yes);
								player.spigot().sendMessage(no);

							} else {
								sender.sendMessage(ChatColor.RED + "이 명령어를 실행할 권한이 없습니다.");
							}

						} else if (count == 0) {
							VillageController.deleteVillage(plugin, conn, args[1]);
							sender.sendMessage(ChatColor.GREEN + "마을 [" + ChatColor.GOLD + args[1] + ChatColor.GREEN
									+ "] 이(가) 삭제되었습니다.");

						} else {
							sender.sendMessage(ChatColor.RED + "마을 [" + args[1] + "] 은(는) 존재하지 않는 마을입니다.");
						}

					} else if (args.length == 3) {

						if (args[2].equalsIgnoreCase("confirm")) {
							VillageController.deleteVillage(plugin, conn, args[1]);
							sender.sendMessage(ChatColor.GREEN + "마을 [" + ChatColor.GOLD + args[1] + ChatColor.GREEN
									+ "] 이(가) 삭제되었습니다.");

						} else if (args[2].equalsIgnoreCase("deny")) {
							sender.sendMessage(ChatColor.GREEN + "마을을 삭제하지 않았습니다.");

						} else {
							sender.sendMessage(ChatColor.RED + "사용법: /마을 삭제 <마을이름>");
						}

					} else {
						sender.sendMessage(ChatColor.RED + "사용법: /마을 삭제 <마을이름>");
					}

				} else {
					sender.sendMessage(ChatColor.RED + "이 명령어를 실행할 권한이 없습니다.");
				}

			} else if (args[0].equalsIgnoreCase("탈퇴")) {

				if (args.length == 2) {

					if (sender instanceof Player) {
						Player player = (Player) sender;
						int village = isPresident(player);

						if (sender.isOp() || village > 0) {
							UUID uuid = PlayerController.getPlayerUUID(plugin, args[1]);
							
							if (uuid == null) {
								sender.sendMessage(ChatColor.RED + args[1] + " 은(는) 존재하지 않는 유저입니다.");
								
							} else {
								PlayerData pd = plugin.players.get(uuid);
								deleteUser(sender, pd);
							}
							

						} else {
							sender.sendMessage(ChatColor.RED + "이 명령어를 실행할 권한이 없습니다.");
						}

					} else {
						sender.sendMessage(ChatColor.RED + "이 명령어를 실행할 권한이 없습니다.");
					}

				} else if (args.length == 1) {

					if (sender instanceof Player) {
						Player player = (Player) sender;
						PlayerData pd = plugin.players.get(player.getUniqueId());

						if (pd.getVillage() > 0) {
							deleteUser(sender, pd);

						} else {
							sender.sendMessage(ChatColor.RED + "현재 가입된 마을이 없습니다.");
						}

					} else {
						sender.sendMessage(ChatColor.RED + "이 명령어를 실행할 권한이 없습니다.");
					}

				} else {
					sender.sendMessage(ChatColor.RED + "/마을 으로 도움말을 확인하세요.");
				}

			} else if (args[0].equalsIgnoreCase("이장")) {

				if (args.length == 2) {
					if (sender instanceof Player) {
						Player player = (Player) sender;

						if (sender.isOp() || isPresident(player) > 0) {
							UUID uuid = PlayerController.getPlayerUUID(plugin, args[1]);
							
							if (uuid == null) {
								sender.sendMessage(ChatColor.RED + args[1] + " 은(는) 존재하지 않는 유저입니다.");
								
							} else {
								PlayerData pd = plugin.players.get(uuid);
								registerPresident(sender, pd);
							}

						} else {
							sender.sendMessage(ChatColor.RED + "이 명령어를 실행할 권한이 없습니다.");
						}

					} else {
						sender.sendMessage(ChatColor.RED + "이 명령어를 실행할 권한이 없습니다.");
					}

				} else if (args.length == 3) {

					if (args[2].equalsIgnoreCase("confirm")) {
						UUID uuid = PlayerController.getPlayerUUID(plugin, args[1]);
						
						if (uuid == null) {
							sender.sendMessage(ChatColor.RED + args[1] + " 은(는) 존재하지 않는 유저입니다.");
							
						} else {
							PlayerData pd = plugin.players.get(uuid);
							VillageController.dropPresident(plugin, conn, pd);
							VillageController.registerPresident(plugin, conn, pd);
							String villageName = VillageController.getVillageName(plugin, conn, pd.getVillage());
							sender.sendMessage(ChatColor.GREEN + "마을 [" + ChatColor.GOLD + villageName + ChatColor.GREEN
									+ "] 의 이장이 유저 [" + ChatColor.GOLD + pd.getUsername() + ChatColor.GREEN
									+ "] 으로 변경되었습니다.");
						}
						
					} else if (args[2].equalsIgnoreCase("deny")) {
						sender.sendMessage(ChatColor.GREEN + "이장이 변경되지 않았습니다.");
					}

				} else {
					sender.sendMessage(ChatColor.RED + "/마을 으로 도움말을 확인하세요.");
				}

			} else if (args[0].equalsIgnoreCase("목록")) {
				if (sender.isOp()) {
					sender.sendMessage(ChatColor.GREEN + "------------ 마을 목록 ------------");
					for (int id : plugin.villages.keySet()) {
						VillageData vd = plugin.villages.get(id);
						if (vd.getPresident() == null) {
							sender.sendMessage(ChatColor.GOLD + " - " + vd.getName() + " [이장: 없음 / 인원: "
									+ VillageController.countVillageMembers(plugin, vd.getName()) + "]");
						} else {
							PlayerData president = plugin.players.get(vd.getPresident());
							sender.sendMessage(ChatColor.GOLD + " - " + vd.getName() + " [이장: "
									+ president.getUsername() + " / 인원: "
									+ VillageController.countVillageMembers(plugin, vd.getName()) + "]");
						}

					}
					sender.sendMessage(ChatColor.GREEN + "-------------------------------");

				} else {
					sender.sendMessage(ChatColor.RED + "이 명령어를 실행할 권한이 없습니다.");
				}

			} else if (args[0].equalsIgnoreCase("정보")) {

				if (args.length == 1) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						PlayerData pd = plugin.players.get(player.getUniqueId());
						System.out.println(plugin.players.toString());
						System.out.println(pd.toString());

						if (pd.getVillage() > 0) {
							VillageData vd = plugin.villages.get(pd.getVillage());
							sender.sendMessage(ChatColor.GREEN + "------------ 마을 정보 ------------");
							sender.sendMessage(ChatColor.GOLD + "마을이름: " + vd.getName());
							sender.sendMessage(ChatColor.GOLD + "마을원:");

							for (UUID u : plugin.players.keySet()) {
								PlayerData p = plugin.players.get(u);
								if (p.getVillage() == pd.getVillage()) {

									if (vd.getPresident().equals(u)) {
										sender.sendMessage(ChatColor.GOLD + " - " + p.getUsername() + " (이장)");
									} else {
										sender.sendMessage(ChatColor.GOLD + " - " + p.getUsername());
									}

								}
							}
							sender.sendMessage(ChatColor.GREEN + "-------------------------------");

						} else {
							sender.sendMessage(ChatColor.RED + "현재 가입되어있는 마을이 없습니다.");
						}

					} else {
						sender.sendMessage(ChatColor.RED + "이 명령어를 실행할 권한이 없습니다.");
					}

				} else if (args.length == 2) {
					if (sender.isOp()) {
						int villageId = VillageController.getVillageId(plugin, args[1]);

						if (villageId > 0) {
							VillageData vd = plugin.villages.get(villageId);

							sender.sendMessage(ChatColor.GREEN + "------------ 마을 정보 ------------");
							sender.sendMessage(ChatColor.GOLD + "마을이름: " + vd.getName());
							if (vd.getSpawn() != null) {
								sender.sendMessage(ChatColor.GOLD + "스폰: " + Arrays.toString(vd.getSpawn()));
							} else {
								sender.sendMessage(ChatColor.GOLD + "스폰: 없음");
							}
							sender.sendMessage(ChatColor.GOLD + "마을원:");

							for (UUID u : plugin.players.keySet()) {
								PlayerData p = plugin.players.get(u);
								if (p.getVillage() == villageId) {

									if (vd.getPresident() != null && vd.getPresident().equals(u)) {
										sender.sendMessage(ChatColor.GOLD + " - " + p.getUsername() + " (이장)");
									} else {
										sender.sendMessage(ChatColor.GOLD + " - " + p.getUsername());
									}

								}
							}
							sender.sendMessage(ChatColor.GREEN + "-------------------------------");

						} else {
							sender.sendMessage(ChatColor.GREEN + "현재 가입되어있는 마을이 없습니다.");
						}

					} else {
						sender.sendMessage(ChatColor.RED + "이 명령어를 실행할 권한이 없습니다.");
					}

				} else {
					sender.sendMessage(ChatColor.RED + "/마을 으로 도움말을 확인하세요.");
				}

			} else if (args[0].equalsIgnoreCase("스폰")) {

				if (args.length == 2) {

					if (sender.isOp() && sender instanceof Player) {
						Player player = (Player) sender;
						int[] coordinate = new int[3];
						coordinate[0] = (int) player.getLocation().getX();
						coordinate[1] = (int) player.getLocation().getY();
						coordinate[2] = (int) player.getLocation().getZ();

						int villageId = VillageController.getVillageId(plugin, args[1]);
						if (villageId > 0) {
							VillageController.setVillageSpawn(plugin, conn, args[1], coordinate);
							sender.sendMessage(ChatColor.GREEN + "마을 [" + ChatColor.GOLD + args[1] + ChatColor.GREEN
									+ "] 의 스폰 좌표가 (" + coordinate[0] + ", " + coordinate[1] + ", " + coordinate[2]
									+ ") 로 변경되었습니다.");

						} else {
							sender.sendMessage(ChatColor.RED + "마을 [" + args[1] + "] 은(는) 존재하지 않는 마을입니다.");
						}

					}
				}

			} else {
				sender.sendMessage(ChatColor.RED + "/마을 으로 도움말을 확인하세요.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public int isPresident(Player player) throws SQLException {
		for (int villageId : plugin.villages.keySet()) {
			VillageData vd = plugin.villages.get(villageId);
			if (vd.getPresident() != null && vd.getPresident().equals(player.getUniqueId())) {
				return villageId;
			}
		}

		return -1;

	}

	public void addUser(CommandSender sender, String villageName, PlayerData pd) throws SQLException {

		if (pd.getVillage() == 0) {
			int villageId = VillageController.addUser(plugin, conn, villageName, pd);
			if (villageId > 0) {
				sender.sendMessage(ChatColor.GREEN + "유저 [" + ChatColor.GOLD + pd.getUsername() + ChatColor.GREEN
						+ "] 이(가) 마을 [" + ChatColor.GOLD + villageName + ChatColor.GREEN + "] 에 가입되었습니다.");

				OfflinePlayer p = plugin.getServer().getOfflinePlayer(pd.getUuid());
				if (p.isOnline()) {
					Player pp = (Player) p;
					pp.sendMessage(ChatColor.GREEN + "축하합니다! 마을 [" + ChatColor.GOLD + villageName + ChatColor.GREEN
							+ "] 에 가입되었습니다.");
					pp.playSound(pp, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
				}

			} else if (villageId == -1) {
				sender.sendMessage(ChatColor.RED + "마을 [" + villageName + "] 은(는) 존재하지 않는 마을입니다.");
			}

		} else {
			int villageId = VillageController.getVillageId(plugin, villageName);
			if (pd.getVillage() == villageId) {
				sender.sendMessage(ChatColor.RED + pd.getUsername() + " 은(는) 이미 마을에 가입되어있는 유저입니다.");

			} else {
				sender.sendMessage(ChatColor.RED + pd.getUsername() + " 은(는) 다른 마을에 가입되어있는 유저입니다.");
			}
		}
	}

	public void addUser(CommandSender sender, int villageId, PlayerData pd) throws SQLException {
		if (pd.getVillage() == 0) {
			String villageName = VillageController.addUser(plugin, conn, villageId, pd);

			if (villageName != null) {
				sender.sendMessage(ChatColor.GREEN + "유저 [" + ChatColor.GOLD + pd.getUsername() + ChatColor.GREEN
						+ "] 이(가) 마을 [" + ChatColor.GOLD + villageName + ChatColor.GREEN + "] 에 가입되었습니다.");

				OfflinePlayer p = plugin.getServer().getOfflinePlayer(pd.getUuid());
				if (p.isOnline()) {
					Player pp = (Player) p;
					pp.sendMessage(ChatColor.GREEN + "축하합니다! 마을 [" + ChatColor.GOLD + villageName + ChatColor.GREEN
							+ "] 에 가입되었습니다.");
					pp.playSound(pp, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
				}

			} else {
				sender.sendMessage(ChatColor.RED + "오류가 발생했습니다. 시스템 어드민에게 문의해주세요.");
			}

		} else {

			if (pd.getVillage() == villageId) {
				sender.sendMessage(ChatColor.RED + pd.getUsername() + " 은(는) 이미 마을에 가입되어있는 유저입니다.");

			} else {
				sender.sendMessage(ChatColor.RED + pd.getUsername() + " 은(는) 다른 마을에 가입되어있는 유저입니다.");
			}
		}
	}

	public void deleteUser(CommandSender sender, PlayerData pd) throws SQLException {
		if (pd.getVillage() > 0) {
			String villageName = VillageController.deleteUser(plugin, conn, pd);
			sender.sendMessage(ChatColor.GREEN + "유저 [" + ChatColor.GOLD + pd.getUsername() + ChatColor.GREEN
					+ "] 이(가) 마을 [" + ChatColor.GOLD + villageName + ChatColor.GREEN + "] 에서 탈퇴되었습니다.");

			OfflinePlayer p = plugin.getServer().getOfflinePlayer(pd.getUuid());

			if (p.isOnline()) {
				Player pp = (Player) p;
				pp.sendMessage(
						ChatColor.GREEN + "마을 [" + ChatColor.GOLD + villageName + ChatColor.GREEN + "] 에서 탈퇴되었습니다.");
				pp.playSound(pp, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
			}

		} else {
			sender.sendMessage(ChatColor.RED + pd.getUsername() + " 은(는) 마을에 가입되어 있지 않습니다.");
		}
	}

	public void registerPresident(CommandSender sender, PlayerData pd) throws SQLException {
		if (pd.getVillage() > 0) {
			VillageData vd = plugin.villages.get(pd.getVillage());
			UUID presidentUUID = vd.getPresident();
			String villageName = vd.getName();

			if (presidentUUID == null) {
				VillageController.registerPresident(plugin, conn, pd);
				sender.sendMessage(ChatColor.GREEN + "유저 [" + ChatColor.GOLD + pd.getUsername() + ChatColor.GREEN
						+ "] 이(가) 마을 [" + ChatColor.GOLD + villageName + ChatColor.GREEN + "] 의 이장이 되었습니다.");

			} else {
				sender.sendMessage(ChatColor.GREEN + "마을 [" + ChatColor.GOLD + villageName + ChatColor.GREEN
						+ "] 에는 이미 이장이 있습니다. 변경 하시겠습니까?");

				TextComponent yes = new TextComponent(ChatColor.GREEN + "> " + ChatColor.BOLD + "예");
				yes.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("이장을 변경합니다.")));
				yes.setClickEvent(
						new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/마을 이장 " + pd.getUsername() + " confirm"));

				TextComponent no = new TextComponent(ChatColor.GREEN + "> " + ChatColor.BOLD + "아니오");
				no.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("이장을 변경하지 않습니다.")));
				no.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/마을 이장 " + pd.getUsername() + " deny"));

				Player player = (Player) sender;
				player.spigot().sendMessage(yes);
				player.spigot().sendMessage(no);
			}

		} else {
			sender.sendMessage(ChatColor.RED + pd.getUsername() + " 은(는) 마을에 가입되어 있지 않습니다.");
		}
	}
}
