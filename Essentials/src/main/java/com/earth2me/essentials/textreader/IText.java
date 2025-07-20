package com.earth2me.essentials.textreader;

import java.util.List;
import java.util.Map;

public interface IText extends IResolvable {
    // Contains the raw text lines
    List<String> getLines();

    // Chapters contain the names that are displayed automatically if the file doesn't contain a introduction chapter.
    List<String> getChapters();

    // Bookmarks contains the string mappings from 'chapters' to line numbers.
    Map<String, Integer> getBookmarks();

    default int getLineCount() {
        return getLines().size();
    }
}
