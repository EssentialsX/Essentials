package com.earth2me.essentials;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;


public class EssentialsPluginListener extends ServerListener implements IConf
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
		ess.getPermissionsHandler().checkPermissions();
		if (!ess.getPaymentMethod().hasMethod() && ess.getPaymentMethod().setMethod(ess.getServer().getPluginManager()))
		{
			LOGGER.log(Level.INFO, "[Essentials] Payment method found (" + ess.getPaymentMethod().getMethod().getName() + " version: " + ess.getPaymentMethod().getMethod().getVersion() + ")");
		}
	}

	@Override
	public void onPluginDisable(final PluginDisableEvent event)
	{
		ess.getPermissionsHandler().checkPermissions();
		// Check to see if the plugin thats being disabled is the one we are using
		if (ess.getPaymentMethod() != null && ess.getPaymentMethod().hasMethod() && ess.getPaymentMethod().checkDisabled(event.getPlugin()))
		{
			ess.getPaymentMethod().reset();
			LOGGER.log(Level.INFO, "[Essentials] Payment method was disabled. No longer accepting payments.");
		}
	}

	@Override
	public void reloadConfig()
	{
		ess.getPermissionsHandler().setUseSuperperms(ess.getSettings().useBukkitPermissions());
		ess.getPermissionsHandler().checkPermissions();
	}
}
