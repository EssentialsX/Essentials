package com.earth2me.essentials.chat.listenerlevel;

import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.chat.EssentialsChatPlayer;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerChatEvent;


public class EssentialsChatPlayerListenerLowest extends EssentialsChatPlayer
{
	public EssentialsChatPlayerListenerLowest(final Server server, final IEssentials ess)
	{
		super(server, ess);
	}

	@Override
	public void onPlayerChat(final PlayerChatEvent event)
	{
		if (isAborted(event))
		{
			return;
		}

		formatChat(event);
	}
}