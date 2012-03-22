package com.earth2me.essentials.chat;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.util.Locale;
import java.util.Map;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChatEvent;


public class EssentialsChatPlayerListenerLowest extends EssentialsChatPlayer
{
	public EssentialsChatPlayerListenerLowest(final Server server,
											  final IEssentials ess,
											  final Map<String, IEssentialsChatListener> listeners,
											  final Map<PlayerChatEvent, ChatStore> chatStorage)
	{
		super(server, ess, listeners, chatStorage);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	@Override
	public void onPlayerChat(final PlayerChatEvent event)
	{
		if (isAborted(event))
		{
			return;
		}

		final User user = ess.getUser(event.getPlayer());
		final ChatStore chatStore = new ChatStore(ess, user, getChatType(event.getMessage()));
		setChatStore(event, chatStore);

		/**
		 * This listener should apply the general chat formatting only...then return control back the event handler
		 */
		event.setMessage(Util.formatString(user, "essentials.chat", event.getMessage()));		
		String group = user.getGroup();
		String world = user.getWorld().getName();
		event.setFormat(ess.getSettings().getChatFormat(group).format(new Object[] {group, world, world.substring(0, 1).toUpperCase(Locale.ENGLISH)}));
	}
}
