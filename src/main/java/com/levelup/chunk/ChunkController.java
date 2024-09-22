package com.levelup.chunk;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import com.levelup.LevelUp;
import com.levelup.player.PlayerData;
import com.levelup.village.VillageData;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;

import dev.lone.itemsadder.api.CustomStack;
import net.md_5.bungee.api.ChatColor;

public class ChunkController {
	
	public static final List<String> BANNED_BLOCKS = new ArrayList<String>(
			Arrays.asList("CHEST", "BOX", "FURNACE", "SMOKER", "HOPPER", "BARREL", "SIGN", "BREWING_STAND", "ANVIL"));

	public static final int PLAYERCHUNK_PRICE = 5;
	public static final int VILLAGECHUNK_PRICE = 20;
	public static final double MULTIPLE = 1.3;
	public static final int VILLAGE_DISTANCE = 15;

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

	public static Map<Integer, List<Chunk>> getVillageChunks(LevelUp plugin) throws SQLException {
		Connection conn = plugin.mysql.getConnection();

		String sql = "SELECT * FROM village_chunk";

		PreparedStatement pstmt = conn.prepareStatement(sql);
		ResultSet rs = pstmt.executeQuery();

		MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
		MVWorldManager worldManager = core.getMVWorldManager();
		MultiverseWorld world = worldManager.getMVWorld("world");

		Map<Integer, List<Chunk>> villageChunks = new HashMap<Integer, List<Chunk>>();

		while (rs.next()) {
			int villageId = rs.getInt("village");

			if (!villageChunks.containsKey(villageId))
				villageChunks.put(villageId, new ArrayList<Chunk>());

			villageChunks.get(villageId).add(world.getCBWorld().getChunkAt(rs.getInt("x"), rs.getInt("z")));
		}

		rs.close();
		pstmt.close();

		plugin.getServer().getConsoleSender().sendMessage("[" + plugin.getName() + "] " + ChatColor.GREEN + "Loaded "
				+ ChatColor.YELLOW + villageChunks.size() + ChatColor.GREEN + " Village Chunk Data");

		return villageChunks;
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

		plugin.playerChunks.get(player.getUniqueId()).add(chunk);
	}

	public static void addVillageChunk(LevelUp plugin, int villageId, Chunk chunk) throws SQLException {
		Connection conn = plugin.mysql.getConnection();

		String sql = "INSERT INTO village_chunk (village, x, z) VALUES (?, ?, ?)";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, villageId);
		pstmt.setInt(2, chunk.getX());
		pstmt.setInt(3, chunk.getZ());
		pstmt.executeUpdate();
		pstmt.close();

