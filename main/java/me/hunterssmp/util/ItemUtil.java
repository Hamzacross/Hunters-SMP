package me.hunterssmp.util;

import java.util.ArrayList;
import java.util.List;
import me.hunterssmp.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public class ItemUtil {

    public static final String PDC_ARMOR_KEY = "hunter_armor_type";
    public static final String PDC_ITEM_KEY = "shop_item_key";
    public static final String PDC_COMPASS_KEY = "hunt_compass"; // added for compass

    // ===== CREATE ARMOR =====
    public static ItemStack createSpecialHelmet() {
        return createSpecialArmor(
                Material.NETHERITE_HELMET,
                "&6&lHunter's Sight Helm",
                List.of(
                        "&7Eyes sharp as an eagle’s,",
                        "&7granting vision beyond the prey’s",
                        "&7last hiding place.",
                        "&6Grants Regeneration II",
                        "&8[Exclusive Hunt Reward]"
                ),
                "helmet"
        );
    }

    public static ItemStack createSpecialChestplate() {
        return createSpecialArmor(
                Material.NETHERITE_CHESTPLATE,
                "&6&lHunter's Valor Chestplate",
                List.of(
                        "&7Forged in the heat of the hunt,",
                        "&7this chestplate carries the spirit",
                        "&7of every fallen prey.",
                        "&6Grants +5 Hearts",
                        "&8[Exclusive Hunt Reward]"
                ),
                "chestplate"
        );
    }

    public static ItemStack createSpecialLeggings() {
        return createSpecialArmor(
                Material.NETHERITE_LEGGINGS,
                "&6&lHunter's Pursuit Greaves",
                List.of(
                        "&7Swift and unyielding, these greaves",
                        "&7drive the hunter forward,",
                        "&7relentless in pursuit.",
                        "&6Grants Strength II",
                        "&8[Exclusive Hunt Reward]"
                ),
                "leggings"
        );
    }

    public static ItemStack createSpecialBoots() {
        return createSpecialArmor(
                Material.NETHERITE_BOOTS,
                "&6&lHunter's Silent Step Boots",
                List.of(
                        "&7As silent as a shadow,",
                        "&7yet striking as the storm.",
                        "&7No prey hears them coming.",
                        "&6Grants Speed II",
                        "&8[Exclusive Hunt Reward]"
                ),
                "boots"
        );
    }

    // ===== HELPER TO CREATE ARMOR =====
    private static ItemStack createSpecialArmor(Material material, String displayName, List<String> lore, String type) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));

            List<String> coloredLore = new ArrayList<>();
            for (String line : lore) coloredLore.add(ChatColor.translateAlternateColorCodes('&', line));
            meta.setLore(coloredLore);

            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);

            if (Main.getInstance() != null) {
                PersistentDataContainer container = meta.getPersistentDataContainer();
                container.set(Main.getInstance().getKey(PDC_ARMOR_KEY), PersistentDataType.STRING, type);
            }

            item.setItemMeta(meta);
        }

        return item;
    }

    // ===== ARMOR CHECKERS =====
    public static boolean isSpecialArmor(ItemStack item) {
        if (item == null || !item.hasItemMeta() || Main.getInstance() == null) return false;
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        return container.has(Main.getInstance().getKey(PDC_ARMOR_KEY), PersistentDataType.STRING);
    }

    public static @Nullable String getSpecialArmorType(ItemStack item) {
        if (item == null || !item.hasItemMeta() || Main.getInstance() == null) return null;
        return item.getItemMeta().getPersistentDataContainer()
                .get(Main.getInstance().getKey(PDC_ARMOR_KEY), PersistentDataType.STRING);
    }

    // ===== SHOP ITEM HELPER =====
    public static ItemStack createShopItem(Material material, String displayName, List<String> lore, int keysCost) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + displayName);

            List<String> newLore = new ArrayList<>();
            if (lore != null) newLore.addAll(lore);
            newLore.add("");
            newLore.add(ChatColor.YELLOW + "Cost: " + ChatColor.GREEN + keysCost + " Hunt Keys");

            meta.setLore(newLore);
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }

        return item;
    }

    // ===== KEYS DISPLAY =====
    public static ItemStack createKeysDisplay(int keys) {
        ItemStack item = new ItemStack(Material.TRIPWIRE_HOOK);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "Hunt Keys");

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.YELLOW + "You have " + ChatColor.GREEN + keys + ChatColor.YELLOW + " hunt keys.");
            lore.add(ChatColor.GRAY + "Use keys to purchase legendary hunter items.");

            meta.setLore(lore);
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }

        return item;
    }

    // ===== PERSISTENT ITEM KEY =====
    public static ItemStack addItemKey(ItemStack item, String key) {
        if (item == null || !item.hasItemMeta() || Main.getInstance() == null) return item;
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(
                Main.getInstance().getKey(PDC_ITEM_KEY),
                PersistentDataType.STRING,
                key
        );
        item.setItemMeta(meta);
        return item;
    }

    public static @Nullable String getItemKey(ItemStack item) {
        if (item == null || !item.hasItemMeta() || Main.getInstance() == null) return null;
        return item.getItemMeta().getPersistentDataContainer()
                .get(Main.getInstance().getKey(PDC_ITEM_KEY), PersistentDataType.STRING);
    }

    // ===== HUNT COMPASS =====
    public static ItemStack createHuntCompass() {
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta meta = compass.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GREEN + "Hunt Compass");
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            if (Main.getInstance() != null) {
                meta.getPersistentDataContainer().set(
                        Main.getInstance().getKey(PDC_COMPASS_KEY),
                        PersistentDataType.STRING,
                        "hunt_compass"
                );
            }
            compass.setItemMeta(meta);
        }
        return compass;
    }

    public static boolean isSpecialCompass(ItemStack item) {
        if (item == null || !item.hasItemMeta() || Main.getInstance() == null) return false;
        return item.getItemMeta().getPersistentDataContainer()
                .has(Main.getInstance().getKey(PDC_COMPASS_KEY), PersistentDataType.STRING);
    }
}
