package com.earth2me.essentials.craftbukkit;

import com.earth2me.essentials.utils.MaterialUtil;
import com.earth2me.essentials.utils.VersionUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public final class Inventories {
    private static final int HELM_SLOT = 39;
    private static final int CHEST_SLOT = 38;
    private static final int LEG_SLOT = 37;
    private static final int BOOT_SLOT = 36;
    private static final boolean IS_OFFHAND = VersionUtil.getServerBukkitVersion().isHigherThanOrEqualTo(VersionUtil.v1_9_R01);

    private Inventories() {
    }

    public static boolean hasSpace(final Player player, final int maxStack, final boolean includeArmor, ItemStack... items) {
        items = normalizeItems(cloneItems(items));
        final InventoryData inventoryData = parseInventoryData(player.getInventory(), items, maxStack, includeArmor);
        System.out.println(inventoryData.getEmptySlots());
        System.out.println(inventoryData.getPartialSlots());

        final List<Integer> emptySlots = inventoryData.getEmptySlots();
        for (final ItemStack item : items) {
            if (item == null || MaterialUtil.isAir(item.getType())) {
                continue;
            }

            final int itemMax = Math.max(maxStack, item.getMaxStackSize());
            final List<Integer> partialSlots = inventoryData.getPartialSlots().get(item);
            while (true) {
                if (partialSlots == null || partialSlots.isEmpty()) {
                    if (emptySlots.isEmpty()) {
                        return false;
                    }

                    final int slot = emptySlots.remove(0);
                    if (slot == HELM_SLOT && !MaterialUtil.isHelmet(item.getType())) {
                        continue;
                    } else if (slot == CHEST_SLOT && !MaterialUtil.isChestplate(item.getType())) {
                        continue;
                    } else if (slot == LEG_SLOT && !MaterialUtil.isLeggings(item.getType())) {
                        continue;
                    } else if (slot == BOOT_SLOT && !MaterialUtil.isBoots(item.getType())) {
                        continue;
                    }

                    if (item.getAmount() > itemMax) {
                        item.setAmount(item.getAmount() - itemMax);
                    } else {
                        break;
                    }
                } else {
                    final int slot = partialSlots.remove(0);
                    final ItemStack existing = player.getInventory().getItem(slot);

                    final int amount = item.getAmount();
                    //noinspection ConstantConditions
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
        System.out.println(inventoryData.getEmptySlots());
        System.out.println(inventoryData.getPartialSlots());

        final List<Integer> emptySlots = inventoryData.getEmptySlots();
        for (int i = 0; i < items.length; i++) {
            final ItemStack item = items[i];
            if (item == null || MaterialUtil.isAir(item.getType())) {
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
                    if (slot == HELM_SLOT && !MaterialUtil.isHelmet(item.getType())) {
                        continue;
                    } else if (slot == CHEST_SLOT && !MaterialUtil.isChestplate(item.getType())) {
                        continue;
                    } else if (slot == LEG_SLOT && !MaterialUtil.isLeggings(item.getType())) {
                        continue;
                    } else if (slot == BOOT_SLOT && !MaterialUtil.isBoots(item.getType())) {
                        continue;
                    }

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
                    final ItemStack existing = player.getInventory().getItem(slot);

                    final int amount = item.getAmount();
                    //noinspection ConstantConditions
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
        final ItemStack[] items = new ItemStack[41];
        for (int i = 0; i < items.length; i++) {
            if (!includeArmor && isArmorSlot(i)) {
                items[i] = null;
                continue;
            }

            items[i] = player.getInventory().getItem(i);
        }

        return items;
    }

    public static void removeItem(final Player player, final ItemStack toRemove, final boolean includeArmor) {
        removeItem(player, itemStack -> itemStack.equals(toRemove), includeArmor);
    }

    public static void removeItem(final Player player, final Predicate<ItemStack> removePredicate, final boolean includeArmor) {
        final ItemStack[] items = player.getInventory().getContents();
        for (int i = 0; i < items.length; i++) {
            if (!includeArmor && isArmorSlot(i)) {
                continue;
            }

            final ItemStack item = items[i];
            if (item == null || MaterialUtil.isAir(item.getType())) {
                continue;
            }

            if (removePredicate.test(item)) {
                item.setAmount(0);
                player.getInventory().setItem(i, item);
            }
        }
    }

    public static void clearSlot(final Player player, final int slot) {
        final ItemStack item = player.getInventory().getItem(slot);
        if (item != null && !MaterialUtil.isAir(item.getType())) {
            item.setAmount(0);
            player.getInventory().setItem(slot, item);
        }
    }

    public static void setSlot(final Player inventory, final int slot, final ItemStack item) {
        inventory.getInventory().setItem(slot, item);
    }

    private static ItemStack[] normalizeItems(final ItemStack[] items) {
        System.out.println(Arrays.toString(items));
        if (items.length <= 1) {
            return items;
        }

        final ItemStack[] normalizedItems = new ItemStack[items.length];
        int nextNormalizedIndex = 0;
        inputLoop:
        for (final ItemStack item : items) {
            if (item == null || MaterialUtil.isAir(item.getType())) {
                continue;
            }

            for (int j = 0; j < nextNormalizedIndex; j++) {
                final ItemStack normalizedItem = normalizedItems[j];
                if (normalizedItem == null || MaterialUtil.isAir(normalizedItem.getType())) {
                    continue;
                }

                if (item.isSimilar(normalizedItem)) {
                    normalizedItem.setAmount(normalizedItem.getAmount() + item.getAmount());
                    continue inputLoop;
                }
            }
            normalizedItems[nextNormalizedIndex++] = item;
        }

        System.out.println(Arrays.toString(normalizedItems));
        return normalizedItems;
    }

    private static ItemStack[] cloneItems(final ItemStack[] items) {
        final ItemStack[] clonedItems = new ItemStack[items.length];
        for (int i = 0; i < items.length; i++) {
            final ItemStack item = items[i];
            if (item == null || MaterialUtil.isAir(item.getType())) {
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
            if (invItem == null || MaterialUtil.isAir(invItem.getType())) {
                emptySlots.add(i);
            } else {
                for (final ItemStack newItem : items) {
                    if (invItem.getAmount() < Math.max(maxStack, invItem.getMaxStackSize()) && invItem.isSimilar(newItem)) {
                        partialSlots.computeIfAbsent(newItem, k -> new ArrayList<>()).add(i);
                    }
                }
            }
        }
        return new InventoryData(emptySlots, partialSlots);
    }

    private static boolean isArmorSlot(final int slot) {
        return slot == HELM_SLOT || slot == CHEST_SLOT || slot == LEG_SLOT || slot == BOOT_SLOT;
    }
}
