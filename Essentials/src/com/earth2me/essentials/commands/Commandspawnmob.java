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
import net.minecraft.server.EntitySheep;
import net.minecraft.server.EntityWolf;
import net.minecraft.server.PathEntity;
import org.bukkit.DyeColor;
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
	public String[] getTriggers()
	{
		return new String[]
				{
					getName(), "mob"
				};
	}

	@Override
	public void run(Server server, Essentials parent, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			user.sendMessage("§cUsage: /spawnmob [mob]<,mount><:size> <quantity>");
			user.sendMessage("§7Mobs: Zombie PigZombie Skeleton Slime Chicken Pig Monster Spider Creeper Ghast Squid Giant Cow Sheep Wolf");
			return;
		}

		String[] split1 = args[0].split(":");
		String[] split0 = null;
		CraftEntity spawned1 = null;
		Mob mob2 = null;
		if (split1.length == 1 && !split1[0].equalsIgnoreCase("Slime"))
		{
			split0 = args[0].split(",");
			split1[0] = split0[0];
		}
		if (split1.length == 2)
		{
			args[0] = split1[0] + "";
		}
		Mob mob = Mob.fromName(split1[0].equalsIgnoreCase("PigZombie") ? "PigZombie" : capitalCase(split1[0]));
		if (mob == null)
		{
			user.sendMessage("Invalid mob type.");
			return;
		}
		WorldServer world = ((org.bukkit.craftbukkit.CraftWorld)user.getWorld()).getHandle();
		CraftEntity spawned = null;
		try
		{
			spawned = mob.spawn(user, server);
		}
		catch (MobException e)
		{
			user.sendMessage("Unable to spawn mob.");
			return;
		}
		int[] ignore = {8, 9};
		Location loc = (new TargetBlock(user, 300, 0.2, ignore)).getTargetBlock().getLocation();
		int blkId = user.getWorld().getBlockTypeIdAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		while (!(blkId == 0 || blkId == 8 || blkId == 9))
		{
			loc.setY(loc.getY() + 1);
			blkId = user.getWorld().getBlockTypeIdAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		}
		spawned.teleportTo(loc);
		world.a(spawned.getHandle());
		if (split0 != null && split0.length == 2)
		{
			mob2 = Mob.fromName(split0[1].equalsIgnoreCase("PigZombie") ? "PigZombie" : capitalCase(split0[1]));
			if (mob2 == null)
			{
				user.sendMessage("Invalid mob type.");
				return;
			}
			try
			{
				spawned1 = mob2.spawn(user, server);
			}
			catch (MobException e)
			{
				user.sendMessage("Unable to spawn mob.");
				return;
			}
			spawned1.teleportTo(spawned);
			spawned1.getHandle().setPassengerOf(spawned.getHandle());
			world.a(spawned1.getHandle());
		}
		if (split1.length == 2 && "Slime".equals(mob.name))
		{
			try
			{
				((CraftSlime)spawned).setSize(Integer.parseInt(split1[1]));
			}
			catch (Exception e)
			{
				user.sendMessage("Malformed size.");
				return;
			}
		}
		if (split1.length == 2 && "Sheep".equals(mob.name))
		{
			try
			{
				((CraftSheep)spawned).setColor(DyeColor.valueOf(split1[1].toUpperCase()));
			}
			catch (Exception e)
			{
				user.sendMessage("Malformed color.");
				return;
			}
		}
		if (split1.length == 2 && "Wolf".equals(mob.name) && split1[1].equalsIgnoreCase("tamed")) 
		{
			EntityWolf wolf = ((CraftWolf) spawned).getHandle();
			wolf.d(true);
			wolf.a((PathEntity) null);
			wolf.b(true);
			wolf.health = 20;
			wolf.a(user.getName());
			wolf.world.a(wolf, (byte) 7);
		}
		if (split1.length == 2 && "Wolf".equals(mob.name) && split1[1].equalsIgnoreCase("angry"))
		{
			((CraftWolf)spawned).setAngry(true);
		}
		if (args.length == 2)
		{
			int mobCount = Integer.parseInt(args[1]);
			int serverLimit =  Essentials.getSettings().getSpawnMobLimit();
			if(mobCount > serverLimit)
			{
				mobCount = serverLimit;
				user.sendMessage("Mob quantity limited to server limit");
			}
			user.charge(this);
			try
			{
				for (int i = 1; i < mobCount; i++)
				{
					spawned = mob.spawn(user, server);
					spawned.teleportTo(loc);
					if (split1.length > 1 && "Slime".equals("Slime"))
					{
						try
						{
							//((EntitySlime)spawned.getHandle()).a(Integer.parseInt(split1[1]));
						}
						catch (Exception e)
						{
							user.sendMessage("Malformed size.");
							return;
						}
					}
					world.a(spawned.getHandle());
					if (split0.length == 2)
					{
						if (mob2 == null)
						{
							user.sendMessage("Invalid mob mount.");
							return;
						}
						try
						{
							spawned1 = mob2.spawn(user, server);
						}
						catch (MobException e)
						{
							user.sendMessage("Unable to spawn mob.");
							return;
						}
						spawned1.teleportTo(spawned);
						spawned1.getHandle().setPassengerOf(spawned.getHandle());
						world.a(spawned1.getHandle());
					}
				}
				user.sendMessage(args[1] + " " + mob.name.toLowerCase() + mob.s + " spawned.");
			}
			catch (MobException e1)
			{
				throw new Exception("Unable to spawn mobs.  Insert bad excuse here.");
			}
			catch (NumberFormatException e2)
			{
				throw new Exception("A number goes there, silly.");
			}
			catch (NullPointerException np)
			{
				throw new Exception("That mob likes to be alone");
			}
		}
		else
		{
			user.sendMessage(mob.name + " spawned.");
		}
	}

	private String capitalCase(String s)
	{
		return s.toUpperCase().charAt(0) + s.toLowerCase().substring(1);
	}
}
