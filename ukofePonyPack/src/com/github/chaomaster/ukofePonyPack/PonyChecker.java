package com.github.chaomaster.ukofePonyPack;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;

import javax.imageio.ImageIO;

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

	private PonyType webCheck(String playerName) {
		try {
			this.plugin.getLogger().info("Getting " + playerName + "'s skin");

			URL skinUrl = new URL("http://s3.amazonaws.com/MinecraftSkins/"
					+ playerName + ".png");
			BufferedImage skin = ImageIO.read(skinUrl);

			int pix = skin.getRGB(0, 0);
			if (pix == -413391)
				return PonyType.EARTH;
			if (pix == -7812368)
				return PonyType.PEGASUS;
			if (pix == -3039260)
				return PonyType.UNICORN;
			if (pix == -67076) {
				return PonyType.ALICORN;
			}
			return PonyType.NONE;
		} catch (MalformedURLException ex) {
			this.plugin.getLogger().info(
					"MalforumedURLException with " + playerName + "'s skin");

			return PonyType.ERROR;
		} catch (IOException ex) {
			this.plugin.getLogger().info(
					"IOException with " + playerName + "'s skin");
		}
		return PonyType.ERROR;
	}

	public PonyType checkPlayer(String playerName) {
		PlayerConfigInfo configInfo = fromPlayerConfig(playerName);
		if (configInfo.hasExpired()) {
			PonyType webType = webCheck(playerName);
			if (webType == PonyType.ERROR)
				return configInfo.type;
			if (webType.ignUn() == PonyType.NONE)
				saveToPlayerConfig(playerName, new PlayerConfigInfo(webType, 0));
			else if (webType != configInfo.type) {
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