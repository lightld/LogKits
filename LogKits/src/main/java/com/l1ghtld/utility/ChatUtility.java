package com.l1ghtld.utility;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.l1ghtld.LogKits;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ChatUtility {
    private static FileConfiguration messagesConfig;

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    private ChatUtility() {}

    public static String color(String s) {
        if (s == null) return "";
        s = translateHex(s);
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    private static String translateHex(String message) {
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String hex = matcher.group(1);
            StringBuilder replacement = new StringBuilder("§x");
            for (char c : hex.toCharArray()) {
                replacement.append('§').append(c);
            }
            matcher.appendReplacement(buffer, replacement.toString());
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    public static String msg(String path) {
        FileConfiguration cfg = getMessagesConfig();
        String raw = cfg.getString("messages." + path, "&cсообщение: " + path);
        return color(raw);
    }

    public static String msg(String path, String... placeholders) {
        String str = msg(path);
        if (placeholders != null) {
            for (int i = 0; i + 1 < placeholders.length; i += 2) {
                str = str.replace(placeholders[i], placeholders[i + 1]);
            }
        }
        return str;
    }

    public static FileConfiguration getMessagesConfig() {
        if (messagesConfig == null) {
            reloadMessages();
        }
        return messagesConfig;
    }

    public static void reloadMessages() {
        LogKits plugin = LogKits.getInstance();
        File file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(file);
    }

    public static void saveMessages() {
        if (messagesConfig == null) return;
        try {
            messagesConfig.save(new File(LogKits.getInstance().getDataFolder(), "messages.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
