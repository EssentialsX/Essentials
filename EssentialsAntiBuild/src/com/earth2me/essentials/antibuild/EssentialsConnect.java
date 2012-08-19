package com.earth2me.essentials.antibuild;

import com.earth2me.essentials.IConf;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;
import static com.earth2me.essentials.I18n._;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public class EssentialsConnect
{
	private static final Logger LOGGER = Logger.getLogger("Minecraft");
	private final transient IEssentials ess;
	private final transient IAntiBuild protect;

	public EssentialsConnect(Plugin essPlugin, Plugin essProtect)
	{
		if (!essProtect.getDescription().getVersion().equals(essPlugin.getDescription().getVersion()))
		{
			LOGGER.log(Level.WARNING, _("versionMismatchAll"));
		}
		ess = (IEssentials)essPlugin;
		protect = (IAntiBuild)essProtect;
		AntiBuildReloader pr = new AntiBuildReloader();
		pr.reloadConfig();
		ess.addReloadListener(pr);
	}

	public void onDisable()
	{
	}

	public IEssentials getEssentials()
	{
		return ess;
	}

	public void alert(final User user, final String item, final String type)
	{
		final Location loc = user.getLocation();
		final String warnMessage = _("alertFormat", user.getName(), type, item,
									 loc.getWorld().getName() + "," + loc.getBlockX() + ","
									 + loc.getBlockY() + "," + loc.getBlockZ());
		LOGGER.log(Level.WARNING, warnMessage);
		for (Player p : ess.getServer().getOnlinePlayers())
		{
			final User alertUser = ess.getUser(p);
			if (alertUser.isAuthorized("essentials.protect.alerts"))
			{
				alertUser.sendMessage(warnMessage);
			}
		}
	}


	private class AntiBuildReloader implements IConf
	{
		@Override
		public void reloadConfig()
		{
			for (AntiBuildConfig protectConfig : AntiBuildConfig.values())
			{
				if (protectConfig.isList())
				{
					protect.getSettingsList().put(protectConfig, ess.getSettings().getProtectList(protectConfig.getConfigName()));
				}
				else
				{
					protect.getSettingsBoolean().put(protectConfig, ess.getSettings().getProtectBoolean(protectConfig.getConfigName(), protectConfig.getDefaultValueBoolean()));
				}

			}

		}
	}
}
