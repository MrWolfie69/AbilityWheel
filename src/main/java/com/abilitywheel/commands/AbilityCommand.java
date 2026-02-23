package com.abilitywheel.commands;

import com.abilitywheel.AbilityWheel;
import com.abilitywheel.ability.AbilityType;
import com.abilitywheel.gui.WheelListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * /ability [respin | info | wheel | admin ...]
 * /aw       (alias)
 *
 * Admin:
 *   /ability admin set <player> <ability>
 *   /ability admin remove <player>
 *   /ability admin respin <player>
 *   /ability admin list
 */
public class AbilityCommand implements CommandExecutor, TabCompleter {

    private final AbilityWheel plugin;
    private final WheelListener wheelListener;

    public AbilityCommand(AbilityWheel plugin) {
        this.plugin = plugin;
        // Get the wheel listener from registered listeners
        this.wheelListener = new WheelListener(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            // Show current ability
            if (!(sender instanceof Player player)) {
                sender.sendMessage("This command is for players only.");
                return true;
            }
            showAbility(player);
            return true;
        }

        switch (args[0].toLowerCase()) {

            case "respin" -> {
                if (!(sender instanceof Player player)) { sender.sendMessage("Players only."); return true; }
                if (!player.hasPermission("abilitywheel.respin")) {
                    player.sendMessage(ChatColor.RED + "You don't have permission."); return true;
                }
                wheelListener.openRespinWheel(player);
            }

            case "wheel" -> {
                if (!(sender instanceof Player player)) { sender.sendMessage("Players only."); return true; }
                String uuid = player.getUniqueId().toString();
                if (!plugin.getDataManager().hasAbility(uuid)) {
                    wheelListener.openWheel(player, false);
                } else {
                    player.sendMessage(ChatColor.YELLOW + "✦ Use " + ChatColor.GOLD + "/ability respin" +
                        ChatColor.YELLOW + " to change your ability (costs 5 Netherite Blocks).");
                }
            }

            case "info" -> showAllAbilities(sender);

            case "admin" -> {
                if (!sender.hasPermission("abilitywheel.admin")) {
                    sender.sendMessage(ChatColor.RED + "✦ No permission."); return true;
                }
                handleAdmin(sender, args);
            }

            default -> {
                sender.sendMessage(ChatColor.YELLOW + "Usage: /ability [respin|info|wheel|admin]");
            }
        }
        return true;
    }

    private void showAbility(Player player) {
        String uuid = player.getUniqueId().toString();
        AbilityType ability = plugin.getDataManager().getAbility(uuid);

        if (ability == null) {
            player.sendMessage(ChatColor.YELLOW + "✦ You haven't selected an ability yet!");
            player.sendMessage(ChatColor.YELLOW + "  Type " + ChatColor.GOLD + "/ability wheel" + ChatColor.YELLOW + " to open the wheel.");
        } else {
            player.sendMessage("");
            player.sendMessage(ChatColor.GOLD + "✦ Your Ability: " + ability.getDisplayName());
            player.sendMessage(ChatColor.GRAY + "  " + ability.getDescription());
            player.sendMessage(ChatColor.YELLOW + "  Change: /ability respin " + ChatColor.DARK_GRAY + "(5 Netherite Blocks)");
            player.sendMessage("");
        }
    }

    private void showAllAbilities(CommandSender sender) {
        sender.sendMessage("");
        sender.sendMessage(ChatColor.GOLD + "✦══════ All Abilities ══════✦");
        for (AbilityType a : AbilityType.values()) {
            sender.sendMessage(ChatColor.WHITE + "  " + a.getDisplayName() +
                ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + a.getDescription());
        }
        sender.sendMessage(ChatColor.GOLD + "✦═══════════════════════════✦");
        sender.sendMessage("");
    }

