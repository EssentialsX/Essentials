package com.earth2me.essentials.signs;

import com.earth2me.essentials.User;
import net.ess3.api.IEssentials;
import net.ess3.provider.ContainerProvider;

public class SignAnvil extends EssentialsSign {
    public SignAnvil() {
        super("Anvil");
    }

    @Override
    protected boolean onSignCreate(final ISign sign, final User player, final String username, final IEssentials ess) {
        if (ess.provider(ContainerProvider.class) == null) {
            player.sendTl("unsupportedBrand");
            return false;
        }
        return true;
    }

    @Override
    protected boolean onSignInteract(final ISign sign, final User player, final String username, final IEssentials ess) {
        ess.provider(ContainerProvider.class).openAnvil(player.getBase());
        return true;
    }
}
