package com.l1ghtld.command.sub;

import com.l1ghtld.LogKits;
import com.l1ghtld.command.SubCommand;
import com.l1ghtld.utility.ChatUtility;
import org.bukkit.command.CommandSender;

public class CreateCommand implements SubCommand {

    private static final String DEFAULT_COOLDOWN = "1d";

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getPermission() {
        return "logkits.admin";
    }

    @Override
    public boolean isPlayerOnly() {
        return false;
    }

    @Override
    public boolean isAdminOnly() {
        return true;
    }

    @Override
    public void execute(LogKits plugin, CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatUtility.msg("specify_kit_name"));
            return;
        }

        String kitName = args[1];

        try {
            if (!plugin.getKitManager().createKit(kitName, DEFAULT_COOLDOWN)) {
                sender.sendMessage(ChatUtility.msg("kit_exists", "%kit%", kitName));
                return;
            }
        } catch (IllegalArgumentException ex) {
            sender.sendMessage(ChatUtility.msg("invalid_cooldown"));
            return;
        }

        sender.sendMessage(ChatUtility.msg("kit_created", "%kit%", kitName, "%cooldown%", DEFAULT_COOLDOWN));
        String perm = plugin.getPermissionManager().registerKitPermission(kitName);
        sender.sendMessage(ChatUtility.msg("perm_info", "%perm%", perm));
    }
}
