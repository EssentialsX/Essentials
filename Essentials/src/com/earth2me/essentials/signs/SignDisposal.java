package com.earth2me.essentials.signs;

import com.earth2me.essentials.User;
import net.ess3.api.IEssentials;

import static com.earth2me.essentials.I18n.tl;


public class SignDisposal extends EssentialsSign {
    public SignDisposal() {
        super("Disposal");
    }

    @Override
    protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) {
        player.getBase().openInventory(ess.getServer().createInventory(player.getBase(), 36, tl("disposal")));
        return true;
    }
}
