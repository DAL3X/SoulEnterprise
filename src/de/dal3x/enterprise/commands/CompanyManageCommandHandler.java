package de.dal3x.enterprise.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import de.dal3x.enterprise.company.Company;
import de.dal3x.enterprise.company.CompanyHelper;
import de.dal3x.enterprise.config.Config;
import de.dal3x.enterprise.docking.VaultDock;
import de.dal3x.enterprise.enums.ProductionBranch;
import de.dal3x.enterprise.enums.WageClass;
import de.dal3x.enterprise.file.Filehandler;
import de.dal3x.enterprise.inventory.PriceGUI;
import de.dal3x.enterprise.inventory.ProductionGUI;
import de.dal3x.enterprise.inventory.StorageGUI;
import de.dal3x.enterprise.inventory.UpgradeGUI;
import de.dal3x.enterprise.main.EnterprisePlugin;
import de.dal3x.enterprise.market.MarketPlace;
import de.dal3x.enterprise.output.CompanyManageOutput;

public class CompanyManageCommandHandler {

	@SuppressWarnings("deprecation")
	public static void handleCommand(Player p, Command command, String label, String[] args) {
		UUID id = p.getUniqueId();
		// create
		if (args.length > 1 && args[0].equalsIgnoreCase("gründen")) {
			if (args.length != 3) {
				p.sendMessage(CompanyManageOutput.createHelp);
				return;
			}
			if (CompanyHelper.companyExists(args[1])) {
				p.sendMessage(CompanyManageOutput.companyExists);
				return;
			}
			if (CompanyHelper.isEmployee(id)) {
				p.sendMessage(CompanyManageOutput.youAlreadyInCompany);
				return;
			}
			if (!ProductionBranch.exists(ProductionBranch.translate(args[2]))) {
				p.sendMessage(CompanyManageOutput.unvalidBranch);
				return;
			}
			if(!VaultDock.hasMoney(p, Config.foundingPrice)) {
				p.sendMessage(CompanyManageOutput.noMoneyToFound);
				return;
			}
			VaultDock.removeMoney(p, Config.foundingPrice);
			ProductionBranch branch = ProductionBranch.valueOf(ProductionBranch.translate(args[2]));
			Company company = new Company(args[1], id, branch);
			EnterprisePlugin.getInstance().addCompany(args[1], company);
			EnterprisePlugin.getInstance().addPlayerData(id, company.getName());
			Filehandler.storeCompany(company);
			Filehandler.storePlayerData(p.getUniqueId());
			p.sendMessage(CompanyManageOutput.createsuccess);
			return;
		}
		// delete
		if (args.length > 0 && args.length < 2 && args[0].equalsIgnoreCase("auflösen")){
			p.sendMessage(CompanyManageOutput.wantToClose);
			p.sendMessage(CompanyManageOutput.wantToCloseConfirm);
			return;
		}
		if (args.length > 1 && args[0].equalsIgnoreCase("auflösen") && args[1].equalsIgnoreCase("bestätigen")) {
			if (!CompanyHelper.isEmployee(id)) {
				p.sendMessage(CompanyManageOutput.youNotInCompany);
				return;
			}
			if (!CompanyHelper.isCEO(id)) {
				p.sendMessage(CompanyManageOutput.youNoCEO);
				return;
			}
			Company company = EnterprisePlugin.getInstance().getCompanyForPlayerID(id);
			for(UUID oID : company.getMarketOffer().keySet()) {
				MarketPlace.deleteOffer(company.getBranch(), company.getMarketOffer().get(oID).getItem().getType(), oID);
			}
			for (UUID memberID : company.getMember()) {
				OfflinePlayer member = Bukkit.getOfflinePlayer(memberID);
				if (member.isOnline()) {
					if (!member.getUniqueId().equals(id)) {
						member.getPlayer().sendMessage(CompanyManageOutput.companyClosed);
					}
					EnterprisePlugin.getInstance().removePlayerData(member.getUniqueId());
				}
				Filehandler.deletePlayerData(memberID);
			}
			EnterprisePlugin.getInstance().removeCompany(company.getName());
			Filehandler.deleteCompanyFile(company.getName());
			p.sendMessage(CompanyManageOutput.closesuccess);
			return;
		}
		// info
		if (args.length > 0 && args[0].equalsIgnoreCase("info")) {
			if (args.length == 1) {
				if (!CompanyHelper.isEmployee(id)) {
					p.sendMessage(CompanyManageOutput.youNotInCompany);
					return;
				}
				CompanyManageOutput.sendInfo(p, EnterprisePlugin.getInstance().getCompanyForPlayerID(id));
				return;
			} else {
				if (!CompanyHelper.companyExists(args[1])) {
					p.sendMessage(CompanyManageOutput.noCompanyWithNameFound);
					return;
				}
				Filehandler.loadCompany(args[1]);
				CompanyManageOutput.sendInfo(p, EnterprisePlugin.getInstance().getCompanyForName(args[1]));
				return;
			}
		}
		// apply
		if (args.length > 0 && args[0].equalsIgnoreCase("bewerben")) {
			if (args.length < 2) {
				p.sendMessage(CompanyManageOutput.applyHelp);
				return;
			} else {
				String appliance = "";
				for (int i = 2; i < args.length; i++) {
					appliance += args[i] + " ";
				}
				if (!CompanyHelper.companyExists(args[1])) {
					p.sendMessage(CompanyManageOutput.noCompanyWithNameFound);
					return;
				}
				Company company = EnterprisePlugin.getInstance().getCompanyForName(args[1]);
				if (company.equals(EnterprisePlugin.getInstance().getCompanyForPlayerID(id))) {
					p.sendMessage(CompanyManageOutput.youAlreadyEmployee);
					return;
				}
				for (UUID memberID : company.getMember()) {
					OfflinePlayer member = Bukkit.getOfflinePlayer(memberID);
					if (member.isOnline()) {
						if (company.getCeo().equals(memberID) || company.getManager().contains(memberID)) {
							member.getPlayer().sendMessage(CompanyManageOutput.somebodyApplied);
						}
					}
				}
				company.addApplication(p.getUniqueId(), appliance);
				Filehandler.storeCompany(company);
				EnterprisePlugin.getInstance().unloadIfOffline(company);
				p.sendMessage(CompanyManageOutput.succesApplication);
				return;
			}
		}
		// applications
		if (args.length > 0 && args[0].equalsIgnoreCase("bewerbungen")) {
			if (!CompanyHelper.isEmployee(id)) {
				p.sendMessage(CompanyManageOutput.youNotInCompany);
				return;
			}
			if (!CompanyHelper.isManagement(id)) {
				p.sendMessage(CompanyManageOutput.youNoManagement);
				return;
			}
			CompanyManageOutput.sendApplications(p, EnterprisePlugin.getInstance().getCompanyForPlayerID(id));
			return;
		}
		// hire
		if (args.length > 0 && args[0].equalsIgnoreCase("einstellen")) {
			if (args.length < 2) {
				p.sendMessage(CompanyManageOutput.acceptHelp);
				return;
			}
			if (!CompanyHelper.isEmployee(id)) {
				p.sendMessage(CompanyManageOutput.youNotInCompany);
				return;
			}
			if (!CompanyHelper.isManagement(id)) {
				p.sendMessage(CompanyManageOutput.youNoManagement);
				return;
			}
			Company comp = EnterprisePlugin.getInstance().getCompanyForPlayerID(id);
			if (!comp.hasEmployeeSpace()) {
				p.sendMessage(CompanyManageOutput.noMoreEmployeeSpace);
				return;
			}
			UUID inviteID = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
			if (Filehandler.hasPlayerData(inviteID)) {
				p.sendMessage(CompanyManageOutput.alreadyInCompany);
				return;
			}
			if (comp.getApplications().get(inviteID) == null) {
				p.sendMessage(CompanyManageOutput.noApplicationFound);
				return;
			}
			for (UUID memberID : comp.getMember()) {
				OfflinePlayer member = Bukkit.getOfflinePlayer(memberID);
				if (member.isOnline()) {
					member.getPlayer().sendMessage(CompanyManageOutput.newEmployee + args[1]);
				}
			}
			OfflinePlayer invited = Bukkit.getOfflinePlayer(inviteID);
			if (invited.isOnline()) {
				invited.getPlayer().sendMessage(CompanyManageOutput.youAccept);
			}
			comp.hireEmployee(inviteID);
			comp.removeApplication(inviteID);
			EnterprisePlugin.getInstance().addPlayerData(inviteID, comp.getName());
			Filehandler.storeCompany(comp);
			Filehandler.storePlayerData(inviteID);
			return;
		}
		// deny
		if (args.length > 0 && args[0].equalsIgnoreCase("ablehnen")) {
			if (args.length < 2) {
				p.sendMessage(CompanyManageOutput.denyHelp);
				return;
			}
			if (!CompanyHelper.isEmployee(id)) {
				p.sendMessage(CompanyManageOutput.youNotInCompany);
				return;
			}
			if (!CompanyHelper.isManagement(id)) {
				p.sendMessage(CompanyManageOutput.youNoManagement);
				return;
			}
			Company comp = EnterprisePlugin.getInstance().getCompanyForPlayerID(id);
			UUID inviteID = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
			if (comp.getApplications().get(inviteID) == null) {
				p.sendMessage(CompanyManageOutput.noApplicationFound);
				return;
			}
			comp.getApplications().remove(inviteID);
			OfflinePlayer applicant = Bukkit.getOfflinePlayer(inviteID);
			if (applicant.isOnline()) {
				applicant.getPlayer().sendMessage(CompanyManageOutput.youDeny);
			}
			Filehandler.storeCompany(comp);
			p.sendMessage(CompanyManageOutput.playerSuccessDeny);
			return;
		}
		// branchlist
		if (args.length > 0 && args[0].equalsIgnoreCase("zweige")) {
			CompanyManageOutput.sendBranches(p);
			return;
		}
		// fire
		if (args.length > 0 && args[0].equalsIgnoreCase("feuern")) {
			if (args.length < 2) {
				p.sendMessage(CompanyManageOutput.fireHelp);
				return;
			}
			if (!CompanyHelper.isEmployee(id)) {
				p.sendMessage(CompanyManageOutput.youNotInCompany);
				return;
			}
			if (!CompanyHelper.isManagement(id)) {
				p.sendMessage(CompanyManageOutput.youNoManagement);
				return;
			}
			Company comp = EnterprisePlugin.getInstance().getCompanyForPlayerID(id);
			UUID fireID = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
			if (!comp.getMember().contains(fireID)) {
				p.sendMessage(CompanyManageOutput.playerNotInCompany);
				return;
			}
			comp.fireEmployee(fireID);
			Filehandler.deletePlayerData(fireID);
			Filehandler.storeCompany(comp);
			p.sendMessage(CompanyManageOutput.fireSuccess);
			return;
		}
		// quit
		if (args.length > 0 && args[0].equalsIgnoreCase("kündigen")) {
			if (!CompanyHelper.isEmployee(id)) {
				p.sendMessage(CompanyManageOutput.youNotInCompany);
				return;
			}
			if (CompanyHelper.isCEO(id)) {
				p.sendMessage(CompanyManageOutput.ceoCantLeave);
				return;
			}
			Company comp = EnterprisePlugin.getInstance().getCompanyForPlayerID(id);
			if (comp.getManager().contains(id)) {
				comp.removeManager(id);
			}
			comp.getMember().remove(id);
			Filehandler.deletePlayerData(id);
			Filehandler.storeCompany(comp);
			p.sendMessage(CompanyManageOutput.leaveSuccess);
			CompanyManageOutput.sendLeaveMessage(p, comp);
			return;
		}
		// manager
		if (args.length > 0 && args[0].equalsIgnoreCase("manager")) {
			if (args.length < 2) {
				p.sendMessage(CompanyManageOutput.managerHelp);
				return;
			}
			if (!CompanyHelper.isEmployee(id)) {
				p.sendMessage(CompanyManageOutput.youNotInCompany);
				return;
			}
			if (!CompanyHelper.isCEO(id)) {
				p.sendMessage(CompanyManageOutput.youNoCEO);
				return;
			}
			Company company = EnterprisePlugin.getInstance().getCompanyForPlayerID(id);
			UUID managerID = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
			if (!company.getMember().contains(managerID)) {
				p.sendMessage(CompanyManageOutput.playerNotInCompany);
				return;
			}
			if (company.getManager().contains(managerID)) {
				company.removeManager(managerID);
				p.sendMessage(CompanyManageOutput.managerDownSuccess);
				if (Bukkit.getOfflinePlayer(managerID).isOnline()) {
					Bukkit.getOfflinePlayer(managerID).getPlayer().sendMessage(CompanyManageOutput.youManagerDown);
				}
			} else {
				if (!company.hasManagerSpace()) {
					p.sendMessage(CompanyManageOutput.noManagerSpace);
					return;
				}
				company.addManager(managerID);
				p.sendMessage(CompanyManageOutput.managerUpSuccess);
				if (Bukkit.getOfflinePlayer(managerID).isOnline()) {
					Bukkit.getOfflinePlayer(managerID).getPlayer().sendMessage(CompanyManageOutput.youManagerUp);
				}
			}
			Filehandler.storeCompany(company);
			return;
		}
		// wage
		if (args.length > 0 && args[0].equalsIgnoreCase("lohn")) {
			if (args.length < 3) {
				p.sendMessage(CompanyManageOutput.wageHelp);
				CompanyManageOutput.sendWageLevel(p);
				return;
			}
			if (!CompanyHelper.isEmployee(id)) {
				p.sendMessage(CompanyManageOutput.youNotInCompany);
				return;
			}
			if (!CompanyHelper.isManagement(id)) {
				p.sendMessage(CompanyManageOutput.youNoManagement);
				return;
			}
			Company company = EnterprisePlugin.getInstance().getCompanyForPlayerID(id);
			UUID employeeID = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
			if (!company.getMember().contains(employeeID)) {
				p.sendMessage(CompanyManageOutput.playerNotInCompany);
				return;
			}
			if (!WageClass.isWageClass(WageClass.translate(args[2]))) {
				p.sendMessage(CompanyManageOutput.unvalidwage);
				CompanyManageOutput.sendWageLevel(p);
				return;
			}
			company.setWage(employeeID, WageClass.valueOf(WageClass.translate(args[2])));
			CompanyManageOutput.successWage(p, employeeID, WageClass.valueOf(WageClass.translate(args[2])));
			return;
		}
		// wageClasses
		if (args.length > 0 && args[0].equalsIgnoreCase("lohnstufen")) {
			CompanyManageOutput.sendWageLevel(p);
			return;
		}
		// storage
		if (args.length > 0 && args[0].equalsIgnoreCase("lager")) {
			if (!CompanyHelper.isEmployee(p.getUniqueId())) {
				p.sendMessage(CompanyManageOutput.youNotInCompany);
				return;
			}
			Company comp = EnterprisePlugin.getInstance().getCompanyForPlayerID(p.getUniqueId());
			new StorageGUI(comp).openInventory(p, 0);
			return;
		}
		// production
		if (args.length > 0 && args[0].equalsIgnoreCase("produktion")) {
			if (!CompanyHelper.isEmployee(id)) {
				p.sendMessage(CompanyManageOutput.youNotInCompany);
				return;
			}
			if (!CompanyHelper.isManagement(id)) {
				p.sendMessage(CompanyManageOutput.youNoManagement);
				return;
			}
			Company comp = EnterprisePlugin.getInstance().getCompanyForPlayerID(p.getUniqueId());
			new ProductionGUI(comp).openInventory(p, 0);
			return;
		}
		// upgrades
		if (args.length > 0 && args[0].equalsIgnoreCase("upgrades")) {
			if (!CompanyHelper.isEmployee(id)) {
				p.sendMessage(CompanyManageOutput.youNotInCompany);
				return;
			}
			if (!CompanyHelper.isManagement(id)) {
				p.sendMessage(CompanyManageOutput.youNoManagement);
				return;
			}
			Company comp = EnterprisePlugin.getInstance().getCompanyForPlayerID(p.getUniqueId());
			new UpgradeGUI(comp).openInventory(p);
			return;
		}
		if (args.length > 0 && args[0].equalsIgnoreCase("management")) {
			CompanyManageOutput.sendManagerHelp(p);
			return;
		}
		if (args.length > 0 && args[0].equalsIgnoreCase("ceo")) {
			CompanyManageOutput.sendCEOHelp(p);
			return;
		}
		if (args.length > 0 && (args[0].equalsIgnoreCase("verkauf") || args[0].equalsIgnoreCase("verkaufen"))) {
			if (!CompanyHelper.isEmployee(id)) {
				p.sendMessage(CompanyManageOutput.youNotInCompany);
				return;
			}
			if (!CompanyHelper.isManagement(id)) {
				p.sendMessage(CompanyManageOutput.youNoManagement);
				return;
			}
			Company comp = EnterprisePlugin.getInstance().getCompanyForPlayerID(p.getUniqueId());
			if (args.length < 2) {
				p.sendMessage(CompanyManageOutput.sellHelp);
				new PriceGUI(comp, -1).openInventory(p, 0);
				return;
			}
			if(!args[1].matches("\\d+(\\.{1}\\d+){0,1}") || Double.parseDouble(args[1]) < 0) {
				p.sendMessage(CompanyManageOutput.sellPriceHelp);
				return;
			}
			new PriceGUI(comp, Double.parseDouble(args[1])).openInventory(p, 0);
			return;
		}
		// help
		CompanyManageOutput.sendHelp(p);
	}
}
