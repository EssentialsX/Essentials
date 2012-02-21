package com.earth2me.essentials;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.api.ISettings;
import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.perm.Permissions;
import com.earth2me.essentials.user.UserData.TimestampType;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
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

			try
			{
				final IUser user = ess.getUser(player);
				onlineUsers.add(user);
				user.setLastOnlineActivity(currentTime);
				user.checkActivity();

				boolean mailDisabled = false;
				ISettings settings = ess.getSettings();
				settings.acquireReadLock();
				try
				{
					mailDisabled = settings.getData().getCommands().isDisabled("mail");
				}
				finally
				{
					settings.unlock();
				}
				// New mail notification
				if (user != null && !mailDisabled && Permissions.MAIL.isAuthorized(user) && !user.gotMailInfo())
				{
					final List<String> mail = user.getMails();
					if (mail != null && !mail.isEmpty())
					{
						user.sendMessage(_("youHaveNewMail", mail.size()));
					}
				}
			}
			catch (Exception e)
			{
				ess.getLogger().log(Level.WARNING, "EssentialsTimer Error:", e);
			}
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
