package com.earth2me.essentials.textreader;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.FormatUtil;
import com.earth2me.essentials.utils.StringUtil;
import java.io.*;
import java.lang.ref.SoftReference;
import java.util.*;
import net.ess3.api.IEssentials;


public class TextInput implements IText
{
	private static final HashMap<String, SoftReference<TextInput>> cache = new HashMap<String, SoftReference<TextInput>>();
	private final transient List<String> lines;
	private final transient List<String> chapters;
	private final transient Map<String, Integer> bookmarks;
	private final transient long lastChange;

	public TextInput(final CommandSource sender, final String filename, final boolean createFile, final IEssentials ess) throws IOException
	{

		File file = null;
		if (sender.isPlayer())
		{
			final User user = ess.getUser(sender.getPlayer());
			file = new File(ess.getDataFolder(), filename + "_" + StringUtil.sanitizeFileName(user.getName()) + ".txt");
			if (!file.exists())
			{
				file = new File(ess.getDataFolder(), filename + "_" + StringUtil.sanitizeFileName(user.getGroup()) + ".txt");
			}
		}
		if (file == null || !file.exists())
		{
			file = new File(ess.getDataFolder(), filename + ".txt");
		}
		if (file.exists())
		{
			lastChange = file.lastModified();
			boolean readFromfile;
			synchronized (cache)
			{
				final SoftReference<TextInput> inputRef = cache.get(file.getName());
				TextInput input;
				if (inputRef == null || (input = inputRef.get()) == null || input.lastChange < lastChange)
				{
					lines = new ArrayList<String>();
					chapters = new ArrayList<String>();
					bookmarks = new HashMap<String, Integer>();
					cache.put(file.getName(), new SoftReference<TextInput>(this));
					readFromfile = true;
				}
				else
				{
					lines = Collections.unmodifiableList(input.getLines());
					chapters = Collections.unmodifiableList(input.getChapters());
					bookmarks = Collections.unmodifiableMap(input.getBookmarks());
					readFromfile = false;
				}
			}
			if (readFromfile)
			{
				final Reader reader = new InputStreamReader(new FileInputStream(file), "utf-8");
				final BufferedReader bufferedReader = new BufferedReader(reader);
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
						if (line.length() > 1 && line.charAt(0) == '#')
						{
							String[] titles = line.substring(1).trim().replace(" ", "_").split(",");
							chapters.add(FormatUtil.replaceFormat(titles[0]));
							for (String title : titles)
							{
								bookmarks.put(FormatUtil.stripEssentialsFormat(title.toLowerCase(Locale.ENGLISH)), lineNumber);
							}
						}
						lines.add(FormatUtil.replaceFormat(line));
						lineNumber++;
					}
				}
				finally
				{
					reader.close();
					bufferedReader.close();
				}
			}
		}
		else
		{
			lastChange = 0;
			lines = Collections.emptyList();
			chapters = Collections.emptyList();
			bookmarks = Collections.emptyMap();
			if (createFile)
			{
				final InputStream input = ess.getResource(filename + ".txt");
				final OutputStream output = new FileOutputStream(file);
				try
				{
					final byte[] buffer = new byte[1024];
					int length = input.read(buffer);
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
