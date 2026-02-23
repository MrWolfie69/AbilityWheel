package com.abilitywheel.ability;

import com.abilitywheel.AbilityWheel;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class AbilityManager {

    private final AbilityWheel plugin;
    // 5 minutes duration in ticks - refreshed every 3 seconds so it never expires
    private static final int DURATION = 6000;

    public AbilityManager(AbilityWheel plugin) {
        this.plugin = plugin;
    }

    /** Apply ability to player - called on selection or on join */
    public void applyAbility(Player player, AbilityType ability) {
        if (ability == null) return;
        removeAllAbilityEffects(player);
        giveEffect(player, ability);
        player.sendActionBar(ChatColor.GOLD + "✦ Active: " + ability.getDisplayName());
    }

    /** Refresh effect so it never expires - called every 3 seconds */
    public void refreshEffect(Player player) {
        String uuid = player.getUniqueId().toString();
        if (!plugin.getDataManager().hasAbility(uuid)) return;

        AbilityType ability = plugin.getDataManager().getAbility(uuid);
        if (ability == null) return;

        PotionEffect current = player.getPotionEffect(ability.getEffectType());
        // Only refresh if under 5 seconds remaining
        if (current == null || current.getDuration() < 100) {
            giveEffect(player, ability);
        }
    }

    private void giveEffect(Player player, AbilityType ability) {
        player.addPotionEffect(new PotionEffect(
            ability.getEffectType(),
            DURATION,
            ability.getAmplifier(),
            true,   // ambient = subtle particles
            false,  // no particles
            true    // show icon in HUD
        ));
    }

    /** Remove ability - called by admin command */
    public void removeAbility(Player player) {
        removeAllAbilityEffects(player);
        player.sendMessage(ChatColor.RED + "✦ Your ability has been removed by an admin.");
    }

    private void removeAllAbilityEffects(Player player) {
        for (AbilityType type : AbilityType.values()) {
            player.removePotionEffect(type.getEffectType());
        }
    }

    /** Play celebration effects when ability is chosen */
    public void celebrate(Player player, AbilityType ability) {
        // Sound
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);

        // Chat message
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "✦══════════════════════✦");
        player.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "    ABILITY SELECTED!");
        player.sendMessage(ChatColor.WHITE + "    " + ability.getDisplayName());
        player.sendMessage(ChatColor.GRAY + "    " + ability.getDescription());
        player.sendMessage(ChatColor.GOLD + "✦══════════════════════✦");
        player.sendMessage("");

        // Title on screen
        player.sendTitle(
            ChatColor.GOLD + "" + ChatColor.BOLD + "ABILITY SELECTED",
            ability.getDisplayName(),
            10, 60, 20
        );
    }
}
