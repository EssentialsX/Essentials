package com.earth2me.essentials.user;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.storage.AbstractDelayedYamlFileWriter;
import com.earth2me.essentials.storage.StorageObject;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import lombok.Cleanup;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

// this is a prototype for locking userdata
public class User extends UserBase implements IOfflineUser
{
	private transient UserData data = new UserData();
	private final transient ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

	public User(final Player base, final IEssentials ess)
	{
		super(base, ess);
	}

	public User(final OfflinePlayer offlinePlayer, final IEssentials ess)
	{
		super(offlinePlayer, ess);
	}

	@Override
	public UserData getData()
	{
		return data;
	}

	@Override
	public void aquireReadLock()
	{
		rwl.readLock().lock();
	}

	@Override
	public void aquireWriteLock()
	{
		while (rwl.getReadHoldCount() > 0)
		{
			rwl.readLock().unlock();
		}
		rwl.writeLock().lock();
		rwl.readLock().lock();
	}

	@Override
	public void close()
	{
		if (rwl.isWriteLockedByCurrentThread())
		{
			rwl.writeLock().unlock();
			scheduleSaving();
		}
		while (rwl.getReadHoldCount() > 0)
		{
			rwl.readLock().unlock();
		}
	}

	public void example()
	{
		// Cleanup will call close at the end of the function
		@Cleanup
		final User user = this;

		// read lock allows to read data from the user
		user.aquireReadLock();
		final double money = user.getData().getMoney();

		// write lock allows only one thread to modify the data
		user.aquireWriteLock();
		user.getData().setMoney(10 + money);
	}

	private void scheduleSaving()
	{
		new UserDataWriter();
	}

	private class UserDataWriter extends AbstractDelayedYamlFileWriter
	{
		public UserDataWriter()
		{
			super(ess, ess.getUserMap().getUserFile(User.this.getName()));
		}

		@Override
		public StorageObject getObject()
		{
			aquireReadLock();
			return getData();
		}

		@Override
		public void onFinish()
		{
			close();
		}
	}
}
