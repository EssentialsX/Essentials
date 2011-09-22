package com.earth2me.essentials;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;


public class EssentialsPlayerListener extends PlayerListener
{
	private static final Logger LOGGER = Logger.getLogger("Minecraft");
	private final transient Server server;
	private final transient IEssentials ess;

	public EssentialsPlayerListener(final IEssentials parent)
	{
		this.ess = parent;
		this.server = parent.getServer();
	}

	@Override
	public void onPlayerRespawn(final PlayerRespawnEvent event)
	{
		final User user = ess.getUser(event.getPlayer());
		user.setDisplayName(user.getNick());
		updateCompass(user);
		if (ess.getSettings().changeDisplayName())
		{
			user.setDisplayName(user.getNick());
		}
	}

	@Override
	public void onPlayerChat(final PlayerChatEvent event)
	{
		final User user = ess.getUser(event.getPlayer());
		if (user.isMuted())
		{
			event.setCancelled(true);
			user.sendMessage(Util.i18n("playerMuted"));
			LOGGER.info(Util.format("mutedUserSpeaks", user.getName()));
		}
		final Iterator<Player> it = event.getRecipients().iterator();
		while (it.hasNext())
		{
			final User u = ess.getUser(it.next());
			if (u.isIgnoredPlayer(user.getName()))
			{
				it.remove();
			}
		}
		user.updateActivity(true);
		if (ess.getSettings().changeDisplayName())
		{
			user.setDisplayName(user.getNick());
		}
	}

	@Override
	public void onPlayerMove(final PlayerMoveEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		final User user = ess.getUser(event.getPlayer());

		if (user.isAfk() && ess.getSettings().getFreezeAfkPlayers())
		{
			final Location from = event.getFrom();
			final Location to = event.getTo().clone();
			to.setX(from.getX());
			to.setY(from.getY());
			to.setZ(from.getZ());
			try
			{
				event.setTo(Util.getSafeDestination(to));
			}
			catch (Exception ex)
			{
				event.setTo(to);
			}
			return;
		}

		Location afk = user.getAfkPosition();
		if (afk == null || !event.getTo().getWorld().equals(afk.getWorld()) || afk.distanceSquared(event.getTo()) > 9) {
			user.updateActivity(true);
		}

		if (!ess.getSettings().getNetherPortalsEnabled())
		{
			return;
		}

		final Block block = event.getPlayer().getWorld().getBlockAt(event.getTo().getBlockX(), event.getTo().getBlockY(), event.getTo().getBlockZ());
		final List<World> worlds = server.getWorlds();

		if (block.getType() == Material.PORTAL && worlds.size() > 1 && user.isAuthorized("essentials.portal"))
		{
			if (user.getJustPortaled())
			{
				return;
			}

			World nether = server.getWorld(ess.getSettings().getNetherName());
			if (nether == null)
			{
				for (World world : worlds)
				{
					if (world.getEnvironment() == World.Environment.NETHER)
					{
						nether = world;
						break;
					}
				}
				if (nether == null)
				{
					return;
				}
			}
			final World world = user.getWorld() == nether ? worlds.get(0) : nether;

			double factor;
			if (user.getWorld().getEnvironment() == World.Environment.NETHER && world.getEnvironment() == World.Environment.NORMAL)
			{
				factor = ess.getSettings().getNetherRatio();
			}
			else if (user.getWorld().getEnvironment() == World.Environment.NORMAL && world.getEnvironment() == World.Environment.NETHER)
			{
				factor = 1.0 / ess.getSettings().getNetherRatio();
			}
			else
			{
				factor = 1.0;
			}

			Location loc = event.getTo();
			int x = loc.getBlockX();
			int y = loc.getBlockY();
			int z = loc.getBlockZ();

			if (user.getWorld().getBlockAt(x, y, z - 1).getType() == Material.PORTAL)
			{
				z--;
			}
			if (user.getWorld().getBlockAt(x - 1, y, z).getType() == Material.PORTAL)
			{
				x--;
			}

			x = (int)(x * factor);
			z = (int)(z * factor);
			loc = new Location(world, x + .5, y, z + .5);

			Block dest = world.getBlockAt(x, y, z);
			NetherPortal portal = NetherPortal.findPortal(dest);
			if (portal == null)
			{
				if (world.getEnvironment() == World.Environment.NETHER || ess.getSettings().getGenerateExitPortals())
				{
					portal = NetherPortal.createPortal(dest);
					LOGGER.info(Util.format("userCreatedPortal", event.getPlayer().getName()));
					user.sendMessage(Util.i18n("generatingPortal"));
					loc = portal.getSpawn();
				}
			}
			else
			{
				LOGGER.info(Util.format("userUsedPortal", event.getPlayer().getName()));
				user.sendMessage(Util.i18n("usingPortal"));
				loc = portal.getSpawn();
			}

			event.setFrom(loc);
			event.setTo(loc);
			try
			{
				user.getTeleport().now(loc, new Trade("portal", ess));
			}
			catch (Exception ex)
			{
				user.sendMessage(ex.getMessage());
			}
			user.setJustPortaled(true);
			user.sendMessage(Util.i18n("teleportingPortal"));

			event.setCancelled(true);
			return;
		}

		user.setJustPortaled(false);
	}

