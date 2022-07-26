package com.earth2me.essentials.textreader;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.I18n;

public class SimpleTextPager {
    private final transient IResolvable resolvable;

    public SimpleTextPager(final IResolvable resolvable) {
        this.resolvable = resolvable;
    }

    public void showPage(final CommandSource sender) {
        if (resolvable instanceof ITranslatableText) {
            final ITranslatableText text = (ITranslatableText) resolvable;
            for (final ITranslatableText.TranslatableText line : text.getLines()) {
                sender.sendTl(line.getKey(), line.getArgs());
            }
        } else if (resolvable instanceof IText) {
            for (String line : ((IText) resolvable).getLines()) {
                sender.sendMessage(line);
            }
        }
    }

    public void showPage(final CommandSource sender, final String pageStr, final String commandName) {
        int page = 1;
        if (pageStr != null) {
            try {
                page = Integer.parseInt(pageStr);
            } catch (final NumberFormatException ignored) {
            }
            if (page < 1) {
                page = 1;
            }
        }

        final int start = (page - 1) * 9;
        final int end = resolvable.getLineCount();

        final int pages = end / 9 + (end % 9 > 0 ? 1 : 0);
        if (page > pages) {
            sender.sendTl("infoUnknownChapter");
            return;
        }
        if (commandName != null) {
            final StringBuilder content = new StringBuilder();
            final String[] title = commandName.split(" ", 2);
            if (title.length > 1) {
                content.append(I18n.capitalCase(title[0])).append(": ");
                content.append(title[1]);
            } else {
                content.append(I18n.capitalCase(commandName));
            }
            sender.sendTl("infoPages", page, pages, content);
        }
        for (int i = start; i < end && i < start + 9; i++) {
            if (resolvable instanceof ITranslatableText) {
                final ITranslatableText.TranslatableText text = ((ITranslatableText) resolvable).getLines().get(i);
                sender.sendTl(text.getKey(), text.getArgs());
            } else if (resolvable instanceof IText) {
                sender.sendMessage(((IText) resolvable).getLines().get(i));
            }
        }
        if (page < pages && commandName != null) {
            sender.sendTl("readNextPage", commandName, page + 1);
        }
    }
}
