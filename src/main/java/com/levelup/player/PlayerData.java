package com.levelup.player;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.levelup.chat.ChatType;

public class PlayerData {
	
	private final String PATTERN = "<(.+)>(.+)</(.+)>";

	private UUID uuid;
	private String username;
	private String nickname;
	private int balance;
	private int village;
	private LocalDateTime lastOnline;
	private ChatType chatType;

	public PlayerData(UUID uuid, String username, String nickname, int balance, int village, LocalDateTime lastOnline) {
		this.uuid = uuid;
		this.username = username;
		this.nickname = nickname;
		this.balance = balance;
		this.village = village;
		this.lastOnline = lastOnline;
		this.chatType = ChatType.DEFAULT;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

	public int getVillage() {
		return village;
	}

	public void setVillage(int village) {
		this.village = village;
	}

	public LocalDateTime getLastOnline() {
		return lastOnline;
	}
	
	public String getLastOnlineAsString() {
		LocalDateTime now = LocalDateTime.now();
		
		long years = ChronoUnit.YEARS.between(lastOnline, now);
		long months = ChronoUnit.MONTHS.between(lastOnline, now);
		long days = ChronoUnit.DAYS.between(lastOnline, now);
		long hours = ChronoUnit.HOURS.between(lastOnline, now);
		long minutes = ChronoUnit.MINUTES.between(lastOnline, now);
		long seconds = ChronoUnit.SECONDS.between(lastOnline, now);
		
		String lastOnline = "";
		if (years > 0) {
			lastOnline = years + "년 전";

		} else if (months > 0) {
			lastOnline = months + "달 전";

		} else if (days > 0) {
			lastOnline = days + "일 전";

		} else if (hours > 0) {
			lastOnline = hours + "시간 전";

		} else if (minutes > 0) {
			lastOnline = minutes + "분 전";

		} else if (seconds > 0) {
			lastOnline = seconds + "초 전";
		}
		
		return lastOnline;
	}

	public void setLastOnline(LocalDateTime lastOnline) {
		this.lastOnline = lastOnline;
	}

	public ChatType getChatType() {
		return chatType;
	}

	public void setChatType(ChatType chatType) {
		this.chatType = chatType;
	}

	public String getName() {
		if (this.nickname == null) {
			return this.username;
		} else {
			return this.getNicknameWithoutColor();
		}
	}
	
	public String getNicknameColor() {
		if (this.nickname != null) {
			Pattern pattern = Pattern.compile(PATTERN);
			Matcher matcher = pattern.matcher(this.nickname);
			if (matcher.find()) {
				return matcher.group(1);
			}
		}
		
		return null;
	}
	
	public String getNicknameWithoutColor() {
		if (this.nickname != null) {
			Pattern pattern = Pattern.compile(PATTERN);
			Matcher matcher = pattern.matcher(this.nickname);
			if (matcher.find()) {
				return matcher.group(2);
			}
		}
		
		return this.nickname;
	}

	@Override
	public String toString() {
		return "PlayerData [uuid=" + uuid + ", username=" + username + ", nickname=" + nickname + ", balance=" + balance
				+ ", village=" + village + ", lastOnline=" + lastOnline + "]";
	}

}
