package com.earth2me.essentials.storage;


/**
 * <p>IStorageObjectHolder interface.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public interface IStorageObjectHolder<T extends StorageObject> {
    /**
     * <p>getData.</p>
     *
     * @return a T object.
     */
    T getData();

    /**
     * <p>acquireReadLock.</p>
     */
    void acquireReadLock();

    /**
     * <p>acquireWriteLock.</p>
     */
    void acquireWriteLock();

    /**
     * <p>close.</p>
     */
    void close();

    /**
     * <p>unlock.</p>
     */
    void unlock();
}
