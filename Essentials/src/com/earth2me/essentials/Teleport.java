package com.earth2me.essentials;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.ITeleport;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;


public class Teleport implements Runnable, ITeleport
{
	private static final double MOVE_CONSTANT = 0.3;


	private class Target
	{
		private final Location location;
		private final String name;

		Target(Location location)
		{
			this.location = location;
			this.name = null;
		}

		Target(Player entity)
		{
			this.name = entity.getName();
			this.location = null;
		}

		public Location getLocation()
		{
			if (this.name != null)
			{

				return ess.getServer().getPlayerExact(name).getLocation();
			}
			return location;
		}
	}
	private IUser user;
	private IUser teleportUser;
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
	private TeleportCause cause;

	private void initTimer(long delay, Target target, Trade chargeFor, TeleportCause cause)
	{
		initTimer(delay, user, target, chargeFor, cause);
	}

	private void initTimer(long delay, IUser teleportUser, Target target, Trade chargeFor, TeleportCause cause)
	{
		this.started = System.currentTimeMillis();
		this.delay = delay;
		this.health = teleportUser.getHealth();
		this.initX = Math.round(teleportUser.getLocation().getX() * MOVE_CONSTANT);
		this.initY = Math.round(teleportUser.getLocation().getY() * MOVE_CONSTANT);
		this.initZ = Math.round(teleportUser.getLocation().getZ() * MOVE_CONSTANT);
		this.teleportUser = teleportUser;
		this.teleportTarget = target;
		this.chargeFor = chargeFor;
		this.cause = cause;
	}

	@Override
	public void run()
	{

		if (user == null || !user.isOnline() || user.getLocation() == null)
		{
			cancel(false);
			return;
		}
		if (teleportUser == null || !teleportUser.isOnline() || teleportUser.getLocation() == null)
		{
			cancel(false);
			return;
		}

		if (Math.round(teleportUser.getLocation().getX() * MOVE_CONSTANT) != initX
			|| Math.round(teleportUser.getLocation().getY() * MOVE_CONSTANT) != initY
			|| Math.round(teleportUser.getLocation().getZ() * MOVE_CONSTANT) != initZ
			|| teleportUser.getHealth() < health)
		{	// user moved, cancel teleport
			cancel(true);
			return;
		}

		health = teleportUser.getHealth();  // in case user healed, then later gets injured

		long now = System.currentTimeMillis();
		if (now > started + delay)
		{
			try
			{
				cooldown(false);
				teleportUser.sendMessage(_("teleportationCommencing"));
				try
				{

					teleportUser.getTeleport().now(teleportTarget, cause);
					cancel(false);
					if (chargeFor != null)
					{
						chargeFor.charge(user);
					}
				}
				catch (Throwable ex)
				{
					ess.showError(user.getBase(), ex, "teleport");
				}
			}
			catch (Exception ex)
			{
				user.sendMessage(_("cooldownWithMessage", ex.getMessage()));
				if (user != teleportUser)
				{
					teleportUser.sendMessage(_("cooldownWithMessage", ex.getMessage()));
				}
			}
		}
	}

	public Teleport(IUser user, IEssentials ess)
	{
		this.user = user;
		this.ess = ess;
	}

	public void cooldown(boolean check) throws Exception
	{
		final Calendar time = new GregorianCalendar();
		if (user.getLastTeleportTimestamp() > 0)
		{
			// Take the current time, and remove the delay from it.
			final double cooldown = ess.getSettings().getTeleportCooldown();
			final Calendar earliestTime = new GregorianCalendar();
			earliestTime.add(Calendar.SECOND, -(int)cooldown);
			earliestTime.add(Calendar.MILLISECOND, -(int)((cooldown * 1000.0) % 1000.0));
			// This value contains the most recent time a teleport could have been used that would allow another use.
			final long earliestLong = earliestTime.getTimeInMillis();

			// When was the last teleport used?
			final Long lastTime = user.getLastTeleportTimestamp();

			if (lastTime > time.getTimeInMillis())
			{
				// This is to make sure time didn't get messed up on last kit use.
				// If this happens, let's give the user the benifit of the doubt.
				user.setLastTeleportTimestamp(time.getTimeInMillis());
				return;
			}
			else if (lastTime > earliestLong && !user.isAuthorized("essentials.teleport.cooldown.bypass"))
			{
				time.setTimeInMillis(lastTime);
				time.add(Calendar.SECOND, (int)cooldown);
				time.add(Calendar.MILLISECOND, (int)((cooldown * 1000.0) % 1000.0));
				throw new Exception(_("timeBeforeTeleport", Util.formatDateDiff(time.getTimeInMillis())));
			}
		}
		// if justCheck is set, don't update lastTeleport; we're just checking
		if (!check)
		{
			user.setLastTeleportTimestamp(time.getTimeInMillis());
		}
	}

