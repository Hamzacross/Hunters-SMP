package me.hunterssmp.listeners;

import me.hunterssmp.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.Player;

public class DeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        // Remove custom Hunter armor from inventory
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;
            if (ItemUtil.isSpecialArmor(item) || isHunterCompass(item)) {
                player.getInventory().remove(item);
            }
        }

        // Remove armor from armor slots
        for (ItemStack item : player.getInventory().getArmorContents()) {
            if (item == null) continue;
            if (ItemUtil.isSpecialArmor(item)) {
                player.getInventory().remove(item);
            }
        }
    }

    // Helper method to check if an item is the Hunter compass
    private boolean isHunterCompass(ItemStack item) {
        if (item.getType() != Material.COMPASS) return false;
        if (!item.hasItemMeta()) return false;
        if (!item.getItemMeta().hasDisplayName()) return false;

        // Check if the display name matches the compass name (adjust if needed)
        return item.getItemMeta().getDisplayName().contains("Hunter Compass");
    }
}
