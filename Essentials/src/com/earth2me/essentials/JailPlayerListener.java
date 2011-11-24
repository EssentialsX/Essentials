package com.earth2me.essentials;

import static com.earth2me.essentials.I18n._;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.event.player.*;


public class JailPlayerListener extends PlayerListener
{
	private static final Logger LOGGER = Logger.getLogger("Minecraft");
	private final IEssentials ess;

	public JailPlayerListener(IEssentials parent)
	{
		this.ess = parent;
	}

	@Override
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		final User user = ess.getUser(event.getPlayer());
		if (user.isJailed())
		{
			event.setCancelled(true);
		}
	}

	@Override
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		final User user = ess.getUser(event.getPlayer());
		if (user.isJailed() && user.getJail() != null && !user.getJail().isEmpty())
		{
			try
			{
				event.setRespawnLocation(ess.getJail().getJail(user.getJail()));
			}
			catch (Exception ex)
			{
			}
		}
	}

	@Override
	public void onPlayerTeleport(PlayerTeleportEvent event)
	{
		final User user = ess.getUser(event.getPlayer());
		if (!user.isJailed() || user.getJail() == null || user.getJail().isEmpty())
		{
			return;
		}
		try
		{
			event.setTo(ess.getJail().getJail(user.getJail()));
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
		User u = ess.getUser(event.getPlayer());
		if (u.isJailed())
		{
			try
			{
				ess.getJail().sendToJail(u, u.getJail());
			}
			catch (Exception ex)
			{
				LOGGER.log(Level.WARNING, _("returnPlayerToJailError"), ex);
			}
			u.sendMessage(_("jailMessage"));
		}
	}
}
