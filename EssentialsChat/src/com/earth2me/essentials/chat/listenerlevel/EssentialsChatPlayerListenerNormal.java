package com.earth2me.essentials.chat.listenerlevel;

import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.chat.EssentialsChatPlayer;
import java.util.Map;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerChatEvent;


public class EssentialsChatPlayerListenerNormal extends EssentialsChatPlayer
{
	private final transient Map<PlayerChatEvent, String> charges;

	public EssentialsChatPlayerListenerNormal(final Server server,
											  final IEssentials ess,
											  final Map<PlayerChatEvent, String> charges)
	{
		super(server, ess);
		this.charges = charges;
	}

	@Override
	public void onPlayerChat(final PlayerChatEvent event)
	{
		if (isAborted(event))
		{
			return;
		}

		 handleLocalChat(charges, event);
	}
}
