package de.dal3x.enterprise.file;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import de.dal3x.enterprise.company.Company;
import de.dal3x.enterprise.config.Config;
import de.dal3x.enterprise.enums.ProductionBranch;
import de.dal3x.enterprise.enums.WageClass;
import de.dal3x.enterprise.inventory.UnlimitedInventory;
import de.dal3x.enterprise.main.EnterprisePlugin;
import de.dal3x.enterprise.market.Offer;
import de.dal3x.enterprise.upgrades.UpgradeStatus;

public class Filehandler {

	public static void loadCompany(String name) {
		File companyFile = new File("plugins/SoulEnterprise/companies", name + ".yml");
		if (!companyFile.exists()) {
			return;
		}
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(companyFile);
		// ceo
		UUID ceo = UUID.fromString(cfg.getString("ceo"));
		// manager
		List<UUID> manager = new LinkedList<UUID>();
		for (String stringID : cfg.getStringList("manager")) {
			manager.add(UUID.fromString(stringID));
		}
		// member
		List<UUID> member = new LinkedList<UUID>();
		for (String stringID : cfg.getStringList("member")) {
			member.add(UUID.fromString(stringID));
		}
		// wages
		HashMap<UUID, WageClass> wages = new HashMap<UUID, WageClass>();
		if (cfg.isConfigurationSection("wages")) {
			for (String memberID : cfg.getConfigurationSection("wages").getKeys(false)) {
				wages.put(UUID.fromString(memberID), WageClass.valueOf(cfg.getString("wages." + memberID)));
			}
		}
		// applications
		HashMap<UUID, String> applications = new HashMap<UUID, String>();
		if (cfg.isConfigurationSection("application")) {
			for (String applicantID : cfg.getConfigurationSection("application").getKeys(false)) {
				applications.put(UUID.fromString(applicantID), cfg.getString("application." + applicantID));
			}
		}
		// branch
		ProductionBranch branch = ProductionBranch.valueOf(cfg.getString("branch"));
		// product
		HashMap<Integer, Material> product = new HashMap<Integer, Material>();
		if (cfg.isConfigurationSection("products")) {
			for (String iterate : cfg.getConfigurationSection("products").getKeys(false)) {
				product.put(Integer.valueOf(iterate), Material.valueOf(cfg.getString("products." + iterate)));
			}
		}
		// status
		int employeeSlots = cfg.getInt("employeeSlots");
		int productionSlots = cfg.getInt("productionSlots");
		double efficiency = cfg.getDouble("efficiency");
		int storageAmount = cfg.getInt("storageSize");
		UpgradeStatus status = new UpgradeStatus(employeeSlots, productionSlots, efficiency, storageAmount);
		// money
		double money = cfg.getDouble("money");
		// storage
		List<ItemStack> items = new LinkedList<ItemStack>();
		int storageSize = cfg.getInt("storageSize");
		if (cfg.isConfigurationSection("storage")) {
			for (String sSlot : cfg.getConfigurationSection("storage").getKeys(false)) {
				Material material = Material.valueOf(cfg.getString("storage." + sSlot + ".material"));
				int amount = cfg.getInt("storage." + sSlot + ".amount");
				ItemStack item = new ItemStack(material, amount);
				items.add(item);
			}
		}
		UnlimitedInventory inv = new UnlimitedInventory(storageSize, items);
		// Offers
		HashMap<UUID, Offer> marketOffer = new HashMap<UUID, Offer>();
		if (cfg.isConfigurationSection("market")) {
			for (String sID : cfg.getConfigurationSection("market").getKeys(false)) {
				Material mat = Material.valueOf(cfg.getString("market." + sID + ".material"));
				ItemStack item = new ItemStack(mat, cfg.getInt("market." + sID + ".amount"));
				double price = cfg.getDouble("market." + sID + ".price");
				marketOffer.put(UUID.fromString(sID), new Offer(item, price, name, true));
			}
		}
		// Create
		Company comp = new Company(name, ceo, manager, member, wages, applications, branch, product, status, money, inv,
				marketOffer);
		EnterprisePlugin.getInstance().addCompany(name, comp);
	}

