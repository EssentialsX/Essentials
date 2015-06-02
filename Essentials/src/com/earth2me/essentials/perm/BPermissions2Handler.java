package com.earth2me.essentials.perm;

import org.bukkit.entity.Player;


public class BPermissions2Handler extends AbstractVaultHandler {
    @Override
    public boolean canBuild(final Player base, final String group) {
        return hasPermission(base, "bPermissions.build");
    }
}