	//If we need to cancel a pending teleport call this method
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
				user.sendMessage(_("pendingTeleportCancelled"));
				if (teleportUser != user)
				{
					teleportUser.sendMessage(_("pendingTeleportCancelled"));
				}
			}
		}
		finally
		{
			teleTimer = -1;
		}
	}

	//The teleport function is used when you want to normally teleport someone to a location or player.
	public void teleport(Location loc, Trade chargeFor, TeleportCause cause) throws Exception
	{
		teleport(new Target(loc), chargeFor, cause);
	}

	public void teleport(Player entity, Trade chargeFor, TeleportCause cause) throws Exception
	{
		teleport(new Target(entity), chargeFor, cause);
	}

	private void teleport(Target target, Trade chargeFor, TeleportCause cause) throws Exception
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
			now(target, cause);
			if (chargeFor != null)
			{
				chargeFor.charge(user);
			}
			return;
		}

		cancel(false);
		Calendar c = new GregorianCalendar();
		c.add(Calendar.SECOND, (int)delay);
		c.add(Calendar.MILLISECOND, (int)((delay * 1000.0) % 1000.0));
		user.sendMessage(_("dontMoveMessage", Util.formatDateDiff(c.getTimeInMillis())));
		initTimer((long)(delay * 1000.0), target, chargeFor, cause);

		teleTimer = ess.scheduleSyncRepeatingTask(this, 10, 10);
	}

	//The now function is used when you want to skip tp delay when teleporting someone to a location or player.
	public void now(Location loc, boolean cooldown, TeleportCause cause) throws Exception
	{
		if (cooldown)
		{
			cooldown(false);
		}
		now(new Target(loc), cause);
	}

	public void now(Player entity, boolean cooldown, TeleportCause cause) throws Exception
	{
		if (cooldown)
		{
			cooldown(false);
		}
		now(new Target(entity), cause);
	}

	private void now(Target target, TeleportCause cause) throws Exception
	{
		cancel(false);
		user.setLastLocation();
		user.getBase().teleport(Util.getSafeDestination(target.getLocation()), cause);
	}

	//The teleportToMe function is a wrapper used to handle teleporting players to them, like /tphere
	public void teleportToMe(User otherUser, Trade chargeFor, TeleportCause cause) throws Exception
	{
		Target target = new Target(user);

		double delay = ess.getSettings().getTeleportDelay();

		if (chargeFor != null)
		{
			chargeFor.isAffordableFor(user);
		}
		cooldown(true);
		if (delay <= 0 || user.isAuthorized("essentials.teleport.timer.bypass"))
		{
			cooldown(false);
			otherUser.getTeleport().now(target, cause);
			if (chargeFor != null)
			{
				chargeFor.charge(user);
			}
			return;
		}

		cancel(false);
		Calendar c = new GregorianCalendar();
		c.add(Calendar.SECOND, (int)delay);
		c.add(Calendar.MILLISECOND, (int)((delay * 1000.0) % 1000.0));
		otherUser.sendMessage(_("dontMoveMessage", Util.formatDateDiff(c.getTimeInMillis())));
		initTimer((long)(delay * 1000.0), otherUser, target, chargeFor, cause);

		teleTimer = ess.scheduleSyncRepeatingTask(this, 10, 10);
	}

	//The respawn function is a wrapper used to handle tp fallback, on /jail and /home
	public void respawn(final Trade chargeFor, TeleportCause cause) throws Exception
	{
		final Player player = user.getBase();
		final Location bed = player.getBedSpawnLocation();
		final PlayerRespawnEvent pre = new PlayerRespawnEvent(player, bed == null ? player.getWorld().getSpawnLocation() : bed, bed != null);
		ess.getServer().getPluginManager().callEvent(pre);
		teleport(new Target(pre.getRespawnLocation()), chargeFor, cause);
	}

	//The warp function is a wrapper used to teleport a player to a /warp
	public void warp(String warp, Trade chargeFor, TeleportCause cause) throws Exception
	{
		Location loc = ess.getWarps().getWarp(warp);
		teleport(new Target(loc), chargeFor, cause);
		user.sendMessage(_("warpingTo", warp));
	}

	//The back function is a wrapper used to teleport a player /back to their previous location.	
	public void back(Trade chargeFor) throws Exception
	{
		teleport(new Target(user.getLastLocation()), chargeFor, TeleportCause.COMMAND);
	}

	//This function is used to throw a user back after a jail sentence
	public void back() throws Exception
	{
		now(new Target(user.getLastLocation()), TeleportCause.COMMAND);
	}

	//This function handles teleporting to /home
	public void home(Location loc, Trade chargeFor) throws Exception
	{
		teleport(new Target(loc), chargeFor, TeleportCause.COMMAND);
	}
}
