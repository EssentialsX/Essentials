package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.NumberUtil;
import org.bukkit.Material;
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
        final ItemStack stack = user.getItemInHand();
        if (stack == null || stack.getType() == Material.AIR) {
            throw new Exception(tl("cantSpawnItem", "Air"));
        }

        final boolean canOversized = user.isAuthorized("essentials.oversizedstacks");
        if (stack.getAmount() >= (canOversized ? ess.getSettings().getOversizedStackSize() : stack.getMaxStackSize())) {
            throw new Exception(tl("fullStack"));
        }

        final String itemname = stack.getType().toString().toLowerCase(Locale.ENGLISH).replace("_", "");
        if (!user.canSpawnItem(stack.getType())) {
            throw new Exception(tl("cantSpawnItem", itemname));
        }

        int newStackSize = stack.getAmount();
        if (args.length >= 1) {
            if (!NumberUtil.isPositiveInt(args[0])) {
                throw new Exception(tl("nonZeroPosNumber"));
            }
            newStackSize += Integer.parseInt(args[0]);

            if (newStackSize > (canOversized ? ess.getSettings().getOversizedStackSize() : stack.getMaxStackSize())) {
                user.sendMessage(tl(canOversized ? "fullStackDefaultOversize" : "fullStackDefault", canOversized ? ess.getSettings().getOversizedStackSize() : stack.getMaxStackSize()));
                newStackSize = canOversized ? ess.getSettings().getOversizedStackSize() : stack.getMaxStackSize();
            }
        } else if (canOversized) {
            newStackSize = ess.getSettings().getOversizedStackSize();
        } else {
            newStackSize = stack.getMaxStackSize();
        }
        stack.setAmount(newStackSize);
        user.getBase().updateInventory();
    }
}
