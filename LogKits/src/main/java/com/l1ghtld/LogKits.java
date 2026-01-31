package com.l1ghtld;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import com.l1ghtld.command.KitCommand;
import com.l1ghtld.data.DataManager;
import com.l1ghtld.listener.PreviewClickListener;
import com.l1ghtld.kit.KitManager;
import com.l1ghtld.manager.PermissionManager;
import com.l1ghtld.command.tabcompleter.KitTabCompleter;
import com.l1ghtld.utility.ChatUtility;

import java.io.File;

@Getter
public class LogKits extends JavaPlugin {

    private static LogKits instance;
    private KitManager kitManager;
    private DataManager dataManager;
    private PermissionManager permissionManager;

    public static LogKits getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        saveMessagesIfAbsent();
        
        File kitsDir = new File(getDataFolder(), "kits");
        if (!kitsDir.exists()) {
            kitsDir.mkdirs();
        }
        
        this.kitManager = new KitManager(this);
        this.dataManager = new DataManager(this);
        this.permissionManager = new PermissionManager(this);
        this.permissionManager.registerAllExistingKitPermissions(kitManager.listKitNames());
        
        KitCommand kitCommand = new KitCommand(this);
        KitTabCompleter kitTabCompleter = new KitTabCompleter(this);
        PluginCommand cmd = getCommand("kit");
        if (cmd != null) {
            cmd.setExecutor(kitCommand);
            cmd.setTabCompleter(kitTabCompleter);
        }
        
        Bukkit.getPluginManager().registerEvents(new PreviewClickListener(this), this);
    }

    @Override
    public void onDisable() {
        if (dataManager != null) {
            dataManager.save();
        }
    }

    private void saveMessagesIfAbsent() {
        File msgFile = new File(getDataFolder(), "messages.yml");
        if (!msgFile.exists()) {
            saveResource("messages.yml", false);
        }
    }

    public FileConfiguration getMessages() {
        return ChatUtility.getMessagesConfig();
    }
}
