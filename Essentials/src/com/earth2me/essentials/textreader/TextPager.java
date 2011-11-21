package com.earth2me.essentials.textreader;

import static com.earth2me.essentials.I18n._;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.bukkit.command.CommandSender;


public class TextPager
{
	private final transient IText text;
	private final transient boolean showHeader;

	public TextPager(final IText text)
	{
		this(text, true);
	}

	public TextPager(final IText text, final boolean showHeader)
	{
		this.text = text;
		this.showHeader = showHeader;
	}

	public void showPage(final String pageStr, final String chapterPageStr, final CommandSender sender)
	{
		List<String> lines = text.getLines();
		List<String> chapters = text.getChapters();
		Map<String, Integer> bookmarks = text.getBookmarks();

		if (bookmarks.isEmpty())
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

			int start = (page - 1) * 9;
			if (showHeader)
			{
				int pages = lines.size() / 9 + (lines.size() % 9 > 0 ? 1 : 0);
				sender.sendMessage(_("infoPages", page, pages));
			}
			for (int i = start; i < lines.size() && i < start + 9; i++)
			{
				sender.sendMessage(lines.get(i));
			}
			return;
		}

		if (pageStr == null || pageStr.isEmpty() || pageStr.matches("[0-9]+"))
		{
			if (lines.get(0).startsWith("#"))
			{
				if (!showHeader)
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

				int start = (page - 1) * 9;
				int end;
				for (end = 0; end < lines.size(); end++)
				{
					String line = lines.get(end);
					if (line.startsWith("#"))
					{
						break;
					}
				}

				if (showHeader)
				{
					int pages = end / 9 + (end % 9 > 0 ? 1 : 0);
					sender.sendMessage(_("infoPages", page, pages));
				}
				for (int i = start; i < end && i < start + 9; i++)
				{
					sender.sendMessage(lines.get(i));
				}
				return;
			}
		}

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
		}

		if (!bookmarks.containsKey(pageStr.toLowerCase(Locale.ENGLISH)))
		{
			sender.sendMessage(_("infoUnknownChapter"));
			return;
		}
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
		final int start = chapterstart + chapterpage * 9;

		if (showHeader)
		{
			final int page = chapterpage + 1;
			final int pages = (chapterend - chapterstart) / 9 + ((chapterend - chapterstart) % 9 > 0 ? 1 : 0);
			sender.sendMessage(_("infoChapterPages", pageStr, page, pages));
		}
		for (int i = start; i < chapterend && i < start + 9; i++)
		{
			sender.sendMessage(lines.get(i));
		}
	}
}
