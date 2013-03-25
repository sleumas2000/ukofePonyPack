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

package com.github.chaomaster.ukofePonyPack;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.potion.PotionEffect;

public class ZebraPonyPowers extends PonyPowers {

	public ZebraPonyPowers(ukofePonyPack plugin) {
		super(plugin);
	}

	@Override
	public void reloadConfig() {
		// TODO identify and load config.
	}

	@EventHandler
	public void onPotionSplashEvent(PotionSplashEvent event) {
		Entity tThrower = event.getPotion().getShooter();
		Player thrower;
		if (tThrower instanceof Player) {
			thrower = (Player) tThrower;
			if (this.plugin.checker.getType(thrower) == PonyType.ZEBRA) {
				potionSplash(event);
			}
		}
	}

	private void potionSplash(PotionSplashEvent event) {
		for (PotionEffect e : event.getPotion().getEffects()) {
			PotionEffect toGive = new PotionEffect(e.getType(),
					e.getDuration(), e.getAmplifier() + 1);
			for (LivingEntity p : event.getAffectedEntities()) {
				p.addPotionEffect(toGive,true);
			}
		}
		event.setCancelled(true);
	}
}
