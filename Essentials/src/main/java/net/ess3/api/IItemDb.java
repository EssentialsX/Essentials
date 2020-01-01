package net.ess3.api;


import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

public interface IItemDb extends com.earth2me.essentials.api.IItemDb {

    /**
     * Add an item resolver that is called before looking up the item in the item database.
     *
     * @param plugin The owning plugin
     * @param name The name of the resolver
     * @param resolver The resolver accepting a String and returning an ItemStack, or null if
     *                 none was found
     * @throws Exception If a resolver with a conflicting name is found
     */
    void registerResolver(Plugin plugin, String name, ItemResolver resolver) throws Exception;

    /**
     * Remove an item resolver from the given plugin with the given name.
     *
     * @param plugin The owning plugin
     * @param name The name of the resolver
     * @throws Exception If no matching resolver was found
     */
    void unregisterResolver(Plugin plugin, String name) throws Exception;

    /**
     * Check whether a resolver with a given name from a given plugin has been registered.
     *
     * @param plugin The owning plugin
     * @param name The name of the resolver
     * @return Whether the resolver could be found
     */
    boolean isResolverPresent(Plugin plugin, String name);

    /**
     * Get all registered resolvers.
     *
     * @return A map of all registered resolvers
     */
    Map<PluginKey, ItemResolver> getResolvers();

    /**
     * Get all registered resolvers from the given plugin.
     *
     * @param plugin The owning plugin
     * @return A map of all matching resolvers
     */
    Map<PluginKey, ItemResolver> getResolvers(Plugin plugin);

    /**
     * Get the resolver function with the given name from the given plugin.
     *
     * @param plugin The owning plugin
     * @param name The name of the resolver
     * @return The resolver function, or null if not found
     */
    ItemResolver getResolver(Plugin plugin, String name);

    /**
     * Create a stack from the given name with the maximum stack size for that material.
     *
     * @param name Item name to look up in the database
     * @param useResolvers Whether to call other plugins' resolver functions before looking the
     *                     item up in the database
     * @return The requested item stack with the maximum stack size
     * @throws Exception if the item stack cannot be created
     */
    ItemStack get(String name, boolean useResolvers) throws Exception;

    @FunctionalInterface
    interface ItemResolver extends Function<String, ItemStack> {
        default Collection<String> getNames() {
            return null;
        }
    }

}
