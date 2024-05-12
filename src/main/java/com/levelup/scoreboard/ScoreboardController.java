package com.levelup.scoreboard;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.levelup.chat.ChatType;
import com.levelup.main.LevelUp;
import com.levelup.money.MoneyController;
import com.levelup.player.PlayerData;
import com.levelup.village.VillageData;

public class ScoreboardController {

	public static void newScoreboard(LevelUp plugin, Player player) {
		ScoreboardManager sbm = plugin.getServer().getScoreboardManager();
		Scoreboard scoreboard = sbm.getNewScoreboard();

		Objective objective = scoreboard.registerNewObjective(player.getName(), Criteria.DUMMY, "§f⛏️ §6LEVEL §9UP §f🪓");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);

		// 공백
        Score space1 = objective.getScore("");
        space1.setScore(4);
        
        PlayerData pd = plugin.players.get(player.getUniqueId());
        VillageData vd = plugin.villages.get(pd.getVillage());
        
        // 마을
        Score village;
        if (vd == null) {
        	village = objective.getScore("§e마을: §f없음");
        } else {
        	village = objective.getScore("§e마을: §f" + vd.getName());
        }
        village.setScore(3);
        
        // 잔고
        Score balance = objective.getScore("§e잔고: §f" + MoneyController.withLargeIntegers(pd.getBalance()));
        balance.setScore(2);
        
        // 채팅
        Score chat;
        if (pd.getChatType() == ChatType.DEFAULT) {
        	chat = objective.getScore("§e채팅: §f전체채팅");
        	
        } else {
        	chat = objective.getScore("§e채팅: §f마을채팅");
        } 
        
        chat.setScore(1);
        
        // 공백
        Score space2 = objective.getScore("                           ");
        space2.setScore(0);
		
		player.setScoreboard(scoreboard);
	}
	
	public static void updateScoreboard(LevelUp plugin, Player player) {
		Scoreboard scoreboard = player.getScoreboard();
		scoreboard.clearSlot(DisplaySlot.SIDEBAR);
		
		scoreboard = plugin.getServer().getScoreboardManager().getNewScoreboard();
		Objective objective = scoreboard.registerNewObjective(player.getName(), Criteria.DUMMY, "§f⛏️ §6LEVEL §9UP §f🪓");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);

		// 공백
        Score space1 = objective.getScore("");
        space1.setScore(4);
        
        PlayerData pd = plugin.players.get(player.getUniqueId());
        VillageData vd = plugin.villages.get(pd.getVillage());
        
        // 마을
        Score village;
        if (vd == null) {
        	village = objective.getScore("\n§e마을: §f없음");
        } else {
        	village = objective.getScore("§e마을: §f" + vd.getName());
        }
        village.setScore(3);
        
        // 잔고
        Score balance = objective.getScore("§e잔고: §f" + MoneyController.withLargeIntegers(pd.getBalance()));
        balance.setScore(2);
        
        // 채팅
        Score chat;
        if (pd.getChatType() == ChatType.DEFAULT) {
        	chat = objective.getScore("§e채팅: §f전체채팅");
        	
        } else {
        	chat = objective.getScore("§e채팅: §f마을채팅");
        } 
        
        chat.setScore(1);
        
        // 공백
        Score space2 = objective.getScore("                           ");
        space2.setScore(0);
		
		player.setScoreboard(scoreboard);
		
	}

}
