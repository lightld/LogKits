package com.l1ghtld.utility;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemUtility {

    private final ItemStack item;
    private final ItemMeta meta;

    public ItemUtility(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }

    public ItemUtility(ItemStack item) {
        this.item = item.clone();
        this.meta = this.item.getItemMeta();
    }

    public ItemUtility name(String name) {
        if (meta != null) {
            meta.setDisplayName(ChatUtility.color(name));
        }
        return this;
    }

    public ItemUtility lore(String... lines) {
        if (meta != null) {
            List<String> lore = new ArrayList<>();
            for (String line : lines) {
                lore.add(ChatUtility.color(line));
            }
            meta.setLore(lore);
        }
        return this;
    }

    public ItemUtility lore(List<String> lines) {
        if (meta != null) {
            List<String> lore = new ArrayList<>();
            for (String line : lines) {
                lore.add(ChatUtility.color(line));
            }
            meta.setLore(lore);
        }
        return this;
    }

    public ItemUtility addLore(String line) {
        if (meta != null) {
            List<String> lore = meta.getLore();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            lore.add(ChatUtility.color(line));
            meta.setLore(lore);
        }
        return this;
    }

    public ItemUtility customModelData(int data) {
        if (meta != null && data > 0) {
            meta.setCustomModelData(data);
        }
        return this;
    }

    public ItemUtility flags(ItemFlag... flags) {
        if (meta != null) {
            meta.addItemFlags(flags);
        }
        return this;
    }

    public ItemUtility hideFlags() {
        if (meta != null) {
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }

    public ItemUtility amount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemStack build() {
        if (meta != null) {
            item.setItemMeta(meta);
        }
        return item;
    }
}
