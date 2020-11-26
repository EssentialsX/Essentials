package com.earth2me.essentials.commands;

import com.earth2me.essentials.Enchantments;
import com.earth2me.essentials.MetaItemStack;
import com.earth2me.essentials.User;
import com.earth2me.essentials.craftbukkit.InventoryWorkaround;
import com.earth2me.essentials.utils.StringUtil;
import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static com.earth2me.essentials.I18n.tl;

public class Commandenchant extends EssentialsCommand {
    public Commandenchant() {
        super("enchant");
    }

    //TODO: Implement charge costs: final Trade charge = new Trade("enchant-" + enchantmentName, ess);
    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final ItemStack stack = user.getItemInHand();
        if (stack == null || stack.getType() == Material.AIR) {
            throw new Exception(tl("nothingInHand"));
        }

        if (args.length == 0) {
            final Set<String> usableEnchants = new TreeSet<>();
            for (final Map.Entry<String, Enchantment> entry : Enchantments.entrySet()) {
                final String name = entry.getValue().getName().toLowerCase(Locale.ENGLISH);
                if (usableEnchants.contains(name) || (user.isAuthorized("essentials.enchantments." + name) && entry.getValue().canEnchantItem(stack))) {
                    usableEnchants.add(entry.getKey());
                }
            }
            throw new NotEnoughArgumentsException(tl("enchantments", StringUtil.joinList(usableEnchants.toArray())));
        }

        int level = 1;
        if (args.length > 1) {
            try {
                level = Integer.parseInt(args[1]);
            } catch (final NumberFormatException ex) {
                throw new NotEnoughArgumentsException();
            }
        }

        final MetaItemStack metaStack = new MetaItemStack(stack);
        final Enchantment enchantment = metaStack.getEnchantment(user, args[0]);
        metaStack.addEnchantment(user.getSource(), ess.getSettings().allowUnsafeEnchantments() && user.isAuthorized("essentials.enchantments.allowunsafe"), enchantment, level);
        InventoryWorkaround.setItemInMainHand(user.getBase(), metaStack.getItemStack());
        user.getBase().updateInventory();
        final String enchantName = enchantment.getName().toLowerCase(Locale.ENGLISH).replace('_', ' ');
        if (level == 0) {
            user.sendMessage(tl("enchantmentRemoved", enchantName));
        } else {
            user.sendMessage(tl("enchantmentApplied", enchantName));
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final User user, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(Enchantments.keySet());
        } else if (args.length == 2) {
            final Enchantment enchantment = Enchantments.getByName(args[0]);
            if (enchantment == null) {
                return Collections.emptyList();
            }
            final int min = enchantment.getStartLevel();
            final int max = enchantment.getMaxLevel();
            final List<String> options = Lists.newArrayList();
            for (int i = min; i <= max; i++) {
                options.add(Integer.toString(i));
            }
            return options;
        } else {
            return Collections.emptyList();
        }
    }
}
