package com.earth2me.essentials.craftbukkit;

import com.earth2me.essentials.InventoryWorkaround;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.IInventory;
import net.minecraft.server.PlayerInventory;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftInventoryPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class ShowInventory
{
	public static void showEmptyInventory(final Player player)
	{
		try
		{
			final EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
			final CraftInventoryPlayer inv = new CraftInventoryPlayer(new PlayerInventory(((CraftPlayer)player).getHandle()));
			inv.clear();
			entityPlayer.a((IInventory)inv.getInventory());
		}
		catch (Throwable ex)
		{
			Logger.getLogger("Minecraft").log(Level.SEVERE, null, ex);
		}
	}

	public static void showFilledInventory(final Player player, final ItemStack stack)
	{
		try
		{
			final EntityPlayer entityPlayer = ((CraftPlayer)player).getHandle();
			final CraftInventoryPlayer inv = new CraftInventoryPlayer(new PlayerInventory(((CraftPlayer)player).getHandle()));
			inv.clear();
			InventoryWorkaround.addItem(inv, true, stack);
			entityPlayer.a((IInventory)inv.getInventory());
		}
		catch (Throwable ex)
		{
			Logger.getLogger("Minecraft").log(Level.SEVERE, null, ex);
		}
	}
}
