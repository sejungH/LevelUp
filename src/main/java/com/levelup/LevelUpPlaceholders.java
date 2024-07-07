package com.levelup;

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;

import com.levelup.player.PlayerData;
import com.levelup.village.VillageData;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class LevelUpPlaceholders extends PlaceholderExpansion {

	private LevelUp plugin;
	
	public LevelUpPlaceholders(LevelUp plugin) {
		this.plugin = plugin;
	}

	@Override
	public String getIdentifier() {
		PluginDescriptionFile pdFile = plugin.getDescription();
		return pdFile.getName().toLowerCase();
	}

	@Override
	public String getAuthor() {
		PluginDescriptionFile pdFile = plugin.getDescription();
		return pdFile.getAuthors().get(0);
	}

	@Override
	public String getVersion() {
		PluginDescriptionFile pdFile = plugin.getDescription();
		return pdFile.getVersion();
	}
	
	@Override
	public String onRequest(OfflinePlayer player, @NotNull String params) {
		if (params.equalsIgnoreCase("nickname")) {
			PlayerData pd = plugin.players.get(player.getUniqueId());
			if (pd.getNickname() != null)
				return pd.getNickname();
			else
				return pd.getUsername();
			
		} else if (params.equalsIgnoreCase("balance")) {
			PlayerData pd = plugin.players.get(player.getUniqueId());
			return String.valueOf(pd.getBalance());
			
		} else if (params.equalsIgnoreCase("village_name")) {
			PlayerData pd = plugin.players.get(player.getUniqueId());
			if (pd.getVillage() > 0) {
				VillageData vd = plugin.villages.get(pd.getVillage());
				String color;
				if ((color = vd.getColor()) != null) {
					return "<" + color + ">" + vd.getName() + "</" + color + ">";
				} else {
					return vd.getName();
				}
			} else {
				return "";
			}
		}
		
		return null;
	}
	
}
