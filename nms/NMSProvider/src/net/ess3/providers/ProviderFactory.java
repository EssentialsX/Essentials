package net.ess3.providers;

import java.util.logging.Logger;

public class ProviderFactory<T extends Provider> {
    private Logger logger;
    private String providerType;
    private Iterable<Class <? extends T>> availableProviders;

    public ProviderFactory(Logger logger, Iterable<Class <? extends T>> availableProviders, String providerType) {
        this.logger = logger;
        this.providerType = providerType;
        this.availableProviders = availableProviders;
    }

    public T getProvider() {
        T finalProvider = null;
        for (Class<? extends T> providerClass : availableProviders) {
            finalProvider = loadProvider(providerClass);
            if (finalProvider != null && finalProvider.tryProvider()) {
                break;
            }
        }
        assert finalProvider != null;
        logger.info("Using " + finalProvider.getHumanName() + " as " + providerType + " provider.");
        return finalProvider;
    }

    private T loadProvider(Class<? extends T> providerClass) {
        try {
            return providerClass.getConstructor().newInstance();
        } catch (Throwable ignored) {
            return null;
        }
    }
}
