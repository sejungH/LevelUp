package com.levelup.tool;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import com.levelup.LevelUp;

public class PickaxeData extends ToolAbstract {

	public PickaxeData(LevelUp plugin, UUID uuid, Material material) {
		super(plugin, uuid, material);
	}

	public PickaxeData(LevelUp plugin, UUID uuid, String name, Material material, int level, int exp,
			Map<Enchantment, Integer> enchantment, String customskin) {
		super(plugin, uuid, name, material, level, exp, enchantment, customskin);
	}

	@Override
	public int getEnchantLimit(Enchantment enchant) {
		if (enchant == Enchantment.DIG_SPEED) {
			if (this.getMaterial() == Material.NETHERITE_PICKAXE)
				return 6;
			else
				return 5;
			
		} else if (enchant == Enchantment.LOOT_BONUS_BLOCKS) {
			if (this.getMaterial() == Material.IRON_PICKAXE)
				return 2;
			else
				return 3;
			
		} else if (enchant == Enchantment.SILK_TOUCH) {
			return 1;
		}
		
		return 0;
	}

	@Override
	public int getMainStat() {
		if (this.getMaterial() == Material.IRON_PICKAXE)
			return this.getLevel();

		else if (this.getMaterial() == Material.DIAMOND_PICKAXE)
			return this.getLevel() + 4;

		else if (this.getMaterial() == Material.NETHERITE_PICKAXE)
			return this.getLevel() + 8;
		
		return 0;
	}

	@Override
	public int getSubStat() {
		return 0;
	}

	@Override
	public ToolType getType() {
		return ToolType.PICKAXE;
	}

}
