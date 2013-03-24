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

import com.github.chaomaster.ukofePonyPack.UnicornPonyPowers;

import java.util.HashSet;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class RockVisionCharging extends ChargedSpell {
	private int VISION_DISTANCE = 5;
	private int DURATION_TICKS = 600;
	private boolean DISABLED = false;
	private HashSet<Material> REPLACE_LIST = new HashSet<Material>();

	private final Material CHARGING_BLOCK = Material.ICE;
	private final Material VISION_BLOCK = Material.GLASS;

	public RockVisionCharging(Player caster, UnicornPonyPowers handler,
			ConfigurationSection setup) {
		super(caster, handler, setup);
		if (setup != null) {
			if (setup.isInt("VisionDistance")) {
				this.VISION_DISTANCE = setup.getInt("VisionDistance");
			}
			if (setup.isInt("DurationTicks")) {
				this.DURATION_TICKS = setup.getInt("DurationTicks");
			}
			if (setup.isBoolean("Disabled")) {
				this.DISABLED = setup.getBoolean("Disabled");
			}
			if (setup.isString("ReplaceList")) {
				String[] blocks = setup.getString("ReplaceList").split(",");
				for (int i = 0; i < blocks.length; i++)
					this.REPLACE_LIST.add(Material.getMaterial(Integer
							.parseInt(blocks[i])));
			} else {
				this.REPLACE_LIST.add(Material.STONE);
			}
		} else {
			this.REPLACE_LIST.add(Material.STONE);
		}
	}

	public void start() {
		if (!this.DISABLED)
			super.start();
		else
			removeSelf();
	}

	public void step() {
		super.step();
		int range = this.step * this.VISION_DISTANCE / this.totalSteps;
		for (int x = -range; x <= range; x++)
			for (int y = -range; y <= range; y++)
				for (int z = -range; z <= range; z++) {
					Location lo = this.caster.getLocation().add(x, y, z);
					if (this.REPLACE_LIST.contains(lo.getBlock().getType()))
						this.caster.sendBlockChange(lo, this.CHARGING_BLOCK,
								(byte) 0);
				}
	}

	public void clear() {
		for (int x = -this.VISION_DISTANCE; x <= this.VISION_DISTANCE; x++)
			for (int y = -this.VISION_DISTANCE; y <= this.VISION_DISTANCE; y++)
				for (int z = -this.VISION_DISTANCE; z <= this.VISION_DISTANCE; z++) {
					Location lo = this.caster.getLocation().add(x, y, z);
					this.caster.sendBlockChange(lo, lo.getBlock().getType(), lo
							.getBlock().getData());
				}
	}

	public void cast() {
		removeSelf();
		new RockVisionCasting(this.caster, this.handler, this.VISION_DISTANCE,
				this.DURATION_TICKS, this.VISION_BLOCK, this.REPLACE_LIST)
				.start();
	}
}