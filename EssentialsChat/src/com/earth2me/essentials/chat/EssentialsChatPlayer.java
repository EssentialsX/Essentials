package com.earth2me.essentials.chat;

import com.earth2me.essentials.ChargeException;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.api.ISettings;
import com.earth2me.essentials.api.IUser;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

//TODO: Translate the local/spy tags
public abstract class EssentialsChatPlayer extends PlayerListener
{
	protected transient IEssentials ess;
	protected final static Logger logger = Logger.getLogger("Minecraft");
		protected final transient Server server;

	public EssentialsChatPlayer(final Server server, final IEssentials ess)
	{
		this.ess = ess;
		this.server = server;
	}

	@Override
	public void onPlayerChat(final PlayerChatEvent event)
	{
	}

	public boolean isAborted(final PlayerChatEvent event)
	{
		return isAborted(event, "chat");
	}

	public boolean isAborted(final PlayerChatEvent event, final String command)
	{
		if (event.isCancelled())
		{
			return true;
		}

		final IUser user = ess.getUser(event.getPlayer());
		if (!isAffordableFor(user, command))
		{
			event.setCancelled(true);
			return true;
		}
		return false;
	}
	
	protected void chargeChat (final PlayerChatEvent event, final Map<PlayerChatEvent, String> charges) {
		
		final IUser user = ess.getUser(event.getPlayer());
		
		String charge = charges.remove(event);
		if (charge == null)
		{
			charge = "chat";
		}		

		try
		{
			charge(user, charge);
		}
		catch (ChargeException e)
		{
			ess.getCommandHandler().showCommandError(user, charge, e);
			event.setCancelled(true);
		}
	}

	protected void charge(final CommandSender sender, final String command) throws ChargeException
	{
		if (sender instanceof Player)
		{
			final Trade charge = new Trade(command, ess);
			charge.charge(ess.getUser((Player)sender));
		}
	}

	protected boolean isAffordableFor(final CommandSender sender, final String command)
	{
		if (sender instanceof Player)
		{
			try
			{
				final Trade charge = new Trade(command, ess);
				charge.isAffordableFor(ess.getUser((Player)sender));
			}
			catch (ChargeException e)
			{
				return false;
			}
		}
		else
		{
			return false;
		}

		return true;
	}

	protected void formatChat(final PlayerChatEvent event)
	{
		final IUser user = ess.getUser(event.getPlayer());
		if (user.isAuthorized("essentials.chat.color"))
		{
			event.setMessage(event.getMessage().replaceAll("&([0-9a-f])", "\u00a7$1"));
		}
		String format = ess.getGroups().getChatFormat(user);
		event.setFormat(format.replace('&', '\u00a7').replace("\u00a7\u00a7", "&").replace("{DISPLAYNAME}", "%1$s").replace("{GROUP}", user.getGroup()).replace("{MESSAGE}", "%2$s").replace("{WORLDNAME}", user.getWorld().getName()).replace("{SHORTWORLDNAME}", user.getWorld().getName().substring(0, 1).toUpperCase(Locale.ENGLISH)));
	}
	
	protected String getChatType(final String message)
	{
		switch (message.charAt(0))
		{
		case '!':
			return "shout";
		case '?':
			return "question";
		//case '@':
		//	return "admin";			
		default:
			return "";
		}
	}

	protected void handleLocalChat(final Map<PlayerChatEvent, String> charges, final PlayerChatEvent event)
	{
		long radius = 0;
		ISettings settings = ess.getSettings();
		settings.acquireReadLock();
		try {
			radius = settings.getData().getChat().getLocalRadius();
		} finally {
			settings.unlock();
		}
		radius *= radius;

		final IUser user = ess.getUser(event.getPlayer());
		final String chatType = getChatType(event.getMessage());
		final StringBuilder command = new StringBuilder();
		command.append("chat");		

		if (event.getMessage().length() > 0 && chatType.length() > 0)
		{
			command.append("-").append(chatType);
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
		
		event.setCancelled(true);
		final EssentialsLocalChatEvent localChat = new EssentialsLocalChatEvent(event, radius);
		ess.getServer().getPluginManager().callEvent(localChat);
	}
}
