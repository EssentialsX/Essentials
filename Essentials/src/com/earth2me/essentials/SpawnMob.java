package com.earth2me.essentials;

import com.earth2me.essentials.utils.StringUtil;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Mob.MobException;
import com.earth2me.essentials.utils.LocationUtil;
import com.earth2me.essentials.utils.NumberUtil;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Colorable;


public class SpawnMob
{
	public static String mobList(final User user)
	{
		final Set<String> mobList = Mob.getMobList();
		final Set<String> availableList = new HashSet<String>();
		for (String mob : mobList)
		{
			if (user.isAuthorized("essentials.spawnmob." + mob.toLowerCase(Locale.ENGLISH)))
			{
				availableList.add(mob);
			}
		}
		if (availableList.isEmpty())
		{
			availableList.add(_("none"));
		}
		return StringUtil.joinList(availableList);
	}

	public static List<String> mobParts(final String mobString)
	{
		String[] mobParts = mobString.split(",");

		List<String> mobs = new ArrayList<String>();

		for (String mobPart : mobParts)
		{
			String[] mobDatas = mobPart.split(":");
			mobs.add(mobDatas[0]);
		}
		return mobs;
	}

	public static List<String> mobData(final String mobString)
	{
		String[] mobParts = mobString.split(",");

		List<String> mobData = new ArrayList<String>();

		for (String mobPart : mobParts)
		{
			String[] mobDatas = mobPart.split(":");
			if (mobDatas.length == 1)
			{
				mobData.add(null);
			}
			else
			{
				mobData.add(mobDatas[1]);
			}
		}

		return mobData;
	}

	// This method spawns a mob where the user is looking, owned by user
	public static void spawnmob(final IEssentials ess, final Server server, final User user, final List<String> parts, final List<String> data, int mobCount) throws Exception
	{
		final Block block = LocationUtil.getTarget(user.getBase()).getBlock();
		if (block == null)
		{
			throw new Exception(_("unableToSpawnMob"));
		}
		spawnmob(ess, server, user.getBase(), user, block.getLocation(), parts, data, mobCount);
	}

	// This method spawns a mob at loc, owned by noone
	public static void spawnmob(final IEssentials ess, final Server server, final CommandSender sender, final Location loc, final List<String> parts, final List<String> data, int mobCount) throws Exception
	{
		spawnmob(ess, server, sender, null, loc, parts, data, mobCount);
	}

	// This method spawns a mob at target, owned by target
	public static void spawnmob(final IEssentials ess, final Server server, final CommandSender sender, final User target, final List<String> parts, final List<String> data, int mobCount) throws Exception
	{
		spawnmob(ess, server, sender, target, target.getLocation(), parts, data, mobCount);
	}

