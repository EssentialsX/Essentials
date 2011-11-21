package com.earth2me.essentials;

import static com.earth2me.essentials.I18n._;
import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;


public class Jail extends BlockListener implements IConf
{
	private static final Logger logger = Logger.getLogger("Minecraft");
	private final EssentialsConf config;
	private final IEssentials ess;

	public Jail(IEssentials ess)
	{
		this.ess = ess;
		config = new EssentialsConf(new File(ess.getDataFolder(), "jail.yml"));
		config.load();
	}

	public void setJail(Location loc, String jailName) throws Exception
	{
		config.setProperty(jailName.toLowerCase(Locale.ENGLISH), loc);
		config.save();
	}

	public Location getJail(String jailName) throws Exception
	{
		if (jailName == null || config.getProperty(jailName.toLowerCase(Locale.ENGLISH)) == null)
		{
			throw new Exception(_("jailNotExist"));
		}

		Location loc = config.getLocation(jailName.toLowerCase(Locale.ENGLISH), ess.getServer());
		return loc;
	}

	public void sendToJail(User user, String jail) throws Exception
	{
		if (!(user.getBase() instanceof OfflinePlayer))
		{
			user.getTeleport().now(getJail(jail));
		}
		user.setJail(jail);
	}

	public void delJail(String jail) throws Exception
	{
		config.removeProperty(jail.toLowerCase(Locale.ENGLISH));
		config.save();
	}

	public List<String> getJails() throws Exception
	{
		return config.getKeys(null);
	}

	@Override
	public void reloadConfig()
	{
		config.load();
	}

	@Override
	public void onBlockBreak(BlockBreakEvent event)
	{
		User user = ess.getUser(event.getPlayer());
		if (user.isJailed())
		{
			event.setCancelled(true);
		}
	}

	@Override
	public void onBlockPlace(BlockPlaceEvent event)
	{
		User user = ess.getUser(event.getPlayer());
		if (user.isJailed())
		{
			event.setCancelled(true);
		}
	}

	@Override
	public void onBlockDamage(BlockDamageEvent event)
	{
		User user = ess.getUser(event.getPlayer());
		if (user.isJailed())
		{
			event.setCancelled(true);
		}
	}
}
