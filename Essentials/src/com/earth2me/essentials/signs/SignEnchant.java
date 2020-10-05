package com.earth2me.essentials.signs;

import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.Enchantments;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import net.ess3.api.IEssentials;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

import static com.earth2me.essentials.I18n.tl;


public class SignEnchant extends EssentialsSign {
    public SignEnchant() {
        super("Enchant");
    }

    @Override
    protected boolean onSignCreate(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, ChargeException {
        final ItemStack stack;
        try {
            stack = sign.getLine(1).equals("*") || sign.getLine(1).equalsIgnoreCase("any") ? null : getItemStack(sign.getLine(1), 1, ess);
        } catch (SignException e) {
            sign.setLine(1, "§c<item|any>");
            throw e;
        }
        final String[] enchantLevel = sign.getLine(2).split(":");
        int level = 1;
        final Enchantment enchantment = Enchantments.getByName(enchantLevel[0]);
        if (enchantment == null) {
            sign.setLine(2, "§c<enchant>");
            throw new SignException(tl("enchantmentNotFound"));
        }
        if (enchantLevel.length > 1) {
            try {
                level = Integer.parseInt(enchantLevel[1]);
            } catch (NumberFormatException ex) {
                sign.setLine(2, "§c<enchant>");
                throw new SignException(ex.getMessage(), ex);
            }
        }
        final boolean allowUnsafe = ess.getSettings().allowUnsafeEnchantments() && player.isAuthorized("essentials.enchantments.allowunsafe") && player.isAuthorized("essentials.signs.enchant.allowunsafe");
        if (level < 0 || (!allowUnsafe && level > enchantment.getMaxLevel())) {
            level = enchantment.getMaxLevel();
            sign.setLine(2, enchantLevel[0] + ":" + level);
        }
        try {
            if (stack != null) {
                if (allowUnsafe) {
                    stack.addUnsafeEnchantment(enchantment, level);
                } else {
                    stack.addEnchantment(enchantment, level);
                }
            }
        } catch (Throwable ex) {
            throw new SignException(ex.getMessage(), ex);
        }
        getTrade(sign, 3, ess);
        return true;
    }

    @Override
    protected boolean onSignInteract(ISign sign, User player, String username, IEssentials ess) throws SignException, ChargeException {
        final ItemStack search = sign.getLine(1).equals("*") || sign.getLine(1).equalsIgnoreCase("any") ? null : getItemStack(sign.getLine(1), 1, ess);
        final Trade charge = getTrade(sign, 3, ess);
        charge.isAffordableFor(player);
        final String[] enchantLevel = sign.getLine(2).split(":");
        final Enchantment enchantment = Enchantments.getByName(enchantLevel[0]);
        if (enchantment == null) {
            throw new SignException(tl("enchantmentNotFound"));
        }
        int level = 1;
        if (enchantLevel.length > 1) {
            try {
                level = Integer.parseInt(enchantLevel[1]);
            } catch (NumberFormatException ex) {
                throw new SignException(ex.getMessage(), ex);
            }
        }

        final ItemStack playerHand = player.getBase().getItemInHand();
        if (playerHand == null || playerHand.getAmount() != 1 || (playerHand.containsEnchantment(enchantment) && playerHand.getEnchantmentLevel(enchantment) == level)) {
            throw new SignException(tl("missingItems", 1, sign.getLine(1)));
        }
        if (search != null && playerHand.getType() != search.getType()) {
            throw new SignException(tl("missingItems", 1, search.getType().toString().toLowerCase(Locale.ENGLISH).replace('_', ' ')));
        }

        try {
            if (level == 0) {
                playerHand.removeEnchantment(enchantment);
            } else {
                if (ess.getSettings().allowUnsafeEnchantments() && player.isAuthorized("essentials.signs.enchant.allowunsafe")) {
                    playerHand.addUnsafeEnchantment(enchantment, level);
                } else {
                    playerHand.addEnchantment(enchantment, level);
                }
            }
        } catch (Exception ex) {
            throw new SignException(ex.getMessage(), ex);
        }

        final String enchantmentName = enchantment.getName().toLowerCase(Locale.ENGLISH);
        if (level == 0) {
            player.sendMessage(tl("enchantmentRemoved", enchantmentName.replace('_', ' ')));
        } else {
            player.sendMessage(tl("enchantmentApplied", enchantmentName.replace('_', ' ')));
        }

        charge.charge(player);
        Trade.log("Sign", "Enchant", "Interact", username, charge, username, charge, sign.getBlock().getLocation(), ess);
        player.getBase().updateInventory();
        return true;
    }
}
