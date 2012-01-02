package com.earth2me.essentials.listener;

import com.earth2me.essentials.api.ISettings;
import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.Util;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.craftbukkit.SetBed;
import com.earth2me.essentials.textreader.IText;
import com.earth2me.essentials.textreader.KeywordReplacer;
import com.earth2me.essentials.textreader.TextInput;
import com.earth2me.essentials.textreader.TextPager;
import com.earth2me.essentials.user.UserData.TimestampType;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Cleanup;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;


public class EssentialsPlayerListener extends PlayerListener
{
	private static final Logger LOGGER = Logger.getLogger("Minecraft");
	private final transient Server server;
	private final transient IEssentials ess;

	public EssentialsPlayerListener(final IEssentials parent)
	{
		super();
		this.ess = parent;
		this.server = parent.getServer();
	}

	@Override
	public void onPlayerRespawn(final PlayerRespawnEvent event)
	{
		final IUser user = ess.getUser(event.getPlayer());
		user.updateCompass();
		user.updateDisplayName();
	}

	@Override
	public void onPlayerChat(final PlayerChatEvent event)
	{
		@Cleanup
		final IUser user = ess.getUser(event.getPlayer());
		user.acquireReadLock();
		if (user.getData().isMuted())
		{
			event.setCancelled(true);
			user.sendMessage(_("playerMuted"));
			LOGGER.info(_("mutedUserSpeaks", user.getName()));
		}
		final Iterator<Player> it = event.getRecipients().iterator();
		while (it.hasNext())
		{
			final IUser player = ess.getUser(it.next());
			if (player.isIgnoringPlayer(user.getName()))
			{
				it.remove();
			}
		}
		user.updateActivity(true);
		user.updateDisplayName();
	}

	@Override
	public void onPlayerMove(final PlayerMoveEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		@Cleanup
		final IUser user = ess.getUser(event.getPlayer());
		user.acquireReadLock();
		@Cleanup
		final ISettings settings = ess.getSettings();
		settings.acquireReadLock();

		if (user.getData().isAfk() && settings.getData().getCommands().getAfk().isFreezeAFKPlayers())
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
		@Cleanup
		final IUser user = ess.getUser(event.getPlayer());
		user.acquireReadLock();
		@Cleanup
		final ISettings settings = ess.getSettings();
		settings.acquireReadLock();
		if (settings.getData().getCommands().getGod().isRemoveOnDisconnect() && user.isGodModeEnabled())
		{
			user.toggleGodModeEnabled();
		}
		if (user.getData().getInventory() != null)
		{
			user.getInventory().setContents(user.getData().getInventory().getBukkitInventory());
			user.getData().setInventory(null);
		}
		user.updateActivity(false);
		user.dispose();
	}

