package com.levelup.menu;

public enum MenuUnicode {
	
	SPACE('\uEAAA'), 
	
	MENU('\uEABA'),
	
	TOOL_HOME('\uEACA'),
	TOOL_PICKAXE_STAT('\uEACB'),
	TOOL_AXE_STAT('\uEACC'),
	TOOL_SWORD_STAT('\uEACD'),
	TOOL_SHOVEL_STAT('\uEACE'),
	
	BANK_HOME('\uEADA'),
	BANK_DEPOSIT('\uEADB'),
	BANK_WITHDRAW('\uEADC'),
	BANK_TAX('\uEADD'),
	
	VILLAGE_HOME('\uEAEA'),
	VILLAGE_MANAGE('\uEAEB'),
	VILLAGE_INFO('\uEAEC'),
	
	CALENDAR_HOME('\uEAFA'),
	CALENDAR_SEASONPASS_1('\uEAFB'),
	CALENDAR_SEASONPASS_2('\uEAFC'),
	CALENDAR_SEASONPASS_3('\uEAFD'),
	CALENDAR_SEASONPASS_4('\uEAFE'),
	
	FRIEND_HOME('\uEBAA'),
	FRIEND_REQUEST('\uEBAB'),
	FRIEND_LIST('\uEBAC'),
	FRIEND_WAITING('\uEBAD'),
	FRIEND_BLOCK('\uEBAE'),
	
	WARP_HOME('\uEBBA'),
	WARP_VILLAGE('\uEBBB'),
	
	SHOPPING_HOME('\uEBCA'),
	
	GUIDE_HOME('\uEBDA');
	
	public final char code;

	MenuUnicode(char code) {
		this.code = code;
	}
	
	public char val() {
		return this.code;
	}
}
