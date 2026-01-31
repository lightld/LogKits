package com.l1ghtld.command.sub;

import com.l1ghtld.LogKits;
import com.l1ghtld.command.SubCommand;
import com.l1ghtld.kit.Kit;
import com.l1ghtld.utility.ChatUtility;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveCommand implements SubCommand {

    @Override
    public String getName() {
        return "give";
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
        if (args.length < 3) {
            sender.sendMessage(ChatUtility.msg("specify_player_and_kit"));
            return;
        }

        String playerName = args[1];
        String kitName = args[2];

        Player target = plugin.getServer().getPlayer(playerName);
        if (target == null) {
            sender.sendMessage(ChatUtility.msg("player_not_found", "%player%", playerName));
            return;
        }

        Kit kit = plugin.getKitManager().loadKit(kitName);
        if (kit == null) {
            sender.sendMessage(ChatUtility.msg("error_kitmsg"));
            return;
        }

        plugin.getKitManager().giveKitTo(target, kit.getName());
        sender.sendMessage(ChatUtility.msg("kit_given_to_player", "%player%", target.getName(), "%kit%", kit.getName()));
    }
}
