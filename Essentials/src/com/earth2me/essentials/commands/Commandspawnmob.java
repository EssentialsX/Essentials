package com.earth2me.essentials.commands;

import com.earth2me.essentials.Mob;
import com.earth2me.essentials.Mob.MobException;
import com.earth2me.essentials.TargetBlock;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.util.Random;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.*;


public class Commandspawnmob extends EssentialsCommand
{
	public Commandspawnmob()
	{
		super("spawnmob");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
			//TODO: user.sendMessage("§7Mobs: Zombie PigZombie Skeleton Slime Chicken Pig Monster Spider Creeper Ghast Squid Giant Cow Sheep Wolf");
		}


		String[] mountparts = args[0].split(",");
		String[] parts = mountparts[0].split(":");
		String mobType = parts[0];
		mobType = mobType.equalsIgnoreCase("CaveSpider") ? "CaveSpider" : mobType.equalsIgnoreCase("PigZombie") ? "PigZombie" : Util.capitalCase(mobType);
		String mobData = null;
		if (parts.length == 2)
		{
			mobData = parts[1];
		}
		String mountType = null;
		String mountData = null;
		if (mountparts.length > 1)
		{
			parts = mountparts[1].split(":");
			mountType = parts[0];
			mountType = mountType.equalsIgnoreCase("CaveSpider") ? "CaveSpider" : mountType.equalsIgnoreCase("PigZombie") ? "PigZombie" : Util.capitalCase(mountType);
			if (parts.length == 2)
			{
				mountData = parts[1];
			}
		}


		Entity spawnedMob = null;
		Mob mob = null;
		Entity spawnedMount = null;
		Mob mobMount = null;

		mob = Mob.fromName(mobType);
		if (mob == null)
		{
			throw new Exception(Util.i18n("invalidMob"));
		}
		
		if (ess.getSettings().getProtectPreventSpawn(mob.getType().toString().toLowerCase()))
		{
			throw new Exception(Util.i18n("unableToSpawnMob"));
		}

		int[] ignore =
		{
			8, 9
		};
		Block block = (new TargetBlock(user, 300, 0.2, ignore)).getTargetBlock();
		if (block == null)
		{
			throw new Exception(Util.i18n("unableToSpawnMob"));
		}
		Location loc = block.getLocation();
		Location sloc = Util.getSafeDestination(loc);
		try
		{
			spawnedMob = mob.spawn(user, server, sloc);
		}
		catch (MobException e)
		{
			throw new Exception(Util.i18n("unableToSpawnMob"));
		}

		if (mountType != null)
		{
			mobMount = Mob.fromName(mountType);
			if (mobMount == null)
			{
				user.sendMessage(Util.i18n("invalidMob"));
				return;
			}
			
			if (ess.getSettings().getProtectPreventSpawn(mobMount.getType().toString().toLowerCase()))
			{
				throw new Exception(Util.i18n("unableToSpawnMob"));
			}
			try
			{
				spawnedMount = mobMount.spawn(user, server, loc);
			}
			catch (MobException e)
			{
				throw new Exception(Util.i18n("unableToSpawnMob"));
			}
			spawnedMob.setPassenger(spawnedMount);
		}
		if (mobData != null)
		{
			changeMobData(mob.name, spawnedMob, mobData, user);
		}
		if (spawnedMount != null && mountData != null)
		{
			changeMobData(mobMount.name, spawnedMount, mountData, user);
		}
		if (args.length == 2)
		{
			int mobCount = Integer.parseInt(args[1]);
			int serverLimit = ess.getSettings().getSpawnMobLimit();
			if (mobCount > serverLimit)
			{
				mobCount = serverLimit;
				user.sendMessage(Util.i18n("mobSpawnLimit"));
			}

			try
			{
				for (int i = 1; i < mobCount; i++)
				{
					spawnedMob = mob.spawn(user, server, loc);
					if (mobMount != null)
					{
						try
						{
							spawnedMount = mobMount.spawn(user, server, loc);
						}
						catch (MobException e)
						{
							throw new Exception(Util.i18n("unableToSpawnMob"));
						}
						spawnedMob.setPassenger(spawnedMount);
					}
					if (mobData != null)
					{
						changeMobData(mob.name, spawnedMob, mobData, user);
					}
					if (spawnedMount != null && mountData != null)
					{
						changeMobData(mobMount.name, spawnedMount, mountData, user);
					}
				}
				user.sendMessage(args[1] + " " + mob.name.toLowerCase() + mob.suffix + " " + Util.i18n("spawned"));
			}
			catch (MobException e1)
			{
				throw new Exception(Util.i18n("unableToSpawnMob"), e1);
			}
			catch (NumberFormatException e2)
			{
				throw new Exception(Util.i18n("numberRequired"), e2);
			}
			catch (NullPointerException np)
			{
				throw new Exception(Util.i18n("soloMob"), np);
			}
		}
		else
		{
			user.sendMessage(mob.name + " " + Util.i18n("spawned"));
		}
	}

	private void changeMobData(String type, Entity spawned, String data, User user) throws Exception
	{
		if ("Slime".equalsIgnoreCase(type))
		{
			try
			{
				((Slime)spawned).setSize(Integer.parseInt(data));
			}
			catch (Exception e)
			{
				throw new Exception(Util.i18n("slimeMalformedSize"), e);
			}
		}
		if ("Sheep".equalsIgnoreCase(type))
		{
			try
			{
				if (data.equalsIgnoreCase("random"))
				{
					Random rand = new Random();
					((Sheep)spawned).setColor(DyeColor.values()[rand.nextInt(DyeColor.values().length)]);
				}
				else
				{
					((Sheep)spawned).setColor(DyeColor.valueOf(data.toUpperCase()));
				}
			}
			catch (Exception e)
			{
				throw new Exception(Util.i18n("sheepMalformedColor"), e);
			}
		}
		if ("Wolf".equalsIgnoreCase(type) && data.equalsIgnoreCase("tamed"))
		{
			Wolf wolf = ((Wolf)spawned);
			wolf.setTamed(true);
			wolf.setOwner(user);
			wolf.setSitting(true);
		}
		if ("Wolf".equalsIgnoreCase(type) && data.equalsIgnoreCase("angry"))
		{
			((Wolf)spawned).setAngry(true);
		}
		if ("Creeper".equalsIgnoreCase(type) && data.equalsIgnoreCase("powered"))
		{
			((Creeper)spawned).setPowered(true);
		}
	}
}
