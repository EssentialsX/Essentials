package com.earth2me.essentials.perm.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PermissionsExHandler extends AbstractVaultHandler {
    @Override
    public boolean canBuild(final Player base, final String group) {
        final PermissionUser user = PermissionsEx.getPermissionManager().getUser(base.getUniqueId());
        return user != null && user.getOptionBoolean("build", base.getWorld().getName(), false);
    }

    @Override
    public boolean tryProvider() {
        return super.canLoad() && Bukkit.getPluginManager().getPlugin("PermissionsEx") != null;
    }
}
