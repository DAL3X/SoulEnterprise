package de.dal3x.enterprise.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.dal3x.enterprise.inventory.BranchSelectGUI;

public class MarketCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player) && args.length > 0) {
			String name = args[0];
			Player p = Bukkit.getPlayer(name);
			if (p != null) {
				new BranchSelectGUI().openInventory(p);
				return true;
			}
			return true;
		} else if (sender.hasPermission("soulenterprise.market")) {
			Player p = (Player) sender;
			new BranchSelectGUI().openInventory(p);
			return true;
		}
		return true;
	}

}
