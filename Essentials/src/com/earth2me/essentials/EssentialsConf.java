package com.earth2me.essentials;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.config.Configuration;


public class EssentialsConf extends Configuration
{
	private static final Logger logger = Logger.getLogger("Minecraft");
	private File configFile;
	private String templateName = null;
	private Class<?> resourceClass = EssentialsConf.class;

	public EssentialsConf(File configFile)
	{
		super(configFile);
		this.configFile = configFile;
		if (this.root == null) {
			this.root = new HashMap<String, Object>();
		}
	}

	@Override
	public void load()
	{
		configFile = configFile.getAbsoluteFile();
		if (!configFile.getParentFile().exists())
		{
			configFile.getParentFile().mkdirs();
		}
		if (!configFile.exists())
		{
			if (templateName != null)
			{
				logger.log(Level.INFO, "Creating config from template: " + configFile.toString());
				createFromTemplate();
			}
			else
			{
				try
				{
					logger.log(Level.INFO, "Creating empty config: " + configFile.toString());
					configFile.createNewFile();
				}
				catch (IOException ex)
				{
					logger.log(Level.SEVERE, "Failed to create config " + configFile.toString(), ex);
				}
			}
		}
		super.load();
		if (this.root == null) {
			this.root = new HashMap<String, Object>();
		}
	}

	private void createFromTemplate()
	{
		OutputStream ostr = null;
		try
		{
			InputStream istr = resourceClass.getResourceAsStream(templateName);
			if (istr == null)
			{
				logger.log(Level.SEVERE, "Could not find template " + templateName);
				return;
			}
			ostr = new FileOutputStream(configFile);
			byte[] buffer = new byte[1024];
			int length = 0;
			length = istr.read(buffer);
			while (length > 0)
			{
				ostr.write(buffer, 0, length);
				length = istr.read(buffer);
			}
			istr.close();
		}
		catch (IOException ex)
		{
			logger.log(Level.SEVERE, "Failed to write config " + configFile.toString(), ex);
			return;
		}
		finally
		{
			try
			{
				if (ostr != null) {
					ostr.close();
				}
			}
			catch (IOException ex)
			{
				logger.log(Level.SEVERE, "Failed to close config " + configFile.toString(), ex);
				return;
			}
		}
	}

	public void setTemplateName(String templateName)
	{
		this.templateName = templateName;
	}

	public File getFile()
	{
		return configFile;
	}

	public void setTemplateName(String templateName, Class<?> resClass) {
		this.templateName = templateName;
		this.resourceClass = resClass;
	}
	
	public boolean hasProperty(String path) {
		return getProperty(path) != null;
	}
	
	public Location getLocation(String path, Server server) {
		String worldName = getString(path+".world");
		if (worldName == null || worldName.isEmpty()) {
			return null;
		}
		World world = server.getWorld(worldName);
		if (world == null) {
			return null;
		}
		return new Location(world, 
			getDouble(path+".x", 0),
			getDouble(path+".y", 0),
			getDouble(path+".z", 0),
			(float)getDouble(path+".paw", 0),
			(float)getDouble(path+".pitch", 0));
	}
	
	public void setProperty(String path, Location loc) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("world", loc.getWorld().getName());
		map.put("x", loc.getX());
		map.put("y", loc.getY());
		map.put("z", loc.getZ());
		map.put("yaw", loc.getYaw());
		map.put("pitch", loc.getPitch());
		setProperty(path, map);
	}
	
	public ItemStack getItemStack(String path) {
		return new ItemStack(
			Material.valueOf(getString(path+".type", "AIR")),
			getInt(path+".amount", 1),
			(short)getInt(path+".damage", 0),
			(byte)getInt(path+".data", 0));
	}
	
	public void setProperty(String path, ItemStack stack) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("type", stack.getType().toString());
		map.put("amount", stack.getAmount());
		map.put("damage", stack.getDurability());
		map.put("data", stack.getData().getData());
		setProperty(path, map);
	}
	
	public long getLong(String path, long def) {
		Number num = (Number)getProperty(path);
		if (num == null) {
			return def;
		}
		return num.longValue();
	}
}
