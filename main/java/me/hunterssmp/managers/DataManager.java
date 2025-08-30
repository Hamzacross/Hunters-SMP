package me.hunterssmp.managers;

import me.hunterssmp.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataManager {

    private final Main plugin;
    private final File dataFile;
    private FileConfiguration dataConfig;

    private final Map<UUID, Integer> keysMap = new HashMap<>();
    private final Map<UUID, Integer> winsMap = new HashMap<>();
    private final Map<UUID, Integer> lossesMap = new HashMap<>();

    public DataManager(Main plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "keys.yml");

        if (!dataFile.exists()) {
            plugin.saveResource("keys.yml", false);
        }

        this.dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    // ===== LOAD DATA FROM FILE =====
    public void loadData() {
        for (String key : dataConfig.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                keysMap.put(uuid, dataConfig.getInt(key + ".keys", 0));
                winsMap.put(uuid, dataConfig.getInt(key + ".wins", 0));
                lossesMap.put(uuid, dataConfig.getInt(key + ".losses", 0));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid UUID in keys.yml: " + key);
            }
        }
    }

    // ===== SAVE DATA TO FILE =====
    public void saveData() {
        for (UUID uuid : keysMap.keySet()) {
            dataConfig.set(uuid.toString() + ".keys", keysMap.get(uuid));
            dataConfig.set(uuid.toString() + ".wins", winsMap.getOrDefault(uuid, 0));
            dataConfig.set(uuid.toString() + ".losses", lossesMap.getOrDefault(uuid, 0));
        }

        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save keys.yml!");
            e.printStackTrace();
        }
    }

    // ===== KEYS =====
    public int getKeys(UUID uuid) {
        return keysMap.getOrDefault(uuid, 0);
    }

    public void addKeys(UUID uuid, int amount) {
        keysMap.put(uuid, getKeys(uuid) + amount);
    }

    public boolean removeKeys(UUID uuid, int amount) {
        int current = getKeys(uuid);
        if (current < amount) return false;
        keysMap.put(uuid, current - amount);
        return true;
    }

    // ===== WINS & LOSSES =====
    public void recordWin(UUID uuid) {
        winsMap.put(uuid, winsMap.getOrDefault(uuid, 0) + 1);
    }

    public void recordLoss(UUID uuid) {
        lossesMap.put(uuid, lossesMap.getOrDefault(uuid, 0) + 1);
    }

    public int getWins(UUID uuid) {
        return winsMap.getOrDefault(uuid, 0);
    }

    public int getLosses(UUID uuid) {
        return lossesMap.getOrDefault(uuid, 0);
    }

    // ===== HELPER =====
    public Main getPlugin() {
        return plugin; // optional if you really need it
    }
}
