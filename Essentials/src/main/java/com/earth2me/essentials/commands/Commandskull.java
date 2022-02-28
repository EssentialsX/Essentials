package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.craftbukkit.InventoryWorkaround;
import com.earth2me.essentials.utils.EnumUtil;
import com.earth2me.essentials.utils.MaterialUtil;
import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static com.earth2me.essentials.I18n.tl;

public class Commandskull extends EssentialsCommand {

    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]+$");
    private static final Material SKULL_ITEM = EnumUtil.getMaterial("PLAYER_HEAD", "SKULL_ITEM");

    public Commandskull() {
        super("skull");
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final String owner;
        if (args.length > 0 && user.isAuthorized("essentials.skull.others")) {
            if (!NAME_PATTERN.matcher(args[0]).matches()) {
                throw new IllegalArgumentException(tl("alphaNames"));
            }
            owner = args[0];
        } else {
            owner = user.getName();
        }

        ItemStack itemSkull = user.getItemInHand();
        final SkullMeta metaSkull;
        boolean spawn = false;

        if (itemSkull != null && MaterialUtil.isPlayerHead(itemSkull)) {
            metaSkull = (SkullMeta) itemSkull.getItemMeta();
        } else if (user.isAuthorized("essentials.skull.spawn")) {
            itemSkull = new ItemStack(SKULL_ITEM, 1, (byte) 3);
            metaSkull = (SkullMeta) itemSkull.getItemMeta();
            spawn = true;
        } else {
            throw new Exception(tl("invalidSkull"));
        }

        if (metaSkull.hasOwner() && !user.isAuthorized("essentials.skull.modify")) {
            throw new Exception(tl("noPermissionSkull"));
        }

        editSkull(user, itemSkull, metaSkull, owner, spawn);
    }

    private void editSkull(final User user, final ItemStack stack, final SkullMeta skullMeta, final String owner, final boolean spawn) {
        new BukkitRunnable() {
            @Override
            public void run() {
                //Run this stuff async because SkullMeta#setOwner causes a http request.
                skullMeta.setDisplayName("§fSkull of " + owner);
                skullMeta.setOwner(owner);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        stack.setItemMeta(skullMeta);
                        if (spawn) {
                            InventoryWorkaround.addItems(user.getBase().getInventory(), stack);
                            user.sendMessage(tl("givenSkull", owner));
                            return;
                        }
                        user.sendMessage(tl("skullChanged", owner));
                    }
                }.runTask(ess);
            }
        }.runTaskAsynchronously(ess);
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final User user, final String commandLabel, final String[] args) {
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