    private void handleAdmin(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.YELLOW + "Admin commands:");
            sender.sendMessage(ChatColor.WHITE + "  /ability admin set <player> <ability>");
            sender.sendMessage(ChatColor.WHITE + "  /ability admin remove <player>");
            sender.sendMessage(ChatColor.WHITE + "  /ability admin respin <player>");
            sender.sendMessage(ChatColor.WHITE + "  /ability admin list");
            return;
        }

        switch (args[1].toLowerCase()) {

            case "set" -> {
                if (args.length < 4) { sender.sendMessage(ChatColor.RED + "Usage: /ability admin set <player> <ability>"); return; }
                Player target = Bukkit.getPlayer(args[2]);
                if (target == null) { sender.sendMessage(ChatColor.RED + "Player not found or offline."); return; }
                AbilityType ability = AbilityType.fromId(args[3]);
                if (ability == null) { sender.sendMessage(ChatColor.RED + "Unknown ability: " + args[3]); return; }

                plugin.getDataManager().setAbility(target.getUniqueId().toString(), ability);
                plugin.getAbilityManager().applyAbility(target, ability);

                sender.sendMessage(ChatColor.GREEN + "✦ Set " + target.getName() + "'s ability to " + ability.getDisplayName());
                target.sendMessage(ChatColor.GOLD + "✦ Admin set your ability to: " + ability.getDisplayName());
            }

            case "remove" -> {
                if (args.length < 3) { sender.sendMessage(ChatColor.RED + "Usage: /ability admin remove <player>"); return; }
                Player target = Bukkit.getPlayer(args[2]);
                if (target == null) { sender.sendMessage(ChatColor.RED + "Player not found or offline."); return; }

                plugin.getDataManager().removeAbility(target.getUniqueId().toString());
                plugin.getAbilityManager().removeAbility(target);
                sender.sendMessage(ChatColor.GREEN + "✦ Removed " + target.getName() + "'s ability.");
            }

            case "respin" -> {
                if (args.length < 3) { sender.sendMessage(ChatColor.RED + "Usage: /ability admin respin <player>"); return; }
                Player target = Bukkit.getPlayer(args[2]);
                if (target == null) { sender.sendMessage(ChatColor.RED + "Player not found or offline."); return; }

                wheelListener.openWheel(target, false);
                sender.sendMessage(ChatColor.GREEN + "✦ Opened ability wheel for " + target.getName() + " (free).");
                target.sendMessage(ChatColor.GOLD + "✦ Admin is letting you re-spin the ability wheel!");
            }

            case "list" -> {
                sender.sendMessage("");
                sender.sendMessage(ChatColor.GOLD + "✦══════ Player Abilities ══════✦");
                var allAbilities = plugin.getDataManager().getAllAbilities();
                if (allAbilities.isEmpty()) {
                    sender.sendMessage(ChatColor.GRAY + "  No players registered yet.");
                } else {
                    allAbilities.forEach((uuid, ability) -> {
                        Player p = Bukkit.getPlayer(java.util.UUID.fromString(uuid));
                        String name = p != null ? p.getName() : "(" + uuid.substring(0, 8) + "...)";
                        String abilityStr = ability != null ? ability.getDisplayName() : ChatColor.RED + "None";
                        String online = p != null ? ChatColor.GREEN + "●" : ChatColor.GRAY + "○";
                        sender.sendMessage("  " + online + " " + ChatColor.WHITE + name + ChatColor.DARK_GRAY + ": " + abilityStr);
                    });
                }
                sender.sendMessage(ChatColor.GOLD + "✦══════════════════════════════✦");
                sender.sendMessage("");
            }

            default -> sender.sendMessage(ChatColor.RED + "Unknown admin subcommand: " + args[1]);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.addAll(Arrays.asList("respin", "info", "wheel", "admin"));
        } else if (args.length == 2 && args[0].equalsIgnoreCase("admin")) {
            completions.addAll(Arrays.asList("set", "remove", "respin", "list"));
        } else if (args.length == 3 && args[0].equalsIgnoreCase("admin")) {
            Bukkit.getOnlinePlayers().forEach(p -> completions.add(p.getName()));
        } else if (args.length == 4 && args[0].equalsIgnoreCase("admin") && args[1].equalsIgnoreCase("set")) {
            for (AbilityType t : AbilityType.values()) completions.add(t.getId());
        }
        return completions;
    }
}
