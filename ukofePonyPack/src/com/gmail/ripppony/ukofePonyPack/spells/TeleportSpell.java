package com.gmail.ripppony.ukofePonyPack.spells;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

import com.gmail.ripppony.ukofePonyPack.UnicornPonyPowers;

public class TeleportSpell extends ChargedSpell {
	private int DEVIATION = 0;
	private int GROUP_RANGE = 1;
	private int GROUP_DEVIATION = 0;
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
		}
		this.rand = new Random();
		int targetX = target.getCenterX()
				+ this.rand.nextInt(this.DEVIATION * 2 + 1) - this.DEVIATION;
		int targetZ = target.getCenterZ()
				+ this.rand.nextInt(this.DEVIATION * 2 + 1) - this.DEVIATION;
		int targetY = target.getWorld().getHighestBlockYAt(targetX, targetZ);
		this.target = new Location(target.getWorld(), targetX, targetY, targetZ);
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
				p.teleport(new Location(this.target.getWorld(), localX, localY,
						localZ));
			}
		}
		this.target.getWorld().strikeLightningEffect(this.target);
		removeSelf();
	}

	public void clear() {
	}
}