package me.hunterssmp.listeners;

import java.util.UUID;
import me.hunterssmp.managers.DataManager;
import me.hunterssmp.managers.HunterManager;
import me.hunterssmp.util.ItemUtil;
import me.hunterssmp.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class HuntListener implements Listener {

    private final HunterManager hunterManager;
    private final DataManager dataManager;

    public HuntListener(HunterManager hunterManager, DataManager dataManager) {
        this.hunterManager = hunterManager;
        this.dataManager = dataManager;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        hunterManager.removeShopArmor(victim);

        if (!hunterManager.isHuntActive()) return;

        UUID hunterUUID = hunterManager.getHunterUUID();
        UUID huntedUUID = hunterManager.getHuntedUUID();
        UUID victimUUID = victim.getUniqueId();

        if (hunterUUID == null || huntedUUID == null) return;

        // Hunted dies
        if (victimUUID.equals(huntedUUID)) {
            if (killer != null && killer.getUniqueId().equals(hunterUUID)) {
                hunterManager.handleHuntSuccess(killer, victim);
            } else {
                dataManager.recordLoss(victimUUID);
                hunterManager.endHunt();
                System.out.println("§cThe hunted player died without the hunter killing them!");
            }
        }
        // Hunter dies
        else if (victimUUID.equals(hunterUUID)) {
            dataManager.recordLoss(hunterUUID);
            dataManager.recordWin(huntedUUID);
            hunterManager.endHunt();
            System.out.println("§cThe hunter died! Hunt ended.");
        }
    }
}
