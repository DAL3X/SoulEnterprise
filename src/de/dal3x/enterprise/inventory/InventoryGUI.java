package de.dal3x.enterprise.inventory;

import java.util.ArrayList;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.dal3x.enterprise.main.EnterprisePlugin;

public abstract class InventoryGUI implements InventoryHolder, Listener {

	protected Inventory inv;
	protected boolean isClosing;
	protected static HashSet<InventoryGUI> lookup = new HashSet<InventoryGUI>();
	protected Player player;

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	protected InventoryGUI(int slots, String name) {
		this.isClosing = true;
		this.inv = Bukkit.createInventory(this, slots, name);
		EnterprisePlugin.getInstance().getServer().getPluginManager().registerEvents(this,
				EnterprisePlugin.getInstance());
	}

	public void refreshInventory(Player p) {
	}

	// Nice little method to create a gui item with a custom name, and description
	protected ItemStack createGuiItem(Material material, String name, String... lore) {
		ItemStack item = new ItemStack(material, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		ArrayList<String> metaLore = new ArrayList<String>();

		for (String loreComments : lore) {
			metaLore.add(loreComments);
		}

		meta.setLore(metaLore);
		item.setItemMeta(meta);
		return item;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryClose(InventoryCloseEvent e) {
		final InventoryGUI gui = this;
		if (this.isClosing && e.getInventory().equals(this.inv)) {
			Bukkit.getScheduler().runTask(EnterprisePlugin.getInstance(), new Runnable() {
				public void run() {
					HandlerList.unregisterAll(gui);
					lookup.remove(gui);
				}
			});
		}
	}

	public Inventory getInventory() {
		return this.inv;
	}

}
