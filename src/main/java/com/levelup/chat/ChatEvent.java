package com.levelup.chat;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.levelup.LevelUp;
import com.levelup.player.PlayerData;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.chat.ComponentSerializer;

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
			event.setCancelled(true);
			
			TextComponent displayName = new TextComponent(pd.getName());
			String color;
			if (pd.getNickname() != null && (color = pd.getNicknameColor()) != null) {
				displayName.setColor(ChatColor.of(color));
			}
			
			ArrayList<TextComponent> components = new ArrayList<TextComponent>();
			TextComponent hoverMessage = new TextComponent(new ComponentBuilder(pd.getUsername()).create());
			hoverMessage.addExtra(new TextComponent(ComponentSerializer.parse("{text: \"\n\"}")));
			hoverMessage.addExtra(new TextComponent(new ComponentBuilder("/귓 " + pd.getName()).color(ChatColor.GRAY).create()));
			components.add(hoverMessage);
			BaseComponent[] hoverToSend = (BaseComponent[])components.toArray(new BaseComponent[components.size()]);

			displayName.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hoverToSend)));
			displayName.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/귓 " + pd.getName() + " "));
			
			TextComponent seperator = new TextComponent(" : ");
			seperator.setColor(ChatColor.of("#AAAAAA"));
			
			TextComponent message = new TextComponent(event.getMessage());

			plugin.getServer().spigot().broadcast(new TextComponent(displayName, seperator, message));

		} else if (pd.getChatType() == ChatType.VILLAGE) {
			event.setCancelled(true);

			ChatColor color = ChatColor.of("#FFBCE1");
			
			TextComponent displayName = new TextComponent("[마을채팅] " + pd.getName());
			displayName.setColor(color);
			
			ArrayList<TextComponent> components = new ArrayList<TextComponent>();
			TextComponent hoverMessage = new TextComponent(new ComponentBuilder(pd.getUsername()).create());
			hoverMessage.addExtra(new TextComponent(ComponentSerializer.parse("{text: \"\n\"}")));
			hoverMessage.addExtra(new TextComponent(new ComponentBuilder("/귓 " + pd.getName()).color(ChatColor.GRAY).create()));
			components.add(hoverMessage);
			BaseComponent[] hoverToSend = (BaseComponent[])components.toArray(new BaseComponent[components.size()]);
			
			displayName.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hoverToSend)));
			displayName.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/귓 " + pd.getName() + " "));
			
			TextComponent seperator = new TextComponent(" > ");
			seperator.setColor(ChatColor.of("#AAAAAA"));
			
			TextComponent message = new TextComponent(event.getMessage());
			message.setColor(color);
			
			for (Player op : plugin.getServer().getOnlinePlayers()) {
				PlayerData opd = plugin.players.get(op.getUniqueId());

				if (pd.getVillage() == opd.getVillage()) {
					op.spigot().sendMessage(new TextComponent(displayName, seperator, message));
				}
			}
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

				ChatController.sendWhisper(plugin, player, args[1], message);

			} else {
				player.sendMessage(ChatColor.RED + "사용법: /귓 <유저> <메세지>");
			}
		}
	}

}
