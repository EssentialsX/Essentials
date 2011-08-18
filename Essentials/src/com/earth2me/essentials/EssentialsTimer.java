package com.earth2me.essentials;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.bukkit.entity.Player;


public class EssentialsTimer implements Runnable
{
	private final transient IEssentials ess;
	private final transient Set<User> onlineUsers = new HashSet<User>();

	EssentialsTimer(final IEssentials ess)
	{
		this.ess = ess;
	}

	@Override
	public void run()
	{
		final long currentTime = System.currentTimeMillis();
		for (Player player : ess.getServer().getOnlinePlayers())
		{
			final User user = ess.getUser(player);
			onlineUsers.add(user);
			user.setLastActivity(currentTime);
		}

		final Iterator<User> iterator = onlineUsers.iterator();
		while (iterator.hasNext())
		{
			final User user = iterator.next();
			if (user.getLastActivity() < currentTime && user.getLastActivity() > user.getLastLogout())
			{
				user.setLastLogout(user.getLastActivity());
				iterator.remove();
				continue;
			}
			user.checkMuteTimeout(currentTime);
			user.checkJailTimeout(currentTime);
		}
	}
}
