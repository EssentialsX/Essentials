package com.earth2me.essentials.utils;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import net.ess3.api.IEssentials;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PasteUtil {

    private static final String PASTE_URL = "https://api.github.com/gists";
    private static final String SHORTENER_URL = "https://git.io";
    private static final Gson GSON = new Gson();

    public class Gist {
        private final Map<String, String> files = new HashMap<>();
        public String description = "";
        public boolean isPublic = false; // Not sure if necessary

        public Gist(String description) {
            super();
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void addFile(String name, String contents) {
            files.put(name, contents);
        }

        private String toJson() throws Exception {
            Iterator i = files.keySet().iterator();
            StringWriter sw = new StringWriter();
            JsonWriter jw = new JsonWriter(sw).beginObject()
                .name("description").value(description)
                .name("public").value(isPublic)
                .name("files").beginObject();
            while (i.hasNext()) {
                String fileName = (String) i.next();
                String content = files.get(fileName);
                jw.name("kit.yml")
                    .beginObject().name("content").value(content)
                    .endObject();
            }
            jw.endObject().endObject();
            return sw.toString();
        }
    }

    public static URL uploadGist(Gist gist) throws Exception {
        String payload = gist.toJson();
        HttpURLConnection connection = (HttpURLConnection) new URL(PASTE_URL).openConnection();
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            os.write(payload.getBytes());
        }

        if (connection.getResponseCode() >= 400) {
            String message = CharStreams.toString(new InputStreamReader(connection.getErrorStream(), Charsets.UTF_8));
            throw new Exception(message);
        }
        Map<String, String> map = GSON.fromJson(new InputStreamReader(connection.getInputStream(), Charsets.UTF_8),
                new TypeToken<Map<String, Object>>() {}.getType());
        return new URL(map.get("html_url"));
    }

    public static URL shortenUrl(URL url) throws Exception {
        String payload = "url=" + url.toString();
        HttpURLConnection connection = (HttpURLConnection) new URL(SHORTENER_URL).openConnection();
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            os.write(payload.getBytes());
        }

        return new URL(connection.getHeaderField("Location"));
    }

}
