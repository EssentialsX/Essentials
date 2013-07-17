package com.earth2me.essentials.chat;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import static com.earth2me.essentials.chat.EssentialsChatPlayer.logger;
import net.ess3.api.IEssentials;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import net.ess3.api.events.LocalChatSpyEvent;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;


public class EssentialsChatPlayerListenerNormal extends EssentialsChatPlayer
{
	public EssentialsChatPlayerListenerNormal(final Server server,
											  final IEssentials ess,
											  final Map<AsyncPlayerChatEvent, ChatStore> chatStorage)
	{
		super(server, ess, chatStorage);
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

		final Location loc = user.getLocation();
		final World world = loc.getWorld();

		if (charge(event, chatStore) == false)
		{
			return;
		}

		Set<Player> outList = event.getRecipients();
		Set<Player> spyList = new HashSet<Player>();

		try
		{
			outList.add(event.getPlayer());
		}
		catch (UnsupportedOperationException ex)
		{
			if (ess.getSettings().isDebug())
			{
				ess.getLogger().log(Level.INFO, "Plugin triggered custom chat event, local chat handling aborted.", ex);
			}
			return;
		}
		
		final String format = event.getFormat();
		event.setFormat(_("chatTypeLocal").concat(event.getFormat()));

		logger.info(_("localFormat", user.getName(), event.getMessage()));

		final Iterator<Player> it = outList.iterator();
		while (it.hasNext())
		{
			final Player onlinePlayer = it.next();
			final User onlineUser = ess.getUser(onlinePlayer);
			if (!onlineUser.equals(user))
			{
				boolean abort = false;
				final Location playerLoc = onlineUser.getLocation();
				if (playerLoc.getWorld() != world)
				{
					abort = true;
				}
				else
				{
					final double delta = playerLoc.distanceSquared(loc);
					if (delta > chatStore.getRadius())
					{
						abort = true;
					}
				}
				if (abort)
				{
					if (onlineUser.isAuthorized("essentials.chat.spy"))
					{
						spyList.add(onlinePlayer);
					}
					it.remove();
				}
			}
		}

		if (outList.size() < 2) {
			user.sendMessage(_("localNoOne"));
		}

		LocalChatSpyEvent spyEvent = new LocalChatSpyEvent(event.isAsynchronous(), event.getPlayer(), format, event.getMessage(), spyList);
		server.getPluginManager().callEvent(spyEvent);

		if (!spyEvent.isCancelled())
		{
			for (Player onlinePlayer : spyEvent.getRecipients())
			{
				onlinePlayer.sendMessage(String.format(spyEvent.getFormat(), user.getDisplayName(), spyEvent.getMessage()));
			}
		}
	}
}
