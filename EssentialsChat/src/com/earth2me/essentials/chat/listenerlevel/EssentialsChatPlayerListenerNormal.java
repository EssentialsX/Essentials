package com.earth2me.essentials.chat.listenerlevel;

import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.chat.ChatStore;
import com.earth2me.essentials.chat.EssentialsChatPlayer;
import java.util.Map;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChatEvent;


public class EssentialsChatPlayerListenerNormal extends EssentialsChatPlayer
{
	public EssentialsChatPlayerListenerNormal(final Server server,
											  final IEssentials ess,
											  final Map<PlayerChatEvent, ChatStore> chatStorage)
	{
		super(server, ess, chatStorage);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	@Override
	public void onPlayerChat(final PlayerChatEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		final ChatStore chatStore = getChatStore(event);
		handleLocalChat(event, chatStore);
	}
}
