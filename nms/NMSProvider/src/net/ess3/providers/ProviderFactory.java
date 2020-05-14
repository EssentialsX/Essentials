package net.ess3.providers;

import java.util.logging.Logger;

public class ProviderFactory<T extends Provider> {
    private Logger logger;
    private Iterable<Class <? extends T>> providers;

    public ProviderFactory(Logger logger, Iterable<Class <? extends T>> providers) {
        this.logger = logger;
        this.providers = providers;
    }

    public T getProvider() {
        T provider = null;
        for (Class<? extends T> providerClass : providers) {
            provider = loadProvider(providerClass);
            if (provider != null && provider.tryProvider()) {
                break;
            }
        }
        assert provider != null;
        logger.info("Using " + provider.getClass().getSimpleName() + " as " + provider.getType() + " provider.");
        return provider;
    }

    private T loadProvider(Class<? extends T> providerClass) {
        try {
            return providerClass.getConstructor().newInstance();
        } catch (Throwable ignored) {
            return null;
        }
    }
}
