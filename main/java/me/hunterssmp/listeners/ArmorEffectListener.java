package me.hunterssmp.listeners;

import me.hunterssmp.Main;
import me.hunterssmp.util.ItemUtil;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ArmorEffectListener implements Listener {
    private final Main plugin;

    public ArmorEffectListener(Main plugin) {
        this.plugin = plugin;
    }

    private void applyEffects(Player player, ItemStack armor) {
        if (!ItemUtil.isSpecialArmor(armor)) return;

        String type = ItemUtil.getSpecialArmorType(armor);
        if (type == null) return;

        switch (type) {
            case "helmet":
                // Resistance + Health Regen + Night Vision + Tracking Bonus (lore info)
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
                break;
            case "chestplate":
                // Extra hearts, damage bonus, damage resistance, regen
                double baseHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(baseHealth + 10.0); // +5 hearts
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false)); // +10% damage
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, false, false)); // +5% damage resist
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0, false, false)); // regen
                break;
            case "leggings":
                // Strength + Sprint Speed + Knockback resistance
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 0, false, false)); // +15% strength
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false)); // sprint speed
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0, false, false)); // knockback resist
                break;
            case "boots":
                // Speed + Jump + No fall damage + Evasion (lore info)
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 0, false, false));
                // No fall damage & evasion will need to be handled separately in DamageListener if needed
                break;
        }
    }

    private void removeEffects(Player player, ItemStack armor) {
        if (!ItemUtil.isSpecialArmor(armor)) return;

        String type = ItemUtil.getSpecialArmorType(armor);
        if (type == null) return;

        switch (type) {
            case "helmet":
                player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                player.removePotionEffect(PotionEffectType.REGENERATION);
                player.removePotionEffect(PotionEffectType.NIGHT_VISION);
                break;
            case "chestplate":
                double baseHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
                player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(Math.max(20.0, baseHealth - 10.0)); // remove 5 hearts
                if (player.getHealth() > player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue())
                    player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
                player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                player.removePotionEffect(PotionEffectType.REGENERATION);
                break;
            case "leggings":
                player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                player.removePotionEffect(PotionEffectType.SPEED);
                player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                break;
            case "boots":
                player.removePotionEffect(PotionEffectType.SPEED);
                player.removePotionEffect(PotionEffectType.JUMP);
                break;
        }
    }

    private void refreshArmorEffects(Player player) {
        ItemStack helmet = player.getInventory().getHelmet();
        ItemStack chestplate = player.getInventory().getChestplate();
        ItemStack leggings = player.getInventory().getLeggings();
        ItemStack boots = player.getInventory().getBoots();

        removeEffects(player, helmet);
        removeEffects(player, chestplate);
        removeEffects(player, leggings);
        removeEffects(player, boots);

        if (helmet != null) applyEffects(player, helmet);
        if (chestplate != null) applyEffects(player, chestplate);
        if (leggings != null) applyEffects(player, leggings);
        if (boots != null) applyEffects(player, boots);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity clicker = event.getWhoClicked();
        if (!(clicker instanceof Player player)) return;

        int rawSlot = event.getRawSlot();
        if (rawSlot >= 5 && rawSlot <= 8) {
            plugin.getServer().getScheduler().runTask(plugin, () -> refreshArmorEffects(player));
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        plugin.getServer().getScheduler().runTask(plugin, () -> refreshArmorEffects(player));
    }

    @EventHandler
    public void onSwapHandItems(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        plugin.getServer().getScheduler().runTask(plugin, () -> refreshArmorEffects(player));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        removeEffects(player, player.getInventory().getHelmet());
        removeEffects(player, player.getInventory().getChestplate());
        removeEffects(player, player.getInventory().getLeggings());
        removeEffects(player, player.getInventory().getBoots());
    }
}
