package com.l1ghtld.config;

import com.l1ghtld.LogKits;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

@RequiredArgsConstructor
public class ConfigManager {

    private final LogKits plugin;

    @Getter
    private FileConfiguration messagesConfig;

    public void loadAll() {
        plugin.saveDefaultConfig();
        loadMessages();
    }

    public void loadMessages() {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(file);
    }

    public void saveMessages() {
        if (messagesConfig == null) return;
        try {
            messagesConfig.save(new File(plugin.getDataFolder(), "messages.yml"));
        } catch (IOException e) {
        }
    }

    public void reloadAll() {
        plugin.reloadConfig();
        loadMessages();
    }
}
