package com.earth2me.essentials.perm.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.bukkit.entity.Player;

public class ModernVaultHandler extends AbstractVaultHandler {
    private final List<String> supportedPlugins = Arrays.asList("PermissionsEx", "LuckPerms");

    @Override
    protected boolean emulateWildcards() {
        return false;
    }

    @Override
    public boolean canBuild(final Player base, final String group) {
        Objects.requireNonNull(base, "Can't check build override for nonexistent player!");
        return super.canBuild(base, group) || chat.getPlayerInfoBoolean(base.getWorld().getName(), base, "build", false);
    }

    @Override
    public boolean tryProvider() {
        return super.canLoad() && supportedPlugins.contains(getEnabledPermsPlugin());
    }
}
