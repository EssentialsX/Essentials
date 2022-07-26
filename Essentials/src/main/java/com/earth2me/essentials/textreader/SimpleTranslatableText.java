package com.earth2me.essentials.textreader;

import java.util.ArrayList;
import java.util.List;

public class SimpleTranslatableText implements ITranslatableText {
    private final List<TranslatableText> lines = new ArrayList<>();

    public void addLine(final String tlKey, final Object... args) {
        this.lines.add(new TranslatableText(tlKey, args));
    }

    @Override
    public List<TranslatableText> getLines() {
        return lines;
    }
}
