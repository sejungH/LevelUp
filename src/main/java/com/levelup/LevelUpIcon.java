package com.levelup;

public enum LevelUpIcon {
	
	LOGO('\uECA0'),
	VILLAGE('\uECA1'),
	COIN('\uECA2'),
	CHAT('\uECA3'),
	SPRING('\uECA4'),
	SUMMER('\uECA5'),
	AUTUMN('\uECA6'),
	WINTER('\uECA7'),
	CLOCK('\uECA8'),
	MAIL('\uECA9'),
	ADMIN('\uECAA'),
	WORLD('\uECAB'),

	ONLINE('\uECAC'),
	OFFLINE('\uECAD'),
	
	// 캐시 도구 아이콘
	NEKO('\uECC0'),
	SHARK('\uECC1'),
	RABBIT('\uECC2'),
	BOX('\uECC3'),
	
	// 도구 아이콘
	WOODEN_PICKAXE('\uED50'),
	STONE_PICKAXE('\uED51'),
	IRON_PICKAXE('\uED52'),
	DIAMOND_PICKAXE('\uED53'),
	NETHERITE_PICKAXE('\uED54'),

	WOODEN_AXE('\uED55'),
	STONE_AXE('\uED56'),
	IRON_AXE('\uED57'),
	DIAMOND_AXE('\uED58'),
	NETHERITE_AXE('\uED59'),
	
	WOODEN_SWORD('\uED5A'),
	STONE_SWORD('\uED5B'),
	IRON_SWORD('\uED5C'),
	DIAMOND_SWORD('\uED5D'),
	NETHERITE_SWORD('\uED5E'),
	
	WOODEN_SHOVEL('\uED5F'),
	STONE_SHOVEL('\uED60'),
	IRON_SHOVEL('\uED61'),
	DIAMOND_SHOVEL('\uED62'),
	NETHERITE_SHOVEL('\uED63'),
	
	// 초상화
	PICKAXE_DEFAULT('\uEE00'),
	AXE_DEFAULT('\uEE10'),
	SWORD_DEFAULT('\uEE20'),
	SHOVEL_DEFAULT('\uEE30'),

	BLACKSMITH_DEFAULT('\uEE40'),
	BLACKSMITH_SPEAKING('\uEE41');
	
	public final char code;
	
	LevelUpIcon(char code) {
		this.code = code;
	}
	
	public char val() {
		return this.code;
	}
}
