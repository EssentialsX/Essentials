package com.earth2me.essentials.perm.impl;

import net.ess3.api.IEssentials;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


/**
 * <p>ConfigPermissionsHandler class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class ConfigPermissionsHandler extends SuperpermsHandler {
    private final transient IEssentials ess;

    /**
     * <p>Constructor for ConfigPermissionsHandler.</p>
     *
     * @param ess a {@link org.bukkit.plugin.Plugin} object.
     */
    public ConfigPermissionsHandler(final Plugin ess) {
        this.ess = (IEssentials) ess;
    }

    /** {@inheritDoc} */
    @Override
    public boolean canBuild(final Player base, final String group) {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasPermission(final Player base, final String node) {
        final String[] cmds = node.split("\\.", 2);
        return ess.getSettings().isPlayerCommand(cmds[cmds.length - 1]) || super.hasPermission(base, node);
    }

    /** {@inheritDoc} */
    @Override
    public boolean tryProvider() {
        return true;
    }
}
