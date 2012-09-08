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
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;


public class EssentialsPlayerListener implements Listener
{
	private static final Logger LOGGER = Logger.getLogger("Minecraft");
	private final transient IEssentials ess;
	private static final int AIR = Material.AIR.getId();
	private static final int BED = Material.BED_BLOCK.getId();

	public EssentialsPlayerListener(final IEssentials parent)
	{
		this.ess = parent;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerRespawn(final PlayerRespawnEvent event)
	{
		final User user = ess.getUser(event.getPlayer());
		updateCompass(user);
		user.setDisplayNick();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerChat(final AsyncPlayerChatEvent event)
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
			if (u.isIgnoredPlayer(user))
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
		if ((!ess.getSettings().cancelAfkOnMove() && !ess.getSettings().getFreezeAfkPlayers())
			|| event.getFrom().getBlockX() == event.getTo().getBlockX()
			   && event.getFrom().getBlockZ() == event.getTo().getBlockZ()
			   && event.getFrom().getBlockY() == event.getTo().getBlockY())
		{
			return;
		}

		final User user = ess.getUser(event.getPlayer());
		if (user.isAfk() && ess.getSettings().getFreezeAfkPlayers())
		{
			final Location from = event.getFrom();
			final Location origTo = event.getTo();
			final Location to = origTo.clone();
			if (ess.getSettings().cancelAfkOnMove() && origTo.getY() >= from.getBlockY() + 1)
			{
				user.updateActivity(true);
				return;
			}
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
			user.setGodModeEnabled(false);
		}
		if (user.isVanished())
		{
			user.toggleVanished();
		}
		if (!user.isJailed())
		{
			user.setLastLocation();
		}
		user.updateActivity(false);
		user.dispose();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(final PlayerJoinEvent event)
	{
		ess.scheduleAsyncDelayedTask(new Runnable()
		{
			@Override
			public void run()
			{
				delayedJoin(event.getPlayer());
			}
		});
	}

	public void delayedJoin(final Player player)
	{
		if (!player.isOnline())
		{
			return;
		}
		ess.getBackup().onPlayerJoin();
		final User user = ess.getUser(player);
		user.setDisplayNick();
		updateCompass(user);
		user.setLastLogin(System.currentTimeMillis());
		user.updateActivity(false);

		for (String p : ess.getVanishedPlayers())
		{
			if (!user.isAuthorized("essentials.vanish.see"))
			{
				user.hidePlayer(ess.getUser(p).getBase());
			}
		}

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

	private void updateCompass(final User user)
	{
		Location loc = user.getHome(user.getLocation());
		if (loc == null)
		{
			loc = user.getBedSpawnLocation();
		}
		if (loc != null)
		{
			final Location updateLoc = loc;
			ess.scheduleSyncDelayedTask(new Runnable()
			{
				@Override
				public void run()
				{
					user.setCompassTarget(updateLoc);
				}
			});
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

		final User user = ess.getUser(event.getPlayer());
		if (user.isNPC())
		{
			user.setNPC(false);
		}

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

		if (event.getResult() == Result.KICK_FULL && !user.isAuthorized("essentials.joinfullserver"))
		{
			event.disallow(Result.KICK_FULL, _("serverFull"));
			return;
		}
		event.allow();
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerTeleport(final PlayerTeleportEvent event)
	{
		final boolean backListener = ess.getSettings().registerBackInListener();
		final boolean teleportInvulnerability = ess.getSettings().isTeleportInvulnerability();
		if (backListener || teleportInvulnerability)
		{
			final User user = ess.getUser(event.getPlayer());
			//There is TeleportCause.COMMMAND but plugins have to actively pass the cause in on their teleports.
			if (backListener && (event.getCause() == TeleportCause.PLUGIN || event.getCause() == TeleportCause.COMMAND))
			{
				user.setLastLocation();
			}
			if (teleportInvulnerability)
			{
				user.enableInvulnerabilityAfterTeleport();
			}
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
	private final static List<String> COMMANDS = Arrays.asList("msg", "r", "mail", "m", "t", "whisper", "emsg", "tell", "er", "reply", "ereply", "email", "action", "describe", "eme", "eaction", "edescribe", "etell", "ewhisper", "pm");

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerCommandPreprocess(final PlayerCommandPreprocessEvent event)
	{
		final Player player = event.getPlayer();
		final String cmd = event.getMessage().toLowerCase(Locale.ENGLISH).split(" ")[0].replace("/", "").toLowerCase(Locale.ENGLISH);
		if (COMMANDS.contains(cmd))
		{
			for (Player onlinePlayer : ess.getServer().getOnlinePlayers())
			{
				final User spyer = ess.getUser(onlinePlayer);
				if (spyer.isSocialSpyEnabled() && !player.equals(onlinePlayer))
				{
					onlinePlayer.sendMessage(player.getDisplayName() + " : " + event.getMessage());
				}
			}
		}
		else if (!cmd.equalsIgnoreCase("afk"))
		{
			final User user = ess.getUser(player);
			user.updateActivity(true);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerChangedWorldHack(final PlayerChangedWorldEvent event)
	{
		final Player user = event.getPlayer();
		user.setAllowFlight(false);
		user.setFlySpeed(0.1f);
		user.setWalkSpeed(0.2f);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerChangedWorld(final PlayerChangedWorldEvent event)
	{
		final User user = ess.getUser(event.getPlayer());
		final String newWorld = event.getPlayer().getLocation().getWorld().getName();
		user.setDisplayNick();
		updateCompass(user);
		if (ess.getSettings().getNoGodWorlds().contains(newWorld) && user.isGodModeEnabledRaw())
		{
			user.sendMessage(_("noGodWorldWarning"));
		}

		if (!event.getPlayer().getWorld().getName().equals(newWorld))
		{
			user.sendMessage(_("currentWorld", newWorld));
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(final PlayerInteractEvent event)
	{
		switch (event.getAction())
		{
		case RIGHT_CLICK_BLOCK:
			if (!event.isCancelled() && event.getClickedBlock().getTypeId() == BED && ess.getSettings().getUpdateBedAtDaytime())
			{
				Player player = event.getPlayer();
				player.setBedSpawnLocation(event.getClickedBlock().getLocation());
				player.sendMessage(_("homeSet", player.getLocation().getWorld().getName(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ()));
			}
			break;
		case LEFT_CLICK_AIR:
			if (event.getPlayer().isFlying())			
			{
				final User user = ess.getUser(event.getPlayer());
				if (user.isFlyClickJump())
				{
					useFlyClickJump(user);
					return;
				}
			}
		case LEFT_CLICK_BLOCK:
			if (event.getItem() != null && event.getItem().getTypeId() != AIR)
			{
				final User user = ess.getUser(event.getPlayer());
				if (user.hasPowerTools() && user.arePowerToolsEnabled() && usePowertools(user, event.getItem().getTypeId()))
				{
					event.setCancelled(true);
				}
			}
			break;
		default:
			break;
		}
	}

	private void useFlyClickJump(final User user)
	{
		try
		{
			final Location otarget = Util.getTarget(user);

			ess.scheduleSyncDelayedTask(
					new Runnable()
					{
						@Override
						public void run()
						{
							Location loc = user.getLocation();
							loc.setX(otarget.getX());
							loc.setZ(otarget.getZ());
							while (Util.isBlockDamaging(loc.getWorld(), loc.getBlockX(), loc.getBlockY() -1, loc.getBlockZ())) {
								loc.setY(loc.getY() + 1d);
							}
							user.getBase().teleport(loc, TeleportCause.PLUGIN);
						}
					});
		}
		catch (Exception ex)
		{
			if (ess.getSettings().isDebug())
			{
				LOGGER.log(Level.WARNING, ex.getMessage(), ex);
			}
		}
	}

	private boolean usePowertools(final User user, final int id)
	{
		final List<String> commandList = user.getPowertool(id);
		if (commandList == null || commandList.isEmpty())
		{
			return false;
		}
		boolean used = false;
		// We need to loop through each command and execute
		for (final String command : commandList)
		{
			if (command.contains("{player}"))
			{
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
								LOGGER.log(Level.INFO, String.format("[PT] %s issued server command: /%s", user.getName(), command));
							}
						});
			}
		}
		return used;
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerPickupItem(final PlayerPickupItemEvent event)
	{
		if (ess.getSettings().getDisableItemPickupWhileAfk())
		{
			if (ess.getUser(event.getPlayer()).isAfk())
			{
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onInventoryClickEvent(final InventoryClickEvent event)
	{
		if (event.getView().getTopInventory().getType() == InventoryType.PLAYER)
		{
			final User user = ess.getUser(event.getWhoClicked());
			final InventoryHolder invHolder = event.getView().getTopInventory().getHolder();
			if (invHolder != null && invHolder instanceof HumanEntity)
			{
				final User invOwner = ess.getUser((HumanEntity)invHolder);
				if (user.isInvSee() && (!user.isAuthorized("essentials.invsee.modify")
										|| invOwner.isAuthorized("essentials.invsee.preventmodify")
										|| !invOwner.isOnline()))
				{
					event.setCancelled(true);
				}
			}
		}
		else if (event.getView().getTopInventory().getType() == InventoryType.ENDER_CHEST)
		{
			final User user = ess.getUser(event.getWhoClicked());
			if (user.isEnderSee() && (!user.isAuthorized("essentials.enderchest.modify")))
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
		else if (event.getView().getTopInventory().getType() == InventoryType.ENDER_CHEST)
		{
			final User user = ess.getUser(event.getPlayer());
			user.setEnderSee(false);
		}
	}
}