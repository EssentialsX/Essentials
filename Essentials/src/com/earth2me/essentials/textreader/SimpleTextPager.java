package com.earth2me.essentials.textreader;

import org.bukkit.command.CommandSender;


public class SimpleTextPager
{
	private final transient IText text;

	public SimpleTextPager(final IText text)
	{
		this.text = text;
	}

	public void showPage(final CommandSender sender)
	{
		for (String line : text.getLines())
		{
			sender.sendMessage(line);
		}
	}

	public String getString(int line)
	{
		if (text.getLines().size() < line)
		{
			return null;
		}
		return text.getLines().get(line);
	}
}
