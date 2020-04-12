package de.dal3x.enterprise.market;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

import de.dal3x.enterprise.company.Company;
import de.dal3x.enterprise.enums.ProductionBranch;
import de.dal3x.enterprise.file.Filehandler;
import de.dal3x.enterprise.main.EnterprisePlugin;
import de.dal3x.soulcore.docking.VaultBankDock;

public class MarketPlace {

    public static void storeCompanyOffer(ProductionBranch b, Offer offer) {
        Filehandler.storeOffer(b, offer, true);
    }

    public static void storePrivateOffer(ProductionBranch b, Offer offer) {
        Filehandler.storeOffer(b, offer, false);
    }

    public static Set<Material> getMaterialOffers(ProductionBranch b) {
        return Filehandler.getMaterialOffers(b);
    }

    public static List<Offer> loadOffers(ProductionBranch b, Material m) {
        List<Offer> offers = Filehandler.loadOffers(b, m);
        sortByPrice(offers);
        return offers;
    }

    public static Offer loadOffer(ProductionBranch b, Material m, UUID id) {
        return Filehandler.loadOffer(b, m, id);
    }

    public static void doTransaction(Offer o) {
        if (o.isCompany()) {
            if (EnterprisePlugin.getInstance().getCompanyForName(o.getSeller()) == null) {
                Filehandler.loadCompany(o.getSeller());
            }
            Company comp = EnterprisePlugin.getInstance().getCompanyForName(o.getSeller());
            double price = o.getPrice() * o.getItem().getAmount();
            comp.gainMoney(price);
            comp.getStorage().removeItem(o.getItem());
            comp.deleteOffer(o.getTransactionID());
            Filehandler.storeCompany(comp);
        }
        else {
            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(o.getSeller()));
            VaultBankDock.addPlayerMoney(player, o.getPrice() * o.getItem().getAmount());
        }
    }

    public static void deleteOffer(ProductionBranch b, Material m, UUID id) {
        Filehandler.deleteOffer(b, m, id);
    }

    public static void sortByPrice(List<Offer> list) {
        if (list.size() < 2) {
            return;
        }
        int mid = list.size() / 2;
        List<Offer> left = new LinkedList<Offer>(list.subList(0, mid));
        List<Offer> right = new LinkedList<Offer>(list.subList(mid, list.size()));

        sortByPrice(left);
        sortByPrice(right);
        merge(left, right, list);
    }

    private static void merge(List<Offer> left, List<Offer> right, List<Offer> list) {
        int leftIndex = 0;
        int rightIndex = 0;
        int listIndex = 0;

        while (leftIndex < left.size() && rightIndex < right.size()) {
            if (left.get(leftIndex).getPrice() < right.get(rightIndex).getPrice()) {
                list.set(listIndex++, left.get(leftIndex++));
            } else {
                list.set(listIndex++, right.get(rightIndex++));
            }
        }
        while (leftIndex < left.size()) {
            list.set(listIndex++, left.get(leftIndex++));
        }
        while (rightIndex < right.size()) {
            list.set(listIndex++, right.get(rightIndex++));
        }
    }

}