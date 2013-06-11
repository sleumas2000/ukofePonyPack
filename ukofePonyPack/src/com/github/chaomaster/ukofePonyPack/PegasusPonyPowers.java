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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class PegasusPonyPowers extends PonyPowers {
	private double EXHAUSTION_PER_METER = 0.22D;
	private double EXHAUSTION_PER_SECOND = 0.1D;
	private int FOOD_NEEDED_TO_FLY = 6;
	private HashMap<Player, Location> lastFlightPos;
	private BukkitRunnable flightTimerChecker;

	public PegasusPonyPowers(ukofePonyPack plugin) {
		super(plugin);
		this.lastFlightPos = new HashMap<Player, Location>();
		this.flightTimerChecker = new BukkitRunnable() {
			public void run() {
				Iterator<?> entries = PegasusPonyPowers.this.lastFlightPos
						.entrySet().iterator();
				while (entries.hasNext()) {
					Map.Entry entry = (Map.Entry) entries.next();
					Player p = (Player) entry.getKey();
					Location l = (Location) entry.getValue();
					if (!p.isFlying() || p.getGameMode() == GameMode.CREATIVE) {
						entries.remove();
					} else {
						if (l.getWorld().equals(p.getWorld())) {
							p.setExhaustion((float) (p.getExhaustion()
									+ l.distance(p.getLocation())
									* PegasusPonyPowers.this.EXHAUSTION_PER_METER + PegasusPonyPowers.this.EXHAUSTION_PER_SECOND));
						}
						entry.setValue(p.getLocation());
					}
				}
			}
		};
		this.flightTimerChecker.runTaskTimer(plugin, 20L, 20L);
	}

	public void checkAssignFlight(Entity entity, int newFoodLevel) {
		if (isOfActiveType(entity)) {
			Player player = (Player) entity;
			if (newFoodLevel < this.FOOD_NEEDED_TO_FLY
					|| player.getGameMode() == GameMode.CREATIVE) {
				player.setAllowFlight(false);
			} else {
				player.setAllowFlight(true);
			}
		}
	}

	public void checkAssignFlight(Entity entity) {
		if (isOfActiveType(entity)) {
			checkAssignFlight((Player) entity, ((Player) entity).getFoodLevel());
		}
	}

	public boolean reloadConfig() {
		File configFile = new File(this.plugin.getDataFolder(),
				"PegasusConfig.yml");
		YamlConfiguration config = YamlConfiguration
				.loadConfiguration(configFile);

		if (config.isDouble("ExaustionPerMeter")) {
			this.EXHAUSTION_PER_METER = config.getDouble("ExaustionPerMeter");
		}
		if (config.isDouble("ExaustionPerSecond")) {
			this.EXHAUSTION_PER_SECOND = config.getDouble("ExaustionPerSecond");
		}
		if (config.isInt("FoodNeededToFly")) {
			this.FOOD_NEEDED_TO_FLY = config.getInt("FoodNeededToFly");
		}
		return super.reloadConfig(config);
	}

	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		checkAssignFlight(event.getPlayer());
	}

	@EventHandler
	public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
		checkAssignFlight(event.getPlayer());
	}

	@EventHandler
	public void onFoodLevelChangeEvent(FoodLevelChangeEvent event) {
		checkAssignFlight(event.getEntity(), event.getFoodLevel());
	}

	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		event.getPlayer().setAllowFlight(false);
	}

	@EventHandler
	public void onPlayerKickEvent(PlayerKickEvent event) {
		event.getPlayer().setAllowFlight(false);
	}

	@EventHandler
	public void onPlayerToggleFlightEvent(PlayerToggleFlightEvent event) {
		if (isOfActiveType(event.getPlayer()))
			if (event.isFlying()) {
				this.lastFlightPos.put(event.getPlayer(), event.getPlayer()
						.getLocation());
			} else if (this.lastFlightPos.containsKey(event.getPlayer())) {
				this.lastFlightPos.remove(event.getPlayer());
			}
	}

	@EventHandler
	public void onPlayerGameModeChangeEvent(PlayerGameModeChangeEvent event) {
		if (event.getNewGameMode() == GameMode.CREATIVE) {
			if (this.lastFlightPos.containsKey(event.getPlayer())) {
				this.lastFlightPos.remove(event.getPlayer());
			}
		}
		// TODO, UNSURE HOW, if able to fly reassign when exiting creative
		// At current the system seems to handle the flight removal after.
		// Might be able to completely redo the assignment.
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (isOfActiveType(event.getPlayer())){
			if (this.lastFlightPos.containsKey(event.getPlayer())) {
				this.lastFlightPos.put(event.getPlayer(), event.getTo());
			}
			if (event.getCause() == TeleportCause.ENDER_PEARL) {
				int E = 0;
				for (ItemStack i:event.getPlayer().getEquipment().getArmorContents()){
					int p = i.getEnchantmentLevel(Enchantment.PROTECTION_ENVIRONMENTAL);
					int f = i.getEnchantmentLevel(Enchantment.PROTECTION_FALL);
					E += (int) ((6+p*p)*0.75/3) + (int) ((6+f*f)*2.5/3);
				}
				if (E>25){E=25;}
				E = (int) ((rand.nextDouble()/2+0.5)*E);
				if (E>20){E=20;}
				event.getPlayer().damage((int) ((1-(E*0.04))*5));
			}
		}
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if (this.lastFlightPos.containsKey(player)) {
				this.lastFlightPos.remove(player);
				player.setAllowFlight(false);
			}
		}
	}

	@EventHandler
	public void onEntityDamageEvent(EntityDamageEvent event) {
		if (isOfActiveType(event.getEntity())
			&& (event.getCause() == EntityDamageEvent.DamageCause.FALL)) {
			event.setDamage(0);
		}
	}

	/*
	 * public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event)
	 * { if (isOfActiveType(event.getDamager())) { if (event.getCause() !=
	 * EntityDamageEvent.DamageCause.PROJECTILE){ event.critical??? #TODO
	 * Critical detection, limitation in bukkit } } }
	 */
}