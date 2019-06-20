package com.earth2me.essentials.perm.impl;

/**
 * <p>GenericVaultHandler class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class GenericVaultHandler extends AbstractVaultHandler {
    /** {@inheritDoc} */
    @Override
    public boolean tryProvider() {
        return super.canLoad();
    }
}
