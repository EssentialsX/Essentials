package com.earth2me.essentials.textreader;

import com.earth2me.essentials.api.IEssentials;
import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.Util;
import com.earth2me.essentials.api.InvalidNameException;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import sun.util.BuddhistCalendar;


public class TextInput implements IText
{
	private final transient List<String> lines = new ArrayList<String>();
	private final transient List<String> chapters = new ArrayList<String>();
	private final transient Map<String, Integer> bookmarks = new HashMap<String, Integer>();

	public TextInput(final CommandSender sender, final String filename, final boolean createFile, final IEssentials ess) throws IOException
	{

		File file = null;
		if (sender instanceof Player)
		{
			try
			{
				final IUser user = ess.getUser((Player)sender);
				file = new File(ess.getDataFolder(), filename + "_" + Util.sanitizeFileName(user.getName()) + ".txt");
				if (!file.exists())
				{
					file = new File(ess.getDataFolder(), filename + "_" + Util.sanitizeFileName(user.getGroup()) + ".txt");
				}
			}
			catch (InvalidNameException ex)
			{
				Bukkit.getLogger().log(Level.WARNING, ex.getMessage(), ex);
			}
		}
		if (file == null || !file.exists())
		{
			file = new File(ess.getDataFolder(), filename + ".txt");
		}
		if (file.exists())
		{
			final BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			try
			{
				int lineNumber = 0;
				while (bufferedReader.ready())
				{
					final String line = bufferedReader.readLine();
					if (line == null)
					{
						break;
					}
					if (line.length() > 0 && line.charAt(0) == '#')
					{
						bookmarks.put(line.substring(1).toLowerCase(Locale.ENGLISH).replaceAll("&[0-9a-f]", ""), lineNumber);
						chapters.add(line.substring(1).replace('&', '§').replace("§§", "&"));
					}
					lines.add(line.replace('&', '§').replace("§§", "&"));
					lineNumber++;
				}
			}
			finally
			{
				bufferedReader.close();
			}
		}
		else
		{
			if (createFile)
			{
				final InputStream input = ess.getResource(filename + ".txt");
				final OutputStream output = new FileOutputStream(file);
				try
				{
					final byte[] buffer = new byte[1024];
					int length = 0;
					length = input.read(buffer);
					while (length > 0)
					{
						output.write(buffer, 0, length);
						length = input.read(buffer);
					}
				}
				finally
				{
					output.close();
					input.close();
				}
				throw new FileNotFoundException("File " + filename + ".txt does not exist. Creating one for you.");
			}
		}
	}

	@Override
	public List<String> getLines()
	{
		return lines;
	}

	@Override
	public List<String> getChapters()
	{
		return chapters;
	}

	@Override
	public Map<String, Integer> getBookmarks()
	{
		return bookmarks;
	}
}
