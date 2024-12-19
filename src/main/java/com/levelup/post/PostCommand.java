package com.levelup.post;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.levelup.LevelUp;
import com.levelup.player.PlayerController;
import com.levelup.player.PlayerData;

public class PostCommand implements CommandExecutor {
	
	private LevelUp plugin;
	
	public PostCommand(LevelUp plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			if (sender.isOp() && sender instanceof Player player) {
				if (args.length == 1) {
					String name = args[0];
					PlayerData pd = PlayerController.getPlayerData(plugin, name);
					if (pd != null) {
						player.openInventory(PostController.getPostInventory(plugin, pd.getUuid()));
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
