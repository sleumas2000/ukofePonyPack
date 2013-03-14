package com.gmail.ripppony.ukofePonyPack;

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