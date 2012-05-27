package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
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
		if (user.getItemInHand().getType() != Material.AIR)
		{
			final ItemStack hand = user.getItemInHand();
			final PlayerInventory inv = user.getInventory();
			final ItemStack head = inv.getHelmet();
			inv.removeItem(hand);
			inv.setHelmet(hand);
			inv.setItemInHand(head);
			user.sendMessage(_("hatPlaced"));
		}
		else
		{
			user.sendMessage(_("hatFail"));
		}
	}
}
