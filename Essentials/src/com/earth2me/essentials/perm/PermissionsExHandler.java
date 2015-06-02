package com.earth2me.essentials.perm;

import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PermissionsExHandler extends AbstractVaultHandler {
    private final transient PermissionManager manager;

    public PermissionsExHandler() {
        manager = PermissionsEx.getPermissionManager();
    }

    @Override
    public boolean canBuild(final Player base, final String group) {
        final PermissionUser user = manager.getUser(base.getName());
        return user != null && user.getOptionBoolean("build", base.getWorld().getName(), false);
    }
}
