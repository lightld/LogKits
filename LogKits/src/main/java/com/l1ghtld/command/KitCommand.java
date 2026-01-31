package com.l1ghtld.command;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import com.l1ghtld.LogKits;
import com.l1ghtld.command.sub.*;
import com.l1ghtld.kit.Kit;
import com.l1ghtld.utility.ChatUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitCommand implements CommandExecutor {

    private static final String PERMISSION_ADMIN = "logkits.admin";
    private static final String PERMISSION_KIT = "logkits.kit";
    private static final String PLAYER_ONLY_MESSAGE = "Команда доступна только для игроков!";

    private final LogKits plugin;

    @Getter
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public KitCommand(LogKits plugin) {
        this.plugin = plugin;
        registerSubCommands();
    }

    private void registerSubCommands() {
        registerSubCommand(new HelpCommand());
        registerSubCommand(new CreateCommand());
        registerSubCommand(new SetCommand());
        registerSubCommand(new SetCooldownCommand());
        registerSubCommand(new PreviewCommand());
        registerSubCommand(new KitsCommand());
        registerSubCommand(new GiveCommand());
    }

    private void registerSubCommand(SubCommand subCommand) {
        subCommands.put(subCommand.getName().toLowerCase(), subCommand);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        boolean isAdmin = sender.hasPermission(PERMISSION_ADMIN);

        if (args.length == 0) {
            return handleNoArgs(sender, isAdmin);
        }

        String subName = args[0].toLowerCase();
        SubCommand subCommand = subCommands.get(subName);

        if (subCommand == null) {
            return handleDirectKitClaim(sender, args[0]);
        }

        if (!canExecute(sender, subCommand, isAdmin)) {
            return true;
        }

        subCommand.execute(plugin, sender, args);
        return true;
    }

    private boolean handleNoArgs(CommandSender sender, boolean isAdmin) {
        if (!isAdmin) {
            sender.sendMessage(ChatUtility.msg("no_perms"));
            return true;
        }
        subCommands.get("help").execute(plugin, sender, new String[0]);
        return true;
    }

    private boolean canExecute(CommandSender sender, SubCommand subCommand, boolean isAdmin) {
        if (subCommand.isAdminOnly() && !isAdmin) {
            sender.sendMessage(ChatUtility.msg("no_perms"));
            return false;
        }

        if (subCommand.isPlayerOnly() && !(sender instanceof Player)) {
            sender.sendMessage(PLAYER_ONLY_MESSAGE);
            return false;
        }

        if (!isAdmin && "preview".equals(subCommand.getName())) {
            if (!sender.hasPermission(PERMISSION_KIT)) {
                sender.sendMessage(ChatUtility.msg("no_perms"));
                return false;
            }
        }

        return true;
    }

    private boolean handleDirectKitClaim(CommandSender sender, String kitName) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatUtility.msg("unknown_subcommand"));
            return true;
        }

        Player player = (Player) sender;
        Kit kit = plugin.getKitManager().loadKit(kitName);

        if (kit == null) {
            sender.sendMessage(ChatUtility.msg("error_kitmsg"));
            return true;
        }

        plugin.getKitManager().giveKitTo(player, kit.getName());
        return true;
    }
}
