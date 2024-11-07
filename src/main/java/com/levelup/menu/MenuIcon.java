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
	
	VILLAGE("village"),
	FROG("frog"),
	CHEST("chest"),
	
	PREV("prev"),
	NEXT("next"),
	
	PICKAXE("pickaxe"),
	AXE("axe"),
	SWORD("sword"),
	SHOVEL("shovel");
	
	public final CustomStack instance;
	
	MenuIcon(String id) {
		instance = CustomStack.getInstance("customitems:" + id);
	}
	
	public CustomStack val() {
		return instance;
	}
	
	public String namespacedID() {
		return instance.getNamespacedID();
	}
	
}
