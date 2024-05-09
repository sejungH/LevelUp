package com.levelup.village;

import java.util.Arrays;
import java.util.UUID;

public class VillageData {
	private int id;
	private String name;
	private UUID president;
	private int[] spawn;
	
	public VillageData(int id, String name, UUID president, int[] spawn) {
		super();
		this.id = id;
		this.name = name;
		this.president = president;
		this.spawn = spawn;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UUID getPresident() {
		return president;
	}

	public void setPresident(UUID president) {
		this.president = president;
	}

	public int[] getSpawn() {
		return spawn;
	}

	public void setSpawn(int[] spawn) {
		this.spawn = spawn;
	}

	@Override
	public String toString() {
		return "VillageData [id=" + id + ", name=" + name + ", president=" + president + ", spawn="
				+ Arrays.toString(spawn) + "]";
	}
	
}
