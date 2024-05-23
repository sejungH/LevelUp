package com.levelup.tool;

public class ToolData {

	private PickaxeData pickaxe;
	private AxeData axe;
	private SwordData sword;
	private ShovelData shovel;
	
	public ToolData() {
		this.pickaxe = null;
		this.axe = null;
		this.sword = null;
		this.shovel = null;
	}

	public ToolData(PickaxeData pickaxe, AxeData axe, SwordData sword, ShovelData shovel) {
		this.pickaxe = pickaxe;
		this.axe = axe;
		this.sword = sword;
		this.shovel = shovel;
	}

	public PickaxeData getPickaxe() {
		return pickaxe;
	}

	public void setPickaxe(PickaxeData pickaxe) {
		this.pickaxe = pickaxe;
	}

	public AxeData getAxe() {
		return axe;
	}

	public void setAxe(AxeData axe) {
		this.axe = axe;
	}

	public SwordData getSword() {
		return sword;
	}

	public void setSword(SwordData sword) {
		this.sword = sword;
	}

	public ShovelData getShovel() {
		return shovel;
	}

	public void setShovel(ShovelData shovel) {
		this.shovel = shovel;
	}

}
