package com.earth2me.essentials.textreader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class SimpleTextInput implements IText
{
	private final transient List<String> lines = new ArrayList<String>();
	
	public SimpleTextInput (final String input) {
		for (String line : input.split("\\n")) {
			lines.add(line);
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
		return Collections.emptyList();
	}

	@Override
	public Map<String, Integer> getBookmarks()
	{
		return Collections.emptyMap();
	}
	
}
