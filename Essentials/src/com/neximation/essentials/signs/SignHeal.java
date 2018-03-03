package com.neximation.essentials.signs;

import com.neximation.essentials.ChargeException;
import com.neximation.essentials.Trade;
import com.neximation.essentials.User;
import net.ess3.api.IEssentials;

import static com.neximation.essentials.I18n.tl;


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
            throw new SignException(tl("healDead"));
        }
        final Trade charge = getTrade(sign, 1, ess);
        charge.isAffordableFor(player);
        player.getBase().setHealth(20);
        player.getBase().setFoodLevel(20);
        player.getBase().setFireTicks(0);
        player.sendMessage(tl("youAreHealed"));
        charge.charge(player);
        Trade.log("Sign", "Heal", "Interact", username, null, username, charge, sign.getBlock().getLocation(), ess);
        return true;
    }
}
