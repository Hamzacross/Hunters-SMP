package me.hunterssmp.commands;

import me.hunterssmp.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HunterTabCompleter implements TabCompleter {

    private final Main plugin;

    public HunterTabCompleter(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("hunts").setTabCompleter(this);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (!sender.isOp()) return completions; // only admins see subcommands

        if (args.length == 1) {
            completions.add("start");
            completions.add("stop");
            completions.add("key");
            completions.add("reload");
            completions.add("help");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("start")) {
            // second argument for /hunts start <hunter>
            for (Player p : Bukkit.getOnlinePlayers()) completions.add(p.getName());
        } else if (args.length == 2 && args[0].equalsIgnoreCase("key")) {
            // second argument for /hunts key <add|remove>
            completions.add("add");
            completions.add("remove");
        } else if (args.length == 3 && args[0].equalsIgnoreCase("key")) {
            // third argument for /hunts key add/remove <player>
            for (Player p : Bukkit.getOnlinePlayers()) completions.add(p.getName());
        } else if (args.length == 4 && args[0].equalsIgnoreCase("key")) {
            // fourth argument for /hunts key add/remove <player> <amount>
            for (int i = 1; i <= 10; i++) completions.add(String.valueOf(i));
        } else if (args.length == 3 && args[0].equalsIgnoreCase("start")) {
            // third argument for /hunts start <hunter> <hunted>
            for (Player p : Bukkit.getOnlinePlayers()) completions.add(p.getName());
        }

        return completions;
    }
}
