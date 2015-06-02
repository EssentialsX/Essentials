package com.earth2me.essentials.perm;

import com.earth2me.essentials.Essentials;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PermissionsHandler implements IPermissionsHandler {
    private transient IPermissionsHandler handler = new NullPermissionsHandler();
    private transient String defaultGroup = "default";
    private final transient Essentials ess;
    private transient boolean useSuperperms = false;

    public PermissionsHandler(final Essentials plugin) {
        this.ess = plugin;
    }

    public PermissionsHandler(final Essentials plugin, final boolean useSuperperms) {
        this.ess = plugin;
        this.useSuperperms = useSuperperms;
    }

    public PermissionsHandler(final Essentials plugin, final String defaultGroup) {
        this.ess = plugin;
        this.defaultGroup = defaultGroup;
    }

    @Override
    public String getGroup(final Player base) {
        final long start = System.nanoTime();
        String group = handler.getGroup(base);
        if (group == null) {
            group = defaultGroup;
        }
        checkPermLag(start, String.format("Getting group for %s", base.getName()));
        return group;
    }

    @Override
    public List<String> getGroups(final Player base) {
        final long start = System.nanoTime();
        List<String> groups = handler.getGroups(base);
        if (groups == null || groups.isEmpty()) {
            groups = Collections.singletonList(defaultGroup);
        }
        checkPermLag(start, String.format("Getting groups for %s", base.getName()));
        return Collections.unmodifiableList(groups);
    }

    @Override
    public boolean canBuild(final Player base, final String group) {
        return handler.canBuild(base, group);
    }

    @Override
    public boolean inGroup(final Player base, final String group) {
        final long start = System.nanoTime();
        final boolean result = handler.inGroup(base, group);
        checkPermLag(start, String.format("Checking if %s is in group %s", base.getName(), group));
        return result;
    }

    @Override
    public boolean hasPermission(final Player base, final String node) {
        return handler.hasPermission(base, node);
    }

    @Override
    public String getPrefix(final Player base) {
        final long start = System.nanoTime();
        String prefix = handler.getPrefix(base);
        if (prefix == null) {
            prefix = "";
        }
        checkPermLag(start, String.format("Getting prefix for %s", base.getName()));
        return prefix;
    }

    @Override
    public String getSuffix(final Player base) {
        final long start = System.nanoTime();
        String suffix = handler.getSuffix(base);
        if (suffix == null) {
            suffix = "";
        }
        checkPermLag(start, String.format("Getting suffix for %s", base.getName()));
        return suffix;
    }

    public void checkPermissions() {
        final PluginManager pluginManager = ess.getServer().getPluginManager();
        final Plugin vaultAPI = pluginManager.getPlugin("Vault");
        if (vaultAPI != null && vaultAPI.isEnabled()) {
            final Plugin permExPlugin = pluginManager.getPlugin("PermissionsEx");
            if (permExPlugin != null && permExPlugin.isEnabled()) {
                if (!(handler instanceof PermissionsExHandler)) {
                    ess.getLogger().info("Using PermissionsEX based permissions with Vault.");
                    handler = new PermissionsExHandler();
                }
                return;
            }

            final Plugin GMplugin = pluginManager.getPlugin("GroupManager");
            if (GMplugin != null && GMplugin.isEnabled()) {
                if (!(handler instanceof GroupManagerHandler)) {
                    ess.getLogger().info("Using GroupManager based permissions with Vault.");
                    handler = new GroupManagerHandler(GMplugin);
                }
                return;
            }

            final Plugin simplyPermsPlugin = pluginManager.getPlugin("SimplyPerms");
            if (simplyPermsPlugin != null && simplyPermsPlugin.isEnabled()) {
                if (!(handler instanceof SimplyPermsHandler)) {
                    ess.getLogger().info("Using SimplyPerms based permissions with Vault.");
                    handler = new SimplyPermsHandler();
                }
                return;
            }

            final Plugin privPlugin = pluginManager.getPlugin("Privileges");
            if (privPlugin != null && privPlugin.isEnabled()) {
                if (!(handler instanceof PrivilegesHandler)) {
                    ess.getLogger().info("Using Privileges based permissions with Vault.");
                    handler = new PrivilegesHandler();
                }
                return;
            }

            final Plugin bPermPlugin = pluginManager.getPlugin("bPermissions");
            if (bPermPlugin != null && bPermPlugin.isEnabled()) {
                if (!(handler instanceof BPermissions2Handler)) {
                    ess.getLogger().info("Using bPermissions based permissions with Vault.");
                    handler = new BPermissions2Handler();
                }
                return;
            }

            if (!(handler instanceof GenericVaultHandler)) {
                AbstractVaultHandler vault = new GenericVaultHandler();
                if (vault.setupProviders()) {
                    ess.getLogger().info("Using generic Vault based permissions.");
                    handler = vault;
                }
            }
            return;
        }

        if (useSuperperms) {
            if (!(handler instanceof SuperpermsHandler)) {
                ess.getLogger().info("Using superperms based permissions.");
                handler = new SuperpermsHandler();
            }
        } else {
            if (!(handler instanceof ConfigPermissionsHandler)) {
                ess.getLogger().info("Essentials: Using config file enhanced permissions.");
                ess.getLogger().info("Permissions listed in as player-commands will be given to all users.");
                handler = new ConfigPermissionsHandler(ess);
            }
        }
    }

    public void setUseSuperperms(final boolean useSuperperms) {
        this.useSuperperms = useSuperperms;
    }

    public String getName() {
        return handler.getClass().getSimpleName().replace("Handler", "");
    }

    private void checkPermLag(long start, String summary) {
        final long elapsed = System.nanoTime() - start;
        if (elapsed > ess.getSettings().getPermissionsLagWarning()) {
            ess.getLogger().log(Level.WARNING, String.format("Permissions lag notice with (%s). Response took %fms. Summary: %s", getName(), elapsed / 1000000.0, summary));
        }
    }

    private void checkPermLag(long start) {
        checkPermLag(start, "not defined");
    }
}
