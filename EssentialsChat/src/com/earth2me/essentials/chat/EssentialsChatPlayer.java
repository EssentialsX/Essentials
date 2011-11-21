package com.earth2me.essentials.chat;

import com.earth2me.essentials.ChargeException;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import java.util.Map;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;


public abstract class EssentialsChatPlayer extends PlayerListener
{
	protected transient IEssentials ess;
	protected final static Logger logger = Logger.getLogger("Minecraft");
	protected final transient Map<String, IEssentialsChatListener> listeners;
	protected final transient Server server;

	public EssentialsChatPlayer(Server server, IEssentials ess, Map<String, IEssentialsChatListener> listeners)
	{
		this.ess = ess;
		this.listeners = listeners;
		this.server = server;
	}

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
		for (IEssentialsChatListener listener : listeners.values())
		{
			if (listener.shouldHandleThisChat(event))
			{
				return true;
			}
		}

		final User user = ess.getUser(event.getPlayer());
		if (!isAffordableFor(user, command))
		{
			event.setCancelled(true);
			return true;
		}
		return false;
	}

	public String getChatType(final String message)
	{
		switch (message.charAt(0))
		{
		case '!':
			return "shout";
		case '?':
			return "question";
		default:
			return "";
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

	protected void sendLocalChat(final User sender, final long radius, final PlayerChatEvent event)
	{
		event.setCancelled(true);
		logger.info(_("localFormat", sender.getName(), event.getMessage()));
		final Location loc = sender.getLocation();
		final World world = loc.getWorld();
		final int x = loc.getBlockX();
		final int y = loc.getBlockY();
		final int z = loc.getBlockZ();
		for (Player p : server.getOnlinePlayers())
		{
			String type = "[L]";
			final User u = ess.getUser(p);
			//TODO: remove reference to op 
			if (u.isIgnoredPlayer(sender.getName()) && !sender.isOp())
			{
				continue;
			}
			if (!u.equals(sender))
			{
				final Location l = u.getLocation();
				final int dx = x - l.getBlockX();
				final int dy = y - l.getBlockY();
				final int dz = z - l.getBlockZ();
				final long delta = dx * dx + dy * dy + dz * dz;
				if (delta > radius || world != l.getWorld())
				{
					if (!u.isAuthorized("essentials.chat.spy"))
					{
						continue;
					}
					else
					{
						type = type.concat("[Spy]");
					}
				}
			}

			String message = String.format(event.getFormat(), type.concat(sender.getDisplayName()), event.getMessage());
			for (IEssentialsChatListener listener : listeners.values())
			{
				message = listener.modifyMessage(event, p, message);
			}
			u.sendMessage(message);
		}
	}
}
