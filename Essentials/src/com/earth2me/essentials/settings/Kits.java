package com.earth2me.essentials.settings;

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
public class Kits implements StorageObject
{
	public Kits()
	{
		final Kit kit = new Kit();
		kit.setDelay(10.0);
		kit.getItems().add(new ItemStack(Material.DIAMOND_SPADE, 1));
		kit.getItems().add(new ItemStack(Material.DIAMOND_PICKAXE, 1));
		kit.getItems().add(new ItemStack(Material.DIAMOND_AXE, 1));
		kits.put("tools", kit);
	}
	@MapValueType(Kit.class)
	private Map<String, Kit> kits = new HashMap<String, Kit>();	
}
