package com.earth2me.essentials.commands;

import com.earth2me.essentials.MetaItemStack;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.NumberUtil;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import static com.earth2me.essentials.I18n.tl;

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
        final ItemStack stack = user.getBase().getItemInHand();
        if (stack.getType() == Material.FIREWORK) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("clear")) {
                    FireworkMeta fmeta = (FireworkMeta) stack.getItemMeta();
                    fmeta.clearEffects();
                    stack.setItemMeta(fmeta);
                    user.sendMessage(tl("fireworkEffectsCleared"));
                } else if (args.length > 1 && (args[0].equalsIgnoreCase("power") || (args[0].equalsIgnoreCase("p")))) {
                    FireworkMeta fmeta = (FireworkMeta) stack.getItemMeta();
                    try {
                        int power = Integer.parseInt(args[1]);
                        fmeta.setPower(power > 3 ? 4 : power);
                    } catch (NumberFormatException e) {
                        throw new Exception(tl("invalidFireworkFormat", args[1], args[0]));
                    }
                    stack.setItemMeta(fmeta);
                } else if ((args[0].equalsIgnoreCase("fire") || (args[0].equalsIgnoreCase("f"))) && user.isAuthorized("essentials.firework.fire")) {
                    int amount = 1;
                    boolean direction = false;
                    if (args.length > 1) {
                        if (NumberUtil.isInt(args[1])) {
                            final int serverLimit = ess.getSettings().getSpawnMobLimit();
                            amount = Integer.parseInt(args[1]);
                            if (amount > serverLimit) {
                                amount = serverLimit;
                                user.sendMessage(tl("mobSpawnLimit"));
                            }
                        } else {
                            direction = true;
                        }
                    }
                    for (int i = 0; i < amount; i++) {
                        Firework firework = (Firework) user.getWorld().spawnEntity(user.getLocation(), EntityType.FIREWORK);
                        FireworkMeta fmeta = (FireworkMeta) stack.getItemMeta();
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
                    for (String arg : args) {
                        try {
                            mStack.addFireworkMeta(user.getSource(), true, arg, ess);
                        } catch (Exception e) {
                            user.sendMessage(tl("fireworkSyntax"));
                            throw e;
                        }
                    }

                    if (mStack.isValidFirework()) {
                        FireworkMeta fmeta = (FireworkMeta) mStack.getItemStack().getItemMeta();
                        FireworkEffect effect = mStack.getFireworkBuilder().build();
                        if (!fmeta.getEffects().isEmpty() && !user.isAuthorized("essentials.firework.multiple")) {
                            throw new Exception(tl("multipleCharges"));
                        }
                        fmeta.addEffect(effect);
                        stack.setItemMeta(fmeta);
                    } else {
                        user.sendMessage(tl("fireworkSyntax"));
                        throw new Exception(tl("fireworkColor"));
                    }
                }
            } else {
                throw new NotEnoughArgumentsException();
            }
        } else {
            throw new Exception(tl("holdFirework"));
        }
    }
}
