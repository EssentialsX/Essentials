package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Mob;
import com.earth2me.essentials.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.*;

// This could be rewritten in a simpler form if we made a mapping of all Entity names to their types (which would also provide possible mod support)

public class Commandremove extends EssentialsCommand
{
	public Commandremove()
	{
		super("remove");
	}

	@Override
	protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		World world = user.getWorld();
		int radius = 0;
		Bukkit.broadcastMessage("len: " + args.length);
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
		parseCommand(server, user.getSource(), args, world, radius);

	}

	@Override
	protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 2)
		{
			throw new NotEnoughArgumentsException();
		}
		World world = ess.getWorld(args[1]);
		parseCommand(server, sender, args, world, 0);
	}

	private void parseCommand(Server server, CommandSource sender, String[] args, World world, int radius) throws Exception
	{
		List<String> types = new ArrayList<String>();
		List<String> customTypes = new ArrayList<String>();

		if (args.length > 0 && (args[0].contentEquals("*") || args[0].contentEquals("all")))
		{
			types.add(0, "ALL");
		}
		else
		{
			for (String s : args[0].split(","))
			{
				ToRemove toRemove;
				try
				{
					toRemove = ToRemove.valueOf(s.toUpperCase(Locale.ENGLISH));
				}
				catch (Exception e)
				{
					try
					{
						toRemove = ToRemove.valueOf(s.concat("S").toUpperCase(Locale.ENGLISH));
					}
					catch (Exception ee)
					{
						toRemove = ToRemove.CUSTOM;
						customTypes.add(s);
					}
				}
				types.add(toRemove.toString());
			}
		}
		removeHandler(sender, types, customTypes, world, radius);
	}

	private void removeHandler(CommandSource sender, List<String> types, List<String> customTypes, World world, int radius)
	{
		int removed = 0;
		if (radius > 0)
		{
			radius *= radius;
		}

		ArrayList<ToRemove> removeTypes = new ArrayList<ToRemove>();

		for (String s : types)
		{
			removeTypes.add(ToRemove.valueOf(s));
		}

		for (Chunk chunk : world.getLoadedChunks())
		{
			for (Entity e : chunk.getEntities())
			{
				if (radius > 0)
				{
					if (sender.getPlayer().getLocation().distanceSquared(e.getLocation()) > radius)
					{
						continue;
					}
				}
				if (e instanceof HumanEntity)
				{
					continue;
				}

				for (ToRemove toRemove : removeTypes)
				{

					if (e instanceof Tameable && ((Tameable)e).isTamed())
					{
						if (toRemove == ToRemove.TAMED)
						{
							e.remove();
							removed++;
						}
						else
						{
							continue;
						}
					}

					switch (toRemove)
					{
					case DROPS:
						if (e instanceof Item)
						{
							e.remove();
							removed++;
						}
						;
						break;
					case ARROWS:
						if (e instanceof Projectile)
						{
							e.remove();
							removed++;
						}
						break;
					case BOATS:
						if (e instanceof Boat)
						{
							e.remove();
							removed++;
						}
						break;
					case MINECARTS:
						if (e instanceof Minecart)
						{
							e.remove();
							removed++;
						}
						break;
					case XP:
						if (e instanceof ExperienceOrb)
						{
							e.remove();
							removed++;
						}
						break;
					case PAINTINGS:
						if (e instanceof Painting)
						{
							e.remove();
							removed++;
						}
						break;
					case ITEMFRAMES:
						if (e instanceof ItemFrame)
						{
							e.remove();
							removed++;
						}
						break;
					case ENDERCRYSTALS:
						if (e instanceof EnderCrystal)
						{
							e.remove();
							removed++;
						}
						break;
					case AMBIENT:
						if (e instanceof Flying)
						{
							e.remove();
							removed++;
						}
						break;
					case HOSTILE:
					case MONSTERS:
						if (e instanceof Monster || e instanceof ComplexLivingEntity || e instanceof Flying || e instanceof Slime)
						{
							e.remove();
							removed++;
						}
						break;
					case PASSIVE:
					case ANIMALS:
						if (e instanceof Animals || e instanceof NPC || e instanceof Snowman || e instanceof WaterMob || e instanceof Ambient)
						{
							e.remove();
							removed++;
						}
						break;
					case MOBS:
						if (e instanceof Animals || e instanceof NPC || e instanceof Snowman || e instanceof WaterMob
							|| e instanceof Monster || e instanceof ComplexLivingEntity || e instanceof Flying || e instanceof Slime || e instanceof Ambient)
						{
							e.remove();
							removed++;
						}
						break;
					case ENTITIES:
					case ALL:
						if (e instanceof Entity)
						{
							e.remove();
							removed++;
						}
						break;
					case CUSTOM:
						for (String type : customTypes)
						{
							if (e.getType() == Mob.fromName(type).getType())
							{
								e.remove();
								removed++;
							}
						}
						break;
					}
				}
			}
		}
		sender.sendMessage(_("removed", removed));
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
		ENDERCRYSTALS,
		HOSTILE,
		MONSTERS,
		PASSIVE,
		ANIMALS,
		AMBIENT,
		MOBS,
		ENTITIES,
		ALL,
		CUSTOM,
		TAMED
	}
}