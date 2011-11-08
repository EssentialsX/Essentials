package com.earth2me.essentials.update;

import com.earth2me.essentials.update.UpdateCheck.CheckResult;
import java.util.logging.Level;
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
			Bukkit.getLogger().log(Level.SEVERE, "Could not create data folder: {0}", getDataFolder().getPath());
		}
		essentialsHelp = new EssentialsHelp(this);
		essentialsHelp.registerEvents();

		final UpdateCheck updateCheck = new UpdateCheck(this);
		updateCheck.checkForUpdates();
		updateProcess = new UpdateProcess(this, updateCheck);
		updateProcess.registerEvents();

		Bukkit.getLogger().log(Level.INFO, "EssentialsUpdate {0} loaded.", getDescription().getVersion());

		if (updateCheck.isEssentialsInstalled())
		{
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
