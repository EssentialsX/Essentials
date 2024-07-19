package com.earth2me.essentials.perm;

import com.earth2me.essentials.commands.Commandhat;
import com.earth2me.essentials.utils.MaterialUtil;
import com.google.common.collect.ImmutableMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

public final class PermissionsDefaults {

    private PermissionsDefaults() {
    }

    public static void registerAllBackDefaults() {
        for (final World world : Bukkit.getWorlds()) {
            registerBackDefaultFor(world);
        }
    }

    public static void registerBackDefaultFor(final World w) {
        final String permName = "essentials.back.into." + w.getName();

        Permission p = Bukkit.getPluginManager().getPermission(permName);
        if (p == null) {
            p = new Permission(permName,
                "Allows access to /back when the destination location is within world " + w.getName(),
                PermissionDefault.TRUE);
            Bukkit.getPluginManager().addPermission(p);
        }
    }

    public static void registerAllHatDefaults() {
        final PluginManager pluginManager = Bukkit.getPluginManager();

        final Permission hatPerm = pluginManager.getPermission(Commandhat.PERM_PREFIX + "*");
        if (hatPerm != null) {
            return;
        }

        final ImmutableMap.Builder<String, Boolean> children = ImmutableMap.builder();
        for (final Material mat : MaterialUtil.getKnownMaterials()) {
            final String matPerm = Commandhat.PERM_PREFIX + mat.name().toLowerCase();
            children.put(matPerm, true);
            pluginManager.addPermission(new Permission(matPerm, "Prevent using " + mat + " as a type of hat.", PermissionDefault.FALSE));
        }
        pluginManager.addPermission(new Permission(Commandhat.PERM_PREFIX + "*", "Prevent all types of hats", PermissionDefault.FALSE, children.build()));
    }
}
