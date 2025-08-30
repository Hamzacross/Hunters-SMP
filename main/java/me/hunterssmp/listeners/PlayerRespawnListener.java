package me.hunterssmp.listeners;

import me.hunterssmp.managers.HunterManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;

public class PlayerRespawnListener implements Listener {

    private final HunterManager hunterManager;
    private final Random random = new Random();

    public PlayerRespawnListener(HunterManager hunterManager) {
        this.hunterManager = hunterManager;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        // Only apply effect if a hunt is active and the player was the hunter who died
        if (hunterManager.isHuntActive() && hunterManager.isHunter(player) && hunterManager.wasKilledByHunted(player)) {

            // All possible negative effects
            PotionEffectType[] badEffects = new PotionEffectType[] {
                    PotionEffectType.SLOW,
                    PotionEffectType.WEAKNESS,
                    PotionEffectType.POISON,
                    PotionEffectType.BLINDNESS,
                    PotionEffectType.HUNGER,
                    PotionEffectType.CONFUSION,
                    PotionEffectType.SLOW_DIGGING
            };

            // Pick one random effect
            PotionEffectType effectType = badEffects[random.nextInt(badEffects.length)];

            // Apply the effect for 30 minutes (30 * 60 * 20 ticks)
            PotionEffect effect = new PotionEffect(effectType, 30 * 60 * 20, 1);
            player.addPotionEffect(effect);
        }
    }
}
