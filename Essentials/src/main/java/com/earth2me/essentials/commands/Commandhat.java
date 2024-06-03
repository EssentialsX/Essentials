package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.craftbukkit.Inventories;
import com.earth2me.essentials.utils.TriState;
import com.earth2me.essentials.utils.VersionUtil;
import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collections;
import java.util.List;

public class Commandhat extends EssentialsCommand {

    // The prefix for hat prevention commands
    public static final String PERM_PREFIX = "essentials.hat.prevent-type.";

    public Commandhat() {
        super("hat");
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length == 0 || (!args[0].contains("rem") && !args[0].contains("off") && !args[0].equalsIgnoreCase("0"))) {
            final ItemStack hand = Inventories.getItemInMainHand(user.getBase());
            if (hand == null || hand.getType() == Material.AIR) {
                user.sendTl("hatFail");
                return;
            }

            final TriState wildcard = user.isAuthorizedExact(PERM_PREFIX + "*");
            final TriState material = user.isAuthorizedExact(PERM_PREFIX + hand.getType().name().toLowerCase());
            if ((wildcard == TriState.TRUE && material != TriState.FALSE) || ((wildcard != TriState.TRUE) && material == TriState.TRUE)) {
                user.sendTl("hatFail");
                return;
            }

            if (hand.getType().getMaxDurability() != 0) {
                user.sendTl("hatArmor");
                return;
            }

            final PlayerInventory inv = user.getBase().getInventory();
            final ItemStack head = inv.getHelmet();
            if (VersionUtil.getServerBukkitVersion().isHigherThan(VersionUtil.v1_9_4_R01) && head != null && head.getEnchantments().containsKey(Enchantment.BINDING_CURSE) && !user.isAuthorized("essentials.hat.ignore-binding")) {
                user.sendTl("hatCurse");
                return;
            }
            inv.setHelmet(hand);
            Inventories.setItemInMainHand(user.getBase(), head);
            user.sendTl("hatPlaced");
            return;
        }

        final PlayerInventory inv = user.getBase().getInventory();
        final ItemStack head = inv.getHelmet();
        if (head == null || head.getType() == Material.AIR) {
            user.sendTl("hatEmpty");
        } else if (VersionUtil.getServerBukkitVersion().isHigherThan(VersionUtil.v1_9_4_R01) && head.getEnchantments().containsKey(Enchantment.BINDING_CURSE) && !user.isAuthorized("essentials.hat.ignore-binding")) {
            user.sendTl("hatCurse");
        } else {
            final ItemStack air = new ItemStack(Material.AIR);
            inv.setHelmet(air);
            Inventories.addItem(user.getBase(), head);
            user.sendTl("hatRemoved");
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final User user, final String commandLabel, final String[] args) {
        if (args.length == 1) {
            return Lists.newArrayList("remove", "wear"); // "wear" isn't real
        } else {
            return Collections.emptyList();
        }
    }
}
