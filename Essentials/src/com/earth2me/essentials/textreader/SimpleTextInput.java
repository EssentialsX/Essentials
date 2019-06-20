package com.earth2me.essentials.textreader;

import java.util.*;


/**
 * <p>SimpleTextInput class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class SimpleTextInput implements IText {
    private final transient List<String> lines = new ArrayList<String>();

    /**
     * <p>Constructor for SimpleTextInput.</p>
     *
     * @param input a {@link java.lang.String} object.
     */
    public SimpleTextInput(final String input) {
        lines.addAll(Arrays.asList(input.split("\\n")));
    }

    /**
     * <p>Constructor for SimpleTextInput.</p>
     *
     * @param input a {@link java.util.List} object.
     */
    public SimpleTextInput(final List<String> input) {
        lines.addAll(input);
    }

    /**
     * <p>Constructor for SimpleTextInput.</p>
     */
    public SimpleTextInput() {
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getLines() {
        return lines;
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getChapters() {
        return Collections.emptyList();
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Integer> getBookmarks() {
        return Collections.emptyMap();
    }
}
