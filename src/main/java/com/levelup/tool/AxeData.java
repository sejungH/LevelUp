package com.levelup.tool;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import com.levelup.LevelUp;

public class AxeData extends ToolAbstract {

	public AxeData(LevelUp plugin, UUID uuid, Material material) {
		super(plugin, uuid, material);
	}

	public AxeData(LevelUp plugin, UUID uuid, String name, Material material, int level, int exp, Map<Enchantment, Integer> enchantment,
			String customskin) {
		super(plugin, uuid, name, material, level, exp, enchantment, customskin);
	}

	@Override
	public int getEnchantLimit(Enchantment enchant) {
		if (enchant == Enchantment.DIG_SPEED) {
			if (this.getMaterial() == Material.NETHERITE_AXE)
				return 6;
			else
				return 5;
			
		} else if (enchant == Enchantment.LOOT_BONUS_BLOCKS) {
			if (this.getMaterial() == Material.IRON_AXE)
				return 2;
			else
				return 3;
			
		} else if (enchant == Enchantment.SILK_TOUCH) {
			return 1;
			
		} else if (enchant == Enchantment.DAMAGE_ALL) {
			if (this.getMaterial() == Material.IRON_AXE)
				return 3;
			else
				return 5;
			
		} else if (enchant == Enchantment.DAMAGE_UNDEAD) {
			if (this.getMaterial() == Material.IRON_AXE)
				return 3;
			else
				return 5;
			
		} else if (enchant == Enchantment.DAMAGE_ARTHROPODS) {
			if (this.getMaterial() == Material.IRON_AXE)
				return 3;
			else
				return 5;
			
		}
		
		return 0;
	}
	
	@Override
	public int getMainStat() {
		if (this.getMaterial() == Material.IRON_AXE)
			return this.getLevel();
			
		else if (this.getMaterial() == Material.DIAMOND_AXE)
			return this.getLevel() + 4;
			
		else if (this.getMaterial() == Material.NETHERITE_AXE)
			return this.getLevel() + 8;
		
		return 0;
	}

	@Override
	public int getSubStat() {
		if (this.getMaterial() == Material.IRON_AXE) {
			if (this.getLevel() > 1) 
				return this.getLevel() - 1;
			
		} else if (this.getMaterial() == Material.DIAMOND_AXE) {
			if (this.getLevel() == 0)
				return 3;
			else if (this.getLevel() == 1)
				return 4;
			else
				return 5;
			
		} else if (this.getMaterial() == Material.NETHERITE_AXE) {
			return 5;
			
		}
		
		return 0;
	}

}
