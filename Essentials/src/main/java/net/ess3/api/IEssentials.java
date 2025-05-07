package net.ess3.api;

import com.earth2me.essentials.items.CustomItemResolver;

import java.util.Collection;

/**
 * This interface exposes certain extra methods implemented in the main class that are not implemented in {@link com.earth2me.essentials.IEssentials}.
 * External plugins should use this class instead of {@link com.earth2me.essentials.Essentials} or {@link com.earth2me.essentials.IEssentials} where possible.
 */
public interface IEssentials extends com.earth2me.essentials.IEssentials {

    /**
     * Get a list of players who are vanished.
     *
     * @return A list of players who are vanished
     */
    Collection<String> getVanishedPlayersNew();

    /**
     * Get the {@link CustomItemResolver} that is currently in use.
     *
     * <b>Note: external plugins should generally avoid using this. If you want to add custom items from your plugin,
     * you probably want to implement your own {@link net.ess3.api.IItemDb.ItemResolver}.</b>
     *
     * @return The custom item resolver
     */
    CustomItemResolver getCustomItemResolver();
}
