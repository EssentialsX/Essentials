package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import java.util.Locale;
import org.bukkit.Chunk;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;

//Todo: Fix this up
public class Commandremove extends EssentialsCommand
{
	public Commandremove()
	{
		super("remove");
	}


	private enum ToRemove
	{
		DROPS,
		ARROWS,
		BOATS,
		MINECARTS,
		XP,
		PAINTINGS,
		ITEMFRAMES,
		ENDERCRYSTALS
	}

	@Override
	protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		ToRemove toRemove;
		World world = user.getWorld();
		int radius = 0;

		if (args.length >= 2)
		{
			try
			{
				radius = Integer.parseInt(args[1]);
			}
			catch (NumberFormatException e)
			{
				throw new Exception(_("numberRequired"), e);
			}
		}

		if (args.length >= 3)
		{
			world = ess.getWorld(args[2]);
		}

		try
		{
			toRemove = ToRemove.valueOf(args[0].toUpperCase(Locale.ENGLISH));
		}
		catch (IllegalArgumentException e)
		{
			try
			{
				toRemove = ToRemove.valueOf(args[0].concat("S").toUpperCase(Locale.ENGLISH));
			}
			catch (IllegalArgumentException ee)
			{
				throw new NotEnoughArgumentsException(ee); //TODO: translate and list types
			}
		}

		removeEntities(user.getBase(), world, toRemove, radius);
	}

	@Override
	protected void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 2)
		{
			throw new NotEnoughArgumentsException();
		}
		World world = ess.getWorld(args[1]);

		ToRemove toRemove;
		try
		{
			toRemove = ToRemove.valueOf(args[0].toUpperCase(Locale.ENGLISH));
		}
		catch (IllegalArgumentException e)
		{
			try
			{
				toRemove = ToRemove.valueOf(args[0].concat("S").toUpperCase(Locale.ENGLISH));
			}
			catch (IllegalArgumentException ee)
			{
				throw new NotEnoughArgumentsException(ee); //TODO: translate and list types
			}
		}
		removeEntities(sender, world, toRemove, 0);
	}

	protected void removeEntities(final CommandSender sender, final World world, final ToRemove toRemove, int radius) throws Exception
	{
		int removed = 0;
		if (radius > 0)
		{
			radius *= radius;
		}
		for (Chunk chunk : world.getLoadedChunks())
		{
			for (Entity e : chunk.getEntities())
			{
				if (radius > 0)
				{
					if (((Player)sender).getLocation().distanceSquared(e.getLocation()) > radius)
					{
						continue;
					}
				}
				if (toRemove == ToRemove.DROPS)
				{
					if (e instanceof Item)
					{
						e.remove();
						removed++;
					}
				}
				else if (toRemove == ToRemove.ARROWS)
				{
					if (e instanceof Projectile)
					{
						e.remove();
						removed++;
					}
				}
				else if (toRemove == ToRemove.BOATS)
				{
					if (e instanceof Boat)
					{
						e.remove();
						removed++;
					}
				}
				else if (toRemove == ToRemove.MINECARTS)
				{
					if (e instanceof Minecart)
					{
						e.remove();
						removed++;
					}
				}
				else if (toRemove == ToRemove.XP)
				{
					if (e instanceof ExperienceOrb)
					{
						e.remove();
						removed++;
					}
				}
				else if (toRemove == ToRemove.PAINTINGS)
				{
					if (e instanceof Painting)
					{
						e.remove();
						removed++;
					}
				}
				else if (toRemove == ToRemove.ITEMFRAMES)
				{
					if (e instanceof Painting)
					{
						e.remove();
						removed++;
					}
				}
				else if (toRemove == ToRemove.ENDERCRYSTALS)
				{
					if (e instanceof EnderCrystal)
					{
						e.remove();
						removed++;
					}
				}
			}
		}
		sender.sendMessage(_("removed", removed));
	}
}
