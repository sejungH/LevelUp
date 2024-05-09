package com.levelup.player;

import java.time.LocalDateTime;
import java.util.UUID;

import com.levelup.chat.ChatType;

public class PlayerData {

	private UUID uuid;
	private String username;
	private int balance;
	private int village;
	private LocalDateTime lastOnline;
	private ChatType chatType;

	public PlayerData(UUID uuid, String username, int balance, int village, LocalDateTime lastOnline) {
		this.uuid = uuid;
		this.username = username;
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
	
	public void setLastOnline(LocalDateTime lastOnline) {
		this.lastOnline = lastOnline;
	}

	public ChatType getChatType() {
		return chatType;
	}

	public void setChatType(ChatType chatType) {
		this.chatType = chatType;
	}

	@Override
	public String toString() {
		return "PlayerData [uuid=" + uuid + ", username=" + username + ", balance=" + balance + ", village=" + village
				+ ", lastOnline=" + lastOnline + "]";
	}

}
