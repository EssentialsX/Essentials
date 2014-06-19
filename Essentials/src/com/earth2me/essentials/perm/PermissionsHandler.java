package com.earth2me.essentials.perm;

import com.earth2me.essentials.Essentials;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;


public class PermissionsHandler implements IPermissionsHandler
{
	private transient IPermissionsHandler handler = new NullPermissionsHandler();
	private transient String defaultGroup = "default";
	private final transient Essentials ess;
	private static final Logger LOGGER = Logger.getLogger("Essentials");
	private transient boolean useSuperperms = false;

	public PermissionsHandler(final Essentials plugin)
	{
		this.ess = plugin;
	}

	public PermissionsHandler(final Essentials plugin, final boolean useSuperperms)
	{
		this.ess = plugin;
		this.useSuperperms = useSuperperms;
	}

	public PermissionsHandler(final Essentials plugin, final String defaultGroup)
	{
		this.ess = plugin;
		this.defaultGroup = defaultGroup;
	}

	@Override
	public String getGroup(final Player base)
	{
		final long start = System.nanoTime();
		String group = handler.getGroup(base);
		if (group == null)
		{
			group = defaultGroup;
		}
		checkPermLag(start);
		return group;
	}

	@Override
	public List<String> getGroups(final Player base)
	{
		final long start = System.nanoTime();
		List<String> groups = handler.getGroups(base);
		if (groups == null || groups.isEmpty())
		{
			groups = Collections.singletonList(defaultGroup);
		}
		checkPermLag(start);
		return Collections.unmodifiableList(groups);
	}

	@Override
	public boolean canBuild(final Player base, final String group)
	{
		return handler.canBuild(base, group);
	}

	@Override
	public boolean inGroup(final Player base, final String group)
	{
		final long start = System.nanoTime();
		final boolean result = handler.inGroup(base, group);
		checkPermLag(start);
		return result;
	}

	@Override
	public boolean hasPermission(final Player base, final String node)
	{
		return handler.hasPermission(base, node);
	}

	@Override
	public String getPrefix(final Player base)
	{
		final long start = System.nanoTime();
		String prefix = handler.getPrefix(base);
		if (prefix == null)
		{
			prefix = "";
		}
		checkPermLag(start);
		return prefix;
	}

	@Override
	public String getSuffix(final Player base)
	{
		final long start = System.nanoTime();
		String suffix = handler.getSuffix(base);
		if (suffix == null)
		{
			suffix = "";
		}
		checkPermLag(start);
		return suffix;
	}

	public void checkPermissions()
	{
		final PluginManager pluginManager = ess.getServer().getPluginManager();

		final Plugin permExPlugin = pluginManager.getPlugin("PermissionsEx");
		if (permExPlugin != null && permExPlugin.isEnabled())
		{
			if (!(handler instanceof PermissionsExHandler))
			{
				LOGGER.log(Level.INFO, "Essentials: Using PermissionsEx based permissions.");
				handler = new PermissionsExHandler();
			}
			return;
		}

		final Plugin GMplugin = pluginManager.getPlugin("GroupManager");
		if (GMplugin != null && GMplugin.isEnabled())
		{
			if (!(handler instanceof GroupManagerHandler))
			{
				LOGGER.log(Level.INFO, "Essentials: Using GroupManager based permissions.");
				handler = new GroupManagerHandler(GMplugin);
			}
			return;
		}

		final Plugin permBukkitPlugin = pluginManager.getPlugin("PermissionsBukkit");
		if (permBukkitPlugin != null && permBukkitPlugin.isEnabled())
		{
			if (!(handler instanceof PermissionsBukkitHandler))
			{
				LOGGER.log(Level.INFO, "Essentials: Using PermissionsBukkit based permissions.");
				handler = new PermissionsBukkitHandler(permBukkitPlugin);
			}
			return;
		}

		final Plugin simplyPermsPlugin = pluginManager.getPlugin("SimplyPerms");
		if (simplyPermsPlugin != null && simplyPermsPlugin.isEnabled())
		{
			if (!(handler instanceof SimplyPermsHandler))
			{
				LOGGER.log(Level.INFO, "Essentials: Using SimplyPerms based permissions.");
				handler = new SimplyPermsHandler(simplyPermsPlugin);
			}
			return;
		}

		final Plugin privPlugin = pluginManager.getPlugin("Privileges");
		if (privPlugin != null && privPlugin.isEnabled())
		{
			if (!(handler instanceof PrivilegesHandler))
			{
				LOGGER.log(Level.INFO, "Essentials: Using Privileges based permissions.");
				handler = new PrivilegesHandler(privPlugin);
			}
			return;
		}

		final Plugin bPermPlugin = pluginManager.getPlugin("bPermissions");
		if (bPermPlugin != null && bPermPlugin.isEnabled())
		{
			if (!(handler instanceof BPermissions2Handler))
			{
				LOGGER.log(Level.INFO, "Essentials: Using bPermissions2 based permissions.");
				handler = new BPermissions2Handler();
			}
			return;
		}

		final Plugin zPermsPlugin = pluginManager.getPlugin("zPermissions");
		if (zPermsPlugin != null && zPermsPlugin.isEnabled())
		{
			if (!(handler instanceof ZPermissionsHandler))
			{
				LOGGER.log(Level.INFO, "Essentials: Using zPermissions based permissions.");
				handler = new ZPermissionsHandler(ess);
			}
			return;
		}

		if (useSuperperms)
		{
			if (!(handler instanceof SuperpermsHandler))
			{
				LOGGER.log(Level.INFO, "Essentials: Using superperms based permissions.");
				handler = new SuperpermsHandler();
			}
		}
		else
		{
			if (!(handler instanceof ConfigPermissionsHandler))
			{
				LOGGER.log(Level.INFO, "Essentials: Using config file enhanced permissions.");
				LOGGER.log(Level.INFO, "Permissions listed in as player-commands will be given to all users.");
				handler = new ConfigPermissionsHandler(ess);
			}
		}
	}

	public void setUseSuperperms(final boolean useSuperperms)
	{
		this.useSuperperms = useSuperperms;
	}

	public String getName()
	{
		return handler.getClass().getSimpleName().replace("Handler", "");
	}

	private void checkPermLag(long start)
	{
		final long elapsed = System.nanoTime() - start;
		if (elapsed > ess.getSettings().getPermissionsLagWarning())
		{
			ess.getLogger().log(Level.INFO, "Lag Notice - Slow Permissions System (" + getName() + ") Response - Request took over {0}ms!", elapsed / 1000000.0);
		}
	}
}
