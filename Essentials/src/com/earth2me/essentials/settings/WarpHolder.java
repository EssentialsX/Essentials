package com.earth2me.essentials.settings;

import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.api.IWarp;
import com.earth2me.essentials.api.InvalidNameException;
import com.earth2me.essentials.storage.AsyncStorageObjectHolder;
import java.io.File;
import java.io.IOException;


public class WarpHolder extends AsyncStorageObjectHolder<Warp> implements IWarp
{
	private final String name;

	public WarpHolder(String name, IEssentials ess)
	{
		super(ess, Warp.class);
		this.name = name;
		onReload();
	}

	@Override
	public File getStorageFile() throws IOException
	{
		try
		{
			return ess.getWarps().getWarpFile(name);
		}
		catch (InvalidNameException ex)
		{
			throw new IOException(ex.getMessage(), ex);
		}
	}
	
}
