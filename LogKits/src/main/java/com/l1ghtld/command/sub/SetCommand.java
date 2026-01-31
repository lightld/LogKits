package com.l1ghtld.command.sub;

import com.l1ghtld.LogKits;
import com.l1ghtld.command.SubCommand;
import com.l1ghtld.kit.KitItems;
import com.l1ghtld.utility.ChatUtility;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetCommand implements SubCommand {

    @Override
    public String getName() {
        return "set";
    }

    @Override
    public String getPermission() {
        return "logkits.admin";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public boolean isAdminOnly() {
        return true;
    }

    @Override
    public void execute(LogKits plugin, CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if (args.length < 2) {
            sender.sendMessage(ChatUtility.msg("specify_kit_to_set"));
            return;
        }

        String kitName = args[1];

        if (!plugin.getKitManager().exists(kitName)) {
            sender.sendMessage(ChatUtility.msg("error_kitmsg"));
            return;
        }

        KitItems contents = plugin.getKitManager().collectPlayerContents(player);

        if (contents.isCompletelyEmpty()) {
            sender.sendMessage(ChatUtility.msg("kit_set_empty"));
            return;
        }

        if (plugin.getKitManager().saveKitContents(kitName, contents)) {
            int total = countItems(contents);
            sender.sendMessage(ChatUtility.msg("kit_set_ok", "%kit%", kitName, "%items%", String.valueOf(total)));
        } else {
            sender.sendMessage("Ошибка сохранения файла кита.");
        }
    }

    private int countItems(KitItems contents) {
        int count = contents.getItems().size();
        if (contents.getHelmet() != null) count++;
        if (contents.getChestplate() != null) count++;
        if (contents.getLeggings() != null) count++;
        if (contents.getBoots() != null) count++;
        if (contents.getOffhand() != null) count++;
        return count;
    }
}
