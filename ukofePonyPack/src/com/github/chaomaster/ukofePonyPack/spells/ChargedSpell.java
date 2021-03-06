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

import org.bukkit.Effect;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class ChargedSpell extends BukkitRunnable {
	protected static final int INTERVAL = 10;
	protected int step;
	protected int totalSteps = 10;
	protected int hungerCost = 5;
	protected Effect castingEffect;
	protected byte castingEffectData;
	protected Player caster;
	protected UnicornPonyPowers handler;
	protected boolean disabled = false;

	public ChargedSpell(Player caster, UnicornPonyPowers handler,
			ConfigurationSection setup) {
		if (setup != null) {
			if (setup.isInt("ChargeTicks")) {
				this.totalSteps = (setup.getInt("ChargeTicks") / 10);
			}
			if (setup.isInt("HungerCost")) {
				this.hungerCost = setup.getInt("HungerCost");
			}
		}
		this.castingEffect = Effect.MOBSPAWNER_FLAMES;
		this.castingEffectData = 0;
		this.caster = caster;
		this.handler = handler;
	}
	
	public boolean isDisabled(){
		return disabled;
	}

	public void start() {
		this.step = 0;
		runTaskTimer(this.handler.getPlugin(), 10L, 10L);
	}

	public void run() {
		this.step += 1;
		step();
		if (this.step == this.totalSteps) {
			cast();
			cancel();
		}
	}

	public void cancel() {
		clear();
		super.cancel();
	}

	public void step() {
		int currentFood = this.caster.getFoodLevel();
		int diff = this.step * this.hungerCost / this.totalSteps
				- (this.step - 1) * this.hungerCost / this.totalSteps;
		this.caster.getWorld().playEffect(this.caster.getLocation(),
				this.castingEffect, this.castingEffectData);
		if (diff == 0)
			return;
		if (currentFood > 0)
			this.caster.setFoodLevel(currentFood - diff);
		else
			this.caster.setHealth(this.caster.getHealth() - diff);
	}

	protected void removeSelf() {
		this.handler.clearChargeSpell(this.caster);
	}

	public abstract void clear();

	public abstract void cast();
}