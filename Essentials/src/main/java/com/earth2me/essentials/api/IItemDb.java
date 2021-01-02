package com.earth2me.essentials.api;

import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.MaterialUtil;
import com.earth2me.essentials.utils.NumberUtil;
import com.earth2me.essentials.utils.StringUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;

/**
 * Provides access to the current item alias registry.
 *
 * @deprecated External plugins should use {@link net.ess3.api.IItemDb} instead, which includes access to {@link net.ess3.api.IItemDb.ItemResolver}
 *             APIs.
 */
@Deprecated
public interface IItemDb {

    /**
     * Create a stack from the given name with the given quantity.
     *
     * @param name     Item name to look up in the database
     * @param quantity Quantity of the item stack
     * @return The requested item stack
     * @throws Exception if the item stack cannot be created
     */
    default ItemStack get(final String name, final int quantity) throws Exception {
        final ItemStack stack = get(name);
        stack.setAmount(quantity);
        return stack;
    }

    /**
     * Create a stack from the given name with the maximum stack size for that material.
     * <p>
     * Note that this will always check against resolver functions from other plugins as well.
     * To avoid this behaviour, use net.ess3.api.IItemDb#get(String name, boolean useResolvers).
     *
     * @param name Item name to look up in the database
     * @return The requested item stack with the maximum stack size
     * @throws Exception if the item stack cannot be created
     */
    ItemStack get(final String name) throws Exception;

    /**
     * Get a comma-separated string list of up to 15 aliases for the given stack.
     *
     * @param item Item stack whose names to find
     * @return Comma-separated list of up to 15 item names
     */
    default String names(final ItemStack item) {
        List<String> nameList = nameList(item);

        if (nameList.size() > 15) {
            nameList = nameList.subList(0, 14);
        }
        return StringUtil.joinList(", ", nameList);
    }

    /**
     * Get a List of all aliases for the given item stack.
     *
     * @param item Item stack whose names to find
     * @return List of all names
     */
    List<String> nameList(ItemStack item);

    /**
     * Get the primary name for the given item stack.
     *
     * @param item Item stack whose name to find
     * @return Primary name of the item
     */
    String name(ItemStack item);

    /**
     * Get all stacks in a given User's inventory that matches the given arguments.
     *
     * @param user The user with the player inventory to search
     * @param args Either an item name, or one of the following:
     *             hand (default), inventory/invent/all, blocks
     * @return A List of all matching ItemStacks
     * @throws Exception if the given args are invalid or no blocks are found
     */
    List<ItemStack> getMatching(User user, String[] args) throws Exception;

    /**
     * Converts the given {@link ItemStack} to a string representation that can be saved.
     * This is typically used for /createkit but can be used by other plugins for various purposes too.
     * Note that this will try registered resolvers first - to avoid this, use {@link net.ess3.api.IItemDb#serialize(ItemStack, boolean)} instead.
     *
     * @param is The stack to serialize
     * @return A string representing the given stack
     */
    String serialize(ItemStack is);

    /**
     * Return names recognised by the database, intended for tab-completion.
     *
     * @return Collection of all item names
     */
    Collection<String> listNames();

    /**
     * Get the material matching the given legacy ID. Used for conversion from item IDs to
     * modern names.
     *
     * @param id Legacy ID of material to find
     * @return Updated material
     */
    @Deprecated
    default Material getFromLegacyId(final int id) {
        return getFromLegacy(id, (byte) 0);
    }

    /**
     * Get the legacy ID for the given material.
     *
     * @param material Material to look up
     * @return Legacy ID of given material
     * @throws Exception if the ID cannot be looked up
     * @deprecated Item IDs are no longer supported.
     */
    @Deprecated
    int getLegacyId(Material material) throws Exception;

    /**
     * Convert colon syntax (eg. "13", "1:5") legacy IDs to Material. Used for conversion from
     * item IDs to modern names.
     *
     * @param item Legacy ID in colon syntax.
     * @return Material if an appropriate material exists, else null.
     */
    default Material getFromLegacy(final String item) {
        final String[] split = item.split(":");

        if (!NumberUtil.isInt(split[0])) return null;

        final int id = Integer.parseInt(split[0]);
        byte damage = 0;

        if (split.length > 1 && NumberUtil.isInt(split[1])) {
            damage = Byte.parseByte(split[1]);
        }

        return getFromLegacy(id, damage);
    }

    /**
     * Convert legacy ID and damage value to Material. Used for conversion from item IDs to
     * modern names.
     *
     * @param id     Legacy ID
     * @param damage Damage value
     * @return Material
     */
    default Material getFromLegacy(final int id, final byte damage) {
        return MaterialUtil.convertFromLegacy(id, damage);
    }

    /**
     * Check whether the item database is loaded and ready for use.
     *
     * @return Whether items have finished loading
     */
    boolean isReady();
}
