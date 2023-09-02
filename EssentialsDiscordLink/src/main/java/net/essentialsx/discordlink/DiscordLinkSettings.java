package net.essentialsx.discordlink;

import com.earth2me.essentials.IConf;
import com.earth2me.essentials.config.EssentialsConfiguration;

import java.io.File;
import java.util.Map;

public class DiscordLinkSettings implements IConf {
    private final EssentialsDiscordLink plugin;
    private final EssentialsConfiguration config;

    private LinkPolicy linkPolicy;
    private Map<String, String> roleSyncGroups;
    private Map<String, String> roleSyncRoles;

    public DiscordLinkSettings(EssentialsDiscordLink plugin) {
        this.plugin = plugin;
        this.config = new EssentialsConfiguration(new File(plugin.getDataFolder(), "config.yml"), "/config.yml", EssentialsDiscordLink.class);
        reloadConfig();
    }

    public LinkPolicy getLinkPolicy() {
        return linkPolicy;
    }

    public boolean isBlockUnlinkedChat() {
        return config.getBoolean("block-unlinked-chat", false);
    }

    public boolean isUnlinkOnLeave() {
        return config.getBoolean("unlink-on-leave", true);
    }

    public boolean isRelayMail() {
        return config.getBoolean("relay-mail", true);
    }

    public boolean isRoleSyncRemoveRoles() {
        return config.getBoolean("role-sync.remove-roles", true);
    }

    public boolean isRoleSyncRemoveGroups() {
        return config.getBoolean("role-sync.remove-groups", true);
    }

    public int getRoleSyncResyncDelay() {
        return config.getInt("role-sync.resync-delay", 5);
    }

    public boolean isRoleSyncPrimaryGroupOnly() {
        return config.getBoolean("role-sync.primary-group-only", false);
    }

    public Map<String, String> getRoleSyncGroups() {
        return roleSyncGroups;
    }

    private Map<String, String> _getRoleSyncGroups() {
        return config.getStringMap("role-sync.groups");
    }

    public Map<String, String> getRoleSyncRoles() {
        return roleSyncRoles;
    }

    private Map<String, String> _getRoleSyncRoles() {
        return config.getStringMap("role-sync.roles");
    }

    public enum LinkPolicy {
        KICK,
        FREEZE,
        NONE;

        static LinkPolicy fromName(final String name) {
            for (LinkPolicy policy : values()) {
                if (policy.name().equalsIgnoreCase(name)) {
                    return policy;
                }
            }
            return LinkPolicy.NONE;
        }
    }

    @Override
    public void reloadConfig() {
        config.load();

        linkPolicy = LinkPolicy.fromName(config.getString("link-policy", "none"));
        roleSyncGroups = _getRoleSyncGroups();
        roleSyncRoles = _getRoleSyncRoles();

        plugin.onReload();
    }
}
