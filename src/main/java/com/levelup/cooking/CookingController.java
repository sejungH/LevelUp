package com.levelup.cooking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.levelup.LevelUp;
import com.levelup.LevelUpItem;
import com.levelup.menu.MenuController;
import com.levelup.menu.MenuIcon;
import com.levelup.menu.MenuUnicode;

import dev.lone.itemsadder.api.CustomStack;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillTrigger;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.adapters.BukkitPlayer;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import net.md_5.bungee.api.ChatColor;

public class CookingController {

	public static final String POT = "customitems:pot";
	public static final String POT_MYTHIC = "pot";

	public static final String CHOPPING_BOARD = "customitems:chopping_board";
	public static final String CHOPPING_BOARD_MYTHIC = "chopping_board";

	public static final List<Integer> POT_INGREDIENT = Arrays.asList(MenuController.slot(2, 3),
			MenuController.slot(2, 4), MenuController.slot(2, 5), MenuController.slot(3, 3), MenuController.slot(3, 4),
			MenuController.slot(3, 5));
	public static final int POT_FUEL = MenuController.slot(5, 4);
	public static final List<Material> FUELS = Arrays.asList(Material.ACACIA_LOG, Material.BIRCH_LOG,
			Material.CHERRY_LOG, Material.DARK_OAK_LOG, Material.JUNGLE_LOG, Material.MANGROVE_LOG, Material.OAK_LOG,
			Material.SPRUCE_LOG, Material.STRIPPED_ACACIA_LOG, Material.STRIPPED_BIRCH_LOG,
			Material.STRIPPED_CHERRY_LOG, Material.STRIPPED_DARK_OAK_LOG, Material.STRIPPED_JUNGLE_LOG,
			Material.STRIPPED_MANGROVE_LOG, Material.STRIPPED_OAK_LOG, Material.STRIPPED_SPRUCE_LOG,
			Material.CRIMSON_STEM, Material.WARPED_STEM, Material.STRIPPED_WARPED_STEM, Material.STRIPPED_CRIMSON_STEM);
	protected static final ItemStack FAILURE = new ItemStack(Material.STRING);
	
	public static List<LevelUpItem> ingredients = new ArrayList<LevelUpItem>();

	public static class Recipe {

		private List<LevelUpItem> recipe;
		private LevelUpItem result;

		public Recipe(List<LevelUpItem> recipe, LevelUpItem result) {
			this.recipe = recipe;
			this.result = result;
		}

		public List<LevelUpItem> getRecipe() {
			return recipe;
		}

		public void setRecipe(List<LevelUpItem> recipe) {
			this.recipe = recipe;
		}

		public LevelUpItem getResult() {
			return result;
		}

		public void setResult(LevelUpItem result) {
			this.result = result;
		}

	}

