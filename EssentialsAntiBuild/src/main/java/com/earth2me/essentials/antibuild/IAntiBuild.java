package com.earth2me.essentials.antibuild;

import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;

public interface IAntiBuild extends Plugin {
    boolean checkProtectionItems(final AntiBuildConfig list, final Material mat);

    boolean getSettingBool(final AntiBuildConfig protectConfig);

    EssentialsConnect getEssentialsConnect();

    Map<AntiBuildConfig, Boolean> getSettingsBoolean();

    Map<AntiBuildConfig, List<Material>> getSettingsList();
}
