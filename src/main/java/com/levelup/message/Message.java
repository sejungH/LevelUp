package com.levelup.message;

import java.time.LocalDateTime;
import java.util.UUID;

public class Message {
	private int id;
	private UUID uuid;
	private String message;
	private LocalDateTime datetime;
	private boolean isRead;

	public Message(int id, UUID uuid, String message, LocalDateTime datetime, boolean isRead) {
		this.id = id;
		this.uuid = uuid;
		this.message = message;
		this.datetime = datetime;
		this.isRead = isRead;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LocalDateTime getDatetime() {
		return datetime;
	}

	public void setDatetime(LocalDateTime datetime) {
		this.datetime = datetime;
	}

	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}
}
