package com.levelup.player;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.levelup.LevelUp;
import com.levelup.chat.ChatType;
import com.levelup.tool.ToolController;

import dev.lone.itemsadder.api.CustomStack;
import net.md_5.bungee.api.ChatColor;

public class PlayerCommand implements CommandExecutor {

	private LevelUp plugin;

	public PlayerCommand(LevelUp plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				PlayerData pd = plugin.players.get(player.getUniqueId());
				
				if (label.equalsIgnoreCase("전체채팅")) {
					pd.setChatType(ChatType.DEFAULT);
					sender.sendMessage(ChatColor.GREEN + "전체채팅 모드로 전환되었습니다.");
					
				} else if (label.equalsIgnoreCase("마을채팅")) {
					
					if (pd.getVillage() > 0) {
						pd.setChatType(ChatType.VILLAGE);
						sender.sendMessage(ChatColor.GREEN + "마을채팅 모드로 전환되었습니다.");
					}
					
				} else if (label.equalsIgnoreCase("c") || label.equalsIgnoreCase("ㅊ")) {
					
					if (pd.getVillage() > 0) {
						
						if (pd.getChatType() == ChatType.DEFAULT) {
							pd.setChatType(ChatType.VILLAGE);
							sender.sendMessage(ChatColor.GREEN + "마을채팅 모드로 전환되었습니다.");
							
						} else {
							pd.setChatType(ChatType.DEFAULT);
							sender.sendMessage(ChatColor.GREEN + "전체채팅 모드로 전환되었습니다.");
						}
						
					} else {
						sender.sendMessage(ChatColor.RED + "현재 가입되어있는 마을이 없습니다.");
					}
					
				} else if (label.equalsIgnoreCase("hat")) {
					ItemStack item = player.getInventory().getItemInMainHand();
					ItemStack current = player.getEquipment().getHelmet();
					CustomStack custom = CustomStack.byItemStack(item);
					
					if (custom == null || !custom.getNamespacedID().equals(ToolController.TOOLBOX_ID)) {
						player.getEquipment().setHelmet(item);
						player.getInventory().setItemInMainHand(current);
					}
					
				} else {
					sender.sendMessage(ChatColor.RED + "/전체채팅, /지역채팅, /마을채팅 으로 채팅 모드를 전환하세요.");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}