package com.earth2me.essentials.storage;

import com.earth2me.essentials.api.IReload;


public interface IStorageObjectHolder<T extends StorageObject> extends IReload
{
	T getData();

	void acquireReadLock();

	void acquireWriteLock();

	void close();

	void unlock();
}
