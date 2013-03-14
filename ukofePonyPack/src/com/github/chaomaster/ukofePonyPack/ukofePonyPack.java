package com.github.chaomaster.ukofePonyPack;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ukofePonyPack extends JavaPlugin {
	public PonyChecker checker = null;

	public void onEnable() {
		this.checker = new PonyChecker(this);
		new EarthPonyPowers(this);
		new UnicornPonyPowers(this);
		new PegasusPonyPowers(this);
	}

	public void onDisable() {
		this.checker.savePlayerConfig();

		for (Player p : getServer().getOnlinePlayers())
			p.removeMetadata("ponyType", this);
	}
}