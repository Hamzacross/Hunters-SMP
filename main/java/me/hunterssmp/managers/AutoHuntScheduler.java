package me.hunterssmp.managers;

import me.hunterssmp.Main;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class AutoHuntScheduler {

    private final Main plugin;
    private final HunterManager hunterManager;
    private BukkitRunnable task;
    private boolean running = false;
    private final long intervalTicks;

    public AutoHuntScheduler(Main plugin, HunterManager hunterManager) {
        this.plugin = plugin;
        this.hunterManager = hunterManager;

        // Read interval in seconds from config and convert to ticks
        long intervalSeconds = plugin.getConfig().getLong("autoHunt.intervalSeconds", 1800);
        this.intervalTicks = intervalSeconds * 20L; // 20 ticks per second
    }

    public void startAutoHunt() {
        if (!plugin.getConfig().getBoolean("autoHunt.enabled", true)) {
            plugin.getLogger().info("Auto Hunt is disabled in config.");
            return;
        }
        if (running) return;

        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!hunterManager.isHuntActive()) {
                    int notifySeconds = plugin.getConfig().getInt("autoHunt.notifyBeforeStartSeconds", 30);
                    boolean announce = plugin.getConfig().getBoolean("autoHunt.announce", true);

                    if (notifySeconds > 0 && announce) {
                        Bukkit.broadcastMessage("§eAuto hunt starting in " + notifySeconds + " seconds! Prepare yourselves!");
                    }

                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        boolean started = hunterManager.startHunt();
                        if (started) {
                            if (announce) Bukkit.broadcastMessage("§aAuto Hunt has started!");
                            plugin.getLogger().info("Auto Hunt started successfully.");
                        } else {
                            plugin.getLogger().info("Auto Hunt could not start (not enough players online).");
                        }
                    }, notifySeconds * 20L);
                }
            }
        };

        this.task.runTaskTimer(plugin, 0L, intervalTicks);
        running = true;
        plugin.getLogger().info("Auto Hunt Scheduler started. Interval: " + (intervalTicks / 20L) + " seconds.");
    }

    public void stopAutoHunt() {
        if (!running) return;
        if (task != null) task.cancel();
        running = false;
        plugin.getLogger().info("Auto Hunt Scheduler stopped.");
    }

    public boolean isRunning() {
        return running;
    }
}
