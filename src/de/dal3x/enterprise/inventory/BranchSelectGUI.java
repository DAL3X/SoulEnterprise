package de.dal3x.enterprise.inventory;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import de.dal3x.enterprise.enums.ProductionBranch;

public class BranchSelectGUI extends InventoryGUI {

	public BranchSelectGUI() {
		super(9, "Wähle Produktart");
	}

	// You can call this whenever you want to put the items in
	public void initializeItems() {
		ItemStack blocks = createGuiItem(Material.OAK_LOG, ProductionBranch.translate(ProductionBranch.values()[0].toString()));
		ItemStack materials = createGuiItem(Material.SLIME_BALL, ProductionBranch.translate(ProductionBranch.values()[1].toString()));
		ItemStack agricult = createGuiItem(Material.CARROT, ProductionBranch.translate(ProductionBranch.values()[2].toString())); 
		ItemStack hunter = createGuiItem(Material.STRING, ProductionBranch.translate(ProductionBranch.values()[3].toString()));
		for (int i = 0; i < 9; i++) {
			this.inv.setItem(i, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, ""));
		}
		this.inv.setItem(1, blocks);
		this.inv.setItem(3, materials);
		this.inv.setItem(5, agricult);
		this.inv.setItem(7, hunter);
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
		if (e.getRawSlot() == 1) {
			new MaterialSelectGUI(ProductionBranch.values()[0]).openInventory(p);
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
		if (e.getRawSlot() == 7) {
			new MaterialSelectGUI(ProductionBranch.values()[3]).openInventory(p);
			return;
		}
	}

}
