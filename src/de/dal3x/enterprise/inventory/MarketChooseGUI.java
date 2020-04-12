package de.dal3x.enterprise.inventory;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import de.dal3x.enterprise.enums.ProductionBranch;

public class MarketChooseGUI extends InventoryGUI {

    public MarketChooseGUI() {
        super(9, "Einzelhandle oder Groﬂhandel");
    }

    // You can call this whenever you want to put the items in
    public void initializeItems() {
        ItemStack smallmarket = createGuiItem(Material.PLAYER_HEAD, "Einzelhandel");
        ItemStack bigmarket = createGuiItem(Material.ENDER_CHEST, "Groﬂhandel");
        for (int i = 0; i < 9; i++) {
            this.inv.setItem(i, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, ""));
        }
        this.inv.setItem(3, smallmarket);
        this.inv.setItem(5, bigmarket);
    }

    // You can open the inventory with this
    public void openInventory(Player p) {
            initializeItems();
            this.isClosing = false;
            p.openInventory(this.inv);
            this.isClosing = true;
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
        if (e.getRawSlot() == 3) {
            new MaterialSelectGUI(ProductionBranch.values()[1]).openInventory(p);
            return;
        }
        if (e.getRawSlot() == 5) {
            new MaterialSelectGUI(ProductionBranch.values()[2]).openInventory(p);
            return;
        }
    }

}