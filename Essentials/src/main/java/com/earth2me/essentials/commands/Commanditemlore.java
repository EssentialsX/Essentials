package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.FormatUtil;
import com.earth2me.essentials.utils.NumberUtil;
import com.google.common.collect.Lists;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.earth2me.essentials.I18n.tl;

public class Commanditemlore extends EssentialsCommand {

    public Commanditemlore() {
        super("itemlore");
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final ItemStack item = user.getBase().getItemInHand();
        if (item.getType().name().contains("AIR")) {
            throw new Exception(tl("itemloreInvalidItem"));
        }

        if (args.length == 0) {
            throw new NotEnoughArgumentsException();
        }

        final ItemMeta im = item.getItemMeta();
        if (args[0].equalsIgnoreCase("add") && args.length > 1) {
            final String line = FormatUtil.formatString(user, "essentials.itemlore", getFinalArg(args, 1)).trim();
            final List<String> lore = im.hasLore() ? im.getLore() : new ArrayList<>();
            lore.add(line);
            im.setLore(lore);
            item.setItemMeta(im);
            user.sendMessage(tl("itemloreSuccess", line));
        } else if (args[0].equalsIgnoreCase("clear")) {
            im.setLore(new ArrayList<>());
            item.setItemMeta(im);
            user.sendMessage(tl("itemloreClear"));
        } else if (args[0].equalsIgnoreCase("set") && args.length > 2 && NumberUtil.isInt(args[1])) {
            if (!im.hasLore()) {
                throw new Exception(tl("itemloreNoLore"));
            }

            final int line = Integer.parseInt(args[1]);
            final String newLine = FormatUtil.formatString(user, "essentials.itemlore", getFinalArg(args, 2)).trim();
            final List<String> lore = im.getLore();
            try {
                lore.set(line - 1, newLine);
            } catch (final Exception e) {
                throw new Exception(tl("itemloreNoLine", line), e);
            }
            im.setLore(lore);
            item.setItemMeta(im);
            user.sendMessage(tl("itemloreSuccessLore", line, newLine));
        } else {
            throw new NotEnoughArgumentsException();
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final User user, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return Lists.newArrayList("add", "set", "clear");
        } else {
            return Collections.emptyList();
        }
    }
}
