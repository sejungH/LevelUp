package com.levelup.seasonpass;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.google.gson.JsonArray;
import com.levelup.LevelUp;
import com.levelup.LevelUpItem;
import com.levelup.chat.ChatController;
import com.levelup.menu.MenuIcon;
import com.levelup.player.PlayerController;
import com.levelup.player.PlayerData;
import com.levelup.seasonpass.SeasonPassController.SeasonPass;

import net.md_5.bungee.api.ChatColor;

public class SeasonPassCommand implements CommandExecutor {

	private LevelUp plugin;

	public SeasonPassCommand(LevelUp plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			if (sender.isOp()) {
				if (args[0].equalsIgnoreCase("set") && args.length == 3) {
					String username = args[1];
					boolean isPremium = Boolean.valueOf(args[2]);

					PlayerData pd = PlayerController.getPlayerData(plugin, username);
					if (pd != null) {
						List<ItemStack> unobtained = SeasonPassController.getUnobtainedItems(plugin, pd.getUuid());
						SeasonPassController.updatePremium(plugin, pd.getUuid(), isPremium);
						
						if (isPremium) {
							sender.sendMessage(ChatColor.GREEN + "[" + ChatColor.GOLD + pd.getName() + ChatColor.GREEN
									+ "] 의 시즌패스 프리미엄이 활성화되었습니다");
							ItemStack chest = MenuIcon.CHEST.val().getItemStack();
							ItemMeta chestMeta = chest.getItemMeta();
							chestMeta.setDisplayName(ChatController.gradient("시즌패스 프리미엄 보상", ChatColor.YELLOW, ChatColor.GOLD));
							JsonArray jsonArray = new JsonArray();
							List<String> chestLore = new ArrayList<String>();
							for (ItemStack item : unobtained) {
								LevelUpItem lvItem = new LevelUpItem(item);
								jsonArray.add(lvItem.createItemJson());
								if (item.getItemMeta().hasDisplayName()) {
									chestLore.add(ChatColor.GRAY + "- " + ChatColor.stripColor(item.getItemMeta().getDisplayName())
											+ " x " + item.getAmount());
								} else {
									chestLore.add(ChatColor.GRAY + "- " + item.getType().name() + " x " + item.getAmount());
								}

							}
							chestMeta.setLore(chestLore);
							chestMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "unobtained"),
									PersistentDataType.STRING, jsonArray.toString());
							chest.setItemMeta(chestMeta);
							
							OfflinePlayer player = plugin.getServer().getOfflinePlayer(pd.getUuid());
							if (player.isOnline()) {
								((Player) player).sendMessage(ChatColor.GREEN
										+ "시즌패스 프리미엄이 활성화되었습니다. 보물 상자를 우클릭해서 시즌패스에서 놓쳤던 보상들을 획득하실 수 있습니다.");
								((Player) player).playSound(((Player) player), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F,
										1.0F);
							}

						} else {
							sender.sendMessage(ChatColor.GREEN + "[" + ChatColor.GOLD + pd.getName() + ChatColor.GREEN
									+ "] 의 시즌패스 프리미엄이 활성화되었습니다");
							OfflinePlayer player = plugin.getServer().getOfflinePlayer(pd.getUuid());
							if (player.isOnline()) {
								((Player) player).sendMessage(ChatColor.GREEN
										+ "시즌패스 프리미엄이 비활성화되었습니다");
								((Player) player).playSound(((Player) player), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F,
										1.0F);
							}
						}

					} else {
						sender.sendMessage(ChatColor.RED + "유저 " + username + " 은 존재하지 않습니다");
					}

				} else if (args[0].equalsIgnoreCase("info") && args.length == 2) {
					String username = args[1];
					PlayerData pd = PlayerController.getPlayerData(plugin, username);
					if (pd != null) {
						SeasonPass seasonPass = plugin.seasonPassData.get(pd.getUuid());
						sender.sendMessage("user: " + pd.getName());
						sender.sendMessage(" - position: " + seasonPass.getPosition());
						sender.sendMessage(" - available: " + seasonPass.getAvailable());
						sender.sendMessage(" - last_date: " + seasonPass.getLastDate().toString());
						sender.sendMessage(" - premium: " + seasonPass.isPremium());
					} else {
						sender.sendMessage(ChatColor.RED + "유저 " + username + " 은 존재하지 않습니다");
					}
				} else {
					sender.sendMessage(
							ChatColor.RED + "사용법: /premium set {USERNAME} [true|false]  or  /premium info {USERNAME}");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

}
