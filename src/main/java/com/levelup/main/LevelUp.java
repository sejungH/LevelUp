package com.levelup.main;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.level.npc.NPCController;
import com.level.npc.NPCEvent;
import com.level.npc.NPCTrade;
import com.level.ride.RideCommand;
import com.level.ride.RideController;
import com.level.ride.RideEvent;
import com.level.ride.RideTabCompleter;
import com.levelup.chat.ChatEvent;
import com.levelup.db.MySQLConnect;
import com.levelup.friend.FriendCommand;
import com.levelup.friend.FriendController;
import com.levelup.friend.FriendData;
import com.levelup.friend.FriendTabCompleter;
import com.levelup.menu.MenuEvent;
import com.levelup.money.MoneyCommand;
import com.levelup.player.PlayerCommand;
import com.levelup.player.PlayerController;
import com.levelup.player.PlayerData;
import com.levelup.player.PlayerEvent;
import com.levelup.tool.ToolAbstract;
import com.levelup.tool.ToolCommand;
import com.levelup.tool.ToolController;
import com.levelup.tool.ToolData;
import com.levelup.tool.ToolEvent;
import com.levelup.tool.ToolTabCompleter;
import com.levelup.village.VillageCommand;
import com.levelup.village.VillageController;
import com.levelup.village.VillageData;
import com.levelup.village.VillageTabCompleter;

public class LevelUp extends JavaPlugin {
	
	public MySQLConnect mysql;
	
	public Map<UUID, PlayerData> players;
	public Map<Integer, VillageData> villages;
	public List<FriendData> friends;
	public Map<UUID, List<NPCTrade>> npcs;
	public Map<UUID, ToolData> tools;

	@Override
	public void onEnable() {
		super.onEnable();
		PluginDescriptionFile pdFile = this.getDescription();
		this.getLogger().info(pdFile.getName() + " version " + pdFile.getVersion() + " is enabled");

		try {
			initDB();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		initEvents();
		initCommand();
	}

	@Override
	public void onDisable() {
		super.onDisable();
		mysql.closeConnection();
		PluginDescriptionFile pdFile = this.getDescription();
		this.getLogger().info(pdFile.getName() + " version " + pdFile.getVersion() + " is disabled");
	}

	public void initCommand() {
		getCommand("levelup").setTabCompleter(new LevelUpTabCompleter(this));
		getCommand("levelup").setExecutor(new LevelUpCommand(this));
		
		getCommand("마을").setTabCompleter(new VillageTabCompleter(this));
		getCommand("마을").setExecutor(new VillageCommand(this));

		getCommand("친구").setTabCompleter(new FriendTabCompleter(this));
		getCommand("친구").setExecutor(new FriendCommand(this));
		
		getCommand("입금").setExecutor(new MoneyCommand(this));
		getCommand("출금").setExecutor(new MoneyCommand(this));
		
		getCommand("전체채팅").setExecutor(new PlayerCommand(this));
		getCommand("마을채팅").setExecutor(new PlayerCommand(this));
		getCommand("c").setExecutor(new PlayerCommand(this));
		getCommand("ㅊ").setExecutor(new PlayerCommand(this));
		getCommand("hat").setExecutor(new PlayerCommand(this));
		
		getCommand("ride").setTabCompleter(new RideTabCompleter(this));
		getCommand("ride").setExecutor(new RideCommand(this));
		
		getCommand("t").setTabCompleter(new ToolTabCompleter(this));
		getCommand("t").setExecutor(new ToolCommand(this));
	}

	public void initEvents() {
		PluginManager pm = getServer().getPluginManager();
		
		pm.registerEvents(new PlayerEvent(this), this);
		pm.registerEvents(new ChatEvent(this), this);
		pm.registerEvents(new MenuEvent(this), this);
		pm.registerEvents(new NPCEvent(this), this);
		pm.registerEvents(new RideEvent(this), this);
		pm.registerEvents(new ToolEvent(this), this);
	}
	
	public void initDB() throws SQLException {
		mysql = new MySQLConnect(this);
		mysql.openConnection();
		
		players = PlayerController.getPlayers(this);
		villages = VillageController.getVillages(this);
		friends = FriendController.getFriends(this);
		npcs = NPCController.getNPCs(this);
		tools = ToolController.getTools(this);
	}
}
