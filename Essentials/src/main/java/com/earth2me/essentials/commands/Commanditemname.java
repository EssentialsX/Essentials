package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.FormatUtil;
import com.earth2me.essentials.utils.MaterialUtil;
import com.earth2me.essentials.utils.TriState;
import com.google.common.collect.Lists;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

import static com.earth2me.essentials.I18n.tl;

public class Commanditemname extends EssentialsCommand {
    public static final String PERM_PREFIX = "essentials.itemname.prevent-type.";

    public Commanditemname() {
        super("itemname");
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final ItemStack item = user.getBase().getItemInHand();
        if (MaterialUtil.isAir(item.getType())) {
            user.sendMessage(tl("itemnameInvalidItem"));
            return;
        }

        final TriState wildcard = user.isAuthorizedExact(PERM_PREFIX + "*");
        final TriState material = user.isAuthorizedExact(PERM_PREFIX + item.getType().name().toLowerCase());
        if ((wildcard == TriState.TRUE && material != TriState.FALSE) || ((wildcard != TriState.TRUE) && material == TriState.TRUE)) {
            user.sendMessage(tl("itemnameInvalidItem"));
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

    @Override
    protected List<String> getTabCompleteOptions(Server server, User user, String commandLabel, String[] args) {
        if (args.length == 1) {
            final ItemStack item = user.getBase().getItemInHand();
            if (!MaterialUtil.isAir(item.getType()) && item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                return Lists.newArrayList(FormatUtil.unformatString(user, "essentials.itemname", item.getItemMeta().getDisplayName()));
            }
        }
        return Collections.emptyList();
    }
}
