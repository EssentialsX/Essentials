package com.earth2me.essentials;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


public class ItemDb
{
	private final static Logger logger = Logger.getLogger("Minecraft");
	private static Map<String, Integer> items = new HashMap<String, Integer>();
	private static Map<String, Short> durabilities = new HashMap<String, Short>();

	public static void load(File folder, String fname) throws IOException
	{
		folder.mkdirs();
		File file = new File(folder, fname);

		if (!file.exists())
		{
			file.createNewFile();
			InputStream res = ItemDb.class.getResourceAsStream("/items.csv");
			FileWriter tx = new FileWriter(file);
			try
			{
				for (int i = 0; (i = res.read()) > 0;)
				{
					tx.write(i);
				}
			}
			finally
			{
				try
				{
					tx.flush();
					tx.close();
					res.close();
				}
				catch (Exception ex)
				{
				}
			}
		}

		BufferedReader rx = new BufferedReader(new FileReader(file));
		try
		{
			items.clear();

			for (int i = 0; rx.ready(); i++)
			{
				try
				{
					String line = rx.readLine().trim().toLowerCase();
					if (line.startsWith("#"))
					{
						continue;
					}

					String[] parts = line.split("[^a-z0-9]");
					if (parts.length < 2)
					{
						continue;
					}

					int numeric = Integer.parseInt(parts[1]);

					durabilities.put(parts[0].toLowerCase(), parts.length > 2 && !parts[2].equals("0") ? Short.parseShort(parts[2]) : 0);
					items.put(parts[0].toLowerCase(), numeric);
				}
				catch (Exception ex)
				{
					logger.warning(Util.format("parseError", fname, i));
				}
			}
		}
		finally
		{
			rx.close();
		}
	}

	public static ItemStack get(String id, int quantity) throws Exception
	{
		ItemStack retval = get(id.toLowerCase());
		retval.setAmount(quantity);
		return retval;
	}

	public static ItemStack get(String id) throws Exception
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

		Material mat = Material.getMaterial(itemid);
		if (mat == null)
		{
			throw new Exception(Util.format("unknownItemId", itemid));
		}
		ItemStack retval = new ItemStack(mat);
		retval.setAmount(Essentials.getStatic().getSettings().getDefaultStackSize());
		retval.setDurability(metaData);
		return retval;
	}
}
