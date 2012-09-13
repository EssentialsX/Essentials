package com.earth2me.essentials.xmpp;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;


class EssentialsXMPPPlayerListener implements Listener
{
	private final transient IEssentials ess;

	EssentialsXMPPPlayerListener(final IEssentials ess)
	{
		super();
		this.ess = ess;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(final PlayerJoinEvent event)
	{
		final User user = ess.getUser(event.getPlayer());
		sendMessageToSpyUsers("Player " + user.getDisplayName() + " joined the game");
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerChat(final AsyncPlayerChatEvent event)
	{
		final User user = ess.getUser(event.getPlayer());
		sendMessageToSpyUsers(String.format(event.getFormat(), user.getDisplayName(), event.getMessage()));
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(final PlayerQuitEvent event)
	{
		final User user = ess.getUser(event.getPlayer());
		sendMessageToSpyUsers("Player " + user.getDisplayName() + " left the game");
	}

	private void sendMessageToSpyUsers(final String message)
	{
		try
		{
			List<String> users = EssentialsXMPP.getInstance().getSpyUsers();
			synchronized (users)
			{
				for (final String address : users)
				{
					Bukkit.getScheduler().scheduleSyncDelayedTask(ess, new Runnable()
					{
						@Override
						public void run()
						{
							EssentialsXMPP.getInstance().sendMessage(address, message);
						}

					});

				}
			}
		}
		catch (Exception ex)
		{
			// Ignore exceptions
		}
	}
}
