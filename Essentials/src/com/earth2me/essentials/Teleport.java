package com.earth2me.essentials;

import net.ess3.api.IEssentials;
import net.ess3.api.IUser;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.utils.DateUtil;
import com.earth2me.essentials.utils.LocationUtil;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;


public class Teleport implements net.ess3.api.ITeleport
{
	private final IUser teleportOwner;
	private final IEssentials ess;
	private TimedTeleport timedTeleport;

	public Teleport(IUser user, IEssentials ess)
	{
		this.teleportOwner = user;
		this.ess = ess;
	}

	public void cooldown(boolean check) throws Exception
	{
		final Calendar time = new GregorianCalendar();
		if (teleportOwner.getLastTeleportTimestamp() > 0)
		{
			// Take the current time, and remove the delay from it.
			final double cooldown = ess.getSettings().getTeleportCooldown();
			final Calendar earliestTime = new GregorianCalendar();
			earliestTime.add(Calendar.SECOND, -(int)cooldown);
			earliestTime.add(Calendar.MILLISECOND, -(int)((cooldown * 1000.0) % 1000.0));
			// This value contains the most recent time a teleportPlayer could have been used that would allow another use.
			final long earliestLong = earliestTime.getTimeInMillis();

			// When was the last teleportPlayer used?
			final Long lastTime = teleportOwner.getLastTeleportTimestamp();

			if (lastTime > time.getTimeInMillis())
			{
				// This is to make sure time didn't get messed up on last teleportPlayer use.
				// If this happens, let's give the user the benifit of the doubt.
				teleportOwner.setLastTeleportTimestamp(time.getTimeInMillis());
				return;
			}
			else if (lastTime > earliestLong && !teleportOwner.isAuthorized("essentials.teleport.cooldown.bypass"))
			{
				time.setTimeInMillis(lastTime);
				time.add(Calendar.SECOND, (int)cooldown);
				time.add(Calendar.MILLISECOND, (int)((cooldown * 1000.0) % 1000.0));
				throw new Exception(_("timeBeforeTeleport", DateUtil.formatDateDiff(time.getTimeInMillis())));
			}
		}
		// if justCheck is set, don't update lastTeleport; we're just checking
		if (!check)
		{
			teleportOwner.setLastTeleportTimestamp(time.getTimeInMillis());
		}
	}

	private void warnUser(final IUser user, final double delay)
	{
		Calendar c = new GregorianCalendar();
		c.add(Calendar.SECOND, (int)delay);
		c.add(Calendar.MILLISECOND, (int)((delay * 1000.0) % 1000.0));
		user.sendMessage(_("dontMoveMessage", DateUtil.formatDateDiff(c.getTimeInMillis())));
	}

	//The now function is used when you want to skip tp delay when teleporting someone to a location or player.
	@Override
	public void now(Location loc, boolean cooldown, TeleportCause cause) throws Exception
	{
		if (cooldown)
		{
			cooldown(false);
		}
		now(teleportOwner, new LocationTarget(loc), cause);
	}

	@Override
	public void now(Player entity, boolean cooldown, TeleportCause cause) throws Exception
	{
		if (cooldown)
		{
			cooldown(false);
		}
		now(teleportOwner, new PlayerTarget(entity), cause);
	}

	protected void now(IUser teleportee, ITarget target, TeleportCause cause) throws Exception
	{
		cancel(false);
		teleportee.setLastLocation();
		teleportee.getBase().teleport(LocationUtil.getSafeDestination(target.getLocation()), cause);
	}

	//The teleportPlayer function is used when you want to normally teleportPlayer someone to a location or player.
	//This method is nolonger used internally and will be removed.
	@Deprecated
	public void teleport(Location loc, Trade chargeFor) throws Exception
	{
		teleport(loc, chargeFor, TeleportCause.PLUGIN);
	}

	@Override
	public void teleport(Location loc, Trade chargeFor, TeleportCause cause) throws Exception
	{
		teleport(teleportOwner, new LocationTarget(loc), chargeFor, cause);
	}

	@Override
	public void teleport(Player entity, Trade chargeFor, TeleportCause cause) throws Exception
	{
		teleport(teleportOwner, new PlayerTarget(entity), chargeFor, cause);
	}

	@Override
	public void teleportPlayer(IUser teleportee, Location loc, Trade chargeFor, TeleportCause cause) throws Exception
	{
		teleport(teleportee, new LocationTarget(loc), chargeFor, cause);
	}

