package com.github.chaomaster.ukofePonyPack;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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

	public boolean onCommand(CommandSender senderRaw, Command cmd,
			String label, String[] args) {
		Player sender = null;
		if ((senderRaw instanceof Player)) {
			sender = (Player) senderRaw;
		}
		// Info <Player> / Recheck <Player>
		// Return information on the player's skin and powers
		// The recheck command forces the value to be recomputed.
		if (cmd.getName().equalsIgnoreCase("info")
				|| cmd.getName().equalsIgnoreCase("recheck")) {
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
			if (!selfOtherPermCheck(sender, target, "ukofePonyPack."
					+ cmd.getName().toLowerCase())) {
				return true;
			}
			if (cmd.getName().equalsIgnoreCase("recheck")) {
				this.checker.triggerExpire(target);
			}
			sendMessage(sender, this.checker.getFullInfo(target));
			return true;
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