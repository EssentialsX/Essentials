package com.earth2me.essentials.storage;

import com.earth2me.essentials.api.IEssentials;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import org.bukkit.Bukkit;


public abstract class AsyncStorageObjectHolder<T extends StorageObject> implements IStorageObjectHolder<T>
{
	private transient T data;
	private final transient ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	private final transient Class<T> clazz;
	protected final transient IEssentials ess;
	private final transient StorageObjectDataWriter writer = new StorageObjectDataWriter();
	private final transient StorageObjectDataReader reader = new StorageObjectDataReader();
	private final transient AtomicBoolean loaded = new AtomicBoolean(false);

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

	/**
	 * Warning: If you access this method, you have to acquire a read or write lock first
	 * 
	 * 
	 * @return Object storing all the data
	 */
	@Override
	public T getData()
	{
		if (!loaded.get())
		{
			reader.schedule(true);
		}
		return data;
	}

	@Override
	public void acquireReadLock()
	{
		rwl.readLock().lock();
	}

	@Override
	public void acquireWriteLock()
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
		unlock();
	}

	@Override
	public void unlock()
	{
		if (rwl.isWriteLockedByCurrentThread())
		{
			rwl.writeLock().unlock();
			writer.schedule();
		}
		while (rwl.getReadHoldCount() > 0)
		{
			rwl.readLock().unlock();
		}
	}

	@Override
	public void onReload()
	{
		reader.schedule(false);
	}

	public abstract File getStorageFile() throws IOException;


	private class StorageObjectDataWriter extends AbstractDelayedYamlFileWriter
	{
		public StorageObjectDataWriter()
		{
			super(ess);
		}

		@Override
		public File getFile() throws IOException
		{
			return getStorageFile();
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
		}
	}


	private class StorageObjectDataReader extends AbstractDelayedYamlFileReader<T>
	{
		public StorageObjectDataReader()
		{
			super(ess, clazz);
		}

		@Override
		public File onStart() throws IOException
		{
			final File file = getStorageFile();
			rwl.writeLock().lock();
			return file;
		}

		@Override
		public void onSuccess(final T object)
		{
			if (object != null)
			{
				data = object;
			}
			rwl.writeLock().unlock();
			loaded.set(true);
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
			loaded.set(true);
		}
	}
}
