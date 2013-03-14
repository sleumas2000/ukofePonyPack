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