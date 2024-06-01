package com.levelup.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.levelup.LevelUp;
import com.levelup.chat.ChatType;
import com.levelup.money.MoneyController;
import com.levelup.player.PlayerData;
import com.levelup.village.VillageData;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;

import net.momirealms.customcrops.api.CustomCropsPlugin;

public class ScoreboardController {

	public static final char FROG = '\uECAA';
	public static final char VILLAGE = '\uECAB';
	public static final char COIN = '\uECAC';
	public static final char CHAT = '\uECAD';
	public static final char SPRING = '\uECAE';
	public static final char SUMMER = '\uECAF';
	public static final char AUTUMN = '\uECBA';
	public static final char WINTER = '\uECBB';
	public static final char CLOCK = '\uECBC';

	public static void displayScoreboard(LevelUp plugin, Player player) {
		if (player.getScoreboard().getObjective(player.getName()) != null)
			player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
		
		Scoreboard scoreboard = plugin.getServer().getScoreboardManager().getNewScoreboard();
		Objective objective = scoreboard.registerNewObjective(player.getName(), Criteria.DUMMY,
				"§f" + FROG + " §aLEVEL UP! §f" + FROG);
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		int index = 100;
		
		// 공백
		Score space1 = objective.getScore("");
		space1.setScore(index--);

		PlayerData pd = plugin.players.get(player.getUniqueId());
		VillageData vd = plugin.villages.get(pd.getVillage());

		// 마을
		Score village;
		if (vd == null) {
			village = objective.getScore("§f" + VILLAGE + " 없음");
		} else {
			village = objective.getScore("§f" + VILLAGE + " " + vd.getName());
		}
		village.setScore(index--);

		// 잔고
		Score balance = objective.getScore("§f" + COIN + " " + MoneyController.withLargeIntegers(pd.getBalance()));
		balance.setScore(index--);

		// 채팅
		Score chat;
		if (pd.getChatType() == ChatType.DEFAULT) {
			chat = objective.getScore("§f" + CHAT + " 전체채팅");

		} else {
			chat = objective.getScore("§f" + CHAT + " 마을채팅");
		}
		chat.setScore(index--);
		
		// 공백
		Score space2 = objective.getScore(" ");
		space2.setScore(index--);
		
		// 계절
		Score season;
		MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
		MVWorldManager worldManager = core.getMVWorldManager();
		MultiverseWorld world = worldManager.getMVWorld("world");
		
		switch (CustomCropsPlugin.get().getIntegrationManager().getSeasonInterface().getSeason(world.getCBWorld())) {
			case SPRING:
				season = objective.getScore("§f" + SPRING + " 봄");
				break;
			case SUMMER:
				season = objective.getScore("§f" + SUMMER + " 여름");
				break;
			case AUTUMN:
				season = objective.getScore("§f" + AUTUMN + " 가을");
				break;
			case WINTER:
				season = objective.getScore("§f" + WINTER + " 겨울");
				break;
			default:
				season = objective.getScore("계절정보 알수없음");
				break;
		}
		season.setScore(index--);
		
		Score time = objective.getScore("§f" + CLOCK + " " + parseTime(world.getCBWorld().getTime()));
		time.setScore(index--);

		// 공백
		Score space3 = objective.getScore("  ");
		space3.setScore(index--);

		player.setScoreboard(scoreboard);
	}

	public static String parseTime(long time) {
		long gameTime = time;
		long hours = gameTime / 1000 + 6;
		long minutes = (gameTime % 1000) * 60 / 1000;
		String ampm = "AM";
		
		if (hours >= 12) {
			hours -= 12;
			ampm = "PM";
		}
		
		if (hours >= 12) {
			hours -= 12;
			ampm = "AM";
		}
		
		if (hours == 0)
			hours = 12;
		
		String mm = "0" + minutes;
		mm = mm.substring(mm.length() - 2, mm.length());
		
		return hours + ":" + mm + " " + ampm;
	}

}
