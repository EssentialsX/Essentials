package com.earth2me.essentials;

import com.earth2me.essentials.api.IJails;
import com.earth2me.essentials.storage.AsyncStorageObjectHolder;
import static com.earth2me.essentials.I18n._;
import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.block.*;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.plugin.PluginManager;


public class Jails extends AsyncStorageObjectHolder<com.earth2me.essentials.settings.Jails> implements IJails
{
	private static final transient Logger LOGGER = Bukkit.getLogger();

	public Jails(final IEssentials ess)
	{
		super(ess, com.earth2me.essentials.settings.Jails.class);
		reloadConfig();
		registerListeners();
	}

	private void registerListeners()
	{
		final PluginManager pluginManager = ess.getServer().getPluginManager();
		final JailBlockListener blockListener = new JailBlockListener();
		final JailPlayerListener playerListener = new JailPlayerListener();
		pluginManager.registerEvent(Type.BLOCK_BREAK, blockListener, Priority.Low, ess);
		pluginManager.registerEvent(Type.BLOCK_DAMAGE, blockListener, Priority.Low, ess);
		pluginManager.registerEvent(Type.BLOCK_PLACE, blockListener, Priority.Low, ess);
		pluginManager.registerEvent(Type.PLAYER_INTERACT, playerListener, Priority.Low, ess);
		pluginManager.registerEvent(Type.PLAYER_RESPAWN, playerListener, Priority.High, ess);
		pluginManager.registerEvent(Type.PLAYER_TELEPORT, playerListener, Priority.High, ess);
		pluginManager.registerEvent(Type.PLAYER_JOIN, playerListener, Priority.High, ess);
	}

	@Override
	public File getStorageFile()
	{
		return new File(ess.getDataFolder(), "jail.yml");
	}

	@Override
	public Location getJail(final String jailName) throws Exception
	{
		acquireReadLock();
		try
		{
			if (getData().getJails() == null || jailName == null
				|| !getData().getJails().containsKey(jailName.toLowerCase(Locale.ENGLISH)))
			{
				throw new Exception(_("jailNotExist"));
			}
			return getData().getJails().get(jailName.toLowerCase(Locale.ENGLISH));
		}
		finally
		{
			unlock();
		}
	}

	@Override
	public Collection<String> getList() throws Exception
	{
		acquireReadLock();
		try
		{
			if (getData().getJails() == null)
			{
				return Collections.emptyList();
			}
			return new ArrayList<String>(getData().getJails().keySet());
		}
		finally
		{
			unlock();
		}
	}

	@Override
	public void removeJail(final String jail) throws Exception
	{
		acquireWriteLock();
		try
		{
			if (getData().getJails() == null)
			{
				return;
			}
			getData().getJails().remove(jail.toLowerCase(Locale.ENGLISH));
		}
		finally
		{
			unlock();
		}
	}

	@Override
	public void sendToJail(final IUser user, final String jail) throws Exception
	{
		acquireReadLock();
		try
		{
			if (!(user.getBase() instanceof OfflinePlayer))
			{
				user.getTeleport().now(getJail(jail), false, TeleportCause.COMMAND);
			}
			user.setJail(jail);
		}
		finally
		{
			unlock();
		}
	}

	@Override
	public void setJail(final String jailName, final Location loc) throws Exception
	{
		acquireWriteLock();
		try
		{
			if (getData().getJails() == null)
			{
				getData().setJails(new HashMap<String, Location>());
			}
			getData().getJails().put(jailName.toLowerCase(Locale.ENGLISH), loc);
		}
		finally
		{
			unlock();
		}
	}


	private class JailBlockListener extends BlockListener
	{
		@Override
		public void onBlockBreak(final BlockBreakEvent event)
		{
			final User user = ess.getUser(event.getPlayer());
			if (user.isJailed())
			{
				event.setCancelled(true);
			}
		}

		@Override
		public void onBlockPlace(final BlockPlaceEvent event)
		{
			final User user = ess.getUser(event.getPlayer());
			if (user.isJailed())
			{
				event.setCancelled(true);
			}
		}

		@Override
		public void onBlockDamage(final BlockDamageEvent event)
		{
			final User user = ess.getUser(event.getPlayer());
			if (user.isJailed())
			{
				event.setCancelled(true);
			}
		}
	}


	private class JailPlayerListener extends PlayerListener
	{
		@Override
		public void onPlayerInteract(final PlayerInteractEvent event)
		{
			final User user = ess.getUser(event.getPlayer());
			if (user.isJailed())
			{
				event.setCancelled(true);
			}
		}

		@Override
		public void onPlayerRespawn(final PlayerRespawnEvent event)
		{
			final User user = ess.getUser(event.getPlayer());
			if (!user.isJailed() || user.getJail() == null || user.getJail().isEmpty())
			{
				return;
			}

			try
			{
				event.setRespawnLocation(getJail(user.getJail()));
			}
			catch (Exception ex)
			{
				LOGGER.log(Level.WARNING, _("returnPlayerToJailError"), ex);
			}
		}

		@Override
		public void onPlayerTeleport(final PlayerTeleportEvent event)
		{
			final User user = ess.getUser(event.getPlayer());
			if (!user.isJailed() || user.getJail() == null || user.getJail().isEmpty())
			{
				return;
			}

			try
			{
				event.setTo(getJail(user.getJail()));
			}
			catch (Exception ex)
			{
				LOGGER.log(Level.WARNING, _("returnPlayerToJailError"), ex);
			}
			user.sendMessage(_("jailMessage"));
		}

		@Override
		public void onPlayerJoin(final PlayerJoinEvent event)
		{
			final User user = ess.getUser(event.getPlayer());
			if (!user.isJailed() || user.getJail() == null || user.getJail().isEmpty())
			{
				return;
			}

			try
			{
				sendToJail(user, user.getJail());
			}
			catch (Exception ex)
			{
				LOGGER.log(Level.WARNING, _("returnPlayerToJailError"), ex);
			}
			user.sendMessage(_("jailMessage"));
		}
	}
}
