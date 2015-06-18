package com.earth2me.essentials.utils;

import com.earth2me.essentials.IEssentials;
import net.ess3.nms.SpawnerProvider;
import net.ess3.nms.blockmeta.BlockMetaSpawnerProvider;
import net.ess3.nms.legacy.LegacySpawnerProvider;
import net.ess3.nms.v1_8_R1.v1_8_R1SpawnerProvider;
import net.ess3.nms.v1_8_R2.v1_8_R2SpawnerProvider;

import java.util.Arrays;
import java.util.List;

public class SpawnerProviderFactory {
    private IEssentials ess;

    public SpawnerProviderFactory(IEssentials ess) {
        this.ess = ess;
    }

    public SpawnerProvider getProvider() {
        List<Class<? extends SpawnerProvider>> availableProviders = Arrays.asList(
                BlockMetaSpawnerProvider.class,
                v1_8_R1SpawnerProvider.class,
                v1_8_R2SpawnerProvider.class,
                LegacySpawnerProvider.class
        );
        SpawnerProvider finalProvider = null;
        for (Class<? extends SpawnerProvider> providerClass : availableProviders) {
            finalProvider = loadProvider(providerClass);
            if (finalProvider != null && finalProvider.tryProvider()) {
                break;
            }
        }
        assert finalProvider != null;
        ess.getLogger().info("Using " + finalProvider.getHumanName() + " as spawner provider.");
        return finalProvider;
    }

    private SpawnerProvider loadProvider(Class<? extends SpawnerProvider> providerClass) {
        try {
            return providerClass.newInstance();
        } catch (Throwable ignored) {
            return null;
        }
    }
}
