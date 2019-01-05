package com.earth2me.essentials.protect;

import com.earth2me.essentials.metrics.Metrics;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;


public class EssentialsProtect extends JavaPlugin implements IProtect {
    private final Map<ProtectConfig, Boolean> settingsBoolean = new EnumMap<>(ProtectConfig.class);
    private final Map<ProtectConfig, String> settingsString = new EnumMap<>(ProtectConfig.class);
    private final Map<ProtectConfig, List<Material>> settingsList = new EnumMap<>(ProtectConfig.class);
    private EssentialsConnect ess = null;
    private transient Metrics metrics = null;

    private final EmergencyListener emListener = new EmergencyListener(this);

    @Override
    public void onEnable() {
        final PluginManager pm = this.getServer().getPluginManager();
        final Plugin essPlugin = pm.getPlugin("Essentials");
        if (essPlugin == null || !essPlugin.isEnabled()) {
            enableEmergencyMode(pm);
            return;
        }
        
        initialize(pm, essPlugin);

        if (metrics == null) {
            metrics = new Metrics(this);
        }
    }

    private void initialize(final PluginManager pm, final Plugin essPlugin) {
        getLogger().log(Level.INFO, "Continuing to enable Protect.");
        ess = new EssentialsConnect(essPlugin, this);

        final EssentialsProtectBlockListener blockListener = new EssentialsProtectBlockListener(this);
        pm.registerEvents(blockListener, this);

        final EssentialsProtectEntityListener entityListener = new EssentialsProtectEntityListener(this);
        pm.registerEvents(entityListener, this);

        final EssentialsProtectWeatherListener weatherListener = new EssentialsProtectWeatherListener(this);
        pm.registerEvents(weatherListener, this);
    }

    private void enableEmergencyMode(final PluginManager pm) {
        pm.registerEvents(emListener, this);

        for (Player player : getServer().getOnlinePlayers()) {
            player.sendMessage("Essentials Protect is in emergency mode. Check your log for errors.");
        }
        getLogger().log(Level.SEVERE, "Essentials not installed or failed to load. Essentials Protect is in emergency mode now.");
    }

    void disableEmergencyMode() {
        final PluginManager pm = this.getServer().getPluginManager();
        final Plugin essPlugin = pm.getPlugin("Essentials");
        if (essPlugin == null || !essPlugin.isEnabled()) {
            getLogger().log(Level.SEVERE, "Tried to disable emergency mode, but Essentials still isn't enabled!");
            return;
        }

        HandlerList.unregisterAll(emListener);

        for (Player player : getServer().getOnlinePlayers()) {
            player.sendMessage("Essentials Protect is no longer in emergency mode.");
        }
        getLogger().log(Level.SEVERE, "Essentials was loaded late! Essentials Protect is no longer in emergency mode.");

        initialize(pm, essPlugin);
    }

    @Override
    public EssentialsConnect getEssentialsConnect() {
        return ess;
    }

    @Override
    public Map<ProtectConfig, Boolean> getSettingsBoolean() {
        return settingsBoolean;
    }

    @Override
    public Map<ProtectConfig, String> getSettingsString() {
        return settingsString;
    }

    @Override
    public Map<ProtectConfig, List<Material>> getSettingsList() {
        return settingsList;
    }

    @Override
    public boolean getSettingBool(final ProtectConfig protectConfig) {
        final Boolean bool = settingsBoolean.get(protectConfig);
        return bool == null ? protectConfig.getDefaultValueBoolean() : bool;
    }

    @Override
    public String getSettingString(final ProtectConfig protectConfig) {
        final String str = settingsString.get(protectConfig);
        return str == null ? protectConfig.getDefaultValueString() : str;
    }
}
