package com.earth2me.essentials.settings;

import com.earth2me.essentials.storage.EnchantmentLevel;
import com.earth2me.essentials.storage.MapKeyType;
import com.earth2me.essentials.storage.MapValueType;
import com.earth2me.essentials.storage.StorageObject;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.material.MaterialData;


@Data
@EqualsAndHashCode(callSuper = false)
public class Worth implements StorageObject
{
	@MapKeyType(MaterialData.class)
	@MapValueType(Double.class)
	private Map<MaterialData, Double> sell = new HashMap<MaterialData, Double>();
	@MapKeyType(MaterialData.class)
	@MapValueType(Double.class)
	private Map<MaterialData, Double> buy = new HashMap<MaterialData, Double>();
	@MapKeyType(EnchantmentLevel.class)
	@MapValueType(Double.class)
	private Map<EnchantmentLevel, Double> enchantmentMultiplier = new HashMap<EnchantmentLevel, Double>();

	public Worth()
	{
		sell.put(new MaterialData(Material.APPLE, (byte)0), 1.0);
	}
}
