package com.earth2me.essentials;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.user.UserData.TimestampType;
import org.bukkit.entity.Player;


public class EssentialsTimer implements Runnable
{
	private final transient IEssentials ess;
	private final transient Set<IUser> onlineUsers = new HashSet<IUser>();

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
			final IUser user = ess.getUser(player);
			onlineUsers.add(user);
			user.setLastOnlineActivity(currentTime);
			user.checkActivity();
		}

		final Iterator<IUser> iterator = onlineUsers.iterator();
		while (iterator.hasNext())
		{
			final IUser user = iterator.next();
			if (user.getLastOnlineActivity() < currentTime && user.getLastOnlineActivity() > user.getTimestamp(TimestampType.LOGOUT))
			{
				user.setTimestamp(TimestampType.LOGOUT, user.getLastOnlineActivity());
				iterator.remove();
				continue;
			}
			user.checkMuteTimeout(currentTime);
			user.checkJailTimeout(currentTime);
		}
	}
}
