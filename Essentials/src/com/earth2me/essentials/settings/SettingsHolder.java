package com.earth2me.essentials.settings;

import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.api.ISettings;
import com.earth2me.essentials.storage.AsyncStorageObjectHolder;
import java.io.File;


public class SettingsHolder extends AsyncStorageObjectHolder<Settings> implements ISettings
{
	public SettingsHolder(final IEssentials ess)
	{
		super(ess, Settings.class);
	}

	@Override
	public File getStorageFile()
	{
		return new File(ess.getDataFolder(), "settings.yml");
	}
}
