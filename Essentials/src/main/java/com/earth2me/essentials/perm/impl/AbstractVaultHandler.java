package com.earth2me.essentials.perm.impl;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractVaultHandler extends SuperpermsHandler {
    protected static Permission perms = null;
    protected static Chat chat = null;

    private boolean setupProviders() {
        try {
            Class.forName("net.milkbowl.vault.permission.Permission");
            Class.forName("net.milkbowl.vault.chat.Chat");
        } catch (final ClassNotFoundException e) {
            return false;
        }

        final RegisteredServiceProvider<Permission> permsProvider = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        final RegisteredServiceProvider<Chat> chatProvider = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
        if (permsProvider == null || chatProvider == null) return false;

        perms = permsProvider.getProvider();
        chat = chatProvider.getProvider();
        return perms != null && chat != null;
    }

    @Override
    public String getGroup(final OfflinePlayer base) {
        if (base.isOnline()) {
            return perms.getPrimaryGroup(base.getPlayer());
        }
        return perms.getPrimaryGroup(null, base);
    }

    @Override
    public List<String> getGroups(final OfflinePlayer base) {
        if (base.isOnline()) {
            return Arrays.asList(perms.getPlayerGroups(base.getPlayer()));
        }
        return Arrays.asList(perms.getPlayerGroups(null, base));
    }

    @Override
    public List<String> getGroups() {
        return Arrays.asList(perms.getGroups());
    }

    @Override
    public boolean addToGroup(OfflinePlayer base, String group) {
        return perms.playerAddGroup(null, base, group);
    }

    @Override
    public boolean removeFromGroup(OfflinePlayer base, String group) {
        return perms.playerRemoveGroup(null, base, group);
    }

    @Override
    public boolean inGroup(final Player base, final String group) {
        return perms.playerInGroup(base, group);
    }

    @Override
    public String getPrefix(final Player base) {
        final String playerPrefix = chat.getPlayerPrefix(base);
        if (playerPrefix != null) {
            return playerPrefix;
        }

        final String playerGroup = perms.getPrimaryGroup(base);
        if (playerGroup != null) {
            return chat.getGroupPrefix(base.getWorld().getName(), playerGroup);
        }

        return null;
    }

    @Override
    public String getSuffix(final Player base) {
        final String playerSuffix = chat.getPlayerSuffix(base);
        if (playerSuffix != null) {
            return playerSuffix;
        }

        final String playerGroup = perms.getPrimaryGroup(base);
        if (playerGroup != null) {
            return chat.getGroupSuffix(base.getWorld().getName(), playerGroup);
        }

        return null;
    }

    @Override
    public String getBackendName() {
        final String reportedPlugin = perms.getName();
        final String classPlugin = JavaPlugin.getProvidingPlugin(perms.getClass()).getName();

        if (reportedPlugin.equals(classPlugin)) {
            return reportedPlugin;
        }
        return reportedPlugin + " (" + classPlugin + ")";
    }

    boolean canLoad() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) return false;
        try {
            return setupProviders();
        } catch (final Throwable t) {
            return false;
        }
    }
}
