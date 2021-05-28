package com.earth2me.essentials.utils;

import org.bukkit.entity.Player;

/**
 * A state that can be either true, false or unset.
 *
 * @see com.earth2me.essentials.perm.IPermissionsHandler#isPermissionSetExact(Player, String)
 */
public enum TriState {
    TRUE,
    FALSE,
    UNSET
}
