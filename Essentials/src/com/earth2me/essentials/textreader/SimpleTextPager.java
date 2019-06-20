package com.earth2me.essentials.textreader;

import com.earth2me.essentials.CommandSource;

import java.util.List;

/**
 * <p>SimpleTextPager class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class SimpleTextPager {
    private final transient IText text;

    /**
     * <p>Constructor for SimpleTextPager.</p>
     *
     * @param text a {@link com.earth2me.essentials.textreader.IText} object.
     */
    public SimpleTextPager(final IText text) {
        this.text = text;
    }

    /**
     * <p>showPage.</p>
     *
     * @param sender a {@link com.earth2me.essentials.CommandSource} object.
     */
    public void showPage(final CommandSource sender) {
        for (String line : text.getLines()) {
            sender.sendMessage(line);
        }
    }

    /**
     * <p>getLines.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<String> getLines() {
        return text.getLines();
    }

    /**
     * <p>getLine.</p>
     *
     * @param line a int.
     * @return a {@link java.lang.String} object.
     */
    public String getLine(int line) {
        if (text.getLines().size() < line) {
            return null;
        }
        return text.getLines().get(line);
    }
}
