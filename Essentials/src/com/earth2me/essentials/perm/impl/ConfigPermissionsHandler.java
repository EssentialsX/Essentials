package com.earth2me.essentials.perm.impl;

import net.ess3.api.IEssentials;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public class ConfigPermissionsHandler extends SuperpermsHandler {
    private final transient IEssentials ess;

    public ConfigPermissionsHandler(final Plugin ess) {
        this.ess = (IEssentials) ess;
    }

    @Override
    public boolean canBuild(final Player base, final String group) {
        return true;
    }

    @Override
    public boolean hasPermission(final Player base, final String node) {
        final String[] cmds = node.split("\\.", 2);
        return ess.getSettings().isPlayerCommand(cmds[cmds.length - 1]) || super.hasPermission(base, node);
    }

    @Override
    public String getBackendName() {
        return "Essentials";
    }

    @Override
    public boolean tryProvider() {
        return true;
    }
}
