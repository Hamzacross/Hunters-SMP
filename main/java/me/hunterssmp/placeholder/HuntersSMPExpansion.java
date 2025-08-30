package me.hunterssmp.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.hunterssmp.Main;
import me.hunterssmp.managers.DataManager;
import me.hunterssmp.managers.HunterManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HuntersSMPExpansion extends PlaceholderExpansion {

    private final Main plugin;
    private final DataManager dataManager;
    private final HunterManager hunterManager;

    public HuntersSMPExpansion(Main plugin, DataManager dataManager, HunterManager hunterManager) {
        this.plugin = plugin;
        this.dataManager = dataManager;
        this.hunterManager = hunterManager;
    }

    @NotNull
    @Override
    public String getIdentifier() {
        return "hunterssmp";
    }

    @NotNull
    @Override
    public String getAuthor() {
        return "Hamzacross100";
    }

    @NotNull
    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Nullable
    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) return "";

        switch (params.toLowerCase()) {

            // Hunt keys
            case "key":
            case "keys":
                return String.valueOf(dataManager.getKeys(player.getUniqueId()));

            // Wins / kills
            case "kill":
            case "kills":
            case "win":
            case "wins":
                return String.valueOf(dataManager.getWins(player.getUniqueId()));

            // Losses / deaths
            case "death":
            case "deaths":
            case "loss":
            case "losses":
                return String.valueOf(dataManager.getLosses(player.getUniqueId()));

            // Total games (wins + losses)
            case "total":
                int wins = dataManager.getWins(player.getUniqueId());
                int losses = dataManager.getLosses(player.getUniqueId());
                return String.valueOf(wins + losses);

            // Hunt status
            case "huntstatus":
                return hunterManager.isPlayerInHunt(player) ? "Active" : "Inactive";

            // Check if player is currently a hunter
            case "ishunter":
                return hunterManager.isHunter(player) ? "Yes" : "No";

            // Check if player is currently hunted
            case "ishunted":
                return hunterManager.isHunted(player) ? "Yes" : "No";

            default:
                return "";
        }
    }
}
