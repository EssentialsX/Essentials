package net.ess3.api;

import com.earth2me.essentials.User;

import java.math.BigDecimal;
import java.util.Set;

/**
 *
 */
public interface ISettings extends com.earth2me.essentials.ISettings {
    Set<String> getMultipleMoneyCaps();

    BigDecimal getMoneyCap(User user);

    BigDecimal getMoneyCap(String set);
}
