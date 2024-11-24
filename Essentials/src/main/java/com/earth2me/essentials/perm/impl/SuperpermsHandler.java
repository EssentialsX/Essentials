package com.earth2me.essentials.perm.impl;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.perm.IPermissionsHandler;
import com.earth2me.essentials.utils.TriState;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class SuperpermsHandler implements IPermissionsHandler {
    protected boolean emulateWildcards() {
        return true;
    }

    @Override
    public boolean addToGroup(OfflinePlayer base, String group) {
        return false;
    }

    @Override
    public boolean removeFromGroup(OfflinePlayer base, String group) {
        return false;
    }

    @Override
    public String getGroup(final OfflinePlayer base) {
        return null;
    }

    @Override
    public List<String> getGroups(final OfflinePlayer base) {
        return null;
    }

    @Override
    public List<String> getGroups() {
        return null;
    }

    @Override
    public boolean canBuild(final Player base, final String group) {
        return hasPermission(base, "essentials.build");
    }

    @Override
    public boolean inGroup(final Player base, final String group) {
        return hasPermission(base, "group." + group);
    }

    /**
     * Return whether a permission is registered and denied to ops.
     *
     * <p>The default permission value is {@link org.bukkit.permissions.PermissionDefault#OP}, so for a permission
     * to be denied to ops it has to be both explicitly registered and have its default evaluate to false for an operator</p>
     *
     * @param node node to check
     * @return whether an op would, in absense of other permissions being set, have
     */
    private boolean isDeniedToOps(final String node) {
        final Permission perm = Bukkit.getServer().getPluginManager().getPermission(node);
        return perm != null && !perm.getDefault().getValue(true);
    }

    /**
     * Perform a permissions check on {@code base}.
     *
     * <p>Unless {@link #emulateWildcards()} is overridden to disable wildcard emulation,
     * wildcard assignments will be checked for permissions. This has a few subtleties in order
     * to respect default-false assignments. {@link org.bukkit.permissions.Permissible#isPermissionSet(String)}
     * will only return true for permissions that are set on an attachment, or that are a default that evaluates to true.
     * When resolving wildcards, we also want to detect permissions that are not in an attachment, but also won't evaluate
     * to true for operators &mdash; since these are ones we've explicitly set to {@code false} in the {@code plugin.yml}</p>
     *
     * <p>For the resolution itself, we check whether the permission is either set on the permissible or explicitly not
     * granted to ops (i.e. deviating from the default). If so, the permission's value is returned. Otherwise, the portion
     * of the permission from the beginning to the last occurrence of {@code .} followed by a {@code *} is taken and the process is repeated.</p>
     *
     * <p>Once a string without dots has been checked, if no result has been found the literal permission {@code *} is
     * checked and the result of that check is returned.</p>
     *
     * @param base Player to check permissions on
     * @param node permission to check
     * @return calculated value
     */
    @Override
    public boolean hasPermission(final Player base, String node) {
        if (!emulateWildcards()) {
            return base.hasPermission(node);
        }

        String permCheck = node;
        int index;
        while (true) {
            // Either explicitly set on the subject, or globally set to not be automatically granted
            // by declaring in `plugin.yml` with a `default: false` value (as opposed to the default default value of OP)
            if (base.isPermissionSet(permCheck) || isDeniedToOps(node)) {
                return base.hasPermission(permCheck);
            }

            index = node.lastIndexOf('.');
            if (index < 1) {
                return base.hasPermission("*");
            }

            node = node.substring(0, index);
            permCheck = node + ".*";
        }
    }

    @Override
    public boolean isPermissionSet(final Player base, final String node) {
        return base.isPermissionSet(node);
    }

    @Override
    public TriState isPermissionSetExact(Player base, String node) {
        for (final PermissionAttachmentInfo perm : base.getEffectivePermissions()) {
            if (perm.getPermission().equalsIgnoreCase(node)) {
                return perm.getValue() ? TriState.TRUE : TriState.FALSE;
            }
        }
        return TriState.UNSET;
    }

    @Override
    public String getPrefix(final Player base) {
        return null;
    }

    @Override
    public String getSuffix(final Player base) {
        return null;
    }

    @Override
    public void registerContext(final String context, final Function<User, Iterable<String>> calculator, final Supplier<Iterable<String>> suggestions) {
    }

    @Override
    public void unregisterContexts() {
    }

    @Override
    public String getBackendName() {
        return getEnabledPermsPlugin();
    }

    @Override
    public boolean tryProvider(Essentials ess) {
        return getEnabledPermsPlugin() != null;
    }

    public String getEnabledPermsPlugin() {
        String enabledPermsPlugin = null;
        final List<String> specialCasePlugins = Arrays.asList("PermissionsEx", "GroupManager",
            "SimplyPerms", "Privileges", "bPermissions", "zPermissions", "PermissionsBukkit",
            "DroxPerms", "xPerms", "LuckPerms");
        for (final Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (specialCasePlugins.contains(plugin.getName())) {
                enabledPermsPlugin = plugin.getName();
                break;
            }
        }
        return enabledPermsPlugin;
    }
}
