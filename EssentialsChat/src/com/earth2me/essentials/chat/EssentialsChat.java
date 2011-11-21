package com.earth2me.essentials.chat;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.IEssentials;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class EssentialsChat extends JavaPlugin
{
	private static final Logger LOGGER = Logger.getLogger("Minecraft");
	private Map<String, IEssentialsChatListener> chatListener;

	public void onEnable()
	{
		final PluginManager pluginManager = getServer().getPluginManager();
		final IEssentials ess = (IEssentials)pluginManager.getPlugin("Essentials");

		chatListener = new HashMap<String, IEssentialsChatListener>();

		final EssentialsChatPlayerListenerLowest playerListenerLowest = new EssentialsChatPlayerListenerLowest(getServer(), ess, chatListener);
		final EssentialsChatPlayerListenerNormal playerListenerNormal = new EssentialsChatPlayerListenerNormal(getServer(), ess, chatListener);
		final EssentialsChatPlayerListenerHighest playerListenerHighest = new EssentialsChatPlayerListenerHighest(getServer(), ess, chatListener);
		pluginManager.registerEvent(Type.PLAYER_CHAT, playerListenerLowest, Priority.Lowest, this);
		pluginManager.registerEvent(Type.PLAYER_CHAT, playerListenerNormal, Priority.Normal, this);
		pluginManager.registerEvent(Type.PLAYER_CHAT, playerListenerHighest, Priority.Highest, this);
		if (!this.getDescription().getVersion().equals(ess.getDescription().getVersion()))
		{
			LOGGER.log(Level.WARNING, _("versionMismatchAll"));
		}
		LOGGER.info(_("loadinfo", this.getDescription().getName(), this.getDescription().getVersion(), "essentials team"));
	}

	public void onDisable()
	{
		chatListener.clear();
	}

	public void addEssentialsChatListener(final String plugin, final IEssentialsChatListener listener)
	{
		chatListener.put(plugin, listener);
	}

	public IEssentialsChatListener removeEssentialsChatListener(final String plugin)
	{
		return chatListener.remove(plugin);
	}
}
