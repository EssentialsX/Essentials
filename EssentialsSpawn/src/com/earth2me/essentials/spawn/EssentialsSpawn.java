package com.earth2me.essentials.spawn;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.IEssentials;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class EssentialsSpawn extends JavaPlugin
{
	private static final Logger LOGGER = Bukkit.getLogger();
	private transient IEssentials ess;
	private transient SpawnStorage spawns;

	public void onEnable()
	{
		final PluginManager pluginManager = getServer().getPluginManager();
		ess = (IEssentials)pluginManager.getPlugin("Essentials");
		if (!this.getDescription().getVersion().equals(ess.getDescription().getVersion()))
		{
			LOGGER.log(Level.WARNING, _("versionMismatchAll"));
		}
		if (!ess.isEnabled())
		{
			this.setEnabled(false);
			return;
		}

		spawns = new SpawnStorage(ess);
		ess.addReloadListener(spawns);

		final EssentialsSpawnPlayerListener playerListener = new EssentialsSpawnPlayerListener(ess, spawns);
		pluginManager.registerEvent(PlayerRespawnEvent.class, playerListener, ess.getSettings().getRespawnPriority(), new EventExecutor()
		{
			@Override
			public void execute(final Listener ll, final Event event) throws EventException
			{
				((EssentialsSpawnPlayerListener)ll).onPlayerRespawn((PlayerRespawnEvent)event);
			}
		}, this);
		pluginManager.registerEvent(PlayerJoinEvent.class, playerListener, ess.getSettings().getRespawnPriority(), new EventExecutor()
		{
			@Override
			public void execute(final Listener ll, final Event event) throws EventException
			{
				((EssentialsSpawnPlayerListener)ll).onPlayerJoin((PlayerJoinEvent)event);
			}
		}, this);
	}

	public void onDisable()
	{
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command command,
							 final String commandLabel, final String[] args)
	{
		return ess.onCommandEssentials(sender, command, commandLabel, args, EssentialsSpawn.class.getClassLoader(), "com.earth2me.essentials.spawn.Command", "essentials.", spawns);
	}
}
