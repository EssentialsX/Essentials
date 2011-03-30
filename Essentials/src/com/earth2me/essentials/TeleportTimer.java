package com.earth2me.essentials;

import java.util.TimerTask;
import java.util.Calendar;


public abstract class TeleportTimer implements Runnable
{
	private long started;	// time this task was initiated
	private long delay;		// how long to delay the teleport
	public User user;		// the person doing the teleport
	private int health;
	// note that I initially stored a clone of the location for reference, but...
	// when comparing locations, I got incorrect mismatches (rounding errors, looked like)
	// so, the X/Y/Z values are stored instead and rounded off
	private long initX;
	private long initY;
	private long initZ;

	public TeleportTimer(User tUser, long tDelay)
	{
		this.started = Calendar.getInstance().getTimeInMillis();
		this.delay = tDelay;
		this.user = tUser;
		this.health = user.getHealth();
		this.initX = Math.round(user.getLocation().getX() * 10000);
		this.initY = Math.round(user.getLocation().getY() * 10000);
		this.initZ = Math.round(user.getLocation().getZ() * 10000);
	}

	// This function needs to be defined when creating a new TeleportTimer
	// The actual teleport command by itself should be stuck in there, such as teleportToNow(loc)
	public abstract void DoTeleport();
	
	public abstract void DoCancel();

	public void run()
	{
		if (user == null || !user.isOnline() || user.getLocation() == null)
		{
			DoCancel();
			return;
		}
		if (Math.round(user.getLocation().getX() * 10000) != initX
			|| Math.round(user.getLocation().getY() * 10000) != initY
			|| Math.round(user.getLocation().getZ() * 10000) != initZ
			|| user.getHealth() < health)
		{	// user moved, cancel teleport
			user.cancelTeleport(true);
			return;
		}

		health = user.getHealth();  // in case user healed, then later gets injured

		long now = Calendar.getInstance().getTimeInMillis();
		if (now > started + delay)
		{
			try
			{
				user.teleportCooldown(false);
				user.sendMessage("§7Teleportation commencing...");
				this.DoTeleport();
				return;
			}
			catch (Exception ex)
			{
				user.sendMessage("§cCooldown: " + ex.getMessage());
			}
		}
		//else  // uncomment for timing debug
		//	user.sendMessage("§7" + (started + delay - now));
	}
}
