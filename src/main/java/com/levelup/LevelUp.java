package com.levelup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import com.levelup.bag.BagCommand;
import com.levelup.bag.BagEvent;
import com.levelup.bag.BagTabCompleter;
import com.levelup.chat.ChatCommand;
import com.levelup.chat.ChatEvent;
import com.levelup.chat.ChatTabCompleter;
import com.levelup.chunk.ChunkCommand;
import com.levelup.chunk.ChunkController;
import com.levelup.chunk.ChunkEvent;
import com.levelup.chunk.ChunkTabCompleter;
import com.levelup.cooking.CookingController;
import com.levelup.cooking.CookingController.Recipe;
import com.levelup.cooking.CookingEvent;
import com.levelup.db.MySQLConnect;
import com.levelup.friend.FriendCommand;
import com.levelup.friend.FriendController;
import com.levelup.friend.FriendData;
import com.levelup.friend.FriendTabCompleter;
import com.levelup.menu.MenuEvent;
import com.levelup.message.MessageController;
import com.levelup.money.MoneyCommand;
import com.levelup.npc.NPCCommand;
import com.levelup.npc.NPCController;
import com.levelup.npc.NPCController.NPCMythic;
import com.levelup.npc.NPCEvent;
import com.levelup.npc.NPCTabCompleter;
import com.levelup.player.PlayerCommand;
import com.levelup.player.PlayerController;
import com.levelup.player.PlayerData;
import com.levelup.player.PlayerEvent;
import com.levelup.player.PlayerTabCompleter;
import com.levelup.post.PostController;
import com.levelup.post.PostEvent;
import com.levelup.ride.RideCommand;
import com.levelup.ride.RideEvent;
import com.levelup.ride.RideTabCompleter;
import com.levelup.seasonpass.SeasonPassCommand;
import com.levelup.seasonpass.SeasonPassController;
import com.levelup.seasonpass.SeasonPassController.SeasonPass;
import com.levelup.seasonpass.SeasonPassTabCompleter;
import com.levelup.shopping.ShoppingController;
import com.levelup.tool.ToolCommand;
import com.levelup.tool.ToolController;
import com.levelup.tool.ToolData;
import com.levelup.tool.ToolEvent;
import com.levelup.tool.ToolQuest;
import com.levelup.tool.ToolQuestMessage;
import com.levelup.tool.ToolTabCompleter;
import com.levelup.tool.ToolType;
import com.levelup.village.VillageCommand;
import com.levelup.village.VillageController;
import com.levelup.village.VillageData;
import com.levelup.village.VillageEvent;
import com.levelup.village.VillageTabCompleter;
import com.levelup.warp.WarpEvent;

import net.md_5.bungee.api.ChatColor;

public class LevelUp extends JavaPlugin {

	public static final String CONFIG_PATH = "plugins/LevelUp/";
	public static final String[] CONFIG_FILES = { "tool_exp.yml", "tool_quest_items.yml", "tool_quest_message.yml",
			"tool_quest.yml", "cooking_ingredients.yml", "cooking_recipes.yml", "shopping_items.yml", "npc_shop.yml",
			"seasonpass_items.yml", "cash_items.yml" };

	public MySQLConnect mysql;

	public Map<UUID, PlayerData> players;
	public Map<Integer, VillageData> villages;
	public Map<UUID, Integer> villageApplies;
	public List<FriendData> friends;
	public Map<UUID, List<UUID>> userBlocks;
	public Map<UUID, ToolData> tools;
	public Map<UUID, List<ItemStack>> bags;
	public Map<UUID, List<Chunk>> playerChunks;
	public Map<Integer, List<Chunk>> villageChunks;
	public Map<UUID, Map<ToolType, Map<Material, Integer>>> quests;
	public Map<UUID, SeasonPass> seasonPassData;

	public Map<ToolType, Map<Material, List<ToolQuest>>> toolQuest;
	public Map<ToolType, Map<Material, Integer>> toolExp;
	public Map<Material, Entry<Character, String>> toolQuestItems;
	public Map<ToolType, Map<Material, List<ToolQuestMessage>>> toolQuestMessage;
	public Map<UUID, Map<ToolType, List<String>>> toolSkins;
	public List<Recipe> cookingRecipes;
	public Map<String, Map<LevelUpItem, LevelUpItem>> cookingIngredients;
	public Map<LevelUpItem, Integer> shoppingItems;
	public Map<NPCMythic, Map<LevelUpItem, Integer>> npcShopItems;
	public List<Entry<LevelUpItem, Boolean>> seasonPassItems;
	public List<String> cashItems;

