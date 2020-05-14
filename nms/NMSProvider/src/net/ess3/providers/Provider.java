package net.ess3.providers;

public interface Provider {
    boolean tryProvider();

    ProviderType getType();
}
