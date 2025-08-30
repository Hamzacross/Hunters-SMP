package me.hunterssmp.gui;

import me.hunterssmp.Main;
import me.hunterssmp.managers.DataManager;
import me.hunterssmp.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ShopGUI implements Listener {

    private final Main plugin;
    private final DataManager dataManager;
    private final String inventoryTitle;

    public ShopGUI(Main plugin, DataManager dataManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.inventoryTitle = ChatColor.GREEN + "Hunters Shop";
    }

    public void openShop(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, inventoryTitle);

        // Filler
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);
        for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, filler);

        // Armor pieces with spacing (slots: 10, 12, 14, 16)
        inv.setItem(10, createShopItem(ItemUtil.createSpecialHelmet(), "special_helmet"));
        inv.setItem(12, createShopItem(ItemUtil.createSpecialChestplate(), "special_chestplate"));
        inv.setItem(14, createShopItem(ItemUtil.createSpecialLeggings(), "special_leggings"));
        inv.setItem(16, createShopItem(ItemUtil.createSpecialBoots(), "special_boots"));

        // Keys display at slot 4
        ItemStack keysDisplay = new ItemStack(Material.TRIPWIRE_HOOK);
        ItemMeta keysMeta = keysDisplay.getItemMeta();
        keysMeta.setDisplayName(ChatColor.GOLD + "Your Hunt Keys: " + dataManager.getKeys(player.getUniqueId()));
        keysDisplay.setItemMeta(keysMeta);
        inv.setItem(4, keysDisplay);

        player.openInventory(inv);
    }

    private ItemStack createShopItem(ItemStack item, String key) {
        // Add hidden item key
        item = ItemUtil.addItemKey(item, key);

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            int cost = plugin.getConfig().getInt("shop.items." + key + ".keysCost", 10);
            List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.YELLOW + "Cost: " + cost + " hunt keys");
            lore.add(ChatColor.GRAY + "Click to purchase.");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getView().getTitle().equals(inventoryTitle)) return;

        event.setCancelled(true);
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        // Retrieve item key from hidden PersistentData
        String itemKey = ItemUtil.getItemKey(clicked);
        if (itemKey != null) {
            plugin.getConfirmPurchaseGUI().openConfirmGUI(player, itemKey);
        }
    }
}
