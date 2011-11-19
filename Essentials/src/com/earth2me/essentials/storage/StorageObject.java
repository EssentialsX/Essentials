package com.earth2me.essentials.storage;

import java.io.PrintWriter;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map.Entry;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;


public class StorageObject
{
	protected Class<? extends StorageObject> clazz;

	protected StorageObject()
	{
	}
	private static Map<Class, Constructor> constructors = new HashMap<Class, Constructor>();

	public static <T extends StorageObject> T load(Class<? extends T> clazz, Reader reader)
	{
		Constructor constructor;
		if (constructors.containsKey(clazz))
		{
			constructor = constructors.get(clazz);
		}
		else
		{
			constructor = prepareConstructor(clazz);
			constructors.put(clazz, constructor);
		}

		final Yaml yaml = new Yaml(constructor);
		T ret = (T)yaml.load(reader);
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
		ret.clazz = clazz;
		return ret;
	}

	private static Constructor prepareConstructor(final Class<?> clazz)
	{
		final Constructor constructor = new Constructor(clazz);
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
			final MapType mapType = field.getAnnotation(MapType.class);
			if (mapType != null)
			{
				description.putMapPropertyType(field.getName(), String.class, mapType.value());
				if (StorageObject.class.isAssignableFrom(mapType.value())
					&& !classes.contains(mapType.value()))
				{
					prepareConstructor(constructor, classes, mapType.value());
				}
			}
			if (StorageObject.class.isAssignableFrom(field.getType())
				&& !classes.contains(field.getType()))
			{
				prepareConstructor(constructor, classes, field.getType());
			}
		}
		constructor.addTypeDescription(description);
	}
	private transient Yaml yaml;

	public void save(final PrintWriter writer)
	{
		final DumperOptions ops = new DumperOptions();
		yaml = new Yaml(ops);
		try
		{
			writeToFile(this, writer, 0, clazz);
		}
		catch (IllegalArgumentException ex)
		{
			Logger.getLogger(StorageObject.class.getName()).log(Level.SEVERE, null, ex);
		}
		catch (IllegalAccessException ex)
		{
			Logger.getLogger(StorageObject.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void writeToFile(final Object object, final PrintWriter writer, final int depth, final Class clazz) throws IllegalArgumentException, IllegalAccessException
	{
		for (Field field : clazz.getDeclaredFields())
		{
			final int modifier = field.getModifiers();
			if (Modifier.isPrivate(modifier) && !Modifier.isTransient(modifier) && !Modifier.isStatic(modifier))
			{
				field.setAccessible(true);
				final boolean commentPresent = field.isAnnotationPresent(Comment.class);
				final String name = field.getName();
				if (commentPresent)
				{
					final Comment comments = field.getAnnotation(Comment.class);
					for (String comment : comments.value())
					{
						final String trimmed = comment.trim();
						if (trimmed.isEmpty())
						{
							continue;
						}
						writeIndention(writer, depth);
						writer.print("# ");
						writer.print(trimmed);
						writer.println();
					}
				}

				final Object data = field.get(object);
				if (data == null && !commentPresent)
				{
					continue;
				}
				writeIndention(writer, depth);
				if (data == null && commentPresent)
				{
					writer.print('#');
				}
				writer.print(name);
				writer.print(": ");
				if (data == null && commentPresent)
				{
					writer.println();
					writer.println();
					continue;
				}
				if (data instanceof StorageObject)
				{
					writer.println();
					writeToFile(data, writer, depth + 1, data.getClass());
				}
				else if (data instanceof Map)
				{
					writer.println();
					for (Entry<String, Object> entry : ((Map<String, Object>)data).entrySet())
					{
						final Object value = entry.getValue();
						if (value != null)
						{
							writeIndention(writer, depth + 1);
							writer.print(entry.getKey());
							writer.print(": ");
							if (value instanceof StorageObject)
							{
								writer.println();
								writeToFile(value, writer, depth + 2, value.getClass());
							}
							else if (value instanceof String || value instanceof Boolean || value instanceof Number)
							{
								yaml.dumpAll(Collections.singletonList(value).iterator(), writer);
								writer.println();
							}
							else
							{
								throw new UnsupportedOperationException();
							}

						}
					}
				}
				else if (data instanceof Collection)
				{
					writer.println();
					for (Object entry : (Collection<Object>)data)
					{
						if (entry != null)
						{
							writeIndention(writer, depth + 1);
							writer.print("- ");
							if (entry instanceof String || entry instanceof Boolean || entry instanceof Number)
							{
								yaml.dumpAll(Collections.singletonList(entry).iterator(), writer);
							}
							else
							{
								throw new UnsupportedOperationException();
							}
						}
					}
					writer.println();
				}
				else if (data instanceof String || data instanceof Boolean || data instanceof Number)
				{
					yaml.dumpAll(Collections.singletonList(data).iterator(), writer);
					writer.println();
				}
				else
				{
					throw new UnsupportedOperationException();
				}
			}
		}
	}

	private void writeIndention(final PrintWriter writer, final int depth)
	{
		for (int i = 0; i < depth; i++)
		{
			writer.print("  ");
		}
	}
}
