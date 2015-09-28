package com.earth2me.essentials.textreader;

import java.util.*;


public class SimpleTextInput implements IText {
    private final transient List<String> lines = new ArrayList<>();

    public SimpleTextInput(final String input) {
        lines.addAll(Arrays.asList(input.split("\\n")));
    }

    public SimpleTextInput(final List<String> input) {
        lines.addAll(input);
    }

    public SimpleTextInput() {
    }

    @Override
    public List<String> getLines() {
        return lines;
    }

    @Override
    public List<String> getChapters() {
        return Collections.emptyList();
    }

    @Override
    public Map<String, Integer> getBookmarks() {
        return Collections.emptyMap();
    }
}
