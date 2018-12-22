package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.craftbukkit.InventoryWorkaround;
import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Collections;
import java.util.List;

import static com.earth2me.essentials.I18n.tl;

public class Commandskull extends EssentialsCommand {
    public Commandskull() {
        super("skull");
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        String owner;

        if (args.length > 0 && user.isAuthorized("essentials.skull.others")) {
            if (!args[0].matches("^[A-Za-z0-9_]+$")) {
                throw new IllegalArgumentException(tl("alphaNames"));
            }
            owner = args[0];
        } else {
            owner = user.getName();
        }

        ItemStack itemSkull = user.getBase().getInventory().getItemInMainHand();
        SkullMeta metaSkull = null;
        boolean spawn = false;

        if (itemSkull != null && itemSkull.getType() == Material.LEGACY_SKULL_ITEM && itemSkull.getDurability() == 3) {
            metaSkull = (SkullMeta) itemSkull.getItemMeta();
        } else if (user.isAuthorized("essentials.skull.spawn")) {
            itemSkull = new ItemStack(Material.LEGACY_SKULL_ITEM, 1, (byte) 3);
            metaSkull = (SkullMeta) itemSkull.getItemMeta();
            spawn = true;
        } else {
            throw new Exception(tl("invalidSkull"));
        }

        if (metaSkull.hasOwner() && !user.isAuthorized("essentials.skull.modify")) {
            throw new Exception(tl("noPermissionSkull"));
        }

        metaSkull.setDisplayName("§fSkull of " + owner);
        metaSkull.setOwner(owner);

        itemSkull.setItemMeta(metaSkull);

        if (spawn) {
            InventoryWorkaround.addItems(user.getBase().getInventory(), itemSkull);
            user.sendMessage(tl("givenSkull", owner));
        } else {
            user.sendMessage(tl("skullChanged", owner));
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, User user, String commandLabel, String[] args) {
        if (args.length == 1) {
            if (user.isAuthorized("essentials.skull.others")) {
                return getPlayers(server, user);
            } else {
                return Lists.newArrayList(user.getName());
            }
        } else {
            return Collections.emptyList();
        }
    }

}