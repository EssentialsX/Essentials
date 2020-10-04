package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.FormatUtil;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Locale;

import static com.earth2me.essentials.I18n.tl;

public class Commanditemname extends EssentialsCommand {

    public Commanditemname() {
        super("itemname");
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final ItemStack item = user.getBase().getItemInHand();
        if (item.getType() == Material.AIR) {
            user.sendMessage(tl("itemnameInvalidItem", item.getType().toString().toLowerCase(Locale.ENGLISH).replace('_', ' ')));
            return;
        }

        String name = FormatUtil.formatString(user, "essentials.itemname", getFinalArg(args, 0)).trim();
        if (name.isEmpty()) {
            name = null;
        }

        final ItemMeta im = item.getItemMeta();
        im.setDisplayName(name);
        item.setItemMeta(im);
        user.sendMessage(name == null ? tl("itemnameClear") : tl("itemnameSuccess", name));
    }
}
