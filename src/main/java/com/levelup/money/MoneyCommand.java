package com.levelup.money;

import java.sql.Connection;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.levelup.main.LevelUp;
import com.levelup.player.PlayerData;

import net.md_5.bungee.api.ChatColor;

public class MoneyCommand implements CommandExecutor {

	private LevelUp plugin;
	private Connection conn;
	
	public MoneyCommand(LevelUp plugin) {
		this.plugin = plugin;
		this.conn = plugin.mysql.getConnection();
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
						pd.setBalance(pd.getBalance() + total);
						MoneyController.depoistMoeny(plugin, conn, total, player, pd);
						
					} else if (args.length == 1) {
						
						try {
							int amount = Integer.parseInt(args[0]);
							
							int total = MoneyController.depositAll(plugin, player);
							
							if (amount > total) {
								sender.sendMessage(ChatColor.RED + "입금 액수가 소지금보다 많을 수 없습니다. 소지금 전액 입금했습니다.");
								
							} else {
								int remain = amount - total;
								int remainGold = remain / 100;
								int remainSilver = (remain  % 100) / 10;
								int remainCopper = remain % 10;
								
								player.getInventory().addItem();
								ItemStack gold = MoneyController.GOLD.getItemStack().clone();
								gold.setAmount(remainGold);
								player.getInventory().addItem(gold);
								
								ItemStack silver = MoneyController.SILVER.getItemStack().clone();
								silver.setAmount(remainSilver);
								player.getInventory().addItem(silver);
								
								ItemStack copper = MoneyController.COPPER.getItemStack().clone();
								copper.setAmount(remainCopper);
								player.getInventory().addItem(copper);
								
								pd.setBalance(pd.getBalance() + amount);
								MoneyController.depoistMoeny(plugin, conn, amount, player, pd);
							}
							
						} catch (Exception e) {
							sender.sendMessage(ChatColor.RED + "사용법: /입금 [<액수>]");
						}
						
					} else {
						sender.sendMessage(ChatColor.RED + "사용법: /입금 [<액수>]");
					}
					
				} else if (label.equalsIgnoreCase("출금")) {
					
					
				} else if (label.equalsIgnoreCase("잔고")) {
					
					
				}
				
//				if (stack != null) {
//					ItemStack itemStack = stack.getItemStack();
//					player.getInventory().addItem(itemStack);
//				}
				
			} else {
				sender.sendMessage(ChatColor.RED + "이 명령어를 실행할 권한이 없습니다.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
