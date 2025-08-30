package me.hunterssmp.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class ShopGUIHolder implements InventoryHolder {
    private final Inventory inventory;

    public ShopGUIHolder(Inventory inventory) {
        this.inventory = inventory;
    }

    public Inventory getInventory() {
        return this.inventory;
    }
}