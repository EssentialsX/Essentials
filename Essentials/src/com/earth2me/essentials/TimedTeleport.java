package com.earth2me.essentials;

import net.ess3.api.IEssentials;
import net.ess3.api.IUser;
import static com.earth2me.essentials.I18n._;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;


public class TimedTeleport implements Runnable
{
	private static final double MOVE_CONSTANT = 0.3;
	private final IUser teleportOwner;
	private final IEssentials ess;
	private final Teleport teleport;
	private String timer_teleportee;
	private int timer_task = -1;
	private long timer_started;	// time this task was initiated
	private long timer_delay;		// how long to delay the teleportPlayer
	private double timer_health;
	// note that I initially stored a clone of the location for reference, but...
	// when comparing locations, I got incorrect mismatches (rounding errors, looked like)
	// so, the X/Y/Z values are stored instead and rounded off
	private long timer_initX;
	private long timer_initY;
	private long timer_initZ;
	private ITarget timer_teleportTarget;
	private boolean timer_respawn;
	private boolean timer_canMove;
	private Trade timer_chargeFor;
	private TeleportCause timer_cause;

	public TimedTeleport(IUser user, IEssentials ess, Teleport teleport, long delay, IUser teleportUser, ITarget target, Trade chargeFor, TeleportCause cause, boolean respawn)
	{

		this.teleportOwner = user;
		this.ess = ess;
		this.teleport = teleport;
		this.timer_started = System.currentTimeMillis();
		this.timer_delay = delay;
		this.timer_health = teleportUser.getBase().getHealth();
		this.timer_initX = Math.round(teleportUser.getBase().getLocation().getX() * MOVE_CONSTANT);
		this.timer_initY = Math.round(teleportUser.getBase().getLocation().getY() * MOVE_CONSTANT);
		this.timer_initZ = Math.round(teleportUser.getBase().getLocation().getZ() * MOVE_CONSTANT);
		this.timer_teleportee = teleportUser.getName();
		this.timer_teleportTarget = target;
		this.timer_chargeFor = chargeFor;
		this.timer_cause = cause;
		this.timer_respawn = respawn;
		this.timer_canMove = user.isAuthorized("essentials.teleport.timer.move");

		timer_task = ess.scheduleSyncRepeatingTask(this, 20, 20);
	}

	@Override
	public void run()
	{

		if (teleportOwner == null || !teleportOwner.getBase().isOnline() || teleportOwner.getBase().getLocation() == null)
		{
			cancelTimer(false);
			return;
		}

		IUser teleportUser = ess.getUser(this.timer_teleportee);

		if (teleportUser == null || !teleportUser.getBase().isOnline())
		{
			cancelTimer(false);
			return;
		}

		final Location currLocation = teleportUser.getBase().getLocation();
		if (currLocation == null)
		{
			cancelTimer(false);
			return;
		}

		if (!timer_canMove
			&& (Math.round(currLocation.getX() * MOVE_CONSTANT) != timer_initX
				|| Math.round(currLocation.getY() * MOVE_CONSTANT) != timer_initY
				|| Math.round(currLocation.getZ() * MOVE_CONSTANT) != timer_initZ
				|| teleportUser.getBase().getHealth() < timer_health))
		{
			// user moved, cancelTimer teleportPlayer
			cancelTimer(true);
			return;
		}

		timer_health = teleportUser.getBase().getHealth();  // in case user healed, then later gets injured
		final long now = System.currentTimeMillis();
		if (now > timer_started + timer_delay)
		{
			try
			{
				teleport.cooldown(false);
				teleportUser.sendMessage(_("teleportationCommencing"));
				try
				{
					if (timer_respawn)
					{
						teleport.respawnNow(teleportUser, timer_cause);
					}
					else
					{
						teleport.now(teleportUser, timer_teleportTarget, timer_cause);
					}
					cancelTimer(false);
					if (timer_chargeFor != null)
					{
						timer_chargeFor.charge(teleportOwner);
					}
				}
				catch (Throwable ex)
				{
					ess.showError(teleportOwner.getBase(), ex, "teleport");
				}
			}
			catch (Exception ex)
			{
				teleportOwner.sendMessage(_("cooldownWithMessage", ex.getMessage()));
				if (teleportOwner != teleportUser)
				{
					teleportUser.sendMessage(_("cooldownWithMessage", ex.getMessage()));
				}
			}
		}
	}

	//If we need to cancelTimer a pending teleportPlayer call this method
	public void cancelTimer(boolean notifyUser)
	{
		if (timer_task == -1)
		{
			return;
		}
		try
		{
			ess.getServer().getScheduler().cancelTask(timer_task);
			if (notifyUser)
			{
				teleportOwner.sendMessage(_("pendingTeleportCancelled"));
				if (timer_teleportee != null && !timer_teleportee.equals(teleportOwner.getName()))
				{
					ess.getUser(timer_teleportee).sendMessage(_("pendingTeleportCancelled"));
				}
			}
		}
		finally
		{
			timer_task = -1;
		}
	}
}
