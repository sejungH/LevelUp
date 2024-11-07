package com.levelup.seasonpass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.levelup.LevelUp;
import com.levelup.LevelUpIcon;
import com.levelup.LevelUpItem;
import com.levelup.menu.MenuController;
import com.levelup.player.PlayerController;
import com.levelup.player.PlayerData;

import net.md_5.bungee.api.ChatColor;

public class SeasonPassController {

	public static final int[] REWARD_SLOTS = { MenuController.slot(4, 0), MenuController.slot(3, 2),
			MenuController.slot(5, 3), MenuController.slot(3, 5), MenuController.slot(4, 7) };

	public static class SeasonPass {
		private int position;
		private int available;
		private LocalDate lastDate;
		private boolean premium;

		public SeasonPass(int position, int available, LocalDate lastDate, boolean premium) {
			this.position = position;
			this.available = available;
			this.lastDate = lastDate;
			this.premium = premium;
		}

		public int getPosition() {
			return position;
		}

		public void setPosition(int position) {
			this.position = position;
		}

		public void addPosition(int i) {
			this.position += i;
		}

		public int getAvailable() {
			return available;
		}

		public void setAvailable(int available) {
			this.available = available;
		}

		public void addAvailable(int i) {
			this.available += i;
		}

		public LocalDate getLastDate() {
			return lastDate;
		}

		public void setLastDate(LocalDate lastDate) {
			this.lastDate = lastDate;
		}

		public boolean isPremium() {
			return premium;
		}

		public void setPremium(boolean premium) {
			this.premium = premium;
		}

		@Override
		public String toString() {
			return "SeasonPass [available=" + available + ", position=" + position + ", lastDate=" + lastDate
					+ ", premium=" + premium + "]";
		}
	}

	public static Map<UUID, SeasonPass> getSeasonPassData(LevelUp plugin) throws SQLException {
		Connection conn = plugin.mysql.getConnection();

		Map<UUID, SeasonPass> seasonPassData = new HashMap<UUID, SeasonPass>();

		String sql = "SELECT * FROM seasonpass";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();

		while (rs.next()) {
			UUID uuid = UUID.fromString(rs.getString("uuid"));
			int position = Integer.parseInt(rs.getString("position"));
			int available = Integer.parseInt(rs.getString("available"));
			LocalDate lastDate = LocalDate.parse(rs.getString("last_date"));
			boolean premium = rs.getInt("premium") == 0 ? false : true;
			seasonPassData.put(uuid, new SeasonPass(position, available, lastDate, premium));
		}

		rs.close();
		pstmt.close();

		plugin.getServer().getConsoleSender().sendMessage("[" + plugin.getName() + "] " + ChatColor.GREEN + "Loaded "
				+ ChatColor.YELLOW + seasonPassData.size() + ChatColor.GREEN + " Season Pass Data");

		return seasonPassData;
	}

	public static void initSeasonPass(LevelUp plugin, Player player) throws SQLException {
		Connection conn = plugin.mysql.getConnection();
		String sql = "INSERT INTO seasonpass (uuid, position, available, last_date, premium) VALUE (?, ?, ?, ?, ?)";
		PreparedStatement pstmt = conn.prepareStatement(sql);

		LocalDate now = LocalDate.now();

		pstmt.setString(1, player.getUniqueId().toString());
		pstmt.setInt(2, 0);
		pstmt.setInt(3, 0);
		pstmt.setInt(4, 0);
		pstmt.setString(4, now.toString());
		pstmt.setInt(5, 0);
		pstmt.executeUpdate();
		pstmt.close();

		plugin.seasonPassData.put(player.getUniqueId(), new SeasonPass(0, 0, now, false));
	}

	@SuppressWarnings("unchecked")
	public static List<Entry<LevelUpItem, Boolean>> parseSeasonPassItems(List<Object> yaml) {
		List<Entry<LevelUpItem, Boolean>> seasonPassItems = new ArrayList<Entry<LevelUpItem, Boolean>>();

		for (Object obj : yaml) {
			Map<String, Object> o = (Map<String, Object>) obj;
			String material = o.get("material") == null ? null : o.get("material").toString().toUpperCase();
			String namespacedID = o.get("namespacedID") == null ? null : o.get("namespacedID").toString();
			int amount = Integer.parseInt(o.get("amount").toString());
			boolean premium = o.get("premium").toString().equalsIgnoreCase("true") ? true : false;

			seasonPassItems.add(new AbstractMap.SimpleEntry<LevelUpItem, Boolean>(new LevelUpItem(material, namespacedID, amount), premium));
		}

		return seasonPassItems;
	}

