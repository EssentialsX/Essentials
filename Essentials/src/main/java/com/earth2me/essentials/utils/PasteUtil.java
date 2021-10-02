package com.earth2me.essentials.utils;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class PasteUtil {
    private static final String PASTE_URL = "https://paste.gg/";
    private static final String PASTE_UPLOAD_URL = "https://api.paste.gg/v1/pastes";
    private static final ExecutorService PASTE_EXECUTOR_SERVICE = Executors.newSingleThreadExecutor();
    private static final Gson GSON = new Gson();

    private PasteUtil() {
    }

    /**
     * Creates an anonymous paste containing the provided files.
     *
     * @param pages The files to include in the paste.
     * @return The result of the paste, including the paste URL and deletion key.
     */
    public static CompletableFuture<PasteResult> createPaste(List<PasteFile> pages) {
        final CompletableFuture<PasteResult> future = new CompletableFuture<>();
        PASTE_EXECUTOR_SERVICE.submit(() -> {
            try {
                final HttpURLConnection connection = (HttpURLConnection) new URL(PASTE_UPLOAD_URL).openConnection();
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestProperty("User-Agent", "EssentialsX plugin");
                connection.setRequestProperty("Content-Type", "application/json");
                final JsonObject body = new JsonObject();
                final JsonArray files = new JsonArray();
                for (final PasteFile page : pages) {
                    final JsonObject file = new JsonObject();
                    final JsonObject content = new JsonObject();
                    file.addProperty("name", page.getName());
                    content.addProperty("format", "text");
                    content.addProperty("value", page.getContents());
                    file.add("content", content);
                    files.add(file);
                }
                body.add("files", files);

                try (final OutputStream os = connection.getOutputStream()) {
                    os.write(body.toString().getBytes(Charsets.UTF_8));
                }

                if (connection.getResponseCode() >= 400) {
                    //noinspection UnstableApiUsage
                    future.completeExceptionally(new Error(CharStreams.toString(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))));
                    return;
                }

                // Read URL
                final JsonObject object = GSON.fromJson(new InputStreamReader(connection.getInputStream(), Charsets.UTF_8), JsonObject.class);
                final String pasteId = object.get("result").getAsJsonObject().get("id").getAsString();
                final String pasteUrl = PASTE_URL + pasteId;
                final JsonElement deletionKey = object.get("result").getAsJsonObject().get("deletion_key");
                connection.disconnect();

                final PasteResult result = new PasteResult(pasteId, pasteUrl, deletionKey != null ? deletionKey.getAsString() : null);
                future.complete(result);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    public static class PasteFile {
        private final String name;
        private final String contents;

        public PasteFile(final String name, final String contents) {
            this.name = name;
            this.contents = contents;
        }

        public String getName() {
            return name;
        }

        public String getContents() {
            return contents;
        }
    }

    public static class PasteResult {
        private final String pasteId;
        private final String pasteUrl;
        private final @Nullable String deletionKey;

        protected PasteResult(String pasteId, final String pasteUrl, final @Nullable String deletionKey) {
            this.pasteId = pasteId;
            this.pasteUrl = pasteUrl;
            this.deletionKey = deletionKey;
        }

        public String getPasteUrl() {
            return pasteUrl;
        }

        public @Nullable String getDeletionKey() {
            return deletionKey;
        }

        public String getPasteId() {
            return pasteId;
        }
    }

}
