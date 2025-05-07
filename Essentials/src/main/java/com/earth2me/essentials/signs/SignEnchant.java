package com.earth2me.essentials.signs;

import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.Enchantments;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import com.earth2me.essentials.craftbukkit.Inventories;
import com.earth2me.essentials.utils.NumberUtil;
import net.ess3.api.IEssentials;
import net.ess3.provider.MaterialTagProvider;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.Locale;

public class SignEnchant extends EssentialsSign {
    public SignEnchant() {
        super("Enchant");
    }

    @Override
    protected boolean onSignCreate(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, ChargeException {
        final ItemStack stack;
        final String itemName = sign.getLine(1);
        final MaterialTagProvider tagProvider = ess.provider(MaterialTagProvider.class);
        try {
            stack = itemName.equals("*") || itemName.equalsIgnoreCase("any") || (tagProvider != null && tagProvider.tagExists(itemName)) ? null : getItemStack(sign.getLine(1), 1, ess);
        } catch (final SignException e) {
            sign.setLine(1, "§c<item|any>");
            throw e;
        }
        final String[] enchantLevel = sign.getLine(2).split(":");
        int level = 1;
        final Enchantment enchantment = Enchantments.getByName(enchantLevel[0]);
        if (enchantment == null) {
            sign.setLine(2, "§c<enchant>");
            throw new SignException("enchantmentNotFound");
        }
        if (enchantLevel.length > 1) {
            try {
                level = Integer.parseInt(enchantLevel[1]);
            } catch (final NumberFormatException ex) {
                sign.setLine(2, "§c<enchant>");
                throw new SignException(ex, "errorWithMessage", ex.getMessage());
            }
        }
        final boolean allowUnsafe = ess.getSettings().allowUnsafeEnchantments() && player.isAuthorized("essentials.enchantments.allowunsafe") && player.isAuthorized("essentials.signs.enchant.allowunsafe");
        if (level < 0 || (!allowUnsafe && level > enchantment.getMaxLevel())) {
            level = enchantment.getMaxLevel();
        }
        sign.setLine(2, enchantLevel[0].toLowerCase().replaceFirst("^[a-zA-Z]", enchantLevel[0].substring(0,1).toUpperCase()) + ":" + level);
        try {
            if (stack != null) {
                if (allowUnsafe) {
                    stack.addUnsafeEnchantment(enchantment, level);
                } else {
                    stack.addEnchantment(enchantment, level);
                }
            }
        } catch (final Throwable ex) {
            throw new SignException(ex, "errorWithMessage", ex.getMessage());
        }
        final Trade trade = getTrade(sign, 3, 0, ess);
        final BigDecimal money = trade.getMoney();
        if (money != null) {
            sign.setLine(3, NumberUtil.shortCurrency(money, ess));
        }
        return true;
    }

    @Override
    protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, ChargeException {
        final ItemStack playerHand = Inventories.getItemInHand(player.getBase());
        final MaterialTagProvider tagProvider = ess.provider(MaterialTagProvider.class);
        final String itemName = sign.getLine(1);
        final ItemStack search = itemName.equals("*") || itemName.equalsIgnoreCase("any") || (tagProvider != null && tagProvider.tagExists(itemName) && tagProvider.isTagged(itemName, playerHand.getType())) ? null : getItemStack(itemName, 1, ess);
        final Trade charge = getTrade(sign, 3, ess);
        charge.isAffordableFor(player);
        final String[] enchantLevel = sign.getLine(2).split(":");
        final Enchantment enchantment = Enchantments.getByName(enchantLevel[0]);
        if (enchantment == null) {
            throw new SignException("enchantmentNotFound");
        }

        if (!player.isAuthorized("essentials.enchantments."+enchantment.getKey().getKey()) && ess.getSettings().isEnchantSignRestricted()){
            throw new SignException("enchantmentPerm", enchantment.getKey().getKey());
        }

        int level = 1;
        if (enchantLevel.length > 1) {
            try {
                level = Integer.parseInt(enchantLevel[1]);
            } catch (final NumberFormatException ex) {
                throw new SignException(ex, "errorWithMessage", ex.getMessage());
            }
        }

        if (playerHand == null || playerHand.getAmount() != 1 || (playerHand.containsEnchantment(enchantment) && playerHand.getEnchantmentLevel(enchantment) == level)) {
            throw new SignException("missingItems", 1, sign.getLine(1));
        }
        if (search != null && playerHand.getType() != search.getType()) {
            throw new SignException("missingItems", 1, search.getType().toString().toLowerCase(Locale.ENGLISH).replace('_', ' '));
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
        } catch (final Exception ex) {
            throw new SignException(ex, "errorWithMessage", ex.getMessage());
        }

        final String enchantmentName = Enchantments.getRealName(enchantment);
        if (level == 0) {
            player.sendTl("enchantmentRemoved", enchantmentName.replace('_', ' '));
        } else {
            player.sendTl("enchantmentApplied", enchantmentName.replace('_', ' '));
        }

        charge.charge(player);
        Trade.log("Sign", "Enchant", "Interact", username, charge, username, charge, sign.getBlock().getLocation(), player.getMoney(), ess);
        player.getBase().updateInventory();
        return true;
    }
}
