package com.levelup.menu;


import dev.lone.itemsadder.api.CustomStack;

public enum MenuIcon {
	
	BLANK("blank"),

	ZERO("zero"),
	ONE("one"),
	TWO("two"),
	THREE("three"),
	FOUR("four"),
	FIVE("five"),
	SIX("six"),
	SEVEN("seven"),
	EIGHT("eight"),
	NINE("nine"),
	
	PREV("prev"),
	NEXT("next"),
	
	PICKAXE("pickaxe"),
	AXE("axe"),
	SWORD("sword"),
	SHOVEL("shovel");
	
	public final CustomStack instance;
	
	MenuIcon(String id) {
		this.instance = CustomStack.getInstance("customitems:" + id);
	}
	
	public CustomStack val() {
		return this.instance;
	}
	
}
