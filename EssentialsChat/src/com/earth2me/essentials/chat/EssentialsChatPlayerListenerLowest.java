package com.earth2me.essentials.chat;

import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.FormatUtil;
import net.ess3.api.IEssentials;
import java.util.Locale;
import java.util.Map;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scoreboard.Team;


public class EssentialsChatPlayerListenerLowest extends EssentialsChatPlayer
{
	public EssentialsChatPlayerListenerLowest(final Server server,
											  final IEssentials ess,
											  final Map<AsyncPlayerChatEvent, ChatStore> chatStorage)
	{
		super(server, ess, chatStorage);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	@Override
	public void onPlayerChat(final AsyncPlayerChatEvent event)
	{
		if (isAborted(event))
		{
			return;
		}

		final User user = ess.getUser(event.getPlayer());

		if (user == null)
		{
			event.setCancelled(true);
			return;
		}

		final ChatStore chatStore = new ChatStore(ess, user, getChatType(event.getMessage()));
		setChatStore(event, chatStore);

		/**
		 * This listener should apply the general chat formatting only...then return control back the event handler
		 */
		event.setMessage(FormatUtil.formatMessage(user, "essentials.chat", event.getMessage()));
		String group = user.getGroup();
		String world = user.getWorld().getName();
		Team team = user.getBase().getScoreboard().getPlayerTeam(user.getBase());

		String format = ess.getSettings().getChatFormat(group);
		format = format.replace("{0}", group);
		format = format.replace("{1}", world);
		format = format.replace("{2}", world.substring(0, 1).toUpperCase(Locale.ENGLISH));
		format = format.replace("{3}", team == null ? "" : team.getPrefix());
		format = format.replace("{4}", team == null ? "" : team.getSuffix());
		format = format.replace("{5}", team == null ? "" : team.getDisplayName());
		synchronized (format)
		{
			event.setFormat(format);
		}
	}
}
