package com.earth2me.essentials.utils;

import com.earth2me.essentials.IEssentials;
import net.ess3.nms.SpawnerProvider;
import net.ess3.nms.blockmeta.BlockMetaSpawnerProvider;
import net.ess3.nms.legacy.LegacyProvider;
import net.ess3.nms.v1_8_R1.v1_8_R1SpawnerProvider;

import java.util.Arrays;
import java.util.List;

public class SpawnerProviderFactory {
    private IEssentials ess;

    public SpawnerProviderFactory(IEssentials ess) {
        this.ess = ess;
    }

    public SpawnerProvider getProvider() {
        List<SpawnerProvider> availableProviders = Arrays.asList(
                new BlockMetaSpawnerProvider(),
                new v1_8_R1SpawnerProvider(),
                new LegacyProvider()
        );
        SpawnerProvider finalProvider = null;
        for (SpawnerProvider provider : availableProviders) {
            finalProvider = provider;
            if (finalProvider.tryProvider()) {
                break;
            }
        }
        assert finalProvider != null;
        ess.getLogger().info("Using " + finalProvider.getHumanName() + " as spawner provider.");
        return finalProvider;
    }
}
