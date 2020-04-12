package de.dal3x.enterprise.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CompanyCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}
		Player p = (Player) sender;
		if (args.length > 1 && args[0].equalsIgnoreCase("admin") && p.hasPermission("soulenterprise.admin")) {
			CompanyAdminCommandHandler.handleCommand(p, command, label, args);
		} else {
			CompanyManageCommandHandler.handleCommand(p, command, label, args);
		}
		return true;
	}

}
