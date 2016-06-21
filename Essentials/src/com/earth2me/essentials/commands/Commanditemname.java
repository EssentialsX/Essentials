package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n.tl;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.I18n;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.FormatUtil;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Locale;

public class Commanditemname extends EssentialsCommand {
    
    public Commanditemname() {
        super("itemname");
    }

    @Override
    protected void run(Server server, User user, String commandLabel, String[] args) throws Exception {
        ItemStack item = user.getBase().getItemInHand();
        if (item.getType() != Material.AIR) {
            String name = FormatUtil.formatString(user, "essentials.itemname", getFinalArg(args, 0));
            ItemMeta im = item.getItemMeta();
            im.setDisplayName(name);
            item.setItemMeta(im);
            user.sendMessage(tl("itemnameSuccess", name));
            return;
        }
        user.sendMessage(tl("itemnameInvalidItem", item.getType().toString().toLowerCase(Locale.ENGLISH).replace('_', ' ')));
    }
}
