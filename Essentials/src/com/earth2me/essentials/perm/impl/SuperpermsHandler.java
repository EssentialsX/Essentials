package com.earth2me.essentials.perm.impl;

import com.earth2me.essentials.perm.IPermissionsHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;


public class SuperpermsHandler implements IPermissionsHandler {
    @Override
    public String getGroup(final Player base) {
        return null;
    }

    @Override
    public List<String> getGroups(final Player base) {
        return null;
    }

    @Override
    public boolean canBuild(final Player base, final String group) {
        return false;
    }

    @Override
    public boolean inGroup(final Player base, final String group) {
        return hasPermission(base, "group." + group);
    }

    @Override
    public boolean hasPermission(final Player base, String node) {
        String permCheck = node;
        int index;
        while (true) {
            if (base.isPermissionSet(permCheck)) {
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
    public String getPrefix(final Player base) {
        return null;
    }

    @Override
    public String getSuffix(final Player base) {
        return null;
    }

    @Override
    public boolean tryProvider() {
        return getEnabledPermsPlugin() != null;
    }

    public String getEnabledPermsPlugin() {
        String enabledPermsPlugin = null;
        List<String> specialCasePlugins = Arrays.asList("PermissionsEx", "GroupManager",
                "SimplyPerms", "Privileges", "bPermissions", "zPermissions", "PermissionsBukkit",
                "DroxPerms", "xPerms", "LuckPerms");
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (specialCasePlugins.contains(plugin.getName())) {
                enabledPermsPlugin = plugin.getName();
                break;
            }
        }
        return enabledPermsPlugin;
    }
}
