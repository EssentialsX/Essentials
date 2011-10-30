package com.earth2me.essentials.chat;

import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import java.util.Map;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerChatEvent;


public class EssentialsChatPlayerListenerHighest extends EssentialsChatPlayer
{
	public EssentialsChatPlayerListenerHighest(Server server, IEssentials ess, Map<String, IEssentialsChatListener> listeners)
	{
		super(server, ess, listeners);
	}

	@Override
	public void onPlayerChat(final PlayerChatEvent event)
	{
		if (isAborted(event))
		{
			return;
		}

		/**
		 * This file should handle charging the user for the action before returning control back
		 */
		final User user = ess.getUser(event.getPlayer());
		final String chatType = getChatType(event.getMessage());
		final StringBuilder command = new StringBuilder();
		command.append("chat");

		if (chatType.length() > 0)
		{
			command.append("-").append(chatType);
		}

		try
		{
			charge(user, command.toString());
		}
		catch (ChargeException e)
		{
			ess.showError(user, e, command.toString());
			event.setCancelled(true);
			return;
		}
	}
}
