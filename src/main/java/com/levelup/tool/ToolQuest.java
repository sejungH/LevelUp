package com.levelup.tool;

import java.util.Map;

import org.bukkit.Material;

public class ToolQuest {

	private int level;
	private int exp;
	private int required;
	private Map<Material, Integer> quest;

	public ToolQuest(int level, int exp, int required, Map<Material, Integer> quest) {
		this.level = level;
		this.exp = exp;
		this.required = required;
		this.quest = quest;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getRequired() {
		return required;
	}

	public void setRequired(int required) {
		this.required = required;
	}

	public Map<Material, Integer> getQuest() {
		return quest;
	}

	public void setQuest(Map<Material, Integer> quest) {
		this.quest = quest;
	}

	@Override
	public String toString() {
		return "ToolQuest [level=" + level + ", exp=" + exp + ", required=" + required + ", quest=" + quest.toString()
				+ "]";
	}

}
