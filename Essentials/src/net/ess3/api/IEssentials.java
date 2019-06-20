package net.ess3.api;

import net.ess3.nms.PotionMetaProvider;
import net.ess3.nms.SpawnEggProvider;

import java.util.Collection;

/**
 * <p>IEssentials interface.</p>
 *
 * @author LoopyD
 * @version $Id: $Id
 */
public interface IEssentials extends com.earth2me.essentials.IEssentials {

    /**
     * <p>getVanishedPlayersNew.</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    Collection<String> getVanishedPlayersNew();

    /**
     * <p>getSpawnEggProvider.</p>
     *
     * @return a {@link net.ess3.nms.SpawnEggProvider} object.
     */
    SpawnEggProvider getSpawnEggProvider();

    /**
     * <p>getPotionMetaProvider.</p>
     *
     * @return a {@link net.ess3.nms.PotionMetaProvider} object.
     */
    PotionMetaProvider getPotionMetaProvider();
}
