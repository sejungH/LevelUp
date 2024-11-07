package com.levelup.tool;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.levelup.LevelUp;

import net.md_5.bungee.api.ChatColor;

public class ToolCommand implements CommandExecutor {

	private LevelUp plugin;

	public ToolCommand(LevelUp plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			if (sender.isOp() && sender instanceof Player player) {
				if (args.length > 1) {
					if (args[0].equalsIgnoreCase("pickaxe") || args[0].equalsIgnoreCase("axe")
							|| args[0].equalsIgnoreCase("sword") || args[0].equalsIgnoreCase("shovel")) {
						ToolType type = ToolType.get(args[0]);
						ToolAbstract tool = plugin.tools.get(player.getUniqueId()).getTool(type);

						if (args[1].equalsIgnoreCase("exp") && args.length == 4) {
							int num = Integer.parseInt(args[3]);

							if (args[2].equalsIgnoreCase("set")) {
								tool.setExp(num);
								ToolController.updateToolExp(plugin, player.getUniqueId());
								ToolController.showBossBar(plugin, player, type);

							} else if (args[2].equalsIgnoreCase("add")) {
								tool.addExp(num);
								ToolController.updateToolExp(plugin, player.getUniqueId());
								ToolController.showBossBar(plugin, player, type);
							}

						} else if (args[1].equalsIgnoreCase("level") && args.length == 4) {
							int num = Integer.parseInt(args[3]);
							if (args[2].equalsIgnoreCase("set")) {
								tool.setLevel(num);
								ToolController.updateToolData(plugin, player.getUniqueId(), tool);
								ToolController.showBossBar(plugin, player, type);
							}

						} else if (args[1].equalsIgnoreCase("stat")) {
							sender.sendMessage("Type: " + type.toString());
							sender.sendMessage("Material: " + tool.getMaterial().toString());
							sender.sendMessage("Level: " + tool.getLevel());
							sender.sendMessage("Exp: " + tool.getExp() + "/" + tool.getMaxExp());

						} else if (args[1].equalsIgnoreCase("reset")) {
							ItemStack item = tool.getAsItemStack();

							if (type == ToolType.PICKAXE) {
								PickaxeData pickaxe = new PickaxeData(plugin, player.getUniqueId(),
										Material.WOODEN_PICKAXE);
								plugin.tools.get(player.getUniqueId()).setPickaxe(pickaxe);
							} else if (type == ToolType.AXE) {
								AxeData axe = new AxeData(plugin, player.getUniqueId(), Material.WOODEN_AXE);
								plugin.tools.get(player.getUniqueId()).setAxe(axe);

							} else if (type == ToolType.SWORD) {
								SwordData sword = new SwordData(plugin, player.getUniqueId(), Material.WOODEN_SWORD);
								plugin.tools.get(player.getUniqueId()).setSword(sword);

							} else if (type == ToolType.SHOVEL) {
								ShovelData shovel = new ShovelData(plugin, player.getUniqueId(),
										Material.WOODEN_SHOVEL);
								plugin.tools.get(player.getUniqueId()).setShovel(shovel);
							}

							tool = plugin.tools.get(player.getUniqueId()).getTool(type);
							ToolController.updateToolData(plugin, player.getUniqueId(), tool);

							if (player.getInventory().contains(item)) {
								player.getInventory().remove(item);
								player.getInventory().addItem(tool.getAsItemStack());
							}
						}

					} else if (args[0].equalsIgnoreCase("boost") && args.length == 3) {
						try {
							int mult = Integer.parseInt(args[1]);
							int sec = Integer.parseInt(args[2]);
							int tick = (int) plugin.getServer().getServerTickManager().getTickRate();

							ToolController.boostMults.put(player.getUniqueId(), mult);

							BossBar boostBar = ToolController.boostBars.get(player.getUniqueId());
							boostBar.setTitle("경험치 " + mult + "배 이벤트");
							boostBar.setVisible(true);

							final int[] countdown = { sec };
							final int[] task = new int[1];

							task[0] = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
								@Override
								public void run() {
									if (countdown[0] > 0) {
										boostBar.setProgress((double) countdown[0] / (double) sec);
										countdown[0]--;

									} else {
										ToolController.boostMults.put(player.getUniqueId(), 1);
										boostBar.setVisible(false);
										Bukkit.getScheduler().cancelTask(task[0]);
									}
								}

							}, 0, tick);

						} catch (NumberFormatException e) {
							sender.sendMessage(ChatColor.RED + "사용법: /t boost <mult> <sec>");
						}
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
