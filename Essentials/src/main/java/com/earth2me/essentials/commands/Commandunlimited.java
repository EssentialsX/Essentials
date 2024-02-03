package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.craftbukkit.Inventories;
import com.earth2me.essentials.utils.CommonPlaceholders;
import net.ess3.api.TranslatableException;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.StringJoiner;

public class Commandunlimited extends EssentialsCommand {
    public Commandunlimited() {
        super("unlimited");
    }

    @Override
    public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length == 0) {
            throw new NotEnoughArgumentsException();
        }

        User target = user;
        if (args.length > 1 && user.isAuthorized("essentials.unlimited.others")) {
            target = getPlayer(server, user, args, 1);
        }

        if (args[0].equalsIgnoreCase("list")) {
            user.sendMessage(getList(user, target));
        } else if (args[0].equalsIgnoreCase("clear")) {
            for (final Material m : new HashSet<>(target.getUnlimited())) {
                if (m == null) {
                    continue;
                }
                toggleUnlimited(user, target, m.toString());
            }
        } else {
            toggleUnlimited(user, target, args[0]);
        }
    }

    private String getList(final User sendTo, final User target) {
        final StringBuilder output = new StringBuilder();
        output.append(sendTo.playerTl("unlimitedItems")).append(" ");
        final Set<Material> items = target.getUnlimited();
        if (items.isEmpty()) {
            output.append(sendTo.playerTl("none"));
        }
        final StringJoiner joiner = new StringJoiner(", ");
        for (final Material material : items) {
            if (material == null) {
                continue;
            }
            joiner.add(material.toString().toLowerCase(Locale.ENGLISH).replace("_", ""));
        }
        output.append(joiner.toString());

        return output.toString();
    }

    private void toggleUnlimited(final User user, final User target, final String item) throws Exception {
        final ItemStack stack = ess.getItemDb().get(item, 1);
        stack.setAmount(Math.min(stack.getType().getMaxStackSize(), 2));

        final String itemname = stack.getType().toString().toLowerCase(Locale.ENGLISH).replace("_", "");
        if (ess.getSettings().permissionBasedItemSpawn() && !user.isAuthorized("essentials.unlimited.item-all") && !user.isAuthorized("essentials.unlimited.item-" + itemname) && !((stack.getType() == Material.WATER_BUCKET || stack.getType() == Material.LAVA_BUCKET) && user.isAuthorized("essentials.unlimited.item-bucket"))) {
            throw new TranslatableException("unlimitedItemPermission", itemname);
        }

        String message = "disableUnlimited";
        boolean enableUnlimited = false;
        if (!target.hasUnlimited(stack)) {
            message = "enableUnlimited";
            enableUnlimited = true;
            if (!Inventories.containsAtLeast(target.getBase(), stack, stack.getAmount())) {
                Inventories.addItem(target.getBase(), stack);
            }
        }

        if (user != target) {
            user.sendTl(message, itemname, CommonPlaceholders.displayName(target));
        }
        target.sendTl(message, itemname, CommonPlaceholders.displayName(target));
        target.setUnlimited(stack, enableUnlimited);
    }
}
