package de.dal3x.enterprise.output;

import org.bukkit.entity.Player;

import de.dal3x.enterprise.market.Offer;

public class MarketOutput {

	public static final String prefix = CompanyManageOutput.prefix;
	
	public static final String isSelling = "§7Wird verkauft";
	public static final String isStored = "§7Wird gelagert";
	public static final String storeLore = "§7Wird nicht verkauft";
	public static final String sellsFor = "§7Wird verkauft für ";
	public static final String sellPerPiece = " §7pro Item";
	public static final String wholeStack = " §7für gesammten Stack";
	public static final String buyThis = "§7Klicke um Angebot anzusehen";
	public static final String noOffers = "§7Keine Angebote";
	public static final String soldBy = "§7Verkauft von: ";
	public static final String sellSite = "§7Klicke auf Item zum kaufen";
	
	public static final String back = "§cSeite zurück";
	public static final String forth = "§aSeite vor";
	public static final String infoLoreOne = "§7Klicke auf §c" + back + "§7 oder §a" + forth;
	public static final String infoLoreTwo = "§7um dich durch den Marktplatz zu bewegen";
	
	public static final String cantTakeSell = prefix + "§7Dieses Item wird zum Verkauf angeboten. §cEs kann nicht entfernt werden";
	public static final String cantBuyFull = prefix + "§7Du hast §cnicht genug Platz im Inventar§7, um diese Items zu kaufen";
	public static final String cantBuyBroke = prefix + "§7Du hast §cnicht genug Geld§7, um diese Items zu kaufen";
	
	public static final String nowSold = prefix + "§7Die gewählten Items werden nun auf dem Markt §averkauft";
	public static final String nowStore = prefix + "§7Die gewählten Items werden nun §cnicht mehr§7 auf dem Markt verkauft";
	
	public static void sendBoughtMessage(Player p, Offer o) {
		p.sendMessage("§7Du hast erfolgreich §e" + o.getItem().getAmount() + " §7Items gekauft");
	}
	
}
