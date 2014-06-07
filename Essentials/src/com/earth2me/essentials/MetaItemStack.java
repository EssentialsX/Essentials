package com.earth2me.essentials;

import static com.earth2me.essentials.I18n.tl;
import com.earth2me.essentials.textreader.BookInput;
import com.earth2me.essentials.textreader.BookPager;
import com.earth2me.essentials.textreader.IText;
import com.earth2me.essentials.utils.FormatUtil;
import com.earth2me.essentials.utils.NumberUtil;
import java.util.*;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;
import java.util.logging.Level;
import net.ess3.api.IEssentials;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class MetaItemStack
{
	private static final Map<String, DyeColor> colorMap = new HashMap<String, DyeColor>();
	private static final Map<String, FireworkEffect.Type> fireworkShape = new HashMap<String, FireworkEffect.Type>();

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
	private final transient Pattern splitPattern = Pattern.compile("[:+',;.]");
	private ItemStack stack;
	private FireworkEffect.Builder builder = FireworkEffect.builder();
	private PotionEffectType pEffectType;
	private PotionEffect pEffect;
	private boolean validFirework = false;
	private boolean validPotionEffect = false;
	private boolean validPotionDuration = false;
	private boolean validPotionPower = false;
	private boolean completePotion = false;
	private int power = 1;
	private int duration = 120;

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

	public boolean isValidPotion()
	{
		return validPotionEffect && validPotionDuration && validPotionPower;
	}

	public FireworkEffect.Builder getFireworkBuilder()
	{
		return builder;
	}

	public PotionEffect getPotionEffect()
	{
		return pEffect;
	}

	public boolean completePotion()
	{
		return completePotion;
	}

	private void resetPotionMeta()
	{
		pEffect = null;
		pEffectType = null;
		validPotionEffect = false;
		validPotionDuration = false;
		validPotionPower = false;
		completePotion = true;
	}

	public boolean canSpawn(final IEssentials ess)
	{
		try
		{
			ess.getServer().getUnsafe().modifyItemStack(stack, "{}");
			return true;
		}
		catch (NullPointerException npe)
		{
			if (ess.getSettings().isDebug())
			{
				ess.getLogger().log(Level.INFO, "Itemstack is invalid", npe);
			}
			return false;
		}
		catch (NoSuchMethodError nsme)
		{
			return true;
		}
		catch (Throwable throwable)
		{
			if (ess.getSettings().isDebug())
			{
				ess.getLogger().log(Level.INFO, "Itemstack is invalid", throwable);
			}
			return false;
		}
	}

	public void parseStringMeta(final CommandSource sender, final boolean allowUnsafe, String[] string, int fromArg, final IEssentials ess) throws Exception
	{
		if (string[fromArg].startsWith("{") && hasMetaPermission(sender, "vanilla", false, true, ess))
		{
			try
			{
				stack = ess.getServer().getUnsafe().modifyItemStack(stack, Joiner.on(' ').join(Arrays.asList(string).subList(fromArg, string.length)));
			}
			catch (NullPointerException npe)
			{
				if (ess.getSettings().isDebug())
				{
					ess.getLogger().log(Level.INFO, "Itemstack is invalid", npe);
				}
			}
			catch (NoSuchMethodError nsme)
			{
				throw new Exception(tl("noMetaJson"), nsme);
			}
			catch (Throwable throwable)
			{
				throw new Exception(throwable.getMessage(), throwable);
			}
		}
		else
		{
			for (int i = fromArg; i < string.length; i++)
			{
				addStringMeta(sender, allowUnsafe, string[i], ess);
			}
			if (validFirework)
			{
				if (!hasMetaPermission(sender, "firework", true, true, ess))
				{
					throw new Exception(tl("noMetaFirework"));
				}
				FireworkEffect effect = builder.build();
				FireworkMeta fmeta = (FireworkMeta)stack.getItemMeta();
				fmeta.addEffect(effect);
				if (fmeta.getEffects().size() > 1 && !hasMetaPermission(sender, "firework-multiple", true, true, ess))
				{
					throw new Exception(tl("multipleCharges"));
				}
				stack.setItemMeta(fmeta);
			}
		}
	}

	public void addStringMeta(final CommandSource sender, final boolean allowUnsafe, final String string, final IEssentials ess) throws Exception
	{
		final String[] split = splitPattern.split(string, 2);
		if (split.length < 1)
		{
			return;
		}

		if (split.length > 1 && split[0].equalsIgnoreCase("name") && hasMetaPermission(sender, "name", false, true, ess))
		{
			final String displayName = FormatUtil.replaceFormat(split[1].replace('_', ' '));
			final ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName(displayName);
			stack.setItemMeta(meta);
		}
		else if (split.length > 1 && (split[0].equalsIgnoreCase("lore") || split[0].equalsIgnoreCase("desc")) && hasMetaPermission(sender, "lore", false, true, ess))
		{
			final List<String> lore = new ArrayList<String>();
			for (String line : split[1].split("\\|"))
			{
				lore.add(FormatUtil.replaceFormat(line.replace('_', ' ')));
			}
			final ItemMeta meta = stack.getItemMeta();
			meta.setLore(lore);
			stack.setItemMeta(meta);
		}
		else if (split.length > 1 && (split[0].equalsIgnoreCase("player") || split[0].equalsIgnoreCase("owner")) && stack.getType() == Material.SKULL_ITEM && hasMetaPermission(sender, "head", false, true, ess))
		{
			if (stack.getDurability() == 3)
			{
				final String owner = split[1];
				final SkullMeta meta = (SkullMeta)stack.getItemMeta();
				meta.setOwner(owner);
				stack.setItemMeta(meta);
			}
			else
			{
				throw new Exception(tl("onlyPlayerSkulls"));
			}
		}
		else if (split.length > 1 && split[0].equalsIgnoreCase("book") && stack.getType() == Material.WRITTEN_BOOK
				 && (hasMetaPermission(sender, "book", true, true, ess) || hasMetaPermission(sender, "chapter-" + split[1].toLowerCase(Locale.ENGLISH), true, true, ess)))
		{
			final BookMeta meta = (BookMeta)stack.getItemMeta();
			final IText input = new BookInput("book", true, ess);
			final BookPager pager = new BookPager(input);

			List<String> pages = pager.getPages(split[1]);
			meta.setPages(pages);
			stack.setItemMeta(meta);
		}
		else if (split.length > 1 && split[0].equalsIgnoreCase("author") && stack.getType() == Material.WRITTEN_BOOK && hasMetaPermission(sender, "author", false, true, ess))
		{
			final String author = FormatUtil.replaceFormat(split[1]);
			final BookMeta meta = (BookMeta)stack.getItemMeta();
			meta.setAuthor(author);
			stack.setItemMeta(meta);
		}
		else if (split.length > 1 && split[0].equalsIgnoreCase("title") && stack.getType() == Material.WRITTEN_BOOK && hasMetaPermission(sender, "title", false, true, ess))
		{
			final String title = FormatUtil.replaceFormat(split[1].replace('_', ' '));
			final BookMeta meta = (BookMeta)stack.getItemMeta();
			meta.setTitle(title);
			stack.setItemMeta(meta);
		}
		else if (split.length > 1 && split[0].equalsIgnoreCase("power") && stack.getType() == Material.FIREWORK && hasMetaPermission(sender, "firework-power", false, true, ess))
		{
			final int power = NumberUtil.isInt(split[1]) ? Integer.parseInt(split[1]) : 0;
			final FireworkMeta meta = (FireworkMeta)stack.getItemMeta();
			meta.setPower(power > 3 ? 4 : power);
			stack.setItemMeta(meta);
		}
		else if (stack.getType() == Material.FIREWORK) //WARNING - Meta for fireworks will be ignored after this point.
		{
			addFireworkMeta(sender, false, string, ess);
		}
		else if (stack.getType() == Material.POTION) //WARNING - Meta for potions will be ignored after this point.
		{
			addPotionMeta(sender, false, string, ess);
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
				final int red = NumberUtil.isInt(color[0]) ? Integer.parseInt(color[0]) : 0;
				final int green = NumberUtil.isInt(color[1]) ? Integer.parseInt(color[1]) : 0;
				final int blue = NumberUtil.isInt(color[2]) ? Integer.parseInt(color[2]) : 0;
				final LeatherArmorMeta meta = (LeatherArmorMeta)stack.getItemMeta();
				meta.setColor(Color.fromRGB(red, green, blue));
				stack.setItemMeta(meta);
			}
			else
			{
				throw new Exception(tl("leatherSyntax"));
			}
		}
		else
		{
			parseEnchantmentStrings(sender, allowUnsafe, split, ess);
		}
	}

	public void addFireworkMeta(final CommandSource sender, final boolean allowShortName, final String string, final IEssentials ess) throws Exception
	{
		if (stack.getType() == Material.FIREWORK)
		{
			final String[] split = splitPattern.split(string, 2);

			if (split.length < 2)
			{
				return;
			}

			if (split[0].equalsIgnoreCase("color") || split[0].equalsIgnoreCase("colour") || (allowShortName && split[0].equalsIgnoreCase("c")))
			{
				if (validFirework)
				{
					if (!hasMetaPermission(sender, "firework", true, true, ess))
					{
						throw new Exception(tl("noMetaFirework"));
					}
					FireworkEffect effect = builder.build();
					FireworkMeta fmeta = (FireworkMeta)stack.getItemMeta();
					fmeta.addEffect(effect);
					if (fmeta.getEffects().size() > 1 && !hasMetaPermission(sender, "firework-multiple", true, true, ess))
					{
						throw new Exception(tl("multipleCharges"));
					}
					stack.setItemMeta(fmeta);
					builder = FireworkEffect.builder();
				}

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
						throw new Exception(tl("invalidFireworkFormat", split[1], split[0]));
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
					throw new Exception(tl("invalidFireworkFormat", split[1], split[0]));
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
						throw new Exception(tl("invalidFireworkFormat", split[1], split[0]));
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
						throw new Exception(tl("invalidFireworkFormat", split[1], split[0]));
					}
				}
			}
		}
	}

	public void addPotionMeta(final CommandSource sender, final boolean allowShortName, final String string, final IEssentials ess) throws Exception
	{
		if (stack.getType() == Material.POTION)
		{
			final String[] split = splitPattern.split(string, 2);

			if (split.length < 2)
			{
				return;
			}

			if (split[0].equalsIgnoreCase("effect") || (allowShortName && split[0].equalsIgnoreCase("e")))
			{
				pEffectType = Potions.getByName(split[1]);
				if (pEffectType != null && pEffectType.getName() != null)
				{
					if (hasMetaPermission(sender, "potions." + pEffectType.getName().toLowerCase(Locale.ENGLISH), true, false, ess))
					{
						validPotionEffect = true;
					}
					else
					{
						throw new Exception(tl("noPotionEffectPerm", pEffectType.getName().toLowerCase(Locale.ENGLISH)));
					}
				}
				else
				{
					throw new Exception(tl("invalidPotionMeta", split[1]));
				}
			}
			else if (split[0].equalsIgnoreCase("power") || (allowShortName && split[0].equalsIgnoreCase("p")))
			{
				if (NumberUtil.isInt(split[1]))
				{
					validPotionPower = true;
					power = Integer.parseInt(split[1]);
					if (power > 0 && power < 4)
					{
						power -= 1;
					}
				}
				else
				{
					throw new Exception(tl("invalidPotionMeta", split[1]));
				}
			}
			else if (split[0].equalsIgnoreCase("duration") || (allowShortName && split[0].equalsIgnoreCase("d")))
			{
				if (NumberUtil.isInt(split[1]))
				{
					validPotionDuration = true;
					duration = Integer.parseInt(split[1]) * 20; //Duration is in ticks by default, converted to seconds
				}
				else
				{
					throw new Exception(tl("invalidPotionMeta", split[1]));
				}
			}

			if (isValidPotion())
			{
				PotionMeta pmeta = (PotionMeta)stack.getItemMeta();
				pEffect = pEffectType.createEffect(duration, power);
				if (pmeta.getCustomEffects().size() > 1 && !hasMetaPermission(sender, "potions.multiple", true, false, ess))
				{
					throw new Exception(tl("multiplePotionEffects"));
				}
				pmeta.addCustomEffect(pEffect, true);
				stack.setItemMeta(pmeta);
				resetPotionMeta();
			}
		}
	}

	private void parseEnchantmentStrings(final CommandSource sender, final boolean allowUnsafe, final String[] split, final IEssentials ess) throws Exception
	{
		final Enchantment enchantment = Enchantments.getByName(split[0]);
		if (enchantment == null || !hasMetaPermission(sender, "enchantments." + enchantment.getName().toLowerCase(Locale.ENGLISH), false, false, ess))
		{
			return;
		}

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
		addEnchantment(sender, allowUnsafe, enchantment, level);
	}

	public void addEnchantment(final CommandSource sender, final boolean allowUnsafe, final Enchantment enchantment, final int level) throws Exception
	{
		if (enchantment == null)
		{
			throw new Exception(tl("enchantmentNotFound"));
		}
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

	public Enchantment getEnchantment(final User user, final String name) throws Exception
	{
		final Enchantment enchantment = Enchantments.getByName(name);
		if (enchantment == null)
		{
			return null;
		}

		final String enchantmentName = enchantment.getName().toLowerCase(Locale.ENGLISH);

		if (!hasMetaPermission(user, "enchantments." + enchantmentName, true, false))
		{
			throw new Exception(tl("enchantmentPerm", enchantmentName));
		}
		return enchantment;
	}

	private boolean hasMetaPermission(final CommandSource sender, final String metaPerm, final boolean graceful, final boolean includeBase, final IEssentials ess) throws Exception
	{
		final User user = sender != null && sender.isPlayer() ? ess.getUser(sender.getPlayer()) : null;
		return hasMetaPermission(user, metaPerm, graceful, includeBase);
	}

	private boolean hasMetaPermission(final User user, final String metaPerm, final boolean graceful, final boolean includeBase) throws Exception
	{
		final String permBase = includeBase ? "essentials.itemspawn.meta-" : "essentials.";
		if (user == null || user.isAuthorized(permBase + metaPerm))
		{
			return true;
		}

		if (graceful)
		{
			return false;
		}
		else
		{
			throw new Exception(tl("noMetaPerm", metaPerm));
		}
	}
}