	@Override
	public void onPlayerQuit(final PlayerQuitEvent event)
	{
		final User user = ess.getUser(event.getPlayer());
		if (ess.getSettings().removeGodOnDisconnect() && user.isGodModeEnabled())
		{
			user.toggleGodModeEnabled();
		}
		if (user.getSavedInventory() != null)
		{
			user.getInventory().setContents(user.getSavedInventory());
			user.setSavedInventory(null);
		}
		user.updateActivity(false);
		user.dispose();
		if (!ess.getSettings().getReclaimSetting())
		{
			return;
		}
		final Thread thread = new Thread(new Runnable()
		{
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
					LOGGER.log(Level.INFO, Util.format("freedMemory", mem));
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
	public void onPlayerJoin(final PlayerJoinEvent event)
	{
		ess.getBackup().onPlayerJoin();
		final User user = ess.getUser(event.getPlayer());

		if (ess.getSettings().changeDisplayName())
		{
			user.setDisplayName(user.getNick());
		}
		user.updateActivity(false);
		if (user.isAuthorized("essentials.sleepingignored"))
		{
			user.setSleepingIgnored(true);
		}

		if (!ess.getSettings().isCommandDisabled("motd") && user.isAuthorized("essentials.motd"))
		{
			for (String m : ess.getMotd(user, null))
			{
				if (m == null)
				{
					continue;
				}
				user.sendMessage(m);
			}
		}

		if (!ess.getSettings().isCommandDisabled("mail") && user.isAuthorized("essentials.mail"))
		{
			final List<String> mail = user.getMails();
			if (mail.isEmpty())
			{
				user.sendMessage(Util.i18n("noNewMail"));
			}
			else
			{
				user.sendMessage(Util.format("youHaveNewMail", mail.size()));
			}
		}
	}

	@Override
	public void onPlayerLogin(final PlayerLoginEvent event)
	{
		if (event.getResult() != Result.ALLOWED && event.getResult() != Result.KICK_FULL && event.getResult() != Result.KICK_BANNED)
		{
			return;
		}
		User user = ess.getUser(event.getPlayer());
		user.setNPC(false);

		final long currentTime = System.currentTimeMillis();
		user.checkBanTimeout(currentTime);
		user.checkMuteTimeout(currentTime);
		user.checkJailTimeout(currentTime);

		if (user.isBanned())
		{
			final String banReason = user.getBanReason();
			event.disallow(Result.KICK_BANNED, banReason != null && !banReason.isEmpty() ? banReason : Util.i18n("defaultBanReason"));
			return;
		}

		if (server.getOnlinePlayers().length >= server.getMaxPlayers() && !user.isAuthorized("essentials.joinfullserver"))
		{
			event.disallow(Result.KICK_FULL, Util.i18n("serverFull"));
			return;
		}
		event.allow();

		user.setLastLogin(System.currentTimeMillis());
		updateCompass(user);
	}

	private void updateCompass(final User user)
	{
		try
		{
			user.setCompassTarget(user.getHome(user.getLocation()));
		}
		catch (Exception ex)
		{
		}
	}

	@Override
	public void onPlayerTeleport(PlayerTeleportEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		final User user = ess.getUser(event.getPlayer());
		if (ess.getSettings().changeDisplayName())
		{
			user.setDisplayName(user.getNick());
		}
		updateCompass(user);
	}

	@Override
	public void onPlayerInteract(final PlayerInteractEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
		{
			return;
		}

		if (ess.getSettings().getBedSetsHome() && event.getClickedBlock().getType() == Material.BED_BLOCK)
		{
			try
			{
				final User user = ess.getUser(event.getPlayer());
				user.setHome();
				user.sendMessage(Util.i18n("homeSetToBed"));
			}
			catch (Throwable ex)
			{
			}
		}
	}

	@Override
	public void onPlayerEggThrow(final PlayerEggThrowEvent event)
	{
		final User user = ess.getUser(event.getPlayer());
		final ItemStack is = new ItemStack(Material.EGG, 1);
		if (user.hasUnlimited(is))
		{
			user.getInventory().addItem(is);
			user.updateInventory();
		}
	}

	@Override
	public void onPlayerBucketEmpty(final PlayerBucketEmptyEvent event)
	{
		final User user = ess.getUser(event.getPlayer());
		if (user.hasUnlimited(new ItemStack(event.getBucket())))
		{
			event.getItemStack().setType(event.getBucket());
			ess.scheduleSyncDelayedTask(new Runnable()
			{
				public void run()
				{
					user.updateInventory();
				}
			});
		}
	}

	@Override
	public void onPlayerAnimation(final PlayerAnimationEvent event)
	{
		usePowertools(event);
	}

	private void usePowertools(final PlayerAnimationEvent event)
	{
		if (event.getAnimationType() != PlayerAnimationType.ARM_SWING)
		{
			return;
		}
		final User user = ess.getUser(event.getPlayer());
		final ItemStack is = user.getItemInHand();
		if (is == null || is.getType() == Material.AIR || !user.arePowerToolsEnabled())
		{
			return;
		}
		final List<String> commandList = user.getPowertool(is);
		if (commandList == null || commandList.isEmpty())
		{
			return;
		}

		// We need to loop through each command and execute
		for (String command : commandList)
		{
			if (command.matches(".*\\{player\\}.*"))
			{
				//user.sendMessage("Click a player to use this command");
				continue;
			}
			else if (command.startsWith("c:"))
			{
				for (Player p : server.getOnlinePlayers())
				{
					p.sendMessage(user.getDisplayName() + ":" + command.substring(2));
				}
			}
			else
			{
				user.getServer().dispatchCommand(event.getPlayer(), command);
			}
		}
	}

	@Override
	public void onPlayerCommandPreprocess(final PlayerCommandPreprocessEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		final User user = ess.getUser(event.getPlayer());
		final String cmd = event.getMessage().toLowerCase().split(" ")[0].replace("/", "").toLowerCase();
		final List<String> commands = Arrays.asList("msg", "r", "mail", "m", "t", "emsg", "tell", "er", "reply", "ereply", "email");
		if (commands.contains(cmd))
		{
			for (Player player : ess.getServer().getOnlinePlayers())
			{
				User spyer = ess.getUser(player);
				if (spyer.isSocialSpyEnabled() && !user.equals(spyer))
				{
					player.sendMessage(user.getDisplayName() + " : " + event.getMessage());
				}
			}
		}
		if (!cmd.equalsIgnoreCase("afk"))
		{
			user.updateActivity(true);
		}
	}
}
