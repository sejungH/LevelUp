package com.levelup.main;

import java.util.logging.Level;

import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class LevelUp extends JavaPlugin implements Listener {

	@Override
	public void onEnable() {
		super.onEnable();
		
		initCommand();
		initEvents();
		initConfig();
		
		PluginDescriptionFile pdFile = this.getDescription();
		this.getLogger().log(Level.INFO, pdFile.getName() + " version " + pdFile.getVersion() + " is enabled");
	}
	
	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		PluginDescriptionFile pdFile = this.getDescription();
		this.getLogger().log(Level.INFO, pdFile.getName() + " version " + pdFile.getVersion() + " is disabled");
	}
	
	public void initCommand() {
		
	}
	
	public void initEvents() {
		
	}
	
	public void initConfig() {
		
	}

}
