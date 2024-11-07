package com.levelup.shopping;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.levelup.LevelUpItem;

public class ShoppingController {

	@SuppressWarnings("unchecked")
	public static Map<LevelUpItem, Integer> parseShoppingItems(List<Object> yaml) {
		Map<LevelUpItem, Integer> shoppingItems = new LinkedHashMap<LevelUpItem, Integer>();
		
		for (Object obj : yaml) {
			Map<String, Object> o = (Map<String, Object>) obj;
			String material = o.get("material") == null ? null : o.get("material").toString().toUpperCase();
			String namespacedID = o.get("namespacedID") == null ? null : o.get("namespacedID").toString();
			int amount = Integer.parseInt(o.get("amount").toString());
			int price = Integer.parseInt(o.get("price").toString());
			LevelUpItem item = new LevelUpItem(material, namespacedID, amount);
			shoppingItems.put(item, price);
		}
		
		return shoppingItems;
	}

}
