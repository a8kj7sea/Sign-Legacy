package me.kirug.signgui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import java.util.function.BiConsumer;

public class SignTemplates {

    public static void requestRename(Player player, String currentName, BiConsumer<Player, String> action) {
        SignGUI.builder()
                .setLines(currentName, "^^^^^^^^^^^^^", "Enter New Name", "to rename above")
                .type(Material.SIGN_POST)
                .onInput((p, lines) -> {
                    String input = lines[0].trim();

                    if (input.isEmpty()) {
                        p.sendMessage(ChatColor.RED + "Name cannot be empty!");
                        return;
                    }

                    if (input.equalsIgnoreCase(currentName)) {
                        p.sendMessage(ChatColor.RED + "New name is the same as current!");
                        return;
                    }

                    action.accept(p, ChatColor.translateAlternateColorCodes('&', input));
                })
                .build()
                .open(player);
    }

    public static void requestInput(Player player, String placeholder, BiConsumer<Player, String> action) {
        SignGUI.builder()
                .setLines("", "^^^^^^^^^^^^^", placeholder, "Enter text above")
                .type(Material.SIGN_POST)
                .onInput((p, lines) -> {
                    String input = lines[0];
                    if (input == null || input.trim().isEmpty()) {
                        p.sendMessage(ChatColor.RED + "Input cannot be empty!");
                        return;
                    }
                    action.accept(p, input.trim());
                })
                .build()
                .open(player);
    }

    public static void requestAmount(Player player, String itemName, BiConsumer<Player, Integer> action) {
        SignGUI.builder()
                .setLines("", "^^^^^^^^^^^^^", "Enter amount for:", itemName)
                .type(Material.SIGN_POST)
                .onInput((p, lines) -> {
                    try {
                        int amount = Integer.parseInt(lines[0].replaceAll("[^0-9]", ""));
                        if (amount <= 0) throw new NumberFormatException();
                        action.accept(p, amount);
                    } catch (NumberFormatException e) {
                        p.sendMessage(ChatColor.RED + "Invalid number!");
                    }
                })
                .build()
                .open(player);
    }

    public static void requestConfirmation(Player player, String reason, Runnable onConfirm) {
        SignGUI.builder()
                .setLines("", "Type 'CONFIRM'", "to proceed with:", reason)
                .type(Material.SIGN_POST)
                .onInput((p, lines) -> {
                    if ("CONFIRM".equalsIgnoreCase(lines[0].trim())) {
                        onConfirm.run();
                    } else {
                        p.sendMessage(ChatColor.RED + "Action cancelled.");
                    }
                })
                .build()
                .open(player);
    }
}