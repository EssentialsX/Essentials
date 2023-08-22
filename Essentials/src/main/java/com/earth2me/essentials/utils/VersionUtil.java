package com.earth2me.essentials.utils;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import io.papermc.lib.PaperLib;
import net.ess3.nms.refl.ReflUtil;
import org.bukkit.Bukkit;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class VersionUtil {

    public static final BukkitVersion v1_14_R01 = BukkitVersion.fromString("1.14-R0.1-SNAPSHOT");
    public static final BukkitVersion v1_8_8_R01 = BukkitVersion.fromString("1.8.8-R0.1-SNAPSHOT");
    public static final BukkitVersion v1_9_R01 = BukkitVersion.fromString("1.9-R0.1-SNAPSHOT");
    public static final BukkitVersion v1_9_4_R01 = BukkitVersion.fromString("1.9.4-R0.1-SNAPSHOT");
    public static final BukkitVersion v1_10_2_R01 = BukkitVersion.fromString("1.10.2-R0.1-SNAPSHOT");
    public static final BukkitVersion v1_11_R01 = BukkitVersion.fromString("1.11-R0.1-SNAPSHOT");
    public static final BukkitVersion v1_11_2_R01 = BukkitVersion.fromString("1.11.2-R0.1-SNAPSHOT");
    public static final BukkitVersion v1_12_0_R01 = BukkitVersion.fromString("1.12.0-R0.1-SNAPSHOT");
    public static final BukkitVersion v1_12_2_R01 = BukkitVersion.fromString("1.12.2-R0.1-SNAPSHOT");
    public static final BukkitVersion v1_13_0_R01 = BukkitVersion.fromString("1.13.0-R0.1-SNAPSHOT");
    public static final BukkitVersion v1_13_2_R01 = BukkitVersion.fromString("1.13.2-R0.1-SNAPSHOT");
    public static final BukkitVersion v1_14_4_R01 = BukkitVersion.fromString("1.14.4-R0.1-SNAPSHOT");
    public static final BukkitVersion v1_15_R01 = BukkitVersion.fromString("1.15-R0.1-SNAPSHOT");
    public static final BukkitVersion v1_15_2_R01 = BukkitVersion.fromString("1.15.2-R0.1-SNAPSHOT");
    public static final BukkitVersion v1_16_1_R01 = BukkitVersion.fromString("1.16.1-R0.1-SNAPSHOT");
    public static final BukkitVersion v1_16_5_R01 = BukkitVersion.fromString("1.16.5-R0.1-SNAPSHOT");
    public static final BukkitVersion v1_17_R01 = BukkitVersion.fromString("1.17-R0.1-SNAPSHOT");
    public static final BukkitVersion v1_17_1_R01 = BukkitVersion.fromString("1.17.1-R0.1-SNAPSHOT");
    public static final BukkitVersion v1_18_2_R01 = BukkitVersion.fromString("1.18.2-R0.1-SNAPSHOT");
    public static final BukkitVersion v1_19_R01 = BukkitVersion.fromString("1.19-R0.1-SNAPSHOT");
    public static final BukkitVersion v1_19_4_R01 = BukkitVersion.fromString("1.19.4-R0.1-SNAPSHOT");
    public static final BukkitVersion v1_20_1_R01 = BukkitVersion.fromString("1.20.1-R0.1-SNAPSHOT");

    private static final Set<BukkitVersion> supportedVersions = ImmutableSet.of(v1_8_8_R01, v1_9_4_R01, v1_10_2_R01, v1_11_2_R01, v1_12_2_R01, v1_13_2_R01, v1_14_4_R01, v1_15_2_R01, v1_16_5_R01, v1_17_1_R01, v1_18_2_R01, v1_19_4_R01, v1_20_1_R01);

    public static final boolean PRE_FLATTENING = VersionUtil.getServerBukkitVersion().isLowerThan(VersionUtil.v1_13_0_R01);
    public static final boolean FOLIA;

    private static final Map<String, SupportStatus> unsupportedServerClasses;

    static {
        boolean isFolia;
        try {
            Class.forName("io.papermc.paper.threadedregions.scheduler.AsyncScheduler");
            isFolia = true;
        } catch (Throwable ignored) {
            isFolia = false;
        }
        FOLIA = isFolia;

        final ImmutableMap.Builder<String, SupportStatus> builder = new ImmutableMap.Builder<>();

        // Yatopia - Extremely volatile patch set;
        //   * Messes with proxy-forwarded UUIDs
        //   * Frequent data corruptions
        builder.put("org.yatopiamc.yatopia.server.YatopiaConfig", SupportStatus.DANGEROUS_FORK);
        builder.put("net.yatopia.api.event.PlayerAttackEntityEvent", SupportStatus.DANGEROUS_FORK);
        builder.put("org.bukkit.plugin.SimplePluginManager#getPluginLoaders", SupportStatus.DANGEROUS_FORK);
        builder.put("org.bukkit.Bukkit#getLastTickTime", SupportStatus.DANGEROUS_FORK);
        builder.put("brand:Yatopia", SupportStatus.DANGEROUS_FORK);
        // Yatopia downstream(s) which attempt to do tricky things :)
        builder.put("brand:Hyalus", SupportStatus.DANGEROUS_FORK);

        // KibblePatcher - Dangerous bytecode editor snakeoil whose only use is to break plugins
        builder.put("net.kibblelands.server.FastMath", SupportStatus.DANGEROUS_FORK);

        // Brain-dead chat signing bypass that break EssentialsChat
        builder.put("ml.tcoded.nochatreports.NoChatReportsSpigot", SupportStatus.STUPID_PLUGIN);
        builder.put("me.doclic.noencryption.NoEncryption", SupportStatus.STUPID_PLUGIN);

        // Akarin - Dangerous patch history;
        //   * Potentially unsafe saving of nms.JsonList
        builder.put("io.akarin.server.Config", SupportStatus.DANGEROUS_FORK);

        // Forge - Doesn't support Bukkit
        // The below translates to net.minecraftforge.common.MinecraftForge
        builder.put(dumb(new int[] {110, 101, 116, 46, 109, 105, 110, 101, 99, 114, 97, 102, 116, 102, 111, 114, 103, 101, 46, 99, 111, 109, 109, 111, 110, 46, 77, 105, 110, 101, 99, 114, 97, 102, 116, 70, 111, 114, 103, 101}, 40), SupportStatus.UNSTABLE);

        // Fabric - Doesn't support Bukkit
        // The below translates to net.fabricmc.loader.launch.knot.KnotServer
        builder.put(dumb(new int[] {110, 101, 116, 46, 102, 97, 98, 114, 105, 99, 109, 99, 46, 108, 111, 97, 100, 101, 114, 46, 108, 97, 117, 110, 99, 104, 46, 107, 110, 111, 116, 46, 75, 110, 111, 116, 83, 101, 114, 118, 101, 114}, 42), SupportStatus.UNSTABLE);

        // Misc translation layers that do not add NMS will be caught by this
        if (ReflUtil.getNmsVersionObject().isHigherThanOrEqualTo(ReflUtil.V1_17_R1)) {
            builder.put("!net.minecraft.server.MinecraftServer", SupportStatus.NMS_CLEANROOM);
        } else {
            builder.put("!net.minecraft.server." + ReflUtil.getNMSVersion() + ".MinecraftServer", SupportStatus.NMS_CLEANROOM);
        }

        unsupportedServerClasses = builder.build();
    }

    private static BukkitVersion serverVersion = null;
    private static SupportStatus supportStatus = null;
    // Used to find the specific class that caused a given support status
    private static String supportStatusClass = null;

    private VersionUtil() {
    }

    public static boolean isPaper() {
        return PaperLib.isPaper();
    }

    public static BukkitVersion getServerBukkitVersion() {
        if (serverVersion == null) {
            serverVersion = BukkitVersion.fromString(Bukkit.getServer().getBukkitVersion());
        }
        return serverVersion;
    }

    public static SupportStatus getServerSupportStatus() {
        if (supportStatus == null) {
            for (Map.Entry<String, SupportStatus> entry : unsupportedServerClasses.entrySet()) {

                if (entry.getKey().startsWith("brand:")) {
                    if (Bukkit.getName().equalsIgnoreCase(entry.getKey().replaceFirst("brand:", ""))) {
                        supportStatusClass = entry.getKey();
                        return supportStatus = entry.getValue();
                    }
                    continue;
                }

                final boolean inverted = entry.getKey().contains("!");
                final String clazz = entry.getKey().replace("!", "").split("#")[0];
                String method = "";
                if (entry.getKey().contains("#")) {
                    method = entry.getKey().split("#")[1];
                }
                try {
                    final Class<?> lolClass = Class.forName(clazz);

                    if (!method.isEmpty()) {
                        for (final Method mth : lolClass.getDeclaredMethods()) {
                            if (mth.getName().equals(method)) {
                                if (!inverted) {
                                    supportStatusClass = entry.getKey();
                                    return supportStatus = entry.getValue();
                                }
                            }
                        }
                        continue;
                    }

                    if (!inverted) {
                        supportStatusClass = entry.getKey();
                        return supportStatus = entry.getValue();
                    }
                } catch (final ClassNotFoundException ignored) {
                    if (inverted) {
                        supportStatusClass = entry.getKey();
                        return supportStatus = entry.getValue();
                    }
                }
            }

            if (!supportedVersions.contains(getServerBukkitVersion())) {
                return supportStatus = SupportStatus.OUTDATED;
            }

            return supportStatus = PaperLib.isPaper() ? SupportStatus.FULL : SupportStatus.LIMITED;
        }
        return supportStatus;
    }

    public static String getSupportStatusClass() {
        return supportStatusClass;
    }

    public static boolean isServerSupported() {
        return getServerSupportStatus().isSupported();
    }

    public static final class BukkitVersion implements Comparable<BukkitVersion> {
        private static final Pattern VERSION_PATTERN = Pattern.compile("^(\\d+)\\.(\\d+)\\.?([0-9]*)?(?:-pre(\\d))?(?:-rc(\\d+))?(?:-?R?([\\d.]+))?(?:-SNAPSHOT)?");

        private final int major;
        private final int minor;
        private final int preRelease;
        private final int releaseCandidate;
        private final int patch;
        private final double revision;

        private BukkitVersion(final int major, final int minor, final int patch, final double revision, final int preRelease, final int releaseCandidate) {
            this.major = major;
            this.minor = minor;
            this.patch = patch;
            this.revision = revision;
            this.preRelease = preRelease;
            this.releaseCandidate = releaseCandidate;
        }

        public static BukkitVersion fromString(final String string) {
            Preconditions.checkNotNull(string, "string cannot be null.");
            Matcher matcher = VERSION_PATTERN.matcher(string);
            if (!matcher.matches()) {
                if (!Bukkit.getName().equals("Essentials Fake Server")) {
                    throw new IllegalArgumentException(string + " is not in valid version format. e.g. 1.8.8-R0.1");
                }
                matcher = VERSION_PATTERN.matcher(v1_16_1_R01.toString());
                Preconditions.checkArgument(matcher.matches(), string + " is not in valid version format. e.g. 1.8.8-R0.1");
            }

            return from(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(6), matcher.group(4), matcher.group(5));
        }

        private static BukkitVersion from(final String major, final String minor, String patch, String revision, String preRelease, String releaseCandidate) {
            if (patch == null || patch.isEmpty()) patch = "0";
            if (revision == null || revision.isEmpty()) revision = "0";
            if (preRelease == null || preRelease.isEmpty()) preRelease = "-1";
            if (releaseCandidate == null || releaseCandidate.isEmpty()) releaseCandidate = "-1";
            return new BukkitVersion(Integer.parseInt(major),
                Integer.parseInt(minor),
                Integer.parseInt(patch),
                Double.parseDouble(revision),
                Integer.parseInt(preRelease),
                Integer.parseInt(releaseCandidate));
        }

        public boolean isHigherThan(final BukkitVersion o) {
            return compareTo(o) > 0;
        }

        public boolean isHigherThanOrEqualTo(final BukkitVersion o) {
            return compareTo(o) >= 0;
        }

        public boolean isLowerThan(final BukkitVersion o) {
            return compareTo(o) < 0;
        }

        public boolean isLowerThanOrEqualTo(final BukkitVersion o) {
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

        public int getPrerelease() {
            return preRelease;
        }

        public int getReleaseCandidate() {
            return releaseCandidate;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final BukkitVersion that = (BukkitVersion) o;
            return major == that.major &&
                minor == that.minor &&
                patch == that.patch &&
                revision == that.revision &&
                preRelease == that.preRelease;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(major, minor, patch, revision, preRelease, releaseCandidate);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder(major + "." + minor);
            if (patch != 0) {
                sb.append(".").append(patch);
            }
            if (preRelease != -1) {
                sb.append("-pre").append(preRelease);
            }
            if (releaseCandidate != -1) {
                sb.append("-rc").append(releaseCandidate);
            }
            return sb.append("-R").append(revision).toString();
        }

        @Override
        public int compareTo(final BukkitVersion o) {
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
                        if (preRelease < o.preRelease) {
                            return -1;
                        } else if (preRelease > o.preRelease) {
                            return 1;
                        } else { // equal prerelease
                            if (releaseCandidate < o.releaseCandidate) {
                                return -1;
                            } else if (releaseCandidate > o.releaseCandidate) {
                                return 1;
                            } else { // equal release candidate
                                return Double.compare(revision, o.revision);
                            }
                        }
                    }
                }
            }
        }
    }

    public enum SupportStatus {
        FULL(true),
        LIMITED(true),
        DANGEROUS_FORK(false),
        STUPID_PLUGIN(false),
        NMS_CLEANROOM(false),
        UNSTABLE(false),
        OUTDATED(false)
        ;

        private final boolean supported;

        SupportStatus(final boolean supported) {
            this.supported = supported;
        }

        public boolean isSupported() {
            return supported;
        }
    }

    private static String dumb(final int[] clazz, final int len) {
        final char[] chars = new char[clazz.length];

        for (int i = 0; i < clazz.length; i++) {
            chars[i] = (char) clazz[i];
        }

        final String decode = String.valueOf(chars);

        if (decode.length() != len) {
            System.exit(1);
            return "why do hybrids try to bypass this?";
        }

        return decode;
    }
}
