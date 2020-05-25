package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.FormatUtil;
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
    protected void run(Server server, User user, String commandLabel, String[] args) throws Exception {
        ItemStack item = user.getBase().getItemInHand();
        if (item.getType().name().contains("AIR")) {
            user.sendMessage(tl("itemloreInvalidItem"));
            return;
        }

        if (args.length == 0) {
            throw new NotEnoughArgumentsException();
        }

        ItemMeta im = item.getItemMeta();
        if (args[0].equalsIgnoreCase("add")) {
            if (args.length == 1) {
                throw new NotEnoughArgumentsException();
            }

            String line = FormatUtil.formatString(user, "essentials.itemlore", getFinalArg(args, 1)).trim();
            List<String> lore = im.hasLore() ? im.getLore() : new ArrayList<>();
            lore.add(line);
            im.setLore(lore);
            item.setItemMeta(im);
            user.sendMessage(tl("itemloreSuccess", line));
        } else if (args[0].equalsIgnoreCase("clear")) {
            im.setLore(new ArrayList<>());
            item.setItemMeta(im);
            user.sendMessage(tl("itemloreClear"));
        } else {
            throw new NotEnoughArgumentsException();
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(Server server, User user, String commandLabel, String[] args) {
        if (args.length == 1) {
            return Lists.newArrayList("add", "clear");
        } else {
            return Collections.emptyList();
        }
    }
}
