package de.dal3x.enterprise.main;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import de.dal3x.enterprise.commands.CompanyCommand;
import de.dal3x.enterprise.commands.MarketCommand;
import de.dal3x.enterprise.company.Company;
import de.dal3x.enterprise.config.Config;
import de.dal3x.enterprise.docking.VaultDock;
import de.dal3x.enterprise.enums.ProductionBranch;
import de.dal3x.enterprise.file.Filehandler;
import de.dal3x.enterprise.listener.PlayerOnlineListener;

public class EnterprisePlugin extends JavaPlugin {

	private static EnterprisePlugin instance;
	private HashMap<String, Company> compStorage;
	private HashMap<UUID, String> playerData;
	private HashMap<ProductionBranch, HashMap<Integer, Material>> itemBlueprint;
	private HashMap<Material, Integer> baseProduction;
	private static boolean hasActiveThread = false;

	public static EnterprisePlugin getInstance() {
		return instance;
	}

	public void onEnable() {
		instance = this;
		getCommand("firma").setExecutor(new CompanyCommand());
		getCommand("markt").setExecutor(new MarketCommand());
		this.compStorage = new HashMap<String, Company>();
		this.playerData = new HashMap<UUID, String>();
		getServer().getPluginManager().registerEvents(new PlayerOnlineListener(), this);
		for (Player p : Bukkit.getOnlinePlayers()) {
			Filehandler.loadPlayerData(p.getUniqueId());
		}
		Filehandler.loadProductions();
		Filehandler.loadConfig();
		VaultDock.registerVault();
		activateProduction();
	}

	public void onDisable() {
		instance = null;
	}

	public void reload() {
		onDisable();
		onEnable();
	}

	private void activateProduction() {
		if (hasActiveThread) {
			return;
		}
		hasActiveThread = true;
		long interval = new Random().nextInt(Config.maxProdCycle - Config.minProdCycle) + Config.minProdCycle;
		Bukkit.getScheduler().runTaskLater(this, new Runnable() {
			public void run() {
				for (Company company : compStorage.values()) {
					for (Material mat : company.getProduct().values()) {
						if (mat != null) {
							double amount = (baseProduction.get(mat) + baseProduction.get(mat)
									* (company.getOnlinePlayers().size() * Config.prodPerOnline));
							amount = amount * company.getStatus().getEfficiency();
							int endAmount = Math.round((float)amount);
							while (endAmount > mat.getMaxStackSize()) {
								company.addToStorage(new ItemStack(mat, mat.getMaxStackSize()));
								endAmount = endAmount - mat.getMaxStackSize();
							}
							company.addToStorage(new ItemStack(mat, endAmount));
						}
					}
				}
				hasActiveThread = false;
				activateProduction();
			}
			// Change minutes to ticks by multiplying with 1200
		}, interval * 1200);
	}

	public void unloadIfOffline(Company comp) {
		if (comp != null) {
			if (!comp.hasOnline()) {
				Filehandler.storeCompany(comp);
				removeCompany(comp.getName());
			}
		}
	}

	public Company getCompanyForPlayerID(UUID id) {
		return this.compStorage.get(playerData.get(id));
	}

	public Company getCompanyForName(String name) {
		return this.compStorage.get(name);
	}

	public HashMap<String, Company> getCompStorage() {
		return compStorage;
	}

	public void setCompStorage(HashMap<String, Company> compStorage) {
		this.compStorage = compStorage;
	}

	public void addCompany(String name, Company comp) {
		this.compStorage.put(name, comp);
	}

	public void removeCompany(String name) {
		this.compStorage.remove(name);
	}

	public HashMap<UUID, String> getPlayerData() {
		return playerData;
	}

	public void setPlayerData(HashMap<UUID, String> playerData) {
		this.playerData = playerData;
	}

	public void addPlayerData(UUID id, String name) {
		this.playerData.put(id, name);
	}

	public void removePlayerData(UUID id) {
		this.playerData.remove(id);
	}

	public HashMap<ProductionBranch, HashMap<Integer, Material>> getItemBlueprint() {
		return this.itemBlueprint;
	}

	public void setItemBlueprint(HashMap<ProductionBranch, HashMap<Integer, Material>> itemBlueprint) {
		this.itemBlueprint = itemBlueprint;
	}

	public HashMap<Material, Integer> getBaseProduction() {
		return baseProduction;
	}

	public void setBaseProduction(HashMap<Material, Integer> baseProduction) {
		this.baseProduction = baseProduction;
	}

}
