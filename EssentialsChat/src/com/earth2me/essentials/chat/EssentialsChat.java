package com.earth2me.essentials.chat;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.Util;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class EssentialsChat extends JavaPlugin
{
	private static final Logger LOGGER = Logger.getLogger("Minecraft");

	public void onEnable()
	{
		final PluginManager pluginManager = getServer().getPluginManager();

		EssentialsChatPlayerListener.checkFactions(pluginManager);

		final EssentialsChatPlayerListener playerListener = new EssentialsChatPlayerListener(getServer());
		pluginManager.registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Lowest, this);
		pluginManager.registerEvent(Type.PLAYER_CHAT, playerListener, Priority.Highest, this);
		if (!this.getDescription().getVersion().equals(Essentials.getStatic().getDescription().getVersion()))
		{
			LOGGER.log(Level.WARNING, Util.i18n("versionMismatchAll"));
		}
		LOGGER.info(Util.format("loadinfo", this.getDescription().getName(), this.getDescription().getVersion(), Essentials.AUTHORS));
	}

	public void onDisable()
	{
	}
}
