package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

import static com.earth2me.essentials.I18n.tl;


public class Commandmore extends EssentialsCommand {
    public Commandmore() {
        super("more");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final ItemStack stack = user.getBase().getItemInHand();
        if (stack == null) {
            throw new Exception(tl("cantSpawnItem", "Air"));
        }
        if (stack.getAmount() >= ((user.isAuthorized("essentials.oversizedstacks")) ? ess.getSettings().getOversizedStackSize() : stack.getMaxStackSize())) {
            throw new Exception(tl("fullStack"));
        }
        final String itemname = stack.getType().toString().toLowerCase(Locale.ENGLISH).replace("_", "");
        if (!user.canSpawnItem(stack.getType())) {
            throw new Exception(tl("cantSpawnItem", itemname));
        }
        if (args.length > 1) {
            int newAmount = stack.getAmount();

            try {
                newAmount += Integer.parseInt(ChatColor.stripColor(args[1]));
            } catch(Exception e) {
                throw new Exception(tl("numberRequired"));
            }

            if (!user.isAuthorized("essentials.oversizedstacks")) {
                newAmount = stack.getMaxStackSize();
            } else {
                if (newAmount > ess.getSettings().getOversizedStackSize()) {
                    newAmount = ess.getSettings.getOversizedStackSize();
                }
            }

            stack.setAmount(newAmount);
        } else {
            if (user.isAuthorized("essentials.oversizedstacks")) {
                stack.setAmount(ess.getSettings().getOversizedStackSize());
            } else {
                stack.setAmount(stack.getMaxStackSize());
            }
        }
        user.getBase().updateInventory();
    }
}
