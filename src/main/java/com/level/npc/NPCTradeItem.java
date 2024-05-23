package com.level.npc;

import org.bukkit.inventory.ItemStack;

import dev.lone.itemsadder.api.CustomStack;

public class NPCTradeItem {

	private String namespacedID;
	private String material;
	private int count;

	public NPCTradeItem(ItemStack item) {
		CustomStack customStack = CustomStack.byItemStack(item);

		if (customStack == null) {
			this.namespacedID = null;
			this.material = item.getType().toString();
			this.count = item.getAmount();

		} else {
			this.namespacedID = customStack.getNamespacedID();
			this.material = null;
			this.count = item.getAmount();
		}
	}

	public NPCTradeItem(String namespacedID, String material, int count) {
		this.namespacedID = namespacedID;
		this.material = material;
		this.count = count;
	}

	public String getNamespacedID() {
		return namespacedID;
	}

	public void setNamespacedID(String namespacedID) {
		this.namespacedID = namespacedID;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public String toString() {
		
		if (namespacedID == null) {
			return "{'namespacedID': null, 'material': '" + material + "', count: " + count + "}";
			
		} else {
			return "{'namespacedID': '" + namespacedID + "', 'material': null, count: " + count + "}";
		}
	}
}
