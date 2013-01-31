package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Mob;
import java.util.ArrayList;
import java.util.Locale;
import org.bukkit.Chunk;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;


public class Commandkillall extends EssentialsCommand
{
	public Commandkillall()
	{
		super("killall");
	}

	//TODO: Tidy - missed this during command cleanup
	@Override
	public void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		String type = "all";
		int radius = -1;
		World world;
		if (sender instanceof Player)
		{
			world = ((Player)sender).getWorld();
			if (args.length == 1)
			{
				try
				{
					radius = Integer.parseInt(args[0]);
				}
				catch (NumberFormatException e1)
				{
					type = args[0];
				}
			}
			else if (args.length > 1)
			{
				type = args[0];
				try
				{
					radius = Integer.parseInt(args[1]);
				}
				catch (NumberFormatException e)
				{
					throw new Exception(_("numberRequired"), e);
				}
			}
		}
		else
		{
			if (args.length == 0)
			{
				throw new NotEnoughArgumentsException();
			}
			else if (args.length == 1)
			{
				world = ess.getWorld(args[0]);
			}
			else
			{
				type = args[0];
				world = ess.getWorld(args[1]);
			}
		}
		if (radius >= 0)
		{
			radius *= radius;
		}
		String killType = type.toLowerCase(Locale.ENGLISH);
		boolean animals = killType.startsWith("animal");
		boolean monster = killType.startsWith("monster") || killType.startsWith("mob");
		boolean all = killType.equals("all");
		Class<? extends Entity> entityClass = null;
		if (!animals && !monster && !all)
		{
			if (Mob.fromName(killType) == null)
			{
				throw new Exception(_("invalidMob"));
			}
			entityClass = Mob.fromName(killType).getType().getEntityClass();
		}
		int numKills = 0;
		for (Chunk chunk : world.getLoadedChunks())
		{
			for (Entity entity : chunk.getEntities())
			{
				if (sender instanceof Player)
				{
					if (radius >= 0 && ((Player)sender).getLocation().distanceSquared(entity.getLocation()) > radius)
					{
						continue;
					}
				}
				if (entity instanceof LivingEntity == false || entity instanceof HumanEntity)
				{
					continue;
				}
				if (entity instanceof Wolf)
				{
					if (((Wolf)entity).isTamed())
					{
						continue;
					}
				}
				if (entity instanceof Ocelot)
				{
					if (((Ocelot)entity).isTamed())
					{
						continue;
					}
				}
				if (animals)
				{
					if (entity instanceof Animals || entity instanceof NPC || entity instanceof Snowman || entity instanceof WaterMob)
					{
						EntityDeathEvent event = new EntityDeathEvent((LivingEntity)entity, new ArrayList<ItemStack>(0));
						ess.getServer().getPluginManager().callEvent(event);
						entity.remove();
						numKills++;
					}
				}
				else if (monster)
				{
					if (entity instanceof Monster || entity instanceof ComplexLivingEntity || entity instanceof Flying || entity instanceof Slime)
					{
						EntityDeathEvent event = new EntityDeathEvent((LivingEntity)entity, new ArrayList<ItemStack>(0));
						ess.getServer().getPluginManager().callEvent(event);
						entity.remove();
						numKills++;
					}
				}
				else if (all)
				{
					EntityDeathEvent event = new EntityDeathEvent((LivingEntity)entity, new ArrayList<ItemStack>(0));
					ess.getServer().getPluginManager().callEvent(event);
					entity.remove();
					numKills++;
				}
				else if (entityClass != null && entityClass.isAssignableFrom(entity.getClass()))
				{
					EntityDeathEvent event = new EntityDeathEvent((LivingEntity)entity, new ArrayList<ItemStack>(0));
					ess.getServer().getPluginManager().callEvent(event);
					entity.remove();
					numKills++;
				}
			}
		}
		sender.sendMessage(_("kill", numKills));
	}
}