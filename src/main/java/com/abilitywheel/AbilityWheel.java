package com.abilitywheel;

import com.abilitywheel.commands.AbilityCommand;
import com.abilitywheel.data.PlayerDataManager;
import com.abilitywheel.gui.WheelListener;
import com.abilitywheel.ability.AbilityManager;
import org.bukkit.plugin.java.JavaPlugin;

public class AbilityWheel extends JavaPlugin {

    private static AbilityWheel instance;
    private PlayerDataManager dataManager;
    private AbilityManager abilityManager;

    @Override
    public void onEnable() {
        instance = this;

        getLogger().info("╔══════════════════════════╗");
        getLogger().info("║   Ability Wheel Loading  ║");
        getLogger().info("╚══════════════════════════╝");

        // Init managers
        this.dataManager = new PlayerDataManager(this);
        this.abilityManager = new AbilityManager(this);

        // Register events
        getServer().getPluginManager().registerEvents(new WheelListener(this), this);

        // Register commands
        AbilityCommand cmd = new AbilityCommand(this);
        getCommand("ability").setExecutor(cmd);
        getCommand("ability").setTabCompleter(cmd);

        // Start passive effect task (every 3 seconds = 60 ticks)
        getServer().getScheduler().runTaskTimer(this, () -> {
            getServer().getOnlinePlayers().forEach(p -> abilityManager.refreshEffect(p));
        }, 60L, 60L);

        getLogger().info("Ability Wheel enabled! 12 abilities ready.");
    }

    @Override
    public void onDisable() {
        if (dataManager != null) dataManager.saveAll();
        getLogger().info("Ability Wheel disabled. All data saved.");
    }

    public static AbilityWheel getInstance() { return instance; }
    public PlayerDataManager getDataManager() { return dataManager; }
    public AbilityManager getAbilityManager() { return abilityManager; }
}
