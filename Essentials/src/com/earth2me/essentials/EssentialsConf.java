package com.earth2me.essentials;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
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
			ostr.close();
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
				ostr.close();
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
}
