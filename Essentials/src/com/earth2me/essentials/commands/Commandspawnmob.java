package com.earth2me.essentials.commands;

import net.minecraft.server.WorldServer;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.craftbukkit.entity.CraftEntity;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Mob;
import com.earth2me.essentials.Mob.MobException;
import com.earth2me.essentials.TargetBlock;
import com.earth2me.essentials.Util;
import net.minecraft.server.EntityWolf;
import net.minecraft.server.PathEntity;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftCreeper;
import org.bukkit.craftbukkit.entity.CraftSheep;
import org.bukkit.craftbukkit.entity.CraftSlime;
import org.bukkit.craftbukkit.entity.CraftWolf;


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
		mobType = mobType.equalsIgnoreCase("PigZombie") ? "PigZombie" : capitalCase(mobType);
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
			mountType = mountType.equalsIgnoreCase("PigZombie") ? "PigZombie" : capitalCase(mountType);
			if (parts.length == 2)
			{
				mountData = parts[1];
			}
		}


		CraftEntity spawnedMob = null;
		Mob mob = null;
		CraftEntity spawnedMount = null;
		Mob mobMount = null;

		mob = Mob.fromName(mobType);
		if (mob == null)
		{
			user.sendMessage(Util.i18n("invalidMob"));
			return;
		}
		charge(user);
		WorldServer world = ((CraftWorld)user.getWorld()).getHandle();
		try
		{
			spawnedMob = mob.spawn(user, server);
		}
		catch (MobException e)
		{
			user.sendMessage(Util.i18n("unableToSpawnMob"));
			return;
		}
		int[] ignore =
		{
			8, 9
		};
		Location loc = (new TargetBlock(user, 300, 0.2, ignore)).getTargetBlock().getLocation();

		Block block = user.getWorld().getBlockAt(loc);
		while (!(block.getType() == Material.AIR || block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER))
		{
			loc.setY(loc.getY() + 1);
			block = user.getWorld().getBlockAt(loc);
		}
		spawnedMob.teleportTo(loc);
		world.addEntity(spawnedMob.getHandle());

		if (mountType != null)
		{
			mobMount = Mob.fromName(mountType);
			if (mobMount == null)
			{
				user.sendMessage(Util.i18n("invalidMob"));
				return;
			}
			try
			{
				spawnedMount = mobMount.spawn(user, server);
			}
			catch (MobException e)
			{
				user.sendMessage(Util.i18n("unableToSpawnMob"));
				return;
			}
			spawnedMount.teleportTo(spawnedMob);
			spawnedMount.getHandle().setPassengerOf(spawnedMob.getHandle());
			world.addEntity(spawnedMount.getHandle());
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
					spawnedMob = mob.spawn(user, server);
					spawnedMob.teleportTo(loc);
					world.addEntity(spawnedMob.getHandle());
					if (mobMount != null)
					{
						try
						{
							spawnedMount = mobMount.spawn(user, server);
						}
						catch (MobException e)
						{
							user.sendMessage(Util.i18n("unableToSpawnMob"));
							return;
						}
						spawnedMount.teleportTo(spawnedMob);
						spawnedMount.getHandle().setPassengerOf(spawnedMob.getHandle());
						world.addEntity(spawnedMount.getHandle());
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
				user.sendMessage(args[1] + " " + mob.name.toLowerCase() + mob.s + Util.i18n("spawned"));
			}
			catch (MobException e1)
			{
				throw new Exception(Util.i18n("unableToSpawnMob"));
			}
			catch (NumberFormatException e2)
			{
				throw new Exception(Util.i18n("numberRequired"));
			}
			catch (NullPointerException np)
			{
				throw new Exception(Util.i18n("soloMob"));
			}
		}
		else
		{
			user.sendMessage(mob.name + " " + Util.i18n("spawned"));
		}
	}

	private String capitalCase(String s)
	{
		return s.toUpperCase().charAt(0) + s.toLowerCase().substring(1);
	}

	private void changeMobData(String type, CraftEntity spawned, String data, User user) throws Exception
	{
		if ("Slime".equalsIgnoreCase(type))
		{
			try
			{
				((CraftSlime)spawned).setSize(Integer.parseInt(data));
			}
			catch (Exception e)
			{
				throw new Exception(Util.i18n("slimeMalformedSize"));
			}
		}
		if ("Sheep".equalsIgnoreCase(type))
		{
			try
			{
				((CraftSheep)spawned).setColor(DyeColor.valueOf(data.toUpperCase()));
			}
			catch (Exception e)
			{
				throw new Exception(Util.i18n("sheepMalformedColor"));
			}
		}
		if ("Wolf".equalsIgnoreCase(type) && data.equalsIgnoreCase("tamed"))
		{
			EntityWolf wolf = ((CraftWolf)spawned).getHandle();
			wolf.d(true);
			wolf.a((PathEntity)null);
			wolf.setSitting(true);
			wolf.health = 20;
			wolf.a(user.getName());
			wolf.world.a(wolf, (byte)7);
		}
		if ("Wolf".equalsIgnoreCase(type) && data.equalsIgnoreCase("angry"))
		{
			((CraftWolf)spawned).setAngry(true);
		}
		if ("Creeper".equalsIgnoreCase(type) && data.equalsIgnoreCase("powered")) {
			((CraftCreeper)spawned).setPowered(true);
		}
	}
}