		plugin.villageChunks.get(villageId).add(chunk);
	}

	public static void deletePlayerChunk(LevelUp plugin, UUID uuid, Chunk chunk) throws SQLException {
		Connection conn = plugin.mysql.getConnection();

		String sql = "DELETE FROM player_chunk WHERE uuid = ? AND x = ? AND z = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, uuid.toString());
		pstmt.setInt(2, chunk.getX());
		pstmt.setInt(3, chunk.getZ());
		pstmt.executeUpdate();
		pstmt.close();

		plugin.playerChunks.get(uuid).remove(chunk);
	}

	public static void deleteAllPlayerChunks(LevelUp plugin, UUID uuid) throws SQLException {
		Connection conn = plugin.mysql.getConnection();
		String sql = "DELETE FROM player_chunk WHERE uuid = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, uuid.toString());
		pstmt.executeUpdate();
		pstmt.close();

		plugin.playerChunks.remove(uuid);
	}

	public static void deleteVillageChunk(LevelUp plugin, int villageId, Chunk chunk) throws SQLException {
		Connection conn = plugin.mysql.getConnection();

		String sql = "DELETE FROM village_chunk WHERE village = ? AND x = ? AND z = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, villageId);
		pstmt.setInt(2, chunk.getX());
		pstmt.setInt(3, chunk.getZ());
		pstmt.executeUpdate();
		pstmt.close();

		plugin.villageChunks.get(villageId).remove(chunk);
	}

	public static void deleteAllVillageChunks(LevelUp plugin, int villageId) throws SQLException {
		Connection conn = plugin.mysql.getConnection();
		String sql = "DELETE FROM village_chunk WHERE village = ?";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, villageId);
		pstmt.executeUpdate();
		pstmt.close();

		plugin.villageChunks.remove(villageId);
	}

	public static boolean checkPlayerChunkByPlayer(LevelUp plugin, Player player, Chunk chunk) {
		MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
		MVWorldManager worldManager = core.getMVWorldManager();
		MultiverseWorld world = worldManager.getMVWorld(player.getWorld());

		if (!world.getAlias().equalsIgnoreCase("world")) {
			player.sendMessage(ChatColor.RED + "이 월드에서는 실행할 수 없습니다");
			return false;
		}

		for (UUID uuid : plugin.playerChunks.keySet()) {
			PlayerData pd = plugin.players.get(uuid);
			List<Chunk> chunks = plugin.playerChunks.get(uuid);

			for (Chunk c : chunks) {
				int distX = Math.abs(chunk.getX() - c.getX());
				int distZ = Math.abs(chunk.getZ() - c.getZ());
				if (distX == 0 && distZ == 0) {
					if (uuid.equals(player.getUniqueId())) {
						player.sendMessage(ChatColor.RED + "이미 소유 중인 청크입니다");
						return false;
					} else {
						player.sendMessage(ChatColor.RED + "이미 " + pd.getUsername() + " 님이 소유 중인 청크입니다");
						return false;
					}

				} else if (distX <= 1 && distZ <= 1) {
					if (!uuid.equals(player.getUniqueId())) {
						player.sendMessage(ChatColor.RED + "근처에 " + pd.getUsername() + " 님이 소유 중인 청크가 있습니다");
						return false;
					}
				}

			}
		}

		return true;
	}

	public static boolean checkPlayerChunkByVillage(LevelUp plugin, Player player, Chunk chunk) {
		MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
		MVWorldManager worldManager = core.getMVWorldManager();
		MultiverseWorld world = worldManager.getMVWorld(player.getWorld());

		if (!world.getAlias().equalsIgnoreCase("world")) {
			player.sendMessage(ChatColor.RED + "이 월드에서는 실행할 수 없습니다");
			return false;
		}

		for (UUID uuid : plugin.playerChunks.keySet()) {
			PlayerData pd = plugin.players.get(uuid);
			List<Chunk> chunks = plugin.playerChunks.get(uuid);

			for (Chunk c : chunks) {
				int distX = Math.abs(chunk.getX() - c.getX());
				int distZ = Math.abs(chunk.getZ() - c.getZ());
				if (distX <= 1 && distZ <= 1) {
					player.sendMessage(ChatColor.RED + "이미 " + pd.getUsername() + " 님이 소유 중인 청크입니다");
					return false;

				} else if (distX < VILLAGE_DISTANCE && distZ < VILLAGE_DISTANCE) {
					if (distX <= distZ) {
						player.sendMessage(ChatColor.RED + String.valueOf(distZ) + " 청크 이내에 " + pd.getUsername()
								+ " 님이 소유 중인 청크가 있습니다");
					} else if (distZ < distX) {
						player.sendMessage(ChatColor.RED + String.valueOf(distX) + " 청크 이내에 " + pd.getUsername()
								+ " 님이 소유 중인 청크가 있습니다");
					}
					player.sendMessage(
							ChatColor.RED + "다른 유저의 청크와 최소 " + VILLAGE_DISTANCE + " 청크 이상 떨어진 청크만 구매할 수 있습니다");
					return false;
				}
			}
		}

		return true;
	}

	public static boolean checkVillageChunkByPlayer(LevelUp plugin, Player player, Chunk chunk) {
		MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
		MVWorldManager worldManager = core.getMVWorldManager();
		MultiverseWorld world = worldManager.getMVWorld(player.getWorld());

		if (!world.getAlias().equalsIgnoreCase("world")) {
			player.sendMessage(ChatColor.RED + "이 월드에서는 실행할 수 없습니다");
			return false;
		}

		for (int villageId : plugin.villageChunks.keySet()) {
			VillageData vd = plugin.villages.get(villageId);
			List<Chunk> chunks = plugin.villageChunks.get(villageId);

			for (Chunk c : chunks) {
				int distX = Math.abs(chunk.getX() - c.getX());
				int distZ = Math.abs(chunk.getZ() - c.getZ());

				if (distX <= 1 && distZ <= 1) {
					player.sendMessage(ChatColor.RED + "이미 마을 [" + vd.getName() + "] 에서 소유 중인 청크입니다");
					return false;

				} else if (distX < VILLAGE_DISTANCE && distZ < VILLAGE_DISTANCE) {
					if (distX <= distZ) {
						player.sendMessage(
								ChatColor.RED + String.valueOf(distZ) + " 청크 이내에 마을 [" + vd.getName() + "] 이 있습니다");
					} else if (distZ < distX) {
						player.sendMessage(
								ChatColor.RED + String.valueOf(distX) + " 청크 이내에 마을 [" + vd.getName() + "] 이 있습니다");
					}
					player.sendMessage(ChatColor.RED + "다른 마을과 최소 " + VILLAGE_DISTANCE + " 청크 이상 떨어진 청크만 구매할 수 있습니다");
					return false;
				}
			}
		}

		return true;
	}

	public static boolean checkVillageChunkByVillage(LevelUp plugin, Player player, Chunk chunk) {
		MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
		MVWorldManager worldManager = core.getMVWorldManager();
		MultiverseWorld world = worldManager.getMVWorld(player.getWorld());

		if (!world.getAlias().equalsIgnoreCase("world")) {
			player.sendMessage(ChatColor.RED + "이 월드에서는 실행할 수 없습니다");
			return false;
		}

		PlayerData pd = plugin.players.get(player.getUniqueId());

		for (int villageId : plugin.villageChunks.keySet()) {
			VillageData vd = plugin.villages.get(villageId);
			List<Chunk> chunks = plugin.villageChunks.get(villageId);

			for (Chunk c : chunks) {
				int distX = Math.abs(chunk.getX() - c.getX());
				int distZ = Math.abs(chunk.getZ() - c.getZ());

				if (distX <= 1 && distZ <= 1) {
					player.sendMessage(ChatColor.RED + "이미 마을 [" + vd.getName() + "] 에서 소유 중인 청크입니다");
					return false;
				}
			}

			if (villageId == pd.getVillage()) {
				boolean isNextTo = false;

				if (chunks == null) {
					isNextTo = true;
				}

				for (Chunk c : chunks) {
					int distX = Math.abs(chunk.getX() - c.getX());
					int distZ = Math.abs(chunk.getZ() - c.getZ());
					if ((distX <= 4 && distZ <= 1) || (distX <= 1 && distZ <= 4)) {
						isNextTo = true;
						break;
					}
				}

				if (!isNextTo) {
					player.sendMessage(ChatColor.RED + "현재 소유중인 청크에 인접한 청크만 구매할 수 있습니다");
					return false;
				}

			} else {
				for (Chunk c : chunks) {
					int distX = Math.abs(chunk.getX() - c.getX());
					int distZ = Math.abs(chunk.getZ() - c.getZ());
					if (distX < VILLAGE_DISTANCE && distZ < VILLAGE_DISTANCE) {
						if (distX <= distZ) {
							player.sendMessage(
									ChatColor.RED + String.valueOf(distZ) + " 청크 이내에 마을 [" + vd.getName() + "] 이 있습니다");
						} else if (distZ < distX) {
							player.sendMessage(
									ChatColor.RED + String.valueOf(distX) + " 청크 이내에 마을 [" + vd.getName() + "] 이 있습니다");
						}
						player.sendMessage(
								ChatColor.RED + "다른 마을과 최소 " + VILLAGE_DISTANCE + " 청크 이상 떨어진 청크만 구매할 수 있습니다");
						return false;
					}
				}
			}
		}

		return true;
	}

	public static void displayPlayerChunkBorder(LevelUp plugin, Player player, Chunk chunk, Color color, int second) {
		World world = player.getWorld();
		NamespacedKey borderKey = new NamespacedKey(plugin, "border");

		final double y = player.getLocation().getY() + 1;

		for (int i = 0; i < 16; i++) {
			double x = chunk.getX() * 16 + (double) i;

			for (int j = 0; j < 16; j++) {
				double z = chunk.getZ() * 16 + (double) j;

				Location loc = new Location(world, x, y, z);
				
				CustomStack bar;
				if (color.equals(Color.RED)) {
					bar = CustomStack.getInstance("customitems:red_bar");

				} else if (color.equals(Color.BLUE)) {
					bar = CustomStack.getInstance("customitems:blue_bar");

				} else if (color.equals(Color.GREEN)) {
					bar = CustomStack.getInstance("customitems:green_bar");

				} else {
					bar = CustomStack.getInstance("customitems:green_bar");
				}

				if (i == 0) {
					ItemFrame frame = (ItemFrame) world.spawnEntity(loc, EntityType.GLOW_ITEM_FRAME);
					frame.setFixed(true);
					frame.setVisible(false);
					frame.setFacingDirection(BlockFace.EAST);
					frame.setItem(bar.getItemStack());
					frame.setCustomNameVisible(false);
					frame.getPersistentDataContainer().set(borderKey, PersistentDataType.BOOLEAN, true);
				}

				if (j == 0) {
					ItemFrame frame = (ItemFrame) world.spawnEntity(loc, EntityType.GLOW_ITEM_FRAME);
					frame.setFixed(true);
					frame.setVisible(false);
					frame.setFacingDirection(BlockFace.SOUTH);
					frame.setItem(bar.getItemStack());
					frame.setCustomNameVisible(false);
					frame.getPersistentDataContainer().set(borderKey, PersistentDataType.BOOLEAN, true);
				}

				if (i == 15) {
					ItemFrame frame = (ItemFrame) world.spawnEntity(loc, EntityType.GLOW_ITEM_FRAME);
					frame.setFixed(true);
					frame.setVisible(false);
					frame.setFacingDirection(BlockFace.WEST);
					frame.setItem(bar.getItemStack());
					frame.setCustomNameVisible(false);
					frame.getPersistentDataContainer().set(borderKey, PersistentDataType.BOOLEAN, true);
				}

				if (j == 15) {
					ItemFrame frame = (ItemFrame) world.spawnEntity(loc, EntityType.GLOW_ITEM_FRAME);
					frame.setFixed(true);
					frame.setVisible(false);
					frame.setFacingDirection(BlockFace.NORTH);
					frame.setItem(bar.getItemStack());
					frame.setCustomNameVisible(false);
					frame.getPersistentDataContainer().set(borderKey, PersistentDataType.BOOLEAN, true);
				}
			}
		}

		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

			@Override
			public void run() {
				double x = chunk.getX() * 16 + 8;
				double z = chunk.getZ() * 16 + 8;

				Location loc = new Location(world, x, y, z);

				Collection<Entity> entities = world.getNearbyEntities(loc, 16, 1, 16);
				for (Entity entity : entities) {
					if (entity.getType().equals(EntityType.GLOW_ITEM_FRAME)
							&& entity.getPersistentDataContainer().has(borderKey)) {
						entity.remove();
					}
				}
			}

		}, 20 * second);

	}

	public static void displayVillageChunkBorder(LevelUp plugin, Player player, Chunk chunk, Color color, int second) {

		World world = player.getWorld();
		NamespacedKey borderKey = new NamespacedKey(plugin, "border");

		final double y = player.getLocation().getY() + 1;

		for (int i = -16; i < 32; i++) {
			double x = chunk.getX() * 16 + (double) i;

			for (int j = -16; j < 32; j++) {
				double z = chunk.getZ() * 16 + (double) j;

				Location loc = new Location(world, x, y, z);

				CustomStack bar;
				if (color.equals(Color.RED)) {
					bar = CustomStack.getInstance("customitems:red_bar");

				} else if (color.equals(Color.BLUE)) {
					bar = CustomStack.getInstance("customitems:blue_bar");

				} else if (color.equals(Color.GREEN)) {
					bar = CustomStack.getInstance("customitems:green_bar");

				} else {
					bar = CustomStack.getInstance("customitems:green_bar");
				}

				if (i == -16) {
					ItemFrame frame = (ItemFrame) world.spawnEntity(loc, EntityType.GLOW_ITEM_FRAME);
					frame.setFixed(true);
					frame.setVisible(false);
					frame.setFacingDirection(BlockFace.EAST);
					frame.setItem(bar.getItemStack());
					frame.setCustomNameVisible(false);
					frame.getPersistentDataContainer().set(borderKey, PersistentDataType.BOOLEAN, true);
				}

				if (j == -16) {
					ItemFrame frame = (ItemFrame) world.spawnEntity(loc, EntityType.GLOW_ITEM_FRAME);
					frame.setFixed(true);
					frame.setVisible(false);
					frame.setFacingDirection(BlockFace.SOUTH);
					frame.setItem(bar.getItemStack());
					frame.setCustomNameVisible(false);
					frame.getPersistentDataContainer().set(borderKey, PersistentDataType.BOOLEAN, true);
				}

				if (i == 31) {
					ItemFrame frame = (ItemFrame) world.spawnEntity(loc, EntityType.GLOW_ITEM_FRAME);
					frame.setFixed(true);
					frame.setVisible(false);
					frame.setFacingDirection(BlockFace.WEST);
					frame.setItem(bar.getItemStack());
					frame.setCustomNameVisible(false);
					frame.getPersistentDataContainer().set(borderKey, PersistentDataType.BOOLEAN, true);
				}

				if (j == 31) {
					ItemFrame frame = (ItemFrame) world.spawnEntity(loc, EntityType.GLOW_ITEM_FRAME);
					frame.setFixed(true);
					frame.setVisible(false);
					frame.setFacingDirection(BlockFace.NORTH);
					frame.setItem(bar.getItemStack());
					frame.setCustomNameVisible(false);
					frame.getPersistentDataContainer().set(borderKey, PersistentDataType.BOOLEAN, true);
				}
			}
		}

		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

			@Override
			public void run() {
				double x = chunk.getX() * 16 + 8;
				double z = chunk.getZ() * 16 + 8;

				Location loc = new Location(world, x, y, z);

				Collection<Entity> entities = world.getNearbyEntities(loc, 48, 1, 48);
				for (Entity entity : entities) {
					if (entity.getType().equals(EntityType.GLOW_ITEM_FRAME)
							&& entity.getPersistentDataContainer().has(borderKey)) {
						entity.remove();
					}
				}
			}

		}, 20 * second);
	}

	public static double calculatePlayerChunkPrice(int count) {
		if (count == 0)
			return 0;

		else if (count == 1)
			return PLAYERCHUNK_PRICE;

		else
			return calculatePlayerChunkPrice(count - 1) * MULTIPLE;
	}

	public static double calculateVillageChunkPrice(int count) {
		if (count == 0)
			return 0;

		else if (count == 1)
			return VILLAGECHUNK_PRICE;

		else
			return calculateVillageChunkPrice(count - 1) * MULTIPLE;
	}

	public static List<Chunk> getAdjointChunks(Chunk chunk) {
		World world = chunk.getWorld();
		List<Chunk> chunks = new ArrayList<Chunk>();
		chunks.add(world.getChunkAt(chunk.getX() + 1, chunk.getZ() + 1));
		chunks.add(world.getChunkAt(chunk.getX() + 1, chunk.getZ()));
		chunks.add(world.getChunkAt(chunk.getX() + 1, chunk.getZ() - 1));
		chunks.add(world.getChunkAt(chunk.getX(), chunk.getZ() + 1));
		chunks.add(world.getChunkAt(chunk.getX(), chunk.getZ() - 1));
		chunks.add(world.getChunkAt(chunk.getX() - 1, chunk.getZ() + 1));
		chunks.add(world.getChunkAt(chunk.getX() - 1, chunk.getZ()));
		chunks.add(world.getChunkAt(chunk.getX() - 1, chunk.getZ() - 1));

		return chunks;
	}

	public static List<Chunk> getNearVillageChunk(LevelUp plugin, int villageId, Chunk chunk) {

		List<Chunk> near = new ArrayList<Chunk>();

		for (Chunk c : plugin.villageChunks.get(villageId)) {
			int distX = Math.abs(chunk.getX() - c.getX());
			int distZ = Math.abs(chunk.getZ() - c.getZ());

			if ((distX == 3 && distZ == 0) || (distX == 0 && distZ == 3)) {
				near.add(c);
			}
		}

		return near;
	}

	public static UUID getChunkOwnerPlayer(LevelUp plugin, Chunk chunk) {
		for (UUID uuid : plugin.playerChunks.keySet()) {
			List<Chunk> chunks = plugin.playerChunks.get(uuid);

			if (chunks.contains(chunk)) {
				return uuid;
			}
		}

		return null;
	}

	public static int getChunkOwnerVillage(LevelUp plugin, Chunk chunk) {
		for (int id : plugin.villageChunks.keySet()) {
			List<Chunk> chunks = plugin.villageChunks.get(id);

			for (Chunk c : chunks) {
				int distX = Math.abs(chunk.getX() - c.getX());
				int distZ = Math.abs(chunk.getZ() - c.getZ());

				if (distX < 2 && distZ < 2) {
					return id;
				}
			}
		}

		return -1;
	}
	
	public static boolean canInteract(LevelUp plugin, Player player, Chunk chunk) {
		PlayerData pd = plugin.players.get(player.getUniqueId());
		
		UUID owner = getChunkOwnerPlayer(plugin, chunk);
		if (owner != null) {
			
			if (owner.equals(player.getUniqueId())) {
				return true;
				
			} else {
				PlayerData ownerPD = plugin.players.get(owner);
				player.sendMessage(ChatColor.RED + ownerPD.getUsername() + " 님이 소유 중인 청크입니다");
				return false;
			}
			
		}
			
		int ownerVillage = getChunkOwnerVillage(plugin, chunk);
		if (ownerVillage > 0) {
			
			if (ownerVillage == pd.getVillage()) {
				return true;
				
			} else {
				VillageData ownerVD = plugin.villages.get(ownerVillage);
				player.sendMessage(ChatColor.RED + "마을 [" + ownerVD.getName() + "] 에서 소유 중인 청크입니다");
				return false;
			}
			
		}
			
		player.sendMessage(ChatColor.RED + "본인이 소유 중인 청크가 아닙니다");
		return false;
	}
}
