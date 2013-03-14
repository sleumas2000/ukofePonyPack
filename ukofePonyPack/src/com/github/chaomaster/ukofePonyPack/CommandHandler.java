package com.github.chaomaster.ukofePonyPack;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHandler implements CommandExecutor {
	public boolean onCommand(CommandSender senderRaw, Command cmd,
			String label, String[] args) {
		Player sender = null;
		if ((senderRaw instanceof Player)) {
			sender = (Player) senderRaw;
		}

		return false;
	}
}