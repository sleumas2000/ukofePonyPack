package com.github.chaomaster.ukofePonyPack;

import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ukofePonyPack extends JavaPlugin {
	public PonyChecker checker = null;
	private HashMap<String, PonyPowers> ponyPowerMap;

	public void onEnable() {
		this.checker = new PonyChecker(this);
		ponyPowerMap = new HashMap<String, PonyPowers>();
		ponyPowerMap.put("earth", new EarthPonyPowers(this));
		ponyPowerMap.put("unicorn", new UnicornPonyPowers(this));
		ponyPowerMap.put("pegasus", new PegasusPonyPowers(this));
	}

	public void onDisable() {
		this.checker.savePlayerConfig();

		for (Player p : getServer().getOnlinePlayers())
			p.removeMetadata("ponyType", this);
	}

	public boolean onCommand(CommandSender senderRaw, Command cmd,
			String label, String[] args) {
		Player sender = null;
		if ((senderRaw instanceof Player)) {
			sender = (Player) senderRaw;
		}
		// PonyInfo <Player> / PonyRecheck <Player>
		// Return information on the player's skin and powers
		// The recheck command forces the value to be recomputed.
		if (cmd.getName().equalsIgnoreCase("ponyinfo")
				|| cmd.getName().equalsIgnoreCase("ponyrecheck")) {
			Player target = null;
			if (args.length == 0) {
				if (sender == null) {
					return false;
				} else {
					target = sender;
				}
			} else if (args.length == 1) {
				target = this.getServer().getPlayer(args[0]);
			}
			if (target == null) {
				sendMessage(sender,
						String.format("Player %s not found", target));
				return true;
			}
			if (!selfOtherPermCheck(sender, target, cmd.getPermission())) {
				return true;
			}
			if (cmd.getName().equalsIgnoreCase("recheck")) {
				this.checker.triggerExpire(target);
			}
			sendMessage(sender, this.checker.getFullInfo(target));
			return true;
		}
		// PonyAssign [Player] [Type] {Duration}
		// Sets the player's pony type.
		else if (cmd.getName().equalsIgnoreCase("ponyassign")) {
			Player target = null;
			boolean forever = false;
			int duration = 0;
			PonyType toType = null;
			if (args.length == 2) {
				forever = true;
			} else if (args.length == 3) {
				duration = Integer.parseInt(args[2]);
			} else {
				return false;
			}
			target = this.getServer().getPlayer(args[0]);
			try {
				toType = PonyType.valueOf(args[1].toUpperCase());
			} catch (IllegalArgumentException e) {
				sendMessage(sender, String.format(
						"%s is not an accepted pony type", target));
				return true;
			}
			if (target == null) {
				sendMessage(sender,
						String.format("Player %s not found", target));
				return true;
			}
			if (!selfOtherPermCheck(sender, target, cmd.getPermission())) {
				return true;
			}
			this.checker.saveToPlayerConfig(target.getName(),
					new PlayerConfigInfo(toType, duration, forever));
			this.checker.triggerExpire(target);
			if (sender.equals(target)) {
				target.sendMessage(String.format("%s has now made you %s",
						sender == null ? "The console" : sender.getName(),
						this.checker.getType(target).getMessage()));
			} else {
				target.sendMessage(String.format("%s has now made you %s",
						sender == null ? "The console" : sender.getName(),
						this.checker.getType(target).getMessage()));
				sendMessage(sender, String.format("%s is now %s", target
						.getName(), this.checker.getType(target).getMessage()));
			}
			if(toType != PonyType.PEGASUS){ //Temp flight problem fix
				target.setAllowFlight(false);
			}
			return true;
		}
		// Ponycachereload
		// Forces a reload for the player cache
		else if (cmd.getName().equalsIgnoreCase("ponycachereload")) {
			if (sender != null && !sender.hasPermission(cmd.getPermission())) {
				sendMessage(sender,
						"You don't have permission to use this command");
				return true;
			}
			sendMessage(sender, "Nullifying the player cache to force a reload");
			this.checker.triggerCacheExpire();
		}
		// Ponycachesave
		// Forces a save of the player cache
		else if (cmd.getName().equalsIgnoreCase("ponycachesave")) {
			if (sender != null && !sender.hasPermission(cmd.getPermission())) {
				sendMessage(sender,
						"You don't have permission to use this command");
				return true;
			}
			sendMessage(sender, "Saving the player cache");
			this.checker.savePlayerConfig();
		}
		// Ponyconfigreload [section]
		// Forces a config reload of the ponypower section
		else if (cmd.getName().equalsIgnoreCase("ponyconfigreload")) {
			if (sender != null && !sender.hasPermission(cmd.getPermission())) {
				sendMessage(sender,
						"You don't have permission to use this command");
				return true;
			}
			if (args.length == 0) {
				for (PonyPowers power : this.ponyPowerMap.values()) {
					power.reloadConfig();
				}
			} else {
				if (ponyPowerMap.containsKey(args[0].toLowerCase())) {
					ponyPowerMap.get(args[0].toLowerCase()).reloadConfig();
				} else if (args[0].equalsIgnoreCase("checker")) {
					checker.reloadConfig();
				} else {
					sendMessage(sender,
							String.format("Powers for %s not found", args[0]));
				}
			}
		}
		// TODO assign command.
		return false;
	}

	private void sendMessage(Player recipient, String message) {
		if (recipient == null) {
			this.getLogger().info(message);
		} else {
			recipient.sendMessage(message);
		}
	}

	private boolean selfOtherPermCheck(Player sender, Player target,
			String baseNode) {
		if (sender == null) {
			return true;
		}
		if (sender.equals(target)) {
			if (!sender.hasPermission(baseNode + ".self")) {
				sendMessage(sender,
						"You don't have permission to use this command on yourself");
				return false;
			}
		} else {
			if (!sender.hasPermission(baseNode + ".other")) {
				sendMessage(sender,
						"You don't have permission to use this command on others.");
				return false;
			}
		}
		return true;
	}
}