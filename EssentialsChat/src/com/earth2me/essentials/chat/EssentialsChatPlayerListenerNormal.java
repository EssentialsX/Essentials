package com.earth2me.essentials.chat;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import java.util.Locale;
import java.util.Map;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;


public class EssentialsChatPlayerListenerNormal extends EssentialsChatPlayer
{
	public EssentialsChatPlayerListenerNormal(final Server server,
											  final IEssentials ess,
											  final Map<String, IEssentialsChatListener> listeners,
											  final Map<AsyncPlayerChatEvent, ChatStore> chatStorage)
	{
		super(server, ess, listeners, chatStorage);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	@Override
	public void onPlayerChat(final AsyncPlayerChatEvent event)
	{
		if (isAborted(event))
		{
			return;
		}

		/**
		 * This file should handle detection of the local chat features... if local chat is enabled, we need to handle
		 * it here
		 */		
		long radius = ess.getSettings().getChatRadius();
		if (radius < 1)
		{
			return;
		}
		radius *= radius;
		
		final ChatStore chatStore = getChatStore(event);
		final User user = chatStore.getUser();
		chatStore.setRadius(radius);
		
		if (event.getMessage().length() > 1 && chatStore.getType().length() > 0)
		{
			final StringBuilder permission = new StringBuilder();
			permission.append("essentials.chat.").append(chatStore.getType());
			
			if (user.isAuthorized(permission.toString()))
			{
				final StringBuilder format = new StringBuilder();
				format.append(chatStore.getType()).append("Format");
				event.setMessage(event.getMessage().substring(1));
				event.setFormat(_(format.toString(), event.getFormat()));				
				return;
			}
			
			final StringBuilder errorMsg = new StringBuilder();
			errorMsg.append("notAllowedTo").append(chatStore.getType().substring(0, 1).toUpperCase(Locale.ENGLISH)).append(chatStore.getType().substring(1));
			
			user.sendMessage(_(errorMsg.toString()));
			event.setCancelled(true);
			return;
		}
		
		sendLocalChat(event, chatStore);
	}
}
