package com.earth2me.essentials.update;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;


public class UpdateCheck
{
	private transient CheckResult result = CheckResult.UNKNOWN;
	private transient Version currentVersion;
	private transient Version newVersion = null;
	private transient int bukkitResult = 0;
	private transient UpdateFile updateFile;
	private final static int CHECK_INTERVAL = 20 * 60 * 60 * 6;
	private final transient Plugin plugin;
	private transient boolean essentialsInstalled;

	public UpdateCheck(final Plugin plugin)
	{
		this.plugin = plugin;
		updateFile = new UpdateFile(plugin);
		checkForEssentials();
	}

	private void checkForEssentials()
	{
		final PluginManager pluginManager = plugin.getServer().getPluginManager();
		final Plugin essentials = pluginManager.getPlugin("Essentials");
		essentialsInstalled = essentials != null;
		if (essentialsInstalled)
		{
			currentVersion = new Version(essentials.getDescription().getVersion());
		}
		else
		{
			if (new File(plugin.getDataFolder().getParentFile(), "Essentials.jar").exists())
			{
				Bukkit.getLogger().severe("Essentials.jar found, but not recognized by Bukkit. Broken download?");
			}
		}
	}

	public void scheduleUpdateTask()
	{
		plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable()
		{
			@Override
			public void run()
			{
				updateFile = new UpdateFile(plugin);
				checkForUpdates();
			}
		}, CHECK_INTERVAL, CHECK_INTERVAL);
	}

	public boolean isEssentialsInstalled()
	{
		return essentialsInstalled;
	}

	public CheckResult getResult()
	{
		return result;
	}

	public int getNewBukkitVersion()
	{
		return bukkitResult;
	}

	public VersionInfo getNewVersionInfo()
	{
		return updateFile.getVersions().get(newVersion);
	}


	public enum CheckResult
	{
		NEW_ESS, NEW_ESS_BUKKIT, NEW_BUKKIT, OK, UNKNOWN
	}

	public void checkForUpdates()
	{
		if (currentVersion == null)
		{
			return;
		}
		final Map<Version, VersionInfo> versions = updateFile.getVersions();
		final int bukkitVersion = getBukkitVersion();
		Version higher = null;
		Version found = null;
		Version lower = null;
		int bukkitHigher = 0;
		int bukkitLower = 0;
		for (Entry<Version, VersionInfo> entry : versions.entrySet())
		{
			final int minBukkit = entry.getValue().getMinBukkit();
			final int maxBukkit = entry.getValue().getMaxBukkit();
			if (minBukkit == 0 || maxBukkit == 0)
			{
				continue;
			}
			if (bukkitVersion <= maxBukkit)
			{
				if (bukkitVersion < minBukkit)
				{
					if (higher == null || higher.compareTo(entry.getKey()) < 0)
					{

						higher = entry.getKey();
						bukkitHigher = minBukkit;
					}
				}
				else
				{
					if (found == null || found.compareTo(entry.getKey()) < 0)
					{
						found = entry.getKey();
					}
				}
			}
			else
			{
				if (lower == null || lower.compareTo(entry.getKey()) < 0)
				{
					lower = entry.getKey();
					bukkitLower = minBukkit;
				}
			}
		}
		if (found != null)
		{
			if (found.compareTo(currentVersion) > 0)
			{
				result = CheckResult.NEW_ESS;
				newVersion = found;
			}
			else
			{
				result = CheckResult.OK;
			}
		}
		else if (higher != null)
		{
			if (higher.compareTo(currentVersion) > 0)
			{
				newVersion = higher;
				result = CheckResult.NEW_ESS_BUKKIT;
				bukkitResult = bukkitHigher;
			}
			else if (higher.compareTo(currentVersion) < 0)
			{
				result = CheckResult.UNKNOWN;
			}
			else
			{
				result = CheckResult.NEW_BUKKIT;
				bukkitResult = bukkitHigher;
			}
		}
		else if (lower != null)
		{
			if (lower.compareTo(currentVersion) > 0)
			{
				result = CheckResult.NEW_ESS_BUKKIT;
				newVersion = lower;
				bukkitResult = bukkitLower;
			}
			else if (lower.compareTo(currentVersion) < 0)
			{
				result = CheckResult.UNKNOWN;
			}
			else
			{
				result = CheckResult.NEW_BUKKIT;
				bukkitResult = bukkitLower;
			}
		}

	}

	private int getBukkitVersion()
	{
		final Matcher versionMatch = Pattern.compile("git-Bukkit-([0-9]+).([0-9]+).([0-9]+)-[0-9]+-[0-9a-z]+-b([0-9]+)jnks.*").matcher(plugin.getServer().getVersion());
		if (versionMatch.matches())
		{
			return Integer.parseInt(versionMatch.group(4));
		}
		throw new NumberFormatException("Bukkit Version changed!");
	}

	public Version getNewVersion()
	{
		return newVersion;
	}
}
