package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.StringJoiner;

import static com.earth2me.essentials.I18n.tl;

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
            user.sendMessage(getList(target));
        } else if (args[0].equalsIgnoreCase("clear")) {
            for (final Material m : new HashSet<>(target.getUnlimited())) {
                toggleUnlimited(user, target, m.toString());
            }
        } else {
            toggleUnlimited(user, target, args[0]);
        }
    }

    private String getList(final User target) {
        final StringBuilder output = new StringBuilder();
        output.append(tl("unlimitedItems")).append(" ");
        final Set<Material> items = target.getUnlimited();
        if (items.isEmpty()) {
            output.append(tl("none"));
        }
        final StringJoiner joiner = new StringJoiner(", ");
        for (final Material material : items) {
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
            throw new Exception(tl("unlimitedItemPermission", itemname));
        }

        String message = "disableUnlimited";
        boolean enableUnlimited = false;
        if (!target.hasUnlimited(stack)) {
            message = "enableUnlimited";
            enableUnlimited = true;
            if (!target.getBase().getInventory().containsAtLeast(stack, stack.getAmount())) {
                target.getBase().getInventory().addItem(stack);
            }
        }

        if (user != target) {
            user.sendMessage(tl(message, itemname, target.getDisplayName()));
        }
        target.sendMessage(tl(message, itemname, target.getDisplayName()));
        target.setUnlimited(stack, enableUnlimited);
    }
}
