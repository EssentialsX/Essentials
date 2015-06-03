package com.earth2me.essentials.perm;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.dataholder.worlds.WorldsHolder;
import org.anjocaido.groupmanager.permissions.AnjoPermissionsHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public class GroupManagerHandler extends AbstractVaultHandler {
    private final transient GroupManager groupManager;

    public GroupManagerHandler() {
        groupManager = ((GroupManager) Bukkit.getPluginManager().getPlugin("GroupManager"));
    }

    @Override
    public boolean canBuild(final Player base, final String group) {
        final AnjoPermissionsHandler handler = getHandler(base);
        return handler != null && handler.canUserBuild(base.getName());
    }

    private AnjoPermissionsHandler getHandler(final Player base) {
        final WorldsHolder holder = groupManager.getWorldsHolder();
        if (holder == null) {
            return null;
        }
        try {
            return holder.getWorldPermissions(base);
        } catch (NullPointerException ex) {
            return null;
        }
    }
}
