package com.gmail.ripppony.ukofePonyPack.spells;

import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.gmail.ripppony.ukofePonyPack.UnicornPonyPowers;

public class RockVisionCasting extends ChargedSpell {
	final int VISION_DISTANCE;
	final Material VISION_BLOCK;
	final HashSet<Material> REPLACE_LIST;
	Location lastLoc;
	HashSet<Location> glassSet;

	public RockVisionCasting(Player caster, UnicornPonyPowers handler,
			int VISION_DISTANCE, int durationTicks, Material VISION_BLOCK,
			HashSet<Material> REPLACE_LIST) {
		super(caster, handler, null);
		this.VISION_DISTANCE = VISION_DISTANCE;
		this.VISION_BLOCK = VISION_BLOCK;
		this.REPLACE_LIST = REPLACE_LIST;
		this.hungerCost = 0;
		this.totalSteps = (durationTicks / 10);
		this.glassSet = new HashSet<Location>();
	}

	public void start() {
		super.start();
		step();
		this.lastLoc = this.caster.getLocation().getBlock().getLocation();
	}

	public void step() {
		super.step();
		HashSet<Location> newGlass = new HashSet<Location>();
		HashSet<Location> deGlass = this.glassSet;
		Location center = this.caster.getLocation().getBlock().getLocation();
		if (!center.equals(this.lastLoc)) {
			for (int x = -this.VISION_DISTANCE; x <= this.VISION_DISTANCE; x++) {
				for (int y = -this.VISION_DISTANCE; y <= this.VISION_DISTANCE; y++) {
					for (int z = -this.VISION_DISTANCE; z <= this.VISION_DISTANCE; z++) {
						Location lo = center.clone().add(x, y, z);
						if (this.REPLACE_LIST.contains(lo.getBlock().getType())) {
							newGlass.add(lo);
							deGlass.remove(lo);
						}
					}
				}
			}
			deGlass(deGlass);
			glass(newGlass);

			this.glassSet = ((HashSet<Location>) newGlass.clone());
			this.lastLoc = this.caster.getLocation().getBlock().getLocation();
		}
	}

	private void deGlass(HashSet<Location> toReset) {
		for (Location l : toReset)
			this.caster.sendBlockChange(l, l.getBlock().getType(), l.getBlock()
					.getData());
	}

	private void glass(HashSet<Location> toReset) {
		for (Location l : toReset)
			this.caster.sendBlockChange(l, this.VISION_BLOCK, (byte) 0);
	}

	public void clear() {
	}

	public void cast() {
		deGlass(this.glassSet);
	}
}