package net.ess3.api;

import net.ess3.nms.PotionMetaProvider;
import net.ess3.nms.SpawnEggProvider;

public interface IEssentials extends com.neximation.essentials.IEssentials {

    SpawnEggProvider getSpawnEggProvider();

    PotionMetaProvider getPotionMetaProvider();
}
