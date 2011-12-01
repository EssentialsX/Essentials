package com.earth2me.essentials.storage;

import java.io.Reader;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;


public class YamlStorageReader implements IStorageReader
{
	private transient static Map<Class, Yaml> preparedYamls = Collections.synchronizedMap(new HashMap<Class, Yaml>());
	private transient static Map<Class, ReentrantLock> locks = new HashMap<Class, ReentrantLock>();
	private transient final Reader reader;

	public YamlStorageReader(final Reader reader)
	{
		this.reader = reader;
	}

	@Override
	public <T extends StorageObject> T load(final Class<? extends T> clazz)
	{
		Yaml yaml = preparedYamls.get(clazz);
		if (yaml == null)
		{
			yaml = new Yaml(prepareConstructor(clazz));
			preparedYamls.put(clazz, yaml);
		}
		ReentrantLock lock;
		synchronized (locks)
		{
			lock = locks.get(clazz);
			if (lock == null)
			{
				lock = new ReentrantLock();
			}
		}
		T ret;
		lock.lock();
		try
		{
			ret = (T)yaml.load(reader);
		}
		finally
		{
			lock.unlock();
		}
		if (ret == null)
		{
			try
			{
				ret = (T)clazz.newInstance();
			}
			catch (InstantiationException ex)
			{
				Logger.getLogger(StorageObject.class.getName()).log(Level.SEVERE, null, ex);
			}
			catch (IllegalAccessException ex)
			{
				Logger.getLogger(StorageObject.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return ret;
	}

	private static Constructor prepareConstructor(final Class<?> clazz)
	{
		final Constructor constructor = new BukkitConstructor(clazz);
		final Set<Class> classes = new HashSet<Class>();

		prepareConstructor(constructor, classes, clazz);
		return constructor;
	}

	private static void prepareConstructor(final Constructor constructor, final Set<Class> classes, final Class clazz)
	{
		classes.add(clazz);
		final TypeDescription description = new TypeDescription(clazz);
		for (Field field : clazz.getDeclaredFields())
		{
			prepareList(field, description, classes, constructor);
			prepareMap(field, description, classes, constructor);
			if (StorageObject.class.isAssignableFrom(field.getType())
				&& !classes.contains(field.getType()))
			{
				prepareConstructor(constructor, classes, field.getType());
			}
		}
		constructor.addTypeDescription(description);
	}

	private static void prepareList(final Field field, final TypeDescription description, final Set<Class> classes, final Constructor constructor)
	{
		final ListType listType = field.getAnnotation(ListType.class);
		if (listType != null)
		{
			description.putListPropertyType(field.getName(), listType.value());
			if (StorageObject.class.isAssignableFrom(listType.value())
				&& !classes.contains(listType.value()))
			{
				prepareConstructor(constructor, classes, listType.value());
			}
		}
	}

	private static void prepareMap(final Field field, final TypeDescription description, final Set<Class> classes, final Constructor constructor)
	{
		final MapValueType mapType = field.getAnnotation(MapValueType.class);
		if (mapType != null)
		{
			final MapKeyType mapKeyType = field.getAnnotation(MapKeyType.class);
			description.putMapPropertyType(field.getName(),
										   mapKeyType == null ? String.class : mapKeyType.value(),
										   mapType.value());
			if (StorageObject.class.isAssignableFrom(mapType.value())
				&& !classes.contains(mapType.value()))
			{
				prepareConstructor(constructor, classes, mapType.value());
			}
		}
	}
}
