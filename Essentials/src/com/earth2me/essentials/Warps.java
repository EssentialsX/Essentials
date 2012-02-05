package com.earth2me.essentials;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.api.IWarp;
import com.earth2me.essentials.api.IWarps;
import com.earth2me.essentials.api.InvalidNameException;
import com.earth2me.essentials.commands.WarpNotFoundException;
import com.earth2me.essentials.settings.WarpHolder;
import com.earth2me.essentials.storage.StorageObjectMap;
import java.io.File;
import java.util.*;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;


public class Warps extends StorageObjectMap<IWarp> implements IWarps
{
	private static final Logger logger = Bukkit.getLogger();

	public Warps(IEssentials ess)
	{
		super(ess, "warps");
	}

	@Override
	public boolean isEmpty()
	{
		return getKeySize() == 0;
	}

	@Override
	public Collection<String> getList()
	{
		final List<String> names = new ArrayList<String>();
		for (String key : getAllKeys())
		{
			IWarp warp = getObject(key);
			if (warp == null)
			{
				continue;
			}
			warp.acquireReadLock();
			try
			{
				names.add(warp.getData().getName());
			}
			finally
			{
				warp.unlock();
			}
		}
		Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
		return names;
	}

	@Override
	public Location getWarp(final String name) throws Exception
	{
		IWarp warp = getObject(name);
		if (warp == null)
		{
			throw new WarpNotFoundException(_("warpNotExist"));
		}
		warp.acquireReadLock();
		try
		{
			return warp.getData().getLocation().getBukkitLocation();
		}
		finally
		{
			warp.unlock();
		}
	}

	@Override
	public void setWarp(final String name, final Location loc) throws Exception
	{
		setWarp(name, new com.earth2me.essentials.storage.Location(loc));
	}
	
	public void setWarp(final String name, final com.earth2me.essentials.storage.Location loc) throws Exception
	{
		IWarp warp = getObject(name);
		if (warp == null)
		{
			warp = new WarpHolder(name, ess);
		}
		warp.acquireWriteLock();
		try
		{
			warp.getData().setLocation(loc);
		}
		finally
		{
			warp.unlock();
		}
	}

	@Override
	public void removeWarp(final String name) throws Exception
	{
		removeObject(name);
	}

	@Override
	public File getWarpFile(String name) throws InvalidNameException
	{
		return getStorageFile(name);
	}

	@Override
	public IWarp load(String name) throws Exception
	{
		final IWarp warp = new WarpHolder(name, ess);
		warp.onReload();
		return warp;
	}


	private static class StringIgnoreCase
	{
		private final String string;

		public StringIgnoreCase(String string)
		{
			this.string = string;
		}

		@Override
		public int hashCode()
		{
			return getString().toLowerCase(Locale.ENGLISH).hashCode();
		}

		@Override
		public boolean equals(Object o)
		{
			if (o instanceof StringIgnoreCase)
			{
				return getString().equalsIgnoreCase(((StringIgnoreCase)o).getString());
			}
			return false;
		}

		public String getString()
		{
			return string;
		}
	}
}
