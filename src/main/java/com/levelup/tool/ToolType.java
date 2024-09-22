package com.levelup.tool;

import org.bukkit.Material;

public enum ToolType {
	PICKAXE, AXE, SWORD, SHOVEL;

	public static ToolType get(String value) {
		if (value.equalsIgnoreCase("pickaxe")) {
			return PICKAXE;
			
		} else if (value.equalsIgnoreCase("axe")) {
			return AXE;
			
		} else if (value.equalsIgnoreCase("sword")) {
			return SWORD;
			
		} else if (value.equalsIgnoreCase("shovel")) {
			return SHOVEL;
			
		} else {
			return null;
		}
	}

	public static ToolType get(Material material) {

		if (material.equals(Material.WOODEN_PICKAXE) || material.equals(Material.STONE_PICKAXE)
				|| material.equals(Material.IRON_PICKAXE) || material.equals(Material.DIAMOND_PICKAXE)
				|| material.equals(Material.NETHERITE_PICKAXE))
			return PICKAXE;

		else if (material.equals(Material.WOODEN_AXE) || material.equals(Material.STONE_AXE)
				|| material.equals(Material.IRON_AXE) || material.equals(Material.DIAMOND_AXE)
				|| material.equals(Material.NETHERITE_AXE))
			return AXE;

		else if (material.equals(Material.WOODEN_SWORD) || material.equals(Material.STONE_SWORD)
				|| material.equals(Material.IRON_SWORD) || material.equals(Material.DIAMOND_SWORD)
				|| material.equals(Material.NETHERITE_SWORD))
			return SWORD;

		else if (material.equals(Material.WOODEN_SHOVEL) || material.equals(Material.STONE_SHOVEL)
				|| material.equals(Material.IRON_SHOVEL) || material.equals(Material.DIAMOND_SHOVEL)
				|| material.equals(Material.NETHERITE_SHOVEL))
			return SHOVEL;

		return null;
	}

	@Override
	public String toString() {
		switch (this) {
		case PICKAXE:
			return "PICKAXE";
		case AXE:
			return "AXE";
		case SWORD:
			return "SWORD";
		case SHOVEL:
			return "SHOVEL";
		default:
			return null;
		}
	}

	public String valueKor() {
		switch (this) {
		case PICKAXE:
			return "곡괭이";
		case AXE:
			return "도끼";
		case SWORD:
			return "검";
		case SHOVEL:
			return "삽";
		default:
			return null;
		}
	}
}
