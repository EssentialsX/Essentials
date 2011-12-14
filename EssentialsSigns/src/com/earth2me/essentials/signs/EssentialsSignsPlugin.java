package com.earth2me.essentials.signs;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.IEssentials;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class EssentialsSignsPlugin extends JavaPlugin
{
	private static final transient Logger LOGGER = Bukkit.getLogger();
	private transient IEssentials ess;

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
		
		final SignBlockListener signBlockListener = new SignBlockListener(ess);
		pluginManager.registerEvent(Event.Type.SIGN_CHANGE, signBlockListener, Event.Priority.Highest, this);
		pluginManager.registerEvent(Event.Type.BLOCK_PLACE, signBlockListener, Event.Priority.Low, this);
		pluginManager.registerEvent(Event.Type.BLOCK_BREAK, signBlockListener, Event.Priority.Highest, this);
		pluginManager.registerEvent(Event.Type.BLOCK_IGNITE, signBlockListener, Event.Priority.Low, this);
		pluginManager.registerEvent(Event.Type.BLOCK_BURN, signBlockListener, Event.Priority.Low, this);
		pluginManager.registerEvent(Event.Type.BLOCK_PISTON_EXTEND, signBlockListener, Event.Priority.Low, this);
		pluginManager.registerEvent(Event.Type.BLOCK_PISTON_RETRACT, signBlockListener, Event.Priority.Low, this);

		final SignPlayerListener signPlayerListener = new SignPlayerListener(ess);
		pluginManager.registerEvent(Event.Type.PLAYER_INTERACT, signPlayerListener, Event.Priority.Low, this);

		final SignEntityListener signEntityListener = new SignEntityListener(ess);
		pluginManager.registerEvent(Event.Type.ENTITY_EXPLODE, signEntityListener, Event.Priority.Low, this);
		pluginManager.registerEvent(Event.Type.ENDERMAN_PICKUP, signEntityListener, Event.Priority.Low, this);

		
		LOGGER.info(_("loadinfo", this.getDescription().getName(), this.getDescription().getVersion(), "essentials team"));
	}

	public void onDisable()
	{
	}
}
