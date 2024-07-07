package com.levelup;

public enum LevelUpIcon {
	
	FROG('\uECAA'),
	VILLAGE('\uECAB'),
	COIN('\uECAC'),
	CHAT('\uECAD'),
	
	SPRING('\uECAE'),
	SUMMER('\uECAF'),
	AUTUMN('\uECBA'),
	WINTER('\uECBB'),
	
	CLOCK('\uECBC'),
	
	MAIL('\uECBD'),
	
	ADMIN('\uECBE');
	
	public final char code;
	
	LevelUpIcon(char code) {
		this.code = code;
	}
	
	public char val() {
		return this.code;
	}

}
