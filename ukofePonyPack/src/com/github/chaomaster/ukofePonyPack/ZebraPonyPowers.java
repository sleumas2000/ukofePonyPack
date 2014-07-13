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

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.potion.PotionEffect;

public class ZebraPonyPowers extends PonyPowers {

	public ZebraPonyPowers(ukofePonyPack plugin) {
		super(plugin);
	}

	public boolean reloadConfig() {
		File configFile = new File(this.plugin.getDataFolder(),
				"ZebraConfig.yml");
		YamlConfiguration config = YamlConfiguration
				.loadConfiguration(configFile);
		return super.reloadConfig(config);
	}

	@EventHandler
	public void onPotionSplashEvent(PotionSplashEvent event) {
		if (isOfActiveType((Entity) event.getPotion().getShooter())) {
			potionSplash(event);
		}
	}

	private void potionSplash(PotionSplashEvent event) {
		for (PotionEffect e : event.getPotion().getEffects()) {
			PotionEffect toGive = new PotionEffect(e.getType(),
					e.getDuration(), e.getAmplifier() + 1);
			for (LivingEntity p : event.getAffectedEntities()) {
				p.addPotionEffect(toGive, true);
			}
		}
		event.setCancelled(true);
	}
}
