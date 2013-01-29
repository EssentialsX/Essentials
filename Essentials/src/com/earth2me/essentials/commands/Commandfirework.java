package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.MetaItemStack;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
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
import org.bukkit.util.Vector;

//This command has quite a complicated syntax, in theory it has 4 seperate syntaxes which are all variable:
//
//1: /firework clear             - This clears all of the effects on a firework stack
//
//2: /firework power <int>       - This changes the base power of a firework
//
//3: /firework fire              - This 'fires' a copy of the firework held.
//3: /firework fire <int>        - This 'fires' a number of copies of the firework held.
//3: /firework fire <other>      - This 'fires' a copy of the firework held, in the direction you are looking, #easteregg
//
//4: /firework [meta]            - This will add an effect to the firework stack held
//4: /firework color:<color>     - The minimum you need to set an effect is 'color'
//4: Full Syntax:                  color:<color[,color,..]> [fade:<color[,color,..]>] [shape:<shape>] [effect:<effect[,effect]>]
//4: Possible Shapes:              star, ball, large, creeper, burst
//4: Possible Effects              trail, twinkle

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
					int amount = 1;
					boolean direction = false;
					if (Util.isInt(args[1]))
					{
						final int serverLimit = ess.getSettings().getSpawnMobLimit();
						amount = Integer.parseInt(args[1]);
						if (amount > serverLimit)
						{
							amount = serverLimit;
							user.sendMessage(_("mobSpawnLimit"));
						}
					}
					else
					{
						direction = true;
					}
					for (int i = 0; i < amount; i++)
					{
						Firework firework = (Firework)user.getWorld().spawnEntity(user.getLocation(), EntityType.FIREWORK);
						FireworkMeta fmeta = (FireworkMeta)stack.getItemMeta();
						if (direction)
						{
							final Vector vector = user.getEyeLocation().getDirection().multiply(0.070);
							if (fmeta.getPower() > 1)
							{
								fmeta.setPower(1);
							}
							firework.setVelocity(vector);
						}
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
