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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ConfirmPurchaseGUI implements Listener {

    private final Main plugin;
    private final DataManager dataManager;
    private final Map<UUID, String> playerItemKeys = new HashMap<>();
    private final String inventoryTitle;

    public ConfirmPurchaseGUI(Main plugin, DataManager dataManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.inventoryTitle = ChatColor.RED + "Confirm Purchase";
    }

    public void openConfirmGUI(Player player, String itemKey) {
        playerItemKeys.put(player.getUniqueId(), itemKey);

        Inventory inv = Bukkit.createInventory(null, 9, inventoryTitle);

        // Fill inventory with gray panes
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);
        for (int i = 0; i < inv.getSize(); i++) inv.setItem(i, filler);

        // Item to buy
        ItemStack itemToBuy = getItemByKey(itemKey);
        if (itemToBuy == null) {
            player.sendMessage(ChatColor.RED + "Item not found!");
            return;
        }
        inv.setItem(4, itemToBuy);

        // Confirm button
        ItemStack confirm = new ItemStack(Material.GREEN_WOOL);
        ItemMeta confirmMeta = confirm.getItemMeta();
        confirmMeta.setDisplayName(ChatColor.GREEN + "Confirm Purchase");
        confirmMeta.setLore(List.of(ChatColor.YELLOW + "Click to buy this item."));
        confirm.setItemMeta(confirmMeta);
        inv.setItem(2, confirm);

        // Cancel button
        ItemStack cancel = new ItemStack(Material.RED_WOOL);
        ItemMeta cancelMeta = cancel.getItemMeta();
        cancelMeta.setDisplayName(ChatColor.RED + "Cancel Purchase");
        cancelMeta.setLore(List.of(ChatColor.YELLOW + "Click to cancel and go back."));
        cancel.setItemMeta(cancelMeta);
        inv.setItem(6, cancel);

        player.openInventory(inv);
    }

    private ItemStack getItemByKey(String key) {
        return switch (key) {
            case "special_helmet" -> ItemUtil.createSpecialHelmet();
            case "special_chestplate" -> ItemUtil.createSpecialChestplate();
            case "special_leggings" -> ItemUtil.createSpecialLeggings();
            case "special_boots" -> ItemUtil.createSpecialBoots();
            default -> null;
        };
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getView().getTitle().equals(inventoryTitle)) return;

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        String itemKey = playerItemKeys.get(player.getUniqueId());

        if (clicked.getType() == Material.GREEN_WOOL && itemKey != null) {
            int cost = plugin.getConfig().getInt("shop.items." + itemKey + ".keysCost", 10);
            int playerKeys = dataManager.getKeys(player.getUniqueId());

            if (playerKeys < cost) {
                player.closeInventory();
                player.sendMessage(ChatColor.RED + "You do not have enough hunt keys!");
                return;
            }

            // Remove keys and give the item
            dataManager.removeKeys(player.getUniqueId(), cost);
            player.getInventory().addItem(getItemByKey(itemKey));
            player.sendMessage(ChatColor.GREEN + "Purchase successful!");
            player.closeInventory();
            playerItemKeys.remove(player.getUniqueId());

        } else if (clicked.getType() == Material.RED_WOOL) {
            player.closeInventory();
            plugin.getShopGUI().openShop(player);
            playerItemKeys.remove(player.getUniqueId());
        }
    }
}
