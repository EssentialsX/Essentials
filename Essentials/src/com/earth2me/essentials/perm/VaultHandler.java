package com.earth2me.essentials.perm;

import com.earth2me.essentials.Essentials;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Arrays;
import java.util.List;

public class VaultHandler extends SuperpermsHandler {

    private Essentials plugin;
    private static Permission perms = null;
    private static Chat chat = null;

    public VaultHandler(Essentials plugin) {
        this.plugin = plugin;
    }

    public boolean setupProviders() {
        try {
            Class.forName("net.milkbowl.vault.permission.Permission");
            Class.forName("net.milkbowl.vault.chat.Chat");
        } catch (ClassNotFoundException e) {
            return false;
        }

        RegisteredServiceProvider<Permission> permsProvider = plugin.getServer().getServicesManager().getRegistration(Permission.class);
        perms = permsProvider.getProvider();
        RegisteredServiceProvider<Chat> chatProvider = plugin.getServer().getServicesManager().getRegistration(Chat.class);
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
    public boolean hasPermission(final Player base, String node) {
        return base.hasPermission(node);
    }

    @Override
    public String getPrefix(final Player base) {
        String playerPrefix = chat.getPlayerPrefix(base);
        if (playerPrefix == null) {
            String playerGroup = perms.getPrimaryGroup(base);
            return chat.getGroupPrefix((String) null, playerGroup);
        } else {
            return playerPrefix;
        }
    }

    @Override
    public String getSuffix(final Player base) {
        String playerSuffix = chat.getPlayerSuffix(base);
        if (playerSuffix == null) {
            String playerGroup = perms.getPrimaryGroup(base);
            return chat.getGroupSuffix((String) null, playerGroup);
        } else {
            return playerSuffix;
        }
    }
}
