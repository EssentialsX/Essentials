package com.neximation.essentials.storage;


public interface IStorageObjectHolder<T extends StorageObject> {
    T getData();

    void acquireReadLock();

    void acquireWriteLock();

    void close();

    void unlock();
}
