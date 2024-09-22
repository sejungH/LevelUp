package com.levelup.menu;

public enum MenuUnicode {
	
	SPACE('\uEAA0'), 
	
	MENU('\uEAB0'),
	
	TOOL_HOME('\uEAC0'),
	TOOL_STAT_1('\uEAC1'),
	TOOL_STAT_2('\uEAC2'),
	TOOL_STAT_3('\uEAC3'),
	SKIN_TICKET('\uEAC4'),
	TOOLBOX('\uEAC5'),
	
	BANK_HOME('\uEAD0'),
	BANK_DEPOSIT('\uEAD1'),
	BANK_WITHDRAW('\uEAD2'),
	BANK_TAX('\uEAD3'),
	
	VILLAGE_HOME('\uEAE0'),
	VILLAGE_MANAGE('\uEAE1'),
	VILLAGE_INFO('\uEAE2'),
	
	CALENDAR_HOME('\uEAF0'),
	CALENDAR_SEASONPASS_1('\uEAF1'),
	CALENDAR_SEASONPASS_2('\uEAF2'),
	CALENDAR_SEASONPASS_3('\uEAF3'),
	CALENDAR_SEASONPASS_4('\uEAF4'),
	CALENDAR_DAILY_QUEST('\uEAF5'),
	
	FRIEND_HOME('\uEBA0'),
	FRIEND_REQUEST('\uEBA1'),
	FRIEND_LIST('\uEBA2'),
	FRIEND_WAITING('\uEBA3'),
	FRIEND_BLOCK('\uEBA4'),
	
	WARP_HOME('\uEBB0'),
	WARP_VILLAGE('\uEBB1'),
	
	SHOPPING_HOME('\uEBC0'),
	
	GUIDE_HOME('\uEBD0'),
	
	NPC_1('\uEBE0'),
	NPC_2('\uEBE1'),
	NPC_3('\uEBE2'),
	TEXTBOX('\uEBE3'),
	
	COOKING_POT('\uEBF0');
	
	public final char code;

	MenuUnicode(char code) {
		this.code = code;
	}
	
	public char val() {
		return this.code;
	}
}
