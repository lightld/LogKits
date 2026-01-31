package com.l1ghtld.kit;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.l1ghtld.LogKits;
import com.l1ghtld.utility.ChatUtility;
import com.l1ghtld.utility.TimeUtility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

@RequiredArgsConstructor
public class KitManager {

    private final LogKits plugin;

    private File getKitsDir() {
        return new File(plugin.getDataFolder(), "kits");
    }

    public boolean exists(String kitName) {
        return new File(getKitsDir(), kitName + ".yml").exists();
    }

    public Set<String> listKitNames() {
        File kitsDir = getKitsDir();
        if (!kitsDir.exists()) return java.util.Collections.emptySet();
        File[] files = kitsDir.listFiles((dir, name) -> name.toLowerCase(Locale.ROOT).endsWith(".yml"));
        if (files == null) return java.util.Collections.emptySet();
        Set<String> names = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (File f : files) {
            String n = f.getName().substring(0, f.getName().length() - 4);
            names.add(n);
        }
        return names;
    }

    public Kit loadKit(String kitName) {
        File file = new File(getKitsDir(), kitName + ".yml");
        if (!file.exists()) return null;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        String name = cfg.getString("kit_name", kitName);
        String cooldownRaw = cfg.getString("cooldown", "1d");
        long cooldownMs;
        try {
            cooldownMs = TimeUtility.parseDurationToMillis(cooldownRaw);
        } catch (IllegalArgumentException ex) {
            cooldownRaw = "1d";
            cooldownMs = 24L * 60L * 60L * 1000L;
        }
        Kit kit = new Kit(name, cooldownRaw, cooldownMs);
        String perm = cfg.getString("kit_perm");
        if (perm != null && !perm.isEmpty()) {
            kit.setRequiredPermission(perm);
        }
        if (cfg.isConfigurationSection("kit_item")) {
            List<?> list = cfg.getList("kit_item.items", new ArrayList<>());
            List<ItemStack> items = new ArrayList<>();
            for (Object o : list) {
                if (o instanceof ItemStack) {
                    ItemStack it = (ItemStack) o;
                    if (it.getType() != Material.AIR) items.add(it);
                }
            }
            kit.setItems(items);
            kit.setHelmet(readItem(cfg, "kit_item.armor.helmet"));
            kit.setChestplate(readItem(cfg, "kit_item.armor.chestplate"));
            kit.setLeggings(readItem(cfg, "kit_item.armor.leggings"));
            kit.setBoots(readItem(cfg, "kit_item.armor.boots"));
            kit.setOffhand(readItem(cfg, "kit_item.offhand"));
        } else if (cfg.isList("kit_item")) {
            List<?> list = cfg.getList("kit_item", new ArrayList<>());
            List<ItemStack> items = new ArrayList<>();
            for (Object o : list) {
                if (o instanceof ItemStack) {
                    ItemStack it = (ItemStack) o;
                    if (it.getType() != Material.AIR) items.add(it);
                }
            }
            kit.setItems(items);
        } else {
            kit.setItems(new ArrayList<>());
        }
        kit.setClaimAccessLine(cfg.getString("claim_button_access", null));
        return kit;
    }

    private ItemStack readItem(YamlConfiguration cfg, String path) {
        Object o = cfg.get(path);
        if (o instanceof ItemStack) {
            ItemStack it = (ItemStack) o;
            if (it.getType() != Material.AIR) return it;
        }
        return null;
    }

