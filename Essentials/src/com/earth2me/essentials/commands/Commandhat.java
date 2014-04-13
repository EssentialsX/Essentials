package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n.tl;
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
	protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length > 0 && (args[0].contains("rem") || args[0].contains("off") || args[0].equalsIgnoreCase("0")))
		{
			final PlayerInventory inv = user.getBase().getInventory();
			final ItemStack head = inv.getHelmet();
			if (head == null || head.getType() == Material.AIR)
			{
				user.sendMessage(tl("hatEmpty"));
			}
			else
			{
				final ItemStack air = new ItemStack(Material.AIR);
				inv.setHelmet(air);
				InventoryWorkaround.addItems(user.getBase().getInventory(), head);
				user.sendMessage(tl("hatRemoved"));
			}
		}
		else
		{
			if (user.getBase().getItemInHand().getType() != Material.AIR)
			{
				final ItemStack hand = user.getBase().getItemInHand();
				if (hand.getType().getMaxDurability() == 0)
				{
					final PlayerInventory inv = user.getBase().getInventory();
					final ItemStack head = inv.getHelmet();
					inv.setHelmet(hand);
					inv.setItemInHand(head);
					user.sendMessage(tl("hatPlaced"));
				}
				else
				{
					user.sendMessage(tl("hatArmor"));
				}
			}
			else
			{
				user.sendMessage(tl("hatFail"));
			}
		}
	}
}