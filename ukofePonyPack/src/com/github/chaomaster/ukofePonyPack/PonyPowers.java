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

import java.util.HashSet;
import java.util.Random;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class PonyPowers implements Listener {
	protected ukofePonyPack plugin;
	protected final Random rand;

	private boolean DISABLED = false;
	protected HashSet<PonyType> ACTIVETYPES = new HashSet<PonyType>();

	public PonyPowers(ukofePonyPack plugin) {
		this.rand = new Random();
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public ukofePonyPack getPlugin() {
		return this.plugin;
	}

	public abstract boolean reloadConfig();
	
	protected boolean reloadConfig(YamlConfiguration config) {
		if (config.isBoolean("disabled")) {
			this.DISABLED = config.getBoolean("disabled");
		}
		if (config.isList("ponyTypes")) {
			this.ACTIVETYPES.clear();
			for (Object t : config.getList("ponyTypes")) {
				this.ACTIVETYPES.add(PonyType.valueOf(((String) t)
						.toUpperCase()));
			}
		}
		return this.DISABLED;
	}

	protected boolean isOfActiveType(Entity player) {
		return (player instanceof Player)
				&& (this.ACTIVETYPES.contains(this.plugin.checker
						.getType((Player) player)));
	}
}