package com.levelup.tool;

import java.util.List;

public class ToolQuestMessage {

	private int level;
	private List<String> quest;
	private List<String> complete;

	public ToolQuestMessage(int level, List<String> quest, List<String> complete) {
		this.level = level;
		this.quest = quest;
		this.complete = complete;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public List<String> getQuest() {
		return quest;
	}

	public void setQuest(List<String> quest) {
		this.quest = quest;
	}

	public List<String> getComplete() {
		return complete;
	}

	public void setComplete(List<String> complete) {
		this.complete = complete;
	}

}
