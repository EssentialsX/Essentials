package com.earth2me.essentials.api;

import java.util.Locale;


/**
 * <p>II18n interface.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public interface II18n {
    /**
     * Gets the current locale setting
     *
     * @return the current locale, if not set it will return the default locale
     */
    Locale getCurrentLocale();
}
