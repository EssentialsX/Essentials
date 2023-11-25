package com.earth2me.essentials.signs;

import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.Trade.OverflowType;
import com.earth2me.essentials.User;
import net.ess3.api.IEssentials;
import net.ess3.api.MaxMoneyException;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;

public class SignSell extends EssentialsSign {
    public SignSell() {
        super("Sell");
    }

    @Override
    protected boolean onSignCreate(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException {
        validateTrade(sign, 1, 2, player, ess);
        validateTrade(sign, 3, ess);
        return true;
    }

    @Override
    protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, ChargeException, MaxMoneyException {
        Trade charge = getTrade(sign, 1, 2, player, ess);
        Trade money = getTrade(sign, 3, ess);
        if (!player.isAuthorized("essentials.sell.item."+ charge.getItemStack().getType())){
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cError: &4You do not have sufficient permissions to sell " + charge.getItemStack().getType()));
            return false;
        }

        // Check if the player is trying to sell in bulk.
        if (ess.getSettings().isAllowBulkBuySell() && player.getBase().isSneaking()) {
            final ItemStack heldItem = player.getItemInHand();
            if (charge.getItemStack().isSimilar(heldItem)) {
                final int initialItemAmount = charge.getItemStack().getAmount();
                final int newItemAmount = heldItem.getAmount();
                final ItemStack item = charge.getItemStack();
                item.setAmount(newItemAmount);
                charge = new Trade(item, ess);

                final BigDecimal chargeAmount = money.getMoney();
                //noinspection BigDecimalMethodWithoutRoundingCalled
                BigDecimal pricePerSingleItem = chargeAmount.divide(new BigDecimal(initialItemAmount));
                pricePerSingleItem = pricePerSingleItem.multiply(new BigDecimal(newItemAmount));
                money = new Trade(pricePerSingleItem, ess);
            }
        }

        charge.isAffordableFor(player);
        money.pay(player, OverflowType.DROP);
        charge.charge(player);
        Trade.log("Sign", "Sell", "Interact", username, charge, username, money, sign.getBlock().getLocation(), player.getMoney(), ess);
        return true;
    }
}
