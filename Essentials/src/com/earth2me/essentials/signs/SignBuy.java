package com.earth2me.essentials.signs;

import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import net.ess3.api.IEssentials;
import net.ess3.api.MaxMoneyException;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;

import static com.earth2me.essentials.I18n.tl;

public class SignBuy extends EssentialsShopSign {
    public SignBuy() {
        super("Buy");
    }

    @Override
    protected boolean onSignCreate(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException {
        validateTrade(sign, 1, 2, player, ess);
        if(sign.getLine(3).isEmpty()) {
            updateFromWorthMultiplied(sign, player, ess);
        } else {
            validateTrade(sign, 3, ess);
        }
        return true;
    }

    @Override
    protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, ChargeException, MaxMoneyException {
        if(sign.getLine(3).startsWith(tl("signFormatWorth")) && updateFromWorthMultiplied(sign, player, ess)) {
            return false;
        }
        Trade items = getTrade(sign, 1, 2, player, ess);
        Trade charge = getTrade(sign, 3, ess);

        // Check if the player is trying to buy in bulk.
        if (ess.getSettings().isAllowBulkBuySell() && player.getBase().isSneaking()) {
            ItemStack heldItem = player.getItemInHand();
            if (items.getItemStack().isSimilar(heldItem)) {
                int initialItemAmount = items.getItemStack().getAmount();
                int newItemAmount = heldItem.getAmount();
                ItemStack item = items.getItemStack();
                item.setAmount(newItemAmount);
                items = new Trade(item, ess);

                BigDecimal chargeAmount = charge.getMoney();
                //noinspection BigDecimalMethodWithoutRoundingCalled
                BigDecimal pricePerSingleItem = chargeAmount.divide(new BigDecimal(initialItemAmount));
                pricePerSingleItem = pricePerSingleItem.multiply(new BigDecimal(newItemAmount));
                charge = new Trade(pricePerSingleItem, ess);
            }
        }

        charge.isAffordableFor(player);
        if (!items.pay(player)) {
            throw new ChargeException("Inventory full"); //TODO: TL
        }
        charge.charge(player);
        Trade.log("Sign", "Buy", "Interact", username, charge, username, items, sign.getBlock().getLocation(), ess);
        return true;
    }
}
