package de.dal3x.enterprise.inventory;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Inventory;

import de.dal3x.enterprise.docking.VaultDock;
import de.dal3x.enterprise.enums.ProductionBranch;
import de.dal3x.enterprise.market.MarketPlace;
import de.dal3x.enterprise.market.Offer;
import de.dal3x.enterprise.output.CompanyProductionOutput;
import de.dal3x.enterprise.output.MarketOutput;

public class MarketGUI extends InventoryGUI {

    private ProductionBranch branch;
    private Material mat;
    private int invPos;
    private List<Inventory> inventories;
    private Offer[] offers;

    public MarketGUI(ProductionBranch branch, Material mat) {
        super(54, "Wähle Angebot");
        this.branch = branch;
        this.mat = mat;
        this.inventories = new LinkedList<Inventory>();
        initInventories();
    }

    // You can call this whenever you want to put the items in
    public void initializeItems(int invNumber) {
        for (int i = 0; i < this.inv.getSize(); i++) {
            this.inv.setItem(i, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, ""));
        }
        for (int i = 0; i < this.inventories.get(invNumber).getContents().length; i++) {
            if (this.inventories.get(invNumber).getItem(i) != null) {
                this.inv.setItem(i, this.inventories.get(invNumber).getItem(i));
            }
        }
        ItemStack back = createGuiItem(Material.RED_STAINED_GLASS_PANE, MarketOutput.back);
        ItemStack forth = createGuiItem(Material.GREEN_STAINED_GLASS_PANE, MarketOutput.forth);
        String iLO = MarketOutput.infoLoreOne;
        String iLT = MarketOutput.infoLoreTwo;
        ItemStack info = createGuiItem(Material.CYAN_STAINED_GLASS_PANE, MarketOutput.sellSite, iLO, iLT);
        this.inv.setItem(45, back);
        this.inv.setItem(49, info);
        this.inv.setItem(53, forth);
    }

    // You can open the inventory with this
    public void openInventory(Player p, int invNumber) {
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

        if (clickedItem == null || clickedItem.getType() == Material.BLACK_STAINED_GLASS_PANE) {
            return;
        }
        if (e.getRawSlot() == 45) {
            showLastPage(p);
            return;
        }
        if (e.getRawSlot() == 53) {
            showNextPage(p);
            return;
        }
        if (e.getRawSlot() < 45) {
            buyItem(e.getRawSlot(), p);
            return;
        }
    }

    private void initInventories() {
        List<Offer> offerList = MarketPlace.loadOffers(branch, mat);
        this.offers = new Offer[offerList.size()];
        for (int j = 0; j < offerList.size(); j++) {
            this.offers[j] = offerList.get(j);
        }
        for (int i = 0; i < this.offers.length; i = i + 45) {
            this.inventories.add(Bukkit.createInventory(null, 45));
        }
        int invPick = 0;
        int slotPick = 0;
        for (Offer o : this.offers) {
            this.inventories.get(invPick).setItem(slotPick, createShopGUIItem(o));
            slotPick++;
            if (slotPick == 45) {
                invPick++;
                slotPick = 0;
            }
        }
    }

    ItemStack createShopGUIItem(Offer o) {
        String seller = o.getSeller();
        double price = o.getPrice();
        String sL = MarketOutput.soldBy + seller;
        String lO = MarketOutput.sellsFor;
        String lT = price + CompanyProductionOutput.money + MarketOutput.sellPerPiece;
        double fullPrice = price * o.getItem().getAmount();
        String fpL = fullPrice + CompanyProductionOutput.money + MarketOutput.wholeStack;
        ItemStack item = createGuiItem(o.getItem().getType(), "", sL, lO, lT, fpL);
        item.setAmount(o.getItem().getAmount());
        return item;
    }

    private void showNextPage(Player p) {
        int next = this.invPos + 1;
        if (next >= inventories.size()) {
            next = 0;
        }
        openInventory(p, next);
    }

    private void showLastPage(Player p) {
        int last = this.invPos - 1;
        if (last < 0) {
            last = inventories.size() - 1;
        }
        openInventory(p, last);
    }

    private void buyItem(int slot, Player p) {
        Offer o = this.offers[slot];
        if (!hasInvSpace(p, o.getItem())) {
            p.sendMessage(MarketOutput.cantBuyFull);
            return;
        }
        double price = o.getPrice() * o.getItem().getAmount();
        if (!VaultDock.hasMoney(p, price)) {
            p.sendMessage(MarketOutput.cantBuyBroke);
            return;
        }
        MarketOutput.sendBoughtMessage(p, o);
        p.getInventory().addItem(o.getItem());
        p.updateInventory();
        VaultDock.removeMoney(p, price);
        MarketPlace.deleteOffer(this.branch, this.mat, o.getTransactionID());
        MarketPlace.doTransaction(o);
        this.offers[slot] = null;
        for (InventoryGUI invGUI : lookup) {
            if (invGUI instanceof MarketGUI && invGUI.getInventory().getViewers().size() != 0) {
                ItemStack filler = createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "");
                ((MarketGUI) invGUI).inventories.get(this.invPos).setItem(slot, filler);
            }
        }
        openInventory(p, this.invPos);
    }

    private boolean hasInvSpace(Player p, ItemStack item) {
        int freeAmount = 0;
        for (int i = 0; i < 36; i++) {
            ItemStack it = p.getInventory().getItem(i);
            if (it != null && it.getType() == item.getType()) {
                freeAmount = freeAmount + (it.getMaxStackSize() - it.getAmount());
            }
            if (it == null) {
                freeAmount = freeAmount + item.getMaxStackSize();
            }
        }
        if (freeAmount < item.getAmount()) {
            return false;
        }
        return true;
    }
}
