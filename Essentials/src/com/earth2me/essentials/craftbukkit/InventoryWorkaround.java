package com.earth2me.essentials.craftbukkit;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/*
 * This class can be removed when https://github.com/Bukkit/CraftBukkit/pull/193 is accepted to CraftBukkit
 */

public final class InventoryWorkaround
{
	private InventoryWorkaround()
	{
	}

	private static int firstPartial(final Inventory inventory, final ItemStack item, final int maxAmount)
	{
		if (item == null)
		{
			return -1;
		}
		final ItemStack[] stacks = inventory.getContents();
		for (int i = 0; i < stacks.length; i++)
		{
			final ItemStack cItem = stacks[i];
			if (cItem != null && cItem.getAmount() < maxAmount && cItem.isSimilar(item))
			{
				return i;
			}
		}
		return -1;
	}

	public static boolean addAllItems(final Inventory inventory, final ItemStack... items)
	{
		final Inventory fakeInventory = Bukkit.getServer().createInventory(null, inventory.getType());
		fakeInventory.setContents(inventory.getContents());
		if (addItems(fakeInventory, items).isEmpty())
		{
			addItems(inventory, items);
			return true;
		}
		return false;
	}

	public static Map<Integer, ItemStack> addItems(final Inventory inventory, final ItemStack... items)
	{
		return addOversizedItems(inventory, 0, items);
	}

	public static Map<Integer, ItemStack> addOversizedItems(final Inventory inventory, final int oversizedStacks, final ItemStack... items)
	{
		final Map<Integer, ItemStack> leftover = new HashMap<Integer, ItemStack>();

		/*
		 * TODO: some optimization - Create a 'firstPartial' with a 'fromIndex' - Record the lastPartial per Material -
		 * Cache firstEmpty result
		 */

		// combine items

		final ItemStack[] combined = new ItemStack[items.length];
		for (int i = 0; i < items.length; i++)
		{
			if (items[i] == null || items[i].getAmount() < 1)
			{
				continue;
			}
			for (int j = 0; j < combined.length; j++)
			{
				if (combined[j] == null)
				{
					combined[j] = items[i].clone();
					break;
				}
				if (combined[j].isSimilar(items[i]))
				{
					combined[j].setAmount(combined[j].getAmount() + items[i].getAmount());
					break;
				}
			}
		}


		for (int i = 0; i < combined.length; i++)
		{
			final ItemStack item = combined[i];
			if (item == null)
			{
				continue;
			}

			while (true)
			{
				// Do we already have a stack of it?
				final int maxAmount = oversizedStacks > item.getType().getMaxStackSize() ? oversizedStacks : item.getType().getMaxStackSize();
				final int firstPartial = firstPartial(inventory, item, maxAmount);

				// Drat! no partial stack
				if (firstPartial == -1)
				{
					// Find a free spot!
					final int firstFree = inventory.firstEmpty();

					if (firstFree == -1)
					{
						// No space at all!
						leftover.put(i, item);
						break;
					}
					else
					{
						// More than a single stack!
						if (item.getAmount() > maxAmount)
						{
							final ItemStack stack = item.clone();
							stack.setAmount(maxAmount);
							inventory.setItem(firstFree, stack);
							item.setAmount(item.getAmount() - maxAmount);
						}
						else
						{
							// Just store it
							inventory.setItem(firstFree, item);
							break;
						}
					}
				}
				else
				{
					// So, apparently it might only partially fit, well lets do just that
					final ItemStack partialItem = inventory.getItem(firstPartial);

					final int amount = item.getAmount();
					final int partialAmount = partialItem.getAmount();

					// Check if it fully fits
					if (amount + partialAmount <= maxAmount)
					{
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
}
