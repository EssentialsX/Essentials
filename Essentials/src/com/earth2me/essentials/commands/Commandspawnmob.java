package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Mob;
import com.earth2me.essentials.Mob.MobException;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.util.Locale;
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
			throw new NotEnoughArgumentsException(_("mobsAvailable", Util.joinList(Mob.getMobList())));
		}


		String[] mountparts = args[0].split(",");
		String[] parts = mountparts[0].split(":");
		String mobType = parts[0];
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
			throw new Exception(_("invalidMob"));
		}

		if (ess.getSettings().getProtectPreventSpawn(mob.getType().toString().toLowerCase(Locale.ENGLISH)))
		{
			throw new Exception(_("unableToSpawnMob"));
		}
		if (!user.isAuthorized("essentials.spawnmob." + mob.name.toLowerCase()))
		{
			throw new Exception(_("unableToSpawnMob"));
		}

		final Block block = Util.getTarget(user).getBlock();
		if (block == null)
		{
			throw new Exception(_("unableToSpawnMob"));
		}
		Location loc = block.getLocation();
		Location sloc = Util.getSafeDestination(loc);
		try
		{
			spawnedMob = mob.spawn(user, server, sloc);
		}
		catch (MobException e)
		{
			throw new Exception(_("unableToSpawnMob"));
		}

		if (mountType != null)
		{
			mobMount = Mob.fromName(mountType);
			if (mobMount == null)
			{
				user.sendMessage(_("invalidMob"));
				return;
			}

			if (ess.getSettings().getProtectPreventSpawn(mobMount.getType().toString().toLowerCase(Locale.ENGLISH)))
			{
				throw new Exception(_("unableToSpawnMob"));
			}
			if (!user.isAuthorized("essentials.spawnmob." + mobMount.name.toLowerCase()))
			{
				throw new Exception(_("unableToSpawnMob"));
			}
			try
			{
				spawnedMount = mobMount.spawn(user, server, loc);
			}
			catch (MobException e)
			{
				throw new Exception(_("unableToSpawnMob"));
			}
			spawnedMob.setPassenger(spawnedMount);
		}
		if (mobData != null)
		{
			changeMobData(mob.getType(), spawnedMob, mobData, user);
		}
		if (spawnedMount != null && mountData != null)
		{
			changeMobData(mobMount.getType(), spawnedMount, mountData, user);
		}
		if (args.length == 2)
		{
			int mobCount = Integer.parseInt(args[1]);
			int serverLimit = ess.getSettings().getSpawnMobLimit();
			if (mobCount > serverLimit)
			{
				mobCount = serverLimit;
				user.sendMessage(_("mobSpawnLimit"));
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
							throw new Exception(_("unableToSpawnMob"));
						}
						spawnedMob.setPassenger(spawnedMount);
					}
					if (mobData != null)
					{
						changeMobData(mob.getType(), spawnedMob, mobData, user);
					}
					if (spawnedMount != null && mountData != null)
					{
						changeMobData(mobMount.getType(), spawnedMount, mountData, user);
					}
				}
				user.sendMessage(args[1] + " " + mob.name.toLowerCase(Locale.ENGLISH) + mob.suffix + " " + _("spawned"));
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
		else
		{
			user.sendMessage(mob.name + " " + _("spawned"));
		}
	}

	private void changeMobData(CreatureType type, Entity spawned, String data, User user) throws Exception
	{
		if (type == CreatureType.SLIME || type == CreatureType.MAGMA_CUBE)
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
		if ((type == CreatureType.SHEEP
			 || type == CreatureType.COW
			 || type == CreatureType.MUSHROOM_COW
			 || type == CreatureType.CHICKEN
			 || type == CreatureType.PIG
			 || type == CreatureType.WOLF)
			&& data.equalsIgnoreCase("baby"))
		{
			((Animals)spawned).setAge(-24000);
			return;
		}
		if (type == CreatureType.SHEEP)
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
					((Sheep)spawned).setColor(DyeColor.valueOf(data.toUpperCase(Locale.ENGLISH)));
				}
			}
			catch (Exception e)
			{
				throw new Exception(_("sheepMalformedColor"), e);
			}
		}
		if (type == CreatureType.WOLF
			&& data.toLowerCase(Locale.ENGLISH).startsWith("tamed"))
		{
			final Wolf wolf = ((Wolf)spawned);
			wolf.setTamed(true);
			wolf.setOwner(user);
			wolf.setSitting(true);
			if (data.equalsIgnoreCase("tamedbaby"))
			{
				((Animals)spawned).setAge(-24000);
			}
		}
		if (type == CreatureType.WOLF
			&& data.toLowerCase(Locale.ENGLISH).startsWith("angry"))
		{
			((Wolf)spawned).setAngry(true);
			if (data.equalsIgnoreCase("angrybaby"))
			{
				((Animals)spawned).setAge(-24000);
			}
		}
		if (type == CreatureType.CREEPER && data.equalsIgnoreCase("powered"))
		{
			((Creeper)spawned).setPowered(true);
		}
	}
}
