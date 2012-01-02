package com.earth2me.essentials.protect;

import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.settings.protect.Protect;
import com.earth2me.essentials.storage.AsyncStorageObjectHolder;
import java.io.File;
import java.io.IOException;


public class ProtectHolder extends AsyncStorageObjectHolder<Protect>
{
	public ProtectHolder(IEssentials ess)
	{
		super(ess, Protect.class);
	}

	@Override
	public File getStorageFile() throws IOException
	{
		return new File(ess.getDataFolder(), "protect.yml");
	}
}
