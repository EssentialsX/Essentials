package com.earth2me.essentials.perm.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PrivilegesHandler extends AbstractVaultHandler {
    @Override
    public boolean canBuild(Player base, String group) {
        return hasPermission(base, "privileges.build");
    }

    @Override
    public boolean tryProvider() {
        return super.canLoad() && Bukkit.getPluginManager().getPlugin("Privileges") != null;
    }
}
