package com.earth2me.essentials.textreader;

import java.util.List;
import java.util.Map;


public interface IText
{
	List<String> getLines();

	List<String> getChapters();

	Map<String, Integer> getBookmarks();
}
