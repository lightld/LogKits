package com.l1ghtld.gui;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import com.l1ghtld.LogKits;
import com.l1ghtld.config.gui.GuiSettings;
import com.l1ghtld.config.gui.GuiSettings.ButtonSettings;
import com.l1ghtld.kit.Kit;
import com.l1ghtld.utility.ChatUtility;
import com.l1ghtld.utility.ItemUtility;
import com.l1ghtld.utility.TimeUtility;

import java.util.*;

@RequiredArgsConstructor
public class PreviewGUI {

    private final LogKits plugin;

    public void open(Player player, Kit kit) {
        GuiSettings settings = new GuiSettings(plugin.getConfig());

        String title = ChatUtility.color(settings.getMenuTitle());
        int size = settings.getSize();

        Inventory inv = Bukkit.createInventory(new PreviewHolder(kit.getName()), size, title);
        Set<Integer> reserved = new HashSet<>();

        placeArmorItems(inv, kit, settings, reserved, size);
        placeInventoryItems(inv, kit, settings, reserved, size);
        placeClaimButton(inv, player, kit, settings, size);
        placeBackButton(inv, settings, reserved, size);

        player.openInventory(inv);
    }

    private void placeArmorItems(Inventory inv, Kit kit, GuiSettings settings, Set<Integer> reserved, int size) {
        placeItem(inv, kit.getHelmet(), settings.getHelmetSlot(), reserved, size);
        placeItem(inv, kit.getChestplate(), settings.getChestplateSlot(), reserved, size);
        placeItem(inv, kit.getLeggings(), settings.getLeggingsSlot(), reserved, size);
        placeItem(inv, kit.getBoots(), settings.getBootsSlot(), reserved, size);
        placeItem(inv, kit.getOffhand(), settings.getOffhandSlot(), reserved, size);
    }

    private void placeItem(Inventory inv, ItemStack item, int slot, Set<Integer> reserved, int size) {
        if (item != null && isValidSlot(slot, size)) {
            inv.setItem(slot, item);
            reserved.add(slot);
        }
    }

    private void placeInventoryItems(Inventory inv, Kit kit, GuiSettings settings, Set<Integer> reserved, int size) {
        List<Integer> itemSlots = settings.getItemSlots();
        int slotIndex = 0;

        for (ItemStack item : kit.getItems()) {
            if (item == null || item.getType() == Material.AIR) continue;

            while (slotIndex < itemSlots.size()) {
                int slot = itemSlots.get(slotIndex);
                if (isValidSlot(slot, size) && !reserved.contains(slot)) {
                    inv.setItem(slot, item);
                    slotIndex++;
                    break;
                }
                slotIndex++;
            }

            if (slotIndex >= itemSlots.size()) break;
        }
    }

    private void placeClaimButton(Inventory inv, Player player, Kit kit, GuiSettings settings, int size) {
        ButtonSettings btn = settings.getClaimButton();
        if (btn.getMaterial() == null) return;

        String cooldownText = calculateCooldownText(player, kit);

        List<String> processedLore = new ArrayList<>();
        for (String line : btn.getLore()) {
            processedLore.add(line.replace("%cooldown%", cooldownText));
        }

        if (kit.getClaimAccessLine() != null && !kit.getClaimAccessLine().isEmpty()) {
            processedLore.add(kit.getClaimAccessLine());
        }

        ItemStack button = new ItemUtility(btn.getMaterial())
                .name(btn.getDisplayName())
                .lore(processedLore)
                .hideFlags()
                .build();

        if (isValidSlot(btn.getSlot(), size)) {
            inv.setItem(btn.getSlot(), button);
        }
    }

    private void placeBackButton(Inventory inv, GuiSettings settings, Set<Integer> reserved, int size) {
        ButtonSettings btn = settings.getBackButton();
        if (btn.getMaterial() == null) return;

        ItemStack button = new ItemUtility(btn.getMaterial())
                .name(btn.getDisplayName())
                .customModelData(btn.getModelData())
                .hideFlags()
                .build();

        int slot = btn.getSlot();
        if (isValidSlot(slot, size)) {
            inv.setItem(slot, button);
            reserved.add(slot);
        }
    }

    private String calculateCooldownText(Player player, Kit kit) {
        long lastClaim = plugin.getDataManager().getLastClaim(player.getUniqueId(), kit.getName());
        long now = System.currentTimeMillis();
        long nextClaimTime = lastClaim + kit.getCooldownMillis();
        long remainingTime = Math.max(0, nextClaimTime - now);

        return TimeUtility.formatDurationRussian(remainingTime == 0 ? kit.getCooldownMillis() : remainingTime);
    }

    private boolean isValidSlot(int slot, int size) {
        return slot >= 0 && slot < size;
    }
}
