package com.earth2me.essentials.commands;

import com.earth2me.essentials.MetaItemStack;
import com.earth2me.essentials.Potions;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.StringUtil;
import com.google.common.collect.Lists;
import net.ess3.nms.refl.ReflUtil;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static com.earth2me.essentials.I18n.tl;

public class Commandpotion extends EssentialsCommand {
    public Commandpotion() {
        super("potion");
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        final ItemStack stack = user.getItemInHand();
        if (args.length == 0) {
            final Set<String> potionslist = new TreeSet<>();
            for (final Map.Entry<String, PotionEffectType> entry : Potions.entrySet()) {
                final String potionName = entry.getValue().getName().toLowerCase(Locale.ENGLISH);
                if (potionslist.contains(potionName) || user.isAuthorized("essentials.potion." + potionName)) {
                    potionslist.add(entry.getKey());
                }
            }
            throw new NotEnoughArgumentsException(tl("potions", StringUtil.joinList(potionslist.toArray())));
        }

        boolean holdingPotion = stack.getType() == Material.POTION;
        if (!holdingPotion && ReflUtil.getNmsVersionObject().isHigherThanOrEqualTo(ReflUtil.V1_9_R1)) {
            holdingPotion = stack.getType() == Material.SPLASH_POTION || stack.getType() == Material.LINGERING_POTION;
        }
        if (holdingPotion) {
            PotionMeta pmeta = (PotionMeta) stack.getItemMeta();
            if (args[0].equalsIgnoreCase("clear")) {
                pmeta.clearCustomEffects();
                stack.setItemMeta(pmeta);
            } else if (args[0].equalsIgnoreCase("apply") && user.isAuthorized("essentials.potion.apply")) {
                for (final PotionEffect effect : pmeta.getCustomEffects()) {
                    effect.apply(user.getBase());
                }
            } else if (args.length < 3) {
                throw new NotEnoughArgumentsException();
            } else {
                final MetaItemStack mStack = new MetaItemStack(stack);
                for (final String arg : args) {
                    mStack.addPotionMeta(user.getSource(), true, arg, ess);
                }
                if (mStack.completePotion()) {
                    pmeta = (PotionMeta) mStack.getItemStack().getItemMeta();
                    stack.setItemMeta(pmeta);
                } else {
                    user.sendMessage(tl("invalidPotion"));
                    throw new NotEnoughArgumentsException();
                }
            }
        } else {
            throw new Exception(tl("holdPotion"));
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final User user, final String commandLabel, final String[] args) {
        // Note: this enforces an order of effect power duration splash, which the actual command doesn't have.  But that's fine. 
        if (args.length == 1) {
            final List<String> options = Lists.newArrayList();
            options.add("clear");
            if (user.isAuthorized("essentials.potion.apply")) {
                options.add("apply");
            }
            for (final Map.Entry<String, PotionEffectType> entry : Potions.entrySet()) {
                final String potionName = entry.getValue().getName().toLowerCase(Locale.ENGLISH);
                if (user.isAuthorized("essentials.potion." + potionName)) {
                    options.add("effect:" + entry.getKey());
                }
            }
            return options;
        } else if (args.length == 2 && args[0].startsWith("effect:")) {
            return Lists.newArrayList("power:1", "power:2", "power:3", "power:4", "amplifier:0", "amplifier:1", "amplifier:2", "amplifier:3");
        } else if (args.length == 3 && args[0].startsWith("effect:")) {
            final List<String> options = Lists.newArrayList();
            for (final String duration : COMMON_DURATIONS) {
                options.add("duration:" + duration);
            }
            return options;
        } else if (args.length == 4 && args[0].startsWith("effect:")) {
            return Lists.newArrayList("splash:true", "splash:false");
        } else {
            return Collections.emptyList();
        }
    }
}
