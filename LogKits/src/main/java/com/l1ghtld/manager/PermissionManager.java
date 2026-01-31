package com.l1ghtld.manager;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import com.l1ghtld.LogKits;

import java.util.Locale;
import java.util.Set;

@RequiredArgsConstructor
public class PermissionManager {

    private final LogKits plugin;

    public String buildKitPermission(String kitName) {
        return "logkits.kit." + (kitName == null ? "" : kitName.toLowerCase(Locale.ROOT));
    }

    public String registerKitPermission(String kitName) {
        String node = buildKitPermission(kitName);
        if (Bukkit.getPluginManager().getPermission(node) == null) {
            Permission perm = new Permission(node, "Кит " + kitName, PermissionDefault.FALSE);
            Bukkit.getPluginManager().addPermission(perm);
        }
        return node;
    }

    public void registerAllExistingKitPermissions(Set<String> kitNames) {
        for (String name : kitNames) {
            registerKitPermission(name);
        }
    }
}
