package com.earth2me.essentials.update;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public abstract class WorkListener
{
	public WorkListener(final Plugin plugin, final VersionInfo newVersionInfo)
	{
		this.plugin = plugin;
		this.newVersionInfo = newVersionInfo;
	}
	private final transient Plugin plugin;
	private final transient VersionInfo newVersionInfo;

	public final void onWorkAbort() {
		onWorkAbort(null);
	}
	
	public abstract void onWorkAbort(String message);
	
	public final void onWorkDone() {
		onWorkDone(null);
	}

	public abstract void onWorkDone(String message);

	public VersionInfo getNewVersionInfo()
	{
		return newVersionInfo;
	}

	public Plugin getPlugin()
	{
		return plugin;
	}
}
