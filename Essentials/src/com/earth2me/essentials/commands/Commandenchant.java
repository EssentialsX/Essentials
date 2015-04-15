package com.earth2me.essentials.commands;

import com.earth2me.essentials.Enchantments;
import com.earth2me.essentials.MetaItemStack;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.StringUtil;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

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
        final ItemStack stack = user.getBase().getItemInHand();
        if (stack == null || stack.getType() == Material.AIR) {
            throw new Exception(tl("nothingInHand"));
        }
        if (args.length == 0) {
            final Set<String> enchantmentslist = new TreeSet<String>();
            for (Map.Entry<String, Enchantment> entry : Enchantments.entrySet()) {
                final String enchantmentName = entry.getValue().getName().toLowerCase(Locale.ENGLISH);
                if (enchantmentslist.contains(enchantmentName) || (user.isAuthorized("essentials.enchantments." + enchantmentName) && entry.getValue().canEnchantItem(stack))) {
                    enchantmentslist.add(entry.getKey());
                    //enchantmentslist.add(enchantmentName);
                }
            }
            throw new NotEnoughArgumentsException(tl("enchantments", StringUtil.joinList(enchantmentslist.toArray())));
        }

        int level = -1;
        if (args.length > 1) {
            try {
                level = Integer.parseInt(args[1]);
            } catch (NumberFormatException ex) {
                level = -1;
            }
        }

        final boolean allowUnsafe = ess.getSettings().allowUnsafeEnchantments() && user.isAuthorized("essentials.enchantments.allowunsafe");

        final MetaItemStack metaStack = new MetaItemStack(stack);
        final Enchantment enchantment = metaStack.getEnchantment(user, args[0]);
        metaStack.addEnchantment(user.getSource(), allowUnsafe, enchantment, level);
        user.getBase().getInventory().setItemInHand(metaStack.getItemStack());

        user.getBase().updateInventory();
        final String enchantmentName = enchantment.getName().toLowerCase(Locale.ENGLISH);
        if (level == 0) {
            user.sendMessage(tl("enchantmentRemoved", enchantmentName.replace('_', ' ')));
        } else {
            user.sendMessage(tl("enchantmentApplied", enchantmentName.replace('_', ' ')));
        }
    }
}
