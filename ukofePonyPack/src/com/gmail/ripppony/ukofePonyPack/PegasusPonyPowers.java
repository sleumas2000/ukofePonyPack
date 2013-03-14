package com.gmail.ripppony.ukofePonyPack;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
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
					if (!p.isFlying()) {
						entries.remove();
					} else {
						p.setExhaustion((float) (p.getExhaustion()
								+ l.distance(p.getLocation())
								* PegasusPonyPowers.this.EXHAUSTION_PER_METER + PegasusPonyPowers.this.EXHAUSTION_PER_SECOND));
						entry.setValue(p.getLocation());
					}
				}
			}
		};
		this.flightTimerChecker.runTaskTimer(plugin, 20L, 20L);
	}

	public void reloadConfig() {
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
		if (config.isInt("FoodNeededToFly"))
			this.FOOD_NEEDED_TO_FLY = config.getInt("FoodNeededToFly");
	}

	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		if (this.plugin.checker.getType(event.getPlayer()) == PonyType.PEGASUS)
			if (event.getPlayer().getFoodLevel() < this.FOOD_NEEDED_TO_FLY)
				event.getPlayer().setAllowFlight(false);
			else
				event.getPlayer().setAllowFlight(true);
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
	public void onFoodLevelChangeEvent(FoodLevelChangeEvent event) {
		if ((event.getEntity() instanceof Player)) {
			Player target = (Player) event.getEntity();
			if (this.plugin.checker.getType(target) == PonyType.PEGASUS)
				if (event.getFoodLevel() < this.FOOD_NEEDED_TO_FLY)
					target.setAllowFlight(false);
				else
					target.setAllowFlight(true);
		}
	}

	@EventHandler
	public void onPlayerToggleFlightEvent(PlayerToggleFlightEvent event) {
		if (this.plugin.checker.getType(event.getPlayer()) == PonyType.PEGASUS)
			if (event.isFlying())
				this.lastFlightPos.put(event.getPlayer(), event.getPlayer()
						.getLocation());
			else if (this.lastFlightPos.containsKey(event.getPlayer()))
				this.lastFlightPos.remove(event.getPlayer());
	}

	@EventHandler
	public void onPlayerGameModeChangeEvent(PlayerGameModeChangeEvent event) {
		if (event.getNewGameMode() == GameMode.CREATIVE) {
			if (this.lastFlightPos.containsKey(event.getPlayer()))
				this.lastFlightPos.remove(event.getPlayer());
		} else
			this.plugin.checker.getType(event.getPlayer());
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (this.lastFlightPos.containsKey(event.getPlayer()))
			this.lastFlightPos.put(event.getPlayer(), event.getTo());
	}

	@EventHandler
	public void onEntityDamageEvent(EntityDamageEvent event) {
		if (((event.getEntity() instanceof Player))
				&& (this.plugin.checker.getType((Player) event.getEntity()) == PonyType.PEGASUS)
				&& (event.getCause() == EntityDamageEvent.DamageCause.FALL))
			event.setDamage(0);
	}
}