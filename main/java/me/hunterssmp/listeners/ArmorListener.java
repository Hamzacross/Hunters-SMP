package me.hunterssmp.listeners;

import me.hunterssmp.managers.DataManager;
import me.hunterssmp.util.ItemUtil;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ArmorListener implements Listener {
    private final DataManager dataManager;

    public ArmorListener(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public void refreshEffects(Player player) {
        PlayerInventory inv = player.getInventory();
        ItemStack helmet = inv.getHelmet();
        ItemStack chestplate = inv.getChestplate();
        ItemStack leggings = inv.getLeggings();
        ItemStack boots = inv.getBoots();

        handleArmorEffect(player, "helmet", helmet, PotionEffectType.REGENERATION, 1);
        handleChestplateHearts(player, chestplate);
        handleArmorEffect(player, "leggings", leggings, PotionEffectType.INCREASE_DAMAGE, 1);
        handleArmorEffect(player, "boots", boots, PotionEffectType.SPEED, 1);
    }

    private void handleArmorEffect(Player player, String armorType, ItemStack item, PotionEffectType effectType, int amplifier) {
        boolean isWearing = ItemUtil.isSpecialArmor(item) && armorType.equals(ItemUtil.getSpecialArmorType(item));
        boolean hasEffect = player.hasPotionEffect(effectType);

        if (isWearing && !hasEffect) {
            player.addPotionEffect(new PotionEffect(effectType, Integer.MAX_VALUE, amplifier, true, false, true));
        } else if (!isWearing && hasEffect) {
            player.removePotionEffect(effectType);
        }
    }

    private void handleChestplateHearts(Player player, ItemStack chestplate) {
        boolean isWearing = ItemUtil.isSpecialArmor(chestplate) && "chestplate".equals(ItemUtil.getSpecialArmorType(chestplate));
        double baseMaxHealth = 20.0;
        double extraHearts = 10.0;
        double currentMaxHealth = player.getMaxHealth();

        if (isWearing && currentMaxHealth < baseMaxHealth + extraHearts) {
            player.setMaxHealth(baseMaxHealth + extraHearts);
        } else if (!isWearing && currentMaxHealth > baseMaxHealth) {
            player.setMaxHealth(baseMaxHealth);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity entity = event.getWhoClicked();
        if (entity instanceof Player player) {
            player.getServer().getScheduler().runTaskLater(this.dataManager.getPlugin(), () -> refreshEffects(player), 1L);
        }
    }

    @EventHandler
    public void onSwapHandItems(PlayerSwapHandItemsEvent event) {
        refreshEffects(event.getPlayer());
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        refreshEffects(event.getPlayer());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        refreshEffects(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        player.removePotionEffect(PotionEffectType.REGENERATION);
        player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
        player.removePotionEffect(PotionEffectType.SPEED);
        player.setMaxHealth(20.0);
    }
}
