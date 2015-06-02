package com.earth2me.essentials.perm;

import org.bukkit.entity.Player;

public class SimplyPermsHandler extends AbstractVaultHandler {
    @Override
    public boolean canBuild(Player base, String group) {
        return hasPermission(base, "permissions.allow.build");
    }
}
