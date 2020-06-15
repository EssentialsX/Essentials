package com.earth2me.essentials.craftbukkit;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/*
 * This class can be removed when https://github.com/Bukkit/CraftBukkit/pull/193 is accepted to CraftBukkit
 */

public final class InventoryWorkaround {
    private InventoryWorkaround() {
    }

    private static int firstPartial(final Inventory inventory, final ItemStack item, final int maxAmount) {
        if (item == null) {
            return -1;
        }
        final ItemStack[] stacks = inventory.getContents();
        for (int i = 0; i < stacks.length; i++) {
            final ItemStack cItem = stacks[i];
            if (cItem != null && cItem.getAmount() < maxAmount && cItem.isSimilar(item)) {
                return i;
            }
        }
        return -1;
    }

    /*
    Spigot 1.9, for whatever reason, decided to merge the armor and main player inventories without providing a way
    to access the main inventory. There's lots of ugly code in here to work around that.
     */
    private static final int USABLE_PLAYER_INV_SIZE = 36;

    private static boolean isCombinedInventory(Inventory inventory) {
        return inventory instanceof PlayerInventory && inventory.getContents().length > USABLE_PLAYER_INV_SIZE;
    }

    // Clears inventory without clearing armor
    public static void clearInventoryNoArmor(PlayerInventory inventory) {
        if (isCombinedInventory(inventory)) {
            for (int i = 0; i < USABLE_PLAYER_INV_SIZE; i++) {
                inventory.setItem(i, null);
            }
        } else {
            inventory.clear();
        }
    }

    private static Inventory makeTruncatedPlayerInventory(PlayerInventory playerInventory) {
        Inventory fakeInventory = Bukkit.getServer().createInventory(null, USABLE_PLAYER_INV_SIZE);
        fakeInventory.setContents(Arrays.copyOf(playerInventory.getContents(), fakeInventory.getSize()));
        return fakeInventory;
    }

    // Returns what it couldn't store
    // This will will abort if it couldn't store all items
    public static Map<Integer, ItemStack> addAllItems(final Inventory inventory, final ItemStack... items) {
        ItemStack[] contents = inventory.getContents();

        final Inventory fakeInventory;
        if (isCombinedInventory(inventory)) {
            fakeInventory = makeTruncatedPlayerInventory((PlayerInventory) inventory);
        } else {
            fakeInventory = Bukkit.getServer().createInventory(null, inventory.getType());
            fakeInventory.setContents(contents);
        }
        Map<Integer, ItemStack> overflow = addItems(fakeInventory, items);
        if (overflow.isEmpty()) {
            addItems(inventory, items);
            return null;
        }
        return addItems(fakeInventory, items);
    }
    
    public static Map<Integer, ItemStack> addAllOversizedItems(final Inventory inventory, final int oversizedStacks, final ItemStack... items) {
        ItemStack[] contents = inventory.getContents();

        final Inventory fakeInventory;
        if (isCombinedInventory(inventory)) {
            fakeInventory = makeTruncatedPlayerInventory((PlayerInventory) inventory);
        } else {
            fakeInventory = Bukkit.getServer().createInventory(null, inventory.getType());
            fakeInventory.setContents(contents);
        }
        Map<Integer, ItemStack> overflow = addOversizedItems(fakeInventory, oversizedStacks, items);
        if (overflow.isEmpty()) {
            addOversizedItems(inventory, oversizedStacks, items);
            return null;
        }
        return overflow;
    }


    // Returns what it couldn't store
    public static Map<Integer, ItemStack> addItems(final Inventory inventory, final ItemStack... items) {
        return addOversizedItems(inventory, 0, items);
    }

