package me.hunterssmp;

import me.hunterssmp.commands.HunterCommand;
import me.hunterssmp.commands.HunterTabCompleter;
import me.hunterssmp.gui.ConfirmPurchaseGUI;
import me.hunterssmp.gui.ShopGUI;
import me.hunterssmp.listeners.*;
import me.hunterssmp.managers.AutoHuntScheduler;
import me.hunterssmp.managers.DataManager;
import me.hunterssmp.managers.HunterManager;
import me.hunterssmp.placeholder.HuntersSMPExpansion;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;

    private DataManager dataManager;
    private HunterManager hunterManager;
    private AutoHuntScheduler autoHuntScheduler;
    private ShopGUI shopGUI;
    private ConfirmPurchaseGUI confirmPurchaseGUI;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        initializeManagersAndData();
        initializeGUIs();
        registerListeners();
        registerCommands();
        registerPlaceholders();

        getLogger().info("HuntersSMP v" + getDescription().getVersion() + " enabled!");
    }

    @Override
    public void onDisable() {
        if (autoHuntScheduler != null && autoHuntScheduler.isRunning()) {
            autoHuntScheduler.stopAutoHunt();
        }

        if (hunterManager != null && hunterManager.isHuntActive()) {
            hunterManager.endHunt();
        }

        if (dataManager != null) {
            dataManager.saveData();
        }

        getLogger().info("HuntersSMP disabled.");
    }

    // ===== INITIALIZATION =====
    private void initializeManagersAndData() {
        this.dataManager = new DataManager(this);
        this.dataManager.loadData();

        this.hunterManager = new HunterManager(this, dataManager);
        this.autoHuntScheduler = new AutoHuntScheduler(this, hunterManager);

        if (getConfig().getBoolean("autoHunt.enabled", true)) {
            this.autoHuntScheduler.startAutoHunt();
        }
    }

    private void initializeGUIs() {
        this.shopGUI = new ShopGUI(this, dataManager);
        this.confirmPurchaseGUI = new ConfirmPurchaseGUI(this, dataManager);
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new HuntListener(hunterManager, dataManager), this);
        Bukkit.getPluginManager().registerEvents(new ArmorListener(dataManager), this);
        Bukkit.getPluginManager().registerEvents(shopGUI, this);
        Bukkit.getPluginManager().registerEvents(confirmPurchaseGUI, this);
        Bukkit.getPluginManager().registerEvents(new DeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerRespawnListener(hunterManager), this);
        Bukkit.getPluginManager().registerEvents(new ArmorDropListener(), this);
    }

    private void registerCommands() {
        if (getCommand("hunts") != null) {
            HunterCommand hunterCommand = new HunterCommand(this, hunterManager, dataManager, shopGUI, autoHuntScheduler);
            getCommand("hunts").setExecutor(hunterCommand);
            new HunterTabCompleter(this); // Register tab completion
        }
    }

    // ===== PLACEHOLDERAPI =====
    private void registerPlaceholders() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new HuntersSMPExpansion(this, dataManager, hunterManager).register();
            getLogger().info("HuntersSMP placeholders registered with PlaceholderAPI.");
        } else {
            getLogger().warning("PlaceholderAPI not found! Placeholders will not work.");
        }
    }

    // ===== RELOAD METHOD =====
    public void reloadAll() {
        reloadConfig();
        if (dataManager != null) {
            dataManager.loadData();
        }

        if (autoHuntScheduler != null && autoHuntScheduler.isRunning()) {
            autoHuntScheduler.stopAutoHunt();
            if (getConfig().getBoolean("autoHunt.enabled", true)) {
                autoHuntScheduler.startAutoHunt();
            }
        }

        getLogger().info("HuntersSMP config and data reloaded.");
    }

    // ===== INSTANCE =====
    public static Main getInstance() {
        return instance;
    }

    // ===== HELPERS =====
    public NamespacedKey getKey(String key) {
        return new NamespacedKey(this, key);
    }

    public ShopGUI getShopGUI() {
        return shopGUI;
    }

    public ConfirmPurchaseGUI getConfirmPurchaseGUI() {
        return confirmPurchaseGUI;
    }

    public HunterManager getHunterManager() {
        return hunterManager;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public AutoHuntScheduler getAutoHuntScheduler() {
        return autoHuntScheduler;
    }
}
