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
		if (this.root == null)
		{
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
				logger.log(Level.INFO, Util.format("creatingConfigFromTemplate ", configFile.toString()));
				createFromTemplate();
			}
			else
			{
				try
				{
					logger.log(Level.INFO, Util.format("creatingEmptyConfig", configFile.toString()));
					configFile.createNewFile();
				}
				catch (IOException ex)
				{
					logger.log(Level.SEVERE, Util.format("failedToCreateConfig", configFile.toString()), ex);
				}
			}
		}
		super.load();
		if (this.root == null)
		{
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
				logger.log(Level.SEVERE, Util.format("couldNotFindTemplate", templateName));
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
			logger.log(Level.SEVERE, Util.format("failedToWriteConfig", configFile.toString()), ex);
			return;
		}
		finally
		{
			try
			{
				if (ostr != null)
				{
					ostr.close();
				}
			}
			catch (IOException ex)
			{
				logger.log(Level.SEVERE, Util.format("failedToCloseConfig", configFile.toString()), ex);
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

	public void setTemplateName(String templateName, Class<?> resClass)
	{
		this.templateName = templateName;
		this.resourceClass = resClass;
	}

	public boolean hasProperty(String path)
	{
		return getProperty(path) != null;
	}

	public Location getLocation(String path, Server server)
	{
		String worldName = getString((path != null ? path + "." : "") + "world");
		if (worldName == null || worldName.isEmpty())
		{
			return null;
		}
		World world = server.getWorld(worldName);
		if (world == null)
		{
			return null;
		}
		return new Location(world,
							getDouble((path != null ? path + "." : "") + "x", 0),
							getDouble((path != null ? path + "." : "") + "y", 0),
							getDouble((path != null ? path + "." : "") + "z", 0),
							(float)getDouble((path != null ? path + "." : "") + "yaw", 0),
							(float)getDouble((path != null ? path + "." : "") + "pitch", 0));
	}

	public void setProperty(String path, Location loc)
	{
		setProperty((path != null ? path + "." : "") + "world", loc.getWorld().getName());
		setProperty((path != null ? path + "." : "") + "x", loc.getX());
		setProperty((path != null ? path + "." : "") + "y", loc.getY());
		setProperty((path != null ? path + "." : "") + "z", loc.getZ());
		setProperty((path != null ? path + "." : "") + "yaw", loc.getYaw());
		setProperty((path != null ? path + "." : "") + "pitch", loc.getPitch());
	}

	public ItemStack getItemStack(String path)
	{
		return new ItemStack(
				Material.valueOf(getString(path + ".type", "AIR")),
				getInt(path + ".amount", 1),
				(short)getInt(path + ".damage", 0)/*,
				(byte)getInt(path + ".data", 0)*/);
	}

	public void setProperty(String path, ItemStack stack)
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("type", stack.getType().toString());
		map.put("amount", stack.getAmount());
		map.put("damage", stack.getDurability());
		// getData().getData() is broken
		//map.put("data", stack.getDurability());
		setProperty(path, map);
	}

	public long getLong(String path, long def)
	{
		Number num = (Number)getProperty(path);
		if (num == null)
		{
			return def;
		}
		return num.longValue();
	}

	@Override
	public double getDouble(String path, double def)
	{
		Number num = (Number)getProperty(path);
		if (num == null)
		{
			return def;
		}
		return num.doubleValue();
	}
}
