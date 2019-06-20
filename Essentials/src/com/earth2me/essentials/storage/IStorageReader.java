package com.earth2me.essentials.storage;


/**
 * <p>IStorageReader interface.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public interface IStorageReader {
    /**
     * <p>load.</p>
     *
     * @param clazz a {@link java.lang.Class} object.
     * @param <T> a T object.
     * @return a T object.
     * @throws com.earth2me.essentials.storage.ObjectLoadException if any.
     */
    <T extends StorageObject> T load(final Class<? extends T> clazz) throws ObjectLoadException;
}
