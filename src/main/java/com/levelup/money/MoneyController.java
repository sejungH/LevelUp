package com.levelup.money;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.levelup.main.LevelUp;
import com.levelup.player.PlayerData;
import com.levelup.scoreboard.ScoreboardController;

import dev.lone.itemsadder.api.CustomStack;

public class MoneyController {

	public static final CustomStack GOLD = CustomStack.getInstance("customitems:gold");
	public static final CustomStack SILVER = CustomStack.getInstance("customitems:silver");
	public static final CustomStack COPPER = CustomStack.getInstance("customitems:copper");

	public static int depositAll(LevelUp plugin, Player player) {

		int countGold = 0;
		int countSilver = 0;
		int countCopper = 0;

		for (ItemStack i : player.getInventory()) {
			CustomStack customStack = CustomStack.byItemStack(i);
			if (customStack != null) {
				if (customStack.getNamespacedID().equals(GOLD.getNamespacedID())) {
					countGold += i.getAmount();
					player.getInventory().remove(i);

				} else if (customStack.getNamespacedID().equals(SILVER.getNamespacedID())) {
					countSilver += i.getAmount();
					player.getInventory().remove(i);

				} else if (customStack.getNamespacedID().equals(COPPER.getNamespacedID())) {
					countCopper += i.getAmount();
					player.getInventory().remove(i);
				}
			}
		}

		return countGold * 100 + countSilver * 10 + countCopper;
	}

	public static void depoistMoeny(LevelUp plugin, Connection conn, int amount, Player player) throws SQLException {
		PlayerData pd = plugin.players.get(player.getUniqueId());
		
		String sql = "UPDATE player SET balance = ? WHERE uuid = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);

		pstmt.setInt(1, pd.getBalance() + amount);
		pstmt.setString(2, pd.getUuid().toString());
		pstmt.executeUpdate();
		pstmt.close();
		
		pd.setBalance(pd.getBalance() + amount);
		
		ScoreboardController.updateScoreboard(plugin, player);
	}
	
	public static void withdrawMoeny(LevelUp plugin, Connection conn, int amount, Player player) throws SQLException {
		PlayerData pd = plugin.players.get(player.getUniqueId());
		String sql = "UPDATE player SET balance = ? WHERE uuid = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);

		pstmt.setInt(1, pd.getBalance() - amount);
		pstmt.setString(2, pd.getUuid().toString());
		pstmt.executeUpdate();
		pstmt.close();
		
		pd.setBalance(pd.getBalance() - amount);
		
		ScoreboardController.updateScoreboard(plugin, player);
	}
	
	public static String withLargeIntegers(double value) {
	    DecimalFormat df = new DecimalFormat("###,###,###");
	    return df.format(value);
	}

}
