package com.github.chaomaster.ukofePonyPack;

import java.io.File;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.ItemStack;

public class EarthPonyPowers extends PonyPowers {
	private double DAMAGE_MULTIPLIER = 1.1D;
	private double RESISTANCE_DIVISOR = 1.1D;
	private int FOODBAR_MINIUM = 5;
	private double FOODBAR_PRESERVE_CHANCE = 0.1D;
	private int WHEAT_BONUS = 1;
	private int NETHER_WART_BONUS = 2;
	private int COCOA_BONUS = 2;
	private int POTATO_BONUS = 2;
	private int CARROT_BONUS = 2;

	public EarthPonyPowers(ukofePonyPack plugin) {
		super(plugin);
	}

	public void reloadConfig() {
		File configFile = new File(this.plugin.getDataFolder(),
				"EarthConfig.yml");
		YamlConfiguration config = YamlConfiguration
				.loadConfiguration(configFile);
		if (config.isDouble("damageMultiplier")) {
			this.DAMAGE_MULTIPLIER = config.getDouble("damageMultiplier");
		}
		if (config.isDouble("resistanceDivisor")) {
			this.RESISTANCE_DIVISOR = config.getDouble("resistanceDivisor");
		}
		if (config.isInt("foodbarMinium")) {
			this.FOODBAR_MINIUM = config.getInt("foodbarMinium");
		}
		if (config.isDouble("foodbarPreserveChance")) {
			this.FOODBAR_PRESERVE_CHANCE = config
					.getDouble("foodbarPreserveChance");
		}
		if (config.isInt("wheatBonus")) {
			this.WHEAT_BONUS = config.getInt("wheatBonus");
		}
		if (config.isInt("netherWartBonus")) {
			this.NETHER_WART_BONUS = config.getInt("netherWartBonus");
		}
		if (config.isInt("cocoaBonus")) {
			this.COCOA_BONUS = config.getInt("cocoaBonus");
		}
		if (config.isInt("potatoBonus")) {
			this.POTATO_BONUS = config.getInt("potatoBonus");
		}
		if (config.isInt("carrotBonus")) {
			this.CARROT_BONUS = config.getInt("carrotBonus");
		}
	}

	@EventHandler
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		if (((event.getEntity() instanceof Player))
				&& (this.plugin.checker.getType((Player) event.getEntity()) == PonyType.EARTH)) {
			receivingDamage(event);
		}

		if (((event.getDamager() instanceof Player))
				&& (this.plugin.checker.getType((Player) event.getDamager()) == PonyType.EARTH)) {
			dealingDamage(event);
		}
	}

	@EventHandler
	public void onFoodLevelChangeEvent(FoodLevelChangeEvent event) {
		if (((event.getEntity() instanceof Player))
				&& (this.plugin.checker.getType((Player) event.getEntity()) == PonyType.EARTH)) {
			gettingHungry(event);
		}
	}

	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent event) {
		if (this.plugin.checker.getType(event.getPlayer()) == PonyType.EARTH) {
			Block block = event.getBlock();
			switch (block.getTypeId()) {
			case 60:
				if (block.getData() == 7) {
					harvestingCrops(event, new ItemStack(Material.WHEAT,
							this.WHEAT_BONUS));
				}
				break;
			case 263:
				if (block.getData() == 3) {
					harvestingCrops(event, new ItemStack(Material.NETHER_WARTS,
							this.NETHER_WART_BONUS));
				}
				break;
			case 128:
				if ((block.getData() & 0x8) == 8) {
					harvestingCrops(event, new ItemStack(Material.INK_SACK,
							this.COCOA_BONUS, (short) 3));
				}
				break;
			case 143:
				if (block.getData() == 7) {
					harvestingCrops(event, new ItemStack(Material.POTATO_ITEM,
							this.POTATO_BONUS));
				}
				break;
			case 142:
				if (block.getData() == 7) {
					harvestingCrops(event, new ItemStack(Material.CARROT_ITEM,
							this.CARROT_BONUS));
				}
				break;
			}
		}
	}

	public void dealingDamage(EntityDamageByEntityEvent event) {
		double unRounded = event.getDamage() * this.DAMAGE_MULTIPLIER;
		event.setDamage((int) unRounded
				+ (Math.random() < unRounded % 1.0D ? 0 : 1));
	}

	public void receivingDamage(EntityDamageByEntityEvent event) {
		double unRounded = event.getDamage() / this.RESISTANCE_DIVISOR;
		event.setDamage((int) unRounded
				+ (Math.random() < unRounded % 1.0D ? 0 : 1));
	}

	public void gettingHungry(FoodLevelChangeEvent event) {
		if (event.getFoodLevel() < this.FOODBAR_MINIUM) {
			event.setFoodLevel(5);
		} else if (Math.random() < this.FOODBAR_PRESERVE_CHANCE) {
			event.setFoodLevel(event.getFoodLevel() + 1);
		}
	}

	public void harvestingCrops(BlockBreakEvent event, ItemStack drops) {
		event.getBlock().getWorld()
				.dropItemNaturally(event.getBlock().getLocation(), drops);
	}
}