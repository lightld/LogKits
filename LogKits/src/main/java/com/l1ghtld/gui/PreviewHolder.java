package com.l1ghtld.gui;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

@Getter
@RequiredArgsConstructor
public class PreviewHolder implements InventoryHolder {

    private final String kitName;

    @Override
    public Inventory getInventory() {
        return null;
    }
}
