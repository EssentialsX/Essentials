package com.earth2me.essentials.commands;

import java.util.Locale;
import static com.earth2me.essentials.I18n._;
import org.bukkit.Chunk;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;


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
		PAINTINGS
	}

	@Override
	protected void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 2)
		{
			throw new NotEnoughArgumentsException();
		}
		World world;
		int radius = -1;
		if (sender instanceof Player)
		{
			world = ((Player)sender).getWorld();
			try
			{
				radius = Integer.parseInt(args[1]);
			}
			catch (NumberFormatException e)
			{
				throw new Exception(_("numberRequired"));
			}
		}
		else
		{
			world = ess.getWorld(args[1]);
		}
		if (world == null)
		{
			throw new Exception(_("invalidWorld"));
		}
		ToRemove toRemove;
		try
		{
			toRemove = ToRemove.valueOf(args[0].toUpperCase(Locale.ENGLISH));
		}
		catch (IllegalArgumentException e)
		{
			throw new NotEnoughArgumentsException(); //TODO: translate and list types
		}
		int removed = 0;
		for (Chunk chunk : world.getLoadedChunks())
		{
			for (Entity e : chunk.getEntities())
			{
				if (sender instanceof Player)
				{
					if (((Player)sender).getLocation().distance(e.getLocation()) > radius && radius >= 0)
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
				else if (toRemove == ToRemove.DROPS)
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
			}
		}
		sender.sendMessage(_("kill", removed));
	}
}
