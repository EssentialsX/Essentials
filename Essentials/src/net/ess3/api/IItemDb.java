package net.ess3.api;

import org.bukkit.inventory.ItemStack;


public interface IItemDb
{
	ItemStack get(final String name, final int quantity) throws Exception;

	ItemStack get(final String name) throws Exception;
}
