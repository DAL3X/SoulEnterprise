package de.dal3x.enterprise.inventory;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.dal3x.enterprise.company.Company;
import de.dal3x.enterprise.file.Filehandler;
import de.dal3x.enterprise.market.Offer;
import de.dal3x.enterprise.output.CompanyProductionOutput;
import de.dal3x.enterprise.output.MarketOutput;

public class PriceGUI extends InventoryGUI {

    private Company comp;
    private int invPos;
    private double price;

    public PriceGUI(Company comp, double price) {
        super(27, "Verkaufs Menü");
        this.comp = comp;
        this.invPos = 0;
        this.price = price;
    }

    // You can call this whenever you want to put the items in
    public void initializeItems(int invNumber) {
        Inventory target = this.comp.getStorage().getInventorys().get(invNumber);
        for (int i = 18; i < 27; i++) {
            this.inv.setItem(i, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, ""));
        }
        for (int i = 0; i < target.getSize(); i++) {
            if (target.getItem(i) != null && comp.getOfferedMaterials().contains(target.getItem(i).getType())) {
                String lO = MarketOutput.sellsFor;
                String lT = comp.getPriceFor(target.getItem(i).getType()) + CompanyProductionOutput.money
                        + MarketOutput.sellPerPiece;
                double fullPrice = comp.getPriceFor(target.getItem(i).getType()) * target.getItem(i).getAmount();
                String fpL = fullPrice + CompanyProductionOutput.money + MarketOutput.wholeStack;
                ItemStack sell = createGuiItem(Material.GOLD_NUGGET, MarketOutput.isSelling, lO, lT, fpL);
                this.inv.setItem(i, target.getItem(i));
                this.inv.setItem(i + 9, sell);
            } else if (target.getItem(i) != null) {
                ItemStack hold = createGuiItem(Material.CHEST, MarketOutput.isStored, MarketOutput.storeLore);
                this.inv.setItem(i, target.getItem(i));
                this.inv.setItem(i + 9, hold);
            } else {
                this.inv.setItem(i, null);
                this.inv.setItem(i + 9, null);
            }
        }
        ItemStack back = createGuiItem(Material.RED_STAINED_GLASS_PANE, CompanyProductionOutput.back);
        ItemStack forth = createGuiItem(Material.GREEN_STAINED_GLASS_PANE, CompanyProductionOutput.forth);
        String iLO = CompanyProductionOutput.infoLoreOne;
        String iLT = CompanyProductionOutput.infoLoreTwo;
        int modNumber = invNumber + 1;
        ItemStack info = createGuiItem(Material.CYAN_STAINED_GLASS_PANE, CompanyProductionOutput.info, iLO, iLT);
        if (modNumber > 0 && modNumber < 64) {
            info.setAmount(modNumber);
        }
        this.inv.setItem(18, back);
        this.inv.setItem(22, info);
        this.inv.setItem(26, forth);
    }

    // You can open the inventory with this
    public void openInventory(Player p, int invNumber) {
        if (lookup.contains(this)) {
            lookup.remove(this);
        }
        this.player = p;
        this.invPos = invNumber;
        lookup.add(this);
        if (!this.inv.getViewers().contains(p)) {
            p.openInventory(this.inv);
        }
        for (InventoryGUI invGUI : lookup) {
            invGUI.refreshInventory(invGUI.getPlayer());
        }
    }

    @Override
    public void refreshInventory(Player p) {
        if (p.equals(this.player)) {
            if (this.getInventory().getViewers().contains(p)) {
                initializeItems(this.invPos);
                this.isClosing = false;
                p.openInventory(this.inv);
                this.isClosing = true;
            } else {
                lookup.remove(this);
            }
        }
    }

    // Check for clicks on items
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() != this) {
            return;
        }
        e.setCancelled(true);

        Player p = (Player) e.getWhoClicked();
        ItemStack clickedItem = e.getCurrentItem();

        if (clickedItem == null) {
            return;
        }
        if (e.getRawSlot() == 18) {
            showLastPage(p);
        }
        if (e.getRawSlot() == 26) {
            showNextPage(p);
        }
        if (e.getRawSlot() < 9) {
            if (this.inv.getItem(e.getRawSlot() + 9).getType() == Material.GOLD_NUGGET) {
                notSellItem(clickedItem.getType(), p);
                return;
            }
            if (this.inv.getItem(e.getRawSlot() + 9).getType() == Material.CHEST && price >= 0) {
                sellItem(clickedItem.getType(), p);
                return;
            }
        }
    }

    private void sellItem(Material mat, Player p) {
        for (ItemStack item : this.comp.getStorage().getContent()) {
            if (item.getType() == mat) {
                this.comp.addOffer(new Offer(item, price, comp.getName(), true));
            }
        }
        p.sendMessage(MarketOutput.nowSold);
        openInventory(p, this.invPos);
        Filehandler.storeCompany(comp);
    }

    private void notSellItem(Material mat, Player p) {
        List<UUID> todelete = new LinkedList<UUID>();
        for (UUID id : this.comp.getMarketOffer().keySet()) {
            if (this.comp.getMarketOffer().get(id).getItem().getType() == mat) {
                todelete.add(id);
            }
        }
        for (UUID id : todelete) {
            this.comp.deleteOffer(id);
        }
        p.sendMessage(MarketOutput.nowStore);
        openInventory(p, this.invPos);
        Filehandler.storeCompany(comp);
    }

    private void showNextPage(Player p) {
        int next = this.invPos + 1;
        if (next >= this.comp.getStorage().getInventorys().size()) {
            next = 0;
        }
        openInventory(p, next);
    }

    private void showLastPage(Player p) {
        int last = this.invPos - 1;
        if (last < 0) {
            last = this.comp.getStorage().getInventorys().size() - 1;
        }
        openInventory(p, last);
    }

}
