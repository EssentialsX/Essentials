package com.earth2me.essentials;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Mob.MobException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.material.Colorable;


public class SpawnMob
{
	public static String mobList(final User user)
	{
		final Set<String> mobList = Mob.getMobList();
		final Set<String> availableList = new HashSet<String>();
		for (String mob : mobList)
		{
			if (user.isAuthorized("essentials.spawnmob." + mob.toLowerCase()))
			{
				availableList.add(mob);
			}
		}
		if (availableList.isEmpty())
		{
			availableList.add(_("none"));
		}
		return Util.joinList(availableList);
	}

	public static String[] mobData(final String mobString)
	{
		String[] returnString = new String[4];

		final String[] parts = mobString.split(",");
		String[] mobParts = parts[0].split(":");

		returnString[0] = mobParts[0];
		if (mobParts.length == 2)
		{
			returnString[1] = mobParts[1];
		}

		if (parts.length > 1)
		{
			String[] mountParts = parts[1].split(":");
			returnString[2] = mountParts[0];
			if (mountParts.length == 2)
			{
				returnString[3] = mountParts[1];
			}
		}

		return returnString;
	}

	// This method spawns a mob where the user is looking, owned by user
	public static void spawnmob(final IEssentials ess, final Server server, final User user, final String[] Data, int mobCount) throws Exception
	{
		final Block block = Util.getTarget(user).getBlock();
		if (block == null)
		{
			throw new Exception(_("unableToSpawnMob"));
		}
		spawnmob(ess, server, user, user, block.getLocation(), Data, mobCount);
	}

	// This method spawns a mob at loc, owned by noone
	public static void spawnmob(final IEssentials ess, final Server server, final CommandSender sender, final Location loc, final String[] Data, int mobCount) throws Exception
	{
		spawnmob(ess, server, sender, null, loc, Data, mobCount);
	}

	// This method spawns a mob at target, owned by target
	public static void spawnmob(final IEssentials ess, final Server server, final CommandSender sender, final User target, final String[] Data, int mobCount) throws Exception
	{
		spawnmob(ess, server, sender, target, target.getLocation(), Data, mobCount);
	}

	// This method spawns a mob at loc, owned by target
	public static void spawnmob(final IEssentials ess, final Server server, final CommandSender sender, final User target, final Location loc, final String[] Data, int mobCount) throws Exception
	{
		final Location sloc = Util.getSafeDestination(loc);
		final String mobType = Data[0];
		final String mobData = Data[1];
		final String mountType = Data[2];
		final String mountData = Data[3];

		Mob mob = Mob.fromName(mobType);
		Mob mobMount = null;

		checkSpawnable(ess, sender, mob);

		if (mountType != null)
		{
			mobMount = Mob.fromName(mountType);
			checkSpawnable(ess, sender, mobMount);
		}

		int serverLimit = ess.getSettings().getSpawnMobLimit();
		if (mobCount > serverLimit)
		{
			mobCount = serverLimit;
			sender.sendMessage(_("mobSpawnLimit"));
		}

		try
		{
			for (int i = 0; i < mobCount; i++)
			{
				spawnMob(ess, server, sender, target, sloc, mob, mobData, mobMount, mountData);
			}
			sender.sendMessage(mobCount + " " + mob.name.toLowerCase(Locale.ENGLISH) + mob.suffix + " " + _("spawned"));
		}
		catch (MobException e1)
		{
			throw new Exception(_("unableToSpawnMob"), e1);
		}
		catch (NumberFormatException e2)
		{
			throw new Exception(_("numberRequired"), e2);
		}
		catch (NullPointerException np)
		{
			throw new Exception(_("soloMob"), np);
		}
	}

	private static void spawnMob(final IEssentials ess, final Server server, final CommandSender sender, final User target, final Location sloc, Mob mob, String mobData, Mob mobMount, String mountData) throws Exception
	{
		Entity spawnedMob = mob.spawn(sloc.getWorld(), server, sloc);
		Entity spawnedMount = null;

		if (mobMount != null)
		{
			spawnedMount = mobMount.spawn(sloc.getWorld(), server, sloc);
			spawnedMob.setPassenger(spawnedMount);
		}
		if (mobData != null)
		{
			changeMobData(mob.getType(), spawnedMob, mobData, target);
		}
		if (spawnedMount != null && mountData != null)
		{
			changeMobData(mobMount.getType(), spawnedMount, mountData, target);
		}
	}

