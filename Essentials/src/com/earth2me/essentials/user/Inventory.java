package com.earth2me.essentials.user;

import com.earth2me.essentials.storage.MapKeyType;
import com.earth2me.essentials.storage.MapValueType;
import com.earth2me.essentials.storage.StorageObject;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;


@Data
@EqualsAndHashCode(callSuper = false)
public class Inventory implements StorageObject
{
	private int size;
	@MapKeyType(Integer.class)
	@MapValueType(ItemStack.class)
	private Map<Integer, ItemStack> items = new HashMap<Integer, ItemStack>();

	public Inventory()
	{
		items.put(1, new ItemStack(Material.APPLE, 64));
	}
}
