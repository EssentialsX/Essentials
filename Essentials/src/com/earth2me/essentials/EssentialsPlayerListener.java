package com.earth2me.essentials;

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
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;


public class EssentialsPlayerListener implements Listener
{
	private static final Logger LOGGER = Logger.getLogger("Minecraft");
	private final transient Server server;
	private final transient IEssentials ess;

	public EssentialsPlayerListener(final IEssentials parent)
	{
		this.ess = parent;
		this.server = parent.getServer();
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerRespawn(final PlayerRespawnEvent event)
	{
		final User user = ess.getUser(event.getPlayer());
		updateCompass(user);
		user.setDisplayNick();
	}

	@EventHandler(priority = EventPriority.LOWEST)
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
		user.setDisplayNick();
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerMove(final PlayerMoveEvent event)
	{
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

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(final PlayerQuitEvent event)
	{
		final User user = ess.getUser(event.getPlayer());
		if (ess.getSettings().removeGodOnDisconnect() && user.isGodModeEnabled())
		{
			user.toggleGodModeEnabled();
		}
		user.updateActivity(false);
		user.dispose();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(final PlayerJoinEvent event)
	{
		ess.getBackup().onPlayerJoin();
		final User user = ess.getUser(event.getPlayer());
		user.setDisplayNick();
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
				pager.showPage("1", null, "motd", user);
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

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerLogin(final PlayerLoginEvent event)
	{
		switch (event.getResult())
		{
		case ALLOWED:
		case KICK_FULL:
		case KICK_BANNED:
			break;
		default:
			return;
		}

		User user = ess.getUser(event.getPlayer());
		user.setNPC(false);

		final long currentTime = System.currentTimeMillis();
		final boolean banExpired = user.checkBanTimeout(currentTime);
		user.checkMuteTimeout(currentTime);
		user.checkJailTimeout(currentTime);

		if (!banExpired && (user.isBanned() || event.getResult() == Result.KICK_BANNED))
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

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerTeleport(final PlayerTeleportEvent event)
	{

		//There is TeleportCause.COMMMAND but plugins have to actively pass the cause in on their teleports.
		if ((event.getCause() == TeleportCause.PLUGIN || event.getCause() == TeleportCause.COMMAND) && ess.getSettings().registerBackInListener())
		{
			final User user = ess.getUser(event.getPlayer());
			user.setLastLocation();
		}

	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerEggThrow(final PlayerEggThrowEvent event)
	{
		final User user = ess.getUser(event.getPlayer());
		final ItemStack stack = new ItemStack(Material.EGG, 1);
		if (user.hasUnlimited(stack))
		{
			user.getInventory().addItem(stack);
			user.updateInventory();
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
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

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerCommandPreprocess(final PlayerCommandPreprocessEvent event)
	{
		final User user = ess.getUser(event.getPlayer());
		final String cmd = event.getMessage().toLowerCase(Locale.ENGLISH).split(" ")[0].replace("/", "").toLowerCase(Locale.ENGLISH);
		final List<String> commands = Arrays.asList("msg", "r", "mail", "m", "t", "emsg", "tell", "er", "reply", "ereply", "email");
		if (commands.contains(cmd))
		{
			for (Player player : ess.getServer().getOnlinePlayers())
			{
				final User spyer = ess.getUser(player);
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

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerChangedWorld(final PlayerChangedWorldEvent event)
	{
		final User user = ess.getUser(event.getPlayer());
		user.setDisplayNick();
		updateCompass(user);

		if (ess.getSettings().getNoGodWorlds().contains(event.getPlayer().getLocation().getWorld().getName()))
		{
			if (user.isGodModeEnabledRaw())
			{
				user.sendMessage(_("noGodWorldWarning"));
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(final PlayerInteractEvent event)
	{
		final User user = ess.getUser(event.getPlayer());
		user.updateActivity(true);
		switch (event.getAction())
		{
		case RIGHT_CLICK_BLOCK:
			if (event.isCancelled())
			{
				return;
			}
			if (ess.getSettings().getUpdateBedAtDaytime() && event.getClickedBlock().getType() == Material.BED_BLOCK)
			{
				event.getPlayer().setBedSpawnLocation(event.getClickedBlock().getLocation());
			}
			break;
		case LEFT_CLICK_AIR:
		case LEFT_CLICK_BLOCK:
			if (user.hasPowerTools() && user.arePowerToolsEnabled())
			{
				if (usePowertools(user))
				{
					event.setCancelled(true);
				}
			}
			break;
		default:
			break;
		}
	}

	private boolean usePowertools(final User user)
	{
		final ItemStack is = user.getItemInHand();
		int id;
		if (is == null || (id = is.getTypeId()) == 0)
		{
			return false;
		}
		final List<String> commandList = user.getPowertool(id);
		if (commandList == null || commandList.isEmpty())
		{
			return false;
		}
		boolean used = false;
		// We need to loop through each command and execute
		for (final String command : commandList)
		{
			if (command.matches(".*\\{player\\}.*"))
			{
				//user.sendMessage("Click a player to use this command");
				continue;
			}
			else if (command.startsWith("c:"))
			{
				used = true;
				user.chat(command.substring(2));
			}
			else
			{
				used = true;
				ess.scheduleSyncDelayedTask(
						new Runnable()
						{
							@Override
							public void run()
							{
								user.getServer().dispatchCommand(user.getBase(), command);
							}
						});
			}
		}
		return used;
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerPickupItem(final PlayerPickupItemEvent event)
	{
		if (!ess.getSettings().getDisableItemPickupWhileAfk())
		{
			return;
		}
		final User user = ess.getUser(event.getPlayer());
		if (user.isAfk())
		{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onInventoryClickEvent(final InventoryClickEvent event)
	{
		if (event.getView().getTopInventory().getType() == InventoryType.PLAYER)
		{
			final User user = ess.getUser(event.getWhoClicked());
			if (user.isInvSee() && !user.isAuthorized("essentials.invsee.modify"))
			{
				event.setCancelled(true);
			}
		}
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void onInventoryCloseEvent(final InventoryCloseEvent event)
	{
		if (event.getView().getTopInventory().getType() == InventoryType.PLAYER)
		{
			final User user = ess.getUser(event.getPlayer());
			user.setInvSee(false);			
		}
	}
}
