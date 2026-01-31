package com.l1ghtld.kit;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class KitItems {
    private final List<ItemStack> items = new ArrayList<>();
    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private ItemStack offhand;

    public boolean isCompletelyEmpty() {
        if (offhand != null) return false;
        if (helmet != null) return false;
        if (chestplate != null) return false;
        if (leggings != null) return false;
        if (boots != null) return false;
        for (ItemStack it : items) {
            if (it != null && it.getType().isItem()) return false;
        }
        return true;
    }
}
