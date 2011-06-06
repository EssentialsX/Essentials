package com.earth2me.essentials.chat;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerRespawnEvent;


public class EssentialsChatPlayerListener extends PlayerListener
{
	private static final Logger LOGGER = Logger.getLogger("Minecraft");
	private final transient IEssentials ess = Essentials.getStatic();
	private final transient Server server;

	public EssentialsChatPlayerListener(final Server server)
	{
		this.server = server;
	}

	@Override
	public void onPlayerJoin(final PlayerJoinEvent event)
	{
		final User user = ess.getUser(event.getPlayer());
		updateDisplayName(user);
	}

	private void updateDisplayName(final User user)
	{
		final String prefix = ess.getPermissionsHandler().getPrefix(user).replace('&', '§').replace("{WORLDNAME}", user.getWorld().getName());
		final String suffix = ess.getPermissionsHandler().getSuffix(user).replace('&', '§').replace("{WORLDNAME}", user.getWorld().getName());

		user.setDisplayName(prefix + user.getNick() + suffix + (suffix.length() > 1 && suffix.substring(suffix.length() - 2, suffix.length() - 1).equals("§") ? "" : "§f"));
	}

	@Override
	public void onPlayerChat(final PlayerChatEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		final User user = ess.getUser(event.getPlayer());
		updateDisplayName(user);

		if (user.isAuthorized("essentials.chat.color"))
		{
			event.setMessage(event.getMessage().replaceAll("&([0-9a-f])", "§$1"));
		}

		event.setFormat(ess.getSettings().getChatFormat(user.getGroup()).replace('&', '§').replace("§§", "&").replace("{DISPLAYNAME}", "%1$s").replace("{GROUP}", user.getGroup()).replace("{MESSAGE}", "%2$s").replace("{WORLDNAME}", user.getWorld().getName()).replace("{SHORTWORLDNAME}", user.getWorld().getName().substring(0, 1).toUpperCase()));

		final int radius = ess.getSettings().getChatRadius();
		if (radius < 1)
		{
			return;
		}

		if (event.getMessage().startsWith("!") && event.getMessage().length() > 1)
		{
			if (user.isAuthorized("essentials.chat.shout"))
			{
				event.setMessage(event.getMessage().substring(1));
				event.setFormat(Util.format("shoutFormat", event.getFormat()));
				return;
			}
			user.sendMessage(Util.i18n("notAllowedToShout"));
			event.setCancelled(true);
			return;
		}

		if (event.getMessage().startsWith("?") && event.getMessage().length() > 1)
		{
			if (user.isAuthorized("essentials.chat.question"))
			{
				event.setMessage(event.getMessage().substring(1));
				event.setFormat(Util.format("questionFormat", event.getFormat()));
				return;
			}
			user.sendMessage(Util.i18n("notAllowedToQuestion"));
			event.setCancelled(true);
			return;
		}

		event.setCancelled(true);
		LOGGER.info(Util.format("localFormat", user.getName(), event.getMessage()));

		final Location loc = user.getLocation();
		final World world = loc.getWorld();
		final int x = loc.getBlockX();
		final int y = loc.getBlockY();
		final int z = loc.getBlockZ();

		for (Player p : server.getOnlinePlayers())
		{
			final User u = ess.getUser(p);
			if (u.isIgnoredPlayer(user.getName()) && !user.isOp())
			{
				continue;
			}
			if (u.equals(user) && !u.isAuthorized("essentials.chat.spy"))
			{
				final Location l = u.getLocation();
				final int dx = Math.abs(x - l.getBlockX());
				final int dy = Math.abs(y - l.getBlockY());
				final int dz = Math.abs(z - l.getBlockZ());
				final int delta = dx + dy + dz;
				if (delta > radius || world != l.getWorld())
				{
					continue;
				}
			}

			u.sendMessage(String.format(event.getFormat(), user.getDisplayName(), event.getMessage()));
		}
	}
}
