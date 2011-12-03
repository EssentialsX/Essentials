package com.earth2me.essentials.commands;

import com.earth2me.essentials.Mob;
import static com.earth2me.essentials.I18n._;
import java.util.Collections;
import org.bukkit.Chunk;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Animals;
import org.bukkit.entity.ComplexLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Flying;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.NPC;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.WaterMob;
import org.bukkit.entity.Wolf;
import org.bukkit.event.entity.EntityDeathEvent;

public class Commandbutcher extends EssentialsCommand
{
	public Commandbutcher()
	{
		super("butcher");
	}

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
					throw new Exception(_("numberRequired"));
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
		String killType = type.toLowerCase();
		int numKills = 0;
		for (Chunk chunk : world.getLoadedChunks())
		{
			for (Entity entity : chunk.getEntities())
			{
				if (sender instanceof Player)
				{
					if (((Player)sender).getLocation().distance(entity.getLocation()) > radius && radius >= 0)
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
				if (killType.contains("animal"))
				{
					if (entity instanceof Animals || entity instanceof NPC || entity instanceof Snowman || entity instanceof WaterMob)
					{
						EntityDeathEvent event = new EntityDeathEvent(entity, Collections.EMPTY_LIST);
						ess.getServer().getPluginManager().callEvent(event);
						entity.remove();
						numKills++;
					}
				}
				else if (killType.contains("monster"))
				{
					if (entity instanceof Monster || entity instanceof ComplexLivingEntity || entity instanceof Flying || entity instanceof Slime)
					{
						EntityDeathEvent event = new EntityDeathEvent(entity, Collections.EMPTY_LIST);
						ess.getServer().getPluginManager().callEvent(event);
						entity.remove();
						numKills++;
					}
				}
				else if (killType.contains("all"))
				{
					EntityDeathEvent event = new EntityDeathEvent(entity, Collections.EMPTY_LIST);
					ess.getServer().getPluginManager().callEvent(event);
					entity.remove();
					numKills++;
				}
				else
				{
					if (Mob.fromName(killType).getType().getEntityClass().isAssignableFrom(entity.getClass()))
					{
						EntityDeathEvent event = new EntityDeathEvent(entity, Collections.EMPTY_LIST);
						ess.getServer().getPluginManager().callEvent(event);
						entity.remove();
						numKills++;
					}
				}
			}
		}
		sender.sendMessage(_("kill", numKills));
	}
}