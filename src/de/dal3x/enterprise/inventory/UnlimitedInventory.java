package de.dal3x.enterprise.inventory;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.dal3x.enterprise.config.Config;

public class UnlimitedInventory {

	private List<Inventory> inventorys;

	public UnlimitedInventory(int slots, List<ItemStack> items) {
		this.inventorys = new LinkedList<Inventory>();
		for (int i = 0; i < (slots / 9); i++) {
			this.inventorys.add(Bukkit.createInventory(null, 9));
		}
		for (ItemStack item : items) {
			addItem(item);
		}
	}

	public UnlimitedInventory() {
		this.inventorys = new LinkedList<Inventory>();
		for (int i = 0; i < (Config.baseStorage / 9); i++) {
			this.inventorys.add(Bukkit.createInventory(null, 9));
		}
	}

	public int getSlots() {
		return this.inventorys.size() * 9;
	}

	public void addSlots(int amount) {
		for (int i = 0; i < (amount / 9); i++) {
			this.inventorys.add(Bukkit.createInventory(null, 9));
		}
	}

	public HashMap<Integer, ItemStack> addItem(ItemStack item) {
		HashMap<Integer, ItemStack> couldntStore = new HashMap<Integer, ItemStack>();
		couldntStore.put(0, item);
		for (Inventory inventory : this.inventorys) {
			if (couldntStore.keySet().size() == 0) {
				break;
			}
			couldntStore = inventory.addItem(couldntStore.get(0));
		}
		return couldntStore;
	}

	public void addAllItems(Collection<ItemStack> items) {
		for (ItemStack item : items) {
			addItem(item);
		}
	}

	public HashMap<Integer, ItemStack> removeItem(ItemStack item) {
		HashMap<Integer, ItemStack> couldntRemove = new HashMap<Integer, ItemStack>();
		couldntRemove.put(0, item);
		for (int i = this.inventorys.size() - 1; i >= 0; i--) {
			Inventory inventory = this.inventorys.get(i);
			couldntRemove = inventory.removeItem(couldntRemove.get(0));
			if (couldntRemove.keySet().size() == 0) {
				if (hasEmptySlot(inventory)) {
					fillWithLastItem(inventory);
				}
				break;
			}
		}
		return couldntRemove;
	}

	private boolean hasEmptySlot(Inventory inv) {
		for (ItemStack item : inv.getStorageContents()) {
			if (item == null) {
				return false;
			}
		}
		return true;
	}

	public boolean containsItem(ItemStack item) {
		Material mat = item.getType();
		int neededAmount = item.getAmount();
		List<ItemStack> contents = getContent();
		int amount = 0;
		for (ItemStack i : contents) {
			if (i.getType() == mat) {
				amount += i.getAmount();
			}
		}
		if (amount >= neededAmount) {
			return true;
		}
		return false;
	}
	
	public boolean contains(Material mat, int neededAmount) {
		List<ItemStack> contents = getContent();
		int amount = 0;
		for (ItemStack i : contents) {
			if (i.getType() == mat) {
				amount += i.getAmount();
			}
		}
		if (amount >= neededAmount) {
			return true;
		}
		return false;
	}

	public List<ItemStack> getContent() {
		List<ItemStack> contents = new LinkedList<ItemStack>();
		for (Inventory inventory : this.inventorys) {
			for (ItemStack item : inventory.getContents()) {
				if (item != null) {
					contents.add(item);
				}
			}
		}
		return contents;
	}

	public void removeAllItems(Collection<ItemStack> items) {
		for (ItemStack item : items) {
			removeItem(item);
		}
	}

	public boolean hasFreeSlot() {
		for (int i = this.inventorys.size() - 1; i >= 0; i--) {
			Inventory inv = this.inventorys.get(i);
			for (ItemStack slot : inv.getContents()) {
				if (slot == null) {
					return true;
				}
			}
		}
		return false;
	}

	private void fillWithLastItem(Inventory target) {
		for (int i = this.inventorys.size() - 1; i >= 0; i--) {
			Inventory inv = this.inventorys.get(i);
			for (int j = inv.getSize() - 1; j > 0; j--) {
				ItemStack slot = inv.getItem(j);
				ItemStack next = inv.getItem(j - 1);
				if (slot == null && next != null) {
					target.addItem(next);
					inv.setItem(j - 1, null);
					return;
				}
			}
		}
	}

	public List<Inventory> getInventorys() {
		return inventorys;
	}

}
