package com.levelup.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import com.levelup.LevelUp;
import com.levelup.LevelUpIcon;
import com.levelup.player.PlayerData;

import dev.lone.itemsadder.api.CustomStack;
import net.md_5.bungee.api.ChatColor;

public abstract class ToolAbstract {

	private LevelUp plugin;
	private UUID uuid;
	private String name;
	private Material material;
	private int level;
	private int exp;
	private Map<Enchantment, Integer> enchantment;
	private String customskin;

	public ToolAbstract(LevelUp plugin, UUID uuid, String name, Material material, int level, int exp,
			Map<Enchantment, Integer> enchantment, String customskin) {
		this.plugin = plugin;
		this.uuid = uuid;
		this.name = name;
		this.material = material;
		this.level = level;
		this.exp = exp;
		this.enchantment = enchantment;
		this.customskin = customskin;
	}

	public ToolAbstract(LevelUp plugin, UUID uuid, Material material) {
		this.plugin = plugin;
		this.uuid = uuid;
		this.name = null;
		this.material = material;
		this.level = 0;
		this.exp = 0;
		this.enchantment = new HashMap<Enchantment, Integer>();
		this.customskin = null;
	}

	public LevelUp getPlugin() {
		return plugin;
	}

	public void setPlugin(LevelUp plugin) {
		this.plugin = plugin;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public void addExp(int exp) {
		this.exp += exp;
	}

	public Map<Enchantment, Integer> getEnchantment() {
		return enchantment;
	}

	public void setEnchantment(Map<Enchantment, Integer> enchantment) {
		this.enchantment = enchantment;
	}

	public int getEnchantLevel(Enchantment enchant) {
		if (this.enchantment.containsKey(enchant))
			return this.enchantment.get(enchant);

		return 0;
	}
	
	public abstract int getEnchantLimit(Enchantment enchant);

	public String getEnchantmentJSON() {
		String json = "[";
		for (Enchantment e : this.enchantment.keySet()) {
			json += "{'type': '" + e.getKey().toString() + "', 'level': " + this.enchantment.get(e) + "}, ";
		}

		if (this.enchantment.size() > 0) {
			json = json.substring(0, json.length() - 2);
		}
		json += "]";

		return json;
	}

	public String getCustomskin() {
		return customskin;
	}

	public void setCustomstkin(String customskin) {
		this.customskin = customskin;
	}

	public ItemStack getAsItemStack() {
		ItemStack tool;

		if (customskin == null) {
			tool = new ItemStack(this.material);

		} else {
			CustomStack customStack = CustomStack.getInstance(this.customskin);
			if (customStack != null) {
				tool = customStack.getItemStack().clone();

			} else {
				tool = new ItemStack(this.material);
			}
		}

		ItemMeta toolMeta = tool.getItemMeta();

		// set owner uuid
		NamespacedKey ownerKey = new NamespacedKey(plugin, "owner");
		toolMeta.getPersistentDataContainer().set(ownerKey, PersistentDataType.STRING, uuid.toString());

		// set display name
		if (this.name != null)
			toolMeta.setDisplayName(this.name);

		// set enchantment
		if (!this.enchantment.isEmpty()) {
			for (Enchantment e : this.enchantment.keySet()) {
				toolMeta.addEnchant(e, this.enchantment.get(e), true);
			}
		}

		toolMeta.setUnbreakable(true);

		PlayerData pd = plugin.players.get(uuid);
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GRAY + "소유자: " + pd.getUsername());
		toolMeta.setLore(lore);

		// apply item meta
		tool.setItemMeta(toolMeta);

		return tool;
	}

	public boolean equals(ItemStack item) {
		if (item == null)
			return false;

		CustomStack custom = CustomStack.byItemStack(item);
		if (customskin == null) {
			if (custom != null)
				return false;

		} else if (custom != null && !customskin.equals(custom.getNamespacedID()))
			return false;

		if (enchantment == null || enchantment.isEmpty()) {
			if (!(item.getEnchantments() == null || item.getEnchantments().isEmpty()))
				return false;

		} else if (!enchantment.equals(item.getEnchantments()))
			return false;

		if (!material.equals(item.getType()))
			return false;

		if (name == null) {
			if (item.getItemMeta().hasDisplayName())
				return false;

		} else if (!name.equalsIgnoreCase(item.getItemMeta().getDisplayName()))
			return false;

		NamespacedKey ownerKey = new NamespacedKey(plugin, "owner");
		if (!item.getItemMeta().getPersistentDataContainer().has(ownerKey))
			return false;

		if (uuid == null) {
			if (item.getItemMeta().getPersistentDataContainer().get(ownerKey, PersistentDataType.STRING) != null)
				return false;

		} else if (!uuid.equals(UUID
				.fromString(item.getItemMeta().getPersistentDataContainer().get(ownerKey, PersistentDataType.STRING))))
			return false;

		return true;
	}

	public LevelUpIcon getIcon() {
		if (customskin == null)
			return null;

		String regex = "(\\w+):(\\w+)_(wooden|stone|iron|diamond|netherite)_(pickaxe|axe|sword|shovel)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(customskin);

		if (!matcher.matches())
			return null;

		String prefix = matcher.group(2);

		return LevelUpIcon.valueOf(prefix.toUpperCase());
	}

	public abstract int getMainStat();

	public abstract int getSubStat();

	public int getTotalStat() {
		int total = getMainStat() + getSubStat();
		
		for (Entry<Enchantment, Integer> enchant : this.enchantment.entrySet()) {
			total -= enchant.getValue();
		}
		
		return total;
	}

	@Override
	public String toString() {
		return "ToolAbstract [plugin=" + plugin + ", uuid=" + uuid + ", name=" + name + ", material=" + material
				+ ", level=" + level + ", exp=" + exp + ", enchantment=" + this.getEnchantmentJSON() + ", customskin="
				+ customskin + "]";
	}

}
