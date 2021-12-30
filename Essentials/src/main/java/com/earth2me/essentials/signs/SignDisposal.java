package com.earth2me.essentials.signs;

import com.earth2me.essentials.ChargeException;
import com.earth2me.essentials.User;
import net.ess3.api.IEssentials;

public class SignDisposal extends EssentialsSign {
    public SignDisposal() {
        super("Disposal");
    }

    @Override
    protected boolean onSignCreate(final ISign sign, final User player, final String username, final IEssentials ess) throws SignException, ChargeException {
        if (!player.isAuthorized("essentials.signs.disposal.name")) {
            sign.setLine(1, "");
            sign.setLine(2, "");
            sign.setLine(3, "");
        }
        return true;
    }

    @Override
    protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) {
        String title = (sign.getLine(1) + " " + sign.getLine(2) + " " + sign.getLine(3)).trim();
        if (title.isEmpty()) {
            title = player.playerTl("disposal");
        }
        player.getBase().openInventory(ess.getServer().createInventory(player.getBase(), 36, title));
        return true;
    }
}
