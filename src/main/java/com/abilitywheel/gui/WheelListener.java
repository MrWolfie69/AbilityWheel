package com.abilitywheel.gui;

import com.abilitywheel.AbilityWheel;
import com.abilitywheel.ability.AbilityType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class WheelListener implements Listener {

    private final AbilityWheel plugin;
    // Players who currently have the wheel open (to detect close without selecting)
    private final Set<UUID> wheelOpen = new HashSet<>();

    public WheelListener(AbilityWheel plugin) {
        this.plugin = plugin;
    }

    // ─── PLAYER JOIN ─────────────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();

        if (!plugin.getDataManager().isRegistered(uuid)) {
            // Brand new player - register and show wheel after 2 seconds
            plugin.getDataManager().registerPlayer(uuid);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (player.isOnline()) {
                    player.sendMessage("");
                    player.sendMessage(ChatColor.GOLD + "✦══════════════════════════════✦");
                    player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "  Welcome to the server!");
                    player.sendMessage(ChatColor.WHITE + "  Select a " + ChatColor.GOLD + ChatColor.BOLD + "permanent ability" + ChatColor.WHITE + " from the wheel.");
                    player.sendMessage(ChatColor.GRAY + "  Choose wisely — you can only change");
                    player.sendMessage(ChatColor.GRAY + "  it by paying " + ChatColor.DARK_PURPLE + "5 Netherite Blocks" + ChatColor.GRAY + "!");
                    player.sendMessage(ChatColor.GOLD + "✦══════════════════════════════✦");
                    player.sendMessage("");
                    openWheel(player, false);
                }
            }, 40L); // 2 seconds delay

        } else if (plugin.getDataManager().hasAbility(uuid)) {
            // Returning player - reapply their ability
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (player.isOnline()) {
                    AbilityType ability = plugin.getDataManager().getAbility(uuid);
                    plugin.getAbilityManager().applyAbility(player, ability);
                }
            }, 20L);
        }
    }

    // ─── OPEN WHEEL ──────────────────────────────────────────────────────────

    public void openWheel(Player player, boolean isRespin) {
        Inventory wheel = WheelGUI.buildWheel(isRespin);
        player.openInventory(wheel);
        wheelOpen.add(player.getUniqueId());
    }

    public void openRespinWheel(Player player) {
        // Check netherite count
        int count = countItem(player, Material.NETHERITE_BLOCK);
        if (count < 5) {
            player.sendMessage(ChatColor.RED + "✦ You need " +
                ChatColor.DARK_PURPLE + ChatColor.BOLD + "5 Netherite Blocks " +
                ChatColor.RED + "to re-spin! You have: " + count);
            return;
        }
        // Take blocks
        removeItem(player, Material.NETHERITE_BLOCK, 5);
        player.sendMessage(ChatColor.GOLD + "✦ 5 Netherite Blocks consumed! Choose your new ability...");
        openWheel(player, true);
    }

    // ─── INVENTORY CLICK ─────────────────────────────────────────────────────

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        Inventory inv = event.getInventory();
        String title = event.getView().getTitle();

        // Only handle our wheel inventories
        if (!title.equals(WheelGUI.WHEEL_TITLE) && !title.equals(WheelGUI.RESPIN_TITLE)) return;

        // Cancel ALL clicks in this GUI (prevent taking items)
        event.setCancelled(true);

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;
        if (event.getCurrentItem().getType() == Material.GRAY_STAINED_GLASS_PANE) return;
        if (event.getCurrentItem().getType() == Material.BLACK_STAINED_GLASS_PANE) return;
        if (event.getCurrentItem().getType() == Material.NETHERITE_BLOCK) return;

        // Check which ability was clicked
        AbilityType clicked = WheelGUI.getAbilityAtSlot(event.getRawSlot());
        if (clicked == null) return;

        // Handle selection
        player.closeInventory();
        wheelOpen.remove(player.getUniqueId());

        String uuid = player.getUniqueId().toString();
        plugin.getDataManager().setAbility(uuid, clicked);
        plugin.getAbilityManager().applyAbility(player, clicked);
        plugin.getAbilityManager().celebrate(player, clicked);
    }

    // Prevent dragging items in GUI
    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        String title = event.getView().getTitle();
        if (title.equals(WheelGUI.WHEEL_TITLE) || title.equals(WheelGUI.RESPIN_TITLE)) {
            event.setCancelled(true);
        }
    }

    // ─── INVENTORY CLOSE ─────────────────────────────────────────────────────

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        if (!wheelOpen.contains(player.getUniqueId())) return;

        String uuid = player.getUniqueId().toString();
        wheelOpen.remove(player.getUniqueId());

        // If they closed without selecting and still no ability, re-open after 1 second
        if (!plugin.getDataManager().hasAbility(uuid)) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (player.isOnline() && !plugin.getDataManager().hasAbility(uuid)) {
                    player.sendMessage(ChatColor.RED + "✦ You must select an ability before playing!");
                    openWheel(player, false);
                }
            }, 20L);
        }
    }

    // ─── HELPERS ─────────────────────────────────────────────────────────────

    private int countItem(Player player, Material mat) {
        int count = 0;
        for (var item : player.getInventory().getContents()) {
            if (item != null && item.getType() == mat) count += item.getAmount();
        }
        return count;
    }

    private void removeItem(Player player, Material mat, int amount) {
        int remaining = amount;
        var contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length && remaining > 0; i++) {
            if (contents[i] != null && contents[i].getType() == mat) {
                int remove = Math.min(remaining, contents[i].getAmount());
                contents[i].setAmount(contents[i].getAmount() - remove);
                remaining -= remove;
                if (contents[i].getAmount() == 0) contents[i] = null;
            }
        }
        player.getInventory().setContents(contents);
    }
}
