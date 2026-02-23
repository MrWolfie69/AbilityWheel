package com.abilitywheel.data;

import com.abilitywheel.AbilityWheel;
import com.abilitywheel.ability.AbilityType;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Saves each player's ability to:
 * plugins/AbilityWheel/players/<uuid>.yml
 */
public class PlayerDataManager {

    private final AbilityWheel plugin;
    private final File playersDir;

    // In-memory cache: uuid -> ability id
    private final Map<String, String> cache = new ConcurrentHashMap<>();
    // Track which players have selected (even if ability is "none")
    private final Set<String> registered = ConcurrentHashMap.newKeySet();

    public PlayerDataManager(AbilityWheel plugin) {
        this.plugin = plugin;
        this.playersDir = new File(plugin.getDataFolder(), "players");
        if (!playersDir.exists()) playersDir.mkdirs();
        loadAll();
    }

    private void loadAll() {
        File[] files = playersDir.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;
        for (File f : files) {
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(f);
            String uuid = f.getName().replace(".yml", "");
            String ability = cfg.getString("ability", "none");
            registered.add(uuid);
            if (!ability.equals("none")) {
                cache.put(uuid, ability);
            }
        }
        plugin.getLogger().info("Loaded " + registered.size() + " player records.");
    }

    public void saveAll() {
        for (String uuid : registered) {
            savePlayer(uuid);
        }
    }

    private void savePlayer(String uuid) {
        File f = new File(playersDir, uuid + ".yml");
        YamlConfiguration cfg = new YamlConfiguration();
        cfg.set("uuid", uuid);
        cfg.set("ability", cache.getOrDefault(uuid, "none"));
        try { cfg.save(f); } catch (IOException e) {
            plugin.getLogger().warning("Could not save data for " + uuid + ": " + e.getMessage());
        }
    }

    /** Returns true if this player has been registered (joined before) */
    public boolean isRegistered(String uuid) {
        return registered.contains(uuid);
    }

    /** Returns true if player has a selected ability */
    public boolean hasAbility(String uuid) {
        return cache.containsKey(uuid);
    }

    public AbilityType getAbility(String uuid) {
        return AbilityType.fromId(cache.get(uuid));
    }

    public void registerPlayer(String uuid) {
        registered.add(uuid);
        savePlayer(uuid);
    }

    public void setAbility(String uuid, AbilityType ability) {
        registered.add(uuid);
        if (ability == null) {
            cache.remove(uuid);
        } else {
            cache.put(uuid, ability.getId());
        }
        savePlayer(uuid);
    }

    public void removeAbility(String uuid) {
        cache.remove(uuid);
        savePlayer(uuid);
    }

    /** Get all registered players as uuid -> ability map */
    public Map<String, AbilityType> getAllAbilities() {
        Map<String, AbilityType> result = new LinkedHashMap<>();
        for (String uuid : registered) {
            result.put(uuid, getAbility(uuid));
        }
        return result;
    }
}
