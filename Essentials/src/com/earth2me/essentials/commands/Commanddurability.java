package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Commanddurability extends EssentialsCommand
{
	public int durability;
	public int maxuses;
	
	public Commanddurability()
	{
		super("durability");
	}
	
	@Override
	protected void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		ItemStack itemStack = ((Player)sender).getItemInHand();
		maxuses = itemStack.getType().getMaxDurability();
		durability = ((itemStack.getType().getMaxDurability() + 1) - itemStack.getDurability());
		if (itemStack.getType() != Material.AIR)
		{
			if (maxuses != 0)
			{
				sender.sendMessage(_("durability", Integer.toString(durability)));
			}
			else
			{
				sender.sendMessage(_("notATool"));
			}
		}
		else
		{
			sender.sendMessage(_("nothingInHand"));
		}
	}
}	