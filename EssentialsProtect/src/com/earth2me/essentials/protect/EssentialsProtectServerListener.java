package com.earth2me.essentials.protect;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;


public class EssentialsProtectServerListener extends ServerListener
{
	private EssentialsProtect parent;
	Logger log = Logger.getLogger("minecraft");

	public EssentialsProtectServerListener(EssentialsProtect parent)
	{
		this.parent = parent;
	}

	@Override
	public void onPluginEnable(PluginEnableEvent event)
	{
		if ("WorldGuard".equals(event.getPlugin().getDescription().getName()))
		{
			String[] features = new String[] {"disable water flow", "disable lava flow", "disable water bucket flow", "disable all fire spread", "disable tnt explosion", "disable creeper explosion", "disable all damage types"};
			log.log(Level.WARNING, "[EssentialsProtect] WorldGuard was detected, in the near future the following features of Protect will be disabled in favor of WorldGuard's versions");
		
			for (String s : features)
			{
				log.log(Level.WARNING, s);
			}
			
		}
	}
}
