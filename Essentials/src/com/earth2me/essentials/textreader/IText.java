package com.earth2me.essentials.textreader;

import java.util.List;
import java.util.Map;


/**
 * <p>IText interface.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public interface IText {
    // Contains the raw text lines
    /**
     * <p>getLines.</p>
     *
     * @return a {@link java.util.List} object.
     */
    List<String> getLines();

    // Chapters contain the names that are displayed automatically if the file doesn't contain a introduction chapter.
    /**
     * <p>getChapters.</p>
     *
     * @return a {@link java.util.List} object.
     */
    List<String> getChapters();

    // Bookmarks contains the string mappings from 'chapters' to line numbers.
    /**
     * <p>getBookmarks.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    Map<String, Integer> getBookmarks();
}
