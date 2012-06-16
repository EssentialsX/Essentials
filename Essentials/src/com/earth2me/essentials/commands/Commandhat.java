package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import com.earth2me.essentials.craftbukkit.InventoryWorkaround;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;


public class Commandhat extends EssentialsCommand
{
	public Commandhat()
	{
		super("hat");
	}

	@Override
	protected void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			if (user.getItemInHand().getType() != Material.AIR)
			{
				final ItemStack hand = user.getItemInHand();
				if (hand.getType().getMaxDurability() == 0)
				{
					final PlayerInventory inv = user.getInventory();
					final ItemStack head = inv.getHelmet();
					inv.removeItem(hand);
					inv.setHelmet(hand);
					inv.setItemInHand(head);
					user.sendMessage(_("hatPlaced"));
				} else {
					user.sendMessage(_("hatArmor"));
				}
			}
			else
			{
				user.sendMessage(_("hatFail"));
			}
		}
		else
		{
			if (args[0].contains("remove"))
			{
				final PlayerInventory inv = user.getInventory();
				final ItemStack head = inv.getHelmet();
				if (head == null)
				{
					user.sendMessage(_("hatEmpty"));
				}
				else if (head.getType() != Material.AIR)
				{
					final ItemStack air = new ItemStack(Material.AIR);
					inv.setHelmet(air);
					InventoryWorkaround.addItem(user.getInventory(), true, head);
					user.sendMessage(_("hatRemoved"));
				}
			}
		}
	}
}