package com.earth2me.essentials.perm.impl;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Arrays;
import java.util.List;

/**
 * <p>Abstract AbstractVaultHandler class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public abstract class AbstractVaultHandler extends SuperpermsHandler {
    /** Constant <code>perms</code> */
    protected static Permission perms = null;
    /** Constant <code>chat</code> */
    protected static Chat chat = null;

    private boolean setupProviders() {
        try {
            Class.forName("net.milkbowl.vault.permission.Permission");
            Class.forName("net.milkbowl.vault.chat.Chat");
        } catch (ClassNotFoundException e) {
            return false;
        }

        RegisteredServiceProvider<Permission> permsProvider = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        perms = permsProvider.getProvider();
        RegisteredServiceProvider<Chat> chatProvider = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
        chat = chatProvider.getProvider();
        return perms != null && chat != null;
    }

    /** {@inheritDoc} */
    @Override
    public String getGroup(final Player base) {
        return perms.getPrimaryGroup(base);
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getGroups(final Player base) {
        return Arrays.asList(perms.getPlayerGroups(base));
    }

    /** {@inheritDoc} */
    @Override
    public boolean inGroup(final Player base, final String group) {
        return perms.playerInGroup(base, group);
    }

    /** {@inheritDoc} */
    @Override
    public String getPrefix(final Player base) {
        String playerPrefix = chat.getPlayerPrefix(base);
        if (playerPrefix == null) {
            String playerGroup = perms.getPrimaryGroup(base);
            return chat.getGroupPrefix(base.getWorld().getName(), playerGroup);
        } else {
            return playerPrefix;
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getSuffix(final Player base) {
        String playerSuffix = chat.getPlayerSuffix(base);
        if (playerSuffix == null) {
            String playerGroup = perms.getPrimaryGroup(base);
            return chat.getGroupSuffix(base.getWorld().getName(), playerGroup);
        } else {
            return playerSuffix;
        }
    }

    boolean canLoad() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) return false;
        try {
            return setupProviders();
        } catch (Throwable t) {
            return false;
        }
    }
}