    // Returns what it couldn't store
    // Set oversizedStack to below normal stack size to disable oversized stacks
    public static Map<Integer, ItemStack> addOversizedItems(final Inventory inventory, final int oversizedStacks, final ItemStack... items) {
        if (isCombinedInventory(inventory)) {
            Inventory fakeInventory = makeTruncatedPlayerInventory((PlayerInventory) inventory);
            Map<Integer, ItemStack> overflow = addOversizedItems(fakeInventory, oversizedStacks, items);
            for (int i = 0; i < fakeInventory.getContents().length; i++) {
                inventory.setItem(i, fakeInventory.getContents()[i]);
            }
            return overflow;
        }

        final Map<Integer, ItemStack> leftover = new HashMap<>();

		/*
         * TODO: some optimization - Create a 'firstPartial' with a 'fromIndex' - Record the lastPartial per Material -
		 * Cache firstEmpty result
		 */

        // combine items

        final ItemStack[] combined = new ItemStack[items.length];
        for (ItemStack item : items) {
            if (item == null || item.getAmount() < 1) {
                continue;
            }
            for (int j = 0; j < combined.length; j++) {
                if (combined[j] == null) {
                    combined[j] = item.clone();
                    break;
                }
                if (combined[j].isSimilar(item)) {
                    combined[j].setAmount(combined[j].getAmount() + item.getAmount());
                    break;
                }
            }
        }


        for (int i = 0; i < combined.length; i++) {
            final ItemStack item = combined[i];
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }

            while (true) {
                // Do we already have a stack of it?
                final int maxAmount = Math.max(oversizedStacks, item.getType().getMaxStackSize());
                final int firstPartial = firstPartial(inventory, item, maxAmount);

                // Drat! no partial stack
                if (firstPartial == -1) {
                    // Find a free spot!
                    final int firstFree = inventory.firstEmpty();

                    if (firstFree == -1) {
                        // No space at all!
                        leftover.put(i, item);
                        break;
                    } else {
                        // More than a single stack!
                        if (item.getAmount() > maxAmount) {
                            final ItemStack stack = item.clone();
                            stack.setAmount(maxAmount);
                            inventory.setItem(firstFree, stack);
                            item.setAmount(item.getAmount() - maxAmount);
                        } else {
                            // Just store it
                            inventory.setItem(firstFree, item);
                            break;
                        }
                    }
                } else {
                    // So, apparently it might only partially fit, well lets do just that
                    final ItemStack partialItem = inventory.getItem(firstPartial);

                    final int amount = item.getAmount();
                    final int partialAmount = partialItem.getAmount();

                    // Check if it fully fits
                    if (amount + partialAmount <= maxAmount) {
                        partialItem.setAmount(amount + partialAmount);
                        break;
                    }

                    // It fits partially
                    partialItem.setAmount(maxAmount);
                    item.setAmount(amount + partialAmount - maxAmount);
                }
            }
        }
        return leftover;
    }

    // Hot-ish code so cache
    private static Boolean hasMainHandSupport = null;

    @SuppressWarnings("deprecation")
    public static void setItemInMainHand(Player p, ItemStack item) {
        if (hasMainHandSupport == null) {
            try {
                p.getInventory().setItemInMainHand(item);
                hasMainHandSupport = true;
            } catch (Throwable e) {
                p.setItemInHand(item);
                hasMainHandSupport = false;
            }
        } else {
            if (hasMainHandSupport) {
                p.getInventory().setItemInMainHand(item);
            } else {
                p.setItemInHand(item);
            }
        }
    }

    @SuppressWarnings("deprecation")
    public static void setItemInMainHand(EntityEquipment invent, ItemStack item) {
        if (hasMainHandSupport == null) {
            try {
                invent.setItemInMainHand(item);
                hasMainHandSupport = true;
            } catch (Throwable e) {
                invent.setItemInHand(item);
                hasMainHandSupport = false;
            }
        } else {
            if (hasMainHandSupport) {
                invent.setItemInMainHand(item);
            } else {
                invent.setItemInHand(item);
            }
        }
    }

    @SuppressWarnings("deprecation")
    public static void setItemInMainHandDropChance(EntityEquipment invent, float chance) {
        if (hasMainHandSupport == null) {
            try {
                invent.setItemInMainHandDropChance(chance);
                hasMainHandSupport = true;
            } catch (Throwable e) {
                invent.setItemInHandDropChance(chance);
                hasMainHandSupport = false;
            }
        } else {
            if (hasMainHandSupport) {
                invent.setItemInMainHandDropChance(chance);
            } else {
                invent.setItemInHandDropChance(chance);
            }
        }
    }

    public static void setItemInOffHand(Player p, ItemStack item) {
        // This assumes that all builds that support a main hand also support an off hand.
        if (hasMainHandSupport == null || hasMainHandSupport) {
            try {
                p.getInventory().setItemInOffHand(item);
                hasMainHandSupport = true;
            } catch (Throwable e) {
                hasMainHandSupport = false;
            }
        }
    }
}
