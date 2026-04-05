package com.earth2me.essentials.utils;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import io.papermc.lib.PaperLib;
import net.ess3.nms.refl.ReflUtil;
import org.bukkit.Bukkit;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
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
    public static final BukkitVersion v1_20_4_R01 = BukkitVersion.fromString("1.20.4-R0.1-SNAPSHOT");
    public static final BukkitVersion v1_20_6_R01 = BukkitVersion.fromString("1.20.6-R0.1-SNAPSHOT");
    public static final BukkitVersion v1_21_R01 = BukkitVersion.fromString("1.21-R0.1-SNAPSHOT");
    public static final BukkitVersion v1_21_3_R01 = BukkitVersion.fromString("1.21.3-R0.1-SNAPSHOT");
    public static final BukkitVersion v1_21_5_R01 = BukkitVersion.fromString("1.21.5-R0.1-SNAPSHOT");
    public static final BukkitVersion v1_21_8_R01 = BukkitVersion.fromString("1.21.8-R0.1-SNAPSHOT");
    public static final BukkitVersion v1_21_11_R01 = BukkitVersion.fromString("1.21.11-R0.1-SNAPSHOT");
    public static final BukkitVersion v26_1_R01 = BukkitVersion.fromString("26.1.1-R0.1-SNAPSHOT");

    private static final Set<BukkitVersion> supportedVersions = ImmutableSet.of(v1_8_8_R01, v1_9_4_R01, v1_10_2_R01, v1_11_2_R01, v1_12_2_R01, v1_13_2_R01, v1_14_4_R01, v1_15_2_R01, v1_16_5_R01, v1_17_1_R01, v1_18_2_R01, v1_19_4_R01, v1_20_6_R01, v1_21_11_R01, v26_1_R01);

    public static final boolean PRE_FLATTENING = VersionUtil.getServerBukkitVersion().isLowerThan(VersionUtil.v1_13_0_R01);

    public static final List<String> officialPlugins = Arrays.asList(
            "EssentialsAntiBuild",
            "EssentialsChat",
            "EssentialsDiscord",
            "EssentialsDiscordLink",
            "EssentialsGeoIP",
            "EssentialsProtect",
            "EssentialsSpawn",
            "EssentialsXMPP"
    );
    public static final List<String> warnPlugins = Arrays.asList(
            "PermissionsEx",
            "GroupManager",
            "bPermissions",

            // Brain-dead chat signing bypass that break EssentialsChat
            "NoChatReports",
            "NoEncryption"
    );

    private static final Map<String, SupportStatus> unsupportedServerClasses;
    private static final String PFX = make("8(;4>`");

    static {
        final ImmutableMap.Builder<String, SupportStatus> builder = new ImmutableMap.Builder<>();

        // Leaf, yet another "High Performance" fork of Paper. Not supported by EssentialsX.
        builder.put(make("5(=t>(??;7t6?;<t\\026?;<\\03055.).(;*"), SupportStatus.DANGEROUS_FORK);
        builder.put("brand:Leaf", SupportStatus.DANGEROUS_FORK);
        builder.put(PFX + make("\\026?;<"), SupportStatus.DANGEROUS_FORK);

        // KibblePatcher - Dangerous bytecode editor snakeoil whose only use is to break plugins
        builder.put("net.kibblelands.server.FastMath", SupportStatus.DANGEROUS_FORK);

        // Brain-dead chat signing bypass that break EssentialsChat
        builder.put("ml.tcoded.nochatreports.NoChatReportsSpigot", SupportStatus.STUPID_PLUGIN);
        builder.put("me.doclic.noencryption.NoEncryption", SupportStatus.STUPID_PLUGIN);

        // Forge - Doesn't support Bukkit
        builder.put("net.minecraftforge.common.MinecraftForge", SupportStatus.UNSTABLE);
        builder.put(make("4?.t734?9(;<.<5(=?t957754t\\02734?9(;<.\\0345(=?"), SupportStatus.UNSTABLE);
        builder.put(PFX + make("\\027523)."), SupportStatus.UNSTABLE);
        builder.put("brand:Mohist", SupportStatus.UNSTABLE);

        // Fabric - Doesn't support Bukkit
        // The below translates to net.fabricmc.loader.launch.knot.KnotServer
        builder.put("net.fabricmc.loader.launch.knot.KnotServer", SupportStatus.UNSTABLE);
        builder.put(make("4?.t<;8(3979t65;>?(t6;/492t145.t\\02145.\\t?(,?("), SupportStatus.UNSTABLE);
        builder.put(PFX + make("\\0035/?("), SupportStatus.UNSTABLE);

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

                if (entry.getKey().startsWith(PFX)) {
                    if (Bukkit.getName().equalsIgnoreCase(entry.getKey().replaceFirst(PFX, ""))) {
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

            if (!isSupportedVersion(getServerBukkitVersion())) {
                return supportStatus = SupportStatus.OUTDATED;
            }

            return supportStatus = PaperLib.isPaper() ? SupportStatus.FULL : SupportStatus.LIMITED;
        }
        return supportStatus;
    }

    public static String getSupportStatusClass() {
        return supportStatusClass;
    }

    /**
     * Checks if a version is considered supported, either by exact match in the
     * supported versions set, or by matching the base version (major.minor.patch)
     * of a supported version. This handles both the new PaperMC versioning scheme
     * (which omits the Bukkit revision suffix) and development builds that report
     * versions like {@code 26.1-rc-3.build.8-alpha}.
     */
    private static boolean isSupportedVersion(final BukkitVersion version) {
        if (supportedVersions.contains(version)) {
            return true;
        }
        // Check if this version matches a supported base version.
        // This covers Paper versions (no -R0.1-SNAPSHOT suffix) and
        // dev variants (snapshot/pre-release/RC/Paper builds).
        for (final BukkitVersion supported : supportedVersions) {
            if (version.equalsBaseVersion(supported)) {
                return true;
            }
        }
        return false;
    }

    public static final class BukkitVersion implements Comparable<BukkitVersion> {
        private static final Pattern VERSION_PATTERN = Pattern.compile("^(\\d+)\\.(\\d+)\\.?([0-9]*)?(?:-snapshot-(\\d+))?(?:-pre-?(\\d+))?(?:-rc-?(\\d+))?(?:\\.build\\.(\\d+)(?:-([a-z]+))?)?(?:-?R?([\\d.]+))?(?:-SNAPSHOT)?");
        private static final Pattern LEGACY_SNAPSHOT_PATTERN = Pattern.compile("^(\\d{2})w(\\d{2})([a-z])(?:-?R?([\\d.]+))?(?:-SNAPSHOT)?");

        private final int major;
        private final int minor;
        private final int snapshotRelease;
        private final int preRelease;
        private final int releaseCandidate;
        private final int paperBuild;
        private final String releaseChannel;
        private final int patch;
        private final double revision;

        private final boolean snapshot;
        private final int snapshotYear;
        private final int snapshotWeek;
        private final char snapshotLetter;

        private BukkitVersion(final int major, final int minor, final int patch, final double revision, final int snapshotRelease, final int preRelease, final int releaseCandidate, final int paperBuild, final String releaseChannel) {
            this.major = major;
            this.minor = minor;
            this.patch = patch;
            this.revision = revision;
            this.snapshotRelease = snapshotRelease;
            this.preRelease = preRelease;
            this.releaseCandidate = releaseCandidate;
            this.paperBuild = paperBuild;
            this.releaseChannel = releaseChannel;
            this.snapshot = false;
            this.snapshotYear = -1;
            this.snapshotWeek = -1;
            this.snapshotLetter = '\0';
        }

        private BukkitVersion(final int major, final int minor, final int patch, final double revision, final int snapshotRelease, final int preRelease, final int releaseCandidate, final int paperBuild, final String releaseChannel,
                               final boolean snapshot, final int snapshotYear, final int snapshotWeek, final char snapshotLetter) {
            this.major = major;
            this.minor = minor;
            this.patch = patch;
            this.revision = revision;
            this.snapshotRelease = snapshotRelease;
            this.preRelease = preRelease;
            this.releaseCandidate = releaseCandidate;
            this.paperBuild = paperBuild;
            this.releaseChannel = releaseChannel;
            this.snapshot = snapshot;
            this.snapshotYear = snapshotYear;
            this.snapshotWeek = snapshotWeek;
            this.snapshotLetter = snapshotLetter;
        }

        public static BukkitVersion fromString(final String string) {
            Preconditions.checkNotNull(string, "string cannot be null.");

            // Try standard release format first
            Matcher matcher = VERSION_PATTERN.matcher(string);
            if (matcher.matches()) {
                return from(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(9), matcher.group(4), matcher.group(5), matcher.group(6), matcher.group(7), matcher.group(8));
            }

            // Try snapshot format (e.g., 25w32a-R0.1-SNAPSHOT)
            final Matcher snapshotMatcher = LEGACY_SNAPSHOT_PATTERN.matcher(string);
            if (snapshotMatcher.matches()) {
                final int year = Integer.parseInt(snapshotMatcher.group(1));
                final int week = Integer.parseInt(snapshotMatcher.group(2));
                final char letter = snapshotMatcher.group(3).charAt(0);
                String revision = snapshotMatcher.group(4);
                if (revision == null || revision.isEmpty()) {
                    revision = "0";
                }
                return fromSnapshot(year, week, letter, Double.parseDouble(revision));
            }

            // Fallback for fake server environment
            if (!Bukkit.getName().equals("Essentials Fake Server")) {
                throw new IllegalArgumentException(string + " is not in valid version format. e.g. 1.8.8-R0.1");
            }
            matcher = VERSION_PATTERN.matcher(v1_16_1_R01.toString());
            Preconditions.checkArgument(matcher.matches(), string + " is not in valid version format. e.g. 1.8.8-R0.1");
            return from(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(9), matcher.group(4), matcher.group(5), matcher.group(6), matcher.group(7), matcher.group(8));
        }

        private static BukkitVersion from(final String major, final String minor, String patch, String revision, String snapshotRelease, String preRelease, String releaseCandidate, String paperBuild, final String releaseChannel) {
            if (patch == null || patch.isEmpty()) patch = "0";
            if (revision == null || revision.isEmpty()) revision = "0";
            if (snapshotRelease == null || snapshotRelease.isEmpty()) snapshotRelease = "-1";
            if (preRelease == null || preRelease.isEmpty()) preRelease = "-1";
            if (releaseCandidate == null || releaseCandidate.isEmpty()) releaseCandidate = "-1";
            if (paperBuild == null || paperBuild.isEmpty()) paperBuild = "-1";
            return new BukkitVersion(Integer.parseInt(major),
                Integer.parseInt(minor),
                Integer.parseInt(patch),
                Double.parseDouble(revision),
                Integer.parseInt(snapshotRelease),
                Integer.parseInt(preRelease),
                Integer.parseInt(releaseCandidate),
                Integer.parseInt(paperBuild),
                releaseChannel);
        }

        private static BukkitVersion fromSnapshot(final int year, final int week, final char letter, final double revision) {
            return new BukkitVersion(-1, -1, -1, revision, -1, -1, -1, -1, null, true, year, week, letter);
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

        public int getSnapshotRelease() {
            return snapshotRelease;
        }

        public int getPaperBuild() {
            return paperBuild;
        }

        public String getReleaseChannel() {
            return releaseChannel;
        }

        public boolean isSnapshot() {
            return snapshot;
        }

        /**
         * Checks if this version has the same base version (major, minor, and patch)
         * as the given version, ignoring development specifiers and Bukkit metadata.
         * Revision is ignored because Paper versions do not include the Bukkit
         * revision suffix (e.g. {@code -R0.1-SNAPSHOT}).
         */
        public boolean equalsBaseVersion(final BukkitVersion other) {
            if (this.snapshot || other.snapshot) {
                return false;
            }
            return this.major == other.major &&
                this.minor == other.minor &&
                this.patch == other.patch;
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
            if (snapshot || that.snapshot) {
                return snapshot == that.snapshot &&
                    snapshotYear == that.snapshotYear &&
                    snapshotWeek == that.snapshotWeek &&
                    snapshotLetter == that.snapshotLetter &&
                    Double.compare(revision, that.revision) == 0;
            }
            return major == that.major &&
                minor == that.minor &&
                patch == that.patch &&
                revision == that.revision &&
                snapshotRelease == that.snapshotRelease &&
                preRelease == that.preRelease &&
                releaseCandidate == that.releaseCandidate;
        }

        @Override
        public int hashCode() {
            if (snapshot) {
                return Objects.hashCode("snapshot", snapshotYear, snapshotWeek, snapshotLetter, revision);
            }
            return Objects.hashCode(major, minor, patch, revision, snapshotRelease, preRelease, releaseCandidate);
        }

        @Override
        public String toString() {
            if (snapshot) {
                return snapshotYear + "w" + snapshotWeek + snapshotLetter;
            }
            final StringBuilder sb = new StringBuilder(major + "." + minor);
            if (patch != 0) {
                sb.append(".").append(patch);
            }
            if (snapshotRelease != -1) {
                sb.append("-snapshot-").append(snapshotRelease);
            }
            if (preRelease != -1) {
                sb.append("-pre-").append(preRelease);
            }
            if (releaseCandidate != -1) {
                sb.append("-rc-").append(releaseCandidate);
            }
            if (paperBuild != -1) {
                sb.append(".build.").append(paperBuild);
                if (releaseChannel != null) {
                    sb.append("-").append(releaseChannel);
                }
            }
            return sb.append("-R").append(revision).toString();
        }

        private static int compareDevSpecifier(final int a, final int b) {
            if (a == b) return 0;
            if (a == -1) return 1;
            if (b == -1) return -1;
            return Integer.compare(a, b);
        }

        @Override
        public int compareTo(final BukkitVersion o) {
            // Snapshots are always considered the most recent
            if (snapshot && !o.snapshot) {
                return 1;
            } else if (!snapshot && o.snapshot) {
                return -1;
            } else if (snapshot /* && o.snapshot */) {
                if (snapshotYear != o.snapshotYear) {
                    return Integer.compare(snapshotYear, o.snapshotYear);
                }
                if (snapshotWeek != o.snapshotWeek) {
                    return Integer.compare(snapshotWeek, o.snapshotWeek);
                }
                if (snapshotLetter != o.snapshotLetter) {
                    return Character.compare(snapshotLetter, o.snapshotLetter);
                }
                return Double.compare(revision, o.revision);
            }

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
                        // For dev specifiers, -1 means absent (final release) and
                        // sorts HIGHER than any present value: snapshot < pre < rc < release
                        int cmp = compareDevSpecifier(snapshotRelease, o.snapshotRelease);
                        if (cmp != 0) return cmp;
                        cmp = compareDevSpecifier(preRelease, o.preRelease);
                        if (cmp != 0) return cmp;
                        cmp = compareDevSpecifier(releaseCandidate, o.releaseCandidate);
                        if (cmp != 0) return cmp;
                        if (paperBuild < o.paperBuild) {
                            return -1;
                        } else if (paperBuild > o.paperBuild) {
                            return 1;
                        } else {
                            return Double.compare(revision, o.revision);
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

    private static String make(String in) {
        final char[] c = in.toCharArray();
        for (int i = 0; i < c.length; i++) {
            c[i] ^= 0x5A;
        }
        return new String(c);
    }
}
