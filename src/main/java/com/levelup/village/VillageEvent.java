package com.levelup.village;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import com.levelup.LevelUp;
import com.levelup.chunk.ChunkController;
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

public class VillageEvent implements Listener {

	private LevelUp plugin;

	public VillageEvent(LevelUp plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onBookClicked(PlayerInteractEvent event) throws SQLException {
		if (event.getHand() == EquipmentSlot.OFF_HAND) {
			return;
		}

		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			ItemStack book = event.getItem();
			NamespacedKey villageKey = new NamespacedKey(plugin, "village_application");

			if (book != null && book.getType().equals(Material.WRITTEN_BOOK)) {
				Player player = event.getPlayer();
				BookMeta bookMeta = (BookMeta) book.getItemMeta();

				if (bookMeta.getPersistentDataContainer().has(villageKey)) {
					event.setCancelled(true);

					MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager()
							.getPlugin("Multiverse-Core");
					MVWorldManager worldManager = core.getMVWorldManager();
					MultiverseWorld world = worldManager.getMVWorld(player.getWorld());

					if (!world.getAlias().equalsIgnoreCase("world")) {
						player.sendMessage(ChatColor.RED + "이 월드에서는 사용할 수 없습니다");
						return;
					}

					for (UUID uuid : plugin.playerChunks.keySet()) {
						List<Chunk> chunks = plugin.playerChunks.get(uuid);
						if (chunks.contains(player.getLocation().getChunk())) {
							PlayerData pd = plugin.players.get(uuid);
							player.sendMessage(ChatColor.RED + pd.getUsername() + " 님이 소유 중인 청크입니다");
							return;
						}
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

					if (villageName == null || villageName.isEmpty()) {
						player.sendMessage(ChatColor.RED + "마을 이름을 입력하세요");
						ItemStack newBook = VillageController.getVillageBook(plugin, bookMeta.getPage(1));
						player.getInventory().setItem(event.getHand(), newBook);
						return;

					} else {
						if (villageName.contains(" ")) {
							player.sendMessage(ChatColor.RED + "마을 이름에 공백이 포함될 수 없습니다");
							ItemStack newBook = VillageController.getVillageBook(plugin, bookMeta.getPage(1));
							player.getInventory().setItem(event.getHand(), newBook);
							return;
						}

						if (villageName.length() > 4) {
							player.sendMessage(ChatColor.RED + "마을 이름이 4글자를 초과할 수 없습니다");
							ItemStack newBook = VillageController.getVillageBook(plugin, bookMeta.getPage(1));
							player.getInventory().setItem(event.getHand(), newBook);
							return;
						}

						int villageId = VillageController.getVillageId(plugin, villageName);
						if (villageId > 0) {
							player.sendMessage(ChatColor.RED + "이미 존재하는 마을이름입니다");
							ItemStack newBook = VillageController.getVillageBook(plugin, bookMeta.getPage(1));
							player.getInventory().setItem(event.getHand(), newBook);
							return;
						}
					}

					PlayerData presidentData = PlayerController.getPlayerData(plugin, president);
					if (president == null || president.isEmpty()) {
						player.sendMessage(ChatColor.RED + "마을 이장의 닉네임을 입력하세요");
						ItemStack newBook = VillageController.getVillageBook(plugin, bookMeta.getPage(1));
						player.getInventory().setItem(event.getHand(), newBook);
						return;

					} else {
						if (presidentData == null) {
							player.sendMessage(ChatColor.RED + president + " 은(는) 존재하지 않는 유저입니다");
							ItemStack newBook = VillageController.getVillageBook(plugin, bookMeta.getPage(1));
							player.getInventory().setItem(event.getHand(), newBook);
							return;

						} else {
							if (presidentData.getVillage() > 0) {
								player.sendMessage(ChatColor.RED + president + " 은(는) 이미 마을에 가입되어있는 유저입니다");
								ItemStack newBook = VillageController.getVillageBook(plugin, bookMeta.getPage(1));
								player.getInventory().setItem(event.getHand(), newBook);
								return;
							}

							if (plugin.playerChunks.containsKey(presidentData.getUuid())) {
								List<Chunk> chunks = plugin.playerChunks.get(presidentData.getUuid());
								if (!chunks.isEmpty()) {
									player.sendMessage(
											ChatColor.RED + president + " 이(가) 소유중인 청크가 있습니다. 모두 판매 후 다시 시도해주세요");
									ItemStack newBook = VillageController.getVillageBook(plugin, bookMeta.getPage(1));
									player.getInventory().setItem(event.getHand(), newBook);
									return;
								}
							}
						}
					}

					if (villager.size() < 2 || (villager.size() == 2 && villager.contains(president))) {
						player.sendMessage(ChatColor.RED + "마을을 생성하기 위해선 최소 3명의 마을원이 필요합니다");
						ItemStack newBook = VillageController.getVillageBook(plugin, bookMeta.getPage(1));
						player.getInventory().setItem(event.getHand(), newBook);
						return;
					}

					for (String username : villager) {
						PlayerData playerData = PlayerController.getPlayerData(plugin, username);

						if (playerData == null) {
							player.sendMessage(ChatColor.RED + username + " 은(는) 존재하지 않는 유저입니다");
							ItemStack newBook = VillageController.getVillageBook(plugin, bookMeta.getPage(1));
							player.getInventory().setItem(event.getHand(), newBook);
							return;

						} else {
							if (playerData.getVillage() > 0) {
								player.sendMessage(ChatColor.RED + username + " 은(는) 이미 마을에 가입되어있는 유저입니다");
								ItemStack newBook = VillageController.getVillageBook(plugin, bookMeta.getPage(1));
								player.getInventory().setItem(event.getHand(), newBook);
								return;
							}

							if (plugin.playerChunks.containsKey(playerData.getUuid())) {
								List<Chunk> chunks = plugin.playerChunks.get(playerData.getUuid());
								if (!chunks.isEmpty()) {
									player.sendMessage(
											ChatColor.RED + username + " 이(가) 소유중인 청크가 있습니다. 모두 판매 후 다시 시도해주세요");
									ItemStack newBook = VillageController.getVillageBook(plugin, bookMeta.getPage(1));
									player.getInventory().setItem(event.getHand(), newBook);
									return;
								}
							}
						}
					}

					Chunk chunk = player.getLocation().getChunk();
					if (ChunkController.checkPlayerChunkByVillage(plugin, player, chunk)
							&& ChunkController.checkVillageChunkByVillage(plugin, player, chunk)) {

						int[] coordinate = new int[3];
						coordinate[0] = (int) player.getLocation().getX();
						coordinate[1] = (int) player.getLocation().getY();
						coordinate[2] = (int) player.getLocation().getZ();

						player.sendMessage("----------- 마을 신청서 -----------");
						for (String line : context.split("\n")) {
							if (line.startsWith("마을:") || line.startsWith("이장:") || line.startsWith("마을원:")) {
								player.sendMessage(line);
							}
						}
						player.sendMessage("스폰: " + coordinate[0] + ", " + coordinate[1] + ", " + coordinate[2]);
						player.sendMessage("--------------------------------");
						player.sendMessage("위 정보로 마을을 생성하시겠습니까?");

						TextComponent yes = new TextComponent(ChatColor.GREEN + "> " + ChatColor.BOLD + "예");
						yes.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("마을을 생성합니다")));
						yes.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/마을 생성 villageBook confirm "
								+ coordinate[0] + " " + coordinate[1] + " " + coordinate[2]));

						TextComponent no = new TextComponent(ChatColor.GREEN + "> " + ChatColor.BOLD + "예");
						no.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("마을을 생성합니다")));
						no.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/마을 생성 villageBook deny"));

						player.spigot().sendMessage(yes);
						player.spigot().sendMessage(no);

					}
				}
			}
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
