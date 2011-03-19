package com.earth2me.essentials;

import java.util.List;
import java.util.logging.Logger;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerLoginEvent.Result;


public class EssentialsPlayerListener extends PlayerListener
{
	private static final Logger logger = Logger.getLogger("Minecraft");
	private final Server server;
	private final Essentials parent;

	public EssentialsPlayerListener(Essentials parent)
	{
		this.parent = parent;
		this.server = parent.getServer();
	}

	@Override
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		User user = User.get(event.getPlayer());
		user.setDisplayName(user.getNick());
		updateCompass(user);
	}

	@Override
	public void onPlayerChat(PlayerChatEvent event)
	{
		User user = User.get(event.getPlayer());
		if (user.isMuted())
		{
			event.setCancelled(true);
			logger.info(user.getName() + " tried to speak, but is muted.");
		}
	}

	@Override
	public void onPlayerMove(PlayerMoveEvent event)
	{
		if (event.isCancelled()) return;
		final User user = User.get(event.getPlayer());

		if (!Essentials.getSettings().getNetherPortalsEnabled()) return;

		final Block block = event.getPlayer().getWorld().getBlockAt(event.getTo().getBlockX(), event.getTo().getBlockY(), event.getTo().getBlockZ());
		List<World> worlds = server.getWorlds();

		if (block.getType() == Material.PORTAL && worlds.size() > 1 && user.isAuthorized("essentials.portal"))
		{
			if (user.getJustPortaled()) return;

			Location loc = event.getTo();
			final World world = worlds.get(user.getWorld() == worlds.get(0) ? 1 : 0);

			double factor;
			if (user.getWorld().getEnvironment() == World.Environment.NETHER && world.getEnvironment() == World.Environment.NORMAL)
				factor = 16.0;
			else if (user.getWorld().getEnvironment() != world.getEnvironment())
				factor = 1.0 / 16.0;
			else
				factor = 1.0;

			int x = loc.getBlockX();
			int y = loc.getBlockY();
			int z = loc.getBlockZ();

			if (user.getWorld().getBlockAt(x, y, z - 1).getType() == Material.PORTAL)
				z--;
			if (user.getWorld().getBlockAt(x - 1, y, z).getType() == Material.PORTAL)
				x--;

			x = (int)(x * factor);
			z = (int)(z * factor);
			loc = new Location(world, x + .5, y, z + .5);

			Block dest = world.getBlockAt(x, y, z);
			NetherPortal portal = NetherPortal.findPortal(dest);
			if (portal == null)
			{
				if (world.getEnvironment() == World.Environment.NETHER || Essentials.getSettings().getGenerateExitPortals())
				{
					portal = NetherPortal.createPortal(dest);
					logger.info(event.getPlayer().getName() + " used a portal and generated an exit portal.");
					user.sendMessage("§7Generating an exit portal.");
					loc = portal.getSpawn();
				}
			}
			else
			{
				logger.info(event.getPlayer().getName() + " used a portal and used an existing exit portal.");
				user.sendMessage("§7Teleporting via portal to an existing portal.");
				loc = portal.getSpawn();
			}

			event.setFrom(loc);
			event.setTo(loc);
			try {
				user.teleportToNow(loc);
			} catch (Exception ex) {
				user.sendMessage(ex.getMessage());
			}
			user.setJustPortaled(true);
			user.sendMessage("§7Teleporting via portal.");

			event.setCancelled(true);
			return;
		}

		user.setJustPortaled(false);
	}

	@Override
	public void onPlayerQuit(PlayerEvent event)
	{
		if (!Essentials.getSettings().getReclaimSetting())
		{
			return;
		}
		User.get(event.getPlayer()).dispose();
		Thread thread = new Thread(new Runnable()
		{
			@SuppressWarnings("LoggerStringConcat")
			public void run()
			{
				try
				{
					Thread.sleep(1000);
					Runtime rt = Runtime.getRuntime();
					double mem = rt.freeMemory();
					rt.runFinalization();
					rt.gc();
					mem = rt.freeMemory() - mem;
					mem /= 1024 * 1024;
					logger.info("Freed " + mem + " MB.");
				}
				catch (InterruptedException ex)
				{
					return;
				}
			}
		});
		thread.setPriority(Thread.MIN_PRIORITY);
		thread.start();
	}

	@Override
	public void onPlayerJoin(PlayerEvent event)
	{
		Essentials.getStatic().backup.onPlayerJoin();
		User user = User.get(event.getPlayer());

		//we do not know the ip address on playerlogin so we need to do this here.
		if (user.isIpBanned())
		{
			user.kickPlayer("The Ban Hammer has spoken!");
			return;
		}

		user.setDisplayName(user.getNick());

		if (!Essentials.getSettings().isCommandDisabled("motd") && user.isAuthorized("essentials.motd"))
		{
			for (String m : parent.getMotd(user, null))
			{
				if (m == null) continue;
				user.sendMessage(m);
			}
		}

		if (!Essentials.getSettings().isCommandDisabled("mail"))
		{
			List<String> mail = Essentials.readMail(user);
			if (mail.isEmpty()) user.sendMessage("§7You have no new mail.");
			else user.sendMessage("§cYou have " + mail.size() + " messages!§f Type §7/mail read§f to view your mail.");
		}
	}

	@Override
	public void onPlayerLogin(PlayerLoginEvent event)
	{
		User user = User.get(event.getPlayer());
		if (event.getResult() != Result.ALLOWED)
			return;
		
		if (user.isBanned())
		{
			event.disallow(Result.KICK_BANNED, "The Ban Hammer has spoken!");
			return;
		}

		if (server.getOnlinePlayers().length >= server.getMaxPlayers() && !user.isOp())
		{
			event.disallow(Result.KICK_FULL, "Server is full");
			return;
		}

		updateCompass(user);
	}

	private void updateCompass(User user)
	{
		try
		{
			if (server.getPluginManager().isPluginEnabled("EssentialsHome"))
				user.setCompassTarget(user.getHome());
		}
		catch (Throwable ex)
		{
		}
	}

	@Override
	public void onPlayerTeleport(PlayerMoveEvent event)
	{
		User user = User.get(event.getPlayer());
		if (user.currentJail == null || user.currentJail.isEmpty())
			return;
		event.setCancelled(true);
		user.sendMessage(ChatColor.RED + "You do the crime, you do the time.");
	}
}
