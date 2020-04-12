package de.dal3x.enterprise.docking;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import de.dal3x.enterprise.main.EnterprisePlugin;
import de.dal3x.soulcore.docking.VaultBankDock;
import net.milkbowl.vault.economy.Economy;

public class VaultDock {
	
	private static Economy eco;
	
	public static void registerVault() {
		eco = EnterprisePlugin.getInstance().getServer().getServicesManager().getRegistration(Economy.class).getProvider();
	}

	public static boolean hasMoney(OfflinePlayer p, double cost) {
		return eco.has(p, cost);
	}
	
	public static void removeMoney(OfflinePlayer p, double amount) {
		eco.withdrawPlayer(p, amount);
	}
	
	public static void addMoney(OfflinePlayer p, double amount) {
		eco.depositPlayer(p, amount);
	}
	
	public static void addBankMoney(UUID id, double amount) {
		VaultBankDock.addBankMoney(Bukkit.getOfflinePlayer(id), amount);
	}
	
}
