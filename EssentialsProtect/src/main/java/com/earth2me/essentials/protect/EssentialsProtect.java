package com.earth2me.essentials.protect;

import com.earth2me.essentials.EssentialsLogger;
import com.earth2me.essentials.metrics.MetricsWrapper;
import com.earth2me.essentials.utils.VersionUtil;
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
    private final EmergencyListener emListener = new EmergencyListener(this);
    private EssentialsConnect ess = null;
    private transient MetricsWrapper metrics = null;

    @Override
    public void onEnable() {
        EssentialsLogger.updatePluginLogger(this);
        final PluginManager pm = this.getServer().getPluginManager();
        final Plugin essPlugin = pm.getPlugin("Essentials");
        if (essPlugin == null || !essPlugin.isEnabled()) {
            enableEmergencyMode(pm);
            return;
        }

        initialize(pm, essPlugin);

        if (metrics == null) {
            metrics = new MetricsWrapper(this, 3816, false);
        }
    }

    private void initialize(final PluginManager pm, final Plugin essPlugin) {
        getLogger().log(Level.INFO, "Continuing to enable Protect.");
        ess = new EssentialsConnect(essPlugin, this);

        final EssentialsProtectBlockListener blockListener = new EssentialsProtectBlockListener(this);
        pm.registerEvents(blockListener, this);

        final EssentialsProtectEntityListener entityListener = new EssentialsProtectEntityListener(this);
        pm.registerEvents(entityListener, this);

        if (VersionUtil.getServerBukkitVersion().isHigherThan(VersionUtil.v1_13_2_R01)) {
            final EssentialsProtectEntityListener_1_13_2_R1 entityListener_1_13_2_r1 = new EssentialsProtectEntityListener_1_13_2_R1(this);
            pm.registerEvents(entityListener_1_13_2_r1, this);
        }
        if (VersionUtil.getServerBukkitVersion().isHigherThan(VersionUtil.v1_14_R01)) {
            final EssentialsProtectEntityListener_1_14_R1 entityListener_1_14_r1 = new EssentialsProtectEntityListener_1_14_R1(this);
            pm.registerEvents(entityListener_1_14_r1, this);
        }

        if (VersionUtil.getServerBukkitVersion().isHigherThanOrEqualTo(VersionUtil.v1_16_1_R01)) {
            final EssentialsProtectBlockListener_1_16_R1 blockListener_1_16_r1 = new EssentialsProtectBlockListener_1_16_R1(this);
            pm.registerEvents(blockListener_1_16_r1, this);
        }

        final EssentialsProtectWeatherListener weatherListener = new EssentialsProtectWeatherListener(this);
        pm.registerEvents(weatherListener, this);
    }

    private void enableEmergencyMode(final PluginManager pm) {
        pm.registerEvents(emListener, this);

        for (final Player player : getServer().getOnlinePlayers()) {
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

        for (final Player player : getServer().getOnlinePlayers()) {
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
