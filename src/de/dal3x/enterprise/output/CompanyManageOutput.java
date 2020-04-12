package de.dal3x.enterprise.output;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import de.dal3x.enterprise.company.Company;
import de.dal3x.enterprise.config.Config;
import de.dal3x.enterprise.enums.ProductionBranch;
import de.dal3x.enterprise.enums.WageClass;

public class CompanyManageOutput {

	public static final String prefix = "§8[§5Firma§8] ";

	public static final String createHelp = prefix + "§a/firma erstellen §8[§aFirmenname§8] [§aProduktionszweig§8]";
	public static final String companyExists = prefix + "§7Eine Firma mit diesem Namen §cexistiert bereits";
	public static final String youAlreadyInCompany = prefix + "§7Du §cbist bereits §7in einer Firma";
	public static final String unvalidBranch = prefix + "§cUngültiger Produktionszweig! §7Nutze §e/firma zweige";
	public static final String createsuccess = prefix + "§7Du hast §aerfolgreich §7eine Firma gegründet";
	public static final String noMoneyToFound = prefix + "§7Du benötigst §c" + Config.foundingPrice
			+ CompanyProductionOutput.money + "§7 um eine Firma zu gründen";

	public static final String youNotInCompany = prefix + "§7Du bist §ckein Angestellter§7 in einer Firma";
	public static final String youNoCEO = prefix + "§cNur§7 der Firmen §cCEO§7 kann diesen Befehl nutzen";
	public static final String companyClosed = prefix + "§7Deine Firme wurde vom CEO §caufgelöst";
	public static final String wantToClose = prefix + "§cWillst du deine Firm sicher auflösen?";
	public static final String wantToCloseConfirm = "§a/firma auflösen bestätigen";
	public static final String closesuccess = prefix + "§7Du hast deine Firma §aaufgelöst";

	public static final String noCompanyWithNameFound = prefix + "§7Eine Firma mit diesem Namen §cexistiert nicht";

	public static final String applyHelp = prefix + "§e/firma bewerben §8[§aFirmen-Name§8] [§aBewerbungstext§8]";
	public static final String somebodyApplied = prefix
			+ "§aJemand hat sich für deine Firma beworben. §7Um die Bewerbung zu sehen nutze §e/firma bewerbungen";
	public static final String youNoManagement = prefix
			+ "§7Du musst ein §cCEO§7, oder §cManager§7 sein um diesen Befehl zu nutzen";
	public static final String acceptHelp = prefix + "§a/firma einstellen §7[§aSpielername§7]";
	public static final String noMoreEmployeeSpace = prefix
			+ "§7Deine Firma hat §ckeine Mitarbeiter-Plätze mehr.§7 Schalte erst neue Plätze frei";
	public static final String noApplicationFound = prefix + "§7Dieser Spieler hat sich §cnicht beworben";
	public static final String alreadyInCompany = prefix
			+ "§7Dieser Spieler §cist bereits §7in einer Firma §cangestellt";
	public static final String newEmployee = prefix + "§7Es gibt einen §eneuen Mitarbeiter: ";
	public static final String denyHelp = prefix + "§a/firma ablehnen §8[§aSpielername§8]";
	public static final String playerSuccessDeny = prefix + "§7Du hast die Bewerbung §aabgelehnt";
	public static final String youDeny = prefix + "§7Deine Bewerbung wurde §cabgelehnt";
	public static final String youAccept = prefix + "§7Deine Bewerbung wurde §aangenommen";
	public static final String youAlreadyEmployee = prefix + "§7Du bist bereits bei §adieser Firma angestellt";
	public static final String succesApplication = prefix + "§7Du hast dich §aerfolgreich beworben";

	public static final String playerNotInCompany = prefix + "§7Dieser Spieler ist §cnicht Angestellter§7 deiner Firma";
	public static final String managerHelp = prefix + "§a/firma manager §8[§aSpielername§8]";

	public static final String fireHelp = prefix + "§a/firma feuern §8[§aName§8]";
	public static final String fireSuccess = prefix + "§7Du hast diesen Spieler §aentlassen";
	public static final String ceoCantLeave = prefix + "§7Du kannst als CEO nicht §ckündigen";
	public static final String leaveSuccess = prefix + "§7Du hast der Firma §agekündigt";

	public static final String managerDownSuccess = prefix + "§7Dieser Mitarbeiter ist nun §ckein Manager§7 mehr";
	public static final String managerUpSuccess = prefix + "§7Dieser Mitarbeiter ist nun §aein Manager";

	public static final String youManagerDown = prefix + "§7Du bist nun §ckein Manager§7 mehr";
	public static final String youManagerUp = prefix + "§7Du bist nun §aein Manager";

	public static final String noManagerSpace = prefix + "§7Es gibt §ckeinen freien Managerplatz§7 mehr";

	public static final String wageHelp = prefix + "§a/firma lohn §8[§aName§8] [§aLohnstufe§8]";
	public static final String unvalidwage = prefix + "§cUngültige§7 Lohnstufe!";

	public static final String sellHelp = prefix
			+ "§7Verkaufen: §a/firma verkauf §8[§aPreis§8] §8(§ePreis wird pro Stück angegeben§8)";
	public static final String sellPriceHelp = prefix
			+ "§7Preisformat: §axxxx §7oder §axxxx.xx §8(§ePreis wird pro Stück angegeben§8)";

