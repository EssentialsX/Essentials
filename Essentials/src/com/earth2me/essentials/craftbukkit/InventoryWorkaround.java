package com.earth2me.essentials.craftbukkit;

import java.util.HashMap;
import java.util.Map;
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

	public static int first(final Inventory inventory, final ItemStack item, final boolean enforceDurability, final boolean enforceAmount, final boolean enforceEnchantments)
	{
		return next(inventory, item, 0, enforceDurability, enforceAmount, enforceEnchantments);
	}

	public static int next(final Inventory cinventory, final ItemStack item, final int start, final boolean enforceDurability, final boolean enforceAmount, final boolean enforceEnchantments)
	{
		final ItemStack[] inventory = cinventory.getContents();
		for (int i = start; i < inventory.length; i++)
		{
			final ItemStack cItem = inventory[i];
			if (cItem == null)
			{
				continue;
			}
			if (item.getTypeId() == cItem.getTypeId() && (!enforceAmount || item.getAmount() == cItem.getAmount()) && (!enforceDurability || cItem.getDurability() == item.getDurability()) && (!enforceEnchantments || cItem.getEnchantments().equals(item.getEnchantments())))
			{
				return i;
			}
		}
		return -1;
	}

	public static int firstPartial(final Inventory cinventory, final ItemStack item, final boolean enforceDurability)
	{
		return firstPartial(cinventory, item, enforceDurability, item.getType().getMaxStackSize());
	}

	public static int firstPartial(final Inventory cinventory, final ItemStack item, final boolean enforceDurability, final int maxAmount)
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
			if (item.getTypeId() == cItem.getTypeId() && cItem.getAmount() < maxAmount && (!enforceDurability || cItem.getDurability() == item.getDurability()) && cItem.getEnchantments().equals(item.getEnchantments()))
			{
				return i;
			}
		}
		return -1;
	}

	public static boolean addAllItems(final Inventory cinventory, final boolean enforceDurability, final ItemStack... items)
	{
		final Inventory fake = new FakeInventory(cinventory.getContents());
		if (addItem(fake, enforceDurability, items).isEmpty())
		{
			addItem(cinventory, enforceDurability, items);
			return true;
		}
		else
		{
			return false;
		}
	}

	public static Map<Integer, ItemStack> addItem(final Inventory cinventory, final boolean forceDurability, final ItemStack... items)
	{
		return addItem(cinventory, forceDurability, 0, items);
	}

	public static Map<Integer, ItemStack> addItem(final Inventory cinventory, final boolean enforceDurability, final int oversizedStacks, final ItemStack... items)
	{
		final Map<Integer, ItemStack> leftover = new HashMap<Integer, ItemStack>();

		/*
		 * TODO: some optimization - Create a 'firstPartial' with a 'fromIndex' - Record the lastPartial per Material -
		 * Cache firstEmpty result
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
					combined[j] = items[i].clone();
					break;
				}
				if (combined[j].getTypeId() == items[i].getTypeId() && (!enforceDurability || combined[j].getDurability() == items[i].getDurability()) && combined[j].getEnchantments().equals(items[i].getEnchantments()))
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
				final int firstPartial = firstPartial(cinventory, item, enforceDurability, maxAmount);

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
						if (item.getAmount() > maxAmount)
						{
							final ItemStack stack = item.clone();
							stack.setAmount(maxAmount);
							cinventory.setItem(firstFree, stack);
							item.setAmount(item.getAmount() - maxAmount);
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

	public static Map<Integer, ItemStack> removeItem(final Inventory cinventory, final boolean enforceDurability, final boolean enforceEnchantments, final ItemStack... items)
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
				final int first = first(cinventory, item, enforceDurability, false, enforceEnchantments);

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

	public static boolean containsItem(final Inventory cinventory, final boolean enforceDurability, final boolean enforceEnchantments, final ItemStack... items)
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
					combined[j] = items[i].clone();
					break;
				}
				if (combined[j].getTypeId() == items[i].getTypeId() && (!enforceDurability || combined[j].getDurability() == items[i].getDurability()) && (!enforceEnchantments || combined[j].getEnchantments().equals(items[i].getEnchantments())))
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

				final int slot = next(cinventory, item, position, enforceDurability, false, enforceEnchantments);

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
}
