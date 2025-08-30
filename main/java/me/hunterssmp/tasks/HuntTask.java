package me.hunterssmp.tasks;

import me.hunterssmp.managers.HunterManager;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class HuntTask extends BukkitRunnable {
    private final HunterManager manager;
    private final BossBar bossBar;
    private int secondsLeft;
    private final int totalSeconds;

    public HuntTask(HunterManager manager, int durationSeconds) {
        this.manager = manager;
        this.secondsLeft = durationSeconds;
        this.totalSeconds = durationSeconds;
        this.bossBar = Bukkit.createBossBar("§aHunt in Progress...", BarColor.RED, BarStyle.SOLID, new BarFlag[0]);
    }

    @Override
    public void run() {
        if (!this.manager.isHuntActive()) {
            this.removeBar();
            this.cancel();
            return;
        }

        if (this.secondsLeft <= 0) {
            this.removeBar();
            this.manager.handleHuntTimeout();
            this.cancel();
            return;
        }

        // Progress only (no timer numbers in title)
        this.bossBar.setTitle("§aHunt in Progress...");
        this.bossBar.setProgress((double) this.secondsLeft / (double) this.totalSeconds);

        Player hunter = this.manager.getHunterPlayer();
        Player hunted = this.manager.getHuntedPlayer();

        if (hunter != null && !this.bossBar.getPlayers().contains(hunter)) {
            this.bossBar.addPlayer(hunter);
        }

        if (hunted != null && !this.bossBar.getPlayers().contains(hunted)) {
            this.bossBar.addPlayer(hunted);
        }

        this.secondsLeft--;
    }

    private void removeBar() {
        this.bossBar.removeAll();
    }
}
