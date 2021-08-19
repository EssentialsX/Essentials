package com.earth2me.essentials.textreader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

    public void addLine(String line) {
        lines.add(line);
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
