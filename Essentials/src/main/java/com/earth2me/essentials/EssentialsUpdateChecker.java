package com.earth2me.essentials;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;

public class EssentialsUpdateChecker {
    private final static String REPO = "EssentialsX/Essentials";
    private final static String BRANCH = "2.x";

    private static String releaseString = null;
    private static UpdateToken cachedDev = null;
    private static UpdateToken cachedRelease = null;

    public static UpdateToken getDevToken() {
        if (cachedDev == null) {
            cachedDev = fetchDistance(BRANCH, getCommitHash());
        }
        return cachedDev;
    }

    public static UpdateToken getReleaseToken() {
        if (cachedRelease == null) {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL("https://api.github.com/repos/" + REPO + "/releases").openConnection();
                connection.connect();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                    // Locally built?
                    return cachedRelease = new UpdateToken(BranchStatus.UNKNOWN);
                }
                if (connection.getResponseCode() == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                    // Github is down
                    return new UpdateToken(BranchStatus.ERROR);
                }

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charsets.UTF_8))) {
                    JsonObject latestRelease = new Gson().fromJson(reader, JsonArray.class).get(0).getAsJsonObject();
                    String release = latestRelease.get("tag_name").getAsString();
                    cachedRelease = fetchDistance(release, getCommitHash());
                } catch (JsonSyntaxException | NumberFormatException e) {
                    e.printStackTrace();
                    return new UpdateToken(BranchStatus.ERROR);
                }
            } catch (IOException e) {
                e.printStackTrace();
                return new UpdateToken(BranchStatus.ERROR);
            }
        }
        return cachedRelease;
    }

    public static String getCommitHash() {
        if (releaseString == null) {
            try {
                releaseString = Files.asCharSource(new File(EssentialsUpdateChecker.class.getClassLoader().getResource("release").toURI()), Charsets.UTF_8).readFirstLine();
            } catch (IOException | URISyntaxException e) {
                return null;
            }
        }
        return releaseString;
    }

    private static UpdateToken fetchDistance(final String head, final String hash) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("https://api.github.com/repos/" + REPO + "/compare/" + head + "..." + hash).openConnection();
            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                // Locally built?
                return new UpdateToken(BranchStatus.UNKNOWN);
            }
            if (connection.getResponseCode() == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                // Github is down
                return new UpdateToken(BranchStatus.ERROR);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charsets.UTF_8))) {
                JsonObject obj = new Gson().fromJson(reader, JsonObject.class);
                switch (obj.get("status").getAsString()) {
                    case "identical": {
                        return new UpdateToken(BranchStatus.IDENTICAL, 0);
                    }
                    case "ahead": {
                        return new UpdateToken(BranchStatus.AHEAD, obj.get("ahead_by").getAsInt());
                    }
                    case "behind": {
                        return new UpdateToken(BranchStatus.BEHIND, obj.get("behind_by").getAsInt());
                    }
                    case "diverged": {
                        return new UpdateToken(BranchStatus.DIVERGED, obj.get("behind_by").getAsInt());
                    }
                    default: {
                        return new UpdateToken(BranchStatus.UNKNOWN);
                    }
                }
            } catch (JsonSyntaxException | NumberFormatException e) {
                e.printStackTrace();
                return new UpdateToken(BranchStatus.ERROR);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new UpdateToken(BranchStatus.ERROR);
        }
    }

    private static class UpdateToken {
        private final BranchStatus branchStatus;
        private final int distance;

        public UpdateToken(BranchStatus branchStatus) {
            this(branchStatus, 0);
        }

        public UpdateToken(BranchStatus branchStatus, int distance) {
            this.branchStatus = branchStatus;
            this.distance = distance;
        }

        public BranchStatus getBranchStatus() {
            return branchStatus;
        }

        public int getDistance() {
            return distance;
        }
    }

    private enum BranchStatus {
        IDENTICAL,
        AHEAD,
        BEHIND,
        DIVERGED,
        ERROR,
        UNKNOWN
    }
}
