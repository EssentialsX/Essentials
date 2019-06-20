package com.earth2me.essentials.perm;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.perm.impl.*;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

/**
 * <p>PermissionsHandler class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class PermissionsHandler implements IPermissionsHandler {
    private transient IPermissionsHandler handler = null;
    private transient String defaultGroup = "default";
    private final transient Essentials ess;
    private transient boolean useSuperperms;

    private Class<?> lastHandler = null;

    /**
     * <p>Constructor for PermissionsHandler.</p>
     *
     * @param plugin a {@link com.earth2me.essentials.Essentials} object.
     * @param useSuperperms a boolean.
     */
    public PermissionsHandler(final Essentials plugin, final boolean useSuperperms) {
        this.ess = plugin;
        this.useSuperperms = useSuperperms;
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    public boolean canBuild(final Player base, final String group) {
        return handler.canBuild(base, group);
    }

    /** {@inheritDoc} */
    @Override
    public boolean inGroup(final Player base, final String group) {
        final long start = System.nanoTime();
        final boolean result = handler.inGroup(base, group);
        checkPermLag(start, String.format("Checking if %s is in group %s", base.getName(), group));
        return result;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasPermission(final Player base, final String node) {
        return handler.hasPermission(base, node);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isPermissionSet(final Player base, final String node) {
        return handler.isPermissionSet(base, node);
    }

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
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

    /** {@inheritDoc} */
    @Override
    public boolean tryProvider() {
        return true;
    }

    /**
     * <p>checkPermissions.</p>
     */
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

    /**
     * <p>Setter for the field <code>useSuperperms</code>.</p>
     *
     * @param useSuperperms a boolean.
     */
    public void setUseSuperperms(final boolean useSuperperms) {
        this.useSuperperms = useSuperperms;
    }

    /**
     * <p>getName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
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
