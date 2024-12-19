package com.levelup.bag;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import com.levelup.LevelUp;
import com.levelup.LevelUpIcon;
import com.levelup.player.PlayerController;
import com.levelup.player.PlayerData;

import dev.lone.itemsadder.api.CustomStack;
import net.md_5.bungee.api.ChatColor;

public class BagEvent implements Listener {

	private LevelUp plugin;

	public BagEvent(LevelUp plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onBagOpen(PlayerInteractEvent event) {
		if (event.getHand() == EquipmentSlot.OFF_HAND)
			return;

		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player player = event.getPlayer();
			CustomStack bag = CustomStack.byItemStack(event.getItem());
			NamespacedKey ownerKey = new NamespacedKey(plugin, "owner");

			if (bag != null && bag.getNamespacedID().contains("_bag")) {

				ItemMeta meta = bag.getItemStack().getItemMeta();

				if (meta.getPersistentDataContainer().has(ownerKey)) {
					UUID ownerUUID = UUID
							.fromString(meta.getPersistentDataContainer().get(ownerKey, PersistentDataType.STRING));

					if (player.getUniqueId().equals(ownerUUID)) {
						Pattern pattern = Pattern.compile("(^.?)\\s(.+)");
						Matcher matcher = pattern.matcher(ChatColor.stripColor(bag.getDisplayName()));

						String name;
						if (matcher.find()) {
							name = ChatColor.WHITE + matcher.group(1) + ChatColor.RESET + " " + matcher.group(2);
						} else {
							name = ChatColor.stripColor(bag.getDisplayName());
						}

						Inventory bagInv = BagController.getBagInventory(plugin, ownerUUID, name, 4);
						player.openInventory(bagInv);

					} else {
						player.sendMessage(ChatColor.RED + "본인 소유의 가방만 열 수 있습니다");

					}

				} else {
					List<String> lore = new ArrayList<String>();
					lore.add(ChatColor.GRAY + "소유자: " + player.getName());
					meta.setLore(lore);
					meta.getPersistentDataContainer().set(ownerKey, PersistentDataType.STRING,
							player.getUniqueId().toString());

					event.getItem().setItemMeta(meta);
					player.sendMessage(ChatColor.GREEN + "이 가방의 소유자로 등록되었습니다");
				}
			}
		}
	}

	@EventHandler
	public void onBagClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		CustomStack bag = CustomStack.byItemStack(player.getInventory().getItemInMainHand());
		String title = ChatColor.stripColor(event.getView().getTitle());

		if (bag != null && bag.getNamespacedID().contains("_bag")
				&& title.equalsIgnoreCase(ChatColor.stripColor(bag.getDisplayName()))) {
			if (event.getCurrentItem() != null
					&& event.getCurrentItem().equals(player.getInventory().getItemInMainHand())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBagClose(InventoryCloseEvent event) throws SQLException {
		Inventory inv = event.getInventory();
		Player player = (Player) event.getPlayer();
		CustomStack bag = CustomStack.byItemStack(player.getInventory().getItemInMainHand());
		String title = ChatColor.stripColor(event.getView().getTitle());
		boolean isOnline = false;

		if (bag != null && bag.getNamespacedID().contains("_bag")
				&& title.equalsIgnoreCase(ChatColor.stripColor(bag.getDisplayName()))) {
			isOnline = true;

		} else if (event.getView().getTitle().contains(Character.toString(LevelUpIcon.BAG.val()))) {
			String regex = "(?<=\\s).+?(?= 님의 가방)";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(event.getView().getTitle());
			
			if (matcher.find()) {
				PlayerData pd = PlayerController.getPlayerData(plugin, matcher.group());
				OfflinePlayer offlinePlayer = plugin.getServer().getOfflinePlayer(pd.getUuid());
				
				if (offlinePlayer.isOnline()) {
					isOnline = true;
					
				} else {
					BagController.saveBagInventory(pd.getUuid(), inv);
				}
				
			}
		}
		
		if (isOnline) {
			Inventory ender = player.getEnderChest();

			if (ender.getItem(0) == null) {
				ender.setItem(0, new ItemStack(Material.SHULKER_BOX));
				ender.setItem(1, new ItemStack(Material.SHULKER_BOX));
			}

			BlockStateMeta box1Meta = (BlockStateMeta) ender.getItem(0).getItemMeta();
			BlockStateMeta box2Meta = (BlockStateMeta) ender.getItem(1).getItemMeta();
			ShulkerBox box1 = (ShulkerBox) box1Meta.getBlockState();
			ShulkerBox box2 = (ShulkerBox) box2Meta.getBlockState();
			
			for (int i = 0; i < 27; i++) {
				if (i < inv.getSize()) 
					box1.getInventory().setItem(i, inv.getItem(i));
				if (i + 27 < inv.getSize()) 
					box2.getInventory().setItem(i, inv.getItem(i + 27));
			}

			box1Meta.setBlockState(box1);
			box2Meta.setBlockState(box2);

			ender.getItem(0).setItemMeta(box1Meta);
			ender.getItem(1).setItemMeta(box2Meta);
		}
	}

}
