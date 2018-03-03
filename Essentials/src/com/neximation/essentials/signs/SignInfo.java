package com.neximation.essentials.signs;

import com.neximation.essentials.ChargeException;
import com.neximation.essentials.Trade;
import com.neximation.essentials.User;
import com.neximation.essentials.textreader.IText;
import com.neximation.essentials.textreader.KeywordReplacer;
import com.neximation.essentials.textreader.TextInput;
import com.neximation.essentials.textreader.TextPager;
import net.ess3.api.IEssentials;

import java.io.IOException;


public class SignInfo extends EssentialsSign {
    public SignInfo() {
        super("Info");
    }

    @Override
    protected boolean onSignCreate(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException {
        validateTrade(sign, 3, ess);
        return true;
    }

    @Override
    protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, ChargeException {
        final Trade charge = getTrade(sign, 3, ess);
        charge.isAffordableFor(player);

        String chapter = sign.getLine(1);
        String page = sign.getLine(2);

        final IText input;
        try {
            player.setDisplayNick();
            input = new TextInput(player.getSource(), "info", true, ess);
            final IText output = new KeywordReplacer(input, player.getSource(), ess);
            final TextPager pager = new TextPager(output);
            pager.showPage(chapter, page, null, player.getSource());

        } catch (IOException ex) {
            throw new SignException(ex.getMessage(), ex);
        }

        charge.charge(player);
        Trade.log("Sign", "Info", "Interact", username, null, username, charge, sign.getBlock().getLocation(), ess);
        return true;
    }
}
