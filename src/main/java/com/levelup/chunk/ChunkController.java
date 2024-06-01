package com.levelup.chunk;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Player;

import com.levelup.LevelUp;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;

import io.lumine.mythic.bukkit.utils.particles.Particle;
import net.md_5.bungee.api.ChatColor;

public class ChunkController {

	public static final int MIN_Y = 30;
	public static final Material BLOCK = Material.WHITE_CONCRETE;
	public static final int PRICE = 5;
	public static final double MULTIPLE = 1.3;

	public static Map<UUID, List<Chunk>> getPlayerChunks(LevelUp plugin) throws SQLException {
		Connection conn = plugin.mysql.getConnection();

		String sql = "SELECT * FROM player_chunk";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();

		MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
		MVWorldManager worldManager = core.getMVWorldManager();
		MultiverseWorld world = worldManager.getMVWorld("world");

		Map<UUID, List<Chunk>> playerChunks = new HashMap<UUID, List<Chunk>>();

		while (rs.next()) {
			UUID uuid = UUID.fromString(rs.getString("uuid"));

			if (!playerChunks.containsKey(uuid))
				playerChunks.put(uuid, new ArrayList<Chunk>());

			playerChunks.get(uuid).add(world.getCBWorld().getChunkAt(rs.getInt("x"), rs.getInt("z")));
		}

		rs.close();
		pstmt.close();

		plugin.getServer().getConsoleSender().sendMessage("[" + plugin.getName() + "] " + ChatColor.GREEN + "Loaded "
				+ ChatColor.YELLOW + playerChunks.size() + ChatColor.GREEN + " Player Chunk Data");

		return playerChunks;
	}

	public static void addPlayerChunk(LevelUp plugin, Player player, Chunk chunk) throws SQLException {
		Connection conn = plugin.mysql.getConnection();

		String sql = "INSERT INTO player_chunk (uuid, x, z) VALUES (?, ?, ?)";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, player.getUniqueId().toString());
		pstmt.setInt(2, chunk.getX());
		pstmt.setInt(3, chunk.getZ());
		pstmt.executeUpdate();
		pstmt.close();
	}

	public static void deletePlayerChunk(LevelUp plugin, Player player, Chunk chunk) throws SQLException {
		Connection conn = plugin.mysql.getConnection();

		String sql = "DELETE FROM player_chunk WHERE uuid = ? AND x = ? AND z = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, player.getUniqueId().toString());
		pstmt.setInt(2, chunk.getX());
		pstmt.setInt(3, chunk.getZ());
		pstmt.executeUpdate();
		pstmt.close();
	}

	public static void displayChunkBorder(LevelUp plugin, final Player player, final Chunk chunk, final Color color, final int second) {

		final int task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {

			@Override
			public void run() {
				DustOptions dustOptions = new DustOptions(color, 1.0F);

				for (int i = 0; i < 17; i++) {
					int x = chunk.getX() * 16 + i;

					for (int j = 0; j < 17; j++) {
						int z = chunk.getZ() * 16 + j;

						if (i == 0 || i == 16) {
							player.spawnParticle(Particle.REDSTONE.toBukkitParticle(), (double) x,
									(double) player.getLocation().getY(), (double) z, 50, dustOptions);

						} else if (j == 0 || j == 16) {
							player.spawnParticle(Particle.REDSTONE.toBukkitParticle(), (double) x,
									(double) player.getLocation().getY(), (double) z, 50, dustOptions);
						}
					}
				}
			}

		}, 0, 20);

		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

			@Override
			public void run() {
				Bukkit.getScheduler().cancelTask(task);
			}

		}, 20 * second);
	}
	
	public static double calculatePrice(int count) {
		if (count == 0)
			return 0;
		
		else if (count == 1) 
			return PRICE;
		
		else 
			return calculatePrice(count - 1) * MULTIPLE;
	}

}
