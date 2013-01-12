package com.earth2me.essentials;

import static com.earth2me.essentials.I18n._;
import java.util.Locale;
import java.util.regex.Pattern;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;


public class MetaItemStack extends ItemStack
{
	private final transient Pattern splitPattern = Pattern.compile("[:+',;.]");
	private final ItemStack stack;

	public MetaItemStack(final ItemStack stack)
	{
		this.stack = stack;
	}
	
	public ItemStack getBase()
	{
		return stack;
	}

	public void addStringMeta(final User user, final boolean allowUnsafe, final String string) throws Exception
	{

	}

	public void addStringEnchantment(final User user, final boolean allowUnsafe, final String string) throws Exception
	{
		final String[] split = splitPattern.split(string, 2);
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
