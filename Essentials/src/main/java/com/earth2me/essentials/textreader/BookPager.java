package com.earth2me.essentials.textreader;

import net.ess3.api.TranslatableException;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BookPager {
    final double pageMax = 254;
    final double charMax = 18.5;
    final int lineMax = 12;
    private final transient IText text;

    public BookPager(final IText text) {
        this.text = text;
    }

    public List<String> getPages(final String pageStr) throws Exception {
        final List<String> lines = text.getLines();
        final List<String> pageLines = new ArrayList<>();
        final Map<String, Integer> bookmarks = text.getBookmarks();

        //This checks to see if we have the chapter in the index
        if (!bookmarks.containsKey(pageStr.toLowerCase(Locale.ENGLISH))) {
            throw new TranslatableException("infoUnknownChapter");
        }

        //Since we have a valid chapter, count the number of lines in the chapter
        final int chapterstart = bookmarks.get(pageStr.toLowerCase(Locale.ENGLISH)) + 1;
        int chapterend;
        for (chapterend = chapterstart; chapterend < lines.size(); chapterend++) {
            final String line = lines.get(chapterend);
            if (line.length() > 0 && line.charAt(0) == '#') {
                break;
            }
        }

        final List<String> pages = new ArrayList<>();
        double pageLength = 0;

        for (int lineNo = chapterstart; lineNo < chapterend; lineNo += 1) {
            final String pageLine = lines.get(lineNo);
            String tempLine;

            final int lineLength = pageLine.length();
            double length = 0;
            int pointer = 0;
            int start = 0;
            double weight = 1;
            boolean forcePageEnd = false;

            while (pointer < lineLength) {
                final char letter = pageLine.charAt(pointer);

                if (pageLine.charAt(start) == ' ') {
                    start++;
                    pointer++;
                    continue;
                }

                if (pageLength >= pageMax) {
                    length = charMax;
                    pageLength = 0;
                    forcePageEnd = true;
                }

                if (length >= charMax || (letter == ChatColor.COLOR_CHAR && length + 1 >= charMax)) {
                    int pos = pointer;
                    int rollback = 0;
                    while (pos > start && pageLine.charAt(pos) != ' ' && pageLine.charAt(pos) != "\n".charAt(0)) {
                        rollback++;
                        pos--;
                    }
                    if (pos != start) {
                        pointer = pos;
                        pageLength -= rollback;
                    }

                    tempLine = pageLine.substring(start, pointer);
                    pageLines.add(tempLine);
                    if (buildPage(pages, pageLines, forcePageEnd)) {
                        pageLength = 0;
                    }
                    forcePageEnd = false;

                    start = pointer;
                    length = 0;
                    pageLength += 1;
                }

                pageLength++;

                if (letter == ChatColor.COLOR_CHAR && pointer + 1 < lineLength) {
                    final char nextLetter = pageLine.charAt(pointer + 1);
                    if (nextLetter == 'l' || nextLetter == 'L') {
                        weight = 1.25;
                    } else {
                        weight = 1;
                    }
                    pointer++;
                } else if (letter == 'i' || letter == '.' || letter == ',' || letter == '!' || letter == ':' || letter == ';' || letter == '|') {
                    length += 0.34 * weight;
                } else if (letter == 'l' || letter == '\'' || letter == '`') {
                    length += 0.53 * weight;
                } else if (letter == ' ' || letter == 't' || letter == 'I' || letter == '[' || letter == ']') {
                    length += 0.69 * weight;
                } else if (letter == 'f' || letter == 'k' || letter == '"' || letter == '*' || letter == '(' || letter == ')' || letter == '{' || letter == '}' || letter == '<' || letter == '>') {
                    length += 0.85 * weight;
                } else if (letter == '@' || letter == '~') {
                    length += 1.2 * weight;
                } else {
                    length += weight;
                }
                pointer++;
            }

            if (length > 0) {
                tempLine = pageLine.substring(start, lineLength);
                pageLines.add(tempLine);
                if (buildPage(pages, pageLines, false)) {
                    pageLength = 0;
                }
            }
        }

        buildPage(pages, pageLines, true);
        return pages;
    }

    boolean buildPage(final List<String> pages, final List<String> lines, final boolean override) {
        if (override || lines.size() > lineMax) {
            final StringBuilder newPage = new StringBuilder();
            for (final String aline : lines) {
                newPage.append(aline).append("\n");
            }
            pages.add(newPage.toString());
            lines.clear();
            return true;
        }
        return false;
    }
}
