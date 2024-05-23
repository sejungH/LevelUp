package com.levelup.tool;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

public class PickaxeData extends ToolAbstract {
	
	public PickaxeData(UUID uuid, String name, Material material, Map<Enchantment, Integer> enchantment,
			String customskin) {
		super(uuid, name, material, enchantment, customskin);
	}

	public PickaxeData(UUID uuid, Material material) {
		super(uuid, material);
	}
	
}
