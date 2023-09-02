package com.earth2me.essentials.perm.impl;

import com.earth2me.essentials.Essentials;

public class GenericVaultHandler extends AbstractVaultHandler {
    @Override
    public boolean tryProvider(Essentials ess) {
        return super.canLoad();
    }
}
