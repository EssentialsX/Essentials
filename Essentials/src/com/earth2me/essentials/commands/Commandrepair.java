/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;


/**
 *
 * @author Seiji
 */
public class Commandrepair extends EssentialsCommand
{
	public Commandrepair()
	{
		super("repair");
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		if (args[0].equalsIgnoreCase("hand"))
		{
			ItemStack item = user.getItemInHand();
			try
			{
				repairItem(item);
			}
			catch (Exception e)
			{
				user.sendMessage(e.getMessage());
				return;
			}

			String itemName = item.getType().toString().toLowerCase().replace('_', ' ');
			user.sendMessage(Util.format("repair", itemName));
		}
		else if (args[0].equalsIgnoreCase("all"))
		{
			StringBuilder itemList = new StringBuilder();
			itemList.append(repairItems(user.getInventory().getContents()));
			
			String armor = repairItems(user.getInventory().getArmorContents());
			
			if(armor.length() > 0) 
			{
				if(itemList.length() > 0)
				{
					itemList.append(", ");
				}
				
				itemList.append(armor);
			}

			if (itemList.length() == 0)
			{
				user.sendMessage(Util.format("repairNone"));
			}
			else
			{
				user.sendMessage(Util.format("repair", itemList.toString()));
			}

		}
		else
		{
			throw new NotEnoughArgumentsException();
		}
	}

	private void repairItem(ItemStack item) throws Exception
	{
		Material material = Material.getMaterial(item.getTypeId());
		String error = null;
		if (material.isBlock() || material.getMaxDurability() < 0)
		{
			throw new Exception(Util.i18n("repairInvalidType"));
		}
		
		if (item.getDurability() == 0)
		{
			throw new Exception(Util.i18n("repairAlreadyFixed"));
		}
		
		item.setDurability((short)0);
	}
	
	private String repairItems(ItemStack[] items)
	{
		StringBuilder itemList = new StringBuilder();
		for (ItemStack item : items)
		{
			try
			{
				repairItem(item);
				if (itemList.length() > 0)
				{
					itemList.append(", ");
				}

				String itemName = item.getType().toString().toLowerCase().replace('_', ' ');
				itemList.append(itemName);
			}
			catch (Exception e)
			{
			}

		}

		return itemList.toString();
	}
}
