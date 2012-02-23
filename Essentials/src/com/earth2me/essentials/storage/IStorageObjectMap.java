package com.earth2me.essentials.storage;

import com.earth2me.essentials.api.IReload;
import com.earth2me.essentials.api.InvalidNameException;
import java.io.File;
import java.util.Set;


interface IStorageObjectMap<I> extends IReload
{
	boolean objectExists(final String name);

	I getObject(final String name);

	void removeObject(final String name) throws InvalidNameException;

	Set<String> getAllKeys();

	int getKeySize();

	File getStorageFile(final String name) throws InvalidNameException;
}
