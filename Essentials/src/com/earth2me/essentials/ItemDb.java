package com.earth2me.essentials;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


public class ItemDb implements IConf
{
	private final transient IEssentials ess;

	public ItemDb(IEssentials ess)
	{
		this.ess = ess;
	}
	private final static Logger LOGGER = Logger.getLogger("Minecraft");
	private final transient Map<String, Integer> items = new HashMap<String, Integer>();
	private final transient Map<String, Short> durabilities = new HashMap<String, Short>();

	public void reloadConfig()
	{
		final File file = new File(ess.getDataFolder(), "items.csv");

		if (!file.exists())
		{
			final InputStream res = ItemDb.class.getResourceAsStream("/items.csv");
			FileWriter tx = null;
			try
			{
				tx = new FileWriter(file);
				for (int i = 0; (i = res.read()) > 0;)
				{
					tx.write(i);
				}
				tx.flush();
			}
			catch (IOException ex)
			{
				LOGGER.log(Level.SEVERE, Util.i18n("itemsCsvNotLoaded"), ex);
				return;
			}
			finally
			{
				try
				{
					res.close();
				}
				catch (Exception ex)
				{
				}
				try
				{
					if (tx != null)
					{
						tx.close();
					}
				}
				catch (Exception ex)
				{
				}
			}
		}

		BufferedReader rx = null;
		try
		{
			rx = new BufferedReader(new FileReader(file));
			durabilities.clear();
			items.clear();

			for (int i = 0; rx.ready(); i++)
			{
				try
				{
					final String line = rx.readLine().trim().toLowerCase();
					if (line.startsWith("#"))
					{
						continue;
					}

					final String[] parts = line.split("[^a-z0-9]");
					if (parts.length < 2)
					{
						continue;
					}

					final int numeric = Integer.parseInt(parts[1]);

					durabilities.put(parts[0].toLowerCase(), parts.length > 2 && !parts[2].equals("0") ? Short.parseShort(parts[2]) : 0);
					items.put(parts[0].toLowerCase(), numeric);
				}
				catch (Exception ex)
				{
					LOGGER.warning(Util.format("parseError", "items.csv", i));
				}
			}
		}
		catch (IOException ex)
		{
			LOGGER.log(Level.SEVERE, Util.i18n("itemsCsvNotLoaded"), ex);
		}
		finally
		{
			if (rx != null) {
				try
				{
					rx.close();
				}
				catch (IOException ex)
				{
					LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
				}
			}
		}
	}

	public ItemStack get(final String id, final int quantity) throws Exception
	{
		final ItemStack retval = get(id.toLowerCase());
		retval.setAmount(quantity);
		return retval;
	}

	public ItemStack get(final String id) throws Exception
	{
		int itemid = 0;
		String itemname = null;
		short metaData = 0;
		if (id.matches("^\\d+[:+',;.]\\d+$"))
		{
			itemid = Integer.parseInt(id.split("[:+',;.]")[0]);
			metaData = Short.parseShort(id.split("[:+',;.]")[1]);
		}
		else if (id.matches("^\\d+$"))
		{
			itemid = Integer.parseInt(id);
		}
		else if (id.matches("^[^:+',;.]+[:+',;.]\\d+$"))
		{
			itemname = id.split("[:+',;.]")[0].toLowerCase();
			metaData = Short.parseShort(id.split("[:+',;.]")[1]);
		}
		else
		{
			itemname = id.toLowerCase();
		}

		if (itemname != null)
		{
			if (items.containsKey(itemname))
			{
				itemid = items.get(itemname);
				if (durabilities.containsKey(itemname) && metaData == 0)
				{
					metaData = durabilities.get(itemname);
				}
			}
			else
			{
				throw new Exception(Util.format("unknownItemName", id));
			}
		}

		final Material mat = Material.getMaterial(itemid);
		if (mat == null)
		{
			throw new Exception(Util.format("unknownItemId", itemid));
		}
		final ItemStack retval = new ItemStack(mat);
		retval.setAmount(ess.getSettings().getDefaultStackSize());
		retval.setDurability(metaData);
		return retval;
	}
}
