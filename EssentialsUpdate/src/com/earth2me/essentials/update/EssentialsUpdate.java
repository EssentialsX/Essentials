package com.earth2me.essentials.update;

import com.earth2me.essentials.update.UpdateCheck.CheckResult;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;


public class EssentialsUpdate extends JavaPlugin
{
	private transient EssentialsHelp essentialsHelp;
	private transient UpdateProcess updateProcess;

	@Override
	public void onEnable()
	{
		if (!getDataFolder().exists() && !getDataFolder().mkdirs() ) {
			Bukkit.getLogger().severe("Could not create data folder:"+getDataFolder().getPath());
		}
		essentialsHelp = new EssentialsHelp(this);
		essentialsHelp.registerEvents();

		final UpdateCheck updateCheck = new UpdateCheck(this);
		updateProcess = new UpdateProcess(this, updateCheck);
		updateProcess.registerEvents();

		Bukkit.getLogger().info("EssentialsUpdate " + getDescription().getVersion() + " loaded.");

		if (updateCheck.isEssentialsInstalled())
		{
			updateCheck.checkForUpdates();
			final Version myVersion = new Version(getDescription().getVersion());
			if (updateCheck.getResult() == CheckResult.NEW_ESS && myVersion.equals(updateCheck.getNewVersion()))
			{
				Bukkit.getLogger().info("Versions of EssentialsUpdate and Essentials do not match. Starting automatic update.");
				updateProcess.doAutomaticUpdate();
			}
			updateCheck.scheduleUpdateTask();
		}
		else
		{
			Bukkit.getLogger().info("Essentials is ready for installation. Join the game and follow the instructions.");
		}
	}

	@Override
	public void onDisable()
	{
		essentialsHelp.onDisable();
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args)
	{
		if (command.getName().equalsIgnoreCase("essentialsupdate"))
		{
			updateProcess.onCommand(sender);
		}
		if (command.getName().equalsIgnoreCase("essentialshelp"))
		{
			essentialsHelp.onCommand(sender);
		}
		return true;
	}
}
