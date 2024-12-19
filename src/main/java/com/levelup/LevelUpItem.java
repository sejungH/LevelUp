package com.levelup;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;

import com.google.gson.JsonObject;

import dev.lone.itemsadder.api.CustomStack;
import net.momirealms.customfishing.api.BukkitCustomFishingPlugin;
import net.momirealms.customfishing.api.mechanic.context.Context;

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
		String customFishing = BukkitCustomFishingPlugin.getInstance().getItemManager().getCustomFishingItemID(item);
		
		if (customFishing == null) {
			CustomStack custom = CustomStack.byItemStack(item);

			if (custom == null) {
				this.material = item.getType().toString();
				this.namespacedID = null;

			} else {
				this.material = null;
				this.namespacedID = custom.getNamespacedID();
			}
			
		} else {
			this.material = null;
			this.namespacedID = "customfishing:" + customFishing;
		}

		this.amount = item.getAmount();
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
			String[] parts = namespacedID.split(":");
			if (parts[0].equalsIgnoreCase("customfishing")) {
				try {
					Context<Player> context = Context.player(null);
					item = BukkitCustomFishingPlugin.getInstance().getItemManager().buildInternal(context, parts[1]);

				} catch (NullPointerException e) {
					return null;
				}
			} else {
				item = CustomStack.getInstance(namespacedID).getItemStack();
			}
		}

		item.setAmount(amount);

		return item;
	}

	public boolean equals(ItemStack item) {
		if (CustomStack.byItemStack(item) == null) {
			if (material == null) {
				return false;
			} else {
				return item.getType() == Material.getMaterial(material.toUpperCase());
			}
		} else {
			return CustomStack.byItemStack(item).getNamespacedID().equals(namespacedID);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((material == null) ? 0 : material.hashCode());
		result = prime * result + ((namespacedID == null) ? 0 : namespacedID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LevelUpItem other = (LevelUpItem) obj;
		if (material == null) {
			if (other.material != null)
				return false;
		} else if (!material.equals(other.material))
			return false;
		if (namespacedID == null) {
			if (other.namespacedID != null)
				return false;
		} else if (!namespacedID.equals(other.namespacedID))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "LevelUpItem [material=" + material + ", namespacedID=" + namespacedID + ", amount=" + amount + "]";
	}
}
