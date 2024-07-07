package com.levelup.menu;


import dev.lone.itemsadder.api.CustomStack;

public enum MenuIcon {

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
	
	ZERO_WHITE("zero_white"),
	ONE_WHITE("one_white"),
	TWO_WHITE("two_white"),
	THREE_WHITE("three_white"),
	FOUR_WHITE("four_white"),
	FIVE_WHITE("five_white"),
	SIX_WHITE("six_white"),
	SEVEN_WHITE("seven_white"),
	EIGHT_WHITE("eight_white"),
	NINE_WHITE("nine_white"),
	
	PREV("prev"),
	NEXT("next"),;
	
	public final CustomStack instance;
	
	MenuIcon(String id) {
		this.instance = CustomStack.getInstance("customitems:" + id);
	}
	
	public CustomStack val() {
		return this.instance;
	}
	
}
