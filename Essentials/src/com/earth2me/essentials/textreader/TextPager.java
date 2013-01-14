package com.earth2me.essentials.textreader;

import com.earth2me.essentials.I18n;
import static com.earth2me.essentials.I18n._;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.bukkit.command.CommandSender;


public class TextPager
{
	private final transient IText text;
	private final transient boolean onePage;

	public TextPager(final IText text)
	{
		this(text, false);
	}

	public TextPager(final IText text, final boolean onePage)
	{
		this.text = text;
		this.onePage = onePage;
	}

	public void showPage(final String pageStr, final String chapterPageStr, final String commandName, final CommandSender sender)
	{
		List<String> lines = text.getLines();
		List<String> chapters = text.getChapters();
		Map<String, Integer> bookmarks = text.getBookmarks();

		//This code deals with the initial chapter.  We use this to display the initial output or contents.
		//We also use this code to display some extra information if we don't intend to use chapters
		if (pageStr == null || pageStr.isEmpty() || pageStr.matches("[0-9]+"))
		{
			//If an info file starts with a chapter title, list the chapters
			//If not display the text up until the first chapter.
			if (lines.get(0).startsWith("#"))
			{
				if (onePage)
				{
					return;
				}
				sender.sendMessage(_("infoChapter"));
				final StringBuilder sb = new StringBuilder();
				boolean first = true;
				for (String string : chapters)
				{
					if (!first)
					{
						sb.append(", ");
					}
					first = false;
					sb.append(string);
				}
				sender.sendMessage(sb.toString());
				return;
			}
			else
			{
				int page = 1;
				try
				{
					page = Integer.parseInt(pageStr);
				}
				catch (Exception ex)
				{
					page = 1;
				}
				if (page < 1)
				{
					page = 1;
				}

				int start = onePage ? 0 : (page - 1) * 9;
				int end;
				for (end = 0; end < lines.size(); end++)
				{
					String line = lines.get(end);
					if (line.startsWith("#"))
					{
						break;
					}
				}

				int pages = end / 9 + (end % 9 > 0 ? 1 : 0);
				if (!onePage && commandName != null)
				{

					StringBuilder content = new StringBuilder();
					final String[] title = commandName.split(" ", 2);
					if (title.length > 1)
					{
						content.append(I18n.capitalCase(title[0])).append(": ");
						content.append(title[1]);
					}
					else if (chapterPageStr != null)
					{
						content.append(I18n.capitalCase(commandName)).append(": ");
						content.append(chapterPageStr);
					}
					else
					{
						content.append(I18n.capitalCase(commandName));
					}
					sender.sendMessage(_("infoPages", page, pages, content));
				}
				for (int i = start; i < end && i < start + (onePage ? 20 : 9); i++)
				{
					sender.sendMessage("§r" + lines.get(i));
				}
				if (!onePage && page < pages && commandName != null)
				{
					sender.sendMessage(_("readNextPage", commandName, page + 1));
				}
				return;
			}
		}

		//If we have a chapter, check to see if we have a page number
		int chapterpage = 0;
		if (chapterPageStr != null)
		{
			try
			{
				chapterpage = Integer.parseInt(chapterPageStr) - 1;
			}
			catch (Exception ex)
			{
				chapterpage = 0;
			}
			if (chapterpage < 0)
			{
				chapterpage = 0;
			}
		}

		//This checks to see if we have the chapter in the index
		if (!bookmarks.containsKey(pageStr.toLowerCase(Locale.ENGLISH)))
		{
			sender.sendMessage(_("infoUnknownChapter"));
			return;
		}
		
		//Since we have a valid chapter, count the number of lines in the chapter
		final int chapterstart = bookmarks.get(pageStr.toLowerCase(Locale.ENGLISH)) + 1;
		int chapterend;
		for (chapterend = chapterstart; chapterend < lines.size(); chapterend++)
		{
			final String line = lines.get(chapterend);
			if (line.length() > 0 && line.charAt(0) == '#')
			{
				break;
			}
		}
		
		//Display the chapter from the starting position
		final int start = chapterstart + (onePage ? 0 : chapterpage * 9);
		final int page = chapterpage + 1;
		final int pages = (chapterend - chapterstart) / 9 + ((chapterend - chapterstart) % 9 > 0 ? 1 : 0);
		if (!onePage && commandName != null)
		{
			sender.sendMessage(_("infoChapterPages", pageStr, page, pages));
		}
		for (int i = start; i < chapterend && i < start + (onePage ? 20 : 9); i++)
		{
			sender.sendMessage("§r" + lines.get(i));
		}
		if (!onePage && page < pages && commandName != null)
		{
			sender.sendMessage(_("readNextPage", commandName, pageStr + " " + (page + 1)));
		}
	}
}
