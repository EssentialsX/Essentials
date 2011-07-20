package com.earth2me.essentials;

import com.earth2me.essentials.register.payment.Methods;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;


public class EssentialsPluginListener extends ServerListener
{
	private final transient IEssentials ess;
	private static final Logger LOGGER = Logger.getLogger("Minecraft");

	public EssentialsPluginListener(final IEssentials ess)
	{
		this.ess = ess;
	}

	@Override
	public void onPluginEnable(final PluginEnableEvent event)
	{
		checkPermissions();
		if (!ess.getPaymentMethod().hasMethod() && ess.getPaymentMethod().setMethod(event.getPlugin()))
		{
			LOGGER.log(Level.INFO, "[Essentials] Payment method found (" + ess.getPaymentMethod().getMethod().getName() + " version: " + ess.getPaymentMethod().getMethod().getVersion() + ")");

		}
	}

	@Override
	public void onPluginDisable(PluginDisableEvent event)
	{
		checkPermissions();
		// Check to see if the plugin thats being disabled is the one we are using
		if (ess.getPaymentMethod() != null && ess.getPaymentMethod().hasMethod() && ess.getPaymentMethod().checkDisabled(event.getPlugin()))
		{
			LOGGER.log(Level.INFO, "[Essentials] Payment method was disabled. No longer accepting payments.");
		}
	}
	
	private void checkPermissions()
	{
		final PluginManager pm = ess.getServer().getPluginManager();
		final Plugin permissionsExPlugin = pm.getPlugin("PermissionsEx");

		if (permissionsExPlugin == null || !permissionsExPlugin.isEnabled())
		{
			final Plugin permissionsPlugin = pm.getPlugin("Permissions");
			if (permissionsPlugin == null || !permissionsPlugin.isEnabled())
			{
				if (ess.getSettings().useBukkitPermissions())
				{
					ess.setPermissionsHandler(new BukkitPermissionsHandler());
				}
				else
				{
					ess.setPermissionsHandler(new ConfigPermissionsHandler(ess));
				}
			}
			else
			{
				if (permissionsPlugin.getDescription().getVersion().charAt(0) == '3')
				{
					ess.setPermissionsHandler(new Permissions3Handler(permissionsPlugin));
				}
				else
				{
					ess.setPermissionsHandler(new Permissions2Handler(permissionsPlugin));
				}
			}
		}
		else
		{
			ess.setPermissionsHandler(new PermissionsExHandler());
		}
	}
}
