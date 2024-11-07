package com.levelup.chat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.levelup.LevelUp;
import com.levelup.player.PlayerController;
import com.levelup.player.PlayerData;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import net.md_5.bungee.chat.ComponentSerializer;

public class ChatController {

	public static void sendWhisper(LevelUp plugin, Player player, String username, String msg) {
		PlayerData reciever = PlayerController.getPlayerData(plugin, username);

		if (reciever != null) {
			OfflinePlayer recievePlayer = plugin.getServer().getOfflinePlayer(reciever.getUuid());

			if (recievePlayer.isOnline()) {
				PlayerData sender = plugin.players.get(player.getUniqueId());

				ChatColor color = ChatColor.of("#C16EFF");

				TextComponent senderInfo = new TextComponent(
						"[" + sender.getName() + " ➡ " + reciever.getName() + "] ");
				senderInfo.setColor(color);

				ArrayList<TextComponent> components = new ArrayList<TextComponent>();
				TextComponent hoverMessage = new TextComponent(new ComponentBuilder(sender.getUsername()).create());
				hoverMessage.addExtra(new TextComponent(ComponentSerializer.parse("{text: \"\n\"}")));
				hoverMessage.addExtra(new TextComponent(
						new ComponentBuilder("/귓 " + sender.getName()).color(ChatColor.GRAY).create()));
				components.add(hoverMessage);
				BaseComponent[] hoverToSend = (BaseComponent[]) components
						.toArray(new BaseComponent[components.size()]);

				senderInfo.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hoverToSend)));
				senderInfo.setClickEvent(
						new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/귓 " + sender.getName() + " "));

				TextComponent recieverInfo = new TextComponent(
						"[" + sender.getName() + " ➡ " + reciever.getName() + "] ");
				recieverInfo.setColor(color);

				components.clear();
				hoverMessage = new TextComponent(new ComponentBuilder(sender.getUsername()).create());
				hoverMessage.addExtra(new TextComponent(ComponentSerializer.parse("{text: \"\n\"}")));
				hoverMessage.addExtra(new TextComponent(
						new ComponentBuilder("/귓 " + reciever.getName()).color(ChatColor.GRAY).create()));
				components.add(hoverMessage);
				hoverToSend = (BaseComponent[]) components.toArray(new BaseComponent[components.size()]);

				recieverInfo.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hoverToSend)));
				recieverInfo.setClickEvent(
						new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/귓 " + reciever.getName() + " "));

				TextComponent message = new TextComponent(": " + msg);
				message.setColor(color);

				((Player) recievePlayer).spigot().sendMessage(new TextComponent(senderInfo, message));
				player.spigot().sendMessage(new TextComponent(recieverInfo, message));

			} else {
				player.sendMessage(ChatColor.RED + username + " 이(가) 오프라인 입니다");
			}

		} else {
			player.sendMessage(ChatColor.RED + username + " 은(는) 존재하지 않는 유저입니다");
		}
	}

	public static String gradient(String text, ChatColor start, ChatColor end) {
		
		text = ChatColor.stripColor(text);

		Color color1 = start.getColor();
		Color color2 = end.getColor();

		float rStart = color1.getRed();
		float gStart = color1.getGreen();
		float bStart = color1.getBlue();

		float rEnd = color2.getRed();
		float gEnd = color2.getGreen();
		float bEnd = color2.getBlue();

		float rMath = (rEnd - rStart) / text.length();
		float gMath = (gEnd - gStart) / text.length();
		float bMath = (bEnd - bStart) / text.length();

		String[] chars = text.split("");
		StringBuilder newText = new StringBuilder();
		int index = 0;

		for (String letter : chars) {

			float r = rStart + (rMath * index);
			float g = gStart + (gMath * index);
			float b = bStart + (bMath * index);
			Color color = new Color(r / 255, g / 255, b / 255);
			newText.append(ChatColor.of(color)).append(letter);
			index++;
		}

		return newText.toString();
	}

	public static String gradient(String text, List<ChatColor> colors) {
		
		text = ChatColor.stripColor(text).strip();

		int divisions = colors.size() - 1;
		float divideEveryChars = text.length() / divisions > 0 ? (float) text.length() / divisions : 1;
		List<String> substrings = new ArrayList<>();
		StringBuilder finalText = new StringBuilder();

		for (float i = 0; i <= text.length() + divideEveryChars; i += divideEveryChars) {

			if (i + divideEveryChars > text.length() && text.length() > 0) {
				int lastSub = substrings.size() - 1;
				String latestStr = substrings.get(lastSub);
				substrings.set(lastSub, latestStr + text.substring(Math.round(i)));
				break;
			}

			String sub = text.substring(Math.round(i), Math.round(i + divideEveryChars));
			substrings.add(sub);

		}

		int color = 0;
		for (String s : substrings) {

			ChatColor color1;
			ChatColor color2;

			try {
				color1 = colors.get(color);
				color2 = colors.get(color + 1);
			} catch (IndexOutOfBoundsException e) {
				color1 = colors.get(colors.size() - 1);
				color2 = colors.get(colors.size() - 1);
			}

			finalText.append(gradient(s, color1, color2));

			color++;
		}

		return finalText.toString();
	}
}
