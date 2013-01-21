package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.MetaItemStack;
import com.earth2me.essentials.User;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
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
		final ItemStack stack = user.getItemInHand();
		if (stack.getType() == Material.FIREWORK)
		{
			if (args.length > 0)
			{
				if (args[0].equalsIgnoreCase("clear"))
				{
					FireworkMeta fmeta = (FireworkMeta)stack.getItemMeta();
					fmeta.clearEffects();
					stack.setItemMeta(fmeta);
					user.sendMessage(_("fireworkEffectsCleared"));
				}
				else if (args.length > 1 && (args[0].equalsIgnoreCase("power") || (args[0].equalsIgnoreCase("p"))))
				{
					FireworkMeta fmeta = (FireworkMeta)stack.getItemMeta();
					try
					{
						int power = Integer.parseInt(args[1]);
						fmeta.setPower(power > 3 ? 4 : power);
					}
					catch (NumberFormatException e)
					{
						throw new Exception(_("invalidFireworkFormat", args[1], args[0]));
					}
					stack.setItemMeta(fmeta);
				}
				else if ((args[0].equalsIgnoreCase("fire") || (args[0].equalsIgnoreCase("p")))
						 && user.isAuthorized("essentials.firework.fire"))
				{
					int amount;
					try
					{
						final int serverLimit = ess.getSettings().getSpawnMobLimit();
						amount = Integer.parseInt(args[1]);
						if (amount > serverLimit)
						{
							amount = serverLimit;
							user.sendMessage(_("mobSpawnLimit"));
						}
					}
					catch (Exception e)
					{
						amount = 1;
					}
					for (int i = 0; i < amount; i++)
					{
						Firework firework = (Firework)user.getWorld().spawnEntity(user.getLocation(), EntityType.FIREWORK);
						FireworkMeta fmeta = (FireworkMeta)stack.getItemMeta();
						firework.setFireworkMeta(fmeta);
					}
				}
				else
				{
					final MetaItemStack mStack = new MetaItemStack(stack);
					for (String arg : args)
					{
						final String[] split = splitPattern.split(arg, 2);
						mStack.addFireworkMeta(user, true, arg, ess);
					}

					if (mStack.isValidFirework())
					{
						FireworkMeta fmeta = (FireworkMeta)mStack.getItemStack().getItemMeta();
						FireworkEffect effect = mStack.getFireworkBuilder().build();
						fmeta.addEffect(effect);
						stack.setItemMeta(fmeta);
					}
					else
					{
						user.sendMessage(_("fireworkSyntax"));
						throw new Exception(_("fireworkColor"));
					}
				}
			}
			else
			{
				throw new NotEnoughArgumentsException();
			}
		}
		else
		{
			throw new Exception(_("holdFirework"));
		}
	}
}
