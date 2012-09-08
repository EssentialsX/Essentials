package com.earth2me.essentials;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.IItemDb;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


public class ItemDb implements IConf, IItemDb
{
	private final transient IEssentials ess;

	public ItemDb(final IEssentials ess)
	{
		this.ess = ess;
		file = new ManagedFile("items.csv", ess);
	}
	private final transient Map<String, Integer> items = new HashMap<String, Integer>();
	private final transient Map<String, Short> durabilities = new HashMap<String, Short>();
	private final transient ManagedFile file;

	@Override
	public void reloadConfig()
	{
		final List<String> lines = file.getLines();

		if (lines.isEmpty())
		{
			return;
		}

		durabilities.clear();
		items.clear();

		for (String line : lines)
		{
			line = line.trim().toLowerCase(Locale.ENGLISH);
			if (line.length() > 0 && line.charAt(0) == '#')
			{
				continue;
			}

			final String[] parts = line.split("[^a-z0-9]");
			if (parts.length < 2)
			{
				continue;
			}

			final int numeric = Integer.parseInt(parts[1]);

			durabilities.put(parts[0].toLowerCase(Locale.ENGLISH), parts.length > 2 && !parts[2].equals("0") ? Short.parseShort(parts[2]) : 0);
			items.put(parts[0].toLowerCase(Locale.ENGLISH), numeric);
		}
	}

	public ItemStack get(final String id, final int quantity) throws Exception
	{
		final ItemStack retval = get(id.toLowerCase(Locale.ENGLISH));
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
			itemname = id.split("[:+',;.]")[0].toLowerCase(Locale.ENGLISH);
			metaData = Short.parseShort(id.split("[:+',;.]")[1]);
		}
		else
		{
			itemname = id.toLowerCase(Locale.ENGLISH);
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
			else if (Material.getMaterial(itemname) != null)
			{
				itemid = Material.getMaterial(itemname).getId();
				metaData = 0;
			}
			else
			{
				throw new Exception(_("unknownItemName", id));
			}
		}

		final Material mat = Material.getMaterial(itemid);
		if (mat == null)
		{
			throw new Exception(_("unknownItemId", itemid));
		}
		final ItemStack retval = new ItemStack(mat);
		retval.setAmount(mat.getMaxStackSize());
		retval.setDurability(metaData);
		return retval;
	}
}
