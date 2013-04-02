/**
 * Copyright (C) 2013 chao-master
 * 
 * This file is part of ukofePonyPack.
 * 
 *     Foobar is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     Foobar is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.chaomaster.ukofePonyPack.spells;

import java.util.HashSet;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

import com.github.chaomaster.ukofePonyPack.PonyType;
import com.github.chaomaster.ukofePonyPack.UnicornPonyPowers;

public class TeleportSpell extends ChargedSpell {
	private int DEVIATION = 0;
	private int GROUP_RANGE = 1;
	private int GROUP_DEVIATION = 0;
	private HashSet<Environment> ALLOWEDENVIRONMENTS = new HashSet<Environment>();
	private boolean ALLOWCROSSWORLD = false;
	private Location target;
	private Random rand;

	public TeleportSpell(Player caster, UnicornPonyPowers handler,
			MapView target, ConfigurationSection setup) {
		super(caster, handler, setup);
		if (setup != null) {
			if (setup.isInt("Deviation")) {
				this.DEVIATION = setup.getInt("Deviation");
			}
			if (setup.isInt("GroupRange")) {
				this.GROUP_RANGE = setup.getInt("GroupRange");
			}
			if (setup.isInt("GroupDeviation")) {
				this.GROUP_DEVIATION = setup.getInt("GroupDeviation");
			}
			if (setup.isList("AllowedEnvironments")) {
				for (Object t : setup.getList("AllowedEnvironments")) {
					this.ALLOWEDENVIRONMENTS.add(Environment
							.valueOf(((String) t).toUpperCase()));
				}
			}
			if (setup.isBoolean("AllowCrossWorld")) {
				this.ALLOWCROSSWORLD = setup.getBoolean("AllowCrossWorld");
			}
		}
		this.rand = new Random();
		int targetX = target.getCenterX()
				+ this.rand.nextInt(this.DEVIATION * 2 + 1) - this.DEVIATION;
		int targetZ = target.getCenterZ()
				+ this.rand.nextInt(this.DEVIATION * 2 + 1) - this.DEVIATION;
		int targetY = target.getWorld().getHighestBlockYAt(targetX, targetZ);
		this.target = new Location(target.getWorld(), targetX, targetY, targetZ);
		if (!this.ALLOWEDENVIRONMENTS.contains(this.target.getWorld()
				.getEnvironment())) {
			this.caster.sendMessage("You cannot teleport to that kind of relm");
			removeSelf();
			return;
		}
		if (!this.ALLOWCROSSWORLD
				&& !caster.getLocation().getWorld().equals(target.getWorld())) {
			this.caster
					.sendMessage("You must be in the same world as the map's target to teleport.");
			removeSelf();
			return;
		}
	}

	public void step() {
		super.step();
		Location center = this.caster.getLocation();
		int distSquared = this.GROUP_RANGE * this.GROUP_RANGE;
		for (Player p : this.handler.getPlugin().getServer().getOnlinePlayers())
			if ((p.getWorld() == center.getWorld())
					&& (p.getLocation().distanceSquared(center) <= distSquared))
				p.playEffect(p.getLocation().add(0.0D, 1.0D, 0.0D),
						this.castingEffect, this.castingEffectData);
	}

	public void cast() {
		Location from = this.caster.getLocation();
		int distSquared = this.GROUP_RANGE * this.GROUP_RANGE;
		for (Player p : this.handler.getPlugin().getServer().getOnlinePlayers()) {
			Location tTo = null;
			int counter = 0;
			do {
				if ((p.getWorld() == from.getWorld())
						&& (p.getLocation().distanceSquared(from) <= distSquared)) {
					int localX = this.target.getBlockX()
							+ this.rand.nextInt(this.GROUP_DEVIATION * 2 + 1)
							- this.GROUP_DEVIATION;
					int localZ = this.target.getBlockZ()
							+ this.rand.nextInt(this.GROUP_DEVIATION * 2 + 1)
							- this.GROUP_DEVIATION;
					int localY = this.target.getWorld().getHighestBlockYAt(localX,
							localZ)
							+ this.rand.nextInt(this.GROUP_DEVIATION + 1) + 1;
					
					tTo = new Location(this.target.getWorld(), localX, localY, localZ);
					counter++;
				}
			} while (!tTo.getBlock().isEmpty() && counter<1000);
			if (tTo.getBlock().isEmpty()){
				p.teleport(tTo);
			} else {
				p.sendMessage("Teleport failed");
			}
		}
		this.target.getWorld().strikeLightningEffect(this.target);
		removeSelf();
	}

	public void clear() {
	}
}