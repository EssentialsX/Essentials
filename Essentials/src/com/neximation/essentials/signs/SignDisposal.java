package com.neximation.essentials.signs;

import com.neximation.essentials.User;
import net.ess3.api.IEssentials;


public class SignDisposal extends EssentialsSign {
    public SignDisposal() {
        super("Disposal");
    }

    @Override
    protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) {
        player.getBase().openInventory(ess.getServer().createInventory(player.getBase(), 36, "Disposal"));
        return true;
    }
}
