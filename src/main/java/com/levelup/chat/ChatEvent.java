package com.levelup.chat;

import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.levelup.LevelUp;
import com.levelup.player.PlayerData;

import net.md_5.bungee.api.ChatColor;

public class ChatEvent implements Listener {

	private LevelUp plugin;

	public ChatEvent(LevelUp plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		PlayerData pd = plugin.players.get(player.getUniqueId());

		if (pd.getChatType() == ChatType.DEFAULT) {

			String displayName = pd.getName();
			if (pd.getNickname() != null && pd.getNicknameColor() != null) {
				displayName = ChatColor.of(pd.getNicknameColor()) + displayName;
			}

			event.setMessage(RomanToKorean.RomToKor(event.getMessage()));
			event.setFormat(displayName + ChatColor.of("#AAAAAA") + " : " + ChatColor.WHITE + event.getMessage());

			for (Entry<UUID, List<UUID>> entry : plugin.userBlocks.entrySet()) {
				if (entry.getValue().contains(player.getUniqueId())) {
					OfflinePlayer op = plugin.getServer().getOfflinePlayer(entry.getKey());
					if (op.isOnline()) {
						event.getRecipients().remove((Player) op);
					}
				}
			}
			
//			event.setCancelled(true);
//
//			TextComponent displayName = new TextComponent(pd.getName());
//			String color;
//			if (pd.getNickname() != null && (color = pd.getNicknameColor()) != null) {
//				displayName.setColor(ChatColor.of(color));
//			}
//
//			ArrayList<TextComponent> components = new ArrayList<TextComponent>();
//			TextComponent hoverMessage = new TextComponent(new ComponentBuilder(pd.getUsername()).create());
//			hoverMessage.addExtra(new TextComponent(ComponentSerializer.parse("{text: \"\n\"}")));
//			hoverMessage.addExtra(
//					new TextComponent(new ComponentBuilder("/귓 " + pd.getName()).color(ChatColor.GRAY).create()));
//			components.add(hoverMessage);
//			BaseComponent[] hoverToSend = (BaseComponent[]) components.toArray(new BaseComponent[components.size()]);
//
//			displayName.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hoverToSend)));
//			displayName.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/귓 " + pd.getName() + " "));
//
//			TextComponent seperator = new TextComponent(" : ");
//			seperator.setColor(ChatColor.of("#AAAAAA"));
//
//			TextComponent message = new TextComponent();
//
//			plugin.getServer().spigot().broadcast(new TextComponent(displayName, seperator, message));
//			plugin.getLogger().info(pd.getUsername() + " : " + event.getMessage());

		} else if (pd.getChatType() == ChatType.VILLAGE) {
			event.setMessage(RomanToKorean.RomToKor(event.getMessage()));
			event.setFormat(ChatColor.of("#FFBCE1") + "[마을채팅] " + pd.getName() + ChatColor.of("#AAAAAA") + " : "
					+ ChatColor.of("#FFBCE1") + event.getMessage());

			for (Player op : plugin.getServer().getOnlinePlayers()) {
				PlayerData opd = plugin.players.get(op.getUniqueId());

				if (pd.getVillage() != opd.getVillage()) {
					event.getRecipients().remove((Player) op);
				}
			}

			for (Entry<UUID, List<UUID>> entry : plugin.userBlocks.entrySet()) {
				if (entry.getValue().contains(player.getUniqueId())) {
					OfflinePlayer op = plugin.getServer().getOfflinePlayer(entry.getKey());
					if (op.isOnline() && event.getRecipients().contains((Player) op)) {
						event.getRecipients().remove((Player) op);
					}
				}
			}

//			event.setCancelled(true);
//
//			ChatColor color = ChatColor.of("#FFBCE1");
//
//			TextComponent displayName = new TextComponent("[마을채팅] " + pd.getName());
//			displayName.setColor(color);
//
//			ArrayList<TextComponent> components = new ArrayList<TextComponent>();
//			TextComponent hoverMessage = new TextComponent(new ComponentBuilder(pd.getUsername()).create());
//			hoverMessage.addExtra(new TextComponent(ComponentSerializer.parse("{text: \"\n\"}")));
//			hoverMessage.addExtra(
//					new TextComponent(new ComponentBuilder("/귓 " + pd.getName()).color(ChatColor.GRAY).create()));
//			components.add(hoverMessage);
//			BaseComponent[] hoverToSend = (BaseComponent[]) components.toArray(new BaseComponent[components.size()]);
//
//			displayName.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hoverToSend)));
//			displayName.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/귓 " + pd.getName() + " "));
//
//			TextComponent seperator = new TextComponent(" : ");
//			seperator.setColor(ChatColor.of("#AAAAAA"));
//
//			TextComponent message = new TextComponent(RomanToKorean.RomToKor(event.getMessage()));
//			message.setColor(color);
//
//			for (Player op : plugin.getServer().getOnlinePlayers()) {
//				PlayerData opd = plugin.players.get(op.getUniqueId());
//
//				if (pd.getVillage() == opd.getVillage()) {
//					op.spigot().sendMessage(new TextComponent(displayName, seperator, message));
//				}
//			}
//
//			VillageData vd = plugin.villages.get(pd.getVillage());
//			plugin.getLogger().info("[" + vd.getName() + "] " + pd.getUsername() + " : " + event.getMessage());
		}
	}

	@EventHandler
	public void onPlayerWhisper(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		String command = event.getMessage().substring(1);
		String[] args = command.split(" ");

		if (args[0].equalsIgnoreCase("w") || args[0].equalsIgnoreCase("tell") || args[0].equalsIgnoreCase("mag")) {
			event.setCancelled(true);

			if (args.length > 2) {
				String message = "";
				for (int i = 2; i < args.length; i++) {
					if (i == args.length - 1) {
						message += args[i];
					} else {
						message += args[i] + " ";
					}
				}

				ChatController.sendWhisper(plugin, player, args[1], RomanToKorean.RomToKor(message));

			} else {
				player.sendMessage(ChatColor.RED + "사용법: /귓 <유저> <메세지>");
			}
		}
	}

}
