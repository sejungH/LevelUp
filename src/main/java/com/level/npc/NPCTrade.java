package com.level.npc;

public class NPCTrade {
	
	private NPCTradeItem item1;
	private NPCTradeItem item2;
	private NPCTradeItem result;
	
	public NPCTrade(NPCTradeItem item1, NPCTradeItem item2, NPCTradeItem result) {
		this.item1 = item1;
		this.item2 = item2;
		this.result = result;
	}
	
	public NPCTrade(NPCTradeItem item1, NPCTradeItem result) {
		this.item1 = item1;
		this.item2 = null;
		this.result = result;
	}

	public NPCTradeItem getItem1() {
		return item1;
	}

	public void setItem1(NPCTradeItem item1) {
		this.item1 = item1;
	}

	public NPCTradeItem getItem2() {
		return item2;
	}

	public void setItem2(NPCTradeItem item2) {
		this.item2 = item2;
	}

	public NPCTradeItem getResult() {
		return result;
	}

	public void setResult(NPCTradeItem result) {
		this.result = result;
	}

	@Override
	public String toString() {
		if (item2 == null) {
			return "{'item1': " + item1.toString() + ", 'item2': null, 'result': " + result.toString() + "}";
			
		} else {
			return "{'item1': " + item1.toString() + ", 'item2': " + item2.toString() + ", 'result': " + result.toString() + "}";
		}
	}
	
}
