package com.earth2me.essentials.signs;

import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import net.ess3.api.IEssentials;

public class SignHeal extends EssentialsSign {
    public SignHeal() {
        super("Heal");
    }

    @Override
    protected boolean onSignCreate(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException {
        validateTrade(sign, 1, ess);
        return true;
    }

    @Override
    protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, ChargeException {
        if (player.getBase().getHealth() == 0) {
            throw new SignException("healDead");
        }
        final double amount = player.getBase().getMaxHealth();
        final Trade charge = getTrade(sign, 1, ess);
        charge.isAffordableFor(player);
        player.getBase().setHealth(amount);
        player.getBase().setFoodLevel(20);
        player.getBase().setFireTicks(0);
        player.sendTl("youAreHealed");
        charge.charge(player);
        Trade.log("Sign", "Heal", "Interact", username, null, username, charge, sign.getBlock().getLocation(), player.getMoney(), ess);
        return true;
    }
}
