package com.levelup.npc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.levelup.LevelUpItem;

public class NPCData {

	private UUID uuid;
	private String id;
	private String displayName;
	private List<LevelUpItem> trades;

	public NPCData(UUID uuid, String id, String displayName) {
		this.uuid = uuid;
		this.id = id;
		this.displayName = displayName;
		this.trades = new ArrayList<LevelUpItem>();
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public List<LevelUpItem> getTrades() {
		return trades;
	}

	public void setTrades(List<LevelUpItem> trades) {
		this.trades = trades;
	}

}
