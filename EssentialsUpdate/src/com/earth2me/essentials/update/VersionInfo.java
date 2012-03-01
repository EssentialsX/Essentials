package com.earth2me.essentials.update;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.Configuration;


public class VersionInfo
{
	private final transient List<String> changelog;
	private final transient int minBukkit;
	private final transient int maxBukkit;
	private final transient Map<String, ModuleInfo> modules;

	public VersionInfo(final Configuration updateConfig, final String path)
	{
		changelog = updateConfig.getStringList(path + ".changelog");
		minBukkit = updateConfig.getInt(path + ".min-bukkit", 0);
		maxBukkit = updateConfig.getInt(path + ".max-bukkit", 0);
		modules = new HashMap<String, ModuleInfo>();
		final String modulesPath = path + ".modules";
		for (String module : updateConfig.getKeys(false))
		{
			modules.put(module, new ModuleInfo(updateConfig, modulesPath + module));
		}
	}

	public List<String> getChangelog()
	{
		return Collections.unmodifiableList(changelog);
	}

	public int getMinBukkit()
	{
		return minBukkit;
	}

	public int getMaxBukkit()
	{
		return maxBukkit;
	}

	public Map<String, ModuleInfo> getModules()
	{
		return Collections.unmodifiableMap(modules);
	}
}