	public static void storeCompany(Company comp) {
		File companyFile = new File("plugins/SoulEnterprise/companies", comp.getName() + ".yml");
		if (companyFile.exists()) {
			companyFile.delete();
		}
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(companyFile);
		// ceo
		cfg.set("ceo", comp.getCeo().toString());
		// manager
		List<String> managers = new LinkedList<String>();
		for (UUID id : comp.getManager()) {
			managers.add(id.toString());
		}
		cfg.set("manager", managers.toString());
		// member
		List<String> member = new LinkedList<String>();
		for (UUID id : comp.getMember()) {
			member.add(id.toString());
		}
		cfg.set("member", member);
		// wages
		for (UUID id : comp.getWages().keySet()) {
			cfg.set("wages." + id.toString(), comp.getWages().get(id).toString());
		}
		// applications
		for (UUID id : comp.getApplications().keySet()) {
			cfg.set("application." + id.toString(), comp.getApplications().get(id));
		}
		// branch
		cfg.set("branch", comp.getBranch().toString());
		// product
		for (int i = 0; i < comp.getProduct().size(); i++) {
			if (comp.getProduct().get(i) != null) {
				cfg.set("products." + i, comp.getProduct().get(i).toString());
			}
		}
		// status
		cfg.set("employeeSlots", comp.getStatus().getEmployeeSlots());
		cfg.set("productionSlots", comp.getStatus().getProductionSlots());
		cfg.set("efficiency", comp.getStatus().getEfficiency());
		cfg.set("storageSize", comp.getStatus().getStorageAmount());
		// money
		cfg.set("money", comp.getMoney());
		// storage
		for (int i = 0; i < comp.getStorage().getContent().size(); i++) {
			ItemStack item = comp.getStorage().getContent().get(i);
			cfg.set("storage." + i + ".material", item.getType().toString());
			cfg.set("storage." + i + ".amount", item.getAmount());
		}
		// Offers
		for (UUID id : comp.getMarketOffer().keySet()) {
			Offer o = comp.getMarketOffer().get(id);
			Material mat = o.getItem().getType();
			cfg.set("market." + id.toString() + ".material", mat.toString());
			int amount = o.getItem().getAmount();
			cfg.set("market." + id.toString() + ".amount", amount);
			double price = o.getPrice();
			cfg.set("market." + id.toString() + ".price", price);
		}
		// Save
		try {
			cfg.save(companyFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean hasCompanyFile(String name) {
		File companyFile = new File("plugins/SoulEnterprise/companies", name + ".yml");
		return companyFile.exists();
	}

	public static void deleteCompanyFile(String name) {
		File companyFile = new File("plugins/SoulEnterprise/companies", name + ".yml");
		if (companyFile.exists()) {
			companyFile.delete();
		}
	}

	public static void loadPlayerData(UUID id) {
		File playerFile = new File("plugins/SoulEnterprise/playerData", id.toString() + ".yml");
		if (!playerFile.exists()) {
			return;
		}
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(playerFile);
		String companyName = cfg.getString("company");
		// Loads company if not loaded
		if (EnterprisePlugin.getInstance().getCompanyForName(companyName) == null) {
			loadCompany(companyName);
		}
		EnterprisePlugin.getInstance().addPlayerData(id, companyName);
	}

	public static void storePlayerData(UUID id) {
		File playerFile = new File("plugins/SoulEnterprise/playerData", id.toString() + ".yml");
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(playerFile);
		cfg.set("company", EnterprisePlugin.getInstance().getCompanyForPlayerID(id).getName());
		try {
			cfg.save(playerFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean hasPlayerData(UUID id) {
		File playerFile = new File("plugins/SoulEnterprise/playerData", id.toString() + ".yml");
		return playerFile.exists();
	}

	public static void deletePlayerData(UUID id) {
		File playerFile = new File("plugins/SoulEnterprise/playerData", id.toString() + ".yml");
		if (playerFile.exists()) {
			playerFile.delete();
		}
	}

	public static void loadProductions() {
		// Products for Branches
		File productFile = new File("plugins/SoulEnterprise/production", "BranchProduction.yml");
		if (!productFile.exists()) {
			return;
		}
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(productFile);
		HashMap<ProductionBranch, HashMap<Integer, Material>> itemBlueprint = new HashMap<ProductionBranch, HashMap<Integer, Material>>();
		for (String bran : cfg.getConfigurationSection("").getKeys(false)) {
			ProductionBranch branch = ProductionBranch.valueOf(bran);
			HashMap<Integer, Material> slots = new HashMap<Integer, Material>();
			for (String sSlot : cfg.getConfigurationSection(bran).getKeys(false)) {
				String sMaterial = cfg.getString(bran + "." + sSlot);
				slots.put(Integer.valueOf(sSlot), Material.valueOf(sMaterial));
			}
			itemBlueprint.put(branch, slots);
		}
		EnterprisePlugin.getInstance().setItemBlueprint(itemBlueprint);

		// Product Amount
		productFile = new File("plugins/SoulEnterprise/production", "ProductionAmount.yml");
		if (!productFile.exists()) {
			return;
		}
		cfg = YamlConfiguration.loadConfiguration(productFile);
		HashMap<Material, Integer> baseProduction = new HashMap<Material, Integer>();
		for (String mat : cfg.getConfigurationSection("").getKeys(false)) {
			baseProduction.put(Material.valueOf(mat), cfg.getInt(mat));
		}
		EnterprisePlugin.getInstance().setBaseProduction(baseProduction);
	}

	public static void loadConfig() {
		File configFile = new File("plugins/SoulEnterprise", "config.yml");
		if (!configFile.exists()) {
			return;
		}
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(configFile);
		Config.taxes = cfg.getDouble("Steuern") / 100;
		Config.companyTake = cfg.getDouble("Firmenanteil") / 100;
		Config.baseEmployeeSlots = cfg.getInt("StartMitarbeiter");
		Config.baseStorage = cfg.getInt("StartLager");
		Config.prodPerOnline = cfg.getInt("ProduktPlusProOnline");
		Config.minProdCycle = cfg.getInt("minimalProduktionsZeit");
		Config.maxProdCycle = cfg.getInt("maximalProduktionsZeit");
		Config.addsEmployeeSlots = cfg.getInt("MitarbeiterProUpgrade");
		Config.addsProductionSlots = cfg.getInt("ProduktSlotsProUpgrade");
		Config.addsEfficiency = cfg.getDouble("EffizienzProUpgrade") / 100;
		Config.addsStorage = cfg.getInt("LagerSlotsProUpgrade");
		Config.employeePrice = cfg.getInt("MitarbeiterUpgradePreis");
		Config.productionSlotsPrice = cfg.getInt("ProduktSlotUpgradePreis");
		Config.efficiencyPrice = cfg.getInt("EffizienzUpgradePreis");
		Config.storagePrice = cfg.getInt("LagerUpgradePreis");
		Config.foundingPrice = cfg.getInt("GründungsPreis");
	}

	public static void storeOffer(ProductionBranch b, Offer offer, boolean isCompany) {
		UUID id = offer.getTransactionID();
		String folder = "plugins/SoulEnterprise/market/" + b.toString() + "/" + offer.getItem().getType().toString();
		File offerFile = new File(folder, id.toString() + ".yml");
		if (offerFile.exists()) {
			offerFile.delete();
		}
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(offerFile);
		cfg.set("material", offer.getItem().getType().toString());
		cfg.set("amount", offer.getItem().getAmount());
		cfg.set("price", offer.getPrice());
		cfg.set("seller", offer.getSeller());
		cfg.set("company", isCompany);
		try {
			cfg.save(offerFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static Set<Material> getMaterialOffers(ProductionBranch b) {
		String folder = "plugins/SoulEnterprise/market/" + b.toString();
		File[] directories = new File(folder).listFiles();
		HashSet<Material> matOffer = new HashSet<Material>();
		if (directories != null) {
			for (File dir : directories) {
				Material mat = Material.valueOf(dir.getName());
				matOffer.add(mat);
			}
		}
		return matOffer;
	}

	public static List<Offer> loadOffers(ProductionBranch b, Material m) {
		String folder = "plugins/SoulEnterprise/market/" + b.toString() + "/" + m.toString();
		File[] files = new File(folder).listFiles();
		LinkedList<Offer> offers = new LinkedList<Offer>();
		for (File file : files) {
			offers.add(loadOffer(b, m, UUID.fromString(file.getName().substring(0, file.getName().length() - 4))));
		}
		return offers;
	}

	public static Offer loadOffer(ProductionBranch b, Material m, UUID id) {
		String folder = "plugins/SoulEnterprise/market/" + b.toString() + "/" + m.toString();
		File offerFile = new File(folder, id.toString() + ".yml");
		if (!offerFile.exists()) {
			return null;
		}
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(offerFile);
		ItemStack item = new ItemStack(Material.valueOf(cfg.getString("material")), cfg.getInt("amount"));
		double price = cfg.getDouble("price");
		String seller = cfg.getString("seller");
		boolean isCompany = cfg.getBoolean("company");

		return new Offer(item, price, seller, id, isCompany);
	}

	public static void deleteOffer(ProductionBranch b, Material m, UUID id) {
		String folder = "plugins/SoulEnterprise/market/" + b.toString() + "/" + m.toString();
		File offerFile = new File(folder, id.toString() + ".yml");
		if (offerFile.exists()) {
			offerFile.delete();
		}
		File fold = new File(folder);
		if (fold.exists() && fold.listFiles().length == 0) {
			fold.delete();
		}

	}

}
