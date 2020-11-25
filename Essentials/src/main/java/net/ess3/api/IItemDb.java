package net.ess3.api;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

/**
 * Provides access to the current item alias registry, and allows registration of custom item resolvers.
 */
public interface IItemDb extends com.earth2me.essentials.api.IItemDb {

    /**
     * Add an item resolver that is called before looking up the item in the item database.
     *
     * @param plugin   The owning plugin
     * @param name     The name of the resolver
     * @param resolver The resolver accepting a String and returning an ItemStack, or null if
     *                 none was found
     * @throws Exception If a resolver with a conflicting name is found
     */
    void registerResolver(Plugin plugin, String name, ItemResolver resolver) throws Exception;

    /**
     * Remove an item resolver from the given plugin with the given name.
     *
     * @param plugin The owning plugin
     * @param name   The name of the resolver
     * @throws Exception If no matching resolver was found
     */
    void unregisterResolver(Plugin plugin, String name) throws Exception;

    /**
     * Check whether a resolver with a given name from a given plugin has been registered.
     *
     * @param plugin The owning plugin
     * @param name   The name of the resolver
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
     * @param name   The name of the resolver
     * @return The resolver function, or null if not found
     */
    ItemResolver getResolver(Plugin plugin, String name);

    /**
     * Create a stack from the given name with the maximum stack size for that material.
     *
     * Note: it is unlikely that external plugins will need to call this method directly. In most cases, {@link IItemDb#get(String)}
     * and {@link IItemDb#get(String, int)} should be sufficient. However, if you intend to perform an item lookup <i>inside</i>
     * a {@link ItemResolver} implementation, you <b>must</b> call this method with useResolvers as false to prevent recursion.
     *
     * @param name         Item name to look up in the database
     * @param useResolvers Whether to call other plugins' resolver functions before looking the
     *                     item up in the database
     * @return The requested item stack with the maximum stack size
     * @throws Exception if the item stack cannot be created
     */
    ItemStack get(String name, boolean useResolvers) throws Exception;

    /**
     * Converts the given {@link ItemStack} to a string representation that can be saved.
     * This is typically used for /createkit but may be used by other plugins for various purposes too.
     *
     * @param itemStack    The stack to serialize
     * @param useResolvers Whether to call other plugins' item resolvers before looking the
     *                     item up in the database
     * @return A string representation of the given item stack
     * @deprecated This will soon be replaced with a new two-way API. It should not be relied upon by external plugins!
     */
    @Deprecated
    String serialize(ItemStack itemStack, boolean useResolvers);

    /**
     * A service capable of resolving custom item names to items and vice versa, as well as adding extra item names to
     * tab complete suggestions.
     */
    @FunctionalInterface
    interface ItemResolver extends Function<String, ItemStack> {

        /**
         * Creates an item stack from the given name, if the given name is recognised by this resolver.
         *
         * @param name The name of the item to resolve
         * @return A default stack of the item, or null if not recognised by this resolver.
         */
        @Override
        ItemStack apply(String name);

        /**
         * Get all possible names that are recognised by this item resolver.
         * <p>
         * Implementing this method is optional but recommended, since it enables custom items to be seen in tab complete.
         *
         * @return A collection of all the possible names for items that this resolver recognises.
         */
        default Collection<String> getNames() {
            return null;
        }

        /**
         * Get a name recognised by this resolver for the given ItemStack, and return null if none was found.
         * The implementation of {@link ItemResolver#apply(String)} must recognise any string returned here.
         * Note that if you return a string here, no extra meta will be added - if you want to add extra meta, you need to return it with your serialized string.
         * <p>
         * Implementing this method is optional but recommended, since it enables custom items to be saved by /createkit.
         *
         * @param stack The stack to serialize
         * @return The name of the item if a suitable name was found, else null
         */
        default String serialize(final ItemStack stack) {
            return null;
        }
    }

}
