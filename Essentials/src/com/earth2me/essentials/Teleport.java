package com.earth2me.essentials;

import java.util.Calendar;
import java.util.GregorianCalendar;
import org.bukkit.Location;
import org.bukkit.entity.Entity;


public class Teleport implements Runnable
{
	private static class Target
	{
		private Location location = null;
		private Entity entity = null;

		public Target(Location location)
		{
			this.location = location;
		}

		public Target(Entity entity)
		{
			this.entity = entity;
		}

		public Location getLocation()
		{
			if (this.entity != null)
			{
				return this.entity.getLocation();
			}
			return location;
		}
	}
	User user;
	private int teleTimer = -1;
	private long started;	// time this task was initiated
	private long delay;		// how long to delay the teleport
	private int health;
	// note that I initially stored a clone of the location for reference, but...
	// when comparing locations, I got incorrect mismatches (rounding errors, looked like)
	// so, the X/Y/Z values are stored instead and rounded off
	private long initX;
	private long initY;
	private long initZ;
	private Target teleportTarget;
	private String chargeFor;
	private Essentials ess;

	private void initTimer(long delay, Target target, String chargeFor)
	{
		this.started = System.currentTimeMillis();
		this.delay = delay;
		this.health = user.getHealth();
		this.initX = Math.round(user.getLocation().getX() * 10000);
		this.initY = Math.round(user.getLocation().getY() * 10000);
		this.initZ = Math.round(user.getLocation().getZ() * 10000);
		this.teleportTarget = target;
		this.chargeFor = chargeFor;
	}

	public void run()
	{

		if (user == null || !user.isOnline() || user.getLocation() == null)
		{
			cancel();
			return;
		}
		if (Math.round(user.getLocation().getX() * 10000) != initX
			|| Math.round(user.getLocation().getY() * 10000) != initY
			|| Math.round(user.getLocation().getZ() * 10000) != initZ
			|| user.getHealth() < health)
		{	// user moved, cancel teleport
			cancel(true);
			return;
		}

		health = user.getHealth();  // in case user healed, then later gets injured

		long now = System.currentTimeMillis();
		if (now > started + delay)
		{
			try
			{
				cooldown(false);
				user.sendMessage("§7Teleportation commencing...");
				try
				{
					if (chargeFor != null)
					{
						user.charge(chargeFor);
					}
					now(teleportTarget);
				}
				catch (Throwable ex)
				{
					user.sendMessage("§cError: " + ex.getMessage());
				}
				return;
			}
			catch (Exception ex)
			{
				user.sendMessage("§cCooldown: " + ex.getMessage());
			}
		}
	}

	public Teleport(User user, Essentials ess)
	{
		this.user = user;
		this.ess = ess;
	}

	public void respawn(Spawn spawn, String chargeFor) throws Exception
	{
		teleport(new Target(spawn.getSpawn(user.getGroup())), chargeFor);
	}

	public void warp(String warp, String chargeFor) throws Exception
	{
		Location loc = Essentials.getWarps().getWarp(warp);
		teleport(new Target(loc), chargeFor);
		user.sendMessage("§7Warping to " + warp + ".");
	}

	public void cooldown(boolean check) throws Exception
	{
		Calendar now = new GregorianCalendar();
		if (user.getLastTeleportTimestamp() > 0)
		{
			double cooldown = ess.getSettings().getTeleportCooldown();
			Calendar cooldownTime = new GregorianCalendar();
			cooldownTime.setTimeInMillis(user.getLastTeleportTimestamp());
			cooldownTime.add(Calendar.SECOND, (int)cooldown);
			cooldownTime.add(Calendar.MILLISECOND, (int)((cooldown * 1000.0) % 1000.0));
			if (cooldownTime.after(now) && !user.isAuthorized("essentials.teleport.cooldown.bypass"))
			{
				throw new Exception("Time before next teleport: " + Util.formatDateDiff(cooldownTime.getTimeInMillis()));
			}
		}
		// if justCheck is set, don't update lastTeleport; we're just checking
		if (!check)
		{
			user.setLastTeleportTimestamp(now.getTimeInMillis());
		}
	}

	public void cancel(boolean notifyUser)
	{
		if (teleTimer == -1)
		{
			return;
		}
		try
		{
			user.getServer().getScheduler().cancelTask(teleTimer);
			if (notifyUser)
			{
				user.sendMessage("§cPending teleportation request cancelled.");
			}
		}
		finally
		{
			teleTimer = -1;
		}
	}

	public void cancel()
	{
		cancel(false);
	}

	public void teleport(Location loc, String name) throws Exception
	{
		teleport(new Target(loc), chargeFor);
	}

	public void teleport(Entity entity, String name) throws Exception
	{
		teleport(new Target(entity), chargeFor);
	}

	private void teleport(Target target, String chargeFor) throws Exception
	{
		double delay = ess.getSettings().getTeleportDelay();

		cooldown(true);
		if (delay <= 0 || user.isAuthorized("essentials.teleport.timer.bypass"))
		{
			if (chargeFor != null)
			{
				user.charge(chargeFor);
			}
			now(target);
			return;
		}

		cancel();
		Calendar c = new GregorianCalendar();
		c.add(Calendar.SECOND, (int)delay);
		c.add(Calendar.MILLISECOND, (int)((delay * 1000.0) % 1000.0));
		user.sendMessage("§7Teleportation will commence in " + Util.formatDateDiff(c.getTimeInMillis()) + ". Don't move.");
		initTimer((long)(delay * 1000.0), target, chargeFor);

		teleTimer = user.getServer().getScheduler().scheduleSyncRepeatingTask(Essentials.getStatic(), this, 10, 10);
	}

	private void now(Target target) throws Exception
	{
		cancel();
		user.setLastLocation();
		user.getBase().teleport(Util.getSafeDestination(target.getLocation()));
	}

	public void now(Location loc) throws Exception
	{
		now(new Target(loc));
	}

	public void now(Entity entity) throws Exception
	{
		now(new Target(entity));
	}

	public void back(final String chargeFor) throws Exception
	{
		teleport(new Target(user.getLastLocation()), chargeFor);
	}

	public void back() throws Exception
	{
		back(null);
	}

	public void home(String chargeFor) throws Exception
	{
		home(user, chargeFor);
	}

	public void home(User user, String chargeFor) throws Exception
	{
		Location loc = user.getHome();
		if (loc == null)
		{
			if (ess.getSettings().spawnIfNoHome())
			{
				respawn(Essentials.getSpawn(), chargeFor);
			}
			else
			{
				throw new Exception(user == this.user ? "You have not set a home." : "Player has not set a home.");
			}
		}
		teleport(new Target(loc), chargeFor);
	}
}
