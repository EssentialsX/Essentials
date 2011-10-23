package com.earth2me.essentials.update;

import org.bukkit.plugin.Plugin;


public class UpdatesDownloader extends WorkListener
{
	public UpdatesDownloader(final Plugin plugin, final VersionInfo newVersionInfo)
	{
		super(plugin, newVersionInfo);
	}

	public void start()
	{
	}

	@Override
	public void onWorkAbort(String message)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void onWorkDone(String message)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
