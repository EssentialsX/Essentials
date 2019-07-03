package com.earth2me.essentials.signs;

import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.User;
import net.ess3.api.IEssentials;

import static com.earth2me.essentials.I18n.tl;


public class SignDisposal extends EssentialsSign {
    public SignDisposal() {
        super("Disposal");
    }

    @Override
    protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, ChargeException {
        if (!sign.getLine(3).isEmpty()) {
            Trade charge = getTrade(sign, 3, ess);
            charge.isAffordableFor(player);
            charge.charge(player);
        }

        StringBuilder titleBuilder = new StringBuilder();
        titleBuilder.append(sign.getLine(1));
        titleBuilder.append(sign.getLine(2));
        if (titleBuilder.length() == 0) {
            titleBuilder.append(tl("disposal"));
        }

        player.getBase().openInventory(ess.getServer().createInventory(player.getBase(), 36, titleBuilder.toString()));
        return true;
    }
}
