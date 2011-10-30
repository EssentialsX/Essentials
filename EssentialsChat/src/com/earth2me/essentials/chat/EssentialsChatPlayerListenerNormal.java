package com.earth2me.essentials.chat;

import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.util.Map;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerChatEvent;


public class EssentialsChatPlayerListenerNormal extends EssentialsChatPlayer
{
	public EssentialsChatPlayerListenerNormal(Server server, IEssentials ess, Map<String, IEssentialsChatListener> listeners)
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
		 * This file should handle detection of the local chat features...
		 * if local chat is enabled, we need to handle it here
		 */
		final User user = ess.getUser(event.getPlayer());
		final String chatType = getChatType(event.getMessage());
		long radius = ess.getSettings().getChatRadius();
		if (radius < 1)
		{
			return;
		}
		radius *= radius;
		try
		{
			if (event.getMessage().length() > 0 && chatType.length() > 0)
			{
				StringBuilder permission = new StringBuilder();
				permission.append("essentials.chat.").append(chatType);

				StringBuilder command = new StringBuilder();
				command.append("chat-").append(chatType);

				StringBuilder format = new StringBuilder();
				format.append(chatType).append("Format");

				StringBuilder errorMsg = new StringBuilder();
				errorMsg.append("notAllowedTo").append(chatType.substring(0, 1).toUpperCase()).append(chatType.substring(1));

				if (user.isAuthorized(permission.toString()))
				{
					charge(user, command.toString());
					event.setMessage(event.getMessage().substring(1));
					event.setFormat(Util.format(format.toString(), event.getFormat()));
					return;
				}

				user.sendMessage(Util.i18n(errorMsg.toString()));
				event.setCancelled(true);
				return;
			}
		}
		catch (ChargeException ex)
		{
			ess.showError(user, ex, "Shout");
			event.setCancelled(true);
			return;
		}
		sendLocalChat(user, radius, event);
	}
}
