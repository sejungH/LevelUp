package com.level.npc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NPCData {

	private UUID uuid;
	private String id;
	private String displayName;
	private List<NPCTradeItem> trades;

	public NPCData(UUID uuid, String id, String displayName) {
		this.uuid = uuid;
		this.id = id;
		this.displayName = displayName;
		this.trades = new ArrayList<NPCTradeItem>();
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

	public List<NPCTradeItem> getTrades() {
		return trades;
	}

	public void setTrades(List<NPCTradeItem> trades) {
		this.trades = trades;
	}

}
