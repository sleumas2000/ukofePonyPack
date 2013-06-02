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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;

import javax.imageio.ImageIO;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.LazyMetadataValue;
import org.bukkit.metadata.MetadataValue;

public class PonyChecker implements Listener {
	private final ukofePonyPack plugin;
	private int CHANGE_COOLDOWN = 1;

	private FileConfiguration playerConfig = null;
	private File playerConfigFile = null;

	public PonyChecker(ukofePonyPack plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		triggerExpire(event.getPlayer());
		event.getPlayer().sendMessage(
				"You are " + getType(event.getPlayer()).getMessage());
	} 

	public PonyType getType(final Player player) {
		if (!player.hasMetadata("ponyType")) {
			player.setMetadata("ponyType", new LazyMetadataValue(this.plugin,
					new Callable<Object>() {
						public PonyType call() {
							return PonyChecker.this.checkPlayer(player
									.getName());
						}
					}));
		}
		return (PonyType) ((MetadataValue) player.getMetadata("ponyType")
				.get(0)).value();
	}

	public void triggerExpire(Player target) {
		if (target.hasMetadata("ponyType")) {
			target.getMetadata("ponyType").get(0).invalidate();
		}
	}

	public void triggerCacheExpire() {
		this.playerConfig = null;
	}

	public String getFullInfo(Player player) {
		PonyType activeType = getType(player);
		PlayerConfigInfo cacheInfo = fromPlayerConfig(player.getName());
		PonyType webType = webCheck(player.getName());
		return String
				.format("%s's Skin info:\nCurrent skin is %s\nCached skin is %s which %s\nWeb skin is %s",
						player.getName(), activeType.getMessage(),
						cacheInfo.type.getMessage(), cacheInfo.formatExpire(),
						webType.getMessage());
	}

	private PonyType webCheck(String playerName) {
		PonyType type;
		type = serverWebCheck(playerName,"http://www.minelittlepony.com/hd/skins/");
		if (type == PonyType.ERROR) {
			type = serverWebCheck(playerName,"http://s3.amazonaws.com/MinecraftSkins/");
		}
		return type;
	}
	
	private PonyType serverWebCheck(String playerName,String serverBase) {
		try {
			this.plugin.getLogger().info("Getting " + playerName + "'s skin from " + serverBase);

			URL skinUrl = new URL(serverBase + playerName + ".png");
			BufferedImage skin = ImageIO.read(skinUrl);

			switch (skin.getRGB(0, 0)) {
			case -413391:
				return PonyType.EARTH;
			case -7812368:
				return PonyType.PEGASUS;
			case -3039260:
				return PonyType.UNICORN;
			case -67076:
				return PonyType.ALICORN;
			case 0xFFD0CCCF:
				return PonyType.ZEBRA;
			case 0xFF282b29:
				return PonyType.CHANGELING;
			default:
				return PonyType.NONE;
			}
		} catch (MalformedURLException ex) {
			this.plugin.getLogger().info(
					"MalforumedURLException with " + playerName + "'s skin from " + serverBase);
			return PonyType.ERROR;
		} catch (IOException ex) {
			this.plugin.getLogger().info(
					"IOException with " + playerName + "'s skin from " + serverBase);
		}
		return PonyType.ERROR;
	}

	public PonyType checkPlayer(String playerName) {
		PlayerConfigInfo configInfo = fromPlayerConfig(playerName);
		if (configInfo.hasExpired()) {
			PonyType webType = webCheck(playerName);
			if (webType == PonyType.ERROR) {
				return configInfo.type;
			}
			if (webType.ignUn() == PonyType.NONE) {
				saveToPlayerConfig(playerName, new PlayerConfigInfo(webType, 0));
			} else if (webType != configInfo.type) {
				saveToPlayerConfig(playerName, new PlayerConfigInfo(webType,
						this.CHANGE_COOLDOWN));
			}
			return webType;
		}
		return configInfo.type;
	}

	public FileConfiguration getPlayerConfig() {
		if (this.playerConfig == null) {
			this.playerConfigFile = new File(this.plugin.getDataFolder(),
					"playerCache.yml");
			this.playerConfig = YamlConfiguration
					.loadConfiguration(this.playerConfigFile);
		}
		return this.playerConfig;
	}

	public void reloadConfig() {
		if (getPlayerConfig().isInt("changeCooldown"))
			this.CHANGE_COOLDOWN = getPlayerConfig().getInt("changeCooldown");
	}

	public void savePlayerConfig() {
		if ((this.playerConfig == null) || (this.playerConfigFile == null))
			return;
		try {
			getPlayerConfig().save(this.playerConfigFile);
		} catch (IOException localIOException) {
		}
	}

	public PlayerConfigInfo fromPlayerConfig(String playerName) {
		String dir = "players." + playerName;
		if (getPlayerConfig().contains(dir))
			return new PlayerConfigInfo(getPlayerConfig()
					.getConfigurationSection(dir).getValues(true));
		return new PlayerConfigInfo();
	}

	public void saveToPlayerConfig(String playerName, PlayerConfigInfo info) {
		String dir = "players." + playerName;
		getPlayerConfig().createSection(dir, info.map());
	}
}