	public static void sendLeaveMessage(Player p, Company comp) {
		for (UUID memberID : comp.getMember()) {
			OfflinePlayer member = Bukkit.getOfflinePlayer(memberID);
			if (member.isOnline()) {
				if (comp.getCeo().equals(memberID) || comp.getManager().contains(memberID)) {
					member.getPlayer().sendMessage(prefix + "§e" + p.getName() + "§a hat gekündigt");
				}
			}
		}
	}

	public static void sendInfo(Player p, Company company) {
		p.sendMessage("§7---§e" + company.getName() + "§7---");
		p.sendMessage("§7CEO: §a" + Bukkit.getOfflinePlayer(company.getCeo()).getName());
		List<String> manager = new LinkedList<String>();
		for (UUID id : company.getManager()) {
			manager.add(Bukkit.getOfflinePlayer(id).getName());
		}
		p.sendMessage("§7Manager: §a" + manager.toString());
		DecimalFormat df2 = new DecimalFormat("#.##");
		p.sendMessage("§7Firmenkonto: §a" + df2.format(company.getMoney()) + " ⛂");
		p.sendMessage("§7Herstellungs-Zweig: §a" + ProductionBranch.translate(company.getBranch().toString()));
		List<String> member = new LinkedList<String>();
		for (UUID id : company.getMember()) {
			if (!company.getCeo().equals(id) && !company.getManager().contains(id)) {
				member.add(Bukkit.getOfflinePlayer(id).getName());
			}
		}
		p.sendMessage("§7Angestellte: §a" + member.toString());
	}

	public static void sendHelp(Player p) {
		p.sendMessage(prefix + "§7---- §eHilfemenü §7----");
		p.sendMessage("§7Firma gründen: §a/firma gründen §8[§aFirmenname§8] [§aProduktions-Zweig§8]");
		p.sendMessage("§7Produktions-Zweige: §a/firma zweige");
		p.sendMessage("§7Firmen-Info anzeigen: §a/firma info §8<[§aFirmenname§8]>");
		p.sendMessage("§7Für Stelle bewerben: §a/firma bewerben §8[§aFirmenname§8] [§aBewerbungstext§8]");
		p.sendMessage("§7Anstellung kündigen: §a/firma kündigen");
		p.sendMessage("§7Firmenlager einsehen: §a/firma lager");
		p.sendMessage("§7Manager Befehle: §a/firma management");
		p.sendMessage("§7Inhaber Befehle: §a/firma ceo");
	}

	public static void sendManagerHelp(Player p) {
		p.sendMessage(prefix + "§7---- §eManager Hilfemenü§7 ----");
		p.sendMessage("§7Bewerbungen einsehen: §a/firma bewerbungen");
		p.sendMessage("§7Bewerbung annehmen: §a/firma einstellen §8[§aSpielername§8]");
		p.sendMessage("§7Bewerbung ablehnen: §a/firma ablehnen §8[§aSpielername§8]");
		p.sendMessage("§7Angestellte entlassen: §a/firma feuern §8[§aSpielername§8]");
		p.sendMessage("§7Lohnstufen setzen: §a/firma lohn §8[§aSpielername§8] §8[§aLohnstufe§8]");
		p.sendMessage("§7Lohnstufen anzeigen: §a/firma lohnstufen");
		p.sendMessage("§7Produktion verwalten: §a/firma produktion");
		p.sendMessage("§7Upgrades verwalten: §a/firma upgrades");
		p.sendMessage("§7Preise bearbeiten: §a/firma verkauf §8<[§aPreis§8]>");
	}

	public static void sendCEOHelp(Player p) {
		p.sendMessage(prefix + "§7---- §eCEO Hilfemenü:§7 ----");
		p.sendMessage("§7Firma auflösen: §a/firma auflösen");
		p.sendMessage("§7Manager bearbeiten: §a/firma manager [Spielername]");
	}

	public static void sendApplications(Player p, Company c) {
		p.sendMessage(prefix + "§eBewerbungen§7: ");
		for (UUID id : c.getApplications().keySet()) {
			String message = c.getApplications().get(id);
			String name = Bukkit.getOfflinePlayer(id).getName();
			p.sendMessage("§7Bewerber: §a" + name);
			p.sendMessage("§a" + message);
			p.sendMessage("§7-------------");
		}
	}

	public static void sendBranches(Player p) {
		String listing = "";
		for (ProductionBranch branch : ProductionBranch.values()) {
			listing += ("§a" + ProductionBranch.translate(branch.toString()) + "§7, ");
		}
		listing = listing.substring(0, listing.length() - 2);
		p.sendMessage(prefix + "§7Mögliche Produktions-Zweige sind: " + listing);
	}

	public static void sendWageLevel(Player p) {
		String listing = "";
		for (WageClass wage : WageClass.values()) {
			listing += ("§a" + WageClass.translate(wage.toString()) + "§7, ");
		}
		listing = listing.substring(0, listing.length() - 2);
		p.sendMessage(prefix + "§7Mögliche Lohnstufen sind: " + listing);
	}

	public static void successWage(Player p, UUID employeeID, WageClass wage) {
		String name = Bukkit.getPlayer(employeeID).getName();
		p.sendMessage(prefix + "§a" + name + " §7ist jetzt in Lohnstufe §a" + WageClass.translate(wage.toString()));
		if (Bukkit.getPlayer(employeeID).isOnline()) {
			String message = prefix + "§7Deine Lohnstufe wurde auf §a" + WageClass.translate(wage.toString())
					+ "§7 geändert";
			Bukkit.getPlayer(employeeID).getPlayer().sendMessage(message);
		}
	}
}
