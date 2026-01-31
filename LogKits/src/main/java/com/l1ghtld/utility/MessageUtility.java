package com.l1ghtld.utility;

import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;

@UtilityClass
public class MessageUtility {

    public String get(String key) {
        return ChatUtility.msg(key);
    }

    public String get(String key, String... placeholders) {
        return ChatUtility.msg(key, placeholders);
    }

    public void send(CommandSender sender, String key) {
        sender.sendMessage(get(key));
    }

    public void send(CommandSender sender, String key, String... placeholders) {
        sender.sendMessage(get(key, placeholders));
    }
}
