package net.ess3.providers;

import java.util.logging.Logger;

/**
 * <p>ProviderFactory class.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public class ProviderFactory<T extends Provider> {
    private Logger logger;
    private String providerType;
    private Iterable<Class <? extends T>> availableProviders;

    /**
     * <p>Constructor for ProviderFactory.</p>
     *
     * @param logger a {@link java.util.logging.Logger} object.
     * @param availableProviders a {@link java.lang.Iterable} object.
     * @param providerType a {@link java.lang.String} object.
     */
    public ProviderFactory(Logger logger, Iterable<Class <? extends T>> availableProviders, String providerType) {
        this.logger = logger;
        this.providerType = providerType;
        this.availableProviders = availableProviders;
    }

    /**
     * <p>getProvider.</p>
     *
     * @return a T object.
     */
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
