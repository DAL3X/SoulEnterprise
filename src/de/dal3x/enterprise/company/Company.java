/*
 * 
 */
package de.dal3x.enterprise.company;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.dal3x.enterprise.config.Config;
import de.dal3x.enterprise.docking.VaultDock;
import de.dal3x.enterprise.enums.ProductionBranch;
import de.dal3x.enterprise.enums.WageClass;
import de.dal3x.enterprise.inventory.UnlimitedInventory;
import de.dal3x.enterprise.market.MarketPlace;
import de.dal3x.enterprise.market.Offer;
import de.dal3x.enterprise.output.CompanyProductionOutput;
import de.dal3x.enterprise.upgrades.UpgradeStatus;

public class Company {

    private String name;
    private UUID ceo;
    private List<UUID> manager;
    private List<UUID> member;
    private HashMap<UUID, WageClass> wages;
    private HashMap<UUID, String> applications;
    private ProductionBranch branch;
    private HashMap<Integer, Material> product;
    private UpgradeStatus status;
    private double money;
    private UnlimitedInventory storage;
    private HashMap<UUID, Offer> marketOffer;

    public Company(String name, UUID ceo, List<UUID> manager, List<UUID> member, HashMap<UUID, WageClass> wages,
            HashMap<UUID, String> applications, ProductionBranch branch, HashMap<Integer, Material> product,
            UpgradeStatus status, double money, UnlimitedInventory storage, HashMap<UUID, Offer> marketOffer) {
        this.name = name;
        this.ceo = ceo;
        this.manager = manager;
        this.member = member;
        this.wages = wages;
        this.applications = applications;
        this.branch = branch;
        this.status = status;
        this.money = money;
        this.storage = storage;
        this.product = new HashMap<Integer, Material>();
        for (int i = 0; i < status.getProductionSlots(); i++) {
            this.product.put(i, null);
        }
        for (int key : product.keySet()) {
            this.product.put(key, product.get(key));
        }
        this.marketOffer = marketOffer;
    }

    public Company(String name, UUID ceo, ProductionBranch branch) {
        this.name = name;
        this.ceo = ceo;
        this.manager = new LinkedList<UUID>();
        this.member = new LinkedList<UUID>();
        this.member.add(ceo);
        this.wages = new HashMap<UUID, WageClass>();
        this.wages.put(ceo, WageClass.HIGH);
        this.applications = new HashMap<UUID, String>();
        this.branch = branch;
        this.product = new HashMap<Integer, Material>();
        this.product.put(0, null);
        this.status = new UpgradeStatus(Config.baseEmployeeSlots, 1, 1, Config.baseStorage);
        this.money = 0;
        this.storage = new UnlimitedInventory();
        this.marketOffer = new HashMap<UUID, Offer>();
    }

    public boolean hasOnline() {
        if (getOnlinePlayers().size() != 0) {
            return true;
        }
        return false;
    }

