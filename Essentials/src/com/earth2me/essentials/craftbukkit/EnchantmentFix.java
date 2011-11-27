package com.earth2me.essentials.craftbukkit;

import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.inventory.CraftInventoryPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public class EnchantmentFix
{
	public static void setContents(Inventory inventory, ItemStack[] items)
	{
		CraftInventory cInventory = (CraftInventory)inventory;
		if (cInventory.getContents().length != items.length)
		{
			throw new IllegalArgumentException("Invalid inventory size; expected " + cInventory.getContents().length);
		}

		net.minecraft.server.ItemStack[] mcItems = cInventory.getInventory().getContents();

		for (int i = 0; i < items.length; i++)
		{
			ItemStack item = items[i];
			if (item == null || item.getTypeId() <= 0)
			{
				mcItems[i] = null;
			}
			else
			{
				mcItems[i] = new net.minecraft.server.ItemStack(item.getTypeId(), item.getAmount(), item.getDurability());
				new CraftItemStack(mcItems[i]).addUnsafeEnchantments(item.getEnchantments());
			}
		}
	}

	public static void setItem(Inventory inventory, int index, ItemStack item)
	{
		CraftInventory cInventory = (CraftInventory)inventory;
		if (item == null)
		{
			cInventory.getInventory().setItem(index, null);
		}
		else
		{
			net.minecraft.server.ItemStack stack = new net.minecraft.server.ItemStack(item.getTypeId(), item.getAmount(), item.getDurability());
			new CraftItemStack(stack).addUnsafeEnchantments(item.getEnchantments());
			cInventory.getInventory().setItem(index, stack);
		}
	}
	
	public static void setItemInHand(Inventory inventory, ItemStack item)
	{
		CraftInventoryPlayer cInventory = (CraftInventoryPlayer)inventory;
		if (item == null)
		{
			cInventory.getInventory().setItem(cInventory.getInventory().itemInHandIndex, null);
		}
		else
		{
			net.minecraft.server.ItemStack stack = new net.minecraft.server.ItemStack(item.getTypeId(), item.getAmount(), item.getDurability());
			new CraftItemStack(stack).addUnsafeEnchantments(item.getEnchantments());
			cInventory.getInventory().setItem(cInventory.getInventory().itemInHandIndex, stack);
		}
	}
}
