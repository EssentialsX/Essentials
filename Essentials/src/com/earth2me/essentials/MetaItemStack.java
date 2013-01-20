package com.earth2me.essentials;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.textreader.*;
import java.util.*;
import java.util.regex.Pattern;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;


public class MetaItemStack
{
	private final transient Pattern splitPattern = Pattern.compile("[:+',;.]");
	private final ItemStack stack;
	private final static Map<String, DyeColor> colorMap = new HashMap<String, DyeColor>();
	private final static Map<String, FireworkEffect.Type> fireworkShape = new HashMap<String, FireworkEffect.Type>();
	private FireworkEffect.Builder builder = FireworkEffect.builder();
	private boolean validFirework = false;

	static
	{
		for (DyeColor color : DyeColor.values())
		{
			colorMap.put(color.name(), color);
		}
		for (FireworkEffect.Type type : FireworkEffect.Type.values())
		{
			fireworkShape.put(type.name(), type);
		}
	}

	public MetaItemStack(final ItemStack stack)
	{
		this.stack = stack.clone();
	}

	public ItemStack getItemStack()
	{
		return stack;
	}

	public boolean isValidFirework()
	{
		return validFirework;
	}

	public FireworkEffect.Builder getFireworkBuilder()
	{
		return builder;
	}

	public void parseStringMeta(final CommandSender user, final boolean allowUnsafe, String[] string, int fromArg, final IEssentials ess) throws Exception
	{

		for (int i = fromArg; i < string.length; i++)
		{
			addStringMeta(user, allowUnsafe, string[i], ess);
		}
		if (validFirework)
		{
			FireworkEffect effect = builder.build();
			FireworkMeta fmeta = (FireworkMeta)stack.getItemMeta();
			fmeta.addEffect(effect);
			stack.setItemMeta(fmeta);
		}
	}

