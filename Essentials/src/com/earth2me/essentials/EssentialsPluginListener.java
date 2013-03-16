package com.earth2me.essentials;

import java.util.logging.Level;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;


public class EssentialsPluginListener implements Listener, IConf
{
	private final transient IEssentials ess;

	public EssentialsPluginListener(final IEssentials ess)
	{
		this.ess = ess;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPluginEnable(final PluginEnableEvent event)
	{
		if (event.getPlugin().getName().equals("EssentialsChat"))
		{
			ess.getSettings().setEssentialsChatActive(true);
		}
		ess.getPermissionsHandler().checkPermissions();
		ess.getAlternativeCommandsHandler().addPlugin(event.getPlugin());
		if (!ess.getPaymentMethod().hasMethod() && ess.getPaymentMethod().setMethod(ess.getServer().getPluginManager()))
		{
			ess.getLogger().log(Level.INFO, "Payment method found (" + ess.getPaymentMethod().getMethod().getLongName() + " version: " + ess.getPaymentMethod().getMethod().getVersion() + ")");
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void cleanupOpenInventories(final PluginDisableEvent event)
	{
		for (Player player : ess.getServer().getOnlinePlayers())
		{
			User user = ess.getUser(player);
			if (user.isRecipeSee())
			{				
				user.getPlayer().getOpenInventory().getTopInventory().clear();
				user.getPlayer().getOpenInventory().close();
				user.setRecipeSee(false);
			}
			if (user.isInvSee())
			{
				user.getPlayer().getOpenInventory().close();
				user.setInvSee(false);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPluginDisable(final PluginDisableEvent event)
	{
		if (event.getPlugin().getName().equals("EssentialsChat"))
		{
			ess.getSettings().setEssentialsChatActive(false);
		}
		ess.getPermissionsHandler().checkPermissions();
		ess.getAlternativeCommandsHandler().removePlugin(event.getPlugin());
		// Check to see if the plugin thats being disabled is the one we are using
		if (ess.getPaymentMethod() != null && ess.getPaymentMethod().hasMethod() && ess.getPaymentMethod().checkDisabled(event.getPlugin()))
		{
			ess.getPaymentMethod().reset();
			ess.getLogger().log(Level.INFO, "Payment method was disabled. No longer accepting payments.");
		}
	}

	@Override
	public void reloadConfig()
	{
		ess.getPermissionsHandler().setUseSuperperms(ess.getSettings().useBukkitPermissions());
		ess.getPermissionsHandler().checkPermissions();
	}
}
