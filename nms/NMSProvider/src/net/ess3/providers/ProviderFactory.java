package net.ess3.providers;

import java.util.logging.Logger;

public class ProviderFactory<T extends Provider> {
    private Logger logger;
    private String providerType;
    private Iterable<Class<? extends T>> providers;

    public ProviderFactory(Logger logger, Iterable<Class<? extends T>> providers, String providerType) {
        this.logger = logger;
        this.providerType = providerType;
        this.providers = providers;
    }

    public T getProvider() {
        for (Class<? extends T> providerClass : providers) {
            T provider = loadProvider(providerClass);
            if (provider != null && provider.tryProvider()) {
                logger.info("Using " + provider.getDescription() + " as " + providerType + " provider.");
                return provider;
            }
        }
        logger.severe("The " + providerType + " provider failed to load! Some parts of Essentials may not function correctly.");
        return null;
    }

    private T loadProvider(Class<? extends T> providerClass) {
        try {
            return providerClass.getConstructor().newInstance();
        } catch (Throwable ignored) {
            return null;
        }
    }
}
