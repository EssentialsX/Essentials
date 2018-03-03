package com.neximation.essentials.signs;

import com.neximation.essentials.User;
import com.neximation.essentials.utils.NumberUtil;
import net.ess3.api.IEssentials;

import static com.neximation.essentials.I18n.tl;


public class SignBalance extends EssentialsSign {
    public SignBalance() {
        super("Balance");
    }

    @Override
    protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException {
        player.sendMessage(tl("balance", NumberUtil.displayCurrency(player.getMoney(), ess)));
        return true;
    }
}
