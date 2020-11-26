package com.earth2me.essentials.api;

import org.bukkit.entity.Player;

import java.util.Locale;

/**
 * Provides access to the current locale in use.
 *
 * @deprecated External plugins should prefer to use either the player's client language ({@link Player#getLocale()} or
 *             {@link net.ess3.api.II18n} in case of future additions.
 */
@Deprecated
public interface II18n {
    /**
     * Gets the current locale setting
     *
     * @return the current locale, if not set it will return the default locale
     */
    Locale getCurrentLocale();
}
