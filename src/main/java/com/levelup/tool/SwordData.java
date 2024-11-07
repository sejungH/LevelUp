package com.levelup.tool;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import com.levelup.LevelUp;

public class SwordData extends ToolAbstract {

	public SwordData(LevelUp plugin, UUID uuid, Material material) {
		super(plugin, uuid, material);
	}

	public SwordData(LevelUp plugin, UUID uuid, String name, Material material, int level, int exp,
			Map<Enchantment, Integer> enchantment, String customskin) {
		super(plugin, uuid, name, material, level, exp, enchantment, customskin);
	}

	@Override
	public int getEnchantLimit(Enchantment enchant) {
		if (enchant == Enchantment.DAMAGE_ALL) {
			if (this.getMaterial() == Material.IRON_SWORD)
				return 3;
			else if (this.getMaterial() == Material.NETHERITE_SWORD)
				return 6;
			else
				return 5;

		} else if (enchant == Enchantment.DAMAGE_UNDEAD) {
			if (this.getMaterial() == Material.IRON_SWORD)
				return 3;
			else if (this.getMaterial() == Material.NETHERITE_SWORD)
				return 6;
			else
				return 5;

		} else if (enchant == Enchantment.DAMAGE_ARTHROPODS) {
			if (this.getMaterial() == Material.IRON_SWORD)
				return 3;
			else if (this.getMaterial() == Material.NETHERITE_SWORD)
				return 6;
			else
				return 5;

		} else if (enchant == Enchantment.LOOT_BONUS_MOBS) {
			if (this.getMaterial() == Material.IRON_SWORD)
				return 2;
			else
				return 3;

		} else if (enchant == Enchantment.FIRE_ASPECT) {
			return 2;

		} else if (enchant == Enchantment.KNOCKBACK) {
			return 2;

		} else if (enchant == Enchantment.SWEEPING_EDGE) {
			return 3;

		}

		return 0;
	}

	@Override
	public int getMainStat() {
		if (this.getMaterial() == Material.IRON_SWORD)
			return this.getLevel();

		else if (this.getMaterial() == Material.DIAMOND_SWORD)
			return this.getLevel() + 4;

		else if (this.getMaterial() == Material.NETHERITE_SWORD)
			return this.getLevel() + 5;

		return 0;
	}

	@Override
	public int getSubStat() {
		if (this.getMaterial() == Material.IRON_SWORD) {
			if (this.getLevel() > 0)
				return this.getLevel() * 2 - 1;

		} else if (this.getMaterial() == Material.DIAMOND_SWORD) {
			if (this.getLevel() == 0)
				return 5;
			else if (this.getLevel() == 1)
				return 7;
			else if (this.getLevel() == 2)
				return 10;

		} else if (this.getMaterial() == Material.NETHERITE_SWORD) {
			return 10;

		}

		return 0;
	}

	@Override
	public ToolType getType() {
		return ToolType.SWORD;
	}

}
