package com.earth2me.essentials;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;


public class Jail extends BlockListener implements IConf
{
	private static final Logger logger = Logger.getLogger("Minecraft");
	private EssentialsConf config;
	private Essentials ess;

	public Jail(Essentials ess)
	{
		this.ess = ess;
		config = new EssentialsConf(new File(ess.getDataFolder(), "jail.yml"));
		config.load();
	}

	public void setJail(Location loc, String jailName) throws Exception
	{
		config.setProperty(jailName.toLowerCase(), loc);
		config.save();
	}

	public Location getJail(String jailName) throws Exception
	{
		if (config.getProperty(jailName.toLowerCase()) == null)
		{
			throw new Exception("That jail does not exist");
		}

		Location loc = config.getLocation(jailName.toLowerCase(), Essentials.getStatic().getServer());
		return loc;
	}

	public void sendToJail(User user, String jail) throws Exception
	{
		user.getTeleport().now(getJail(jail));
		user.setJail(jail);
	}

	public void delJail(String jail) throws Exception
	{
		config.removeProperty(jail.toLowerCase());
		config.save();
	}

	public List<String> getJails() throws Exception
	{
		return config.getKeys(null);
	}

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
