package com.levelup.ride;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import com.levelup.LevelUp;

import dev.lone.itemsadder.api.CustomStack;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import net.md_5.bungee.api.ChatColor;

public class RideEvent implements Listener {

	private LevelUp plugin;
	private HashMap<UUID, Long> cooldowns;
	private final int cool = 1;

	public RideEvent(LevelUp plugin) {
		this.plugin = plugin;
		this.cooldowns = new HashMap<UUID, Long>();
	}

	@EventHandler
	public void onPlayerUseKey(PlayerInteractEvent event) {
		if (event.getHand() == EquipmentSlot.OFF_HAND)
			return;

		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player player = event.getPlayer();
			CustomStack key = CustomStack.byItemStack(event.getItem());
			NamespacedKey ownerKey = new NamespacedKey(plugin, "owner");
			NamespacedKey uuidKey = new NamespacedKey(plugin, "uuid");
			NamespacedKey rideKey = new NamespacedKey(plugin, "ride");

			if (key != null && key.getNamespacedID().contains("_key")) {

				if (cooldowns.get(player.getUniqueId()) != null && !hasCooldown(event.getPlayer())) {
					return;
				}

				ItemMeta meta = key.getItemStack().getItemMeta();

				if (meta.getPersistentDataContainer().has(ownerKey)) {
					UUID ownerUUID = UUID
							.fromString(meta.getPersistentDataContainer().get(ownerKey, PersistentDataType.STRING));

					if (player.getUniqueId().equals(ownerUUID)) {

						if (meta.getPersistentDataContainer().has(uuidKey)) {
							UUID entityUUID = UUID.fromString(key.getItemStack().getItemMeta()
									.getPersistentDataContainer().get(uuidKey, PersistentDataType.STRING));
							LivingEntity entity = (LivingEntity) plugin.getServer().getEntity(entityUUID);

							if (entity != null) {
								entity.remove();
								player.sendMessage(ChatColor.GREEN + "[" + ChatColor.GOLD
										+ ChatColor.stripColor(entity.getCustomName()) + ChatColor.GREEN
										+ "] 을 보관했습니다");
								meta.getPersistentDataContainer().remove(uuidKey);
								key.getItemStack().setItemMeta(meta);
								player.getInventory().setItemInMainHand(key.getItemStack());

							} else {
								String id = "ride_" + key.getNamespacedID().substring(
										key.getNamespacedID().indexOf(":") + 1, key.getNamespacedID().indexOf("_key"));
								MythicMob mythicMob = MythicBukkit.inst().getMobManager().getMythicMob(id).orElse(null);

								if (mythicMob != null) {
									ActiveMob mob = mythicMob.spawn(BukkitAdapter.adapt(player.getLocation()), 1);
									player.sendMessage(ChatColor.GREEN + "[" + ChatColor.GOLD + mob.getDisplayName()
											+ ChatColor.GREEN + "] 을 소환했습니다");
									meta.getPersistentDataContainer().set(uuidKey, PersistentDataType.STRING,
											mob.getUniqueId().toString());
									mob.getEntity().getBukkitEntity().getPersistentDataContainer().set(rideKey,
											PersistentDataType.BOOLEAN, true);
									key.getItemStack().setItemMeta(meta);
									player.getInventory().setItemInMainHand(key.getItemStack());
								}
							}

							activateCooldown(player);

						} else {
							String id = "ride_" + key.getNamespacedID().substring(
									key.getNamespacedID().indexOf(":") + 1, key.getNamespacedID().indexOf("_key"));
							MythicMob mythicMob = MythicBukkit.inst().getMobManager().getMythicMob(id).orElse(null);

							if (mythicMob != null) {
								ActiveMob mob = mythicMob.spawn(BukkitAdapter.adapt(player.getLocation()), 1);
								player.sendMessage(ChatColor.GREEN + "[" + ChatColor.GOLD + mob.getDisplayName()
										+ ChatColor.GREEN + "] 을 소환했습니다");
								meta.getPersistentDataContainer().set(uuidKey, PersistentDataType.STRING,
										mob.getUniqueId().toString());
								mob.getEntity().getBukkitEntity().getPersistentDataContainer().set(rideKey,
										PersistentDataType.BOOLEAN, true);
								key.getItemStack().setItemMeta(meta);
								player.getInventory().setItemInMainHand(key.getItemStack());
								activateCooldown(player);
							}
						}
					} else {
						player.sendMessage(ChatColor.RED + "본인 소유의 탈것만 소환할 수 있습니다");
					}

				} else {
					List<String> lore = new ArrayList<String>();
					lore.add(ChatColor.GRAY + "소유자: " + player.getName());
					meta.setLore(lore);
					meta.getPersistentDataContainer().set(ownerKey, PersistentDataType.STRING,
							player.getUniqueId().toString());

					event.getItem().setItemMeta(meta);
					player.sendMessage(ChatColor.GREEN + "이 탈것의 소유자로 등록되었습니다");
				}
			}

		}
	}

	@EventHandler
	public void onEntityMount(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		Entity vehicle = event.getRightClicked();

		if (!player.isInsideVehicle()) {
			NamespacedKey rideKey = new NamespacedKey(plugin, "ride");
			if (vehicle.getPersistentDataContainer().has(rideKey)) {
				ActiveMob vehicleMob = MythicBukkit.inst().getMobManager().getMythicMobInstance(vehicle);
				vehicle.addPassenger(player);
				vehicleMob.setStance("mount");
				System.out.println(vehicleMob.getStance());
			}
		}
	}
	
	@EventHandler
	public void onEntityDismount(EntityDismountEvent event) {
		if (event.getEntity() instanceof Player player) {
			Entity vehicle = event.getDismounted();
			NamespacedKey rideKey = new NamespacedKey(plugin, "ride");
			if (vehicle.getPersistentDataContainer().has(rideKey)) {
				ActiveMob vehicleMob = MythicBukkit.inst().getMobManager().getMythicMobInstance(vehicle);
				vehicle.removePassenger(player);
				vehicleMob.setStance("dismount");
				System.out.println(vehicleMob.getStance());
			}
		}
	}

	@EventHandler
	public void onPlayerAttck(PlayerInteractEvent event) {
		if (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
			Player player = event.getPlayer();
			Entity vehicle = player.getVehicle();
			if (player.isInsideVehicle() && vehicle != null) {
				NamespacedKey rideKey = new NamespacedKey(plugin, "ride");
				if (vehicle.getPersistentDataContainer().has(rideKey)) {
					RideController.playAttackAnimation(player, vehicle);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerAttack(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
			Entity vehicle = player.getVehicle();
			if (player.isInsideVehicle() && vehicle != null) {
				NamespacedKey rideKey = new NamespacedKey(plugin, "ride");
				if (vehicle.getPersistentDataContainer().has(rideKey)) {
					RideController.playAttackAnimation(player, vehicle);
				}
			}
		}
	}

	private boolean hasCooldown(Player player) {
		if (cooldowns.get(player.getUniqueId()) < (System.currentTimeMillis() - cool * 1000)) {
			return true;

		} else {
			return false;
		}
	}

	public void activateCooldown(Player player) {
		cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
	}
}
