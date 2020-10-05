package com.earth2me.essentials.perm.impl;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractVaultHandler extends SuperpermsHandler {
    protected static Permission perms = null;
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

    @Override
    public String getGroup(final Player base) {
        return perms.getPrimaryGroup(base);
    }

    @Override
    public List<String> getGroups(final Player base) {
        return Arrays.asList(perms.getPlayerGroups(base));
    }

    @Override
    public boolean inGroup(final Player base, final String group) {
        return perms.playerInGroup(base, group);
    }

    @Override
    public String getPrefix(final Player base) {
        String playerPrefix = chat.getPlayerPrefix(base);
        if (playerPrefix != null) {
            return playerPrefix;
        }

        String playerGroup = perms.getPrimaryGroup(base);
        if (playerGroup != null) {
            return chat.getGroupPrefix(base.getWorld().getName(), playerGroup);
        }

        return null;
    }

    @Override
    public String getSuffix(final Player base) {
        String playerSuffix = chat.getPlayerSuffix(base);
        if (playerSuffix != null) {
            return playerSuffix;
        }

        String playerGroup = perms.getPrimaryGroup(base);
        if (playerGroup != null) {
            return chat.getGroupSuffix(base.getWorld().getName(), playerGroup);
        }

        return null;
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