	//TODO: TL this
	private void addStringMeta(final CommandSender user, final boolean allowUnsafe, final String string, final IEssentials ess) throws Exception
	{
		final String[] split = splitPattern.split(string, 2);
		if (split.length < 1)
		{
			return;
		}

		if (split.length > 1 && split[0].equalsIgnoreCase("name"))
		{
			final String displayName = Util.replaceFormat(split[1].replace('_', ' '));
			final ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName(displayName);
			stack.setItemMeta(meta);
		}
		else if (split.length > 1 && (split[0].equalsIgnoreCase("lore") || split[0].equalsIgnoreCase("desc")))
		{
			final List<String> lore = new ArrayList<String>();
			for (String line : split[1].split("\\|"))
			{
				lore.add(Util.replaceFormat(line.replace('_', ' ')));
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
		else if (split.length > 1 && split[0].equalsIgnoreCase("book") && stack.getType() == Material.WRITTEN_BOOK)
		{
			final BookMeta meta = (BookMeta)stack.getItemMeta();
			final IText input = new BookInput("book", true, ess);
			final BookPager pager = new BookPager(input);

			List<String> pages = pager.getPages(split[1]);
			meta.setPages(pages);

			stack.setItemMeta(meta);
		}
		else if (split.length > 1 && split[0].equalsIgnoreCase("author") && stack.getType() == Material.WRITTEN_BOOK)
		{
			final String author = split[1];
			final BookMeta meta = (BookMeta)stack.getItemMeta();
			meta.setAuthor(author);
			stack.setItemMeta(meta);
		}
		else if (split.length > 1 && split[0].equalsIgnoreCase("title") && stack.getType() == Material.WRITTEN_BOOK)
		{
			final String title = Util.replaceFormat(split[1].replace('_', ' '));
			final BookMeta meta = (BookMeta)stack.getItemMeta();
			meta.setTitle(title);
			stack.setItemMeta(meta);
		}
		else if (stack.getType() == Material.FIREWORK) //WARNING - Meta for fireworks will be ignored after this point.
		{
			addFireworkMeta(user, false, string, ess);
		}
		else if (split.length > 1 && (split[0].equalsIgnoreCase("color") || split[0].equalsIgnoreCase("colour"))
				 && (stack.getType() == Material.LEATHER_BOOTS
					 || stack.getType() == Material.LEATHER_CHESTPLATE
					 || stack.getType() == Material.LEATHER_HELMET
					 || stack.getType() == Material.LEATHER_LEGGINGS))
		{
			final String[] color = split[1].split("(\\||,)");
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
				throw new Exception("Leather Color Syntax: color:<red>,<green>,<blue> eg: color:255,0,0");
			}
		}
		else
		{
			parseEnchantmentStrings(user, allowUnsafe, split);
		}
	}

	public void addFireworkMeta(final CommandSender user, final boolean allowShortName, final String string, final IEssentials ess)
	{
		if (stack.getType() == Material.FIREWORK)
		{
			FireworkMeta fmeta = (FireworkMeta)stack.getItemMeta();
			final String[] split = splitPattern.split(string, 2);

			if (split[0].equalsIgnoreCase("color") || split[0].equalsIgnoreCase("colour") || (allowShortName && split[0].equalsIgnoreCase("c")))
			{
				List<Color> primaryColors = new ArrayList<Color>();
				String[] colors = split[1].split(",");
				for (String color : colors)
				{
					if (colorMap.containsKey(color.toUpperCase()))
					{
						validFirework = true;
						primaryColors.add(colorMap.get(color.toUpperCase()).getFireworkColor());
					}
					else
					{
						user.sendMessage(_("invalidFireworkFormat", split[1], split[0]));
					}
				}
				builder.withColor(primaryColors);
			}
			else if (split[0].equalsIgnoreCase("shape") || split[0].equalsIgnoreCase("type") || (allowShortName && (split[0].equalsIgnoreCase("s") || split[0].equalsIgnoreCase("t"))))
			{
				FireworkEffect.Type finalEffect = null;
				split[1] = (split[1].equalsIgnoreCase("large") ? "BALL_LARGE" : split[1]);
				if (fireworkShape.containsKey(split[1].toUpperCase()))
				{
					finalEffect = fireworkShape.get(split[1].toUpperCase());
				}
				else
				{
					user.sendMessage(_("invalidFireworkFormat", split[1], split[0]));
				}
				if (finalEffect != null)
				{
					builder.with(finalEffect);
				}
			}
			else if (split[0].equalsIgnoreCase("fade") || (allowShortName && split[0].equalsIgnoreCase("f")))
			{
				List<Color> fadeColors = new ArrayList<Color>();
				String[] colors = split[1].split(",");
				for (String color : colors)
				{
					if (colorMap.containsKey(color.toUpperCase()))
					{
						fadeColors.add(colorMap.get(color.toUpperCase()).getFireworkColor());
					}
					else
					{
						user.sendMessage(_("invalidFireworkFormat", split[1], split[0]));
					}
				}
				if (!fadeColors.isEmpty())
				{
					builder.withFade(fadeColors);
				}
			}
			else if (split[0].equalsIgnoreCase("effect") || (allowShortName && split[0].equalsIgnoreCase("e")))
			{
				String[] effects = split[1].split(",");
				for (String effect : effects)
				{
					if (effect.equalsIgnoreCase("twinkle"))
					{
						builder.flicker(true);
					}
					else if (effect.equalsIgnoreCase("trail"))
					{
						builder.trail(true);
					}
					else
					{
						user.sendMessage(_("invalidFireworkFormat", split[1], split[0]));
					}
				}
			}
			else if (split[0].equalsIgnoreCase("power") || (allowShortName && split[0].equalsIgnoreCase("p")))
			{
				try
				{
					int power = Integer.parseInt(split[1]);
					fmeta.setPower(power > 3 ? 4 : power);
				}
				catch (NumberFormatException e)
				{
					user.sendMessage(_("invalidFireworkFormat", split[1], split[0]));
				}
				stack.setItemMeta(fmeta);
			}
		}
	}

	private void parseEnchantmentStrings(final CommandSender user, final boolean allowUnsafe, final String[] split) throws Exception
	{
		Enchantment enchantment = getEnchantment(null, split[0]);

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

	public void addEnchantment(final CommandSender user, final boolean allowUnsafe, final Enchantment enchantment, final int level) throws Exception
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
