package com.earth2me.essentials;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.commands.WarpNotFoundException;
import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Server;


public class Warps implements IConf
{
	private static final Logger logger = Logger.getLogger("Minecraft");
	private final Map<StringIgnoreCase, EssentialsConf> warpPoints = new HashMap<StringIgnoreCase, EssentialsConf>();
	private final File warpsFolder;
	private final Server server;

	public Warps(Server server, File dataFolder)
	{
		this.server = server;
		warpsFolder = new File(dataFolder, "warps");
		if (!warpsFolder.exists())
		{
			warpsFolder.mkdirs();
		}
		reloadConfig();
	}

	public boolean isEmpty()
	{
		return warpPoints.isEmpty();
	}

	public Collection<String> getWarpNames()
	{
		final List<String> keys = new ArrayList<String>();
		for (StringIgnoreCase stringIgnoreCase : warpPoints.keySet())
		{
			keys.add(stringIgnoreCase.getString());
		}
		Collections.sort(keys, String.CASE_INSENSITIVE_ORDER);
		return keys;
	}

	public Location getWarp(String warp) throws Exception
	{
		EssentialsConf conf = warpPoints.get(new StringIgnoreCase(warp));
		if (conf == null)
		{
			throw new WarpNotFoundException();
		}
		return conf.getLocation(null, server);
	}

	public void setWarp(String name, Location loc) throws Exception
	{
		String filename = Util.sanitizeFileName(name);
		EssentialsConf conf = warpPoints.get(new StringIgnoreCase(name));
		if (conf == null)
		{
			File confFile = new File(warpsFolder, filename + ".yml");
			if (confFile.exists())
			{
				throw new Exception(_("similarWarpExist"));
			}
			conf = new EssentialsConf(confFile);
			warpPoints.put(new StringIgnoreCase(name), conf);
		}
		conf.setProperty(null, loc);
		conf.setProperty("name", name);
		conf.save();
	}

	public void delWarp(String name) throws Exception
	{
		EssentialsConf conf = warpPoints.get(new StringIgnoreCase(name));
		if (conf == null)
		{
			throw new Exception(_("warpNotExist"));
		}
		if (!conf.getFile().delete())
		{
			throw new Exception(_("warpDeleteError"));
		}
		warpPoints.remove(new StringIgnoreCase(name));
	}

	@Override
	public final void reloadConfig()
	{
		warpPoints.clear();
		File[] listOfFiles = warpsFolder.listFiles();
		if (listOfFiles.length >= 1)
		{
			for (int i = 0; i < listOfFiles.length; i++)
			{
				String filename = listOfFiles[i].getName();
				if (listOfFiles[i].isFile() && filename.endsWith(".yml"))
				{
					try
					{
						EssentialsConf conf = new EssentialsConf(listOfFiles[i]);
						conf.load();
						String name = conf.getString("name");
						if (name != null)
						{
							warpPoints.put(new StringIgnoreCase(name), conf);
						}
					}
					catch (Exception ex)
					{
						logger.log(Level.WARNING, _("loadWarpError", filename), ex);
					}
				}
			}
		}
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
