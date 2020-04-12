package de.dal3x.enterprise.inventory;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import de.dal3x.enterprise.company.Company;
import de.dal3x.enterprise.config.Config;
import de.dal3x.enterprise.file.Filehandler;
import de.dal3x.enterprise.output.CompanyProductionOutput;
import de.dal3x.enterprise.upgrades.UpgradeStatus;

public class UpgradeGUI extends InventoryGUI {

	private Company comp;

	public UpgradeGUI(Company comp) {
		super(27, "Firmen Upgrades");
		this.comp = comp;
	}

	// You can call this whenever you want to put the items in
	public void initializeItems() {
		for (int i = 0; i < this.inv.getSize(); i++) {
			this.inv.setItem(i, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, ""));
		}
		// EmployeeSlots
		String moneyLore = CompanyProductionOutput.price + Config.employeePrice + CompanyProductionOutput.money;
		String nowLore = CompanyProductionOutput.valueNow + this.comp.getStatus().getEmployeeSlots();
		String addLore = CompanyProductionOutput.plus + Config.addsEmployeeSlots;
		ItemStack employeeSlots = createGuiItem(Material.PLAYER_HEAD, CompanyProductionOutput.employeeSlot, moneyLore,
				nowLore, addLore);
		// ProductionSlots
		moneyLore = CompanyProductionOutput.price + Config.productionSlotsPrice + CompanyProductionOutput.money;
		nowLore = CompanyProductionOutput.valueNow + this.comp.getStatus().getProductionSlots();
		addLore = CompanyProductionOutput.plus + Config.addsProductionSlots;
		ItemStack productionSlots = createGuiItem(Material.STONECUTTER, CompanyProductionOutput.productionSlots,
				moneyLore, nowLore, addLore);
		// Efficiency
		moneyLore = CompanyProductionOutput.price + Config.efficiencyPrice + CompanyProductionOutput.money;
		int effRound = (int) (this.comp.getStatus().getEfficiency() * 100);
		nowLore = CompanyProductionOutput.valueNow + effRound + "%";
		int effAddRound = (int) (Config.addsEfficiency * 100);
		addLore = CompanyProductionOutput.plus + effAddRound + "%";
		ItemStack efficiency = createGuiItem(Material.DIAMOND, CompanyProductionOutput.efficiency, moneyLore, nowLore,
				addLore);
		// StorageSlots
		moneyLore = CompanyProductionOutput.price + Config.storagePrice + CompanyProductionOutput.money;
		nowLore = CompanyProductionOutput.valueNow + this.comp.getStatus().getStorageAmount();
		addLore = CompanyProductionOutput.plus + Config.addsStorage;
		ItemStack storageAmount = createGuiItem(Material.CHEST, CompanyProductionOutput.storageAmount, moneyLore,
				nowLore, addLore);

		this.inv.setItem(10, employeeSlots);
		this.inv.setItem(12, productionSlots);
		this.inv.setItem(14, efficiency);
		this.inv.setItem(16, storageAmount);
	}

	// You can open the inventory with this
	public void openInventory(Player p) {
		this.player = p;
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
                initializeItems();
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

		switch (e.getRawSlot()) {
		case 10:
			upgradeEmployeeSlots(p);
			break;
		case 12:
			upgradeProductSlots(p);
			break;
		case 14:
			upgradeEfficiency(p);
			break;
		case 16:
			upgradeStorageSlots(p);
			break;
		}
		Filehandler.storeCompany(comp);
	}

	private void upgradeEmployeeSlots(Player p) {
		if (comp.getMoney() < Config.employeePrice) {
			p.sendMessage(CompanyProductionOutput.notEnoughMoney);
			return;
		}
		comp.setMoney(comp.getMoney() - Config.employeePrice);
		UpgradeStatus status = comp.getStatus();
		status.addEmployeeSlots(Config.addsEmployeeSlots);
		comp.setStatus(status);
		this.openInventory(p);
	}

	private void upgradeProductSlots(Player p) {
		if (comp.getMoney() < Config.productionSlotsPrice) {
			p.sendMessage(CompanyProductionOutput.notEnoughMoney);
			return;
		}
		comp.setMoney(comp.getMoney() - Config.productionSlotsPrice);
		UpgradeStatus status = comp.getStatus();
		status.addProductionSlots(Config.addsProductionSlots);
		comp.setStatus(status);
		comp.upgradeProductionSlots();
		this.openInventory(p);
	}

	private void upgradeEfficiency(Player p) {
		if (comp.getMoney() < Config.efficiencyPrice) {
			p.sendMessage(CompanyProductionOutput.notEnoughMoney);
			return;
		}
		comp.setMoney(comp.getMoney() - Config.efficiencyPrice);
		UpgradeStatus status = comp.getStatus();
		status.addEfficiency(Config.addsEfficiency);
		comp.setStatus(status);
		this.openInventory(p);
	}

	private void upgradeStorageSlots(Player p) {
		if (comp.getMoney() < Config.storagePrice) {
			p.sendMessage(CompanyProductionOutput.notEnoughMoney);
			return;
		}
		comp.setMoney(comp.getMoney() - Config.storagePrice);
		UpgradeStatus status = comp.getStatus();
		status.addStorageAmount(Config.addsStorage);
		comp.setStatus(status);
		comp.updateStorageSize();
		this.openInventory(p);
	}

}
