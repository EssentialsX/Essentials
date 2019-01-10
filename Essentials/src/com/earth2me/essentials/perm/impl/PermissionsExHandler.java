package com.earth2me.essentials.perm.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PermissionsExHandler extends AbstractVaultHandler {
    @Override
    public boolean canBuild(final Player base, final String group) {
        return base != null && chat.getPlayerInfoBoolean(base.getWorld().getName(), base, "build", false);
    }

    @Override
    public boolean tryProvider() {
        return super.canLoad() && Bukkit.getPluginManager().getPlugin("PermissionsEx") != null;
    }
}
