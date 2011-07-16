package com.earth2me.essentials.spawn;


import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.Util;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class EssentialsSpawn extends JavaPlugin
{
	private static final Logger LOGGER = Logger.getLogger("Minecraft");
	private transient IEssentials ess;

	public void onEnable()
	{
		final PluginManager pluginManager = getServer().getPluginManager();
		ess = (IEssentials)pluginManager.getPlugin("Essentials");
		final EssentialsSpawnPlayerListener playerListener = new EssentialsSpawnPlayerListener(ess);
		pluginManager.registerEvent(Type.PLAYER_RESPAWN, playerListener, Priority.Low, this);
		pluginManager.registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Low, this);


		if (!this.getDescription().getVersion().equals(ess.getDescription().getVersion()))
		{
			LOGGER.log(Level.WARNING, Util.i18n("versionMismatchAll"));
		}
		LOGGER.info(Util.format("loadinfo", this.getDescription().getName(), this.getDescription().getVersion(), "essentials team"));
	}

	public void onDisable()
	{
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args)
	{
		return ess.onCommandEssentials(sender, command, commandLabel, args, EssentialsSpawn.class.getClassLoader(), "com.earth2me.essentials.spawn.Command", "essentials.");
	}
}
