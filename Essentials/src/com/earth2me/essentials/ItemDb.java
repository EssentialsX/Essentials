package com.earth2me.essentials;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.IItemDb;
import java.util.*;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
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
	private final transient Map<ItemData, List<String>> names = new HashMap<ItemData, List<String>>();
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
		names.clear();

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
			final short data = parts.length > 2 && !parts[2].equals("0") ? Short.parseShort(parts[2]) : 0;
			String itemName = parts[0].toLowerCase(Locale.ENGLISH);

			durabilities.put(itemName, data);
			items.put(itemName, numeric);

			ItemData itemData = new ItemData(numeric, data);
			if (names.containsKey(itemData))
			{
				List<String> nameList = names.get(itemData);
				nameList.add(itemName);
				Collections.sort(nameList, new LengthCompare());
			}
			else
			{
				List<String> nameList = new ArrayList<String>();
				nameList.add(itemName);
				names.put(itemData, nameList);
			}
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

	public void addStringEnchantment(final User user, final boolean allowUnsafe, final ItemStack stack, final String string) throws Exception
	{
		final String[] split = string.split("[:+',;.]", 2);
		if (split.length < 1)
		{
			return;
		}

		Enchantment enchantment = getEnchantment(user, split[0]);

		int level = -1;
		if (split.length > 1)
		{
			try
			{
				level = Integer.parseInt(split[1]);
			}
			catch (NumberFormatException ex)
			{
				level = -1;
			}
		}

		if (level < 0 || (!allowUnsafe && level > enchantment.getMaxLevel()))
		{
			level = enchantment.getMaxLevel();
		}
		addEnchantment(user, allowUnsafe, stack, enchantment, level);
	}

	public void addEnchantment(final User user, final boolean allowUnsafe, final ItemStack stack, final Enchantment enchantment, final int level) throws Exception
	{
		if (level == 0)
		{
			stack.removeEnchantment(enchantment);
		}
		else
		{
			try
			{
				if (allowUnsafe)
				{
					stack.addUnsafeEnchantment(enchantment, level);
				}
				else
				{
					stack.addEnchantment(enchantment, level);
				}
			}
			catch (Exception ex)
			{
				throw new Exception("Enchantment " + enchantment.getName() + ": " + ex.getMessage(), ex);
			}
		}
	}

	//TODO: Properly TL this
	public Enchantment getEnchantment(final User user, final String name) throws Exception
	{
		final Enchantment enchantment = Enchantments.getByName(name);
		if (enchantment == null)
		{
			throw new Exception(_("enchantmentNotFound") + ": " + name);
		}
		final String enchantmentName = enchantment.getName().toLowerCase(Locale.ENGLISH);
		if (user != null && !user.isAuthorized("essentials.enchant." + enchantmentName))
		{
			throw new Exception(_("enchantmentPerm", enchantmentName));
		}
		return enchantment;
	}

	public String names(ItemStack item)
	{
		ItemData itemData = new ItemData(item.getTypeId(), item.getDurability());
		List<String> nameList = names.get(itemData);
		if (nameList == null)
		{
			itemData = new ItemData(item.getTypeId(), (short)0);
			nameList = names.get(itemData);
			if (nameList == null)
			{
				return null;
			}
		}

		if (nameList.size() > 15)
		{
			nameList = nameList.subList(0, 14);
		}
		return Util.joinList(", ", nameList);
	}


	class ItemData
	{
		final private int itemNo;
		final private short itemData;

		ItemData(final int itemNo, final short itemData)
		{
			this.itemNo = itemNo;
			this.itemData = itemData;
		}

		public int getItemNo()
		{
			return itemNo;
		}

		public short getItemData()
		{
			return itemData;
		}

		@Override
		public int hashCode()
		{
			return (31 * itemNo) ^ itemData;
		}

		@Override
		public boolean equals(Object o)
		{
			if (o == null)
			{
				return false;
			}
			if (!(o instanceof ItemData))
			{
				return false;
			}
			ItemData pairo = (ItemData)o;
			return this.itemNo == pairo.getItemNo()
				   && this.itemData == pairo.getItemData();
		}
	}


	class LengthCompare implements java.util.Comparator<String>
	{
		public LengthCompare()
		{
			super();
		}

		@Override
		public int compare(String s1, String s2)
		{
			return s1.length() - s2.length();
		}
	}
}
