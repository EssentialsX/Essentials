package com.earth2me.essentials.user;

import com.earth2me.essentials.storage.MapKeyType;
import com.earth2me.essentials.storage.MapValueType;
import com.earth2me.essentials.storage.StorageObject;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.inventory.ItemStack;


@Data
@EqualsAndHashCode(callSuper = false)
public class Inventory implements StorageObject
{
	private int size;
	@MapKeyType(Integer.class)
	@MapValueType(ItemStack.class)
	private Map<Integer, ItemStack> items = null;

	public Inventory()
	{
	}

	public Inventory(ItemStack[] contents)
	{
		size = contents.length;
		if (items == null) {
			items = new HashMap<Integer, ItemStack>(size);
		}
		items.clear();
		for (int i = 0; i < contents.length; i++)
		{
			ItemStack itemStack = contents[i];
			if (itemStack == null) {
				continue;
			}
			items.put(i, itemStack);
		}
	}

	public ItemStack[] getBukkitInventory()
	{
		if (items == null) {
			throw new IllegalStateException();
		}
		final ItemStack[] inventory = new ItemStack[size];
		for (Map.Entry<Integer, ItemStack> entry : items.entrySet())
		{
			if (entry.getKey() < 0 || entry.getKey()>= size) {
				continue;
			}
			inventory[entry.getKey()] = entry.getValue();
		}
		return inventory;
	}
}
