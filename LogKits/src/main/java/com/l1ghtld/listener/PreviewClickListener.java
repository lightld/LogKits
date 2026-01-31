package com.l1ghtld.listener;

import com.l1ghtld.gui.PreviewHolder;
import com.l1ghtld.config.gui.GuiSettings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import com.l1ghtld.LogKits;
import com.l1ghtld.kit.Kit;
import com.l1ghtld.kit.KitManager;
import com.l1ghtld.utility.ChatUtility;

import java.util.List;

public class PreviewClickListener implements Listener {

    private final LogKits plugin;

    public PreviewClickListener(LogKits plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        if (!(e.getInventory().getHolder() instanceof PreviewHolder)) return;

        e.setCancelled(true);
        Player p = (Player) e.getWhoClicked();
        PreviewHolder holder = (PreviewHolder) e.getInventory().getHolder();

        int claimSlot = plugin.getConfig().getInt("gui.claim_button.slot", e.getInventory().getSize() - 1);
        int backSlot = plugin.getConfig().getInt("gui.back_button.slot", 45);

        if (e.getRawSlot() == claimSlot) {
            p.closeInventory();
            KitManager km = plugin.getKitManager();
            Kit kit = km.loadKit(holder.getKitName());
            if (kit == null) {
                p.sendMessage(ChatUtility.msg("error_kitmsg"));
                return;
            }
            km.giveKitTo(p, kit.getName());
            return;
        }

        if (e.getRawSlot() == backSlot) {
            p.closeInventory();

            GuiSettings guiSettings = new GuiSettings(plugin.getConfig());
            GuiSettings.ButtonSettings backButton = guiSettings.getBackButton();

            List<String> commands;
            if (e.getClick() == ClickType.RIGHT || e.getClick() == ClickType.SHIFT_RIGHT) {
                commands = backButton.getRightClickCommands();
            } else {
                commands = backButton.getLeftClickCommands();
            }

            for (String cmd : commands) {
                executeCommand(p, cmd);
            }
        }
    }

    private void executeCommand(Player player, String command) {
        if (command == null || command.isEmpty()) return;

        command = command.replace("%player%", player.getName());

        if (command.startsWith("[player] ")) {
            String cmd = command.substring(9);
            player.performCommand(cmd);
        } else if (command.startsWith("[console] ")) {
            String cmd = command.substring(10);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        } else if (command.startsWith("[openguimenu] ")) {
            String menu = command.substring(14);
            player.performCommand("dm open " + menu);
        } else if (command.startsWith("[close]")) {
            player.closeInventory();
        } else if (command.startsWith("[message] ")) {
            String msg = command.substring(10);
            player.sendMessage(ChatUtility.color(msg));
        } else {
            player.performCommand(command);
        }
    }
}
