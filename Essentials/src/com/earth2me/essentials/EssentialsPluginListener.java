package com.earth2me.essentials;

import com.earth2me.essentials.register.payment.Methods;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;


public class EssentialsPluginListener extends ServerListener
{
	private final Methods methods;
	private static final Logger logger = Logger.getLogger("Minecraft");

	public EssentialsPluginListener(Methods methods)
	{
		this.methods = methods;
	}
	
	@Override
	public void onPluginEnable(PluginEnableEvent event)
	{
		if (!methods.hasMethod())
		{
			if (methods.setMethod(event.getPlugin()))
			{
				logger.log(Level.INFO, "[Essentials] Payment method found (" + methods.getMethod().getName() + " version: " + methods.getMethod().getVersion() + ")");
			}
		}
	}

	@Override
	public void onPluginDisable(PluginDisableEvent event)
	{
		// Check to see if the plugin thats being disabled is the one we are using
		if (methods != null && methods.hasMethod())
		{
			if (methods.checkDisabled(event.getPlugin()))
			{
				logger.log(Level.INFO, "[Essentials] Payment method was disabled. No longer accepting payments.");
			}
		}
	}
}
