/**
 * Copyright (C) 2013 chao-master
 * 
 * This file is part of ukofePonyPack.
 * 
 *     ukofePonyPack is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     ukofePonyPack is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with ukofePonyPack.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.chaomaster.ukofePonyPack.spells;

import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.github.chaomaster.ukofePonyPack.UnicornPonyPowers;

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