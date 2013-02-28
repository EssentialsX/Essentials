package com.earth2me.essentials.protect;

import java.util.List;
import java.util.Map;
import org.bukkit.plugin.Plugin;


public interface IProtect extends Plugin
{
	boolean getSettingBool(final ProtectConfig protectConfig);

	String getSettingString(final ProtectConfig protectConfig);
	
	EssentialsConnect getEssentialsConnect();

	Map<ProtectConfig, Boolean> getSettingsBoolean();

	Map<ProtectConfig, String> getSettingsString();

	Map<ProtectConfig, List<Integer>> getSettingsList();
}
