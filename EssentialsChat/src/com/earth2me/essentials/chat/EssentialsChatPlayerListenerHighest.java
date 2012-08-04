package com.earth2me.essentials.chat;

import com.earth2me.essentials.IEssentials;
import java.util.Map;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;


public class EssentialsChatPlayerListenerHighest extends EssentialsChatPlayer
{
	public EssentialsChatPlayerListenerHighest(final Server server,
											   final IEssentials ess,
											   final Map<String, IEssentialsChatListener> listeners,
											   final Map<AsyncPlayerChatEvent, ChatStore> chatStorage)
	{
		super(server, ess, listeners, chatStorage);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	@Override
	public void onPlayerChat(final AsyncPlayerChatEvent event)
	{
		final ChatStore chatStore = delChatStore(event);
		if (isAborted(event))
		{
			return;
		}

		/**
		 * This file should handle charging the user for the action before returning control back
		 */
		charge(event, chatStore);
	}
}
