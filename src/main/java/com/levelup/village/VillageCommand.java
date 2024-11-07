package com.levelup.village;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import com.levelup.LevelUp;
import com.levelup.player.PlayerController;
import com.levelup.player.PlayerData;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class VillageCommand implements CommandExecutor {

	private LevelUp plugin;

	public VillageCommand(LevelUp plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			if (sender instanceof Player player) {

				if (args.length > 0 && args[0].equalsIgnoreCase("신청서")) {

					if (args.length == 1 && sender.isOp()) {
						ItemStack book = VillageController.getVillageBook(plugin, null);
						player.getInventory().addItem(book);

					} else if (args.length == 2 && args[1].equalsIgnoreCase("confirm")) {
						MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager()
								.getPlugin("Multiverse-Core");
						MVWorldManager worldManager = core.getMVWorldManager();
						MultiverseWorld world = worldManager.getMVWorld(player.getWorld());

						if (!world.getAlias().equalsIgnoreCase("world")) {
							player.sendMessage(ChatColor.RED + "이 월드에서는 사용할 수 없습니다");
							return false;
						}

						if (player.getInventory().contains(Material.WRITTEN_BOOK)) {
							for (ItemStack book : player.getInventory().all(Material.WRITTEN_BOOK).values()) {
								BookMeta bookMeta = (BookMeta) book.getItemMeta();
								if (bookMeta.getPersistentDataContainer()
										.has(new NamespacedKey(plugin, "village_application"))) {
									VillageController.createVillage(plugin, player, bookMeta);
									player.getInventory().remove(book);
								}
							}
						}
					} else {
						player.sendMessage(ChatColor.RED + "마을이 생성되지 않았습니다");
					}

				} else if (args.length == 3 && args[0].equalsIgnoreCase("가입") && sender.isOp()) {
					String villageName = args[1];
					String user = args[2];
					PlayerData pd = PlayerController.getPlayerData(plugin, user);

					if (pd != null) {

						if (pd.getVillage() == 0) {
							if (plugin.playerChunks.containsKey(pd.getUuid())) {
								List<Chunk> chunks = plugin.playerChunks.get(pd.getUuid());
								if (!chunks.isEmpty()) {
									player.sendMessage(ChatColor.RED + pd.getUsername()
											+ " 이(가) 소유중인 청크가 있습니다. 모두 판매 후 다시 시도해주세요");
									return false;
								}
							}

							int villageId = VillageController.addUser(plugin, villageName, pd);
							if (villageId > 0) {
								player.sendMessage(ChatColor.GREEN + "유저 [" + ChatColor.GOLD + pd.getUsername()
										+ ChatColor.GREEN + "] 이(가) 마을 [" + ChatColor.GOLD + villageName
										+ ChatColor.GREEN + "] 에 가입되었습니다.");

								for (PlayerData villager : plugin.players.values()) {
									if (villager.getVillage() == villageId) {
										OfflinePlayer vp = plugin.getServer().getOfflinePlayer(villager.getUuid());
										if (vp.isOnline()) {
											((Player) vp).sendMessage(ChatColor.GREEN + "유저 [" + ChatColor.GOLD
													+ pd.getUsername() + ChatColor.GREEN + "] 님이 마을에 가입하셨습니다");
											((Player) vp).playSound(((Player) vp), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,
													1.0F, 1.0F);
										}
									}
								}

							} else if (villageId == -1) {
								sender.sendMessage(ChatColor.RED + "마을 [" + villageName + "] 은(는) 존재하지 않는 마을입니다.");
							}

						} else {
							player.sendMessage(ChatColor.RED + user + " 은(는) 이미 마을에 가입되어있는 유저입니다.");
						}

					} else {
						player.sendMessage(ChatColor.RED + user + " 은(는) 존재하지 않는 유저입니다");
					}

				} else if (args.length > 1 && args[0].equalsIgnoreCase("삭제") && sender.isOp()) {
					String villageName = args[1];

					if (args.length == 2) {
						int villageId = VillageController.getVillageId(plugin, args[1]);

						if (villageId > 0) {
							int count = VillageController.countVillageMembers(plugin, villageId);

							if (count > 0) {
								player.sendMessage(ChatColor.GREEN + "마을에 " + count + "명의 유저가 존재합니다. 삭제하시겠습니까?");

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
								VillageController.deleteVillage(plugin, villageId);
								player.sendMessage(ChatColor.GREEN + "마을 [" + ChatColor.GOLD + args[1] + ChatColor.GREEN
										+ "] 이(가) 삭제되었습니다.");
							}

						} else {
							player.sendMessage(ChatColor.RED + "마을 [" + villageName + "] 은(는) 존재하지 않는 마을입니다.");
						}

					} else if (args.length == 3) {

						if (args[2].equalsIgnoreCase("confirm")) {
							int villageId = VillageController.getVillageId(plugin, villageName);
							VillageController.deleteVillage(plugin, villageId);
							player.sendMessage(ChatColor.GREEN + "마을 [" + ChatColor.GOLD + villageName + ChatColor.GREEN
									+ "] 이(가) 삭제되었습니다.");

						} else {
							player.sendMessage(ChatColor.RED + "마을을 삭제하지 않았습니다.");
						}

					} else {
						player.sendMessage(ChatColor.RED + "사용법: /마을 삭제 <마을이름>");
					}

				} else if (args.length > 1 && args[0].equalsIgnoreCase("이장")) {
					String user = args[1];

					if (args.length == 2 && sender.isOp()) {
						PlayerData pd = PlayerController.getPlayerData(plugin, user);

						if (pd != null) {
							if (pd.getVillage() > 0) {
								VillageData vd = plugin.villages.get(pd.getVillage());
								if (vd.getPresident() == null) {
									VillageController.registerPresident(plugin, pd);
									player.sendMessage(ChatColor.GREEN + "유저 [" + ChatColor.GOLD + pd.getName()
											+ ChatColor.GREEN + "] 이(가) 마을 [" + ChatColor.GOLD + vd.getName()
											+ ChatColor.GREEN + "] 의 이장이 되었습니다.");

								} else {
									player.sendMessage(ChatColor.GREEN + "마을 [" + ChatColor.GOLD + vd.getName()
											+ ChatColor.GREEN + "] 에는 이미 이장이 있습니다. 변경 하시겠습니까?");

									TextComponent yes = new TextComponent(
											ChatColor.GREEN + "> " + ChatColor.BOLD + "예");
									yes.setHoverEvent(
											new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("이장을 변경합니다.")));
									yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
											"/마을 이장 " + pd.getUsername() + " confirm"));

									TextComponent no = new TextComponent(
											ChatColor.GREEN + "> " + ChatColor.BOLD + "아니오");
									no.setHoverEvent(
											new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("이장을 변경하지 않습니다.")));
									no.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
											"/마을 이장 " + pd.getUsername() + " deny"));

									player.spigot().sendMessage(yes);
									player.spigot().sendMessage(no);
								}
							} else {
								player.sendMessage(ChatColor.RED + user + " 은(는) 마을에 가입되어있지 않습니다");
							}
						} else {
							player.sendMessage(ChatColor.RED + user + " 은(는) 존재하지 않는 유저입니다");
						}
					} else if (args.length == 3) {

						if (args[2].equalsIgnoreCase("confirm")) {
							PlayerData pd = PlayerController.getPlayerData(plugin, user);

							if (pd != null) {
								VillageController.dropPresident(plugin, pd);
								VillageController.registerPresident(plugin, pd);
								String villageName = VillageController.getVillageName(plugin, pd.getVillage());
								player.sendMessage(ChatColor.GREEN + "마을 [" + ChatColor.GOLD + villageName
										+ ChatColor.GREEN + "] 의 이장이 유저 [" + ChatColor.GOLD + pd.getName()
										+ ChatColor.GREEN + "] (으)로 변경되었습니다");
								player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);

								OfflinePlayer op = plugin.getServer().getOfflinePlayer(pd.getUuid());
								if (op.isOnline()) {
									((Player) op).sendMessage(ChatColor.GREEN + "마을 [" + ChatColor.GOLD + villageName
											+ ChatColor.GREEN + "] 의 이장이 유저 [" + ChatColor.GOLD + pd.getName()
											+ ChatColor.GREEN + "] (으)로 변경되었습니다");
									((Player) op).playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
								}

							} else {
								player.sendMessage(ChatColor.RED + user + " 은(는) 존재하지 않는 유저입니다");
							}

						} else {
							player.sendMessage(ChatColor.GREEN + "이장이 변경되지 않았습니다");
						}
					}

				} else if (args.length > 1 && args[0].equalsIgnoreCase("탈퇴")) {

					if (args.length == 2) {

						if (args[1].equalsIgnoreCase("confirm")) {
							PlayerData pd = plugin.players.get(player.getUniqueId());

							if (pd.getVillage() > 0) {
								VillageData vd = plugin.villages.get(pd.getVillage());

								if (vd.getPresident().equals(player.getUniqueId())) {
									player.sendMessage(
											ChatColor.RED + "당신은 이 마을의 이장입니다. 마을을 탈퇴하기 전에 다른 유저에게 이장을 위임하세요");

								} else {
									VillageController.deleteUser(plugin, pd.getUuid());
									OfflinePlayer p = plugin.getServer().getOfflinePlayer(pd.getUuid());

									if (p.isOnline()) {
										Player pp = (Player) p;
										pp.sendMessage(ChatColor.GREEN + "마을 [" + ChatColor.GOLD + vd.getName()
												+ ChatColor.GREEN + "] 에서 탈퇴되었습니다.");
										pp.playSound(pp, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
									}
								}

							} else {
								player.sendMessage(ChatColor.RED + "마을에 가입되어있지 않습니다");
							}

						} else {
							player.sendMessage(ChatColor.GREEN + "마을에서 탈퇴하지 않았습니다");
						}

					} else if (args.length == 3) {
						String user = args[1];

						if (args[2].equalsIgnoreCase("confirm")) {
							PlayerData pd = PlayerController.getPlayerData(plugin, user);
							if (pd != null) {

								if (pd.getVillage() > 0) {
									VillageData vd = plugin.villages.get(pd.getVillage());

									if (vd.getPresident().equals(pd.getUuid())) {
										player.sendMessage(
												ChatColor.RED + "당신은 이 마을의 이장입니다. 마을을 탈퇴하기 전에 다른 유저에게 이장을 위임하세요");

									} else {
										VillageController.deleteUser(plugin, pd.getUuid());
										player.sendMessage(ChatColor.GREEN + "유저 [" + ChatColor.GOLD + pd.getUsername()
												+ ChatColor.GREEN + "] 이(가) 마을 [" + ChatColor.GOLD + vd.getName()
												+ ChatColor.GREEN + "] 에서 탈퇴되었습니다");

										OfflinePlayer p = plugin.getServer().getOfflinePlayer(pd.getUuid());

										if (p.isOnline()) {
											Player pp = (Player) p;
											pp.sendMessage(ChatColor.GREEN + "마을 [" + ChatColor.GOLD + vd.getName()
													+ ChatColor.GREEN + "] 에서 탈퇴되었습니다.");
											pp.playSound(pp, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
										}
									}

								} else {
									player.sendMessage(ChatColor.RED + "마을에 가입되어있지 않습니다");
								}

							} else {
								player.sendMessage(ChatColor.GREEN + "유저가 존재하지 않습니다");
							}

						} else {
							player.sendMessage(ChatColor.GREEN + "마을에서 탈퇴하지 않았습니다");
						}
					}

				} else if (args.length == 3 && args[0].equalsIgnoreCase("이름변경") && sender.isOp()) {
					String villageName = args[1];
					String newVillagName = args[2];
					int villageId = VillageController.getVillageId(plugin, villageName);

					if (villageId > 0) {
						VillageController.renameVillage(plugin, villageName, newVillagName);
						sender.sendMessage(ChatColor.GREEN + "마을 [" + ChatColor.GOLD + villageName + ChatColor.GREEN
								+ "] 이 [" + ChatColor.GOLD + newVillagName + ChatColor.GREEN + "] 로 변경되었습니다");

					} else {
						sender.sendMessage(ChatColor.RED + "마을 [" + args[1] + "] 은(는) 존재하지 않는 마을입니다");
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
}