	@Override
	public void onPlayerJoin(final PlayerJoinEvent event)
	{
		ess.getBackup().startTask();
		@Cleanup
		final IUser user = ess.getUser(event.getPlayer());
		user.acquireWriteLock();

		user.updateDisplayName();
		user.getData().setIpAddress(user.getAddress().getAddress().getHostAddress());
		user.updateActivity(false);
		if (user.isAuthorized("essentials.sleepingignored"))
		{
			user.setSleepingIgnored(true);
		}

		@Cleanup
		final ISettings settings = ess.getSettings();
		settings.acquireReadLock();

		if (!settings.getData().getCommands().isDisabled("motd") && user.isAuthorized("essentials.motd"))
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
				if (settings.getData().getGeneral().isDebug())
				{
					LOGGER.log(Level.WARNING, ex.getMessage(), ex);
				}
				else
				{
					LOGGER.log(Level.WARNING, ex.getMessage());
				}
			}
		}

		if (!settings.getData().getCommands().isDisabled("mail") && user.isAuthorized("essentials.mail"))
		{
			final List<String> mail = user.getData().getMails();
			if (mail == null || mail.isEmpty())
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
			LOGGER.log(Level.INFO, "Disconnecting user {0} due to {1}", new Object[]{event.getPlayer().toString(), event.getResult().toString()});
			return;
		}
		@Cleanup
		final IUser user = ess.getUser(event.getPlayer());
		user.acquireWriteLock();
		user.getData().setNpc(false);

		final long currentTime = System.currentTimeMillis();
		final boolean banExpired = user.checkBanTimeout(currentTime);
		user.checkMuteTimeout(currentTime);
		user.checkJailTimeout(currentTime);

		if (!banExpired && (user.isBanned() || event.getResult() == Result.KICK_BANNED))
		{
			final String banReason = user.getData().getBan() == null ? "" : user.getData().getBan().getReason();
			event.disallow(Result.KICK_BANNED, banReason == null || banReason.isEmpty() || banReason.equalsIgnoreCase("ban") ? _("defaultBanReason") : banReason);
			return;
		}

		if (server.getOnlinePlayers().length >= server.getMaxPlayers() && !user.isAuthorized("essentials.joinfullserver"))
		{
			event.disallow(Result.KICK_FULL, _("serverFull"));
			return;
		}
		event.allow();

		user.setTimestamp(TimestampType.LOGIN, System.currentTimeMillis());
		user.updateCompass();
	}

	@Override
	public void onPlayerTeleport(final PlayerTeleportEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}

		@Cleanup
		final ISettings settings = ess.getSettings();
		settings.acquireReadLock();
		final IUser user = ess.getUser(event.getPlayer());
		//There is TeleportCause.COMMMAND but plugins have to actively pass the cause in on their teleports.
		if ((event.getCause() == TeleportCause.PLUGIN || event.getCause() == TeleportCause.COMMAND) && settings.getData().getCommands().getBack().isRegisterBackInListener())
		{
			user.setLastLocation();
		}

		user.updateDisplayName();
		user.updateCompass();
	}

	@Override
	public void onPlayerEggThrow(final PlayerEggThrowEvent event)
	{
		@Cleanup
		final IUser user = ess.getUser(event.getPlayer());
		user.acquireReadLock();
		final ItemStack hand = new ItemStack(Material.EGG, 1);
		if (user.getData().hasUnlimited(hand.getType()))
		{
			user.getInventory().addItem(hand);
			user.updateInventory();
		}
	}

	@Override
	public void onPlayerBucketEmpty(final PlayerBucketEmptyEvent event)
	{
		@Cleanup
		final IUser user = ess.getUser(event.getPlayer());
		user.acquireReadLock();
		if (user.getData().hasUnlimited(event.getBucket()))
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
		final IUser user = ess.getUser(event.getPlayer());
		user.updateActivity(true);
		usePowertools(event);
	}

	private void usePowertools(final PlayerAnimationEvent event)
	{
		if (event.getAnimationType() != PlayerAnimationType.ARM_SWING)
		{
			return;
		}
		@Cleanup
		final IUser user = ess.getUser(event.getPlayer());
		user.acquireReadLock();
		final ItemStack hand = user.getItemInHand();
		if (hand == null || hand.getType() == Material.AIR || !user.getData().isPowerToolsEnabled())
		{
			return;
		}
		final List<String> commandList = user.getData().getPowertool(hand.getType());
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
				user.chat(command.substring(2));
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
		final IUser user = ess.getUser(event.getPlayer());
		final String cmd = event.getMessage().toLowerCase(Locale.ENGLISH).split(" ")[0].replace("/", "").toLowerCase(Locale.ENGLISH);
		final List<String> commands = Arrays.asList("msg", "r", "mail", "m", "t", "emsg", "tell", "er", "reply", "ereply", "email");
		if (commands.contains(cmd))
		{
			for (Player player : ess.getServer().getOnlinePlayers())
			{
				@Cleanup
				IUser spyer = ess.getUser(player);
				spyer.acquireReadLock();
				if (spyer.getData().isSocialspy() && !user.equals(spyer))
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
		@Cleanup
		final ISettings settings = ess.getSettings();
		settings.acquireReadLock();
		if (!settings.getData().getWorldOptions(event.getPlayer().getLocation().getWorld().getName()).isGodmode())
		{
			@Cleanup
			final IUser user = ess.getUser(event.getPlayer());
			user.acquireReadLock();
			if (user.getData().isGodmode())
			{
				user.sendMessage(_("noGodWorldWarning"));
			}
		}
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

		@Cleanup
		final ISettings settings = ess.getSettings();
		settings.acquireReadLock();
		if (settings.getData().getCommands().getHome().isUpdateBedAtDaytime() && event.getClickedBlock().getType() == Material.BED_BLOCK)
		{
			SetBed.setBed(event.getPlayer(), event.getClickedBlock());
		}
	}

	@Override
	public void onPlayerPickupItem(final PlayerPickupItemEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		@Cleanup
		final ISettings settings = ess.getSettings();
		settings.acquireReadLock();
		if (!settings.getData().getCommands().getAfk().isDisableItemPickupWhileAfk())
		{
			return;
		}
		@Cleanup
		final IUser user = ess.getUser(event.getPlayer());
		user.acquireReadLock();
		if (user.getData().isAfk())
		{
			event.setCancelled(true);
		}
	}
}
