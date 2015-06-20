package com.earth2me.essentials.perm.impl;

public class GenericVaultHandler extends AbstractVaultHandler {
    @Override
    public boolean canLoad() {
        return super.tryProvider();
    }
}
