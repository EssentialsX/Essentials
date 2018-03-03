package com.neximation.essentials.protect;

import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;


public interface IProtect extends Plugin {
    boolean getSettingBool(final ProtectConfig protectConfig);

    String getSettingString(final ProtectConfig protectConfig);

    EssentialsConnect getEssentialsConnect();

    Map<ProtectConfig, Boolean> getSettingsBoolean();

    Map<ProtectConfig, String> getSettingsString();

    Map<ProtectConfig, List<Integer>> getSettingsList();
}
