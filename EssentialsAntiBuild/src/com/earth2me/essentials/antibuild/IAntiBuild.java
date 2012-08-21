package com.earth2me.essentials.antibuild;

import java.util.List;
import java.util.Map;
import org.bukkit.plugin.Plugin;


public interface IAntiBuild extends Plugin
{
	boolean checkProtectionItems(final AntiBuildConfig list, final int id);

	boolean getSettingBool(final AntiBuildConfig protectConfig);

	EssentialsConnect getEssentialsConnect();

	Map<AntiBuildConfig, Boolean> getSettingsBoolean();

	Map<AntiBuildConfig, List<Integer>> getSettingsList();
}
