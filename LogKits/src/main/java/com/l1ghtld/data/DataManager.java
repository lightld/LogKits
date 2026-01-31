package com.l1ghtld.data;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.l1ghtld.LogKits;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class DataManager {

    private final LogKits plugin;
    private final File file;
    private FileConfiguration cfg;

    public DataManager(LogKits plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "data.yml");
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.cfg = YamlConfiguration.loadConfiguration(file);
    }

    public long getLastClaim(UUID uuid, String kitName) {
        return cfg.getLong(uuid.toString() + "." + kitName + ".last", 0L);
    }

    public void setLastClaim(UUID uuid, String kitName, long timestamp) {
        cfg.set(uuid.toString() + "." + kitName + ".last", timestamp);
        save();
    }

    public void save() {
        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
