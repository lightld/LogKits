package com.l1ghtld.command.sub;

import com.l1ghtld.LogKits;
import com.l1ghtld.command.SubCommand;
import com.l1ghtld.utility.ChatUtility;
import org.bukkit.command.CommandSender;

public class HelpCommand implements SubCommand {

    @Override
    public String getName() {
        return "help";
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
        sender.sendMessage(ChatUtility.msg("help_header"));
        for (String line : ChatUtility.getMessagesConfig().getStringList("messages.help_lines")) {
            sender.sendMessage(ChatUtility.color(line));
        }
        sender.sendMessage(ChatUtility.msg("help_footer"));
    }
}