    public boolean createKit(String kitName, String defaultCooldownRaw) throws IllegalArgumentException {
        if (exists(kitName)) return false;
        long cd = TimeUtility.parseDurationToMillis(defaultCooldownRaw);
        File file = new File(getKitsDir(), kitName + ".yml");
        YamlConfiguration cfg = new YamlConfiguration();
        cfg.set("kit_name", kitName);
        cfg.set("cooldown", defaultCooldownRaw);
        cfg.set("kit_perm", "logkits.kit." + kitName.toLowerCase(Locale.ROOT));
        cfg.set("kit_item.items", new ArrayList<>());
        cfg.set("claim_button_access", "&fДоступен &eгруппа");
        try {
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public boolean saveKitContents(String kitName, KitItems contents) {
        File file = new File(getKitsDir(), kitName + ".yml");
        if (!file.exists()) return false;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        cfg.set("kit_item.items", contents.getItems());
        cfg.set("kit_item.armor.helmet", contents.getHelmet());
        cfg.set("kit_item.armor.chestplate", contents.getChestplate());
        cfg.set("kit_item.armor.leggings", contents.getLeggings());
        cfg.set("kit_item.armor.boots", contents.getBoots());
        cfg.set("kit_item.offhand", contents.getOffhand());
        try {
            cfg.save(file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean setKitCooldown(String kitName, String cooldownRaw) throws IllegalArgumentException {
        long cd = TimeUtility.parseDurationToMillis(cooldownRaw);
        File file = new File(getKitsDir(), kitName + ".yml");
        if (!file.exists()) return false;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        cfg.set("cooldown", cooldownRaw);
        try {
            cfg.save(file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public KitItems collectPlayerContents(Player p) {
        KitItems kc = new KitItems();
        for (ItemStack it : p.getInventory().getStorageContents()) {
            if (it != null && it.getType() != Material.AIR) kc.getItems().add(it.clone());
        }
        ItemStack[] armor = p.getInventory().getArmorContents();
        if (armor.length >= 4) {
            if (armor[3] != null && armor[3].getType() != Material.AIR) kc.setHelmet(armor[3].clone());
            if (armor[2] != null && armor[2].getType() != Material.AIR) kc.setChestplate(armor[2].clone());
            if (armor[1] != null && armor[1].getType() != Material.AIR) kc.setLeggings(armor[1].clone());
            if (armor[0] != null && armor[0].getType() != Material.AIR) kc.setBoots(armor[0].clone());
        }
        ItemStack off = p.getInventory().getItemInOffHand();
        if (off != null && off.getType() != Material.AIR) kc.setOffhand(off.clone());
        return kc;
    }

    public GiveResult giveKitTo(Player player, String kitName) {
        Kit kit = loadKit(kitName);
        if (kit == null) {
            player.sendMessage(ChatUtility.msg("error_kitmsg"));
            return GiveResult.ERROR;
        }
        if (kit.isEmpty()) {
            player.sendMessage(ChatUtility.msg("kit_give_fail_empty", "%kit%", kit.getName()));
            return GiveResult.EMPTY;
        }
        boolean isAdmin = player.hasPermission("logkits.admin");
        if (!isAdmin) {
            String requiredPerm = kit.getRequiredPermission();
            if (requiredPerm != null && !requiredPerm.isEmpty() && !player.hasPermission(requiredPerm)) {
                player.sendMessage(ChatUtility.msg("no_perms"));
                return GiveResult.ERROR;
            }
        }
        if (!isAdmin) {
            java.util.UUID uuid = player.getUniqueId();
            long now = System.currentTimeMillis();
            long last = plugin.getDataManager().getLastClaim(uuid, kit.getName());
            long next = last + kit.getCooldownMillis();
            if (now < next) {
                long left = next - now;
                String leftStr = TimeUtility.formatDurationRussian(left);
                player.sendMessage(ChatUtility.msg("cd_kit", "%cd%", leftStr));
                return GiveResult.COOLDOWN;
            }
        }
        List<ItemStack> give = new ArrayList<>();
        for (ItemStack it : kit.getItems()) if (it != null && it.getType() != Material.AIR) give.add(it.clone());
        if (kit.getHelmet() != null) give.add(kit.getHelmet().clone());
        if (kit.getChestplate() != null) give.add(kit.getChestplate().clone());
        if (kit.getLeggings() != null) give.add(kit.getLeggings().clone());
        if (kit.getBoots() != null) give.add(kit.getBoots().clone());
        if (kit.getOffhand() != null) give.add(kit.getOffhand().clone());
        java.util.Map<Integer, ItemStack> notFit = player.getInventory().addItem(give.toArray(new ItemStack[0]));
        if (!notFit.isEmpty()) {
            notFit.values().forEach(item -> player.getWorld().dropItemNaturally(player.getLocation(), item));
        }
        if (!isAdmin) {
            plugin.getDataManager().setLastClaim(player.getUniqueId(), kit.getName(), System.currentTimeMillis());
        }
        player.sendMessage(ChatUtility.msg("yes_kit"));
        return GiveResult.SUCCESS;
    }

    public enum GiveResult { SUCCESS, COOLDOWN, EMPTY, ERROR }
}
