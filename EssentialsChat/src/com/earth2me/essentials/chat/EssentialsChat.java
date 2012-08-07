package com.earth2me.essentials.chat;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.IEssentials;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class EssentialsChat extends JavaPlugin
{
	private static final Logger LOGGER = Logger.getLogger("Minecraft");
	private transient Map<String, IEssentialsChatListener> chatListener;

	@Override
	public void onEnable()
	{
		final PluginManager pluginManager = getServer().getPluginManager();
		final IEssentials ess = (IEssentials)pluginManager.getPlugin("Essentials");
		if (!this.getDescription().getVersion().equals(ess.getDescription().getVersion()))
		{
			LOGGER.log(Level.WARNING, _("versionMismatchAll"));
		}
		if (!ess.isEnabled())
		{
			this.setEnabled(false);
			return;
		}

		chatListener = new ConcurrentSkipListMap<String, IEssentialsChatListener>();
		final Map<AsyncPlayerChatEvent, ChatStore> chatStore = Collections.synchronizedMap(new HashMap<AsyncPlayerChatEvent, ChatStore>());


		final EssentialsChatPlayerListenerLowest playerListenerLowest = new EssentialsChatPlayerListenerLowest(getServer(), ess, chatListener, chatStore);
		final EssentialsChatPlayerListenerNormal playerListenerNormal = new EssentialsChatPlayerListenerNormal(getServer(), ess, chatListener, chatStore);
		final EssentialsChatPlayerListenerHighest playerListenerHighest = new EssentialsChatPlayerListenerHighest(getServer(), ess, chatListener, chatStore);
		pluginManager.registerEvents(playerListenerLowest, this);
		pluginManager.registerEvents(playerListenerNormal, this);
		pluginManager.registerEvents(playerListenerHighest, this);

	}

	@Override
	public void onDisable()
	{
		if (chatListener != null)
		{
			chatListener.clear();
		}
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
