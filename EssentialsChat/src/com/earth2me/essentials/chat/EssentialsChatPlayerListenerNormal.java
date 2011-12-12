package com.earth2me.essentials.chat;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import java.util.Locale;
import java.util.Map;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerChatEvent;


public class EssentialsChatPlayerListenerNormal extends EssentialsChatPlayer
{
	private final transient Map<PlayerChatEvent, String> charges;

	public EssentialsChatPlayerListenerNormal(final Server server,
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
		if (isAborted(event))
		{
			return;
		}

		/**
		 * This file should handle detection of the local chat features... if local chat is enabled, we need to handle
		 * it here
		 */
		final String chatType = getChatType(event.getMessage());
		final StringBuilder command = new StringBuilder();
		command.append("chat");

		if (chatType.length() > 0)
		{
			command.append("-").append(chatType);
		}
		long radius = ess.getSettings().getChatRadius();
		if (radius < 1)
		{
			return;
		}
		radius *= radius;
		final User user = ess.getUser(event.getPlayer());

		if (event.getMessage().length() > 0 && chatType.length() > 0)
		{
			final StringBuilder permission = new StringBuilder();
			permission.append("essentials.chat.").append(chatType);

			final StringBuilder format = new StringBuilder();
			format.append(chatType).append("Format");

			final StringBuilder errorMsg = new StringBuilder();
			errorMsg.append("notAllowedTo").append(chatType.substring(0, 1).toUpperCase(Locale.ENGLISH)).append(chatType.substring(1));

			if (user.isAuthorized(permission.toString()))
			{
				event.setMessage(event.getMessage().substring(1));
				event.setFormat(_(format.toString(), event.getFormat()));
				charges.put(event, command.toString());
				return;
			}

			user.sendMessage(_(errorMsg.toString()));
			event.setCancelled(true);
			return;
		}

		sendLocalChat(user, radius, event);
	}
}
