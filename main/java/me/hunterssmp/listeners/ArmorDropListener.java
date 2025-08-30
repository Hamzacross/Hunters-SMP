package me.hunterssmp.listeners;

import me.hunterssmp.util.ItemUtil;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class ArmorDropListener implements Listener {

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Item dropped = event.getItemDrop();
        ItemStack item = dropped.getItemStack();

        // Only apply if it's one of your special armor pieces
        if (ItemUtil.isSpecialArmor(item)) {
            dropped.setGlowing(true);

            // Use the real name already set in ItemUtil
            String displayName = item.hasItemMeta() && item.getItemMeta().hasDisplayName()
                    ? item.getItemMeta().getDisplayName()
                    : item.getType().name();

            dropped.setCustomName(displayName);
            dropped.setCustomNameVisible(true);
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        Item itemEntity = event.getItem();
        ItemStack item = itemEntity.getItemStack();

        if (ItemUtil.isSpecialArmor(item)) {
            // Reset name/glow once picked up
            itemEntity.setGlowing(false);
            itemEntity.setCustomName(null);
            itemEntity.setCustomNameVisible(false);
        }
    }
}
