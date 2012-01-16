package com.earth2me.essentials.chat;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.util.Locale;
import java.util.Map;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerChatEvent;


public class EssentialsChatPlayerListenerLowest extends EssentialsChatPlayer
{
	public EssentialsChatPlayerListenerLowest(final Server server,
											  final IEssentials ess,
											  final Map<String, IEssentialsChatListener> listeners)
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
		 * This listener should apply the general chat formatting only...then return control back the event handler
		 */
		final User user = ess.getUser(event.getPlayer());
		if (user.isAuthorized("essentials.chat.color"))
		{
			event.setMessage(event.getMessage().replaceAll("&([0-9a-f])", "\u00a7$1"));
		} else {
			event.setMessage(Util.stripColor(event.getMessage()));
		}
		event.setFormat(ess.getSettings().getChatFormat(user.getGroup()).replace('&', '\u00a7').replace("\u00a7\u00a7", "&").replace("{DISPLAYNAME}", "%1$s").replace("{GROUP}", user.getGroup()).replace("{MESSAGE}", "%2$s").replace("{WORLDNAME}", user.getWorld().getName()).replace("{SHORTWORLDNAME}", user.getWorld().getName().substring(0, 1).toUpperCase(Locale.ENGLISH)));
	}
}
