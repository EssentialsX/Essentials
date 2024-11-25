package com.earth2me.essentials.signs;

import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.AdventureUtil;
import com.earth2me.essentials.utils.NumberUtil;
import net.ess3.api.IEssentials;

public class SignBalance extends EssentialsSign {
    public SignBalance() {
        super("Balance");
    }

    @Override
    protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException {
        player.sendTl("balance", AdventureUtil.parsed(NumberUtil.displayCurrency(player.getMoney(), ess)));
        return true;
    }
}