	private static void checkSpawnable(IEssentials ess, CommandSender sender, Mob mob) throws Exception
	{
		if (mob == null)
		{
			throw new Exception(_("invalidMob"));
		}

		if (ess.getSettings().getProtectPreventSpawn(mob.getType().toString().toLowerCase(Locale.ENGLISH)))
		{
			throw new Exception(_("disabledToSpawnMob"));
		}

		if (sender instanceof User && !((User)sender).isAuthorized("essentials.spawnmob." + mob.name.toLowerCase()))
		{
			throw new Exception(_("noPermToSpawnMob"));
		}
	}

	private static void changeMobData(final EntityType type, final Entity spawned, String data, final User target) throws Exception
	{
		data = data.toLowerCase(Locale.ENGLISH);

		if (spawned instanceof Slime)
		{
			try
			{
				((Slime)spawned).setSize(Integer.parseInt(data));
			}
			catch (Exception e)
			{
				throw new Exception(_("slimeMalformedSize"), e);
			}
		}

		if ((spawned instanceof Ageable) && data.contains("baby"))
		{
			((Ageable)spawned).setBaby();
			data = data.replace("baby", "");
		}

		if (spawned instanceof Colorable)
		{
			final String color = data.toUpperCase(Locale.ENGLISH);
			try
			{
				if (color.equals("RANDOM"))
				{
					final Random rand = new Random();
					((Colorable)spawned).setColor(DyeColor.values()[rand.nextInt(DyeColor.values().length)]);
				}
				else if (color.length() > 1)
				{
					((Colorable)spawned).setColor(DyeColor.valueOf(color));
				}
			}
			catch (Exception e)
			{
				throw new Exception(_("sheepMalformedColor"), e);
			}
		}

		if (spawned instanceof Tameable && data.contains("tamed") && target != null)
		{
			final Tameable tameable = ((Tameable)spawned);
			tameable.setTamed(true);
			tameable.setOwner(target.getBase());
			data = data.replace("tamed", "");
		}

		if (type == EntityType.WOLF)
		{
			if (data.contains("angry"))
			{
				((Wolf)spawned).setAngry(true);
			}
		}

		if (type == EntityType.CREEPER && data.contains("powered"))
		{
			((Creeper)spawned).setPowered(true);
		}

		if (type == EntityType.OCELOT)
		{
			if (data.contains("siamese"))
			{
				((Ocelot)spawned).setCatType(Ocelot.Type.SIAMESE_CAT);
			}
			else if (data.contains("red"))
			{
				((Ocelot)spawned).setCatType(Ocelot.Type.RED_CAT);
			}
			else if (data.contains("black"))
			{
				((Ocelot)spawned).setCatType(Ocelot.Type.BLACK_CAT);
			}
		}

		if (type == EntityType.VILLAGER)
		{
			for (Villager.Profession prof : Villager.Profession.values())
			{
				if (data.contains(prof.toString().toLowerCase(Locale.ENGLISH)))
				{
					((Villager)spawned).setProfession(prof);
				}
			}
		}

		if (spawned instanceof Zombie)
		{
			if (data.contains("villager"))
			{
				((Zombie)spawned).setVillager(true);
			}
			if (data.contains("baby"))
			{
				((Zombie)spawned).setBaby(true);
			}
		}

		if (type == EntityType.SKELETON)
		{
			if (data.contains("wither"))
			{
				((Skeleton)spawned).setSkeletonType(SkeletonType.WITHER);
			}
		}

		if (type == EntityType.EXPERIENCE_ORB)
		{
			if (Util.isInt(data))
			{
				((ExperienceOrb)spawned).setExperience(Integer.parseInt(data));

			}
		}
	}
}
