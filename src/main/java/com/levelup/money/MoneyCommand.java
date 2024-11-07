package com.levelup.money;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.levelup.LevelUp;
import com.levelup.money.MoneyController.MoneyItem;
import com.levelup.player.PlayerData;

import dev.lone.itemsadder.api.CustomStack;
import net.md_5.bungee.api.ChatColor;

public class MoneyCommand implements CommandExecutor {

	private LevelUp plugin;

	public MoneyCommand(LevelUp plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				PlayerData pd = plugin.players.get(player.getUniqueId());

				if (label.equalsIgnoreCase("입금")) {

					if (args.length == 0) {
						int total = MoneyController.depositAll(plugin, player);
						MoneyController.depoistMoeny(plugin, total, player.getUniqueId());

					} else {
						sender.sendMessage(ChatColor.RED + "사용법: /입금");
					}

				} else if (label.equalsIgnoreCase("출금")) {

					if (args.length == 1) {
						try {
							int amount = Integer.parseInt(args[0]);
							int amountGold = amount / 100;
							int amountSilver = (amount % 100) / 10;
							int amountCopper = amount % 10;

							if (amount > pd.getBalance()) {
								sender.sendMessage(ChatColor.RED + "출금 액수가 진고 액수보다 많을 수 없습니다");

							} else {

								int invCount = 0;

								if (amountGold > 0) {
									invCount += Math.ceil((double) amountGold / 64.0);
								}
								if (amountSilver > 0) {
									invCount += Math.ceil((double) amountSilver / 64.0);
								}
								if (amountCopper > 0) {
									invCount += Math.ceil((double) amountCopper / 64.0);
								}

								int[] countEmpty = getEmptySlots(player, amountGold, amountSilver, amountCopper);

								boolean goldSlot = false;
								boolean silverSlot = false;
								boolean copperSlot = false;

								if (amountGold > 0) {
									if ((double) countEmpty[0] >= ((double) amountGold / 64.0) || countEmpty[3] >= invCount) {
										goldSlot = true;
										invCount -= 1;
									}
								} else {
									goldSlot = true;
								}
								
								if (amountSilver > 0) {
									if ((double) countEmpty[1] >= ((double) amountSilver / 64.0) || countEmpty[3] >= invCount) {
										silverSlot = true;
										invCount -= 1;
									}
								} else {
									silverSlot = true;
								}
								
								if (amountCopper > 0) {
									if ((double) countEmpty[2] >= ((double) amountCopper / 64.0) || countEmpty[3] >= invCount) {
										copperSlot = true;
									}
								} else {
									copperSlot = true;
								}
								
								if (goldSlot && silverSlot && copperSlot) {
									ItemStack gold = MoneyItem.GOLD.getItemStack().clone();
									gold.setAmount(amountGold);
									player.getInventory().addItem(gold);

									ItemStack silver = MoneyItem.SILVER.getItemStack().clone();
									silver.setAmount(amountSilver);
									player.getInventory().addItem(silver);

									ItemStack copper = MoneyItem.COPPER.getItemStack().clone();
									copper.setAmount(amountCopper);
									player.getInventory().addItem(copper);

									MoneyController.withdrawMoeny(plugin, amount, player.getUniqueId());
									player.sendMessage(ChatColor.GOLD + "총 " + MoneyController.withLargeIntegers(amount) + " 코인을 출금했습니다");
									player.playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
									
								} else {
									sender.sendMessage(ChatColor.RED + "인벤토리에 공간이 충분하지 않습니다 인벤토리를 비우고 다시 시도해주세요.");
								}

							}

						} catch (NumberFormatException e) {
							sender.sendMessage(ChatColor.RED + "사용법: /출금 <액수>");
						}

					} else {
						sender.sendMessage(ChatColor.RED + "사용법: /출금 <액수>");
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

	public int[] getEmptySlots(Player player, int amountGold, int amountSilver, int amountCopper) {
		int[] countEmpty = new int[4];
		
		for (ItemStack item : player.getInventory()) {
			
			if (item == null || item.getType() == Material.AIR) {
				countEmpty[3] += 1;
			} else {
				CustomStack customStack = CustomStack.byItemStack(item);
				if (customStack != null) {
					if (customStack.getNamespacedID().equals(MoneyItem.GOLD.getNamespacedID())) {
						if (item.getAmount() + amountGold <= 64) {
							countEmpty[0] += 1;
						}

					} else if (customStack.getNamespacedID().equals(MoneyItem.SILVER.getNamespacedID())) {
						if (item.getAmount() + amountSilver <= 64) {
							countEmpty[1] += 1;
						}

					} else if (customStack.getNamespacedID().equals(MoneyItem.COPPER.getNamespacedID())) {
						if (item.getAmount() + amountCopper <= 64) {
							countEmpty[2] += 1;
						}

					}
				}
			}
		}
		
		countEmpty[3] -= 5;

		return countEmpty;
	}

}
