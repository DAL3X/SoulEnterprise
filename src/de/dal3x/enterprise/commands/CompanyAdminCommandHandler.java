package de.dal3x.enterprise.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import de.dal3x.enterprise.company.Company;
import de.dal3x.enterprise.file.Filehandler;
import de.dal3x.enterprise.main.EnterprisePlugin;
import de.dal3x.enterprise.market.MarketPlace;

public class CompanyAdminCommandHandler {

	@SuppressWarnings("deprecation")
	public static void handleCommand(Player p, Command command, String label, String[] args) {
		// löschen
		if(args.length > 2 && args[1].equalsIgnoreCase("delete")) {
			Company company = EnterprisePlugin.getInstance().getCompanyForName(args[1]);
			for(UUID id : company.getMarketOffer().keySet()) {
				MarketPlace.deleteOffer(company.getBranch(), company.getMarketOffer().get(id).getItem().getType(), id);
			}
			for (UUID memberID : company.getMember()) {
				Filehandler.deletePlayerData(memberID);
			}
			EnterprisePlugin.getInstance().removeCompany(company.getName());
			Filehandler.deleteCompanyFile(company.getName());
			return;
		}
		// plöschen
		if(args.length > 2 && args[1].equalsIgnoreCase("pdelete")) {
			Company company = EnterprisePlugin.getInstance().getCompanyForPlayerID(Bukkit.getOfflinePlayer(args[2]).getUniqueId());
			for(UUID id : company.getMarketOffer().keySet()) {
				MarketPlace.deleteOffer(company.getBranch(), company.getMarketOffer().get(id).getItem().getType(), id);
			}
			for (UUID memberID : company.getMember()) {
				Filehandler.deletePlayerData(memberID);
			}
			EnterprisePlugin.getInstance().removeCompany(company.getName());
			Filehandler.deleteCompanyFile(company.getName());
			return;
		}
		// reload
		if(args.length > 2 && args[1].equalsIgnoreCase("reload")) {
			EnterprisePlugin.getInstance().reload();
			return;
		}
		 p.sendMessage("reload");
		 p.sendMessage("delete [Name]");
		 p.sendMessage("pdelete [Spielername]");
	}
}
