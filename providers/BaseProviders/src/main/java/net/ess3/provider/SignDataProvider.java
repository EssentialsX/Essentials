package net.ess3.provider;

import net.essentialsx.providers.NullableProvider;
import org.bukkit.block.Sign;

@NullableProvider
public interface SignDataProvider extends Provider {
    void setSignData(Sign sign, String key, String value);

    String getSignData(Sign sign, String key);
}
