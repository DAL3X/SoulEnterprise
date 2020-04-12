package de.dal3x.enterprise.inventory;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import de.dal3x.enterprise.company.Company;
import de.dal3x.enterprise.enums.ProductionBranch;
import de.dal3x.enterprise.file.Filehandler;
import de.dal3x.enterprise.main.EnterprisePlugin;
import de.dal3x.enterprise.output.CompanyProductionOutput;

public class ProductionGUI extends InventoryGUI {

	private static int slots = 45;
	private HashMap<ProductionBranch, HashMap<Integer, Material>> itemBlueprint;

	private ProductionBranch branch;
	private int productSlot;
	private Company comp;

	public ProductionGUI(Company comp) {
		super(slots, "Produktions Menü");
		this.itemBlueprint = EnterprisePlugin.getInstance().getItemBlueprint();
		this.branch = comp.getBranch();
		this.comp = comp;
		this.productSlot = 0;
	}

	// You can call this whenever you want to put the items in
	public void initializeItems(int productSlot) {
		for (int i = 0; i < this.inv.getSize(); i++) {
			this.inv.setItem(i, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, ""));
		}
		HashMap<Integer, Material> itemSlots = itemBlueprint.get(this.branch);
		if (itemSlots == null) {
			return;
		}
		for (int slot : itemSlots.keySet()) {
			String lO = CompanyProductionOutput.setProductionONE;
			String lT = CompanyProductionOutput.setProductionTWO;
			ItemStack item = createGuiItem(itemSlots.get(slot), "", lO, lT);
			this.inv.setItem(slot, item);
		}
		ItemStack back = createGuiItem(Material.RED_STAINED_GLASS_PANE, CompanyProductionOutput.prodBack);
		ItemStack forth = createGuiItem(Material.GREEN_STAINED_GLASS_PANE, CompanyProductionOutput.prodForth);
		String iLO = CompanyProductionOutput.prodInfoLoreOne;
		String iLT = CompanyProductionOutput.prodInfoLoreTwo;
		int modSlot = productSlot + 1;
		ItemStack info = createGuiItem(Material.CYAN_STAINED_GLASS_PANE, "Produktions-Slot " + modSlot, iLO, iLT);
		if (modSlot > 0 && modSlot < 64) {
			info.setAmount(modSlot);
		}
		this.inv.setItem(36, back);
		this.inv.setItem(40, info);
		this.inv.setItem(44, forth);
		Material mat = this.comp.getProduct().get(this.productSlot);
		if (mat != null) {
			ItemStack item = createGuiItem(mat, "", CompanyProductionOutput.nowProducing);
			this.inv.setItem(39, item);
			this.inv.setItem(41, item);
		}
	}

	// You can open the inventory with this
	public void openInventory(Player p, int productSlot) {
		this.player = p;
		this.productSlot = productSlot;
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
                initializeItems(this.productSlot);
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

		if (clickedItem == null || e.getRawSlot() > 44) {
			return;
		}
		if (e.getRawSlot() < 36) {
			if (clickedItem.getType() == Material.BLACK_STAINED_GLASS_PANE) {
				return;
			}
			comp.addProduct(productSlot, clickedItem.getType());
			CompanyProductionOutput.sendNewProd(p, this.productSlot + 1);
			openInventory(p, this.productSlot);
			Filehandler.storeCompany(comp);
			return;
		}
		HashMap<Integer, Material> products = comp.getProduct();
		if (e.getRawSlot() == 36) {
			int slot = this.productSlot - 1;
			if (slot < 0) {
				slot = products.size() - 1;
			}
			openInventory(p, slot);
			return;
		}
		if (e.getRawSlot() == 44) {
			int slot = this.productSlot + 1;
			if (slot > products.size() - 1) {
				slot = 0;
			}
			openInventory(p, slot);
			return;
		}
	}

}
