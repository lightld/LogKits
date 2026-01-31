package com.l1ghtld.command.sub;

import com.l1ghtld.LogKits;
import com.l1ghtld.command.SubCommand;
import com.l1ghtld.utility.ChatUtility;
import org.bukkit.command.CommandSender;

public class SetCooldownCommand implements SubCommand {

    @Override
    public String getName() {
        return "setcooldown";
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

        if (args.length < 3) {
            sender.sendMessage(ChatUtility.msg("specify_cooldown_value"));
            return;
        }

        String kitName = args[1];
        String cooldown = args[2];

        if (!plugin.getKitManager().exists(kitName)) {
            sender.sendMessage(ChatUtility.msg("error_kitmsg"));
            return;
        }

        try {
            if (!plugin.getKitManager().setKitCooldown(kitName, cooldown)) {
                sender.sendMessage("кулдаун для кита не сохранен");
                return;
            }
        } catch (IllegalArgumentException ex) {
            sender.sendMessage(ChatUtility.msg("invalid_cooldown"));
            return;
        }

        sender.sendMessage(ChatUtility.msg("kit_cooldown_set", "%kit%", kitName, "%cooldown%", cooldown));
    }
}
