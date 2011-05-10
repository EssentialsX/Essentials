package com.earth2me.essentials.chat;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import com.nijikokun.bukkit.Permissions.Permissions;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerRespawnEvent;


public class EssentialsChatWorker
{
	private static final Logger logger = Logger.getLogger("Minecraft");

	public static void onPlayerRespawn(Server server, PlayerRespawnEvent event)
	{
		User user = Essentials.getStatic().getUser(event.getPlayer());
		updateDisplayName(user);
	}

	public static void onPlayerJoin(Server server, PlayerEvent event)
	{
		User user = Essentials.getStatic().getUser(event.getPlayer());
		updateDisplayName(user);
	}

	private static void updateDisplayName(User user)
	{
		try
		{
			String group = user.getGroup();
			try
			{
				String prefix = Permissions.Security.getGroupPrefix(user.getWorld().getName(), group).replace('&', '§').replace("{WORLDNAME}", user.getWorld().getName());
				String suffix = Permissions.Security.getGroupSuffix(user.getWorld().getName(), group).replace('&', '§').replace("{WORLDNAME}", user.getWorld().getName());
				
				user.setDisplayName(prefix + user.getNick() + suffix + (suffix.length() > 1 && suffix.substring(suffix.length() -2).equals("§") ? "" : "§f"));
			}
			catch (Throwable ex)
			{
				logger.warning(Util.format("missingPrefixSuffix", group));
			}
		}
		catch (Throwable ex)
		{
			logger.warning(Util.i18n("permissionsError"));
		}
	}

	public static void onPlayerChat(Server server, PlayerChatEvent event)
	{
		if (event.isCancelled()) return;
		User user = Essentials.getStatic().getUser(event.getPlayer());
		updateDisplayName(user);

		if (user.isAuthorized("essentials.chat.color"))
			event.setMessage(event.getMessage().replaceAll("&([0-9a-f])", "§$1"));

		event.setFormat(Essentials.getStatic().getSettings().getChatFormat(user.getGroup()).replace('&', '§').replace("§§", "&").replace("{DISPLAYNAME}", "%1$s").replace("{GROUP}", user.getGroup()).replace("{MESSAGE}", "%2$s").replace("{WORLDNAME}", user.getWorld().getName()));

		int radius = Essentials.getStatic().getSettings().getChatRadius();
		if (radius < 1) return;

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
		logger.info(Util.format("localFormat", user.getName(), event.getMessage()));

		Location loc = user.getLocation();
		World w = loc.getWorld();
		int x = loc.getBlockX();
		int y = loc.getBlockY();
		int z = loc.getBlockZ();

		for (Player p : server.getOnlinePlayers())
		{
			User u = Essentials.getStatic().getUser(p);
			if (u != user && !u.isAuthorized("essentials.chat.spy"))
			{
				Location l = u.getLocation();
				int dx = Math.abs(x - l.getBlockX());
				int dy = Math.abs(y - l.getBlockY());
				int dz = Math.abs(z - l.getBlockZ());
				int delta = dx + dy + dz;
				if (delta > radius || w != l.getWorld()) continue;
			}

			u.sendMessage(String.format(event.getFormat(), user.getDisplayName(), event.getMessage()));
		}
	}
}
