package com.earth2me.essentials.perm.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SimplyPermsHandler extends AbstractVaultHandler {
    @Override
    public boolean canBuild(Player base, String group) {
        return hasPermission(base, "permissions.allow.build");
    }

    @Override
    public boolean tryProvider() {
        return super.canLoad() && Bukkit.getPluginManager().getPlugin("SimplyPerms") != null;
    }
}
