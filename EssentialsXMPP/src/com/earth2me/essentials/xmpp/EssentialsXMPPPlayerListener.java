package com.earth2me.essentials.xmpp;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;


class EssentialsXMPPPlayerListener extends PlayerListener
{
	private final transient IEssentials ess;

	EssentialsXMPPPlayerListener(final IEssentials ess)
	{
		super();
		this.ess = ess;
	}

	@Override
	public void onPlayerJoin(final PlayerJoinEvent event)
	{
		final User user = ess.getUser(event.getPlayer());
		sendMessageToSpyUsers("Player " + user.getDisplayName() + " joined the game");
	}

	@Override
	public void onPlayerChat(final PlayerChatEvent event)
	{
		final User user = ess.getUser(event.getPlayer());
		sendMessageToSpyUsers(String.format(event.getFormat(), user.getDisplayName(), event.getMessage()));
	}

	@Override
	public void onPlayerQuit(final PlayerQuitEvent event)
	{
		final User user = ess.getUser(event.getPlayer());
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
		}
	}
}
