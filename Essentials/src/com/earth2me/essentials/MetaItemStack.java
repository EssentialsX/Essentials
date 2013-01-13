package com.earth2me.essentials;

import static com.earth2me.essentials.I18n._;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;


public class MetaItemStack
{
	private final transient Pattern splitPattern = Pattern.compile("[:+',;.]");
	private final ItemStack stack;

	public MetaItemStack(final ItemStack stack)
	{
		this.stack = stack.clone();
	}

	public ItemStack getItemStack()
	{
		return stack;
	}

	//TODO: TL this
	public void addStringMeta(final User user, final boolean allowUnsafe, final String string) throws Exception
	{
		final String[] split = splitPattern.split(string, 2);
		if (split.length < 1)
		{
			return;
		}

		if (split.length > 1 && split[0].equalsIgnoreCase("name"))
		{
			final String displayName = split[1].replace('_', ' ');
			final ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName(displayName);
			stack.setItemMeta(meta);
		}
		else if (split.length > 1 && (split[0].equalsIgnoreCase("lore") || split[0].equalsIgnoreCase("desc")))
		{
			final List<String> lore = new ArrayList<String>();
			for (String line : split[1].split("\\|"))
			{
				lore.add(line.replace('_', ' '));
			}
			final ItemMeta meta = stack.getItemMeta();
			meta.setLore(lore);
			stack.setItemMeta(meta);
		}
		else if (split.length > 1 && (split[0].equalsIgnoreCase("player") || split[0].equalsIgnoreCase("owner")) && stack.getType() == Material.SKULL_ITEM)
		{
			if (stack.getDurability() == 3)
			{
				final String owner = split[1];
				final SkullMeta meta = (SkullMeta)stack.getItemMeta();
				boolean result = meta.setOwner(owner);
				stack.setItemMeta(meta);
			}
			else
			{
				throw new Exception("You can only set the owner of player skulls (397:3)");
			}
		}
		else if (split.length > 1 && (split[0].equalsIgnoreCase("color") || split[0].equalsIgnoreCase("colour"))
				 && (stack.getType() == Material.LEATHER_BOOTS
					 || stack.getType() == Material.LEATHER_CHESTPLATE
					 || stack.getType() == Material.LEATHER_HELMET
					 || stack.getType() == Material.LEATHER_LEGGINGS))
		{
			final String[] color = split[1].split("\\|");
			if (color.length == 3)
			{
				final int red = Util.isInt(color[0]) ? Integer.parseInt(color[0]) : 0;
				final int green = Util.isInt(color[1]) ? Integer.parseInt(color[1]) : 0;
				final int blue = Util.isInt(color[2]) ? Integer.parseInt(color[2]) : 0;
				final LeatherArmorMeta meta = (LeatherArmorMeta)stack.getItemMeta();
				meta.setColor(Color.fromRGB(red, green, blue));
				stack.setItemMeta(meta);
			}
			else
			{
				throw new Exception("Leather Color Syntax: color:<red>|<green>|<blue> eg: color:255|0|0");
			}
		}
		else
		{
			parseEnchantmentStrings(user, allowUnsafe, split);
		}
	}

	public void addStringEnchantment(final User user, final boolean allowUnsafe, final String string) throws Exception
	{
		final String[] split = splitPattern.split(string, 2);
		if (split.length < 1)
		{
			return;
		}

		parseEnchantmentStrings(user, allowUnsafe, split);
	}

	private void parseEnchantmentStrings(final User user, final boolean allowUnsafe, final String[] split) throws Exception
	{
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
		addEnchantment(user, allowUnsafe, enchantment, level);
	}

	public void addEnchantment(final User user, final boolean allowUnsafe, final Enchantment enchantment, final int level) throws Exception
	{
		try
		{
			if (stack.getType().equals(Material.ENCHANTED_BOOK))
			{
				EnchantmentStorageMeta meta = (EnchantmentStorageMeta)stack.getItemMeta();
				if (level == 0)
				{
					meta.removeStoredEnchant(enchantment);
				}
				else
				{
					meta.addStoredEnchant(enchantment, level, allowUnsafe);
				}
				stack.setItemMeta(meta);
			}
			else // all other material types besides ENCHANTED_BOOK
			{
				if (level == 0)
				{
					stack.removeEnchantment(enchantment);
				}
				else
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
			}
		}
		catch (Exception ex)
		{
			throw new Exception("Enchantment " + enchantment.getName() + ": " + ex.getMessage(), ex);
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
}
