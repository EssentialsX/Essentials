package com.earth2me.essentials;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public final class EssentialsUpdateChecker {
    private final static String REPO = "EssentialsX/Essentials";
    private final static String BRANCH = "2.x";

    private static final String versionIdentifier;
    private static final String versionBranch;
    private static final boolean devBuild;
    private static long lastFetchTime = 0;
    private static CompletableFuture<UpdateToken> pendingDevFuture;
    private static CompletableFuture<UpdateToken> pendingReleaseFuture;
    private static String latestRelease = null;
    private static UpdateToken cachedDev = null;
    private static UpdateToken cachedRelease = null;

    static {
        String identifier = "INVALID";
        String branch = "INVALID";
        boolean dev = false;
        final List<String> versionStr = new BufferedReader(new InputStreamReader(Objects.requireNonNull(EssentialsUpdateChecker.class.getClassLoader().getResourceAsStream("release")), StandardCharsets.UTF_8)).lines().collect(Collectors.toList());
        if (versionStr.size() == 2) {
            if (versionStr.get(0).matches("\\d+\\.\\d+\\.\\d+-dev\\+\\d\\d-[0-9a-f]{7,40}")) {
                identifier = versionStr.get(0).split("-")[2];
                dev = true;
            } else {
                identifier = versionStr.get(0);
            }
            branch = versionStr.get(1);
        }
        versionIdentifier = identifier;
        versionBranch = branch;
        devBuild = dev;
    }

    private EssentialsUpdateChecker() {
    }

    public static boolean isDevBuild() {
        return devBuild;
    }

    public static CompletableFuture<UpdateToken> getDevToken() {
        if (cachedDev == null || ((System.currentTimeMillis() - lastFetchTime) > 300000)) {
            if (pendingDevFuture != null) {
                return pendingDevFuture;
            }
            pendingDevFuture = new CompletableFuture<>();
            new Thread(() -> {
                pendingDevFuture.complete(cachedDev = fetchDistance(BRANCH, getVersionIdentifier()));
                pendingDevFuture = null;
                lastFetchTime = System.currentTimeMillis();
            }).start();
            return pendingDevFuture;
        }
        return CompletableFuture.completedFuture(cachedDev);
    }

    public static CompletableFuture<UpdateToken> getReleaseToken() {
        if (cachedRelease == null || ((System.currentTimeMillis() - lastFetchTime) > 300000)) {
            if (pendingReleaseFuture != null) {
                return pendingReleaseFuture;
            }
            pendingReleaseFuture = new CompletableFuture<>();
            new Thread(() -> {
                catchBlock:
                try {
                    final HttpURLConnection connection = (HttpURLConnection) new URL("https://api.github.com/repos/" + REPO + "/releases/latest").openConnection();
                    connection.connect();

                    if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                        // Locally built?
                        pendingReleaseFuture.complete(cachedRelease = new UpdateToken(BranchStatus.UNKNOWN));
                        break catchBlock;
                    }
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                        // Github is down
                        pendingReleaseFuture.complete(new UpdateToken(BranchStatus.ERROR));
                        break catchBlock;
                    }

                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charsets.UTF_8))) {
                        latestRelease = new Gson().fromJson(reader, JsonObject.class).get("tag_name").getAsString();
                        pendingReleaseFuture.complete(cachedRelease = fetchDistance(latestRelease, getVersionIdentifier()));
                    } catch (JsonSyntaxException | NumberFormatException e) {
                        e.printStackTrace();
                        pendingReleaseFuture.complete(new UpdateToken(BranchStatus.ERROR));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    pendingReleaseFuture.complete(new UpdateToken(BranchStatus.ERROR));
                }
                pendingReleaseFuture = null;
                lastFetchTime = System.currentTimeMillis();
            }).start();
        }
        return CompletableFuture.completedFuture(cachedRelease);
    }

    public static String getVersionIdentifier() {
        return versionIdentifier;
    }

    public static String getVersionBranch() {
        return versionBranch;
    }

    public static String getBuildInfo() {
        return "id:'" + getVersionIdentifier() + "' branch:'" + getVersionBranch() + "' isDev:" + isDevBuild();
    }

    public static String getLatestRelease() {
        return latestRelease;
    }

    private static UpdateToken fetchDistance(final String head, final String hash) {
        try {
            final HttpURLConnection connection = (HttpURLConnection) new URL("https://api.github.com/repos/" + REPO + "/compare/" + head + "..." + hash).openConnection();
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
                final JsonObject obj = new Gson().fromJson(reader, JsonObject.class);
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

    public static class UpdateToken {
        private final BranchStatus branchStatus;
        private final int distance;

        UpdateToken(BranchStatus branchStatus) {
            this(branchStatus, 0);
        }

        UpdateToken(BranchStatus branchStatus, int distance) {
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

    public enum BranchStatus {
        IDENTICAL,
        AHEAD,
        BEHIND,
        DIVERGED,
        ERROR,
        UNKNOWN
    }
}
