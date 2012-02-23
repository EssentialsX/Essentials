package com.earth2me.essentials.chat;

import com.earth2me.essentials.api.ChargeException;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.Util;
import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.api.IGroups;
import com.earth2me.essentials.api.ISettings;
import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.perm.Permissions;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

//TODO: Translate the local/spy tags
public abstract class EssentialsChatPlayer implements Listener
{
	protected transient IEssentials ess;
	protected final static Logger LOGGER = Logger.getLogger("Minecraft");
	protected final transient Server server;
	protected final transient Map<PlayerChatEvent, ChatStore> chatStorage;

	public EssentialsChatPlayer(final Server server,
								final IEssentials ess,
								final Map<PlayerChatEvent, ChatStore> chatStorage)
	{
		this.ess = ess;
		this.server = server;
		this.chatStorage = chatStorage;
	}

	public void onPlayerChat(final PlayerChatEvent event)
	{
	}

	public ChatStore getChatStore(final PlayerChatEvent event)
	{
		return chatStorage.get(event);
	}

	public void setChatStore(final PlayerChatEvent event, final ChatStore chatStore)
	{
		chatStorage.put(event, chatStore);
	}

	public ChatStore delChatStore(final PlayerChatEvent event)
	{
		return chatStorage.remove(event);
	}

	protected void chargeChat(final PlayerChatEvent event, final ChatStore chatStore)
	{
		try
		{
			charge(chatStore.getUser(), chatStore.getCharge());
		}
		catch (ChargeException e)
		{
			ess.getCommandHandler().showCommandError(chatStore.getUser(), chatStore.getLongType(), e);
			event.setCancelled(true);
		}
	}

	protected void charge(final CommandSender sender, final Trade charge) throws ChargeException
	{
		if (sender instanceof Player)
		{
			charge.charge(ess.getUser((Player)sender));
		}
	}

	protected void formatChat(final PlayerChatEvent event, final ChatStore chatStore)
	{
		final IUser user = chatStore.getUser();
		if (Permissions.CHAT_COLOR.isAuthorized(user))
		{
			event.setMessage(Util.stripColor(event.getMessage()));
		}
		String group = ess.getGroups().getMainGroup(user);
		String world = user.getWorld().getName();

		IGroups groupSettings = ess.getGroups();
		event.setFormat(groupSettings.getChatFormat(user).format(new Object[]
				{
					group, world, world.substring(0, 1).toUpperCase(Locale.ENGLISH)
				}));

	}

	//TODO: Flesh this out - '?' trigger is too easily accidentally triggered
	protected String getChatType(final String message)
	{
		switch (message.charAt(0))
		{
		case '!':
			return "shout";
		//case '?':
		//return "question";
		//case '@':
		//	return "admin";			
		default:
			return "";
		}
	}

	protected void handleLocalChat(final PlayerChatEvent event, final ChatStore chatStore)
	{
		long radius = 0;
		ISettings settings = ess.getSettings();
		settings.acquireReadLock();
		try
		{
			radius = settings.getData().getChat().getLocalRadius();
		}
		finally
		{
			settings.unlock();
		}

		if (radius < 1)
		{
			return;
		}

		radius *= radius;

		final IUser user = chatStore.getUser();

		if (event.getMessage().length() > 1 && chatStore.getType().length() > 0)
		{
			if (ChatPermissions.getPermission(chatStore.getType()).isAuthorized(user))
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

		event.setCancelled(true);
		final EssentialsLocalChatEvent localChat = new EssentialsLocalChatEvent(event, radius);
		ess.getServer().getPluginManager().callEvent(localChat);
	}
}
