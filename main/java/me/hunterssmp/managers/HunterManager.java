package me.hunterssmp.managers;

import me.hunterssmp.Main;
import me.hunterssmp.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

public class HunterManager {

    private final Main plugin;
    private final DataManager dataManager;

    private Player hunterPlayer;
    private Player huntedPlayer;
    private BossBar bossBar;
    private BukkitRunnable huntTask;
    private int huntDurationSeconds;
    private int timeLeft;

    private UUID lastHunterKilledBy; // Tracks if hunter was killed by hunted

    public HunterManager(Main plugin, DataManager dataManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.huntDurationSeconds = plugin.getConfig().getInt("huntDurationSeconds", 600);
    }

    // ===== HUNT STATUS =====
    public boolean isHuntActive() {
        return hunterPlayer != null && huntedPlayer != null;
    }

    public boolean isPlayerInHunt(Player player) {
        return isHunter(player) || isHunted(player);
    }

    public boolean isHunter(Player player) {
        return hunterPlayer != null && hunterPlayer.equals(player);
    }

    public boolean isHunted(Player player) {
        return huntedPlayer != null && huntedPlayer.equals(player);
    }

    // ===== HUNT START =====
    public boolean startHunt(Player hunter, Player hunted) {
        if (isHuntActive() || hunter.equals(hunted)) return false;

        this.hunterPlayer = hunter;
        this.huntedPlayer = hunted;
        this.timeLeft = huntDurationSeconds;
        this.lastHunterKilledBy = null;

        this.bossBar = Bukkit.createBossBar(
                formatBossBarTitle(),
                BarColor.RED,
                BarStyle.SOLID
        );
        bossBar.addPlayer(hunter);
        bossBar.addPlayer(hunted);

        // Give hunt compass
        hunter.getInventory().addItem(ItemUtil.createHuntCompass());
        updateCompass();

        hunter.sendMessage("§aYou received a compass to track your hunted player!");
        hunted.sendMessage("§cYou are being hunted by " + hunter.getName() + "!");
        Bukkit.broadcastMessage("§6" + hunter.getName() + " §estarted hunting §6" + hunted.getName() + "!");

        startHuntTimer();
        return true;
    }

    public boolean startHunt() {
        List<Player> onlinePlayers = Bukkit.getOnlinePlayers().stream()
                .filter(p -> !p.isDead())
                .collect(Collectors.toList());
        if (onlinePlayers.size() < 2) return false;

        Player hunter = onlinePlayers.get(new Random().nextInt(onlinePlayers.size()));
        Player hunted;
        do {
            hunted = onlinePlayers.get(new Random().nextInt(onlinePlayers.size()));
        } while (hunter.equals(hunted));

        return startHunt(hunter, hunted);
    }

    private void startHuntTimer() {
        this.huntTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!isHuntActive()) {
                    stopHunt();
                    cancel();
                    return;
                }

                if (timeLeft <= 0) {
                    handleHuntTimeout();
                    cancel();
                    return;
                }

                bossBar.setTitle(formatBossBarTitle());
                bossBar.setProgress((double) timeLeft / huntDurationSeconds);
                updateCompass();
                timeLeft--;
            }
        };
        this.huntTask.runTaskTimer(plugin, 0L, 20L);
    }

    private String formatBossBarTitle() {
        return "Hunt: " + hunterPlayer.getName() + " vs " + huntedPlayer.getName() +
                " - Time Left: " + formatTime(timeLeft);
    }

    private String formatTime(int seconds) {
        int mins = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", mins, secs);
    }

    private void updateCompass() {
        if (hunterPlayer != null && huntedPlayer != null) {
            hunterPlayer.setCompassTarget(huntedPlayer.getLocation());
        }
    }

    // ===== HUNT END =====
    public void handleHuntSuccess(Player hunter, Player hunted) {
        Bukkit.broadcastMessage("§6" + hunter.getName() + " §ahas successfully hunted §6" + hunted.getName() + "!");
        dataManager.addKeys(hunter.getUniqueId(), plugin.getConfig().getInt("keysPerKill", 1));
        stopHunt();
    }

    public void handleHuntTimeout() {
        Bukkit.broadcastMessage("§6" + huntedPlayer.getName() + " §aescaped the hunt!");
        stopHunt();
    }

    public void stopHunt() {
        if (!isHuntActive()) return;

        if (huntTask != null) huntTask.cancel();
        if (bossBar != null) bossBar.removeAll();

        // Remove custom shop armor
        removeShopArmor(hunterPlayer);
        removeShopArmor(huntedPlayer);

        // Remove compass from all players
        for (Player p : Bukkit.getOnlinePlayers()) {
            removeHuntCompass(p);
        }

        hunterPlayer = null;
        huntedPlayer = null;
        lastHunterKilledBy = null;
    }

    public void endHunt() {
        stopHunt();
    }

    // ===== TRACK HUNTER KILL =====
    public void setHunterKilledBy(Player killer) {
        if (hunterPlayer != null && killer != null && killer.equals(huntedPlayer)) {
            lastHunterKilledBy = killer.getUniqueId();
        }
    }

    public boolean wasKilledByHunted(Player hunter) {
        return hunter != null &&
                hunter.equals(hunterPlayer) &&
                lastHunterKilledBy != null &&
                lastHunterKilledBy.equals(huntedPlayer.getUniqueId());
    }

    // ===== REMOVE ITEMS =====
    public void removeShopArmor(Player player) {
        if (player == null) return;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;
            if (ItemUtil.isSpecialArmor(item)) {
                player.getInventory().remove(item);
            }
        }
    }

    private void removeHuntCompass(Player player) {
        if (player == null) return;

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;
            if (item.getType() == Material.COMPASS && ItemUtil.isSpecialCompass(item)) {
                player.getInventory().remove(item);
            }
        }

        ItemStack offHand = player.getInventory().getItemInOffHand();
        if (offHand != null && offHand.getType() == Material.COMPASS && ItemUtil.isSpecialCompass(offHand)) {
            player.getInventory().setItemInOffHand(null);
        }

        player.updateInventory();
    }

    // ===== GETTERS =====
    public Player getHunterPlayer() {
        return hunterPlayer;
    }

    public Player getHuntedPlayer() {
        return huntedPlayer;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public int getHuntDurationSeconds() {
        return huntDurationSeconds;
    }

    public UUID getHunterUUID() {
        return hunterPlayer != null ? hunterPlayer.getUniqueId() : null;
    }

    public UUID getHuntedUUID() {
        return huntedPlayer != null ? huntedPlayer.getUniqueId() : null;
    }
}