	public static List<ItemStack> getUnobtainedItems(LevelUp plugin, UUID uuid) {
		List<ItemStack> unobtainedItems = new ArrayList<ItemStack>();

		SeasonPass seasonPass = plugin.seasonPassData.get(uuid);

		int i = 0;
		for (Entry<LevelUpItem, Boolean> reward : plugin.seasonPassItems) {
			if (i == seasonPass.getPosition())
				break;

			if (!seasonPass.isPremium() && reward.getValue()) {
				ItemStack item = reward.getKey().getItemStack();

				boolean found = false;
				for (ItemStack unobtained : unobtainedItems) {
					if (unobtained.isSimilar(item)) {
						unobtained.setAmount(unobtained.getAmount() + item.getAmount());
						found = true;
						break;
					}
				}

				if (!found) {
					unobtainedItems.add(item);
				}
			}

			i++;
		}

		return unobtainedItems;
	}

	public static void jumpOneStep(LevelUp plugin, Player player) throws SQLException {
		Connection conn = plugin.mysql.getConnection();
		String sql = "UPDATE seasonpass SET position = ? where uuid = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);

		SeasonPass seasonPass = plugin.seasonPassData.get(player.getUniqueId());
		seasonPass.addPosition(1);

		pstmt.setInt(1, seasonPass.getPosition());
		pstmt.setString(2, player.getUniqueId().toString());
		pstmt.executeUpdate();
		pstmt.close();
	}
	
	public static void updatePremium(LevelUp plugin, UUID uuid, boolean isPremium) throws SQLException {
		Connection conn = plugin.mysql.getConnection();
		String sql = "UPDATE seasonpass SET premium = ? where uuid = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);

		SeasonPass seasonPass = plugin.seasonPassData.get(uuid);
		seasonPass.setPremium(isPremium);

		pstmt.setInt(1, isPremium ? 1 : 0);
		pstmt.setString(2, uuid.toString());
		pstmt.executeUpdate();
		pstmt.close();
	}
	
	public static void updateAvailable(LevelUp plugin, Player player) throws SQLException {
		Connection conn = plugin.mysql.getConnection();
		String sql = "UPDATE seasonpass SET available = ?, last_date = ? where uuid = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);

		LocalDate now = LocalDate.now();

		SeasonPass seasonPass = plugin.seasonPassData.get(player.getUniqueId());
		seasonPass.addAvailable(1);
		seasonPass.setLastDate(now);

		pstmt.setInt(1, seasonPass.getAvailable());
		pstmt.setString(2, now.toString());
		pstmt.setString(3, player.getUniqueId().toString());
		pstmt.executeUpdate();
		pstmt.close();
	}

	public static void checkAttendance(LevelUp plugin, Player player) {
		int tick = (int) plugin.getServer().getServerTickManager().getTickRate();
		int[] task = new int[2];
		task[0] = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

			@Override
			public void run() {
				if (player.isOnline()) {
					PlayerData pd = plugin.players.get(player.getUniqueId());
					LocalDateTime now = LocalDateTime.now();
					if (pd.getLastOnline().toLocalDate().equals(now.toLocalDate())) {
						try {
							updateAvailable(plugin, player);
							player.sendMessage(Character.toString(LevelUpIcon.MAIL.val()) + ChatColor.GOLD
									+ "서버 접속 후 30분이 지났습니다. 시즌 패스 보상을 받으세요.");
							player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
							PlayerController.playerScheduler.get(player.getUniqueId()).remove(Integer.valueOf(task[0]));

						} catch (SQLException e) {
							e.printStackTrace();
						}
					} else {
						LocalDateTime twelveThirty = LocalDateTime.of(now.toLocalDate(), LocalTime.of(0, 30));
						Duration duration = Duration.between(now, twelveThirty);
						task[1] = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

							@Override
							public void run() {
								if (player.isOnline()) {
									try {
										updateAvailable(plugin, player);
										player.sendMessage(Character.toString(LevelUpIcon.MAIL.val()) + ChatColor.GOLD
												+ "서버 접속 후 30분이 지났습니다. 시즌 패스 보상을 받으세요.");
										player.playSound(player, Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
										PlayerController.playerScheduler.get(player.getUniqueId()).remove(Integer.valueOf(task[1]));

									} catch (SQLException e) {
										e.printStackTrace();
									}
								}
							}

						}, tick * duration.getSeconds());

						PlayerController.playerScheduler.get(player.getUniqueId()).add(task[1]);
					}
				}
			}

		}, tick * 60 * 30);

		PlayerController.playerScheduler.get(player.getUniqueId()).add(task[0]);
	}

}
