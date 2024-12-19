package com.levelup.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.levelup.LevelUp;
import com.levelup.LevelUpIcon;
import com.levelup.chat.ChatType;
import com.levelup.money.MoneyController;
import com.levelup.player.PlayerData;
import com.levelup.village.VillageData;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;

import net.momirealms.customcrops.api.BukkitCustomCropsPlugin;

public class ScoreboardController {

	public static void displayScoreboard(LevelUp plugin, Player player) {
		if (player.getScoreboard().getObjective(player.getName()) != null)
			player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);

		// " §aLEVEL UP! "
		Scoreboard scoreboard = plugin.getServer().getScoreboardManager().getNewScoreboard();
		Objective objective = scoreboard.registerNewObjective(player.getName(), Criteria.DUMMY,
				Character.toString(LevelUpIcon.LOGO.val()));
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);

		int index = 100;

		// 공백
		Score space1 = objective.getScore("");
		space1.setScore(index--);
		Score space2 = objective.getScore(" ");
		space2.setScore(index--);
		Score space3 = objective.getScore("  ");
		space3.setScore(index--);
		Score space4 = objective.getScore("   ");
		space4.setScore(index--);

		PlayerData pd = plugin.players.get(player.getUniqueId());
		VillageData vd = plugin.villages.get(pd.getVillage());

		// 월드
		Score world;
		MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
		MVWorldManager worldManager = core.getMVWorldManager();
		MultiverseWorld mvWorld = worldManager.getMVWorld(player.getWorld());

		if (mvWorld.getAlias().equalsIgnoreCase("spawn")) {
			world = objective.getScore("  §f" + LevelUpIcon.WORLD.val() + " 광장");

		} else if (mvWorld.getAlias().equalsIgnoreCase("world")) {
			world = objective.getScore("  §f" + LevelUpIcon.WORLD.val() + " 건축 월드");

		} else if (mvWorld.getAlias().equalsIgnoreCase("wild")) {
			world = objective.getScore("  §f" + LevelUpIcon.WORLD.val() + " 야생 월드");

		} else if (mvWorld.getAlias().equalsIgnoreCase("nether")) {
			world = objective.getScore("  §f" + LevelUpIcon.WORLD.val() + " 지옥 월드");
		
		} else if (mvWorld.getAlias().equalsIgnoreCase("tutorial")) {
			world = objective.getScore("  §f" + LevelUpIcon.WORLD.val() + " 튜토리얼");

		} else {
			world = objective.getScore("  §f" + LevelUpIcon.WORLD.val() + " 알 수 없음");
		}
		world.setScore(index--);

		// 마을
		Score village;
		if (vd == null) {
			village = objective.getScore("  §f" + LevelUpIcon.VILLAGE.val() + " 없음");
		} else {
			village = objective.getScore("  §f" + LevelUpIcon.VILLAGE.val() + " " + vd.getName());
		}
		village.setScore(index--);

		// 잔고
		Score balance = objective
				.getScore("  §f" + LevelUpIcon.COIN.val() + " " + MoneyController.withLargeIntegers(pd.getBalance()));
		balance.setScore(index--);

		// 채팅
		Score chat;
		if (pd.getChatType() == ChatType.DEFAULT) {
			chat = objective.getScore("  §f" + LevelUpIcon.CHAT.val() + " 전체채팅");

		} else {
			chat = objective.getScore("  §f" + LevelUpIcon.CHAT.val() + " 마을채팅");
		}
		chat.setScore(index--);

		// 공백
		Score space5 = objective.getScore("    ");
		space5.setScore(index--);
		
		// 계절
		Score season;
		MultiverseWorld mainWorld = worldManager.getMVWorld("world");

		int date = BukkitCustomCropsPlugin.getInstance().getWorldManager().getDate(mainWorld.getCBWorld());

		switch (BukkitCustomCropsPlugin.getInstance().getWorldManager().getSeason(mainWorld.getCBWorld())) {
		case SPRING:
			season = objective.getScore("  §f" + LevelUpIcon.SPRING.val() + " 봄 " + date + "일");
			break;
		case SUMMER:
			season = objective.getScore("  §f" + LevelUpIcon.SUMMER.val() + " 여름 " + date + "일");
			break;
		case AUTUMN:
			season = objective.getScore("  §f" + LevelUpIcon.AUTUMN.val() + " 가을 " + date + "일");
			break;
		case WINTER:
			season = objective.getScore("  §f" + LevelUpIcon.WINTER.val() + " 겨울 " + date + "일");
			break;
		default:
			season = objective.getScore("  계절정보 알수없음");
			break;
		}
		season.setScore(index--);

		Score time = objective
				.getScore("  §f" + LevelUpIcon.CLOCK.val() + " " + parseTime(mainWorld.getCBWorld().getTime()));
		time.setScore(index--);

		// 공백
		Score space6 = objective.getScore("     ");
		space6.setScore(index--);
		
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
