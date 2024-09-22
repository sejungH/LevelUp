package com.levelup.npc;

import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import com.levelup.LevelUp;
import com.levelup.tool.ToolType;

import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.SkinTrait;

public class NPCCommand implements CommandExecutor {

	private LevelUp plugin;

	public NPCCommand(LevelUp plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				
				if ((args.length == 2 || args.length == 3) && args[0].equalsIgnoreCase("spawn")) {
					
					if (sender.isOp()) {
						if (args[1].equalsIgnoreCase("blacksmith")) {
							MythicMob mythicMob = MythicBukkit.inst().getMobManager()
									.getMythicMob(NPCController.BLACKSMITH).orElse(null);
							if (mythicMob != null) {
								ActiveMob mob = mythicMob.spawn(BukkitAdapter.adapt(player.getLocation()), 1);
								LivingEntity entity = (LivingEntity) mob.getEntity().getBukkitEntity();

								boolean setAI = false;
								if (args.length == 3) {
									setAI = args[2].equalsIgnoreCase("true");
								}

								entity.setAI(setAI);
								entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0);
								entity.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(1);
								entity.setCanPickupItems(false);
								entity.setInvulnerable(true);
								entity.setPersistent(true);
								entity.setCollidable(false);
								entity.setSilent(true);

								NamespacedKey npcKey = new NamespacedKey(plugin, "levelup_npc");
								entity.getPersistentDataContainer().set(npcKey, PersistentDataType.BOOLEAN, true);
								NamespacedKey typeKey = new NamespacedKey(plugin, "levelup_npc_type");
								entity.getPersistentDataContainer().set(typeKey, PersistentDataType.STRING,
										"blacksmith");
							}

						} else {
							NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "");

							if (args.length == 3 && args[2].equalsIgnoreCase("true")) {
								LookClose look = npc.getOrAddTrait(LookClose.class);
								look.lookClose(true);
							}
							SkinTrait skin = npc.getOrAddTrait(SkinTrait.class);
							skin.setSkinName(args[1]);
							npc.data().setPersistent("levelup_npc", true);
							npc.spawn(player.getLocation());
						}
					}
					
				} else if (args.length == 2 && args[0].equalsIgnoreCase("quest")) {
					ToolType type = ToolType.get(args[1]);
					NPCController.showQuestMessage(plugin, player, type);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

}
