package com.earth2me.essentials.signs;

import com.earth2me.essentials.User;
import net.ess3.api.IEssentials;

public class SignWorkbench extends EssentialsSign {
    public SignWorkbench() {
        super("Workbench");
    }

    @Override
    protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) {
        player.getBase().openWorkbench(null, true);
        return true;
    }
}
