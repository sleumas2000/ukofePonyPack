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

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.map.MapView;

import com.github.chaomaster.ukofePonyPack.spells.ChargedSpell;
import com.github.chaomaster.ukofePonyPack.spells.RockVisionCharging;
import com.github.chaomaster.ukofePonyPack.spells.TeleportSpell;

public class UnicornPonyPowers extends PonyPowers {
	private ConfigurationSection TELEPORT_CONFIG = null;
	private ConfigurationSection ROCK_VISION_CONFIG = null;
	private HashMap<Player, ChargedSpell> spellMap;

	public UnicornPonyPowers(ukofePonyPack plugin) {
		super(plugin);
		this.spellMap = new HashMap<Player, ChargedSpell>();
	}

	public void reloadConfig() {
		File configFile = new File(this.plugin.getDataFolder(),
				"UnicornConfig.yml");
		YamlConfiguration config = YamlConfiguration
				.loadConfiguration(configFile);

		this.TELEPORT_CONFIG = config.getConfigurationSection("TeleportConfig");
		this.ROCK_VISION_CONFIG = config
				.getConfigurationSection("RockVisionConfig");
	}

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if ((this.plugin.checker.getType(event.getPlayer()) == PonyType.UNICORN)
				&& ((event.getAction() == Action.RIGHT_CLICK_AIR) || (event
						.getAction() == Action.RIGHT_CLICK_BLOCK)))
			if (event.getMaterial() == Material.MAP)
				mapTeleportStart(event.getPlayer());
			else if ((event.getMaterial() == Material.WOOD_PICKAXE)
					|| (event.getMaterial() == Material.STONE_PICKAXE)
					|| (event.getMaterial() == Material.IRON_PICKAXE)
					|| (event.getMaterial() == Material.GOLD_PICKAXE)
					|| (event.getMaterial() == Material.DIAMOND_PICKAXE))
				rockVisionStart(event.getPlayer());
	}

	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		if (this.plugin.checker.getType(event.getPlayer()) == PonyType.UNICORN) {
			Location from = event.getFrom();
			Location to = event.getTo();
			if ((from.getX() != to.getX()) || (from.getY() != to.getY())
					|| (from.getZ() != from.getZ()))
				clearChargeSpell(event.getPlayer());
		}
	}

	public void setChargeSpell(Player caster, ChargedSpell spell) {
		if (!this.spellMap.containsKey(caster)) {
			this.spellMap.put(caster, spell);
			spell.start();
		}
	}

	public void clearChargeSpell(Player caster) {
		if (this.spellMap.containsKey(caster)) {
			((ChargedSpell) this.spellMap.get(caster)).cancel();
			this.spellMap.remove(caster);
		}
	}

	private void mapTeleportStart(Player caster) {
		MapView target = this.plugin.getServer().getMap(
				caster.getItemInHand().getDurability());
		setChargeSpell(caster, new TeleportSpell(caster, this, target,
				this.TELEPORT_CONFIG));
	}

	private void rockVisionStart(Player caster) {
		Material pickType = caster.getItemInHand().getType();
		ConfigurationSection pickConfig = null;
		if (this.ROCK_VISION_CONFIG != null) {
			if (pickType == Material.WOOD_PICKAXE)
				pickConfig = this.ROCK_VISION_CONFIG
						.getConfigurationSection("Wood");
			else if (pickType == Material.STONE_PICKAXE)
				pickConfig = this.ROCK_VISION_CONFIG
						.getConfigurationSection("Stone");
			else if (pickType == Material.IRON_PICKAXE)
				pickConfig = this.ROCK_VISION_CONFIG
						.getConfigurationSection("Iron");
			else if (pickType == Material.GOLD_PICKAXE)
				pickConfig = this.ROCK_VISION_CONFIG
						.getConfigurationSection("Gold");
			else if (pickType == Material.DIAMOND_PICKAXE) {
				pickConfig = this.ROCK_VISION_CONFIG
						.getConfigurationSection("Diamond");
			}
		}
		setChargeSpell(caster, new RockVisionCharging(caster, this, pickConfig));
	}
}
