package com.earth2me.essentials.xmpp;

import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.api.IUser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
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

	@EventHandler(priority= EventPriority.MONITOR)
	public void onPlayerJoin(final PlayerJoinEvent event)
	{
		final IUser user = ess.getUser(event.getPlayer());
		sendMessageToSpyUsers("Player " + user.getDisplayName() + " joined the game");
	}

	@EventHandler(priority= EventPriority.MONITOR)
	public void onPlayerChat(final PlayerChatEvent event)
	{
		final IUser user = ess.getUser(event.getPlayer());
		sendMessageToSpyUsers(String.format(event.getFormat(), user.getDisplayName(), event.getMessage()));
	}

	@EventHandler(priority= EventPriority.MONITOR)
	public void onPlayerQuit(final PlayerQuitEvent event)
	{
		final IUser user = ess.getUser(event.getPlayer());
		sendMessageToSpyUsers("Player " + user.getDisplayName() + " left the game");
	}

	private void sendMessageToSpyUsers(final String message)
	{
		try
		{
			for (String address : EssentialsXMPP.getInstance().getSpyUsers())
			{
				EssentialsXMPP.getInstance().sendMessage(address, message);
			}
		}
		catch (Exception ex)
		{
			// Ignore exceptions
		}
	}
}
