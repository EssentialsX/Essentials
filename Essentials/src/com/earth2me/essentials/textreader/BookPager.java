package com.earth2me.essentials.textreader;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Util;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;


public class BookPager
{
	private final transient IText text;

	public BookPager(final IText text)
	{
		this.text = text;
	}

	public List<String> getPages(final String pageStr) throws Exception
	{
		List<String> lines = text.getLines();
		List<String> chapters = text.getChapters();
		Map<String, Integer> bookmarks = text.getBookmarks();

		int chapterpage = 0;

		//This checks to see if we have the chapter in the index
		if (!bookmarks.containsKey(pageStr.toLowerCase(Locale.ENGLISH)))
		{
			throw new Exception("No such /einfo chapter!");
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

		List<String> pageLines = new ArrayList<String>();

		for (int lineNo = chapterstart; lineNo < chapterend; lineNo += 1)
		{
			String pageLine = "\u00a70" + lines.get(lineNo);
			String tempLine;
			final double max = 18;
			final int lineLength = pageLine.length();
			double length = 0;
			int pointer = 0;
			int start = 0;
			double weight = 1;

			while (pointer < lineLength)
			{
				if (length >= max)
				{
					tempLine = pageLine.substring(start, pointer);
					pageLines.add(tempLine);
					start = pointer;
					length = 0;
				}

				Character letter = pageLine.charAt(pointer);

				if (letter == '\u00a7')
				{
					Character nextLetter = pageLine.charAt(pointer + 1);
					if (nextLetter == 'l')
					{
						weight = 1.25;
					}
					else
					{
						weight = 1;
					}
					pointer++;
				}
				else if (letter == ' ')
				{
					length += (0.7 * weight);
				}
				else
				{
					length += weight;
				}
				pointer++;
			}
			if (length > 0)
			{
				tempLine = pageLine.substring(start, lineLength);
				pageLines.add(tempLine);
			}
		}

		List<String> pages = new ArrayList<String>();

		for (int count = 0; count < pageLines.size(); count += 12)
		{

			StringBuilder newPage = new StringBuilder();

			for (int i = count; i < count + 12 && i < pageLines.size(); i++)
			{
				newPage.append("\n").append(pageLines.get(i));
				//Logger.getLogger("Minecraft").info("adding line " + pageLines.get(i) + " to book");
			}

			//Logger.getLogger("Minecraft").info("adding page to book");
			pages.add(newPage.toString());
		}

		return pages;

	}
}
