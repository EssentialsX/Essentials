package com.earth2me.essentials.craftbukkit;

import com.earth2me.essentials.utils.MaterialUtil;
import com.earth2me.essentials.utils.VersionUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public final class Inventories {
    private static final int HELM_SLOT = 39;
    private static final int CHEST_SLOT = 38;
    private static final int LEG_SLOT = 37;
    private static final int BOOT_SLOT = 36;
    private static final boolean HAS_OFFHAND = VersionUtil.getServerBukkitVersion().isHigherThanOrEqualTo(VersionUtil.v1_9_R01);
    private static final int INVENTORY_SIZE = HAS_OFFHAND ? 41 : 40;
    private static final int BASIC_INVENTORY_SIZE = 36;

    private Inventories() {
    }

    public static ItemStack getItemInHand(final Player player) {
        if (!HAS_OFFHAND) {
            //noinspection deprecation
            return player.getInventory().getItemInHand();
        }
        final PlayerInventory inventory = player.getInventory();
        final ItemStack main = inventory.getItemInMainHand();
        return !isEmpty(main) ? main : inventory.getItemInOffHand();
    }

    public static ItemStack getItemInMainHand(final Player player) {
        if (!HAS_OFFHAND) {
            //noinspection deprecation
            return player.getInventory().getItemInHand();
        }
        return player.getInventory().getItemInMainHand();
    }

    public static void setItemInMainHand(final Player player, final ItemStack stack) {
        if (HAS_OFFHAND) {
            player.getInventory().setItemInMainHand(stack);
        } else {
            //noinspection deprecation
            player.setItemInHand(stack);
        }
    }

    public static void setItemInMainHand(final EntityEquipment entityEquipment, final ItemStack stack) {
        if (HAS_OFFHAND) {
            entityEquipment.setItemInMainHand(stack);
        } else {
            //noinspection deprecation
            entityEquipment.setItemInHand(stack);
        }
    }

    public static void setItemInMainHandDropChance(final EntityEquipment entityEquipment, final float chance) {
        if (HAS_OFFHAND) {
            entityEquipment.setItemInMainHandDropChance(chance);
        } else {
            //noinspection deprecation
            entityEquipment.setItemInHandDropChance(chance);
        }
    }

    public static boolean containsAtLeast(final Player player, final ItemStack item, int amount) {
        for (final ItemStack invItem : player.getInventory().getContents()) {
            if (isEmpty(invItem)) {
                continue;
            }
            if (invItem.isSimilar(item)) {
                amount -= invItem.getAmount();
                if (amount <= 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean hasSpace(final Player player, final int maxStack, final boolean includeArmor, ItemStack... items) {
        items = normalizeItems(cloneItems(items));
        final InventoryData inventoryData = parseInventoryData(player.getInventory(), items, maxStack, includeArmor);

        final List<Integer> emptySlots = inventoryData.getEmptySlots();
        for (final ItemStack item : items) {
            if (isEmpty(item)) {
                continue;
            }

            final int itemMax = Math.max(maxStack, item.getMaxStackSize());
            final List<Integer> partialSlots = inventoryData.getPartialSlots().get(item);
            while (true) {
                if (partialSlots == null || partialSlots.isEmpty()) {
                    if (emptySlots.isEmpty()) {
                        return false;
                    }

                    emptySlots.remove(0);
                    if (item.getAmount() > itemMax) {
                        item.setAmount(item.getAmount() - itemMax);
                    } else {
                        break;
                    }
                } else {
                    final int slot = partialSlots.remove(0);
                    ItemStack existing = player.getInventory().getItem(slot);
                    if (isEmpty(existing)) {
                        existing = item.clone();
                        existing.setAmount(0);
                    }

                    final int amount = item.getAmount();
                    final int existingAmount = existing.getAmount();

                    if (amount + existingAmount <= itemMax) {
                        break;
                    } else {
                        item.setAmount(amount + existingAmount - itemMax);
                    }
                }
            }
        }

        return true;
    }

    public static List<ItemStack> addGear(final Player player, final ItemStack... items) {
        final List<ItemStack> leftovers = new ArrayList<>();

        for (int i = 0; i < items.length; i++) {
            if (items[i] != null) {
                final ItemStack playerCurrentGear = player.getInventory().getItem(BASIC_INVENTORY_SIZE + i);

                if (!isEmpty(playerCurrentGear)) {
                    leftovers.add(items[i]);
                }

                player.getInventory().setItem(BASIC_INVENTORY_SIZE + i, items[i]);
            }
        }

        return leftovers;
    }

    public static Map<Integer, ItemStack> addItem(final Player player, final ItemStack... items) {
        return addItem(player, 0, false, items);
    }

    public static Map<Integer, ItemStack> addItem(final Player player, final int maxStack, final ItemStack... items) {
        return addItem(player, maxStack, false, items);
    }

    public static Map<Integer, ItemStack> addItem(final Player player, final int maxStack, final boolean allowArmor, ItemStack... items) {
        items = normalizeItems(cloneItems(items));
        final Map<Integer, ItemStack> leftover = new HashMap<>();
        final InventoryData inventoryData = parseInventoryData(player.getInventory(), items, maxStack, allowArmor);

        final List<Integer> emptySlots = inventoryData.getEmptySlots();
        for (int i = 0; i < items.length; i++) {
            final ItemStack item = items[i];
            if (isEmpty(item)) {
                continue;
            }

            final int itemMax = Math.max(maxStack, item.getMaxStackSize());
            final List<Integer> partialSlots = inventoryData.getPartialSlots().get(item);
            while (true) {
                if (partialSlots == null || partialSlots.isEmpty()) {
                    if (emptySlots.isEmpty()) {
                        leftover.put(i, item);
                        break;
                    }

                    final int slot = emptySlots.remove(0);
                    if (item.getAmount() > itemMax) {
                        final ItemStack split = item.clone();
                        split.setAmount(itemMax);
                        player.getInventory().setItem(slot, split);
                        item.setAmount(item.getAmount() - itemMax);
                    } else {
                        player.getInventory().setItem(slot, item);
                        break;
                    }
                } else {
                    final int slot = partialSlots.remove(0);
                    ItemStack existing = player.getInventory().getItem(slot);
                    if (isEmpty(existing)) {
                        existing = item.clone();
                        existing.setAmount(0);
                    }

                    final int amount = item.getAmount();
                    final int existingAmount = existing.getAmount();

                    if (amount + existingAmount <= itemMax) {
                        existing.setAmount(amount + existingAmount);
                        player.getInventory().setItem(slot, existing);
                        break;
                    } else {
                        existing.setAmount(itemMax);
                        player.getInventory().setItem(slot, existing);
                        item.setAmount(amount + existingAmount - itemMax);
                    }
                }
            }
        }

        return leftover;
    }

    public static ItemStack[] getInventory(final Player player, final boolean includeArmor) {
        final ItemStack[] items = new ItemStack[INVENTORY_SIZE];
        for (int i = 0; i < items.length; i++) {
            if (!includeArmor && isArmorSlot(i)) {
                items[i] = null;
                continue;
            }

            items[i] = player.getInventory().getItem(i);
        }

        return items;
    }

    public static ItemStack[] getInventoryBasicContents(final Player player) {
        final ItemStack[] items = new ItemStack[BASIC_INVENTORY_SIZE];
        for (int i = 0; i < items.length; i++) {
            items[i] = player.getInventory().getItem(i);
        }

        return items;
    }

    public static ItemStack[] getInventoryGear(final Player player) {
        final ItemStack[] items = new ItemStack[INVENTORY_SIZE - BASIC_INVENTORY_SIZE];
        for (int i = 0; i < items.length; i++) {
            items[i] = player.getInventory().getItem(BASIC_INVENTORY_SIZE + i);
        }

        return items;
    }

    public static void removeItemExact(final Player player, final ItemStack toRemove, final boolean includeArmor) {
        removeItems(player, itemStack -> itemStack.equals(toRemove), includeArmor);
    }

    public static int removeItemSimilar(final Player player, final ItemStack toRemove, final boolean includeArmor) {
        return removeItems(player, itemStack -> itemStack.isSimilar(toRemove), includeArmor);
    }

    public static int removeItems(final Player player, final Predicate<ItemStack> removePredicate, final boolean includeArmor) {
        int removedAmount = 0;
        final ItemStack[] items = player.getInventory().getContents();
        for (int i = 0; i < items.length; i++) {
            if (!includeArmor && isArmorSlot(i)) {
                continue;
            }

            final ItemStack item = items[i];
            if (isEmpty(item)) {
                continue;
            }

            if (removePredicate.test(item)) {
                removedAmount += item.getAmount();
                item.setAmount(0);
                player.getInventory().setItem(i, item);
            }
        }
        return removedAmount;
    }

    public static boolean removeItemAmount(final Player player, final ItemStack toRemove, int amount) {
        final List<Integer> clearSlots = new ArrayList<>();
        final ItemStack[] items = player.getInventory().getContents();

        for (int i = 0; i < items.length; i++) {
            final ItemStack item = items[i];
            if (isEmpty(item)) {
                continue;
            }

            if (item.isSimilar(toRemove)) {
                if (item.getAmount() >= amount) {
                    item.setAmount(item.getAmount() - amount);
                    player.getInventory().setItem(i, item);
                    for (final int slot : clearSlots) {
                        clearSlot(player, slot);
                    }
                    return true;
                } else {
                    amount -= item.getAmount();
                    clearSlots.add(i);
                }

                if (amount == 0) {
                    for (final int slot : clearSlots) {
                        clearSlot(player, slot);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public static void clearSlot(final Player player, final int slot) {
        final ItemStack item = player.getInventory().getItem(slot);
        if (!isEmpty(item)) {
            item.setAmount(0);
            player.getInventory().setItem(slot, item);
        }
    }

    public static void setSlot(final Player inventory, final int slot, final ItemStack item) {
        inventory.getInventory().setItem(slot, item);
    }

    private static ItemStack[] normalizeItems(final ItemStack[] items) {
        if (items.length <= 1) {
            return items;
        }

        final ItemStack[] normalizedItems = new ItemStack[items.length];
        int nextNormalizedIndex = 0;
        inputLoop:
        for (final ItemStack item : items) {
            if (isEmpty(item)) {
                continue;
            }

            for (int j = 0; j < nextNormalizedIndex; j++) {
                final ItemStack normalizedItem = normalizedItems[j];
                if (isEmpty(normalizedItem)) {
                    continue;
                }

                if (item.isSimilar(normalizedItem)) {
                    normalizedItem.setAmount(normalizedItem.getAmount() + item.getAmount());
                    continue inputLoop;
                }
            }
            normalizedItems[nextNormalizedIndex++] = item;
        }

        return normalizedItems;
    }

    private static ItemStack[] cloneItems(final ItemStack[] items) {
        final ItemStack[] clonedItems = new ItemStack[items.length];
        for (int i = 0; i < items.length; i++) {
            final ItemStack item = items[i];
            if (isEmpty(item)) {
                continue;
            }

            clonedItems[i] = item.clone();
        }

        return clonedItems;
    }

    private static InventoryData parseInventoryData(final Inventory inventory, final ItemStack[] items, final int maxStack, final boolean includeArmor) {
        final ItemStack[] inventoryContents = inventory.getContents();
        final List<Integer> emptySlots = new ArrayList<>();
        final HashMap<ItemStack, List<Integer>> partialSlots = new HashMap<>();

        for (int i = 0; i < inventoryContents.length; i++) {
            if (!includeArmor && isArmorSlot(i)) {
                continue;
            }

            final ItemStack invItem = inventoryContents[i];
            if (isEmpty(invItem)) {
                emptySlots.add(i);
            } else {
                for (final ItemStack newItem : items) {
                    if (invItem.getAmount() < Math.max(maxStack, invItem.getMaxStackSize()) && invItem.isSimilar(newItem)) {
                        partialSlots.computeIfAbsent(newItem, k -> new ArrayList<>()).add(i);
                    }
                }
            }
        }

        // Convert empty armor slots to partial slots if we have armor items in the inventory, otherwise remove them from the empty slots.
        if (includeArmor) {
            ItemStack helm = null;
            ItemStack chest = null;
            ItemStack legs = null;
            ItemStack boots = null;
            for (final ItemStack item : items) {
                if (isEmpty(item)) {
                    continue;
                }
                if (helm == null && MaterialUtil.isHelmet(item.getType())) {
                    helm = item;
                    if (emptySlots.contains(HELM_SLOT)) {
                        partialSlots.computeIfAbsent(helm, k -> new ArrayList<>()).add(HELM_SLOT);
                    }
                } else if (chest == null && MaterialUtil.isChestplate(item.getType())) {
                    chest = item;
                    if (emptySlots.contains(CHEST_SLOT)) {
                        partialSlots.computeIfAbsent(chest, k -> new ArrayList<>()).add(CHEST_SLOT);
                    }
                } else if (legs == null && MaterialUtil.isLeggings(item.getType())) {
                    legs = item;
                    if (emptySlots.contains(LEG_SLOT)) {
                        partialSlots.computeIfAbsent(legs, k -> new ArrayList<>()).add(LEG_SLOT);
                    }
                } else if (boots == null && MaterialUtil.isBoots(item.getType())) {
                    boots = item;
                    if (emptySlots.contains(BOOT_SLOT)) {
                        partialSlots.computeIfAbsent(boots, k -> new ArrayList<>()).add(BOOT_SLOT);
                    }
                }
            }
            emptySlots.remove((Object) HELM_SLOT);
            emptySlots.remove((Object) CHEST_SLOT);
            emptySlots.remove((Object) LEG_SLOT);
            emptySlots.remove((Object) BOOT_SLOT);
        }

        return new InventoryData(emptySlots, partialSlots);
    }

    private static boolean isEmpty(final ItemStack stack) {
        return stack == null || MaterialUtil.isAir(stack.getType());
    }

    private static boolean isArmorSlot(final int slot) {
        return slot == HELM_SLOT || slot == CHEST_SLOT || slot == LEG_SLOT || slot == BOOT_SLOT;
    }
}
