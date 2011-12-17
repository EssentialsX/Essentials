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
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
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

		final Map<PlayerChatEvent, String> charges = new HashMap<PlayerChatEvent, String>();


		final EssentialsChatPlayerListenerLowest playerListenerLowest = new EssentialsChatPlayerListenerLowest(getServer(), ess);
		final EssentialsChatPlayerListenerNormal playerListenerNormal = new EssentialsChatPlayerListenerNormal(getServer(), ess, charges);
		final EssentialsChatPlayerListenerHighest playerListenerHighest = new EssentialsChatPlayerListenerHighest(getServer(), ess, charges);
		final EssentialsLocalChatEventListener localChatListener = new EssentialsLocalChatEventListener(getServer(), ess);
		pluginManager.registerEvent(Type.PLAYER_CHAT, playerListenerLowest, Priority.Lowest, this);
		pluginManager.registerEvent(Type.PLAYER_CHAT, playerListenerNormal, Priority.Normal, this);
		pluginManager.registerEvent(Type.PLAYER_CHAT, playerListenerHighest, Priority.Highest, this);
		pluginManager.registerEvent(Type.CUSTOM_EVENT, localChatListener, Priority.Highest, this);

		LOGGER.info(_("loadinfo", this.getDescription().getName(), this.getDescription().getVersion(), "essentials team"));
	}

	@Override
	public void onDisable()
	{
	}
}
