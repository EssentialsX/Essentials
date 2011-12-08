package com.earth2me.essentials.api;

import org.bukkit.inventory.ItemStack;


public interface IWorth extends IReload
{
	double getPrice(ItemStack itemStack);

	void setPrice(ItemStack itemStack, double price);
}
