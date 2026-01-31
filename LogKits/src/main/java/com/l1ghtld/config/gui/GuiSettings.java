package com.l1ghtld.config.gui;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GuiSettings {

    private final String menuTitle;
    private final int size;

    private final int helmetSlot;
    private final int chestplateSlot;
    private final int leggingsSlot;
    private final int bootsSlot;
    private final int offhandSlot;

    private final List<Integer> itemSlots;

    private final ButtonSettings claimButton;
    private final ButtonSettings backButton;

    public GuiSettings(FileConfiguration config) {
        ConfigurationSection gui = config.getConfigurationSection("gui");

        this.menuTitle = gui != null ? gui.getString("menu_title", "&fПросмотр набора") : "&fПросмотр набора";
        int rawSize = gui != null ? gui.getInt("size", 54) : 54;
        rawSize = Math.max(9, rawSize);
        if (rawSize % 9 != 0) rawSize = ((rawSize / 9) + 1) * 9;
        if (rawSize > 54) rawSize = 54;
        this.size = rawSize;

        ConfigurationSection slots = gui != null ? gui.getConfigurationSection("slots") : null;
        this.helmetSlot = slots != null ? slots.getInt("helmet", -1) : -1;
        this.chestplateSlot = slots != null ? slots.getInt("chestplate", -1) : -1;
        this.leggingsSlot = slots != null ? slots.getInt("leggings", -1) : -1;
        this.bootsSlot = slots != null ? slots.getInt("boots", -1) : -1;
        this.offhandSlot = slots != null ? slots.getInt("offhand", -1) : -1;

        List<Integer> slots2 = gui != null ? gui.getIntegerList("item-slots") : null;
        if (slots2 == null || slots2.isEmpty()) {
            slots2 = new ArrayList<>();
            for (int i = 9; i < size; i++) slots2.add(i);
        }
        this.itemSlots = slots2;

        this.claimButton = new ButtonSettings(gui != null ? gui.getConfigurationSection("claim_button") : null,
                Material.SLIME_BLOCK, "&a&lПолучить набор", size - 1, 0);
        this.backButton = new ButtonSettings(gui != null ? gui.getConfigurationSection("back_button") : null,
                Material.ARROW, "&6Назад", 45, 10002);
    }

    @Getter
    public static class ButtonSettings {
        private final Material material;
        private final String displayName;
        private final int slot;
        private final int modelData;
        private final List<String> lore;
        private final List<String> leftClickCommands;
        private final List<String> rightClickCommands;

        public ButtonSettings(ConfigurationSection section, Material defaultMat, String defaultName, int defaultSlot, int defaultModelData) {
            if (section == null) {
                this.material = defaultMat;
                this.displayName = defaultName;
                this.slot = defaultSlot;
                this.modelData = defaultModelData;
                this.lore = new ArrayList<>();
                this.leftClickCommands = new ArrayList<>();
                this.rightClickCommands = new ArrayList<>();
                return;
            }

            String matName = section.getString("material", defaultMat.name());
            Material mat = Material.matchMaterial(matName);
            this.material = mat != null ? mat : defaultMat;

            this.displayName = section.getString("display_name", defaultName);
            this.slot = section.getInt("slot", defaultSlot);
            this.modelData = section.getInt("model_data", defaultModelData);
            this.lore = section.getStringList("lore");
            this.leftClickCommands = section.getStringList("left_click_commands");
            this.rightClickCommands = section.getStringList("right_click_commands");
        }

        public boolean hasModelData() {
            return modelData > 0;
        }
    }
}
