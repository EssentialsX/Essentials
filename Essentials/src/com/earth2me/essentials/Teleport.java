package com.earth2me.essentials;

import com.earth2me.essentials.commands.NotEnoughArgumentsException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.entity.Entity;


public class Teleport implements Runnable
{
	private static final double MOVE_CONSTANT = 0.3;


	private static class Target
	{
		private final Location location;
		private final Entity entity;

		public Target(Location location)
		{
			this.location = location;
			this.entity = null;
		}

		public Target(Entity entity)
		{
			this.entity = entity;
			this.location = null;
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
	private IUser user;
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
	private Trade chargeFor;
	private final IEssentials ess;
	private static final Logger logger = Logger.getLogger("Minecraft");

	private void initTimer(long delay, Target target, Trade chargeFor)
	{
		this.started = System.currentTimeMillis();
		this.delay = delay;
		this.health = user.getHealth();
		this.initX = Math.round(user.getLocation().getX() * MOVE_CONSTANT);
		this.initY = Math.round(user.getLocation().getY() * MOVE_CONSTANT);
		this.initZ = Math.round(user.getLocation().getZ() * MOVE_CONSTANT);
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
		if (Math.round(user.getLocation().getX() * MOVE_CONSTANT) != initX
			|| Math.round(user.getLocation().getY() * MOVE_CONSTANT) != initY
			|| Math.round(user.getLocation().getZ() * MOVE_CONSTANT) != initZ
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
				user.sendMessage(Util.i18n("teleportationCommencing"));
				try
				{

					now(teleportTarget);
					if (chargeFor != null)
					{
						chargeFor.charge(user);
					}
				}
				catch (Throwable ex)
				{
					ess.showError(user.getBase(), ex, "teleport");
				}
				return;
			}
			catch (Exception ex)
			{
				user.sendMessage(Util.format("cooldownWithMessage", ex.getMessage()));
			}
		}
	}

	public Teleport(IUser user, IEssentials ess)
	{
		this.user = user;
		this.ess = ess;
	}

	public void respawn(Spawn spawn, Trade chargeFor) throws Exception
	{
		teleport(new Target(spawn.getSpawn(user.getGroup())), chargeFor);
	}

	public void warp(String warp, Trade chargeFor) throws Exception
	{
		Location loc = ess.getWarps().getWarp(warp);
		teleport(new Target(loc), chargeFor);
		user.sendMessage(Util.format("warpingTo", warp));
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
				throw new Exception(Util.format("timeBeforeTeleport", Util.formatDateDiff(cooldownTime.getTimeInMillis())));
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
			ess.getServer().getScheduler().cancelTask(teleTimer);
			if (notifyUser)
			{
				user.sendMessage(Util.i18n("pendingTeleportCancelled"));
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

	public void teleport(Location loc, Trade chargeFor) throws Exception
	{
		teleport(new Target(loc), chargeFor);
	}

	public void teleport(Entity entity, Trade chargeFor) throws Exception
	{
		teleport(new Target(entity), chargeFor);
	}

	private void teleport(Target target, Trade chargeFor) throws Exception
	{
		double delay = ess.getSettings().getTeleportDelay();

		if (chargeFor != null)
		{
			chargeFor.isAffordableFor(user);
		}
		cooldown(true);
		if (delay <= 0 || user.isAuthorized("essentials.teleport.timer.bypass"))
		{
			cooldown(false);
			now(target);
			if (chargeFor != null)
			{
				chargeFor.charge(user);
			}
			return;
		}

		cancel();
		Calendar c = new GregorianCalendar();
		c.add(Calendar.SECOND, (int)delay);
		c.add(Calendar.MILLISECOND, (int)((delay * 1000.0) % 1000.0));
		user.sendMessage(Util.format("dontMoveMessage", Util.formatDateDiff(c.getTimeInMillis())));
		initTimer((long)(delay * 1000.0), target, chargeFor);

		teleTimer = ess.scheduleSyncRepeatingTask(this, 10, 10);
	}

	private void now(Target target) throws Exception
	{
		cancel();
		user.setLastLocation();
		user.getBase().teleport(Util.getSafeDestination(target.getLocation()));
	}

	public void now(Location loc) throws Exception
	{
		cooldown(false);
		now(new Target(loc));
	}

	public void now(Location loc, Trade chargeFor) throws Exception
	{
		cooldown(false);
		chargeFor.charge(user);
		now(new Target(loc));
	}

	public void now(Entity entity, boolean cooldown) throws Exception
	{
		if (cooldown)
		{
			cooldown(false);
		}
		now(new Target(entity));
	}

	public void back(Trade chargeFor) throws Exception
	{
		teleport(new Target(user.getLastLocation()), chargeFor);
	}

	public void back() throws Exception
	{
		now(new Target(user.getLastLocation()));
	}

	public void home(IUser user, String home, Trade chargeFor) throws Exception
	{
		final Location loc = user.getHome(home);
		if (loc == null)
		{
			throw new NotEnoughArgumentsException();
		}
		teleport(new Target(loc), chargeFor);
	}
}
