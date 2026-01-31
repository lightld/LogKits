package com.l1ghtld.command.tabcompleter;

import com.l1ghtld.LogKits;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import com.l1ghtld.command.KitCommand;
import com.l1ghtld.command.SubCommand;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class KitTabCompleter implements TabCompleter {

    private static final String PERMISSION_KIT = "logkits.kit";
    private static final String PERMISSION_ADMIN = "logkits.admin";

    private static final List<String> COOLDOWN_SUGGESTIONS = Arrays.asList("1d", "12h", "1h", "30m");
    private static final Set<String> KIT_NAME_COMMANDS = new HashSet<>(Arrays.asList("preview", "set", "setcooldown", "kits"));

    private final LogKits plugin;

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, String[] args) {
        if (!sender.hasPermission(PERMISSION_KIT)) {
            return Collections.emptyList();
        }

        boolean isAdmin = sender.hasPermission(PERMISSION_ADMIN);

        if (args.length == 1) {
            return getSubCommandCompletions(sender, isAdmin, args[0]);
        }

        if (args.length == 2) {
            return getSecondArgCompletions(sender, isAdmin, args);
        }

        if (args.length == 3) {
            return getThirdArgCompletions(isAdmin, args);
        }

        return Collections.emptyList();
    }

    private List<String> getSubCommandCompletions(CommandSender sender, boolean isAdmin, String input) {
        List<String> completions = new ArrayList<>();

        KitCommand kitCommand = getKitCommand();
        if (kitCommand != null) {
            for (SubCommand sub : kitCommand.getSubCommands().values()) {
                if (!sub.isAdminOnly() || isAdmin) {
                    completions.add(sub.getName());
                }
            }
        }

        return filter(completions, input);
    }

    private List<String> getSecondArgCompletions(CommandSender sender, boolean isAdmin, String[] args) {
        String subCommand = args[0].toLowerCase(Locale.ROOT);

        if (isAdmin && "give".equals(subCommand)) {
            return getOnlinePlayerNames(args[1]);
        }

        if (KIT_NAME_COMMANDS.contains(subCommand)) {
            if ("preview".equals(subCommand) || isAdmin) {
                return getKitNameCompletions(sender, isAdmin, args[1]);
            }
        }

        return Collections.emptyList();
    }

    private List<String> getThirdArgCompletions(boolean isAdmin, String[] args) {
        if (!isAdmin) return Collections.emptyList();

        String subCommand = args[0].toLowerCase(Locale.ROOT);

        if ("give".equals(subCommand)) {
            return filter(new ArrayList<>(plugin.getKitManager().listKitNames()), args[2]);
        }

        if ("setcooldown".equals(subCommand)) {
            return filter(new ArrayList<>(COOLDOWN_SUGGESTIONS), args[2]);
        }

        return Collections.emptyList();
    }

    private List<String> getOnlinePlayerNames(String input) {
        String lowerInput = input.toLowerCase(Locale.ROOT);
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(lowerInput))
                .collect(Collectors.toList());
    }

    private List<String> getKitNameCompletions(CommandSender sender, boolean isAdmin, String input) {
        List<String> completions = new ArrayList<>();
        Set<String> kits = plugin.getKitManager().listKitNames();

        for (String kit : kits) {
            if (isAdmin || sender.hasPermission(PERMISSION_KIT + "." + kit.toLowerCase(Locale.ROOT))) {
                completions.add(kit);
            }
        }

        return filter(completions, input);
    }

    private KitCommand getKitCommand() {
        if (plugin.getCommand("kit") != null && Objects.requireNonNull(plugin.getCommand("kit")).getExecutor() instanceof KitCommand) {
            return (KitCommand) Objects.requireNonNull(plugin.getCommand("kit")).getExecutor();
        }
        return null;
    }

    private List<String> filter(List<String> base, String token) {
        String lowerToken = token.toLowerCase(Locale.ROOT);
        return base.stream()
                .filter(s -> s.toLowerCase(Locale.ROOT).startsWith(lowerToken))
                .collect(Collectors.toList());
    }
}
