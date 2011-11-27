package com.earth2me.essentials.storage;

import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.yaml.snakeyaml.Yaml;


public class YamlStorageWriter implements IStorageWriter
{
	private transient static final Pattern NON_WORD_PATTERN = Pattern.compile("\\W");
	private transient final PrintWriter writer;
	private transient static final Yaml YAML = new Yaml();
	
	public YamlStorageWriter(final PrintWriter writer)
	{
		this.writer = writer;
	}
	
	public void save(final StorageObject object)
	{
		try
		{
			writeToFile(object, 0, object.getClass());
		}
		catch (IllegalArgumentException ex)
		{
			Logger.getLogger(YamlStorageWriter.class.getName()).log(Level.SEVERE, null, ex);
		}
		catch (IllegalAccessException ex)
		{
			Logger.getLogger(YamlStorageWriter.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	private void writeToFile(final Object object, final int depth, final Class clazz) throws IllegalAccessException
	{
		for (Field field : clazz.getDeclaredFields())
		{
			final int modifier = field.getModifiers();
			if (Modifier.isPrivate(modifier) && !Modifier.isTransient(modifier) && !Modifier.isStatic(modifier))
			{
				field.setAccessible(true);
				
				final Object data = field.get(object);
				if (writeKey(field, depth, data))
				{
					continue;
				}
				if (data instanceof StorageObject)
				{
					writer.println();
					writeToFile(data, depth + 1, data.getClass());
				}
				else if (data instanceof Map)
				{
					writeMap((Map<Object, Object>)data, depth + 1);
				}
				else if (data instanceof Collection)
				{
					writeCollection((Collection<Object>)data, depth + 1);
				}
				else if (data instanceof Location)
				{
					writeLocation((Location)data, depth + 1);
				}
				else
				{
					writeScalar(data);
					writer.println();
				}
			}
		}
	}
	
	private boolean writeKey(final Field field, final int depth, final Object data)
	{
		final boolean commentPresent = writeComment(field, depth);
		if (data == null && !commentPresent)
		{
			return true;
		}
		writeIndention(depth);
		if (data == null && commentPresent)
		{
			writer.print('#');
		}
		final String name = field.getName();
		writer.print(name);
		writer.print(": ");
		if (data == null && commentPresent)
		{
			writer.println();
			writer.println();
			return true;
		}
		return false;
	}
	
	private boolean writeComment(final Field field, final int depth)
	{
		final boolean commentPresent = field.isAnnotationPresent(Comment.class);
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
				writeIndention(depth);
				writer.print("# ");
				writer.print(trimmed);
				writer.println();
			}
		}
		return commentPresent;
	}
	
	private void writeCollection(final Collection<Object> data, final int depth) throws IllegalAccessException
	{
		writer.println();
		if (data.isEmpty())
		{
			writer.println();
		}
		for (Object entry : data)
		{
			if (entry != null)
			{
				writeIndention(depth);
				writer.print("- ");
				if (entry instanceof StorageObject)
				{
					writer.println();
					writeToFile(entry, depth + 1, entry.getClass());
				}
				else if (entry instanceof Location)
				{
					writeLocation((Location)entry, depth + 1);
				}
				else
				{
					writeScalar(entry);
				}
			}
		}
		writer.println();
	}
	
	private void writeMap(final Map<Object, Object> data, final int depth) throws IllegalArgumentException, IllegalAccessException
	{
		writer.println();
		if (data.isEmpty())
		{
			writer.println();
		}
		for (Entry<Object, Object> entry : data.entrySet())
		{
			final Object value = entry.getValue();
			if (value != null)
			{
				writeIndention(depth);
				writeKey(entry.getKey());
				writer.print(": ");
				if (value instanceof StorageObject)
				{
					writer.println();
					writeToFile(value, depth + 1, value.getClass());
				}
				else if (value instanceof Collection)
				{
					writeCollection((Collection<Object>)value, depth + 1);
				}
				else if (value instanceof Location)
				{
					writeLocation((Location)value, depth + 1);
				}
				else
				{
					writeScalar(value);
					writer.println();
				}
			}
		}
	}
	
	private void writeIndention(final int depth)
	{
		for (int i = 0; i < depth; i++)
		{
			writer.print("  ");
		}
	}
	
	private void writeScalar(final Object data)
	{
		if (data instanceof String || data instanceof Boolean || data instanceof Number)
		{
			synchronized (YAML)
			{
				YAML.dumpAll(Collections.singletonList(data).iterator(), writer);
			}
		}
		else if (data instanceof Material)
		{
			writer.println(data.toString().toLowerCase(Locale.ENGLISH));
		}
		else if (data instanceof MaterialData)
		{
			final MaterialData matData = (MaterialData)data;
			writer.println(matData.getItemType().toString().toLowerCase(Locale.ENGLISH)
						   + (matData.getData() > 0 ? ":" + matData.getData() : ""));
		}
		else if (data instanceof ItemStack)
		{
			final ItemStack itemStack = (ItemStack)data;
			writer.print(itemStack.getType().toString().toLowerCase(Locale.ENGLISH));
			
			if (itemStack.getDurability() > 0)
			{
				writer.print(':');
				writer.print(itemStack.getDurability());
			}
			writer.print(' ');
			writer.print(itemStack.getAmount());
			for (Entry<Enchantment, Integer> entry : itemStack.getEnchantments().entrySet())
			{
				writer.print(' ');
				writer.print(entry.getKey().getName().toLowerCase(Locale.ENGLISH));
				writer.print(':');
				writer.print(entry.getValue());
			}
		}
		else
		{
			throw new UnsupportedOperationException();
		}
	}
	
	private void writeKey(final Object data)
	{
		if (data instanceof String || data instanceof Boolean || data instanceof Number)
		{
			String output = data.toString();
			if (NON_WORD_PATTERN.matcher(output).find())
			{
				writer.print('"');
				writer.print(output.replace("\"", "\\\""));
				writer.print('"');
			}
			else
			{
				writer.print(output);
			}
		}
		else if (data instanceof Material)
		{
			writer.print(data.toString().toLowerCase(Locale.ENGLISH));
		}
		else if (data instanceof MaterialData)
		{
			final MaterialData matData = (MaterialData)data;
			writer.print(matData.getItemType().toString().toLowerCase(Locale.ENGLISH)
						 + (matData.getData() > 0 ? ":" + matData.getData() : ""));
		}
		else
		{
			throw new UnsupportedOperationException();
		}
	}
	
	private void writeLocation(final Location entry, final int depth)
	{
		writer.println();
		writeIndention(depth);
		writer.print("world: ");
		writeScalar(entry.getWorld().getName());
		writeIndention(depth);
		writer.print("x: ");
		writeScalar(entry.getX());
		writeIndention(depth);
		writer.print("y: ");
		writeScalar(entry.getY());
		writeIndention(depth);
		writer.print("z: ");
		writeScalar(entry.getZ());
		writeIndention(depth);
		writer.print("yaw: ");
		writeScalar(entry.getYaw());
		writeIndention(depth);
		writer.print("pitch: ");
		writeScalar(entry.getPitch());
	}
}
