package com.earth2me.essentials.compat;

import static com.earth2me.essentials.I18n.tl;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class PluginReport {

    private static String sameVersionPattern = "";

    static {
        Plugin essPlugin = Bukkit.getPluginManager().getPlugin("Essentials");
        if (essPlugin != null) {
            sameVersionPattern = "^" + essPlugin.getDescription().getVersion().replace(".", "\\.");
        }
    }

    public static String generateReport() {
        StringBuilder sb = new StringBuilder();
        boolean isMismatched = false;
        boolean isUnsupported = false;
        boolean isVaultInstalled = false;

        List<ReportEntry> entries = Arrays.stream(Bukkit.getPluginManager().getPlugins())
            .map(ReportEntry::getEntry)
            .filter(Objects::nonNull)
            .sorted()
            .collect(Collectors.toList());

        for (ReportEntry entry : entries) {
            if (entry.type == EntryType.THIRD_PARTY_WARN) {
                isUnsupported = true;
            } else if (entry.type == EntryType.UPSTREAM) {
                isMismatched = true;
            } else if (entry.id.equals("Vault")) {
                isVaultInstalled = true;
            }
            sb.append(getReportLine(entry)).append("\n");
        }

        if (isMismatched) {
            sb.append(tl("versionMismatchAll")).append("\n");
        }

        if (!isVaultInstalled) {
            sb.append(tl("versionOutputVaultMissing")).append("\n");
        }

        if (isUnsupported) {
            sb.append(tl("versionOutputUnsupportedPlugins")).append("\n");
        }

        return sb.toString();
    }

    private static String getReportLine(ReportEntry entry) {
        switch (entry.type) {
            case OFFICIAL:
            case THIRD_PARTY:
                return tl("versionOutputFine", entry.name, entry.getInstalledVersion());
            case UPSTREAM:
                return tl("versionOutputUnsupported", entry.name, entry.getInstalledVersion());
            case THIRD_PARTY_WARN:
                return tl("versionOutputWarn", entry.name, entry.getInstalledVersion());
        }

        throw new RuntimeException();
    }

    enum ReportEntry {
        // EssentialsX
        ESSENTIALSX("Essentials", "EssentialsX", EntryType.OFFICIAL, sameVersionPattern),
        ESSENTIALSX_ANTIBUILD("Essentials", "EssentialsX AntiBuild", EntryType.OFFICIAL, sameVersionPattern),
        ESSENTIALSX_CHAT("Essentials", "EssentialsX Chat", EntryType.OFFICIAL, sameVersionPattern),
        ESSENTIALSX_GEOIP("Essentials", "EssentialsX GeoIP", EntryType.OFFICIAL, sameVersionPattern),
        ESSENTIALSX_PROTECT("Essentials", "EssentialsX Protect", EntryType.OFFICIAL, sameVersionPattern),
        ESSENTIALSX_SPAWN("Essentials", "EssentialsX Spawn", EntryType.OFFICIAL, sameVersionPattern),
        ESSENTIALSX_XMPP("Essentials", "EssentialsX XMPP", EntryType.OFFICIAL, sameVersionPattern),

        // Essentials 2
        LEGACY_ANTIBUILD("Essentials", "Essentials AntiBuild", EntryType.UPSTREAM),
        LEGACY_CHAT("Essentials", "Essentials Chat", EntryType.UPSTREAM),
        LEGACY_GEOIP("Essentials", "Essentials GeoIP", EntryType.UPSTREAM),
        LEGACY_PROTECT("Essentials", "Essentials Protect", EntryType.UPSTREAM),
        LEGACY_SPAWN("Essentials", "Essentials Spawn", EntryType.UPSTREAM),
        LEGACY_XMPP("Essentials", "Essentials XMPP", EntryType.UPSTREAM),

        // APIs
        VAULT("Vault", "Vault", EntryType.THIRD_PARTY),
        PLACEHOLDERAPI("PlaceholderAPI", "PlaceholderAPI", EntryType.THIRD_PARTY),

        // Supported permissions plugins
        PERMISSIONSEX_2("PermissionsEx", "PermissionsEx", EntryType.THIRD_PARTY, "^2\\.[0-9A-Za-z-_\\.]+"),
        LUCKPERMS("LuckPerms", "LuckPerms", EntryType.THIRD_PARTY, "^[45]\\.[0-9\\.]{3,}$"),
        ULTRAPERMISSIONS("UltraPermissions", "UltraPermissions", EntryType.THIRD_PARTY),

        // Unsupported permissions plugins
        PERMISSIONSEX_1("PermissionsEx", "PermissionsEx", EntryType.THIRD_PARTY_WARN),
        BPERMISSIONS("bPermissions", "bPermissions", EntryType.THIRD_PARTY_WARN),
        GROUPMANAGER("GroupManager", "GroupManager", EntryType.THIRD_PARTY_WARN),

        // Misc third-party plugins
        CHESTSHOP("ChestShop", "ChestShop", EntryType.THIRD_PARTY),
        CITIZENS("Citizens", "Citizens", EntryType.THIRD_PARTY),
        CMI("CMI", "CMI", EntryType.THIRD_PARTY_WARN),
        TOWNY("Towny", "Towny", EntryType.THIRD_PARTY_WARN),
        ;

        private final String id;
        private final String name;
        private final EntryType type;
        private final Pattern versionPattern;

        ReportEntry(String id, String name, EntryType type) {
            this(id, name, type, "");
        }

        ReportEntry(String id, String name, EntryType type, String versionMatch) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.versionPattern = Pattern.compile(versionMatch);
        }

        public static ReportEntry getEntry(Plugin plugin) {
            for (ReportEntry entry : ReportEntry.values()) {
                if (entry.id.equals(plugin.getName())) {
                    Matcher matcher = entry.versionPattern.matcher(plugin.getDescription().getVersion());
                    if (matcher.matches()) {
                        return entry;
                    }
                }
            }

            return null;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public EntryType getType() {
            return type;
        }

        public String getInstalledVersion() {
            Plugin plugin = Bukkit.getPluginManager().getPlugin(id);
            if (plugin != null) {
                return plugin.getDescription().getVersion();
            }

            return "unknown";
        }
    }

    enum EntryType {
        OFFICIAL,
        UPSTREAM,
        THIRD_PARTY,
        THIRD_PARTY_WARN
    }
}
