package net.ess3.provider;

import org.bukkit.block.Sign;

public interface SignDataProvider extends Provider {
    void setSignData(Sign sign, String key, String value);

    String getSignData(Sign sign, String key);
}
