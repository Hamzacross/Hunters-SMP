package me.hunterssmp.commands;

import me.hunterssmp.Main;
import me.hunterssmp.gui.ShopGUI;
import me.hunterssmp.managers.HunterManager;
import me.hunterssmp.managers.DataManager;
import me.hunterssmp.managers.AutoHuntScheduler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HunterCommand implements CommandExecutor {

    private final Main plugin;
    private final HunterManager hunterManager;
    private final DataManager dataManager;
    private final ShopGUI shopGUI;
    private final AutoHuntScheduler autoHuntScheduler;

    public HunterCommand(Main plugin, HunterManager hunterManager, DataManager dataManager,
                         ShopGUI shopGUI, AutoHuntScheduler autoHuntScheduler) {
        this.plugin = plugin;
        this.hunterManager = hunterManager;
        this.dataManager = dataManager;
        this.shopGUI = shopGUI;
        this.autoHuntScheduler = autoHuntScheduler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // No arguments: open shop if player
        if (args.length == 0) {
            if (sender instanceof Player player) {
                shopGUI.openShop(player);
            } else {
                sender.sendMessage("§cConsole must use a subcommand: /hunts <start|stop|key|reload|help>");
            }
            return true;
        }

        String sub = args[0].toLowerCase();

        // Only allow "help" for non-OPs
        if (!sender.isOp() && !sub.equals("help")) {
            sender.sendMessage("§cYou do not have permission to use this command.");
            return true;
        }

        switch (sub) {
            case "help":
                if (sender instanceof Player playerHelp) sendAdminHelp(playerHelp);
                else sender.sendMessage(
                        "§6--- HuntersSMP Admin Commands ---\n" +
                                "/hunts start <hunter> <hunted>\n" +
                                "/hunts stop\n" +
                                "/hunts key add <player> <amount>\n" +
                                "/hunts key remove <player> <amount>\n" +
                                "/hunts reload\n" +
                                "/hunts help"
                );
                break;

            case "start":
                if (!(sender instanceof Player playerStart)) {
                    sender.sendMessage("§cConsole must specify hunter and hunted: /hunts start <hunter> <hunted>");
                    return true;
                }
                handleStart(playerStart, args);
                break;

            case "stop":
                if (!(sender instanceof Player playerStop)) {
                    sender.sendMessage("§cConsole cannot stop hunts.");
                    return true;
                }
                handleStop(playerStop);
                break;

            case "key":
                handleKeySubcommand(sender, args);
                break;

            case "reload":
                plugin.reloadAll();
                sender.sendMessage("§aPlugin configuration and data reloaded successfully!");
                break;

            default:
                sender.sendMessage("§cInvalid usage! Correct: /hunts <start|stop|key|reload|help>");
                break;
        }

        return true;
    }

    // ===== KEY SUBCOMMAND =====
    private void handleKeySubcommand(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("§cUsage: /hunts key <add|remove> <player> <amount>");
            return;
        }

        String action = args[1].toLowerCase();
        Player target = Bukkit.getPlayerExact(args[2]);
        if (target == null || !target.isOnline()) {
            sender.sendMessage("§cTarget player not found or offline!");
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[3]);
            if (amount < 1 || amount > 10) {
                sender.sendMessage("§cAmount must be between 1 and 10.");
                return;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid number format!");
            return;
        }

        if (action.equals("add")) {
            dataManager.addKeys(target.getUniqueId(), amount);
            sender.sendMessage("§aAdded " + amount + " hunt key(s) to " + target.getName() + "!");
            target.sendMessage("§aYou received " + amount + " hunt key(s)!");
        } else if (action.equals("remove")) {
            dataManager.removeKeys(target.getUniqueId(), amount);
            sender.sendMessage("§aRemoved " + amount + " hunt key(s) from " + target.getName() + "!");
            target.sendMessage("§cYou lost " + amount + " hunt key(s)!");
        } else {
            sender.sendMessage("§cInvalid usage! Use /hunts key <add|remove> <player> <amount>");
        }
    }

    // ===== START & STOP =====
    private void handleStart(Player player, String[] args) {
        if (hunterManager.isHuntActive()) {
            player.sendMessage("§cA hunt is already active!");
            return;
        }
        if (args.length < 3) {
            player.sendMessage("§cUsage: /hunts start <hunter> <hunted>");
            return;
        }
        Player hunter = Bukkit.getPlayerExact(args[1]);
        Player hunted = Bukkit.getPlayerExact(args[2]);
        if (hunter == null || hunted == null || hunter.equals(hunted)) {
            player.sendMessage("§cOne or both players not found or they are the same player!");
            return;
        }
        if (hunterManager.startHunt(hunter, hunted)) {
            Bukkit.broadcastMessage("§aThe hunt has started! Good luck to " + hunter.getName() + " hunting " + hunted.getName() + "!");
        } else {
            player.sendMessage("§cFailed to start the hunt due to an unknown error.");
        }
    }

    private void handleStop(Player player) {
        if (!hunterManager.isHuntActive()) {
            player.sendMessage("§cThere is no active hunt to stop.");
            return;
        }
        hunterManager.stopHunt();
        player.sendMessage("§aThe hunt has been stopped successfully.");
    }

    // ===== HELP =====
    private void sendAdminHelp(Player player) {
        player.sendMessage("§6--- §eHuntersSMP Admin Commands §6---");
        player.sendMessage("§e/hunts start <hunter> <hunted> §7Start a hunt between two players");
        player.sendMessage("§e/hunts stop §7Stop the current hunt");
        player.sendMessage("§e/hunts key add <player> <amount> §7Add hunt keys to a player");
        player.sendMessage("§e/hunts key remove <player> <amount> §7Remove hunt keys from a player");
        player.sendMessage("§e/hunts reload §7Reload plugin config and data");
        player.sendMessage("§e/hunts help §7Show this help menu");
        player.sendMessage("§6-------------------------------");
    }
}
