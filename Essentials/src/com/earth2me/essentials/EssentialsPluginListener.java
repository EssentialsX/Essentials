package com.earth2me.essentials;

import com.earth2me.essentials.perm.BPermissionsHandler;
import com.earth2me.essentials.perm.ConfigPermissionsHandler;
import com.earth2me.essentials.perm.Permissions3Handler;
import com.earth2me.essentials.perm.Permissions2Handler;
import com.earth2me.essentials.perm.PermissionsBukkitHandler;
import com.earth2me.essentials.perm.PermissionsExHandler;
import com.earth2me.essentials.perm.SuperpermsHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;


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
				final Plugin permissionsBukkitPlugin = pm.getPlugin("PermissionsBukkit");
				final Plugin bPermissionsPlugin = pm.getPlugin("bPermissions");
				if (permissionsBukkitPlugin != null && permissionsBukkitPlugin.isEnabled())
				{
					if (!(ess.getPermissionsHandler() instanceof PermissionsBukkitHandler))
					{
						LOGGER.log(Level.INFO, "Essentials: Using PermissionsBukkit based permissions.");
						ess.setPermissionsHandler(new PermissionsBukkitHandler(permissionsBukkitPlugin));
					}
				}
				else if (bPermissionsPlugin != null && bPermissionsPlugin.isEnabled())
				{
					if (!(ess.getPermissionsHandler() instanceof BPermissionsHandler))
					{
						LOGGER.log(Level.INFO, "Essentials: Using bPermissions based permissions.");
						ess.setPermissionsHandler(new BPermissionsHandler());
					}
				}
				else if (ess.getSettings().useBukkitPermissions())
				{
					if (!(ess.getPermissionsHandler() instanceof SuperpermsHandler))
					{
						LOGGER.log(Level.INFO, "Essentials: Using superperms based permissions.");
						ess.setPermissionsHandler(new SuperpermsHandler());
					}
				}
				else
				{
					if (!(ess.getPermissionsHandler() instanceof ConfigPermissionsHandler))
					{
						LOGGER.log(Level.INFO, "Essentials: Using config based permissions. Enable superperms in config.");
						ess.setPermissionsHandler(new ConfigPermissionsHandler(ess));
					}
				}
			}
			else
			{
				if (permissionsPlugin.getDescription().getVersion().charAt(0) == '3')
				{
					if (!(ess.getPermissionsHandler() instanceof Permissions3Handler))
					{
						LOGGER.log(Level.INFO, "Essentials: Using Permissions 3 based permissions.");
						ess.setPermissionsHandler(new Permissions3Handler(permissionsPlugin));
					}
				}
				else
				{
					if (!(ess.getPermissionsHandler() instanceof Permissions2Handler))
					{
						LOGGER.log(Level.INFO, "Essentials: Using Permissions 2 based permissions.");
						ess.setPermissionsHandler(new Permissions2Handler(permissionsPlugin));
					}
				}
			}
		}
		else
		{
			if (!(ess.getPermissionsHandler() instanceof PermissionsExHandler))
			{
				LOGGER.log(Level.INFO, "Essentials: Using PermissionsEx based permissions.");
				ess.setPermissionsHandler(new PermissionsExHandler());
			}
		}
	}

	@Override
	public void reloadConfig()
	{
		checkPermissions();
	}
}
