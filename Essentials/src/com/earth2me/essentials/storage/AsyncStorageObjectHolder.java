package com.earth2me.essentials.storage;

import com.earth2me.essentials.IConf;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.api.IReload;
import java.io.File;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import org.bukkit.Bukkit;


public abstract class AsyncStorageObjectHolder<T extends StorageObject> implements IConf, IStorageObjectHolder<T>, IReload
{
	private transient T data;
	private final transient ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	private final transient Class<T> clazz;
	protected final transient IEssentials ess;

	public AsyncStorageObjectHolder(final IEssentials ess, final Class<T> clazz)
	{
		this.ess = ess;
		this.clazz = clazz;
		try
		{
			this.data = clazz.newInstance();
		}
		catch (Exception ex)
		{
			Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
		}
	}

	public T getData()
	{
		return data;
	}

	public void acquireReadLock()
	{
		rwl.readLock().lock();
	}

	public void acquireWriteLock()
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
		unlock();
	}

	public void unlock()
	{
		if (rwl.isWriteLockedByCurrentThread())
		{
			rwl.writeLock().unlock();
			new StorageObjectDataWriter();
		}
		while (rwl.getReadHoldCount() > 0)
		{
			rwl.readLock().unlock();
		}
	}

	@Override
	public void reloadConfig()
	{
		new StorageObjectDataReader();
	}

	@Override
	public void onReload()
	{
		new StorageObjectDataReader();
	}
	
	public abstract void finishRead();
	
	public abstract void finishWrite();
	
	public abstract File getStorageFile();


	private class StorageObjectDataWriter extends AbstractDelayedYamlFileWriter
	{
		public StorageObjectDataWriter()
		{
			super(ess, getStorageFile());
		}

		@Override
		public StorageObject getObject()
		{
			acquireReadLock();
			return getData();
		}

		@Override
		public void onFinish()
		{
			unlock();
			finishWrite();
		}
	}


	private class StorageObjectDataReader extends AbstractDelayedYamlFileReader<T>
	{
		public StorageObjectDataReader()
		{
			super(ess, getStorageFile(), clazz);
		}

		@Override
		public void onStart()
		{
			rwl.writeLock().lock();
		}

		@Override
		public void onSuccess(final T object)
		{
			if (object != null)
			{
				data = object;
			}
			rwl.writeLock().unlock();
			finishRead();
		}

		@Override
		public void onException()
		{
			if (data == null)
			{
				try
				{
					data = clazz.newInstance();
				}
				catch (Exception ex)
				{
					Bukkit.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
				}
			}
			rwl.writeLock().unlock();
		}
	}
}
