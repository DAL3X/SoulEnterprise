package de.dal3x.enterprise.inventory;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.dal3x.enterprise.company.Company;
import de.dal3x.enterprise.company.CompanyHelper;
import de.dal3x.enterprise.enums.ProductionBranch;
import de.dal3x.enterprise.file.Filehandler;
import de.dal3x.enterprise.main.EnterprisePlugin;
import de.dal3x.enterprise.output.CompanyManageOutput;
import de.dal3x.enterprise.output.CompanyProductionOutput;
import de.dal3x.enterprise.output.MarketOutput;

public class StorageGUI extends InventoryGUI {

    private Company comp;
    private int invPos;
    private HashMap<ProductionBranch, HashMap<Integer, Material>> itemBlueprint;

    public StorageGUI(Company comp) {
        super(18, "Firmen Lager");
        this.itemBlueprint = EnterprisePlugin.getInstance().getItemBlueprint();
        this.comp = comp;
        this.invPos = 0;
    }

    // You can call this whenever you want to put the items in
    public void initializeItems(int invNumber) {
        Inventory target = this.comp.getStorage().getInventorys().get(invNumber);
        for (int i = 0; i < target.getSize(); i++) {
            this.inv.setItem(i, target.getItem(i));
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
        for (int i = 9; i < 18; i++) {
            this.inv.setItem(i, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, ""));
        }
        this.inv.setItem(9, back);
        this.inv.setItem(13, info);
        this.inv.setItem(17, forth);
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

        if (clickedItem == null) {
            return;
        }
        if (e.getRawSlot() == 9) {
            showLastPage(p);
        }
        if (e.getRawSlot() == 17) {
            showNextPage(p);
        }
        if (e.getRawSlot() > 17) {
            storeItem(e.getRawSlot(), p);
        }
        if (e.getRawSlot() < 9) {
            takeItem(e.getRawSlot(), p);
        }
    }

    private void storeItem(int slot, Player p) {
        int pSlot = (slot - this.inv.getSize()) + 9;
        if (pSlot > 35) {
            pSlot -= 36;
        }
        ItemStack item = p.getInventory().getItem(pSlot);
        if (!isAllowedMaterial(item.getType())) {
            p.sendMessage(CompanyProductionOutput.cantStoreType);
            return;
        }
        HashMap<Integer, ItemStack> cantStore = comp.addToStorage(item);
        if (cantStore.keySet().size() == 0) {
            p.getInventory().setItem(pSlot, null);
        } else {
            p.getInventory().setItem(pSlot, cantStore.get(0));
        }
        p.updateInventory();
        openInventory(p, this.invPos);
        Filehandler.storeCompany(comp);
    }

    private void takeItem(int slot, Player p) {
        if (!CompanyHelper.isManagement(p.getUniqueId())) {
            p.sendMessage(CompanyManageOutput.youNoManagement);
            return;
        }
        ItemStack item = this.inv.getItem(slot).clone();
        if (this.comp.getOfferedMaterials().contains(item.getType())) {
            p.sendMessage(MarketOutput.cantTakeSell);
            return;
        }
        comp.getStorage().removeItem(item);
        HashMap<Integer, ItemStack> cantStore = p.getInventory().addItem(item);
        if (cantStore.keySet().size() != 0) {
            comp.addToStorage(cantStore.get(0));
        }
        p.updateInventory();
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

    private boolean isAllowedMaterial(Material material) {
        return this.itemBlueprint.get(this.comp.getBranch()).containsValue(material);
    }

}
