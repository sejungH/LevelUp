package com.levelup;

import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;

import com.levelup.player.PlayerData;

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
		if (params.equalsIgnoreCase("balance")) {
			PlayerData pd = plugin.players.get(player.getUniqueId());
			return String.valueOf(pd.getBalance());
		}
		
		return null;
	}
	
}