	// This method spawns a mob at loc, owned by target
	public static void spawnmob(final IEssentials ess, final Server server, final CommandSender sender, final User target, final Location loc, final List<String> parts, final List<String> data, int mobCount) throws Exception
	{
		final Location sloc = LocationUtil.getSafeDestination(loc);

		for (int i = 0; i < parts.size(); i++)
		{
			Mob mob = Mob.fromName(parts.get(i));
			checkSpawnable(ess, sender, mob);
		}

		final int serverLimit = ess.getSettings().getSpawnMobLimit();
		int effectiveLimit = serverLimit / parts.size();

		if (effectiveLimit < 1)
		{
			effectiveLimit = 1;
			while (parts.size() > serverLimit)
			{
				parts.remove(serverLimit);
			}
		}

		if (mobCount > effectiveLimit)
		{
			mobCount = effectiveLimit;
			sender.sendMessage(_("mobSpawnLimit"));
		}

		Mob mob = Mob.fromName(parts.get(0)); // Get the first mob
		try
		{
			for (int i = 0; i < mobCount; i++)
			{
				spawnMob(ess, server, sender, target, sloc, parts, data);
			}
			sender.sendMessage(mobCount * parts.size() + " " + mob.name.toLowerCase(Locale.ENGLISH) + mob.suffix + " " + _("spawned"));
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

	private static void spawnMob(final IEssentials ess, final Server server, final CommandSender sender, final User target, final Location sloc, List<String> parts, List<String> data) throws Exception
	{
		Mob mob;
		Entity spawnedMob = null;
		Entity spawnedMount;

		for (int i = 0; i < parts.size(); i++)
		{
			if (i == 0)
			{
				mob = Mob.fromName(parts.get(i));
				spawnedMob = mob.spawn(sloc.getWorld(), server, sloc);
				defaultMobData(mob.getType(), spawnedMob);

				if (data.get(i) != null)
				{
					changeMobData(mob.getType(), spawnedMob, data.get(i), target);
				}
			}

			int next = (i + 1);
			if (next < parts.size()) //If it's the last mob in the list, don't set the mount
			{
				Mob mMob = Mob.fromName(parts.get(next));
				spawnedMount = mMob.spawn(sloc.getWorld(), server, sloc);
				defaultMobData(mMob.getType(), spawnedMount);

				if (data.get(next) != null)
				{
					changeMobData(mMob.getType(), spawnedMount, data.get(next), target);
				}

				spawnedMob.setPassenger(spawnedMount);

				spawnedMob = spawnedMount;
			}
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

		if (sender instanceof Player && !ess.getUser(sender).isAuthorized("essentials.spawnmob." + mob.name.toLowerCase(Locale.ENGLISH)))
		{
			throw new Exception(_("noPermToSpawnMob"));
		}
	}

	private static void changeMobData(final EntityType type, final Entity spawned, String data, final User target) throws Exception
	{
		data = data.toLowerCase(Locale.ENGLISH);

		if (spawned instanceof Zombie || type == EntityType.SKELETON)
		{
			//This should match all Living Entities but most mobs will just ignore the equipment.
			if (data.contains("armor") || data.contains("armour"))
			{
				final EntityEquipment invent = ((LivingEntity)spawned).getEquipment();
				if (data.contains("diamond"))
				{
					invent.setBoots(new ItemStack(Material.DIAMOND_BOOTS, 1));
					invent.setLeggings(new ItemStack(Material.DIAMOND_BOOTS, 1));
					invent.setChestplate(new ItemStack(Material.DIAMOND_BOOTS, 1));
					invent.setHelmet(new ItemStack(Material.DIAMOND_BOOTS, 1));
				}
				else if (data.contains("gold"))
				{
					invent.setBoots(new ItemStack(Material.GOLD_BOOTS, 1));
					invent.setLeggings(new ItemStack(Material.GOLD_BOOTS, 1));
					invent.setChestplate(new ItemStack(Material.GOLD_BOOTS, 1));
					invent.setHelmet(new ItemStack(Material.GOLD_BOOTS, 1));
				}
				else if (data.contains("leather"))
				{
					invent.setBoots(new ItemStack(Material.LEATHER_BOOTS, 1));
					invent.setLeggings(new ItemStack(Material.LEATHER_BOOTS, 1));
					invent.setChestplate(new ItemStack(Material.LEATHER_BOOTS, 1));
					invent.setHelmet(new ItemStack(Material.LEATHER_BOOTS, 1));
				}
				else if (data.contains("no"))
				{
					invent.clear();
				}
				else
				{
					invent.setBoots(new ItemStack(Material.IRON_BOOTS, 1));
					invent.setLeggings(new ItemStack(Material.IRON_BOOTS, 1));
					invent.setChestplate(new ItemStack(Material.IRON_BOOTS, 1));
					invent.setHelmet(new ItemStack(Material.IRON_BOOTS, 1));
				}
				invent.setBootsDropChance(0f);
				invent.setLeggingsDropChance(0f);
				invent.setChestplateDropChance(0f);
				invent.setHelmetDropChance(0f);
			}

		}
		
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
			if (data.contains("siamese") || data.contains("white"))
			{
				((Ocelot)spawned).setCatType(Ocelot.Type.SIAMESE_CAT);
			}
			else if (data.contains("red") || data.contains("orange") || data.contains("tabby"))
			{
				((Ocelot)spawned).setCatType(Ocelot.Type.RED_CAT);
			}
			else if (data.contains("black") || data.contains("tuxedo"))
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
		
		if (spawned instanceof Zombie || type == EntityType.SKELETON)
		{
			if (data.contains("sword"))
			{
				final EntityEquipment invent = ((LivingEntity)spawned).getEquipment();
				if (data.contains("diamond"))
				{
					invent.setItemInHand(new ItemStack(Material.DIAMOND_SWORD, 1));
				}
				else if (data.contains("gold"))
				{
					invent.setItemInHand(new ItemStack(Material.GOLD_SWORD, 1));
				}
				else if (data.contains("iron"))
				{
					invent.setItemInHand(new ItemStack(Material.IRON_SWORD, 1));
				}
				else
				{
					invent.setItemInHand(new ItemStack(Material.STONE_SWORD, 1));
				}
				invent.setItemInHandDropChance(0.1f);
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
			if (NumberUtil.isInt(data))
			{
				((ExperienceOrb)spawned).setExperience(Integer.parseInt(data));

			}
		}
	}

	private static void defaultMobData(final EntityType type, final Entity spawned)
	{
		if (type == EntityType.SKELETON)
		{
			final EntityEquipment invent = ((LivingEntity)spawned).getEquipment();
			invent.setItemInHand(new ItemStack(Material.BOW, 1));
			invent.setItemInHandDropChance(0.1f);

			invent.setBoots(new ItemStack(Material.GOLD_BOOTS, 1));
			invent.setBootsDropChance(0.0f);
		}

		if (type == EntityType.PIG_ZOMBIE)
		{
			final EntityEquipment invent = ((LivingEntity)spawned).getEquipment();
			invent.setItemInHand(new ItemStack(Material.GOLD_SWORD, 1));
			invent.setItemInHandDropChance(0.1f);

			invent.setBoots(new ItemStack(Material.GOLD_BOOTS, 1));
			invent.setBootsDropChance(0.0f);
		}

		if (type == EntityType.ZOMBIE)
		{
			final EntityEquipment invent = ((LivingEntity)spawned).getEquipment();
			invent.setBoots(new ItemStack(Material.GOLD_BOOTS, 1));
			invent.setBootsDropChance(0.0f);
		}

	}
}
