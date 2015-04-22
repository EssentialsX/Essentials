package com.earth2me.essentials.perm;

import com.earth2me.essentials.Essentials;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Arrays;
import java.util.List;

public class VaultHandler extends SuperpermsHandler {

    private Essentials plugin;
    private static Permission perms = null;

    public VaultHandler(Essentials plugin) {
        this.plugin = plugin;
    }

    public boolean setupPermissions() {
        try {
            Class.forName("net.milkbowl.vault.permission.Permission");
        } catch (ClassNotFoundException e) {
            return false;
        }

        RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
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
}