    public List<Player> getOnlinePlayers() {
        List<Player> online = new LinkedList<Player>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (this.member.contains(p.getUniqueId())) {
                online.add(p);
            }
        }
        return online;
    }

    public void updateStorageSize() {
        this.storage.addSlots(this.status.getStorageAmount() - this.storage.getSlots());
    }

    public void upgradeProductionSlots() {
        for (int i = this.product.size(); i < this.status.getProductionSlots(); i++) {
            this.addProduct(i, null);
        }
    }

    public void deleteAllOffers() {
        for (UUID id : this.marketOffer.keySet()) {
            deleteOffer(id);
        }
    }

    public void deleteOffer(UUID id) {
        MarketPlace.deleteOffer(this.branch, this.marketOffer.get(id).getItem().getType(), id);
        this.marketOffer.remove(id);
    }

    public Set<Material> getOfferedMaterials() {
        HashSet<Material> materials = new HashSet<Material>();
        for (UUID id : this.marketOffer.keySet()) {
            materials.add(this.marketOffer.get(id).getItem().getType());
        }
        return materials;
    }

    public void gainMoney(double amount) {
        this.money += (amount * Config.companyTake);
        amount = amount * (Config.companyTake + Config.taxes);
        int distribution = 0;
        for (UUID id : this.wages.keySet()) {
            distribution = distribution + this.wages.get(id).getWorth();
        }
        double toPay = (amount / distribution);
        for (UUID id : this.member) {
            VaultDock.addBankMoney(id, toPay * this.wages.get(id).getWorth());
        }
    }

    public HashMap<Integer, ItemStack> addToStorage(ItemStack item) {
        HashMap<Integer, ItemStack> cantStore = this.storage.addItem(item);
        refreshOffers(item.getType());
        if (cantStore.keySet().size() != 0) {
            for (Player p : getOnlinePlayers()) {
                if (CompanyHelper.isManagement(p.getUniqueId())) {
                    p.sendMessage(CompanyProductionOutput.storageFull);
                }
            }
        }
        return cantStore;
    }

    public void removeFromStorage(ItemStack item) {
        this.storage.removeItem(item);
    }

    public boolean hasItemInStorage(ItemStack item) {
        return this.storage.containsItem(item);
    }

    public boolean hasProductSpace() {
        if (this.product.size() >= status.getProductionSlots()) {
            return false;
        }
        return true;
    }

    public void addProduct(int slot, Material material) {
        this.product.put(slot, material);
    }

    public boolean hasEmployeeSpace() {
        if (this.member.size() >= status.getEmployeeSlots()) {
            return false;
        }
        return true;
    }

    public void hireEmployee(UUID id) {
        this.member.add(id);
        this.wages.put(id, WageClass.LOW);
    }

    public void fireEmployee(UUID id) {
        this.member.add(id);
        this.wages.remove(id);
    }

    public boolean isEmployee(UUID id) {
        return this.member.contains(id);
    }

    public boolean hasManagerSpace() {
        if (this.manager.size() >= 2) {
            return false;
        }
        return true;
    }

    public void addManager(UUID id) {
        this.manager.add(id);
    }

    public void removeManager(UUID id) {
        this.manager.remove(id);
    }

    public boolean isManager(UUID id) {
        return this.manager.contains(id);
    }

    public UUID getCeo() {
        return ceo;
    }

    public void setCeo(UUID ceo) {
        this.ceo = ceo;
    }

    public List<UUID> getManager() {
        return manager;
    }

    public void setManager(List<UUID> manager) {
        this.manager = manager;
    }

    public List<UUID> getMember() {
        return member;
    }

    public void setMember(List<UUID> member) {
        this.member = member;
    }

    public ProductionBranch getBranch() {
        return branch;
    }

    public void setBranch(ProductionBranch branch) {
        this.branch = branch;
    }

    public HashMap<Integer, Material> getProduct() {
        return product;
    }

    public void setProduct(HashMap<Integer, Material> product) {
        this.product = product;
    }

    public UpgradeStatus getStatus() {
        return status;
    }

    public void setStatus(UpgradeStatus status) {
        this.status = status;
    }

    public UnlimitedInventory getStorage() {
        return storage;
    }

    public void setStorage(UnlimitedInventory storage) {
        this.storage = storage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<UUID, String> getApplications() {
        return applications;
    }

    public void addApplication(UUID applicant, String application) {
        this.applications.put(applicant, application);
    }

    public void setApplications(HashMap<UUID, String> applications) {
        this.applications = applications;
    }

    public void removeApplication(UUID applicant) {
        this.applications.remove(applicant);
    }

    public HashMap<UUID, WageClass> getWages() {
        return wages;
    }

    public void setWages(HashMap<UUID, WageClass> wages) {
        this.wages = wages;
    }

    public void setWage(UUID id, WageClass wage) {
        this.wages.put(id, wage);
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public HashMap<UUID, Offer> getMarketOffer() {
        return marketOffer;
    }

    public void setMarketOffer(HashMap<UUID, Offer> marketOffer) {
        this.marketOffer = marketOffer;
    }

    public double getPriceFor(Material material) {
        for (Offer o : this.marketOffer.values()) {
            if (o.getItem().getType() == material) {
                return o.getPrice();
            }
        }
        return 0;
    }

    public void addOffer(Offer offer) {
        this.marketOffer.put(offer.getTransactionID(), offer);
        MarketPlace.storeCompanyOffer(this.branch, offer);
    }

    public void refreshOffers(Material material) {
        double price = 0;
        List<UUID> toRemove = new LinkedList<UUID>();
        for (Offer o : this.marketOffer.values()) {
            if (o.getItem().getType() == material) {
                price = o.getPrice();
                MarketPlace.deleteOffer(this.getBranch(), material, o.getTransactionID());
                toRemove.add(o.getTransactionID());
            }
        }
        for (UUID id : toRemove) {
            this.marketOffer.remove(id);
        }
        if (price <= 0) {
            return;
        }
        for (ItemStack item : getStorage().getContent()) {
            if (item.getType() == material) {
                Offer newOffer = new Offer(item, price, this.name, true);
                addOffer(newOffer);
            }
        }

    }

}
