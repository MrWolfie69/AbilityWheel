package com.abilitywheel.ability;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;

public enum AbilityType {

    STRENGTH(
        "strength",
        ChatColor.RED + "⚔ Strength",
        "Deal extra melee damage permanently",
        Material.DIAMOND_SWORD,
        ChatColor.RED,
        PotionEffectType.STRENGTH, 1
    ),
    REGENERATION(
        "regeneration",
        ChatColor.GREEN + "💚 Regeneration",
        "Slowly regenerate health over time",
        Material.GOLDEN_APPLE,
        ChatColor.GREEN,
        PotionEffectType.REGENERATION, 0
    ),
    DEFENCE(
        "defence",
        ChatColor.BLUE + "🛡 Defence",
        "Take reduced damage permanently",
        Material.IRON_CHESTPLATE,
        ChatColor.BLUE,
        PotionEffectType.RESISTANCE, 1
    ),
    SPEED(
        "speed",
        ChatColor.YELLOW + "⚡ Speed",
        "Move faster permanently",
        Material.SUGAR,
        ChatColor.YELLOW,
        PotionEffectType.SPEED, 1
    ),
    JUMP_BOOST(
        "jump_boost",
        ChatColor.AQUA + "🐇 Jump Boost",
        "Jump much higher permanently",
        Material.RABBIT_FOOT,
        ChatColor.AQUA,
        PotionEffectType.JUMP_BOOST, 2
    ),
    HASTE(
        "haste",
        ChatColor.GOLD + "⛏ Haste",
        "Mine blocks faster permanently",
        Material.GOLDEN_PICKAXE,
        ChatColor.GOLD,
        PotionEffectType.HASTE, 1
    ),
    NIGHT_VISION(
        "night_vision",
        ChatColor.DARK_PURPLE + "👁 Night Vision",
        "See perfectly in the dark",
        Material.ENDER_EYE,
        ChatColor.DARK_PURPLE,
        PotionEffectType.NIGHT_VISION, 0
    ),
    FIRE_RESISTANCE(
        "fire_resistance",
        ChatColor.RED + "🔥 Fire Resistance",
        "Immune to fire and lava",
        Material.BLAZE_ROD,
        ChatColor.RED,
        PotionEffectType.FIRE_RESISTANCE, 0
    ),
    WATER_BREATHING(
        "water_breathing",
        ChatColor.DARK_AQUA + "🐠 Water Breathing",
        "Breathe underwater forever",
        Material.PUFFERFISH,
        ChatColor.DARK_AQUA,
        PotionEffectType.WATER_BREATHING, 0
    ),
    LUCK(
        "luck",
        ChatColor.GREEN + "🍀 Luck",
        "Better loot from fishing and chests",
        Material.EMERALD,
        ChatColor.GREEN,
        PotionEffectType.LUCK, 2
    ),
    ABSORPTION(
        "absorption",
        ChatColor.GOLD + "💛 Absorption",
        "Extra golden hearts permanently",
        Material.ENCHANTED_GOLDEN_APPLE,
        ChatColor.GOLD,
        PotionEffectType.ABSORPTION, 1
    ),
    SATURATION(
        "saturation",
        ChatColor.DARK_GREEN + "🍖 Saturation",
        "Hunger bar depletes much slower",
        Material.COOKED_BEEF,
        ChatColor.DARK_GREEN,
        PotionEffectType.SATURATION, 0
    );

    private final String id;
    private final String displayName;
    private final String description;
    private final Material icon;
    private final ChatColor color;
    private final PotionEffectType effectType;
    private final int amplifier;

    AbilityType(String id, String displayName, String description, Material icon,
                ChatColor color, PotionEffectType effectType, int amplifier) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.icon = icon;
        this.color = color;
        this.effectType = effectType;
        this.amplifier = amplifier;
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public Material getIcon() { return icon; }
    public ChatColor getColor() { return color; }
    public PotionEffectType getEffectType() { return effectType; }
    public int getAmplifier() { return amplifier; }

    public static AbilityType fromId(String id) {
        if (id == null) return null;
        for (AbilityType t : values()) {
            if (t.id.equalsIgnoreCase(id)) return t;
        }
        return null;
    }
}
