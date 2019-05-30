package com.earth2me.essentials.perm;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.perm.impl.*;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class PermissionsHandler implements IPermissionsHandler {
    private transient IPermissionsHandler handler = null;
    private transient String defaultGroup = "default";
    private final transient Essentials ess;
    private transient boolean useSuperperms;

    private Class<?> lastHandler = null;

    public PermissionsHandler(final Essentials plugin, final boolean useSuperperms) {
        this.ess = plugin;
        this.useSuperperms = useSuperperms;
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
    public boolean isPermissionSet(final Player base, final String node) {
        return handler.isPermissionSet(base, node);
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

    @Override
    public boolean tryProvider() {
        return true;
    }

    public void checkPermissions() {
        // load and assign a handler
        List<Class<? extends SuperpermsHandler>> providerClazz = Arrays.asList(
                PermissionsExHandler.class,
                GenericVaultHandler.class,
                SuperpermsHandler.class
        );
        for (Class<? extends IPermissionsHandler> providerClass : providerClazz) {
            try {
                IPermissionsHandler provider = providerClass.newInstance();
                if (provider.tryProvider()) {
                    this.handler = provider;
                    break;
                }
            } catch (Throwable ignored) {
            }
        }
        if (handler == null) {
            if (useSuperperms) {
                handler = new SuperpermsHandler();
            } else {
                handler = new ConfigPermissionsHandler(ess);
            }
        }

        // don't spam logs
        Class<?> handlerClass = handler.getClass();
        if (lastHandler != null && lastHandler == handlerClass) {
            return;
        }
        lastHandler = handlerClass;

        // output handler info
        if (handler instanceof AbstractVaultHandler) {
            String enabledPermsPlugin = ((AbstractVaultHandler) handler).getEnabledPermsPlugin();
            if (enabledPermsPlugin == null) enabledPermsPlugin = "generic";
            ess.getLogger().info("Using Vault based permissions (" + enabledPermsPlugin + ")");
        } else if (handler.getClass() == SuperpermsHandler.class) {
            if (handler.tryProvider()) {
                ess.getLogger().warning("Detected supported permissions plugin " +
                        ((SuperpermsHandler) handler).getEnabledPermsPlugin() + " without Vault installed.");
                ess.getLogger().warning("Features such as chat prefixes/suffixes and group-related functionality will not " +
                        "work until you install Vault.");
            }
            ess.getLogger().info("Using superperms-based permissions.");
        } else if (handler.getClass() == ConfigPermissionsHandler.class) {
            ess.getLogger().info("Using config file enhanced permissions.");
            ess.getLogger().info("Permissions listed in as player-commands will be given to all users.");
        }
    }

    public void setUseSuperperms(final boolean useSuperperms) {
        this.useSuperperms = useSuperperms;
    }

    public String getName() {
        return handler.getClass().getSimpleName().replace("Provider", "");
    }

    private void checkPermLag(long start, String summary) {
        final long elapsed = System.nanoTime() - start;
        if (elapsed > ess.getSettings().getPermissionsLagWarning()) {
            ess.getLogger().log(Level.WARNING, String.format("Permissions lag notice with (%s). Response took %fms. Summary: %s", getName(), elapsed / 1000000.0, summary));
        }
    }

}
