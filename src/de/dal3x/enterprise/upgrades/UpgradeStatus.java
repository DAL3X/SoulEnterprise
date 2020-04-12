package de.dal3x.enterprise.upgrades;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class UpgradeStatus {

	private int employeeSlots;
	private int productionSlots;
	private double efficiency;
	private int storageAmount;

	public UpgradeStatus(int employeeSlots, int productionSlots, double efficiency, int storageAmount) {
		this.employeeSlots = employeeSlots;
		this.productionSlots = productionSlots;
		this.efficiency = efficiency;
		this.storageAmount = storageAmount;
	}

	public int getEmployeeSlots() {
		return employeeSlots;
	}

	public void setEmployeeSlots(int employeeSlots) {
		this.employeeSlots = employeeSlots;
	}

	public void addEmployeeSlots(int slots) {
		this.employeeSlots = this.employeeSlots + slots;
	}

	public double getEfficiency() {
		return efficiency;
	}

	public void setEfficiency(double efficiency) {
		this.efficiency = round(efficiency, 1);
	}

	public void addEfficiency(double efficiency) {
		this.efficiency = round(this.efficiency + efficiency, 1);
	}

	public int getStorageAmount() {
		return storageAmount;
	}

	public void setStorageAmount(int storageAmount) {
		this.storageAmount = storageAmount;
	}

	public void addStorageAmount(int amount) {
		this.storageAmount = this.storageAmount + amount;
	}

	public int getProductionSlots() {
		return productionSlots;
	}

	public void setProductionSlots(int productionSlots) {
		this.productionSlots = productionSlots;
	}

	public void addProductionSlots(int amount) {
		this.productionSlots = this.productionSlots + amount;
	}

	private double round(double value, int places) {
		if (places < 0) {
			throw new IllegalArgumentException();
		}
		BigDecimal bd = BigDecimal.valueOf(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

}
