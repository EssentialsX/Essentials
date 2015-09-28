package com.earth2me.essentials.antibuild;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;


public class EssentialsAntiBuild extends JavaPlugin implements IAntiBuild {
    private final transient Map<AntiBuildConfig, Boolean> settingsBoolean = new EnumMap<>(AntiBuildConfig.class);
    private final transient Map<AntiBuildConfig, List<Integer>> settingsList = new EnumMap<>(AntiBuildConfig.class);
    private transient EssentialsConnect ess;

    @Override
    public void onEnable() {
        final PluginManager pm = this.getServer().getPluginManager();
        final Plugin essPlugin = pm.getPlugin("Essentials");
        if (essPlugin == null || !essPlugin.isEnabled()) {
            return;
        }
        ess = new EssentialsConnect(essPlugin, this);

        final EssentialsAntiBuildListener blockListener = new EssentialsAntiBuildListener(this);
        pm.registerEvents(blockListener, this);
    }

    @Override
    public boolean checkProtectionItems(final AntiBuildConfig list, final int id) {
        final List<Integer> itemList = settingsList.get(list);
        return itemList != null && !itemList.isEmpty() && itemList.contains(id);
    }

    @Override
    public EssentialsConnect getEssentialsConnect() {
        return ess;
    }

    @Override
    public Map<AntiBuildConfig, Boolean> getSettingsBoolean() {
        return settingsBoolean;
    }

    @Override
    public Map<AntiBuildConfig, List<Integer>> getSettingsList() {
        return settingsList;
    }

    @Override
    public boolean getSettingBool(final AntiBuildConfig protectConfig) {
        final Boolean bool = settingsBoolean.get(protectConfig);
        return bool == null ? protectConfig.getDefaultValueBoolean() : bool;
    }
}
