package com.earth2me.essentials.listener;

import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.api.IReload;
import com.earth2me.essentials.api.ISettings;
import com.earth2me.essentials.perm.GMGroups;
import com.earth2me.essentials.perm.VaultGroups;
import com.earth2me.essentials.register.payment.Methods;
import com.earth2me.essentials.settings.General;
import com.earth2me.essentials.settings.GroupsHolder;
import java.util.logging.Level;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;


public class EssentialsPluginListener implements Listener, IReload
{
	private final transient IEssentials ess;

	public EssentialsPluginListener(final IEssentials ess)
	{
		super();
		this.ess = ess;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPluginEnable(final PluginEnableEvent event)
	{
		checkGroups();
		//ess.getPermissionsHandler().checkPermissions();
		ess.getCommandHandler().addPlugin(event.getPlugin());
		if (!Methods.hasMethod() && Methods.setMethod(ess.getServer().getPluginManager()))
		{
			ess.getLogger().log(Level.INFO, "Payment method found ({0} version: {1})", new Object[]
					{
						Methods.getMethod().getName(), Methods.getMethod().getVersion()
					});
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPluginDisable(final PluginDisableEvent event)
	{
		checkGroups();
		//ess.getPermissionsHandler().checkPermissions();
		ess.getCommandHandler().removePlugin(event.getPlugin());
		// Check to see if the plugin thats being disabled is the one we are using
		if (Methods.hasMethod() && Methods.checkDisabled(event.getPlugin()))
		{
			Methods.reset();
			ess.getLogger().log(Level.INFO, "Payment method was disabled. No longer accepting payments.");
		}
	}

	@Override
	public void onReload()
	{
		//ess.getPermissionsHandler().setUseSuperperms(ess.getSettings().useBukkitPermissions());
	}

	private void checkGroups()
	{
		ISettings settings = ess.getSettings();
		settings.acquireReadLock();
		General.GroupStorage storage = General.GroupStorage.FILE;
		try
		{
			storage = settings.getData().getGeneral().getGroupStorage();
		}
		finally
		{
			settings.unlock();
		}
		if (storage == General.GroupStorage.GROUPMANAGER)
		{
			Plugin groupManager = ess.getServer().getPluginManager().getPlugin("GroupManager");
			if (groupManager != null && groupManager.isEnabled() && !(ess.getGroups() instanceof GMGroups))
			{
				if (ess.getGroups() instanceof GroupsHolder)
				{
					ess.removeReloadListener((GroupsHolder)ess.getGroups());
				}
				ess.setGroups(new GMGroups(ess, groupManager));
				return;
			}
		}
		if (storage == General.GroupStorage.VAULT)
		{
			Plugin vault = ess.getServer().getPluginManager().getPlugin("Vault");
			if (vault != null && vault.isEnabled() && !(ess.getGroups() instanceof VaultGroups))
			{
				if (ess.getGroups() instanceof GroupsHolder)
				{
					ess.removeReloadListener((GroupsHolder)ess.getGroups());
				}
				ess.setGroups(new VaultGroups(ess));
				return;
			}
		}
		if (!(ess.getGroups() instanceof GroupsHolder))
		{
			ess.setGroups(new GroupsHolder(ess));
			ess.addReloadListener((GroupsHolder)ess.getGroups());
		}
	}
}
