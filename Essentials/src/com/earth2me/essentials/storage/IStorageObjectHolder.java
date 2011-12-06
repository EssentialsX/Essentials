package com.earth2me.essentials.storage;

import com.earth2me.essentials.user.UserData;


public interface IStorageObjectHolder<T extends StorageObject>
{
	T getData();

	void acquireReadLock();

	void acquireWriteLock();

	void close();

	void unlock();
}
