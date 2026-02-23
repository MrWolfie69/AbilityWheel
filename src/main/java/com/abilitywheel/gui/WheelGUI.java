package com.abilitywheel.gui;

import com.abilitywheel.ability.AbilityType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

/**
 * Builds a 54-slot (6 row) chest inventory as the Ability Wheel.
 *
 * Layout (6x9 grid, slots 0-53):
 *  ░░░░░░░░░  <- Row 0: border
 *  ░AAAAAAA░  <- Row 1: abilities 1-7
 *  ░░░░░░░░░  <- Row 2: border
 *  ░A░A░A░A░  <- Row 3: abilities 8-11
 *  ░░░A░░░░░  <- Row 4: ability 12
 *  ░░░░░░░░░  <- Row 5: border
 *
 * Uses standard chest inventory = works with Geyser (Bedrock) automatically!
 * GUI Title identifies it so WheelListener can filter clicks.
 */
public class WheelGUI {

    public static final String WHEEL_TITLE = ChatColor.GOLD + "" + ChatColor.BOLD + "✦ Choose Your Ability! ✦";
    public static final String RESPIN_TITLE = ChatColor.GOLD + "" + ChatColor.BOLD + "✦ Re-Spin Ability Wheel ✦";

    // Slots where ability items are placed
    public static final int[] ABILITY_SLOTS = {
        10, 11, 12, 13, 14, 15, 16,   // Row 1: 7 abilities
        28, 30, 32, 34,               // Row 3: 4 abilities
        40                            // Row 4: 1 ability  (total = 12)
    };

    public static Inventory buildWheel(boolean isRespin) {
        String title = isRespin ? RESPIN_TITLE : WHEEL_TITLE;
        Inventory inv = Bukkit.createInventory(null, 54, title);

        // Fill entire inventory with border glass first
        ItemStack border = glass(Material.GRAY_STAINED_GLASS_PANE, " ");
        for (int i = 0; i < 54; i++) inv.setItem(i, border);

        // Place black filler in center slots
        ItemStack filler = glass(Material.BLACK_STAINED_GLASS_PANE, " ");
        int[] centerSlots = {19, 20, 21, 22, 23, 24, 25, 37, 38, 39, 41, 42, 43};
        for (int s : centerSlots) inv.setItem(s, filler);

        // If respin, show cost info in center
        if (isRespin) {
            inv.setItem(22, buildCostItem());
        }

        // Place ability items
        AbilityType[] abilities = AbilityType.values();
        for (int i = 0; i < abilities.length && i < ABILITY_SLOTS.length; i++) {
            inv.setItem(ABILITY_SLOTS[i], buildAbilityItem(abilities[i]));
        }

        return inv;
    }

    private static ItemStack buildAbilityItem(AbilityType ability) {
        ItemStack item = new ItemStack(ability.getIcon());
        ItemMeta meta = item.getItemMeta();

        // Display name
        meta.setDisplayName(ability.getDisplayName());

        // Lore (description + hint)
        meta.setLore(Arrays.asList(
            ChatColor.GRAY + ability.getDescription(),
            "",
            ChatColor.YELLOW + "» Click to select this ability!",
            ChatColor.DARK_GRAY + "This ability will be permanent."
        ));

        // Enchantment glow
        meta.addEnchant(Enchantment.LOOT_BONUS_MOBS, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack buildCostItem() {
        ItemStack item = new ItemStack(Material.NETHERITE_BLOCK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Re-Spin Cost");
        meta.setLore(Arrays.asList(
            ChatColor.GRAY + "5 Netherite Blocks have been",
            ChatColor.GRAY + "taken from your inventory.",
            "",
            ChatColor.YELLOW + "Select a new ability below!"
        ));
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack glass(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }

    /** Check if slot is an ability slot, return ability or null */
    public static AbilityType getAbilityAtSlot(int slot) {
        for (int i = 0; i < ABILITY_SLOTS.length; i++) {
            if (ABILITY_SLOTS[i] == slot && i < AbilityType.values().length) {
                return AbilityType.values()[i];
            }
        }
        return null;
    }
}
