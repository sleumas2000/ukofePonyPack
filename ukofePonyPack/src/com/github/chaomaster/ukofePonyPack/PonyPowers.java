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

import java.util.Random;

import org.bukkit.event.Listener;

public abstract class PonyPowers implements Listener {
	protected ukofePonyPack plugin;
	protected final Random rand;

	public PonyPowers(ukofePonyPack plugin) {
		this.rand = new Random();
		this.plugin = plugin;
		reloadConfig();
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	public ukofePonyPack getPlugin() {
		return this.plugin;
	}

	public abstract void reloadConfig();
}