package com.earth2me.essentials.update.chat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public final class UsernameUtil
{
	private static final Pattern CB_PATTERN = Pattern.compile("git-Bukkit-([0-9]+).([0-9]+).([0-9]+)-[0-9]+-[0-9a-z]+-b([0-9]+)jnks.*");

	private UsernameUtil()
	{
	}

	public static String createUsername(final Player player)
	{
		final StringBuilder nameBuilder = new StringBuilder();
		final Server server = Bukkit.getServer();
		nameBuilder.append(player.getName());

		addCraftBukkitVersion(server, nameBuilder);
		addEssentialsVersion(server, nameBuilder);
		addGroupManagerVersion(server, nameBuilder);
		addPermissionsExVersion(server, nameBuilder);
		addPermissionsBukkitVersion(server, nameBuilder);
		addBPermissionsVersion(server, nameBuilder);
		addPermissionsVersion(server, nameBuilder);

		return nameBuilder.toString();
	}

	private static void addPermissionsVersion(final Server server, final StringBuilder nameBuilder)
	{
		final Plugin perm = server.getPluginManager().getPlugin("Permissions");
		if (perm != null)
		{
			nameBuilder.append(" P");
			if (!perm.isEnabled())
			{
				nameBuilder.append('!');
			}
			nameBuilder.append(perm.getDescription().getVersion());
		}
	}

	private static void addBPermissionsVersion(final Server server, final StringBuilder nameBuilder)
	{
		final Plugin bperm = server.getPluginManager().getPlugin("bPermissions");
		if (bperm != null)
		{
			nameBuilder.append(" BP");
			if (!bperm.isEnabled())
			{
				nameBuilder.append('!');
			}
			nameBuilder.append(bperm.getDescription().getVersion());
		}
	}

	private static void addPermissionsBukkitVersion(final Server server, final StringBuilder nameBuilder)
	{
		final Plugin permb = server.getPluginManager().getPlugin("PermissionsBukkit");
		if (permb != null)
		{
			nameBuilder.append(" PB");
			if (!permb.isEnabled())
			{
				nameBuilder.append('!');
			}
			nameBuilder.append(permb.getDescription().getVersion());
		}
	}

	private static void addPermissionsExVersion(final Server server, final StringBuilder nameBuilder)
	{
		final Plugin pex = server.getPluginManager().getPlugin("PermissionsEx");
		if (pex != null)
		{
			nameBuilder.append(" PEX");
			if (!pex.isEnabled())
			{
				nameBuilder.append('!');
			}
			nameBuilder.append(pex.getDescription().getVersion());
		}
	}

	private static void addGroupManagerVersion(final Server server, final StringBuilder nameBuilder)
	{
		final Plugin groupManager = server.getPluginManager().getPlugin("GroupManager");
		if (groupManager != null)
		{
			nameBuilder.append(" GM");
			if (!groupManager.isEnabled())
			{
				nameBuilder.append('!');
			}
		}
	}

	private static void addEssentialsVersion(final Server server, final StringBuilder nameBuilder)
	{
		final Plugin essentials = server.getPluginManager().getPlugin("Essentials");
		if (essentials != null)
		{
			nameBuilder.append(" ESS");
			nameBuilder.append(essentials.getDescription().getVersion());
		}
	}

	private static void addCraftBukkitVersion(final Server server, final StringBuilder nameBuilder)
	{
		final Matcher versionMatch = CB_PATTERN.matcher(server.getVersion());
		if (versionMatch.matches())
		{
			nameBuilder.append(" CB");
			nameBuilder.append(versionMatch.group(4));
		}
	}
}
