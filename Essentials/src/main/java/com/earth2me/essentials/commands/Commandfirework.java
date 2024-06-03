package com.earth2me.essentials.commands;

import com.earth2me.essentials.MetaItemStack;
import com.earth2me.essentials.MobCompat;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.MaterialUtil;
import com.earth2me.essentials.utils.NumberUtil;
import com.google.common.collect.Lists;
import net.ess3.api.TranslatableException;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Server;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.List;

//This command has quite a complicated syntax, in theory it has 4 seperate syntaxes which are all variable:
//
//1: /firework clear             - This clears all of the effects on a firework stack
//
//2: /firework power <int>       - This changes the base power of a firework
//
//3: /firework fire              - This 'fires' a copy of the firework held.
//3: /firework fire <int>        - This 'fires' a number of copies of the firework held.
//3: /firework fire <other>      - This 'fires' a copy of the firework held, in the direction you are looking, #easteregg
//
//4: /firework [meta]            - This will add an effect to the firework stack held
//4: /firework color:<color>     - The minimum you need to set an effect is 'color'
//4: Full Syntax:                  color:<color[,color,..]> [fade:<color[,color,..]>] [shape:<shape>] [effect:<effect[,effect]>]
//4: Possible Shapes:              star, ball, large, creeper, burst
//4: Possible Effects              trail, twinkle

public class Commandfirework extends EssentialsCommand {

    public Commandfirework() {
        super("firework");
    }

    @Override
    protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception {
        if (args.length == 0) {
            throw new NotEnoughArgumentsException();
        }

        final ItemStack stack = user.getItemInHand();
        if (!MaterialUtil.isFirework(stack.getType())) {
            throw new TranslatableException("holdFirework");
        }

        if (args[0].equalsIgnoreCase("clear")) {
            final FireworkMeta fmeta = (FireworkMeta) stack.getItemMeta();
            fmeta.clearEffects();
            stack.setItemMeta(fmeta);
            user.sendTl("fireworkEffectsCleared");
        } else if (args.length > 1 && (args[0].equalsIgnoreCase("power") || args[0].equalsIgnoreCase("p"))) {
            final FireworkMeta fmeta = (FireworkMeta) stack.getItemMeta();
            try {
                final int power = Integer.parseInt(args[1]);
                fmeta.setPower(power > 3 ? 4 : power);
            } catch (final NumberFormatException e) {
                throw new TranslatableException("invalidFireworkFormat", args[1], args[0]);
            }
            stack.setItemMeta(fmeta);
        } else if ((args[0].equalsIgnoreCase("fire") || args[0].equalsIgnoreCase("f")) && user.isAuthorized("essentials.firework.fire")) {
            int amount = 1;
            boolean direction = false;
            if (args.length > 1) {
                if (NumberUtil.isInt(args[1])) {
                    final int serverLimit = ess.getSettings().getSpawnMobLimit();
                    amount = Integer.parseInt(args[1]);
                    if (amount > serverLimit) {
                        amount = serverLimit;
                        user.sendTl("mobSpawnLimit");
                    }
                } else {
                    direction = true;
                }
            }
            for (int i = 0; i < amount; i++) {
                final Firework firework = (Firework) user.getWorld().spawnEntity(user.getLocation(), MobCompat.FIREWORK_ROCKET);
                final FireworkMeta fmeta = (FireworkMeta) stack.getItemMeta();
                if (direction) {
                    final Vector vector = user.getBase().getEyeLocation().getDirection().multiply(0.070);
                    if (fmeta.getPower() > 1) {
                        fmeta.setPower(1);
                    }
                    firework.setVelocity(vector);
                }
                firework.setFireworkMeta(fmeta);
            }
        } else {
            final MetaItemStack mStack = new MetaItemStack(stack);
            for (final String arg : args) {
                try {
                    mStack.addFireworkMeta(user.getSource(), true, arg, ess);
                } catch (final Exception e) {
                    user.sendTl("fireworkSyntax");
                    throw e;
                }
            }

            if (mStack.isValidFirework()) {
                final FireworkMeta fmeta = (FireworkMeta) mStack.getItemStack().getItemMeta();
                final FireworkEffect effect = mStack.getFireworkBuilder().build();
                if (fmeta.getEffects().size() > 0 && !user.isAuthorized("essentials.firework.multiple")) {
                    throw new TranslatableException("multipleCharges");
                }
                fmeta.addEffect(effect);
                stack.setItemMeta(fmeta);
            } else {
                user.sendTl("fireworkSyntax");
                throw new TranslatableException("fireworkColor");
            }
        }
    }

    @Override
    protected List<String> getTabCompleteOptions(final Server server, final User user, final String commandLabel, final String[] args) {
        // Note: this enforces an order of color fade shape effect, which the actual command doesn't have.  But that's fine. 
        if (args.length == 1) {
            final List<String> options = Lists.newArrayList();
            if (args[0].startsWith("color:")) {
                final String prefix;
                if (args[0].contains(",")) {
                    prefix = args[0].substring(0, args[0].lastIndexOf(',') + 1);
                } else {
                    prefix = "color:";
                }
                for (final DyeColor color : DyeColor.values()) {
                    options.add(prefix + color.name().toLowerCase() + ",");
                }
                return options;
            }
            options.add("clear");
            options.add("power");
            options.add("color:");
            if (user.isAuthorized("essentials.firework.fire")) {
                options.add("fire");
            }
            return options;
        } else if (args.length == 2) {
            if (args[0].equals("power")) {
                return Lists.newArrayList("1", "2", "3", "4");
            } else if (args[0].equals("fire")) {
                return Lists.newArrayList("1");
            } else if (args[0].startsWith("color:")) {
                final List<String> options = Lists.newArrayList();
                if (!args[1].startsWith("fade:")) {
                    args[1] = "fade:";
                }
                final String prefix;
                if (args[1].contains(",")) {
                    prefix = args[1].substring(0, args[1].lastIndexOf(',') + 1);
                } else {
                    prefix = "fade:";
                }
                for (final DyeColor color : DyeColor.values()) {
                    options.add(prefix + color.name().toLowerCase() + ",");
                }
                return options;
            } else {
                return Collections.emptyList();
            }
        } else if (args.length == 3 && args[0].startsWith("color:")) {
            return Lists.newArrayList("shape:star", "shape:ball", "shape:large", "shape:creeper", "shape:burst");
        } else if (args.length == 4 && args[0].startsWith("color:")) {
            return Lists.newArrayList("effect:trail", "effect:twinkle", "effect:trail,twinkle", "effect:twinkle,trail");
        } else {
            return Collections.emptyList();
        }
    }
}
