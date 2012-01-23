package com.earth2me.essentials.craftbukkit;

import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;


public class DummyOfflinePlayer implements OfflinePlayer
{
	private final transient String name;

	public DummyOfflinePlayer(String name)
	{
		this.name = name;
	}

	@Override
	public boolean isOnline()
	{
		return false;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public boolean isBanned()
	{
		return false;
	}

	@Override
	public void setBanned(boolean bln)
	{
	}

	@Override
	public boolean isWhitelisted()
	{
		return false;
	}

	@Override
	public void setWhitelisted(boolean bln)
	{
	}

	@Override
	public Player getPlayer()
	{
		return Bukkit.getPlayerExact(name);
	}

	@Override
	public long getFirstPlayed()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public long getLastPlayed()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean hasPlayedBefore()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean isOp()
	{
		return false;
	}

	@Override
	public void setOp(boolean bln)
	{
	}

	@Override
	public Map<String, Object> serialize()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Location getBedSpawnLocation()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
}
