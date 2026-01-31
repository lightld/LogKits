package com.l1ghtld.command;

import org.bukkit.command.CommandSender;
import com.l1ghtld.LogKits;

public interface SubCommand {

    String getName();

    String getPermission();

    boolean isPlayerOnly();

    boolean isAdminOnly();

    void execute(LogKits plugin, CommandSender sender, String[] args);
}
