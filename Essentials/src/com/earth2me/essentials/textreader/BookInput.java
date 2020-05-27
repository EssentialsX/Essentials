package com.earth2me.essentials.textreader;

import net.ess3.api.IEssentials;

import java.io.*;
import java.lang.ref.SoftReference;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class BookInput implements IText {
    private final static HashMap<String, SoftReference<BookInput>> cache = new HashMap<>();
    private final transient List<String> lines;
    private final transient List<String> chapters;
    private final transient Map<String, Integer> bookmarks;
    private final transient long lastChange;

    public BookInput(final String filename, final boolean createFile, final IEssentials ess) throws IOException {

        File file = null;
        if (file == null || !file.exists()) {
            file = new File(ess.getDataFolder(), filename + ".txt");
        }
        if (!file.exists()) {
            if (createFile) {
                try (InputStream input = ess.getResource(filename + ".txt"); OutputStream output = new FileOutputStream(file)) {
                    final byte[] buffer = new byte[1024];
                    int length = input.read(buffer);
                    while (length > 0) {
                        output.write(buffer, 0, length);
                        length = input.read(buffer);
                    }
                }
                ess.getLogger().info("File " + filename + ".txt does not exist. Creating one for you.");
            }
        }
        if (!file.exists()) {
            lastChange = 0;
            lines = Collections.emptyList();
            chapters = Collections.emptyList();
            bookmarks = Collections.emptyMap();
            throw new FileNotFoundException("Could not create " + filename + ".txt");
        } else {
            lastChange = file.lastModified();
            boolean readFromfile;
            synchronized (cache) {
                final SoftReference<BookInput> inputRef = cache.get(file.getName());
                BookInput input;
                if (inputRef == null || (input = inputRef.get()) == null || input.lastChange < lastChange) {
                    lines = new ArrayList<>();
                    chapters = new ArrayList<>();
                    bookmarks = new HashMap<>();
                    cache.put(file.getName(), new SoftReference<>(this));
                    readFromfile = true;
                } else {
                    lines = Collections.unmodifiableList(input.getLines());
                    chapters = Collections.unmodifiableList(input.getChapters());
                    bookmarks = Collections.unmodifiableMap(input.getBookmarks());
                    readFromfile = false;
                }
            }
            if (readFromfile) {
                final Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
                final BufferedReader bufferedReader = new BufferedReader(reader);
                try {
                    int lineNumber = 0;
                    while (bufferedReader.ready()) {
                        final String line = bufferedReader.readLine();
                        if (line == null) {
                            break;
                        }
                        if (line.length() > 0 && line.charAt(0) == '#') {
                            bookmarks.put(line.substring(1).toLowerCase(Locale.ENGLISH).replaceAll("&[0-9a-fk]", ""), lineNumber);
                            chapters.add(line.substring(1).replace('&', '§').replace("§§", "&"));
                        }
                        lines.add(line.replace('&', '§').replace("§§", "&"));
                        lineNumber++;
                    }
                } finally {
                    reader.close();
                    bufferedReader.close();
                }
            }
        }
    }

    @Override
    public List<String> getLines() {
        return lines;
    }

    @Override
    public List<String> getChapters() {
        return chapters;
    }

    @Override
    public Map<String, Integer> getBookmarks() {
        return bookmarks;
    }
}
