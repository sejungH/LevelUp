package com.levelup.chunk;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.levelup.LevelUp;
import com.levelup.player.PlayerController;
import com.levelup.player.PlayerData;
import com.levelup.village.VillageData;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;

import net.md_5.bungee.api.ChatColor;

public class ChunkCommand implements CommandExecutor {

	private LevelUp plugin;

	public ChunkCommand(LevelUp plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			if (args.length == 1 && sender instanceof Player) {
				Player player = (Player) sender;

				MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager()
						.getPlugin("Multiverse-Core");
				MVWorldManager worldManager = core.getMVWorldManager();
				MultiverseWorld world = worldManager.getMVWorld(player.getWorld());

				if (args[0].equalsIgnoreCase("구매")) {

					if (world.getAlias().equalsIgnoreCase("world")) {
						PlayerData pd = plugin.players.get(player.getUniqueId());

						Chunk chunk = player.getLocation().getChunk();
						Chunk spawnChunk = world.getSpawnLocation().getChunk();
						int distX = Math.abs(chunk.getX() - spawnChunk.getX());
						int distZ = Math.abs(chunk.getZ() - spawnChunk.getZ());

						if (distX > 3 || distZ > 3) {
							
							if (pd.getVillage() > 0) {
								VillageData vd = plugin.villages.get(pd.getVillage());

								if (vd.getPresident().equals(player.getUniqueId())) {
									ChunkController.purchaseChunkByVillage(plugin, player);

								} else {
									sender.sendMessage(ChatColor.RED + "이 명령어를 실행할 권한이 없습니다");
								}

							} else {
								ChunkController.purchaseChunkByPlayer(plugin, player);
							}
						} else {
							sender.sendMessage(ChatColor.RED + "정거장 근처의 청크는 구매할 수 없습니다");
						}

					} else {
						player.sendMessage(ChatColor.RED + "이 월드에서는 실행할 수 없습니다");
					}

				} else if (args[0].equalsIgnoreCase("판매")) {

					if (world.getAlias().equalsIgnoreCase("world")) {
						PlayerData pd = plugin.players.get(player.getUniqueId());

						if (pd.getVillage() > 0) {
							VillageData vd = plugin.villages.get(pd.getVillage());

							if (vd.getPresident().equals(player.getUniqueId())) {
								ChunkController.saleChunkByVillage(plugin, player);

							} else {
								sender.sendMessage(ChatColor.RED + "이 명령어를 실행할 권한이 없습니다");
							}

						} else {
							ChunkController.saleChunkByPlayer(plugin, player);
						}

					} else {
						player.sendMessage(ChatColor.RED + "이 월드에서는 실행할 수 없습니다");
					}

				} else if (args[0].equalsIgnoreCase("확인")) {

					if (world.getAlias().equalsIgnoreCase("world")) {
						PlayerData pd = plugin.players.get(player.getUniqueId());

						if (pd.getVillage() > 0) {
							if (plugin.villageChunks.containsKey(pd.getVillage())) {
								List<Chunk> chunks = plugin.villageChunks.get(pd.getVillage());

								for (Chunk c : chunks) {
									ChunkController.displayVillageChunkBorder(plugin, player, c, Color.BLUE, 10);
									player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
								}

							} else {
								sender.sendMessage(ChatColor.RED + "소유 중인 청크가 없습니다");
							}

						} else {

							if (plugin.playerChunks.containsKey(player.getUniqueId())) {
								List<Chunk> chunks = plugin.playerChunks.get(player.getUniqueId());

								for (Chunk chunk : chunks) {
									ChunkController.displayPlayerChunkBorder(plugin, player, chunk, Color.BLUE, 10);
									player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
								}

							} else {
								sender.sendMessage(ChatColor.RED + "소유 중인 청크가 없습니다");
							}
						}

					} else {
						player.sendMessage(ChatColor.RED + "이 월드에서는 실행할 수 없습니다");
					}

				} else if (args[0].equalsIgnoreCase("목록")) {

					PlayerData pd = plugin.players.get(player.getUniqueId());

					if (pd.getVillage() > 0) {
						if (plugin.villageChunks.containsKey(pd.getVillage())) {
							List<Chunk> chunks = plugin.villageChunks.get(pd.getVillage());

							if (!chunks.isEmpty()) {
								sender.sendMessage(ChatColor.GREEN + "------------ 청크 목록 ------------");
								for (Chunk c : chunks) {
									sender.sendMessage(ChatColor.GOLD + " - X: " + ChatColor.RESET + c.getX() * 16
											+ ChatColor.GOLD + " / Z: " + ChatColor.RESET + c.getZ() * 16);
								}
								sender.sendMessage(ChatColor.GREEN + "-------------------------------");

							} else {
								sender.sendMessage(ChatColor.RED + "소유 중인 청크가 없습니다");
							}

						} else {
							sender.sendMessage(ChatColor.RED + "소유 중인 청크가 없습니다");
						}

					} else {
						if (plugin.playerChunks.containsKey(player.getUniqueId())) {
							List<Chunk> chunks = plugin.playerChunks.get(player.getUniqueId());

							if (!chunks.isEmpty()) {
								sender.sendMessage(ChatColor.GREEN + "------------ 청크 목록 ------------");
								for (Chunk chunk : chunks) {
									sender.sendMessage(ChatColor.GOLD + " - X: " + ChatColor.RESET + chunk.getX() * 16
											+ ChatColor.GOLD + " / Z: " + ChatColor.RESET + chunk.getZ() * 16);
								}
								sender.sendMessage(ChatColor.GREEN + "-------------------------------");

							} else {
								sender.sendMessage(ChatColor.RED + "소유 중인 청크가 없습니다");
							}

						} else {
							sender.sendMessage(ChatColor.RED + "소유 중인 청크가 없습니다");
						}
					}

				} else {
					sender.sendMessage(ChatColor.RED + "사용법: /청크 <구매/판매/확인/목록>");
				}

			} else if (args.length == 2 && sender.isOp()) {
				String user = args[1];

				if (args[0].equalsIgnoreCase("확인") && sender instanceof Player) {
					Player player = (Player) sender;

					MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager()
							.getPlugin("Multiverse-Core");
					MVWorldManager worldManager = core.getMVWorldManager();
					MultiverseWorld world = worldManager.getMVWorld(player.getWorld());

					if (world.getAlias().equalsIgnoreCase("world")) {

						PlayerData pd = PlayerController.getPlayerData(plugin, user);

						if (pd != null) {

							if (pd.getVillage() > 0) {
								if (plugin.villageChunks.containsKey(pd.getVillage())) {
									List<Chunk> chunks = plugin.villageChunks.get(pd.getVillage());

									for (Chunk c : chunks) {
										ChunkController.displayVillageChunkBorder(plugin, player, c, Color.BLUE, 10);
										player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
									}
								}

							} else {
								if (plugin.playerChunks.containsKey(pd.getUuid())) {
									List<Chunk> chunks = plugin.playerChunks.get(pd.getUuid());

									for (Chunk chunk : chunks) {
										ChunkController.displayPlayerChunkBorder(plugin, player, chunk, Color.BLUE, 10);
										player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
									}

								} else {
									sender.sendMessage(ChatColor.RED + args[1] + " 님이 소유 중인 청크가 없습니다");
								}
							}

						} else {
							sender.sendMessage(ChatColor.RED + args[1] + " 은(는) 존재하지 않는 유저입니다");
						}

					} else {
						player.sendMessage(ChatColor.RED + "이 월드에서는 실행할 수 없습니다");
					}

				} else if (args[0].equalsIgnoreCase("목록")) {

					PlayerData pd = PlayerController.getPlayerData(plugin, user);

					if (pd != null) {

						if (pd.getVillage() > 0) {
							if (plugin.villageChunks.containsKey(pd.getVillage())) {
								List<Chunk> chunks = plugin.villageChunks.get(pd.getVillage());

								if (!chunks.isEmpty()) {
									sender.sendMessage(ChatColor.GREEN + "------------ 청크 목록 ------------");
									for (Chunk c : chunks) {
										sender.sendMessage(ChatColor.GOLD + " - X: " + ChatColor.RESET + c.getX() * 16
												+ ChatColor.GOLD + " / Z: " + ChatColor.RESET + c.getZ() * 16);
									}
									sender.sendMessage(ChatColor.GREEN + "-------------------------------");
								}
							}

						} else {
							if (plugin.playerChunks.containsKey(pd.getUuid())) {
								List<Chunk> chunks = plugin.playerChunks.get(pd.getUuid());

								if (!chunks.isEmpty()) {
									sender.sendMessage(ChatColor.GREEN + "------------ 청크 목록 ------------");
									for (Chunk chunk : chunks) {
										sender.sendMessage(ChatColor.GOLD + " - X: " + ChatColor.RESET
												+ chunk.getX() * 16 + ChatColor.GOLD + " / Z: " + ChatColor.RESET
												+ chunk.getZ() * 16);
									}
									sender.sendMessage(ChatColor.GREEN + "-------------------------------");

								} else {
									sender.sendMessage(ChatColor.RED + args[1] + " 님이 소유 중인 청크가 없습니다");
								}

							} else {
								sender.sendMessage(ChatColor.RED + args[1] + " 님이 소유 중인 청크가 없습니다");
							}
						}

					} else {
						sender.sendMessage(ChatColor.RED + args[1] + " 은(는) 존재하지 않는 유저입니다");
					}

				} else {
					sender.sendMessage(ChatColor.RED + "사용법: /청크 <구매/판매/확인/목록>");
				}

			} else {
				sender.sendMessage(ChatColor.RED + "사용법: /청크 <구매/판매/확인/목록>");
			}

		} catch (

		Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

}
