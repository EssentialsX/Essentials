package com.earth2me.essentials.spawn;

import java.io.*;
import java.util.logging.*;
import com.earth2me.essentials.*;
import org.bukkit.command.*;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.*;


public class EssentialsSpawn extends JavaPlugin
{
	public static final String AUTHORS = Essentials.AUTHORS;
	private static final Logger logger = Logger.getLogger("Minecraft");

	public EssentialsSpawn() throws IOException
	{
		
	}

	@SuppressWarnings("LoggerStringConcat")
	public void onEnable()
	{
		Plugin p = this.getServer().getPluginManager().getPlugin("Essentials");
		if (p != null) {
			if (!this.getServer().getPluginManager().isPluginEnabled(p)) {
				this.getServer().getPluginManager().enablePlugin(p);
			}
		}
		EssentialsSpawnPlayerListener playerListener = new EssentialsSpawnPlayerListener();
		getServer().getPluginManager().registerEvent(Type.PLAYER_RESPAWN, playerListener, Priority.Low, this);
		getServer().getPluginManager().registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Low, this);
		
		if (!this.getDescription().getVersion().equals(Essentials.getStatic().getDescription().getVersion())) {
			logger.log(Level.WARNING, "Version mismatch! Please update all Essentials jars to the same version.");
		}
		logger.info("Loaded " + this.getDescription().getName() + " build " + this.getDescription().getVersion() + " maintained by " + AUTHORS);
	}

	public void onDisable()
	{
	}

	@SuppressWarnings(
	{
		"LoggerStringConcat", "CallToThreadDumpStack"
	})
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args)
	{
		try
		{
			Essentials.loadClasses();
			Essentials.previewCommand(sender, command, commandLabel, args);
			return EssentialsSpawnWorker.onCommand(sender, command, commandLabel, args);
		}
		catch (Throwable ex)
		{
			ex.printStackTrace();
			return true;
		}
	}
}