	@SuppressWarnings("unchecked")
	public static List<Recipe> parseCookingRecipes(List<Object> yaml) {
		List<Recipe> cookingRecipes = new ArrayList<Recipe>();

		for (Object obj : yaml) {
			Map<String, Object> rw = (Map<String, Object>) obj;

			List<LevelUpItem> recipes = new ArrayList<LevelUpItem>();
			for (Object recipeObj : (List<Object>) rw.get("recipes")) {
				Map<String, Object> recipe = (Map<String, Object>) recipeObj;
				String material = recipe.get("material") == null ? null
						: recipe.get("material").toString().toUpperCase();
				String namespacedID = recipe.get("namespacedID") == null ? null : recipe.get("namespacedID").toString();
				int amount = Integer.parseInt(recipe.get("amount").toString());
				LevelUpItem item = new LevelUpItem(material, namespacedID, amount);
				recipes.add(item);
				ingredients.add(item);
			}

			Map<String, Object> result = (Map<String, Object>) rw.get("result");
			String material = result.get("material") == null ? null : result.get("material").toString().toUpperCase();
			String namespacedID = result.get("namespacedID") == null ? null : result.get("namespacedID").toString();
			int amount = Integer.parseInt(result.get("amount").toString());
			cookingRecipes.add(new Recipe(recipes, new LevelUpItem(material, namespacedID, amount)));
		}

		return cookingRecipes;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Map<LevelUpItem, LevelUpItem>> parseCookingIngredients(Map<String, Object> yaml) {
		Map<String, Map<LevelUpItem, LevelUpItem>> cookingIngredients = new HashMap<String, Map<LevelUpItem, LevelUpItem>>();

		for (String type : yaml.keySet()) {
			Map<LevelUpItem, LevelUpItem> ingredients = new HashMap<LevelUpItem, LevelUpItem>();
			for (Object ingredientObj : (List<Object>) yaml.get(type)) {
				Map<String, Object> ingredient = (Map<String, Object>) ingredientObj;
				String material = ingredient.get("material") == null ? null
						: ingredient.get("material").toString().toUpperCase();
				String namespacedID = ingredient.get("namespacedID") == null ? null
						: ingredient.get("namespacedID").toString();
				String result = ingredient.get("result") == null ? null : ingredient.get("result").toString();
				ingredients.put(new LevelUpItem(material, namespacedID, 1), new LevelUpItem(null, result, 1));
			}
			cookingIngredients.put(type, ingredients);
		}

		return cookingIngredients;
	}

	public static Recipe findMatchingRecipe(LevelUp plugin, List<ItemStack> ingredients) {

		for (Recipe recipe : plugin.cookingRecipes) {
			boolean isValid = true;

			for (LevelUpItem recipeItem : recipe.getRecipe()) {
				int totalAmount = 0;
				boolean found = false;

				for (ItemStack item : ingredients) {
					if ((recipeItem.getMaterial() != null
							&& item.getType() == Material.getMaterial(recipeItem.getMaterial()))
							|| (recipeItem.getNamespacedID() != null && CustomStack.byItemStack(item) != null
									&& CustomStack.byItemStack(item).getNamespacedID()
											.equals(recipeItem.getNamespacedID()))) {
						totalAmount += item.getAmount();
						found = true;
					}
				}

				if (!found || totalAmount < recipeItem.getAmount()) {
					isValid = false;
					break;
				}
			}

			if (isValid) {
				for (ItemStack item : ingredients) {
					boolean isKnownItem = false;

					for (LevelUpItem recipeItem : recipe.getRecipe()) {
						if ((recipeItem.getMaterial() != null
								&& item.getType() == Material.getMaterial(recipeItem.getMaterial()))
								|| (recipeItem.getNamespacedID() != null && CustomStack.byItemStack(item) != null
										&& CustomStack.byItemStack(item).getNamespacedID()
												.equals(recipeItem.getNamespacedID()))) {
							isKnownItem = true;
						}
					}

					if (!isKnownItem) {
						isValid = false; // 정의되지 않은 아이템이 있을 경우 실패
						break;
					}
				}

				if (isValid) {
					return recipe;
				}
			}
		}

		return null;
	}

	public static Inventory getPotInventory(LevelUp plugin, Player player, Entity entity) {
		Inventory potInv = Bukkit.createInventory(player, 54,
				MenuController.getInventoryTitle(MenuUnicode.COOKING_POT.val()));
		ItemStack potID = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta potIDMeta = potID.getItemMeta();

		NamespacedKey uuidKey = new NamespacedKey(plugin, "uuid");
		potIDMeta.getPersistentDataContainer().set(uuidKey, PersistentDataType.STRING, entity.getUniqueId().toString());
		potID.setItemMeta(potIDMeta);

		potInv.setItem(0, potID);

		NamespacedKey invKey = new NamespacedKey(plugin, "inventory");
		if (entity.getPersistentDataContainer().has(invKey, PersistentDataType.STRING)) {
			String json = entity.getPersistentDataContainer().get(invKey, PersistentDataType.STRING);
			JsonArray jsonArray = JsonParser.parseString(json).getAsJsonArray();

			for (JsonElement e : jsonArray) {
				JsonObject jsonObject = e.getAsJsonObject();
				ItemStack item;

				if (jsonObject.has("namespacedID"))
					item = CustomStack.getInstance(jsonObject.get("namespacedID").getAsString()).getItemStack();
				else
					item = new ItemStack(Material.getMaterial(jsonObject.get("material").getAsString()));

				item.setAmount(jsonObject.get("amount").getAsInt());
				potInv.setItem(jsonObject.get("slot").getAsInt(), item);
			}
		}

		ItemStack cookBtn = MenuIcon.BLANK.val().getItemStack().clone();
		ItemMeta cookIM = cookBtn.getItemMeta();
		cookIM.setDisplayName(ChatColor.WHITE + "요리하기");
		cookBtn.setItemMeta(cookIM);
		potInv.setItem(MenuController.slot(4, 3), cookBtn);
		potInv.setItem(MenuController.slot(4, 4), cookBtn);
		potInv.setItem(MenuController.slot(4, 5), cookBtn);

		return potInv;
	}

	public static void cookRecipe(LevelUp plugin, Player player, Inventory potInv) {
		ItemStack potID = potInv.getItem(0);
		NamespacedKey uuidKey = new NamespacedKey(plugin, "uuid");
		UUID uuid = UUID
				.fromString(potID.getItemMeta().getPersistentDataContainer().get(uuidKey, PersistentDataType.STRING));

		Entity entity = plugin.getServer().getEntity(uuid);
		ActiveMob potMob = MythicBukkit.inst().getMobManager().getMythicMobInstance(entity);

		ItemStack fuel = potInv.getItem(CookingController.POT_FUEL);

		if (fuel != null) {
			List<ItemStack> ingredients = new ArrayList<ItemStack>();

			for (Integer s : CookingController.POT_INGREDIENT) {
				if (potInv.getItem(s) != null) {
					ingredients.add(potInv.getItem(s));
				}
			}

			if (!ingredients.isEmpty()) {
				Recipe recipe = CookingController.findMatchingRecipe(plugin, ingredients);
				String[] stance = new String[1];
				
				Skill skill;
				if (recipe != null) {

					for (LevelUpItem ri : recipe.getRecipe()) {
						int amount = ri.getAmount();

						for (Integer s : CookingController.POT_INGREDIENT) {
							ItemStack ingredient = potInv.getItem(s);

							if (ingredient != null && ((ri.getMaterial() != null
									&& ingredient.getType() == Material.getMaterial(ri.getMaterial().toUpperCase()))
									|| (ri.getNamespacedID() != null && CustomStack.byItemStack(ingredient) != null
											&& CustomStack.byItemStack(ingredient).getNamespacedID()
													.equals(ri.getNamespacedID())))) {

								if (ingredient.getAmount() >= amount) {
									ingredient.setAmount(ingredient.getAmount() - amount);
									break;

								} else {
									amount -= ingredient.getAmount();
									ingredient.setAmount(0);
								}
							}
						}
					}

					if (Math.random() < 0.1) {
						skill = MythicBukkit.inst().getSkillManager().getSkill("cooking_perfect").orElse(null);
						stance[0] = "perfect";
					} else {
						skill = MythicBukkit.inst().getSkillManager().getSkill("cooking_success").orElse(null);
						stance[0] = "success";
					}

				} else {
					for (Integer s : CookingController.POT_INGREDIENT) {
						ItemStack ingredient = potInv.getItem(s);
						if (ingredient != null) {
							ingredient.setAmount(ingredient.getAmount() - 1);
						}
					}

					skill = MythicBukkit.inst().getSkillManager().getSkill("cooking_fail").orElse(null);
					stance[0] = "fail";

				}

				SkillMetadata sm = new SkillMetadataImpl(SkillTrigger.get("API"), potMob, new BukkitPlayer(player));
				if (skill != null) {
					skill.execute(sm);
				}

				fuel.setAmount(fuel.getAmount() - 1);

				player.closeInventory();

				Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

					@Override
					public void run() {
						if (!entity.isDead()) {
							ItemStack result;

							if (recipe != null) {
								if (recipe.getResult().getMaterial() != null) {
									result = new ItemStack(Material.getMaterial(recipe.getResult().getMaterial()));
								} else {
									result = CustomStack.getInstance(recipe.getResult().getNamespacedID())
											.getItemStack();
								}
								result.setAmount(recipe.getResult().getAmount());

							} else {
								result = CookingController.FAILURE;
							}

							if (stance[0].equals("perfect")) {
								result.setAmount(2);
							}

							Location loc = entity.getLocation();
							loc.setY(loc.getY() + 1);
							entity.getWorld().dropItem(loc, result);
						}
					}

				}, 20 * 11);
			}
		}

	}

	public static void chopIngredient(LevelUp plugin, Player player, Entity entity) {
		ItemStack item = player.getInventory().getItemInMainHand();

		SkillExecutor skillExecutor = MythicBukkit.inst().getSkillManager();
		Skill skill = null;
		LevelUpItem[] result = new LevelUpItem[1];
		String[] rank = new String[1];
		for (Entry<String, Map<LevelUpItem, LevelUpItem>> entry : plugin.cookingIngredients.entrySet()) {
			LevelUpItem ingredient = new LevelUpItem(item);
			if (ingredient.getNamespacedID() != null) {
				if (ingredient.getNamespacedID().contains("_silver_star")) {
					rank[0] = "silver";
					ingredient.setNamespacedID(ingredient.getNamespacedID().split("_silver_star")[0]);

				} else if (ingredient.getNamespacedID().contains("_golden_star")) {
					rank[0] = "golden";
					ingredient.setNamespacedID(ingredient.getNamespacedID().split("_golden_star")[0]);
				}
			}

			if (entry.getValue().containsKey(ingredient)) {
				if (entry.getKey().equals("meats")) {
					skill = skillExecutor.getSkill("chopping_meat").orElse(null);

				} else if (entry.getKey().equals("vegetables")) {
					skill = skillExecutor.getSkill("chopping_vegetable").orElse(null);

				} else if (entry.getKey().equals("fruits")) {
					skill = skillExecutor.getSkill("chopping_fruit").orElse(null);

				} else if (entry.getKey().equals("fishes")) {
					skill = skillExecutor.getSkill("chopping_fish").orElse(null);

				}
				result[0] = entry.getValue().get(ingredient);
				break;
			}
		}

		if (skill != null) {
			item.setAmount(item.getAmount() - 1);
			ActiveMob mob = MythicBukkit.inst().getMobManager().getMythicMobInstance(entity);
			SkillMetadata sm = new SkillMetadataImpl(SkillTrigger.get("API"), mob, new BukkitPlayer(player));
			skill.execute(sm);

			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {

				@Override
				public void run() {
					Location loc = entity.getLocation();

					if (rank[0] == "silver")
						result[0].setAmount(2);

					else if (rank[0] == "golden")
						result[0].setAmount(3);

					entity.getWorld().dropItem(loc, result[0].getItemStack());
				}
				
			}, 20);
		}
	}
}
