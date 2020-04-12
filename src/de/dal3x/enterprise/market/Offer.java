package de.dal3x.enterprise.market;

import java.util.UUID;

import org.bukkit.inventory.ItemStack;

public class Offer {

	private ItemStack item;
	private double price;
	private String seller;
	private UUID id;
	private boolean isCompany;

	public Offer(ItemStack item, double price, String seller, boolean isCompany) {
		this.item = item;
		this.price = price;
		this.seller = seller;
		this.id = UUID.randomUUID();
		this.isCompany = isCompany;
	}
	
	public Offer(ItemStack item, double price, String seller, UUID id, boolean isCompany) {
		this.item = item;
		this.price = price;
		this.seller = seller;
		this.id = id;
		this.isCompany = isCompany;
	}

	public ItemStack getItem() {
		return this.item;
	}

	public double getPrice() {
		return this.price;
	}

	public String getSeller() {
		return this.seller;
	}

	public UUID getTransactionID() {
		return id;
	}

	public boolean isCompany() {
		return isCompany;
	}

}
