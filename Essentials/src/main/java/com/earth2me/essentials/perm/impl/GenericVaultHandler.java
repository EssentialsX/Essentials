package com.earth2me.essentials.perm.impl;

public class GenericVaultHandler extends AbstractVaultHandler {
    @Override
    public boolean tryProvider() {
        return super.canLoad();
    }
}
