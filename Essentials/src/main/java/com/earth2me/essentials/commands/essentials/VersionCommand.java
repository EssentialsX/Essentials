package com.earth2me.essentials.commands.essentials;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.adventure.ComponentHolder;
import com.earth2me.essentials.commands.EssentialsTreeNode;
import com.earth2me.essentials.economy.EconomyLayer;
import com.earth2me.essentials.economy.EconomyLayers;
import com.earth2me.essentials.utils.VersionUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;

import java.util.Arrays;
import java.util.List;

public class VersionCommand extends EssentialsTreeNode {
    private static final List<String> versionPlugins = Arrays.asList(
            "Vault", // API
            "Reserve", // API
            "PlaceholderAPI", // API
            "CMI", // potential for issues
            "Towny", // past issues; admins should ensure latest
            "ChestShop", // past issues; admins should ensure latest
            "Citizens", // fires player events
            "LuckPerms", // permissions (recommended)
            "UltraPermissions",
            "PermissionsEx", // permissions (unsupported)
            "GroupManager", // permissions (unsupported)
            "bPermissions", // permissions (unsupported)
            "DiscordSRV", // potential for issues if EssentialsXDiscord is installed

            // Chat signing bypass plugins that can potentially break EssentialsChat
            "AntiPopup",
            "NoChatReports",
            "NoEncryption"
    );

    public VersionCommand() {
        super("version", "ver");
    }

    @Override
    protected void run(CommandSource sender, String commandLabel, String[] args) throws Exception {
        if (sender.isPlayer() && !ess.getUser(sender.getPlayer()).isAuthorized("essentials.version")) return;

        boolean isMismatched = false;
        boolean isVaultInstalled = false;
        boolean isUnsupported = false;
        final VersionUtil.SupportStatus supportStatus = VersionUtil.getServerSupportStatus();
        final PluginManager pm = Bukkit.getPluginManager();
        final String essVer = pm.getPlugin("Essentials").getDescription().getVersion();

        final String serverMessageKey;
        if (supportStatus.isSupported()) {
            serverMessageKey = "versionOutputFine";
        } else if (supportStatus == VersionUtil.SupportStatus.UNSTABLE) {
            serverMessageKey = "versionOutputUnsupported";
        } else {
            serverMessageKey = "versionOutputWarn";
        }

        sender.sendTl(serverMessageKey, "Server", Bukkit.getBukkitVersion() + " " + Bukkit.getVersion());
        sender.sendTl(serverMessageKey, "Brand", Bukkit.getName());
        sender.sendTl("versionOutputFine", "EssentialsX", essVer);

        for (final Plugin plugin : pm.getPlugins()) {
            final PluginDescriptionFile desc = plugin.getDescription();
            String name = desc.getName();
            final String version = desc.getVersion();

            if (name.startsWith("Essentials") && !name.equalsIgnoreCase("Essentials")) {
                if (VersionUtil.officialPlugins.contains(name)) {
                    name = name.replace("Essentials", "EssentialsX");

                    if (!version.equalsIgnoreCase(essVer)) {
                        isMismatched = true;
                        sender.sendTl("versionOutputWarn", name, version);
                    } else {
                        sender.sendTl("versionOutputFine", name, version);
                    }
                } else {
                    sender.sendTl("versionOutputUnsupported", name, version);
                    isUnsupported = true;
                }
            }

            if (versionPlugins.contains(name)) {
                if (VersionUtil.warnPlugins.contains(name)) {
                    sender.sendTl("versionOutputUnsupported", name, version);
                    isUnsupported = true;
                } else {
                    sender.sendTl("versionOutputFine", name, version);
                }
            }

            if (name.equals("Vault")) isVaultInstalled = true;
        }

        final String layer;
        if (ess.getSettings().isEcoDisabled()) {
            layer = "Disabled";
        } else if (EconomyLayers.isLayerSelected()) {
            final EconomyLayer economyLayer = EconomyLayers.getSelectedLayer();
            layer = economyLayer.getName() + " (" + economyLayer.getBackendName() + ")";
        } else {
            layer = "None";
        }
        sender.sendTl("versionOutputEconLayer", layer);

        if (isMismatched) {
            sender.sendTl("versionMismatchAll");
        }

        if (!isVaultInstalled) {
            sender.sendTl("versionOutputVaultMissing");
        }

        if (isUnsupported) {
            sender.sendTl("versionOutputUnsupportedPlugins");
        }

        switch (supportStatus) {
            case NMS_CLEANROOM:
                sender.sendTl("serverUnsupportedCleanroom");
                break;
            case DANGEROUS_FORK:
                sender.sendTl("serverUnsupportedDangerous");
                break;
            case STUPID_PLUGIN:
                sender.sendTl("serverUnsupportedDumbPlugins");
                break;
            case UNSTABLE:
                sender.sendTl("serverUnsupportedMods");
                break;
            case OUTDATED:
                sender.sendTl("serverUnsupported");
                break;
            case LIMITED:
                sender.sendTl("serverUnsupportedLimitedApi");
                break;
        }
        if (VersionUtil.getSupportStatusClass() != null) {
            sender.sendTl("serverUnsupportedClass");
        }

        sender.sendTl("versionFetching");
        ess.runTaskAsynchronously(() -> {
            for (final ComponentHolder component : ess.getUpdateChecker().getVersionMessages(true, true, sender)) {
                sender.sendComponent(component);
            }
        });
    }
}
