package com.earth2me.essentials;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Mob.MobException;
import com.earth2me.essentials.utils.LocationUtil;
import com.earth2me.essentials.utils.StringUtil;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.ess3.api.IEssentials;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;


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
				if (mobPart.contains(":"))
				{
					mobData.add("");
				}
				else
				{
					mobData.add(null);
				}
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
		spawnmob(ess, server, user.getSource(), user, block.getLocation(), parts, data, mobCount);
	}

	// This method spawns a mob at target, owned by target
	public static void spawnmob(final IEssentials ess, final Server server, final CommandSource sender, final User target, final List<String> parts, final List<String> data, int mobCount) throws Exception
	{
		spawnmob(ess, server, sender, target, target.getLocation(), parts, data, mobCount);
	}

	// This method spawns a mob at loc, owned by target
	public static void spawnmob(final IEssentials ess, final Server server, final CommandSource sender, final User target, final Location loc, final List<String> parts, final List<String> data, int mobCount) throws Exception
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

	private static void spawnMob(final IEssentials ess, final Server server, final CommandSource sender, final User target, final Location sloc, List<String> parts, List<String> data) throws Exception
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
					changeMobData(sender, mob.getType(), spawnedMob, data.get(i).toLowerCase(Locale.ENGLISH), target);
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
					changeMobData(sender, mMob.getType(), spawnedMount, data.get(next).toLowerCase(Locale.ENGLISH), target);
				}

				spawnedMob.setPassenger(spawnedMount);

				spawnedMob = spawnedMount;
			}
		}
	}

	private static void checkSpawnable(IEssentials ess, CommandSource sender, Mob mob) throws Exception
	{
		if (mob == null)
		{
			throw new Exception(_("invalidMob"));
		}

		if (ess.getSettings().getProtectPreventSpawn(mob.getType().toString().toLowerCase(Locale.ENGLISH)))
		{
			throw new Exception(_("disabledToSpawnMob"));
		}

		if (sender.isPlayer() && !ess.getUser(sender.getPlayer()).isAuthorized("essentials.spawnmob." + mob.name.toLowerCase(Locale.ENGLISH)))
		{
			throw new Exception(_("noPermToSpawnMob"));
		}
	}

	private static void changeMobData(final CommandSource sender, final EntityType type, final Entity spawned, final String inputData, final User target) throws Exception
	{
		String data = inputData;

		if (data.isEmpty())
		{
			sender.sendMessage(_("mobDataList", StringUtil.joinList(MobData.getValidHelp(spawned))));
		}

		if (spawned instanceof Zombie || type == EntityType.SKELETON)
		{
			if (inputData.contains("armor") || inputData.contains("armour"))
			{
				final EntityEquipment invent = ((LivingEntity)spawned).getEquipment();
				if (inputData.contains("noarmor") || inputData.contains("noarmour"))
				{
					invent.clear();
				}
				else if (inputData.contains("diamond"))
				{
					invent.setBoots(new ItemStack(Material.DIAMOND_BOOTS, 1));
					invent.setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS, 1));
					invent.setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE, 1));
					invent.setHelmet(new ItemStack(Material.DIAMOND_HELMET, 1));
				}
				else if (inputData.contains("gold"))
				{
					invent.setBoots(new ItemStack(Material.GOLD_BOOTS, 1));
					invent.setLeggings(new ItemStack(Material.GOLD_LEGGINGS, 1));
					invent.setChestplate(new ItemStack(Material.GOLD_CHESTPLATE, 1));
					invent.setHelmet(new ItemStack(Material.GOLD_HELMET, 1));
				}
				else if (inputData.contains("leather"))
				{
					invent.setBoots(new ItemStack(Material.LEATHER_BOOTS, 1));
					invent.setLeggings(new ItemStack(Material.LEATHER_LEGGINGS, 1));
					invent.setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE, 1));
					invent.setHelmet(new ItemStack(Material.LEATHER_HELMET, 1));
				}
				else
				{
					invent.setBoots(new ItemStack(Material.IRON_BOOTS, 1));
					invent.setLeggings(new ItemStack(Material.IRON_LEGGINGS, 1));
					invent.setChestplate(new ItemStack(Material.IRON_CHESTPLATE, 1));
					invent.setHelmet(new ItemStack(Material.IRON_HELMET, 1));
				}
				invent.setBootsDropChance(0f);
				invent.setLeggingsDropChance(0f);
				invent.setChestplateDropChance(0f);
				invent.setHelmetDropChance(0f);
			}

		}

		MobData newData = MobData.fromData(spawned, data);
		while (newData != null)
		{
			newData.setData(spawned, target.getBase(), data);
			data = data.replace(newData.getMatched(), "");
			newData = MobData.fromData(spawned, data);
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

		if (type == EntityType.HORSE)
		{
			((Horse)spawned).setJumpStrength(1.2);
		}
	}
}
