package com.levelup;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;

import com.google.gson.JsonObject;

import dev.lone.itemsadder.api.CustomStack;

public class LevelUpItem {
	private String material;
	private String namespacedID;
	private int amount;

	public LevelUpItem(String material, String namespacedID, int amount) {
		this.material = material;
		this.namespacedID = namespacedID;
		this.amount = amount;
	}

	public LevelUpItem(@NonNull ItemStack item) {
		CustomStack custom = CustomStack.byItemStack(item);

		if (custom == null) {
			this.material = item.getType().toString();
			this.namespacedID = null;
			this.amount = item.getAmount();

		} else {
			this.material = null;
			this.namespacedID = custom.getNamespacedID();
			this.amount = item.getAmount();
		}
	}
	
	public LevelUpItem(@NonNull JsonObject object) {
		if (object.has("material")) {
			this.material = object.get("material").getAsString();
			this.namespacedID = null;
			this.amount = object.get("amount").getAsInt();
			
		} else {
			this.material = null;
			this.namespacedID = object.get("namespacedID").getAsString();
			this.amount = object.get("amount").getAsInt();
		}
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public String getNamespacedID() {
		return namespacedID;
	}

	public void setNamespacedID(String namespacedID) {
		this.namespacedID = namespacedID;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public JsonObject createItemJson() {
		JsonObject itemObject = new JsonObject();
		if (material != null) {
			itemObject.addProperty("material", material);
		} else {
			itemObject.addProperty("namespacedID", namespacedID);
		}
		itemObject.addProperty("amount", amount);

		return itemObject;
	}

	public ItemStack getItemStack() {
		ItemStack item;
		
		if (material != null) {
			item = new ItemStack(Material.getMaterial(material.toUpperCase()));
		} else {
			item = CustomStack.getInstance(namespacedID).getItemStack();
		}
		
		item.setAmount(amount);
		
		return item;
	}
}
