package com.earth2me.essentials;

import io.papermc.lib.PaperLib;
import net.ess3.provider.Provider;
import net.essentialsx.providers.ProviderData;
import net.essentialsx.providers.ProviderTest;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class ProviderFactory {
    private final Map<Class<? extends Provider>, Provider> providers = new HashMap<>();
    private final Map<Class<? extends Provider>, List<Class<? extends Provider>>> registeredProviders = new HashMap<>();
    private final Essentials essentials;

    public ProviderFactory(final Essentials essentials) {
        this.essentials = essentials;
    }

    /**
     * Gets the provider which has been selected for the given type.
     * @param providerClass The provider type.
     * @return the provider or null if no provider could be selected for that type.
     */
    public <P extends Provider> P get(final Class<P> providerClass) {
        final Provider provider = providers.get(providerClass);
        if (provider == null) {
            return null;
        }
        //noinspection unchecked
        return (P) provider;
    }

    @SafeVarargs
    public final void registerProvider(final Class<? extends Provider>... toRegister) {
        for (final Class<? extends Provider> provider : toRegister) {
            final Class<?> superclass = provider.getInterfaces().length > 0 ? provider.getInterfaces()[0] : provider.getSuperclass();
            if (Provider.class.isAssignableFrom(superclass)) {
                //noinspection unchecked
                registeredProviders.computeIfAbsent((Class<? extends Provider>) superclass, k -> new ArrayList<>()).add(provider);
                if (essentials.getSettings().isDebug()) {
                    essentials.getLogger().info("Registered provider " + provider.getSimpleName() + " for " + superclass.getSimpleName());
                }
            }
        }
    }

    public void finalizeRegistration() {
        for (final Map.Entry<Class<? extends Provider>, List<Class<? extends Provider>>> entry : registeredProviders.entrySet()) {
            final Class<? extends Provider> providerClass = entry.getKey();
            Class<? extends Provider> highestProvider = null;
            ProviderData highestProviderData = null;
            int highestWeight = -1;
            for (final Class<? extends Provider> provider : entry.getValue()) {
                try {
                    final ProviderData providerData = provider.getAnnotation(ProviderData.class);
                    if (providerData.weight() > highestWeight && testProvider(provider)) {
                        highestWeight = providerData.weight();
                        highestProvider = provider;
                        highestProviderData = providerData;
                    }
                } catch (final Exception e) {
                    essentials.getLogger().log(Level.SEVERE, "Failed to initialize provider " + provider.getName(), e);
                }
            }
            if (highestProvider == null) {
                throw new IllegalStateException("No provider found for " + providerClass.getName());
            }

            essentials.getLogger().info("Selected " + highestProviderData.description() + " as the provider for " + providerClass.getSimpleName());
            providers.put(providerClass, getProviderInstance(highestProvider));
        }
        registeredProviders.clear();
    }

    private boolean testProvider(final Class<?> providerClass) throws InvocationTargetException, IllegalAccessException {
        for (final Method method : providerClass.getMethods()) {
            if (method.isAnnotationPresent(ProviderTest.class)) {
                return (Boolean) method.invoke(null);
            }
        }
        return true;
    }

    private <P extends Provider> P getProviderInstance(final Class<P> provider) {
        try {
            final Constructor<?> constructor = provider.getConstructors()[0];
            if (constructor.getParameterTypes().length == 0) {
                //noinspection unchecked
                return (P) constructor.newInstance();
            }
            final Object[] args = new Object[constructor.getParameterTypes().length];

            /*
            Providers can have constructors with any of the following types, and this code will automatically supply them;
            - Plugin - The Essentials instance will be passed
            - boolean - True will be passed if this server is running Paper, otherwise false.
             */
            for (int i = 0; i < args.length; i++) {
                final Class<?> paramType = constructor.getParameterTypes()[i];
                if (paramType.isAssignableFrom(Plugin.class)) {
                    args[i] = essentials;
                } else if (paramType.isAssignableFrom(boolean.class)) {
                    args[i] = PaperLib.isPaper();
                } else {
                    throw new IllegalArgumentException("Unsupported parameter type " + paramType.getName());
                }
            }

            //noinspection unchecked
            return (P) constructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            try {
                return provider.getConstructor().newInstance();
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException ex) {
                e.printStackTrace();
                throw new RuntimeException(ex);
            }
        }
    }
}
