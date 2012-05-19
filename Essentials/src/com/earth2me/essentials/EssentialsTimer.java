package com.earth2me.essentials;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.entity.Player;


public class EssentialsTimer implements Runnable
{
	private final transient IEssentials ess;
	private final transient Set<User> onlineUsers = new HashSet<User>();
	private transient long lastPoll = System.currentTimeMillis() - 3000;
	private final transient LinkedList<Float> history = new LinkedList<Float>();

	EssentialsTimer(final IEssentials ess)
	{
		this.ess = ess;
	}

	@Override
	public void run()
	{
		final long currentTime = System.currentTimeMillis();
		long timeSpent = (currentTime - lastPoll) / 1000;
		if (timeSpent == 0)
		{
			timeSpent = 1;
		}
		if (history.size() > 10)
		{
			history.remove();
		}
		float tps = 100f / timeSpent;
		if (tps <= 20)
		{
			history.add(tps);
		}
		lastPoll = currentTime;
		for (Player player : ess.getServer().getOnlinePlayers())
		{
			try
			{
				final User user = ess.getUser(player);
				onlineUsers.add(user);
				user.setLastOnlineActivity(currentTime);
				user.checkActivity();
			}
			catch (Exception e)
			{
				ess.getLogger().log(Level.WARNING, "EssentialsTimer Error:", e);
			}
		}

		final Iterator<User> iterator = onlineUsers.iterator();
		while (iterator.hasNext())
		{
			final User user = iterator.next();
			if (user.getLastOnlineActivity() < currentTime && user.getLastOnlineActivity() > user.getLastLogout())
			{
				user.setLastLogout(user.getLastOnlineActivity());
				iterator.remove();
				continue;
			}
			user.checkMuteTimeout(currentTime);
			user.checkJailTimeout(currentTime);
			user.resetInvulnerabilityAfterTeleport();
		}
	}

	public float getAverageTPS()
	{
		float avg = 0;
		for (Float f : history)
		{
			if (f != null)
			{
				avg += f;
			}
		}
		return avg / history.size();
	}
}
