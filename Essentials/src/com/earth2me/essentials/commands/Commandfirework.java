package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import static com.earth2me.essentials.I18n._;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;


public class Commandfirework extends EssentialsCommand
{
	private final transient Pattern splitPattern = Pattern.compile("[:+',;.]");
	private final static Map<String, DyeColor> colorMap = new HashMap<String, DyeColor>();
	private final static Map<String, FireworkEffect.Type> fireworkShape = new HashMap<String, FireworkEffect.Type>();

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

	public Commandfirework()
	{
		super("firework");
	}

	@Override
	protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{

		if (args.length > 0)
		{
			ItemStack stack = user.getItemInHand();
			if (stack.getType() == Material.FIREWORK)
			{
				FireworkEffect.Builder builder = FireworkEffect.builder();
				FireworkMeta fmeta = (FireworkMeta)stack.getItemMeta();

				if (args.length > 0)
				{
					if (args[0].equalsIgnoreCase("clear"))
					{
						fmeta.clearEffects();
						stack.setItemMeta(fmeta);
						user.setItemInHand(stack);
					}
					else
					{
						List<Color> primaryColors = new ArrayList<Color>();
						List<Color> fadeColors = new ArrayList<Color>();
						FireworkEffect.Type finalEffect = null;

						boolean valid = false;
						for (String arg : args)
						{
							final String[] split = splitPattern.split(arg, 2);
							if (split[0].equalsIgnoreCase("color") || split[0].equalsIgnoreCase("colour") || split[0].equalsIgnoreCase("c"))
							{
								String[] colors = split[1].split(",");
								for (String color : colors)
								{
									if (colorMap.containsKey(color.toUpperCase()))
									{
										valid = true;
										primaryColors.add(colorMap.get(color.toUpperCase()).getFireworkColor());
									}
									else
									{
										user.sendMessage(_("invalidFireworkFormat", split[1], split[0]));
									}
								}
								builder.withColor(primaryColors);
							}
							if (split[0].equalsIgnoreCase("shape") || split[0].equalsIgnoreCase("s") || split[0].equalsIgnoreCase("type") || split[0].equalsIgnoreCase("t"))
							{
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
							if (split[0].equalsIgnoreCase("fade") || split[0].equalsIgnoreCase("f"))
							{
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
							if (split[0].equalsIgnoreCase("effect") || split[0].equalsIgnoreCase("e"))
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
							if (split[0].equalsIgnoreCase("power") || split[0].equalsIgnoreCase("p"))
							{
								try
								{
									fmeta.setPower(Integer.parseInt(split[1]));
								}
								catch (NumberFormatException e)
								{
									user.sendMessage(_("invalidFireworkFormat", split[1], split[0]));
								}
							}
						}
						if (valid)
						{
							final FireworkEffect effect = builder.build();
							fmeta.addEffect(effect);
							stack.setItemMeta(fmeta);
							user.setItemInHand(stack);
						}
						else
						{
							user.sendMessage(_("fireworkColor"));
						}

					}
				}
			}
			else
			{
				user.sendMessage(_("holdFirework"));
			}
		}
		else
		{
			throw new NotEnoughArgumentsException();
		}
	}
}