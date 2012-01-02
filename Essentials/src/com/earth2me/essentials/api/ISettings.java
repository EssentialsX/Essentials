package com.earth2me.essentials.api;

import com.earth2me.essentials.settings.Settings;
import com.earth2me.essentials.storage.IStorageObjectHolder;


public interface ISettings extends IStorageObjectHolder<Settings>
{
	public String getLocale();

	public boolean isDebug();

	public void setDebug(boolean b);
}
