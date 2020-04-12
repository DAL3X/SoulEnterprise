package de.dal3x.enterprise.output;

import org.bukkit.entity.Player;

public class CompanyProductionOutput {

    public static final String prefix = CompanyManageOutput.prefix;

    // Storage GUI
    public static final String back = "§cSeite zurück";
    public static final String forth = "§aSeite vor";
    public static final String info = "§7Info";
    public static final String infoLoreOne = "§7Klicke auf §c" + back + " §7oder §a" + forth;
    public static final String infoLoreTwo = "§7um dich durch das Firmenlager zu bewegen";
    public static final String cantStoreType = prefix
            + "§7Dieses Item §ckann nicht §7von deiner Firma §cverkauft werden";

    // Production GUI
    public static final String setProductionONE = "§7Klicke um den Produktions-Slot";
    public static final String setProductionTWO = "§7auf dieses Item umzustellen";
    public static final String prodForth = "§7Produktions-Slot §avor";
    public static final String prodBack = "§7Produktions-Slot §czurück";
    public static final String prodInfoLoreOne = "§7Klicke auf §avor §7und §czurück";
    public static final String prodInfoLoreTwo = "§7um die Produktions-Slots zu wechseln";
    public static final String nowProducing = "§7Dieses Item wird im Moment produziert";

    // Upgrade GUI
    public static final String employeeSlot = "§7Mitarbeiter-Plätze";
    public static final String productionSlots = "§7Produktions-Plätze";
    public static final String efficiency = "§7Produktions-Effektivität";
    public static final String storageAmount = "§7Lager-Plätze";
    public static final String price = "§7Preis: ";
    public static final String money = " ⛂";
    public static final String valueNow = "§7Momentan ";
    public static final String plus = "§7Verbessert um ";

    public static final String storageFull = prefix
            + "§7Produzierte Güter §ckonnten nicht eingelagert werden§7, weil das §cFirmenlager voll§7 ist";
    public static final String notEnoughMoney = prefix
            + "§7Deine Firma hat §cnicht genug Geld§7 um sich dieses Upgrade zu leisten";

    public static void sendNewProd(Player p, int slot) {
        p.sendMessage(prefix + "§7Das Produkt für Produktions-Slot§e " + slot + "§7 wurde aktualisiert");
    }
}
