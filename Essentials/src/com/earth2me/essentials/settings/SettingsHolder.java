package com.earth2me.essentials.settings;

import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.api.ISettings;
import com.earth2me.essentials.storage.AsyncStorageObjectHolder;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;


public class SettingsHolder extends AsyncStorageObjectHolder<Settings> implements ISettings
{
	private final transient AtomicBoolean debug = new AtomicBoolean(false);
	public SettingsHolder(final IEssentials ess)
	{
		super(ess, Settings.class);
		onReload();
	}

	@Override
	public final void onReload()
	{
		super.onReload();
		acquireReadLock();
		try {
			debug.set(getData().getGeneral().isDebug());
		} finally {
			unlock();
		}
	}

	@Override
	public File getStorageFile()
	{
		return new File(ess.getDataFolder(), "settings.yml");
	}

	@Override
	public String getLocale()
	{
		acquireReadLock();
		try {
			return getData().getGeneral().getLocale();
		} finally {
			unlock();
		}
	}

	@Override
	public boolean isDebug()
	{
		return debug.get();
	}
	
	public void setDebug(final boolean set)
	{
		debug.set(set);
		acquireWriteLock();
		try {
			getData().getGeneral().setDebug(set);
		} finally {
			unlock();
		}
	}
}
