package com.levelup.village;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Color;
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
import com.levelup.LevelUpIcon;
import com.levelup.chunk.ChunkController;
import com.levelup.money.MoneyController;
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

			if (sender instanceof Player) {
				Player player = (Player) sender;

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
						sender.sendMessage(ChatColor.GOLD + "/마을 청크 <구매/판매/목록/확인>");

					} else {
						sender.sendMessage(ChatColor.GOLD + "/마을 탈퇴");
						sender.sendMessage(ChatColor.GOLD + "/마을 정보");
					}

					sender.sendMessage(ChatColor.GREEN + "--------------------------------");

				} else if (args[0].equalsIgnoreCase("생성")) {

					if (args.length == 2) {
						if (sender.isOp()) {
							int village = VillageController.addVillage(plugin, args[1]);

							if (village > 0) {
								sender.sendMessage(ChatColor.GREEN + "새로운 마을 [" + ChatColor.GOLD + args[1]
										+ ChatColor.GREEN + "] 이(가) 생성되었습니다.");

							} else if (village == -1) {
								sender.sendMessage(ChatColor.RED + "마을 [" + args[1] + "] 은(는) 이미 존재하는 마을입니다.");
							}

						} else {
							sender.sendMessage(ChatColor.RED + "이 명령어를 실행할 권한이 없습니다.");
						}

					} else if (args.length == 3 && args[1].equalsIgnoreCase("villageBook")) {
						if (args[2].equalsIgnoreCase("confirm")) {
							NamespacedKey villageKey = new NamespacedKey(plugin, "village_application");
							if (player.getInventory().contains(Material.WRITTEN_BOOK)) {
								Map<Integer, ? extends ItemStack> books = player.getInventory()
										.all(Material.WRITTEN_BOOK);
								for (int i : books.keySet()) {
									ItemStack book = books.get(i);
									BookMeta bookMeta = (BookMeta) book.getItemMeta();
									if (bookMeta.getPersistentDataContainer().has(villageKey)) {

										MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager()
												.getPlugin("Multiverse-Core");
										MVWorldManager worldManager = core.getMVWorldManager();
										MultiverseWorld world = worldManager.getMVWorld(player.getWorld());

										if (!world.getAlias().equalsIgnoreCase("world")) {
											player.sendMessage(ChatColor.RED + "이 월드에서는 사용할 수 없습니다");
											return false;
										}

										String context = ChatColor.stripColor(bookMeta.getPage(1));

										String villageName = null;
										String president = null;
										List<String> villager = new ArrayList<String>();

										for (String line : context.split("\n")) {
											if (line.startsWith("마을:")) {
												int index = line.indexOf("마을:") + 3;
												villageName = line.substring(index).trim();

											} else if (line.startsWith("이장:")) {
												int index = line.indexOf("이장:") + 3;
												president = line.substring(index).trim();

											} else if (line.startsWith("마을원:")) {
												int index = line.indexOf("마을원:") + 4;
												for (String p : line.substring(index).split(",")) {
													villager.add(sanitizeString(p.trim()));
												}
											}
										}

										PlayerData presidentData = PlayerController.getPlayerData(plugin, president);

										int[] coordinate = new int[3];
										coordinate[0] = Integer.parseInt(args[3]);
										coordinate[1] = Integer.parseInt(args[4]);
										coordinate[2] = Integer.parseInt(args[5]);

										Chunk chunk = world.getCBWorld()
												.getBlockAt(coordinate[0], coordinate[1], coordinate[2]).getChunk();

										int villageId = VillageController.addVillage(plugin, villageName);
										VillageController.addUser(plugin, villageId, presidentData);
										VillageController.registerPresident(plugin, presidentData);
										for (String username : villager) {
											if (!username.equalsIgnoreCase(president)) {
												PlayerData playerData = PlayerController.getPlayerData(plugin,
														username);
												VillageController.addUser(plugin, villageId, playerData);
											}
										}
										VillageController.setVillageSpawn(plugin, villageId, coordinate);

										if (!plugin.villageChunks.containsKey(villageId))
											plugin.villageChunks.put(villageId, new ArrayList<Chunk>());

										ChunkController.addVillageChunk(plugin, villageId, chunk);
										ChunkController.displayVillageChunkBorder(plugin, player, chunk,
												Color.GREEN, 5);
										player.sendMessage(ChatColor.GREEN + "축하합니다! 마을 [" + ChatColor.GOLD
												+ villageName + ChatColor.GREEN + "] 이(가) 생성되었습니다!");
										player.performCommand("마을 정보 " + villageName);
										player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
										player.getInventory().setItem(i, new ItemStack(Material.AIR));

										break;
									}
								}
							}

						} else if (args[2].equalsIgnoreCase("deny")) {
							sender.sendMessage(ChatColor.RED + "마을이 생성되지 않았습니다");
						} else {
							sender.sendMessage(ChatColor.RED + "사용법: /마을 생성 <마을이름>");
						}

					} else {
						sender.sendMessage(ChatColor.RED + "사용법: /마을 생성 <마을이름>");
					}

				} else if (args[0].equalsIgnoreCase("이름변경")) {

					if (sender.isOp()) {

						if (args.length == 3) {
							int villageId = VillageController.getVillageId(plugin, args[1]);

							if (villageId > 0) {
								VillageController.renameVillage(plugin, args[1], args[2]);
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
							PlayerData pd = PlayerController.getPlayerData(plugin, args[2]);

							if (pd != null) {
								addUser(sender, args[1], pd);

							} else {
								sender.sendMessage(ChatColor.RED + args[2] + " 은(는) 존재하지 않는 유저입니다.");
							}

						} else {
							sender.sendMessage(ChatColor.RED + "이 명령어를 실행할 권한이 없습니다.");
						}

					} else if (args.length == 2) {

						int villageId = isPresident(player);

						if (villageId > 0) {
							VillageData vd = plugin.villages.get(villageId);
							PlayerData pd = PlayerController.getPlayerData(plugin, args[1]);

							if (pd != null) {
								addUser(sender, vd.getName(), pd);

							} else {
								sender.sendMessage(ChatColor.RED + args[1] + " 은(는) 존재하지 않는 유저입니다.");
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
							int villageId = VillageController.getVillageId(plugin, args[1]);
							int count = VillageController.countVillageMembers(plugin, villageId);

							if (count > 0) {

								sender.sendMessage(ChatColor.GREEN + "마을에 " + count + "명의 유저가 존재합니다. 삭제하시겠습니까?");

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

							} else if (count == 0) {
								VillageController.deleteVillage(plugin, villageId);
								sender.sendMessage(ChatColor.GREEN + "마을 [" + ChatColor.GOLD + args[1] + ChatColor.GREEN
										+ "] 이(가) 삭제되었습니다.");

							} else {
								sender.sendMessage(ChatColor.RED + "마을 [" + args[1] + "] 은(는) 존재하지 않는 마을입니다.");
							}

						} else if (args.length == 3) {

							if (args[2].equalsIgnoreCase("confirm")) {
								int villageId = VillageController.getVillageId(plugin, args[1]);
								VillageController.deleteVillage(plugin, villageId);
								sender.sendMessage(ChatColor.GREEN + "마을 [" + ChatColor.GOLD + args[1] + ChatColor.GREEN
										+ "] 이(가) 삭제되었습니다.");

							} else if (args[2].equalsIgnoreCase("deny")) {
								sender.sendMessage(ChatColor.RED + "마을을 삭제하지 않았습니다.");

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

						int villageId = isPresident(player);

						if (sender.isOp() || villageId > 0) {
							PlayerData pd = PlayerController.getPlayerData(plugin, args[1]);

							if (pd != null) {
								if (villageId > 0 && player.getUniqueId().equals(pd.getUuid())) {
									sender.sendMessage(
											ChatColor.RED + "당신은 이 마을의 이장입니다. 탈퇴하기 전에 '/마을 이장 <유저>' 로 이장을 위임하세요.");

								} else {
									deleteUser(sender, pd);
								}

							} else {
								sender.sendMessage(ChatColor.RED + args[1] + " 은(는) 존재하지 않는 유저입니다.");
							}

						} else {
							sender.sendMessage(ChatColor.RED + "이 명령어를 실행할 권한이 없습니다.");
						}

					} else if (args.length == 1) {
						PlayerData pd = plugin.players.get(player.getUniqueId());

						if (pd.getVillage() > 0) {

							if (isPresident(player) > 0) {
								sender.sendMessage(
										ChatColor.RED + "당신은 이 마을의 이장입니다. 탈퇴하기 전에 '/마을 이장 <유저>' 로 이장을 위임하세요.");

							} else {
								deleteUser(sender, pd);
							}

						} else {
							sender.sendMessage(ChatColor.RED + "현재 가입된 마을이 없습니다.");
						}

					} else {
						sender.sendMessage(ChatColor.RED + "/마을 으로 도움말을 확인하세요.");
					}

				} else if (args[0].equalsIgnoreCase("이장")) {

					if (args.length == 2) {

						if (sender.isOp() || isPresident(player) > 0) {
							PlayerData pd = PlayerController.getPlayerData(plugin, args[1]);

							if (pd != null) {
								registerPresident(sender, pd);

							} else {
								sender.sendMessage(ChatColor.RED + args[1] + " 은(는) 존재하지 않는 유저입니다.");
							}

						} else {
							sender.sendMessage(ChatColor.RED + "이 명령어를 실행할 권한이 없습니다.");
						}

					} else if (args.length == 3) {

						if (args[2].equalsIgnoreCase("confirm")) {
							PlayerData pd = PlayerController.getPlayerData(plugin, args[1]);

							if (pd != null) {
								VillageController.dropPresident(plugin, pd);
								VillageController.registerPresident(plugin, pd);
								String villageName = VillageController.getVillageName(plugin, pd.getVillage());
								sender.sendMessage(ChatColor.GREEN + "마을 [" + ChatColor.GOLD + villageName
										+ ChatColor.GREEN + "] 의 이장이 유저 [" + ChatColor.GOLD + pd.getUsername()
										+ ChatColor.GREEN + "] 으로 변경되었습니다.");

							} else {
								sender.sendMessage(ChatColor.RED + args[1] + " 은(는) 존재하지 않는 유저입니다.");
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
								sender.sendMessage(ChatColor.GOLD + " - " + vd.getName() + ChatColor.RESET
										+ " [이장: 없음 / 인원: " + VillageController.countVillageMembers(plugin, id) + "]");
							} else {
								PlayerData president = plugin.players.get(vd.getPresident());
								sender.sendMessage(ChatColor.GOLD + " - " + vd.getName() + ChatColor.RESET + " [이장: "
										+ president.getUsername() + " / 인원: "
										+ VillageController.countVillageMembers(plugin, id) + "]");
							}

						}
						sender.sendMessage(ChatColor.GREEN + "-------------------------------");

					} else {
						sender.sendMessage(ChatColor.RED + "이 명령어를 실행할 권한이 없습니다.");
					}

				} else if (args[0].equalsIgnoreCase("정보")) {

					if (args.length == 1) {
						PlayerData pd = plugin.players.get(player.getUniqueId());

						if (pd.getVillage() > 0) {
							VillageData vd = plugin.villages.get(pd.getVillage());
							sender.sendMessage(ChatColor.GREEN + "------------ 마을 정보 ------------");
							sender.sendMessage(ChatColor.GOLD + "마을이름: " + ChatColor.RESET + vd.getName());

							if (vd.getSpawn() != null) {
								sender.sendMessage(
										ChatColor.GOLD + "스폰: " + ChatColor.RESET + Arrays.toString(vd.getSpawn()));
							} else {
								sender.sendMessage(ChatColor.GOLD + "스폰: " + ChatColor.RESET + "  없음");
							}

							sender.sendMessage(ChatColor.GOLD + "마을원:");

							for (UUID u : plugin.players.keySet()) {
								PlayerData p = plugin.players.get(u);
								if (p.getVillage() == pd.getVillage()) {

									if (vd.getPresident() != null && vd.getPresident().equals(u)) {
										sender.sendMessage(" - " + p.getUsername() + " (이장)");
									} else {
										sender.sendMessage(" - " + p.getUsername());
									}

								}
							}
							sender.sendMessage(ChatColor.GREEN + "-------------------------------");

						} else {
							sender.sendMessage(ChatColor.RED + "현재 가입되어있는 마을이 없습니다.");
						}

					} else if (args.length == 2) {

						if (sender.isOp()) {
							int villageId = VillageController.getVillageId(plugin, args[1]);

							if (villageId > 0) {
								VillageData vd = plugin.villages.get(villageId);

								sender.sendMessage(ChatColor.GREEN + "------------ 마을 정보 ------------");
								sender.sendMessage(ChatColor.GOLD + "마을이름: " + ChatColor.RESET + vd.getName());

								if (vd.getSpawn() != null) {
									sender.sendMessage(
											ChatColor.GOLD + "스폰: " + ChatColor.RESET + Arrays.toString(vd.getSpawn()));
								} else {
									sender.sendMessage(ChatColor.GOLD + "스폰: " + ChatColor.RESET + "  없음");
								}
								sender.sendMessage(ChatColor.GOLD + "마을원:");

								for (UUID u : plugin.players.keySet()) {
									PlayerData p = plugin.players.get(u);
									if (p.getVillage() == villageId) {

										if (vd.getPresident() != null && vd.getPresident().equals(u)) {
											sender.sendMessage(" - " + p.getUsername() + " (이장)");
										} else {
											sender.sendMessage(" - " + p.getUsername());
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

					if (sender.isOp()) {

						if (args.length == 2) {

							int[] coordinate = new int[3];
							coordinate[0] = (int) player.getLocation().getX();
							coordinate[1] = (int) player.getLocation().getY();
							coordinate[2] = (int) player.getLocation().getZ();

							int villageId = VillageController.getVillageId(plugin, args[1]);
							if (villageId > 0) {
								VillageController.setVillageSpawn(plugin, villageId, coordinate);
								sender.sendMessage(ChatColor.GREEN + "마을 [" + ChatColor.GOLD + args[1] + ChatColor.GREEN
										+ "] 의 스폰 좌표가 (" + coordinate[0] + ", " + coordinate[1] + ", " + coordinate[2]
										+ ") 로 변경되었습니다.");

							} else {
								sender.sendMessage(ChatColor.RED + "마을 [" + args[1] + "] 은(는) 존재하지 않는 마을입니다.");
							}

						} else {
							sender.sendMessage(ChatColor.RED + "사용법: /마을 스폰 <마을이름>");
						}

					} else {
						sender.sendMessage(ChatColor.RED + "이 명령어를 실행할 권한이 없습니다.");
					}

				} else if (args[0].equalsIgnoreCase("신청서")) {

					if (sender.isOp()) {

						if (args.length == 1) {
							ItemStack book = VillageController.getVillageBook(plugin, null);
							player.getInventory().addItem(book);

						} else {
							sender.sendMessage(ChatColor.RED + "사용법: /마을 신청서");
						}

					} else {
						sender.sendMessage(ChatColor.RED + "이 명령어를 실행할 권한이 없습니다.");
					}

				} else if (args[0].equalsIgnoreCase("청크")) {
					MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager()
							.getPlugin("Multiverse-Core");
					MVWorldManager worldManager = core.getMVWorldManager();
					MultiverseWorld world = worldManager.getMVWorld(player.getWorld());

					Chunk chunk = player.getLocation().getChunk();

					if (args.length == 2) {

						if (args[1].equalsIgnoreCase("구매")) {

							int villageId = isPresident(player);
							if (villageId > 0) {

								if (ChunkController.checkPlayerChunkByVillage(plugin, player, chunk)
										&& ChunkController.checkVillageChunkByVillage(plugin, player, chunk)) {

									if (!plugin.villageChunks.containsKey(villageId))
										plugin.villageChunks.put(villageId, new ArrayList<Chunk>());

									int price = (int) Math.round(ChunkController
											.calculateVillageChunkPrice(plugin.villageChunks.get(villageId).size()));

									List<Chunk> chunks = plugin.villageChunks.get(villageId);

									if (chunks.isEmpty()) {
										PlayerData pd = plugin.players.get(player.getUniqueId());

										if (pd.getBalance() >= price) {
											MoneyController.withdrawMoeny(plugin, price, player.getUniqueId());
											ChunkController.addVillageChunk(plugin, villageId, chunk);
											ChunkController.displayVillageChunkBorder(plugin, player, chunk,
													Color.GREEN, 5);
											
											int[] coordinate = new int[3];
											coordinate[0] = (int) player.getLocation().getX();
											coordinate[1] = (int) player.getLocation().getY();
											coordinate[2] = (int) player.getLocation().getZ();
											VillageController.setVillageSpawn(plugin, villageId, coordinate);
											
											LocalDate today = LocalDate.now();
											LocalDate lastTaxPaid;
											if (today.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
												lastTaxPaid = LocalDate.now();
											} else {
												lastTaxPaid = today.minusDays(today.getDayOfWeek().getValue() + 1);
											}
											int newTax = VillageController.countVillageMembers(plugin, villageId) * VillageController.TAX_RATE;
											VillageController.setLastTax(plugin, villageId, newTax);
											VillageController.updateLastTaxPaid(plugin, villageId, lastTaxPaid);
											
											sender.sendMessage(
													ChatColor.GREEN + "마을 청크를 성공적으로 구매했습니다  " + ChatColor.RESET
															+ LevelUpIcon.COIN.val() + ChatColor.GOLD + " " + price);
											player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);

										} else {
											sender.sendMessage(ChatColor.RED + "청크를 구매할 소지금이 부족합니다  " + ChatColor.RESET
													+ LevelUpIcon.COIN.val() + ChatColor.GOLD + " " + price);
										}

									} else {
										for (Chunk c : chunks) {
											int distX = chunk.getX() - c.getX();
											int distZ = chunk.getZ() - c.getZ();
											int newX = 0;
											int newZ = 0;

											if (distX < -1 && distX > -5)
												newX = -3;
											else if (distX > 1 && distX < 5)
												newX = 3;

											if (distZ < -1 && distZ > -5)
												newZ = -3;
											else if (distZ > 1 && distX < 5)
												newZ = 3;

											if (newX != 0 || newZ != 0) {
												PlayerData pd = plugin.players.get(player.getUniqueId());
												if (pd.getBalance() >= price) {
													MoneyController.withdrawMoeny(plugin, price, player.getUniqueId());
													Chunk newChunk = world.getCBWorld().getChunkAt(c.getX() + newX,
															c.getZ() + newZ);
													ChunkController.addVillageChunk(plugin, villageId, newChunk);
													ChunkController.displayVillageChunkBorder(plugin, player, newChunk,
															Color.GREEN, 5);
													
													sender.sendMessage(ChatColor.GREEN + "마을 청크를 성공적으로 구매했습니다  "
															+ ChatColor.RESET + LevelUpIcon.COIN.val() + ChatColor.GOLD
															+ " " + price);
													player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F,
															1.0F);

												} else {
													sender.sendMessage(ChatColor.RED + "청크를 구매할 소지금이 부족합니다  "
															+ ChatColor.RESET + LevelUpIcon.COIN.val() + ChatColor.GOLD
															+ " " + price);
												}
												break;
											}
										}
									}
								}
							} else {
								sender.sendMessage(ChatColor.RED + "이 명령어를 실행할 권한이 없습니다.");
							}

						} else if (args[1].equalsIgnoreCase("판매")) {

							int villageId = isPresident(player);
							if (villageId > 0) {

								if (world.getAlias().equalsIgnoreCase("world")) {
									PlayerData pd = plugin.players.get(player.getUniqueId());
									List<Chunk> chunks = plugin.villageChunks.get(pd.getVillage());
									List<Chunk> adjoints = ChunkController.getAdjointChunks(chunk);

									List<Chunk> villageChunks = new ArrayList<Chunk>(chunks);
									villageChunks.retainAll(adjoints);

									if (!villageChunks.isEmpty()) {
										Chunk vc = villageChunks.get(0);
										List<Chunk> near = ChunkController.getNearVillageChunk(plugin, villageId, vc);
										int price = (int) Math
												.round(ChunkController.calculateVillageChunkPrice(chunks.size() - 1));

										if (near.size() == 1) {
											MoneyController.depoistMoeny(plugin, price, player.getUniqueId());
											ChunkController.deleteVillageChunk(plugin, villageId, vc);
											ChunkController.displayVillageChunkBorder(plugin, player, vc, Color.RED, 5);
											sender.sendMessage(
													ChatColor.GREEN + "마을 청크를 성공적으로 판매했습니다  " + ChatColor.RESET
															+ LevelUpIcon.COIN.val() + ChatColor.GOLD + " " + price);
											player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);

										} else if (near.size() > 1) {
											boolean canSell = true;
											for (Chunk n : near) {
												List<Chunk> nn = ChunkController.getNearVillageChunk(plugin, villageId,
														n);
												if (nn.size() == 1) {
													canSell = false;
													break;
												}
											}

											if (canSell) {
												MoneyController.depoistMoeny(plugin, price, player.getUniqueId());
												ChunkController.deleteVillageChunk(plugin, villageId, vc);
												ChunkController.displayVillageChunkBorder(plugin, player, vc, Color.RED,
														2);
												sender.sendMessage(ChatColor.GREEN + "마을 청크를 성공적으로 판매했습니다  "
														+ ChatColor.RESET + LevelUpIcon.COIN.val() + ChatColor.GOLD
														+ " " + price);
												player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F,
														1.0F);

											} else {
												sender.sendMessage(
														ChatColor.RED + "이 청크를 판매할 수 없습니다 (모든 마을청크는 인접하여야 합니다)");
											}

										} else {
											sender.sendMessage(ChatColor.RED + "마을에 적어도 하나의 청크를 유지해야합니다");
										}

									} else {
										sender.sendMessage(ChatColor.RED + "마을에서 소유 중인 청크가 아닙니다");
									}

								} else {
									sender.sendMessage(ChatColor.RED + "이 월드에서는 실행할 수 없습니다");
								}
							} else {
								sender.sendMessage(ChatColor.RED + "이 명령어를 실행할 권한이 없습니다.");
							}

						} else if (args[1].equalsIgnoreCase("확인")) {

							if (world.getAlias().equalsIgnoreCase("world")) {

								PlayerData pd = plugin.players.get(player.getUniqueId());

								if (pd.getVillage() > 0) {
									if (plugin.villageChunks.containsKey(pd.getVillage())) {
										List<Chunk> chunks = plugin.villageChunks.get(pd.getVillage());

										for (Chunk c : chunks) {
											ChunkController.displayVillageChunkBorder(plugin, player, c, Color.BLUE,
													10);
											player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
										}

									} else {
										sender.sendMessage(ChatColor.RED + "소유 중인 청크가 없습니다");
									}

								} else {
									sender.sendMessage(ChatColor.GREEN + "현재 가입되어있는 마을이 없습니다.");
								}

							} else {
								sender.sendMessage(ChatColor.RED + "이 월드에서는 실행할 수 없습니다");
							}

						} else if (args[1].equalsIgnoreCase("목록")) {

							PlayerData pd = plugin.players.get(player.getUniqueId());

							if (pd.getVillage() > 0) {

								if (plugin.villageChunks.containsKey(pd.getVillage())) {
									List<Chunk> chunks = plugin.villageChunks.get(pd.getVillage());

									if (!chunks.isEmpty()) {
										sender.sendMessage(ChatColor.GREEN + "------------ 청크 목록 ------------");
										for (Chunk c : chunks) {
											sender.sendMessage(ChatColor.GOLD + " - X: " + ChatColor.RESET
													+ c.getX() * 16 + ChatColor.GOLD + " / Z: " + ChatColor.RESET
													+ c.getZ() * 16);
										}
										sender.sendMessage(ChatColor.GREEN + "-------------------------------");

									} else {
										sender.sendMessage(ChatColor.RED + "소유 중인 청크가 없습니다");
									}

								} else {
									sender.sendMessage(ChatColor.RED + "소유 중인 청크가 없습니다");
								}

							} else {
								sender.sendMessage(ChatColor.GREEN + "현재 가입되어있는 마을이 없습니다.");
							}

						} else {
							sender.sendMessage(ChatColor.RED + "사용법: /마을 청크 <구매/판매/목록/확인>");
						}

					} else if (args.length == 3 && sender.isOp()) {

						if (args[1].equalsIgnoreCase("확인")) {

							int villageId = VillageController.getVillageId(plugin, args[1]);

							if (villageId > 0) {
								if (plugin.villageChunks.containsKey(villageId)) {
									List<Chunk> chunks = plugin.villageChunks.get(villageId);

									for (Chunk c : chunks) {
										ChunkController.displayVillageChunkBorder(plugin, player, c, Color.BLUE, 10);
										player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
									}

								} else {
									sender.sendMessage(ChatColor.RED + "마을 [" + args[1] + "] 에서 소유 중인 청크가 없습니다");
								}

							} else {
								sender.sendMessage(ChatColor.RED + "[" + args[1] + "] 은(는) 존재하지 않는 마을입니다.");
							}

						} else if (args[1].equalsIgnoreCase("목록")) {

							if (world.getAlias().equalsIgnoreCase("world")) {

								int villageId = VillageController.getVillageId(plugin, args[1]);

								if (villageId > 0) {
									if (plugin.villageChunks.containsKey(villageId)) {
										List<Chunk> chunks = plugin.villageChunks.get(villageId);

										if (!chunks.isEmpty()) {
											sender.sendMessage(ChatColor.GREEN + "------------ 청크 목록 ------------");
											for (Chunk c : chunks) {
												sender.sendMessage(ChatColor.GOLD + " - X: " + ChatColor.RESET
														+ c.getX() * 16 + ChatColor.GOLD + " / Z: " + ChatColor.RESET
														+ c.getZ() * 16);
											}
											sender.sendMessage(ChatColor.GREEN + "-------------------------------");

										} else {
											sender.sendMessage(
													ChatColor.RED + "마을 [" + args[1] + "] 에서 소유 중인 청크가 없습니다");
										}

									} else {
										sender.sendMessage(ChatColor.RED + "마을 [" + args[1] + "] 에서 소유 중인 청크가 없습니다");
									}

								} else {
									sender.sendMessage(ChatColor.RED + "[" + args[1] + "] 은(는) 존재하지 않는 마을입니다.");
								}

							} else {
								sender.sendMessage(ChatColor.RED + "이 월드에서는 실행할 수 없습니다");
							}

						}

					} else {
						sender.sendMessage(ChatColor.RED + "사용법: /마을 청크 <구매/판매/목록/확인>");
					}

				} else {
					sender.sendMessage(ChatColor.RED + "/마을 으로 도움말을 확인하세요.");
				}
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

			if (plugin.playerChunks.containsKey(pd.getUuid())) {
				List<Chunk> chunks = plugin.playerChunks.get(pd.getUuid());
				if (!chunks.isEmpty()) {
					sender.sendMessage(ChatColor.RED + pd.getUsername() + " 이(가) 소유중인 청크가 있습니다. 모두 판매 후 다시 시도해주세요");
					return;
				}
			}

			int villageId = VillageController.addUser(plugin, villageName, pd);
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
			String villageName = VillageController.addUser(plugin, villageId, pd);

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
			String villageName = VillageController.deleteUser(plugin, pd);
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
				VillageController.registerPresident(plugin, pd);
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

	public String sanitizeString(String str) {
		if (str.contains("§")) {
			int index = str.indexOf("§");
			return sanitizeString(str.substring(0, index) + str.substring(index, index + 2));

		} else {
			return str.trim();
		}
	}
}
