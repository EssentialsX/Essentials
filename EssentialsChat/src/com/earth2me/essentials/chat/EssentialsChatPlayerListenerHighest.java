package com.earth2me.essentials.chat;

import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.api.IUser;
import java.util.Map;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerChatEvent;


public class EssentialsChatPlayerListenerHighest extends EssentialsChatPlayer
{
	private final transient Map<PlayerChatEvent, String> charges;

	public EssentialsChatPlayerListenerHighest(final Server server,
											   final IEssentials ess,
											   final Map<String, IEssentialsChatListener> listeners,
											   final Map<PlayerChatEvent, String> charges)
	{
		super(server, ess, listeners);
		this.charges = charges;
	}

	@Override
	public void onPlayerChat(final PlayerChatEvent event)
	{
		String charge = charges.remove(event);
		if (charge == null)
		{
			charge = "chat";
		}
		if (isAborted(event))
		{
			return;
		}

		/**
		 * This file should handle charging the user for the action before returning control back
		 */
		final IUser user = ess.getUser(event.getPlayer());

		try
		{
			charge(user, charge);
		}
		catch (ChargeException e)
		{
			ess.getCommandHandler().showCommandError(user, charge, e);
			event.setCancelled(true);
			return;
		}
	}
}
