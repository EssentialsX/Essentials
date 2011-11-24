package com.earth2me.essentials.userdata;

import com.earth2me.essentials.storage.YamlStorageReader;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import lombok.Cleanup;

// this is a prototype for locking userdata
public class User
{
	UserData data = new UserData();
	ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

	public void loadUserData()
	{
		data = new YamlStorageReader(null).load(UserData.class);
	}

	public void aquireReadLock()
	{
		rwl.readLock().lock();
	}

	public void aquireWriteLock()
	{
		while (rwl.getReadHoldCount() > 0)
		{
			rwl.readLock().unlock();
		}
		rwl.writeLock().lock();
		rwl.readLock().lock();
	}

	public void close()
	{
		if (rwl.isWriteLockedByCurrentThread())
		{
			scheduleSaving();
			rwl.writeLock().unlock();
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
		double i = user.data.getMoney();

		// write lock allows only one thread to modify the data
		user.aquireWriteLock();
		user.data.setMoney(10 + user.data.getMoney());
	}

	private void scheduleSaving()
	{
		System.out.println("Schedule saving...");
	}
}
