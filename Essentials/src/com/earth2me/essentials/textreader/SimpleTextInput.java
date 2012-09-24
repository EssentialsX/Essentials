package com.earth2me.essentials.textreader;

import java.util.*;


public class SimpleTextInput implements IText
{
	private final transient List<String> lines = new ArrayList<String>();
	
	public SimpleTextInput (final String input) {
		lines.addAll(Arrays.asList(input.split("\\n")));
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
