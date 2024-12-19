package com.levelup.ride;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import com.levelup.LevelUp;
import com.levelup.player.PlayerData;

import dev.lone.itemsadder.api.CustomStack;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillTrigger;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.adapters.BukkitPlayer;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import net.md_5.bungee.api.ChatColor;

public class RideController {

	public static List<String> getRides() throws SQLException {
		List<String> rides = new ArrayList<String>();

		for (String id : CustomStack.getNamespacedIdsInRegistry()) {
			if (id.contains("_key")) {
				rides.add(id.substring(id.indexOf(":") + 1, id.indexOf("_key")));
			}
		}

		return rides;
	}

	public static CustomStack getKey(LevelUp plugin, UUID uuid, String id) {
		PlayerData pd = plugin.players.get(uuid);

		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GRAY + "소유자: " + pd.getUsername());

		CustomStack key = CustomStack.getInstance("customitems:" + id.toLowerCase() + "_key");
		NamespacedKey namespacedKey = new NamespacedKey(plugin, "owner");
		ItemMeta meta = key.getItemStack().getItemMeta();
		meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, uuid.toString());
		meta.setLore(lore);

		key.getItemStack().setItemMeta(meta);

		return key;
	}

	public static void playAttackAnimation(Player player, Entity entity) {
		ActiveMob vehicleMob = MythicBukkit.inst().getMobManager().getMythicMobInstance(entity);
		Skill skill = MythicBukkit.inst().getSkillManager().getSkill("attack").orElse(null);
		if (skill != null) {
			SkillMetadata sm = new SkillMetadataImpl(SkillTrigger.get("API"), vehicleMob, new BukkitPlayer(player));
			skill.execute(sm);
		}
	}

	public static void removeAllVehicles() {
		for (ActiveMob mob : MythicBukkit.inst().getMobManager().getActiveMobs()) {
			if (mob.getType().getInternalName().contains("ride_")) {
				if (mob.getEntity().getPassengers().isEmpty() && mob.getStance().equals("dismount")) {
					mob.remove();
				}
			}
		}
	}

}