	@Override
	public void onEnable() {
		super.onEnable();

		try {
			initDB();
			initConfig();
			initEvents();
			initCommand();
			registerTimers();

			if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
				new LevelUpPlaceholders(this).register();
			}

			PluginDescriptionFile pdFile = this.getDescription();
			this.getLogger().info(pdFile.getName() + " version " + pdFile.getVersion() + " is enabled");

		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDisable() {
		super.onDisable();

		try {
			getServer().getScheduler().cancelTasks(this);
			saveData();
			mysql.closeConnection();

			PluginDescriptionFile pdFile = this.getDescription();
			this.getLogger().info(pdFile.getName() + " version " + pdFile.getVersion() + " is disabled");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void initCommand() {
		getCommand("levelup").setTabCompleter(new LevelUpTabCompleter());
		getCommand("levelup").setExecutor(new LevelUpCommand(this));

		getCommand("마을").setTabCompleter(new VillageTabCompleter(this));
		getCommand("마을").setExecutor(new VillageCommand(this));

		getCommand("친구").setTabCompleter(new FriendTabCompleter(this));
		getCommand("친구").setExecutor(new FriendCommand(this));
		getCommand("차단").setTabCompleter(new FriendTabCompleter(this));
		getCommand("차단").setExecutor(new FriendCommand(this));

		getCommand("입금").setExecutor(new MoneyCommand(this));
		getCommand("출금").setExecutor(new MoneyCommand(this));

		getCommand("hat").setExecutor(new PlayerCommand(this));
		getCommand("nickname").setTabCompleter(new PlayerTabCompleter(this));
		getCommand("nickname").setExecutor(new PlayerCommand(this));

		getCommand("전체채팅").setExecutor(new ChatCommand(this));
		getCommand("마을채팅").setExecutor(new ChatCommand(this));
		getCommand("c").setExecutor(new ChatCommand(this));
		getCommand("ㅊ").setExecutor(new ChatCommand(this));
		getCommand("귓").setTabCompleter(new ChatTabCompleter(this));
		getCommand("귓").setExecutor(new ChatCommand(this));

		getCommand("lvnpc").setTabCompleter(new NPCTabCompleter(this));
		getCommand("lvnpc").setExecutor(new NPCCommand(this));

		getCommand("ride").setTabCompleter(new RideTabCompleter(this));
		getCommand("ride").setExecutor(new RideCommand(this));

		getCommand("t").setTabCompleter(new ToolTabCompleter());
		getCommand("t").setExecutor(new ToolCommand(this));

		getCommand("bag").setTabCompleter(new BagTabCompleter(this));
		getCommand("bag").setExecutor(new BagCommand(this));

		getCommand("청크").setTabCompleter(new ChunkTabCompleter(this));
		getCommand("청크").setExecutor(new ChunkCommand(this));
		
		getCommand("seasonpass").setTabCompleter(new SeasonPassTabCompleter(this));
		getCommand("seasonpass").setExecutor(new SeasonPassCommand(this));
	}

	public void initEvents() {
		PluginManager pm = getServer().getPluginManager();

		pm.registerEvents(new LevelUpEvent(), this);
		pm.registerEvents(new PlayerEvent(this), this);
		pm.registerEvents(new VillageEvent(this), this);
		pm.registerEvents(new ChatEvent(this), this);
		pm.registerEvents(new MenuEvent(this), this);
		pm.registerEvents(new NPCEvent(this), this);
		pm.registerEvents(new RideEvent(this), this);
		pm.registerEvents(new ToolEvent(this), this);
		pm.registerEvents(new BagEvent(this), this);
		pm.registerEvents(new ChunkEvent(this), this);
		pm.registerEvents(new CookingEvent(this), this);
		pm.registerEvents(new PostEvent(this), this);
		pm.registerEvents(new WarpEvent(), this);
	}

	public void initDB() throws SQLException {
		mysql = new MySQLConnect(this);
		mysql.openConnection();
		loadDB();
	}

	public void loadDB() throws SQLException {
		players = PlayerController.getPlayers(this);
		villages = VillageController.getVillages(this);
		villageApplies = VillageController.getVillageApplies(this);
		friends = FriendController.getFriends(this);
		userBlocks = FriendController.getUserBlocks(this);
		tools = ToolController.getTools(this);
		quests = ToolController.getQuests(this);
		toolSkins = ToolController.getToolSkins(this);
		playerChunks = ChunkController.getPlayerChunks(this);
		villageChunks = ChunkController.getVillageChunks(this);
		seasonPassData = SeasonPassController.getSeasonPassData(this);
		MessageController.getPendingMessages(this);
		PostController.getPostItems(this);
	}

	public void initConfig() throws IOException {
		File directory = new File(CONFIG_PATH);
		if (!directory.exists())
			directory.mkdir();

		for (String filename : CONFIG_FILES) {
			File file = new File(CONFIG_PATH + filename);
			if (!file.exists()) {
				file.createNewFile();
				InputStream fileIn = this.getClass().getClassLoader().getResourceAsStream("initial/" + filename);
				OutputStream out = new FileOutputStream(file);
				fileIn.transferTo(out);
				fileIn.close();
				out.close();
			}
		}

		loadConfig();
	}

	public void loadConfig() throws IOException {

		for (String filename : CONFIG_FILES) {
			File file = new File(CONFIG_PATH + filename);
			InputStream fileInput = new FileInputStream(file);
			Yaml yaml = new Yaml();

			if (filename.equals("tool_quest.yml")) {
				toolQuest = ToolController.parseToolQuest(yaml.load(fileInput));
				this.getServer().getConsoleSender()
						.sendMessage("[" + this.getName() + "] " + ChatColor.GREEN + "Loaded Tool Quest Data");

			} else if (filename.equals("tool_exp.yml")) {
				toolExp = ToolController.parseToolExp(yaml.load(fileInput));
				this.getServer().getConsoleSender()
						.sendMessage("[" + this.getName() + "] " + ChatColor.GREEN + "Loaded Tool Exp Data");

			} else if (filename.equals("tool_quest_items.yml")) {
				toolQuestItems = ToolController.parseToolQuestItems(yaml.load(fileInput));
				this.getServer().getConsoleSender()
						.sendMessage("[" + this.getName() + "] " + ChatColor.GREEN + "Loaded Tool Quest Item Data");

			} else if (filename.equals("tool_quest_message.yml")) {
				toolQuestMessage = ToolController.parseToolQuestMessage(yaml.load(fileInput));
				this.getServer().getConsoleSender()
						.sendMessage("[" + this.getName() + "] " + ChatColor.GREEN + "Loaded Tool Quest Message Data");

			} else if (filename.equals("cooking_recipes.yml")) {
				cookingRecipes = CookingController.parseCookingRecipes(yaml.load(fileInput));
				this.getServer().getConsoleSender()
						.sendMessage("[" + this.getName() + "] " + ChatColor.GREEN + "Loaded Cooking Recipe Data");

			} else if (filename.equals("cooking_ingredients.yml")) {
				cookingIngredients = CookingController.parseCookingIngredients(yaml.load(fileInput));
				this.getServer().getConsoleSender()
						.sendMessage("[" + this.getName() + "] " + ChatColor.GREEN + "Loaded Cooking Ingredient Data");

			} else if (filename.equals("shopping_items.yml")) {
				shoppingItems = ShoppingController.parseShoppingItems(yaml.load(fileInput));
				this.getServer().getConsoleSender()
						.sendMessage("[" + this.getName() + "] " + ChatColor.GREEN + "Loaded Shopping Item Data");
				
			} else if (filename.equals("npc_shop.yml")) {
				npcShopItems = NPCController.parseNPCShopItems(yaml.load(fileInput));
				this.getServer().getConsoleSender()
						.sendMessage("[" + this.getName() + "] " + ChatColor.GREEN + "Loaded NPC Shop Item Data");

			} else if (filename.equals("seasonpass_items.yml")) {
				seasonPassItems = SeasonPassController.parseSeasonPassItems(yaml.load(fileInput));
				this.getServer().getConsoleSender()
						.sendMessage("[" + this.getName() + "] " + ChatColor.GREEN + "Loaded Season Pass Item Data");

			} else if (filename.equals("cash_items.yml")) {
				cashItems = LevelUpController.parseCashItems(yaml.load(fileInput));
				this.getServer().getConsoleSender()
						.sendMessage("[" + this.getName() + "] " + ChatColor.GREEN + "Loaded Cash Item Data");
			}
		}

	}

	public void registerTimers() {
		LevelUp plugin = this;

		int tick = (int) this.getServer().getServerTickManager().getTickRate();
		int min = 60;

		// Run every 1 min
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

			int counter = 0;

			@Override
			public void run() {

				try {
					VillageController.checkDeletionPeriod(plugin);
					VillageController.checkTaxOverdue(plugin);
					PlayerController.checkRestUser(plugin);
					VillageController.updateTaxWeekly(plugin);

					if (counter % 5 == 0)
						ToolController.updateToolExp(plugin);

					if (counter % 10 == 0)
						LevelUpController.cleanUpScheduler(plugin);

					counter++;

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

		}, 0, 1 * min * tick);
	}

	public void saveData() throws SQLException {
		for (Player player : this.getServer().getOnlinePlayers()) {
			PlayerController.updateLastOnline(this, player.getUniqueId());
		}
		ToolController.updateAllToolExp(this);
	}
}
