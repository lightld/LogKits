package com.l1ghtld.kit;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Kit {
    private final String name;
    private String cooldownRaw;
    private long cooldownMillis;
    private List<ItemStack> items = new ArrayList<>();
    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack offhand;
    private String claimAccessLine;
    private String requiredPermission;

    public Kit(String name, String cooldownRaw, long cooldownMillis) {
        this.name = name;
        this.cooldownRaw = cooldownRaw;
        this.cooldownMillis = cooldownMillis;
    }

    public void setItems(List<ItemStack> items) {
        this.items = items != null ? items : new ArrayList<>();
    }

    public void setHelmet(ItemStack helmet) {
        this.helmet = armorOrNull(helmet);
    }

    public void setChestplate(ItemStack chestplate) {
        this.chestplate = armorOrNull(chestplate);
    }

    public void setLeggings(ItemStack leggings) {
        this.leggings = armorOrNull(leggings);
    }

    public void setBoots(ItemStack boots) {
        this.boots = armorOrNull(boots);
    }

    public boolean isEmpty() {
        if (offhand != null && offhand.getType().isItem()) return false;
        if (helmet != null && helmet.getType().isItem()) return false;
        if (chestplate != null && chestplate.getType().isItem()) return false;
        if (leggings != null && leggings.getType().isItem()) return false;
        if (boots != null && boots.getType().isItem()) return false;
        for (ItemStack it : items) {
            if (it != null && it.getType().isItem()) return false;
        }
        return true;
    }

    private ItemStack armorOrNull(ItemStack it) {
        if (it == null) return null;
        if (!it.getType().isItem()) return null;
        return it;
    }
}
