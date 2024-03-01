package com.earth2me.essentials.signs;

import com.earth2me.essentials.User;
import net.ess3.api.IEssentials;

public class SignGrindstone extends EssentialsSign {
    public SignGrindstone() {
        super("Grindstone");
    }

    @Override
    protected boolean onSignCreate(final ISign sign, final User player, final String username, final IEssentials ess) {
        if (ess.getContainerProvider() == null) {
            player.sendTl("unsupportedBrand");
            return false;
        }
        return true;
    }

    @Override
    protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) {
        ess.getContainerProvider().openGrindstone(player.getBase());
        return true;
    }
}
