package com.earth2me.essentials.user;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.IUser;
import com.earth2me.essentials.Teleport;
import com.earth2me.essentials.commands.IEssentialsCommand;
import lombok.Cleanup;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;


public class User extends UserBase implements IUser
{
	public User(final Player base, final IEssentials ess)
	{
		super(base, ess);
	}

	public User(final OfflinePlayer offlinePlayer, final IEssentials ess)
	{
		super(offlinePlayer, ess);
	}

	public void example()
	{
		// Cleanup will call close at the end of the function
		@Cleanup
		final User user = this;

		// read lock allows to read data from the user
		user.acquireReadLock();
		final double money = user.getData().getMoney();

		// write lock allows only one thread to modify the data
		user.acquireWriteLock();
		user.getData().setMoney(10 + money);
	}
	
	@Override
	public void finishRead()
	{
	}

	@Override
	public void finishWrite()
	{
	}

	@Override
	public long getLastTeleportTimestamp()
	{
		acquireReadLock();
		try
		{
			return getData().getTimestamps().get("lastteleport");
		}
		finally
		{
			unlock();
		}
	}

	@Override
	public boolean isAuthorized(String node)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean isAuthorized(IEssentialsCommand cmd)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean isAuthorized(IEssentialsCommand cmd, String permissionPrefix)
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setLastTeleportTimestamp(long time)
	{
		acquireWriteLock();
		try
		{
			getData().getTimestamps().put("lastteleport", time);
		}
		finally
		{
			unlock();
		}
	}

	@Override
	public Location getLastLocation()
	{
		acquireReadLock();
		try
		{
			return getData().getLastLocation();
		}
		finally
		{
			unlock();
		}
	}

	@Override
	public double getMoney()
	{
		acquireReadLock();
		try
		{
			return getData().getMoney();
		}
		finally
		{
			unlock();
		}
	}

	@Override
	public void takeMoney(double value)
	{
		acquireWriteLock();
		try
		{
			getData().setMoney(getData().getMoney() - value);
		}
		finally
		{
			unlock();
		}
	}

	@Override
	public void giveMoney(double value)
	{
		acquireWriteLock();
		try
		{
			getData().setMoney(getData().getMoney() + value);
		}
		finally
		{
			unlock();
		}
	}

	@Override
	public String getGroup()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setLastLocation()
	{
		acquireWriteLock();
		try
		{
			getData().setLastLocation(base.getLocation());
		}
		finally
		{
			unlock();
		}
	}

	@Override
	public Location getHome(String name) throws Exception
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Location getHome(Location loc) throws Exception
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean isHidden()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Teleport getTeleport()
	{
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setJail(final String jail)
	{
		acquireWriteLock();
		try
		{
			getData().setJail(jail);
		}
		finally
		{
			unlock();
		}
	}

	@Override
	public boolean canAfford(final double cost)
	{
		final double mon = getMoney();
		if (isAuthorized("essentials.eco.loan"))
		{
			return (mon - cost) >= ess.getSettings().getMinMoney();
		}
		return cost <= mon;
	}
}
