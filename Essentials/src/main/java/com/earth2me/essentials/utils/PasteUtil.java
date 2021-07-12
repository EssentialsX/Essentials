package com.earth2me.essentials.utils;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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

    public static CompletableFuture<String> createPaste(List<String> pages) {
        final CompletableFuture<String> future = new CompletableFuture<>();
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
                for (final String page : pages) {
                    final JsonObject file = new JsonObject();
                    final JsonObject content = new JsonObject();
                    content.addProperty("format", "text");
                    content.addProperty("value", page);
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
                final String pasteUrl = PASTE_URL + object.get("result").getAsJsonObject().get("id").getAsString();
                connection.disconnect();

                future.complete(pasteUrl);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }
}
