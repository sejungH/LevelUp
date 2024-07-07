package com.levelup.village;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VillageData {

	private final String PATTERN = "<(.+)>(.+)</(.+)>";

	private int id;
	private String name;
	private UUID president;
	private int[] spawn;
	private int lastTax;
	private LocalDate lastTaxPaid;
	private LocalDate deletionPeriod;

	public VillageData(int id, String name, UUID president, int[] spawn, int lastTax, LocalDate lastTaxPaid,
			LocalDate deletionPeriod) {
		this.id = id;
		this.name = name;
		this.president = president;
		this.spawn = spawn;
		this.lastTax = lastTax;
		this.lastTaxPaid = lastTaxPaid;
		this.deletionPeriod = deletionPeriod;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		Pattern pattern = Pattern.compile(PATTERN);
		Matcher matcher = pattern.matcher(name);
		if (matcher.find()) {
			return matcher.group(2);
		} else {
			return name;
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColor() {
		Pattern pattern = Pattern.compile(PATTERN);
		Matcher matcher = pattern.matcher(name);
		if (matcher.find()) {
			return matcher.group(1);
		} else {
			return null;
		}
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

	public int getLastTax() {
		return lastTax;
	}

	public void setLastTax(int lastTax) {
		this.lastTax = lastTax;
	}

	public LocalDate getLastTaxPaid() {
		return lastTaxPaid;
	}

	public void setLastTaxPaid(LocalDate lastTaxPaid) {
		this.lastTaxPaid = lastTaxPaid;
	}

	public LocalDate getDeletionPeriod() {
		return deletionPeriod;
	}

	public void setDeletionPeriod(LocalDate deletionPeriod) {
		this.deletionPeriod = deletionPeriod;
	}

	@Override
	public String toString() {
		return "VillageData [id=" + id + ", name=" + name + ", president=" + president + ", spawn="
				+ Arrays.toString(spawn) + "]";
	}

}