	@Override
	public void teleportPlayer(IUser teleportee, Player entity, Trade chargeFor, TeleportCause cause) throws Exception
	{
		teleport(teleportee, new PlayerTarget(entity), chargeFor, cause);
	}

	private void teleport(IUser teleportee, ITarget target, Trade chargeFor, TeleportCause cause) throws Exception
	{
		double delay = ess.getSettings().getTeleportDelay();

		if (chargeFor != null)
		{
			chargeFor.isAffordableFor(teleportOwner);
		}
		cooldown(true);
		if (delay <= 0 || teleportOwner.isAuthorized("essentials.teleport.timer.bypass")
			|| teleportee.isAuthorized("essentials.teleport.timer.bypass"))
		{
			cooldown(false);
			now(teleportee, target, cause);
			if (chargeFor != null)
			{
				chargeFor.charge(teleportOwner);
			}
			return;
		}

		cancel(false);
		warnUser(teleportee, delay);
		initTimer((long)(delay * 1000.0), teleportee, target, chargeFor, cause, false);
	}

	//The teleportToMe function is a wrapper used to handle teleporting players to them, like /tphere
	@Override
	public void teleportToMe(IUser otherUser, Trade chargeFor, TeleportCause cause) throws Exception
	{
		ITarget target = new PlayerTarget(teleportOwner.getBase());
		teleport(otherUser, target, chargeFor, cause);
	}

	//The respawn function is a wrapper used to handle tp fallback, on /jail and /home
	@Override
	public void respawn(final Trade chargeFor, TeleportCause cause) throws Exception
	{
		double delay = ess.getSettings().getTeleportDelay();
		if (chargeFor != null)
		{
			chargeFor.isAffordableFor(teleportOwner);
		}
		cooldown(true);
		if (delay <= 0 || teleportOwner.isAuthorized("essentials.teleport.timer.bypass"))
		{
			cooldown(false);
			respawnNow(teleportOwner, cause);
			if (chargeFor != null)
			{
				chargeFor.charge(teleportOwner);
			}
			return;
		}

		cancel(false);
		warnUser(teleportOwner, delay);
		initTimer((long)(delay * 1000.0), teleportOwner, null, chargeFor, cause, true);
	}

	protected void respawnNow(IUser teleportee, TeleportCause cause) throws Exception
	{
		final Player player = teleportee.getBase();
		Location bed = player.getBedSpawnLocation();
		if (bed != null)
		{
			now(teleportee, new LocationTarget(bed), cause);
		}
		else
		{
			if (ess.getSettings().isDebug())
			{
				ess.getLogger().info("Could not find bed spawn, forcing respawn event.");
			}
			final PlayerRespawnEvent pre = new PlayerRespawnEvent(player, player.getWorld().getSpawnLocation(), false);
			ess.getServer().getPluginManager().callEvent(pre);
			now(teleportee, new LocationTarget(pre.getRespawnLocation()), cause);
		}
	}

	//The warp function is a wrapper used to teleportPlayer a player to a /warp
	@Override
	public void warp(IUser teleportee, String warp, Trade chargeFor, TeleportCause cause) throws Exception
	{
		Location loc = ess.getWarps().getWarp(warp);
		teleportee.sendMessage(_("warpingTo", warp));
		teleport(teleportee, new LocationTarget(loc), chargeFor, cause);
	}

	//The back function is a wrapper used to teleportPlayer a player /back to their previous location.
	@Override
	public void back(Trade chargeFor) throws Exception
	{
		teleport(teleportOwner, new LocationTarget(teleportOwner.getLastLocation()), chargeFor, TeleportCause.COMMAND);
	}

	//This function is used to throw a user back after a jail sentence
	@Override
	public void back() throws Exception
	{
		now(teleportOwner, new LocationTarget(teleportOwner.getLastLocation()), TeleportCause.COMMAND);
	}

	//This function handles teleporting to /home
	@Override
	public void home(Location loc, Trade chargeFor) throws Exception
	{
		teleport(teleportOwner, new LocationTarget(loc), chargeFor, TeleportCause.COMMAND);
	}

	//If we need to cancelTimer a pending teleportPlayer call this method
	private void cancel(boolean notifyUser)
	{
		if (timedTeleport != null)
		{
			timedTeleport.cancelTimer(notifyUser);
			timedTeleport = null;
		}
	}

	private void initTimer(long delay, IUser teleportUser, ITarget target, Trade chargeFor, TeleportCause cause, boolean respawn)
	{
		timedTeleport = new TimedTeleport(teleportOwner, ess, this, delay, teleportUser, target, chargeFor, cause, respawn);
	}
}
