package com.earth2me.essentials.utils;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import org.bukkit.Bukkit;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionUtil {

    public static final BukkitVersion v1_8_8_R01 = BukkitVersion.fromString("1.8.8-R0.1-SNAPSHOT");
    public static final BukkitVersion v1_9_R01 = BukkitVersion.fromString("1.9-R0.1-SNAPSHOT");
    public static final BukkitVersion v1_9_4_R01 = BukkitVersion.fromString("1.9.4-R0.1-SNAPSHOT");
    public static final BukkitVersion v1_10_2_R01 = BukkitVersion.fromString("1.10.2-R0.1-SNAPSHOT");
    public static final BukkitVersion v1_11_2_R01 = BukkitVersion.fromString("1.11.2-R0.1-SNAPSHOT");
    public static final BukkitVersion v1_12_0_R01 = BukkitVersion.fromString("1.12.0-R0.1-SNAPSHOT");
    public static final BukkitVersion v1_12_2_R01 = BukkitVersion.fromString("1.12.2-R0.1-SNAPSHOT");
    public static final BukkitVersion v1_13_0_R01 = BukkitVersion.fromString("1.13.0-R0.1-SNAPSHOT");
    public static final BukkitVersion v1_13_2_R01 = BukkitVersion.fromString("1.13.2-R0.1-SNAPSHOT");

    private static final Set<BukkitVersion> supportedVersions = ImmutableSet.of(v1_8_8_R01, v1_9_4_R01, v1_10_2_R01, v1_11_2_R01, v1_12_2_R01, v1_13_2_R01);

    private static BukkitVersion serverVersion = null;

    public static BukkitVersion getServerBukkitVersion() {
        if (serverVersion == null) {
            serverVersion = BukkitVersion.fromString(Bukkit.getServer().getBukkitVersion());
        }
        return serverVersion;
    }

    public static boolean isServerSupported() {
        return supportedVersions.contains(getServerBukkitVersion());
    }

    public static class BukkitVersion implements Comparable<BukkitVersion> {
        private static final Pattern VERSION_PATTERN = Pattern.compile("^(\\d+)\\.(\\d+)\\.?([0-9]*)-R([\\d.]+)(?:-SNAPSHOT)?");

        private final int major;
        private final int minor;
        private final int patch;
        private final double revision;

        public static BukkitVersion fromString(String string) {
            Preconditions.checkNotNull(string, "string cannot be null.");
            Matcher matcher = VERSION_PATTERN.matcher(string);
            if (!matcher.matches()) {
                if (!Bukkit.getName().equals("Essentials Fake Server")) {
                    throw new IllegalArgumentException(string + " is not in valid version format. e.g. 1.8.8-R0.1");
                }
                matcher = VERSION_PATTERN.matcher(v1_13_2_R01.toString());
                Preconditions.checkArgument(matcher.matches(), string + " is not in valid version format. e.g. 1.8.8-R0.1");
            }

            return from(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4));
        }

        private static BukkitVersion from(String major, String minor, String patch, String revision) {
            if (patch.isEmpty()) patch = "0";
            return new BukkitVersion(Integer.parseInt(major),
                Integer.parseInt(minor),
                Integer.parseInt(patch),
                Double.parseDouble(revision));
        }

        private BukkitVersion(int major, int minor, int patch, double revision) {
            this.major = major;
            this.minor = minor;
            this.patch = patch;
            this.revision = revision;
        }

        public boolean isHigherThan(BukkitVersion o) {
            return compareTo(o) > 0;
        }

        public boolean isHigherThanOrEqualTo(BukkitVersion o) {
            return compareTo(o) >= 0;
        }

        public boolean isLowerThan(BukkitVersion o) {
            return compareTo(o) < 0;
        }

        public boolean isLowerThanOrEqualTo(BukkitVersion o) {
            return compareTo(o) <= 0;
        }

        public int getMajor() {
            return major;
        }

        public int getMinor() {
            return minor;
        }

        public int getPatch() {
            return patch;
        }

        public double getRevision() {
            return revision;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            BukkitVersion that = (BukkitVersion) o;
            return major == that.major &&
                    minor == that.minor &&
                    patch == that.patch &&
                    revision == that.revision;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(major, minor, patch, revision);
        }

        @Override
        public String toString() {
            return major + "." + minor + "." + patch + "-R" + revision;
        }

        @Override
        public int compareTo(BukkitVersion o) {
            if (major < o.major) {
                return -1;
            } else if (major > o.major) {
                return 1;
            } else { // equal major
                if (minor < o.minor) {
                    return -1;
                } else if (minor > o.minor) {
                    return 1;
                } else { // equal minor
                    if (patch < o.patch) {
                        return -1;
                    } else if (patch > o.patch) {
                        return 1;
                    } else { // equal patch
                        return Double.compare(revision, o.revision);
                    }
                }
            }
        }
    }
}
