package com.earth2me.essentials;

import java.util.HashMap;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

/*
 * This class can be removed when 
 * https://github.com/Bukkit/CraftBukkit/pull/193
 * is accepted to CraftBukkit
 */
public class InventoryWorkaround {

	public static int first(CraftInventory ci,  ItemStack item, boolean forceDurability, boolean forceAmount) {
		return next(ci, item, 0, forceDurability, forceAmount);
	}

	public static int next(CraftInventory ci, ItemStack item, int start, boolean forceDurability, boolean forceAmount) {
		CraftItemStack[] inventory = ci.getContents();
		for (int i = start; i < inventory.length; i++) {
			CraftItemStack cItem = inventory[i];
			if (item.getTypeId() == cItem.getTypeId() && (!forceAmount || item.getAmount() == cItem.getAmount()) && (!forceDurability || cItem.getDurability() == item.getDurability())) {
				return i;
			}
		}
		return -1;
	}

	public static HashMap<Integer, ItemStack> removeItem(CraftInventory ci, boolean forceDurability, ItemStack... items) {
		HashMap<Integer, ItemStack> leftover = new HashMap<Integer, ItemStack>();

		// TODO: optimization

		for (int i = 0; i < items.length; i++) {
			ItemStack item = items[i];
			if (item == null) {
				continue;
			}
			int toDelete = item.getAmount();

			while (true) {

				// Bail when done
				if (toDelete <= 0) {
					break;
				}

				// get first Item, ignore the amount
				int first = first(ci, item, forceDurability, false);

				// Drat! we don't have this type in the inventory
				if (first == -1) {
					item.setAmount(toDelete);
					leftover.put(i, item);
					break;
				} else {
					CraftItemStack itemStack = ci.getItem(first);
					int amount = itemStack.getAmount();

					if (amount <= toDelete) {
						toDelete -= amount;
						// clear the slot, all used up
						ci.clear(first);
					} else {
						// split the stack and store
						itemStack.setAmount(amount - toDelete);
						ci.setItem(first, itemStack);
						toDelete = 0;
					}
				}
			}
		}
		return leftover;
	}

	public static boolean containsItem(CraftInventory ci, boolean forceDurability, ItemStack... items) {
		HashMap<Integer, ItemStack> leftover = new HashMap<Integer, ItemStack>();

		// TODO: optimization

		// combine items

		ItemStack[] combined = new ItemStack[items.length];
		for (int i = 0; i < items.length; i++) {
			if (items[i] == null) {
				continue;
			}
			for (int j = 0; j < combined.length; j++) {
				if (combined[j] == null) {
					combined[j] = new ItemStack(items[i].getType(), items[i].getAmount(), items[i].getDurability());
					break;
				}
				if (combined[j].getTypeId() == items[i].getTypeId() && (!forceDurability || combined[j].getDurability() == items[i].getDurability())) {
					combined[j].setAmount(combined[j].getAmount() + items[i].getAmount());
					break;
				}
			}
		}

		for (int i = 0; i < combined.length; i++) {
			ItemStack item = combined[i];
			if (item == null) {
				continue;
			}
			int mustHave = item.getAmount();
			int position = 0;

			while (true) {
				// Bail when done
				if (mustHave <= 0) {
					break;
				}

				int slot = next(ci, item, position, forceDurability, false);

				// Drat! we don't have this type in the inventory
				if (slot == -1) {
					leftover.put(i, item);
					break;
				} else {
					CraftItemStack itemStack = ci.getItem(slot);
					int amount = itemStack.getAmount();

					if (amount <= mustHave) {
						mustHave -= amount;
					} else {
						mustHave = 0;
					}
					position = slot + 1;
				}
			}
		}
		return leftover.isEmpty();
	}
}
