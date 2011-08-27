package com.earth2me.essentials;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/*
 * This class can be removed when 
 * https://github.com/Bukkit/CraftBukkit/pull/193
 * is accepted to CraftBukkit
 */

public final class InventoryWorkaround
{
	private InventoryWorkaround()
	{
	}

	public static int first(final Inventory inventory, final ItemStack item, final boolean forceDurability, final boolean forceAmount)
	{
		return next(inventory, item, 0, forceDurability, forceAmount);
	}

	public static int next(final Inventory cinventory, final ItemStack item, final int start, final boolean forceDurability, final boolean forceAmount)
	{
		final ItemStack[] inventory = cinventory.getContents();
		for (int i = start; i < inventory.length; i++)
		{
			final ItemStack cItem = inventory[i];
			if (cItem == null)
			{
				continue;
			}
			if (item.getTypeId() == cItem.getTypeId() && (!forceAmount || item.getAmount() == cItem.getAmount()) && (!forceDurability || cItem.getDurability() == item.getDurability()))
			{
				return i;
			}
		}
		return -1;
	}

	public static int firstPartial(final Inventory cinventory, final ItemStack item, final boolean forceDurability)
	{
		if (item == null)
		{
			return -1;
		}
		final ItemStack[] inventory = cinventory.getContents();
		for (int i = 0; i < inventory.length; i++)
		{
			final ItemStack cItem = inventory[i];
			if (cItem == null)
			{
				continue;
			}
			if (item.getTypeId() == cItem.getTypeId() && cItem.getAmount() < cItem.getType().getMaxStackSize() && (!forceDurability || cItem.getDurability() == item.getDurability()))
			{
				return i;
			}
		}
		return -1;
	}

	public static Map<Integer, ItemStack> addItem(final Inventory cinventory, final boolean forceDurability, final ItemStack... items)
	{
		final Map<Integer, ItemStack> leftover = new HashMap<Integer, ItemStack>();

		/* TODO: some optimization
		 *  - Create a 'firstPartial' with a 'fromIndex'
		 *  - Record the lastPartial per Material
		 *  - Cache firstEmpty result
		 */

		// combine items

		ItemStack[] combined = new ItemStack[items.length];
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
					combined[j] = new ItemStack(items[i].getType(), items[i].getAmount(), items[i].getDurability());
					break;
				}
				if (combined[j].getTypeId() == items[i].getTypeId() && (!forceDurability || combined[j].getDurability() == items[i].getDurability()))
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
				final int firstPartial = firstPartial(cinventory, item, forceDurability);

				// Drat! no partial stack
				if (firstPartial == -1)
				{
					// Find a free spot!
					final int firstFree = cinventory.firstEmpty();

					if (firstFree == -1)
					{
						// No space at all!
						leftover.put(i, item);
						break;
					}
					else
					{
						// More than a single stack!
						if (item.getAmount() > item.getType().getMaxStackSize())
						{
							cinventory.setItem(firstFree, new ItemStack(item.getTypeId(), item.getType().getMaxStackSize(), item.getDurability()));
							item.setAmount(item.getAmount() - item.getType().getMaxStackSize());
						}
						else
						{
							// Just store it
							cinventory.setItem(firstFree, item);
							break;
						}
					}
				}
				else
				{
					// So, apparently it might only partially fit, well lets do just that
					final ItemStack partialItem = cinventory.getItem(firstPartial);

					final int amount = item.getAmount();
					final int partialAmount = partialItem.getAmount();
					final int maxAmount = partialItem.getType().getMaxStackSize();

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

	public static Map<Integer, ItemStack> removeItem(final Inventory cinventory, final boolean forceDurability, final ItemStack... items)
	{
		final Map<Integer, ItemStack> leftover = new HashMap<Integer, ItemStack>();

		// TODO: optimization

		for (int i = 0; i < items.length; i++)
		{
			final ItemStack item = items[i];
			if (item == null)
			{
				continue;
			}
			int toDelete = item.getAmount();

			while (true)
			{

				// Bail when done
				if (toDelete <= 0)
				{
					break;
				}

				// get first Item, ignore the amount
				final int first = first(cinventory, item, forceDurability, false);

				// Drat! we don't have this type in the inventory
				if (first == -1)
				{
					item.setAmount(toDelete);
					leftover.put(i, item);
					break;
				}
				else
				{
					final ItemStack itemStack = cinventory.getItem(first);
					final int amount = itemStack.getAmount();

					if (amount <= toDelete)
					{
						toDelete -= amount;
						// clear the slot, all used up
						cinventory.clear(first);
					}
					else
					{
						// split the stack and store
						itemStack.setAmount(amount - toDelete);
						cinventory.setItem(first, itemStack);
						toDelete = 0;
					}
				}
			}
		}
		return leftover;
	}

	public static boolean containsItem(final Inventory cinventory, final boolean forceDurability, final ItemStack... items)
	{
		final Map<Integer, ItemStack> leftover = new HashMap<Integer, ItemStack>();

		// TODO: optimization

		// combine items

		ItemStack[] combined = new ItemStack[items.length];
		for (int i = 0; i < items.length; i++)
		{
			if (items[i] == null)
			{
				continue;
			}
			for (int j = 0; j < combined.length; j++)
			{
				if (combined[j] == null)
				{
					combined[j] = new ItemStack(items[i].getType(), items[i].getAmount(), items[i].getDurability());
					break;
				}
				if (combined[j].getTypeId() == items[i].getTypeId() && (!forceDurability || combined[j].getDurability() == items[i].getDurability()))
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
			int mustHave = item.getAmount();
			int position = 0;

			while (true)
			{
				// Bail when done
				if (mustHave <= 0)
				{
					break;
				}

				final int slot = next(cinventory, item, position, forceDurability, false);

				// Drat! we don't have this type in the inventory
				if (slot == -1)
				{
					leftover.put(i, item);
					break;
				}
				else
				{
					final ItemStack itemStack = cinventory.getItem(slot);
					final int amount = itemStack.getAmount();

					if (amount <= mustHave)
					{
						mustHave -= amount;
					}
					else
					{
						mustHave = 0;
					}
					position = slot + 1;
				}
			}
		}
		return leftover.isEmpty();
	}

	public static Item[] dropItem(final Location loc, final ItemStack itm)
	{
		final int maxStackSize = itm.getType().getMaxStackSize();
		final int stacks = itm.getAmount() / maxStackSize;
		final int leftover = itm.getAmount() % maxStackSize;
		Item[] itemStacks = new Item[stacks + (leftover > 0 ? 1 : 0)];
		for (int i = 0; i < stacks; i++)
		{
			itemStacks[i] = loc.getWorld().dropItem(loc, new ItemStack(itm.getType(), maxStackSize, itm.getDurability()));
		}
		if (leftover > 0)
		{
			itemStacks[stacks] = loc.getWorld().dropItem(loc, new ItemStack(itm.getType(), leftover, itm.getDurability()));
		}
		return itemStacks;
	}
}
