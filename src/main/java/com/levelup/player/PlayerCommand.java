package com.levelup.player;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.levelup.LevelUp;
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

				if (label.equalsIgnoreCase("hat")) {
					ItemStack item = player.getInventory().getItemInMainHand();
					ItemStack current = player.getEquipment().getHelmet();
					CustomStack custom = CustomStack.byItemStack(item);

					if (custom == null || !custom.getNamespacedID().equals(ToolController.TOOLBOX_ID)) {
						player.getEquipment().setHelmet(item);
						player.getInventory().setItemInMainHand(current);
					}

				} else if (label.equalsIgnoreCase("nickname")) {

					if (sender.isOp()) {

						if (args.length == 2) {
							PlayerData pd = PlayerController.getPlayerData(plugin, args[0]);

							if (pd != null) {

								if (PlayerController.isNicknameUnique(plugin, pd, args[1])) {
									PlayerController.updateNickname(plugin, pd.getUuid(), args[1]);

									OfflinePlayer op = plugin.getServer().getOfflinePlayer(pd.getUuid());
									if (op.isOnline())
										PlayerController.updateListName(plugin, (Player) op);

									sender.sendMessage(
											ChatColor.GREEN + pd.getUsername() + " 의 닉네임이 " + args[1] + " 로 설정되었습니다");

								} else {
									sender.sendMessage(ChatColor.RED + "닉네임 " + args[1] + " 은(는) 이미 존재하는 이름입니다");
								}
							} else {
								sender.sendMessage(ChatColor.RED + args[0] + " 은(는) 존재하지 않는 유저입니다");
							}
						} else {
							sender.sendMessage(ChatColor.RED + "사용법: /nickname <유저> <닉네임>");
						}

					} else {
						sender.sendMessage(ChatColor.RED + "이 명령어를 실행할 권한이 없습니다");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

}