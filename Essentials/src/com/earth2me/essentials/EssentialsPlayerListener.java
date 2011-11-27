package com.earth2me.essentials;

import com.earth2me.essentials.craftbukkit.EnchantmentFix;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.textreader.IText;
import com.earth2me.essentials.textreader.KeywordReplacer;
import com.earth2me.essentials.textreader.TextInput;
import com.earth2me.essentials.textreader.TextPager;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.*;
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
		updateCompass(user);
		if (ess.getSettings().changeDisplayName())
		{
			user.setDisplayNick();
		}
	}

	@Override
	public void onPlayerChat(final PlayerChatEvent event)
	{
		final User user = ess.getUser(event.getPlayer());
		if (user.isMuted())
		{
			event.setCancelled(true);
			user.sendMessage(_("playerMuted"));
			LOGGER.info(_("mutedUserSpeaks", user.getName()));
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
			user.setDisplayNick();
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

		final Location afk = user.getAfkPosition();
		if (afk == null || !event.getTo().getWorld().equals(afk.getWorld()) || afk.distanceSquared(event.getTo()) > 9)
		{
			user.updateActivity(true);
		}
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
			EnchantmentFix.setContents(user.getInventory(), user.getSavedInventory());
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
			@Override
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
					LOGGER.log(Level.INFO, _("freedMemory", mem));
				}
				catch (InterruptedException ex)
				{
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
			user.setDisplayNick();
		}
		user.setLastLoginAddress(user.getAddress().getAddress().getHostAddress());
		user.updateActivity(false);
		if (user.isAuthorized("essentials.sleepingignored"))
		{
			user.setSleepingIgnored(true);
		}

		if (!ess.getSettings().isCommandDisabled("motd") && user.isAuthorized("essentials.motd"))
		{
			try
			{
				final IText input = new TextInput(user, "motd", true, ess);
				final IText output = new KeywordReplacer(input, user, ess);
				final TextPager pager = new TextPager(output, true);
				pager.showPage("1", null, user);
			}
			catch (IOException ex)
			{
				if (ess.getSettings().isDebug())
				{
					LOGGER.log(Level.WARNING, ex.getMessage(), ex);
				}
				else
				{
					LOGGER.log(Level.WARNING, ex.getMessage());
				}
			}
		}

		if (!ess.getSettings().isCommandDisabled("mail") && user.isAuthorized("essentials.mail"))
		{
			final List<String> mail = user.getMails();
			if (mail.isEmpty())
			{
				user.sendMessage(_("noNewMail"));
			}
			else
			{
				user.sendMessage(_("youHaveNewMail", mail.size()));
			}
		}
	}

	@Override
	public void onPlayerLogin(final PlayerLoginEvent event)
	{
		if (event.getResult() != Result.ALLOWED && event.getResult() != Result.KICK_FULL && event.getResult() != Result.KICK_BANNED)
		{
			LOGGER.log(Level.INFO, "Disconnecting user " + event.getPlayer().toString() + " due to " + event.getResult().toString());
			return;
		}
		User user = ess.getUser(event.getPlayer());
		user.setNPC(false);

		final long currentTime = System.currentTimeMillis();
		final boolean banExpired = user.checkBanTimeout(currentTime);
		user.checkMuteTimeout(currentTime);
		user.checkJailTimeout(currentTime);

		if (banExpired == false && (user.isBanned() || event.getResult() == Result.KICK_BANNED))
		{
			final String banReason = user.getBanReason();
			event.disallow(Result.KICK_BANNED, banReason != null && !banReason.isEmpty() && !banReason.equalsIgnoreCase("ban") ? banReason : _("defaultBanReason"));
			return;
		}

		if (server.getOnlinePlayers().length >= server.getMaxPlayers() && !user.isAuthorized("essentials.joinfullserver"))
		{
			event.disallow(Result.KICK_FULL, _("serverFull"));
			return;
		}
		event.allow();

		user.setLastLogin(System.currentTimeMillis());
		updateCompass(user);
	}

	private void updateCompass(final User user)
	{
		Location loc = user.getHome(user.getLocation());
		if (loc == null)
		{
			loc = user.getBedSpawnLocation();
		}
		if (loc != null)
		{
			user.setCompassTarget(loc);
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
			user.setDisplayNick();
		}
		updateCompass(user);
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
				@Override
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
		final User user = ess.getUser(event.getPlayer());
		user.updateActivity(true);
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
		final String cmd = event.getMessage().toLowerCase(Locale.ENGLISH).split(" ")[0].replace("/", "").toLowerCase(Locale.ENGLISH);
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

	@Override
	public void onPlayerChangedWorld(final PlayerChangedWorldEvent event)
	{
		if (ess.getSettings().getNoGodWorlds().contains(event.getPlayer().getLocation().getWorld().getName()))
		{
			User user = ess.getUser(event.getPlayer());
			if (user.isGodModeEnabledRaw())
			{
				user.sendMessage(_("noGodWorldWarning"));
			}
		}
	}
}
