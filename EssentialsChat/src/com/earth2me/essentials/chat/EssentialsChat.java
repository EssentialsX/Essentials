package com.earth2me.essentials.chat;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.chat.listenerlevel.EssentialsChatPlayerListenerHighest;
import com.earth2me.essentials.chat.listenerlevel.EssentialsChatPlayerListenerLowest;
import com.earth2me.essentials.chat.listenerlevel.EssentialsChatPlayerListenerNormal;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class EssentialsChat extends JavaPlugin
{
	private static final Logger LOGGER = Logger.getLogger("Minecraft");

	@Override
	public void onEnable()
	{
		final PluginManager pluginManager = getServer().getPluginManager();
		final IEssentials ess = (IEssentials)pluginManager.getPlugin("Essentials3");
		if (!this.getDescription().getVersion().equals(ess.getDescription().getVersion()))
		{
			LOGGER.log(Level.WARNING, _("versionMismatchAll"));
		}
		if (!ess.isEnabled())
		{
			this.setEnabled(false);
			return;
		}

		final Map<PlayerChatEvent, ChatStore> chatStore = new HashMap<PlayerChatEvent, ChatStore>();

		final EssentialsChatPlayerListenerLowest playerListenerLowest = new EssentialsChatPlayerListenerLowest(getServer(), ess, chatStore);
		final EssentialsChatPlayerListenerNormal playerListenerNormal = new EssentialsChatPlayerListenerNormal(getServer(), ess, chatStore);
		final EssentialsChatPlayerListenerHighest playerListenerHighest = new EssentialsChatPlayerListenerHighest(getServer(), ess, chatStore);
		pluginManager.registerEvents(playerListenerLowest, this);
		pluginManager.registerEvents(playerListenerNormal, this);
		pluginManager.registerEvents(playerListenerHighest, this);

		final EssentialsLocalChatEventListener localChatListener = new EssentialsLocalChatEventListener(getServer(), ess);
		pluginManager.registerEvents(localChatListener, this);
	}

	@Override
	public void onDisable()
	{
	}
}
