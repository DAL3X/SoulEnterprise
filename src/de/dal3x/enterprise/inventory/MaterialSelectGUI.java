package de.dal3x.enterprise.inventory;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import de.dal3x.enterprise.enums.ProductionBranch;
import de.dal3x.enterprise.file.Filehandler;
import de.dal3x.enterprise.output.MarketOutput;

public class MaterialSelectGUI extends InventoryGUI {

	private ProductionBranch branch;

	public MaterialSelectGUI(ProductionBranch branch) {
		super(45, "Wähle Item");
		this.branch = branch;
	}

	// You can call this whenever you want to put the items in
	public void initializeItems() {
		for (int i = 0; i < this.inv.getSize(); i++) {
			this.inv.setItem(i, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, ""));
		}
		int i = 0;
		for (Material mat : Filehandler.getMaterialOffers(branch)) {
			ItemStack item = this.createGuiItem(mat, "", MarketOutput.buyThis);
			this.inv.setItem(i, item);
			i++;
		}
		if (i == 0) {
			ItemStack empty = this.createGuiItem(Material.ORANGE_STAINED_GLASS_PANE, MarketOutput.noOffers);
			this.inv.setItem(22, empty);
		}
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
		if (e.getRawSlot() > 44) {
			return;
		}
		if (clickedItem.getType() == Material.BLACK_STAINED_GLASS_PANE) {
			return;
		}
		if (clickedItem.getType() == Material.ORANGE_STAINED_GLASS_PANE) {
			return;
		}
		new MarketGUI(this.branch, clickedItem.getType()).openInventory(p, 0);
	}

}
