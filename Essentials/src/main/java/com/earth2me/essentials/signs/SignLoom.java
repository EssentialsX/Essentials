package com.earth2me.essentials.signs;

import com.earth2me.essentials.User;
import net.ess3.api.IEssentials;

import static com.earth2me.essentials.I18n.tl;

public class SignLoom extends EssentialsSign {
    public SignLoom() {
        super("Loom");
    }

    @Override
    protected boolean onSignCreate(final ISign sign, final User player, final String username, final IEssentials ess) {
        if (ess.getContainerProvider() == null) {
            player.sendMessage(tl("unsupportedBrand"));
            return false;
        }
        return true;
    }

    @Override
    protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) {
        ess.getContainerProvider().openLoom(player.getBase());
        return true;
    }
}
