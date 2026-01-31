package com.l1ghtld.command.sub;

import com.l1ghtld.LogKits;
import com.l1ghtld.command.SubCommand;
import com.l1ghtld.gui.PreviewGUI;
import com.l1ghtld.kit.Kit;
import com.l1ghtld.utility.ChatUtility;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PreviewCommand implements SubCommand {

    @Override
    public String getName() {
        return "preview";
    }

    @Override
    public String getPermission() {
        return "logkits.kit";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public boolean isAdminOnly() {
        return false;
    }

    @Override
    public void execute(LogKits plugin, CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (args.length < 2) {
            sender.sendMessage(ChatUtility.msg("specify_kit_to_preview"));
            return;
        }

        String kitName = args[1];
        Kit kit = plugin.getKitManager().loadKit(kitName);

        if (kit == null) {
            sender.sendMessage(ChatUtility.msg("error_kitmsg"));
            return;
        }

        new PreviewGUI(plugin).open(player, kit);
    }
}
