package com.levelup.friend;

import java.util.UUID;

public class FriendData {

	private UUID fromPlayer;
	private UUID toPlayer;
	private boolean areFriends;

	public FriendData(UUID fromPlayer, UUID toPlayer, boolean areFriends) {
		this.fromPlayer = fromPlayer;
		this.toPlayer = toPlayer;
		this.areFriends = areFriends;
	}

	public UUID getFromPlayer() {
		return fromPlayer;
	}

	public void setFromPlayer(UUID fromPlayer) {
		this.fromPlayer = fromPlayer;
	}

	public UUID getToPlayer() {
		return toPlayer;
	}

	public void setToPlayer(UUID toPlayer) {
		this.toPlayer = toPlayer;
	}

	public boolean areFriends() {
		return areFriends;
	}

	public void setAreFriends(boolean areFriends) {
		this.areFriends = areFriends;
	}

	@Override
	public String toString() {
		return "FriendData [fromPlayer=" + fromPlayer + ", toPlayer=" + toPlayer + ", areFriends=" + areFriends + "]";
	}

}
