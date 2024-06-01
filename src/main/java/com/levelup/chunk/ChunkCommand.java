package com.levelup.chunk;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.levelup.LevelUp;
import com.levelup.money.MoneyController;
import com.levelup.player.PlayerController;
import com.levelup.player.PlayerData;
import com.levelup.scoreboard.ScoreboardController;
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
						Chunk chunk = player.getLocation().getChunk();
						UUID found = null;
						UUID isNear = null;
						for (UUID uuid : plugin.playerChunks.keySet()) {
							List<Chunk> chunks = plugin.playerChunks.get(uuid);
							if (chunks.contains(chunk)) {
								found = uuid;
								break;
							}
							
							if (!uuid.equals(player.getUniqueId())) {
								for (int i = -1; i <= 1; i++) {
									for (int j = -1; j <= 1; j++) {
										if (i != 0 || j != 0) {
											Chunk c = world.getCBWorld().getChunkAt(chunk.getX() + i, chunk.getZ() + j);
											if (chunks.contains(c)) {
												isNear = uuid;
												break;
											}
										}
									}
								}
							}
						}

						if (found == null && isNear == null) {
							PlayerData pd = plugin.players.get(player.getUniqueId());

							if (!plugin.playerChunks.containsKey(player.getUniqueId()))
								plugin.playerChunks.put(player.getUniqueId(), new ArrayList<Chunk>());

							int price = (int) Math.round(ChunkController
									.calculatePrice(plugin.playerChunks.get(player.getUniqueId()).size()));

							if (pd.getBalance() >= price) {
								MoneyController.withdrawMoeny(plugin, price, player);
								ChunkController.addPlayerChunk(plugin, player, chunk);
								plugin.playerChunks.get(player.getUniqueId()).add(chunk);
								ChunkController.displayChunkBorder(plugin, player, chunk, Color.fromRGB(0, 255, 0), 5);
								sender.sendMessage(ChatColor.GREEN + "청크를 성공적으로 구매했습니다  " + ChatColor.RESET
										+ ScoreboardController.COIN + ChatColor.GOLD + " " + price);
								player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);

							} else {
								sender.sendMessage(ChatColor.RED + "청크를 구매할 소지금이 부족합니다");
							}

						} else if (found != null) {
							PlayerData pd = plugin.players.get(found);
							sender.sendMessage(ChatColor.RED + "이미 " + pd.getUsername() + " 님이 소유 중인 청크입니다");
							
						} else if (isNear != null) {
							PlayerData pd = plugin.players.get(isNear);
							sender.sendMessage(ChatColor.RED + "근처에 " + pd.getUsername() + " 님이 소유 중인 청크가 있습니다");
						}

					} else {
						sender.sendMessage(ChatColor.RED + "이 월드에서는 /청크 명령어를 실행할 수 없습니다");
					}

				} else if (args[0].equalsIgnoreCase("판매")) {

					if (world.getAlias().equalsIgnoreCase("world")) {

						if (plugin.playerChunks.containsKey(player.getUniqueId())) {
							List<Chunk> chunks = plugin.playerChunks.get(player.getUniqueId());
							Chunk chunk = player.getLocation().getChunk();

							if (chunks.contains(chunk)) {
								int price = (int) Math.round(ChunkController.calculatePrice(chunks.size() - 1));
								MoneyController.depoistMoeny(plugin, price, player);
								ChunkController.deletePlayerChunk(plugin, player, chunk);
								plugin.playerChunks.get(player.getUniqueId()).remove(chunk);
								ChunkController.displayChunkBorder(plugin, player, chunk, Color.fromRGB(255, 0, 0), 2);
								sender.sendMessage(ChatColor.GREEN + "청크를 성공적으로 판매했습니다  " + ChatColor.RESET
										+ ScoreboardController.COIN + ChatColor.GOLD + " " + price);
								player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);

							} else {
								sender.sendMessage(ChatColor.RED + "소유 중인 청크가 아닙니다");
							}

						} else {
							sender.sendMessage(ChatColor.RED + "소유 중인 청크가 아닙니다");
						}

					} else {
						sender.sendMessage(ChatColor.RED + "이 월드에서는 /청크 명령어를 실행할 수 없습니다");
					}

				} else if (args[0].equalsIgnoreCase("확인")) {

					if (world.getAlias().equalsIgnoreCase("world")) {

						if (plugin.playerChunks.containsKey(player.getUniqueId())) {
							List<Chunk> chunks = plugin.playerChunks.get(player.getUniqueId());

							for (Chunk chunk : chunks) {
								ChunkController.displayChunkBorder(plugin, player, chunk, Color.fromRGB(0, 0, 255), 5);
								player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
							}

						} else {
							sender.sendMessage(ChatColor.RED + "소유 중인 청크가 없습니다");
						}

					} else {
						sender.sendMessage(ChatColor.RED + "이 월드에서는 /청크 명령어를 실행할 수 없습니다");
					}

				} else if (args[0].equalsIgnoreCase("목록")) {

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
				} else {
					sender.sendMessage(ChatColor.RED + "사용법: /청크 <구매/판매/확인/목록>");
				}

			} else if (args.length == 2 && sender.isOp()) {

				if (args[0].equalsIgnoreCase("확인") && sender instanceof Player) {
					Player player = (Player) sender;

					MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager()
							.getPlugin("Multiverse-Core");
					MVWorldManager worldManager = core.getMVWorldManager();
					MultiverseWorld world = worldManager.getMVWorld(player.getWorld());
					if (world.getAlias().equalsIgnoreCase("world")) {

						UUID uuid = PlayerController.getPlayerUUID(plugin, args[1]);

						if (uuid != null) {
							if (plugin.playerChunks.containsKey(uuid)) {
								List<Chunk> chunks = plugin.playerChunks.get(uuid);

								for (Chunk chunk : chunks) {
									ChunkController.displayChunkBorder(plugin, player, chunk, Color.fromRGB(0, 0, 255),
											5);
									player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
								}

							} else {
								sender.sendMessage(ChatColor.RED + args[1] + " 님이 소유 중인 청크가 없습니다");
							}

						} else {
							sender.sendMessage(ChatColor.RED + args[1] + " 은(는) 존재하지 않는 유저입니다.");
						}

					} else {
						sender.sendMessage(ChatColor.RED + "이 월드에서는 /청크 명령어를 실행할 수 없습니다");
					}

				} else if (args[0].equalsIgnoreCase("목록")) {

					UUID uuid = PlayerController.getPlayerUUID(plugin, args[1]);

					if (uuid != null) {
						if (plugin.playerChunks.containsKey(uuid)) {
							List<Chunk> chunks = plugin.playerChunks.get(uuid);

							if (!chunks.isEmpty()) {
								sender.sendMessage(ChatColor.GREEN + "------------ 청크 목록 ------------");
								for (Chunk chunk : chunks) {
									sender.sendMessage(ChatColor.GOLD + " - X: " + ChatColor.RESET + chunk.getX() * 16
											+ ChatColor.GOLD + " / Z: " + ChatColor.RESET + chunk.getZ() * 16);
								}
								sender.sendMessage(ChatColor.GREEN + "-------------------------------");

							} else {
								sender.sendMessage(ChatColor.RED + args[1] + " 님이 소유 중인 청크가 없습니다");
							}

						} else {
							sender.sendMessage(ChatColor.RED + args[1] + " 님이 소유 중인 청크가 없습니다");
						}

					} else {
						sender.sendMessage(ChatColor.RED + args[1] + " 은(는) 존재하지 않는 유저입니다.");
					}

				} else {
					sender.sendMessage(ChatColor.RED + "사용법: /청크 <구매/판매/확인/목록>");
				}

			} else {
				sender.sendMessage(ChatColor.RED + "사용법: /청크 <구매/판매/확인/목록>");
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

}
