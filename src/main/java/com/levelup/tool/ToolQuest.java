package com.levelup.tool;

import java.util.Map;

import org.bukkit.Material;

public class ToolQuest {
	private int level;
	private int exp;
	private int required;
	private Map<Material, Integer> quests;

	public ToolQuest(int level, int exp, int required, Map<Material, Integer> quests) {
		this.level = level;
		this.exp = exp;
		this.required = required;
		this.quests = quests;
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

	public Map<Material, Integer> getQuests() {
		return quests;
	}

	public void setQuests(Map<Material, Integer> quests) {
		this.quests = quests;
	}

}