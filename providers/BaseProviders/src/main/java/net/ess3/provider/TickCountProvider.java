package net.ess3.provider;

import net.essentialsx.providers.NullableProvider;

@NullableProvider
public interface TickCountProvider extends Provider {
    int getTickCount();
}
