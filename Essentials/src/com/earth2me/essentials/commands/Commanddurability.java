package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import org.bukkit.Material;
import org.bukkit.Server;
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
	protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		ItemStack itemStack = user.getItemInHand();
		maxuses = itemStack.getType().getMaxDurability();
		durability = ((itemStack.getType().getMaxDurability() + 1) - itemStack.getDurability());
		if (itemStack.getType() != Material.AIR)
		{
			if (maxuses != 0)
			{
				user.sendMessage(_("durability", Integer.toString(durability)));
			}
			else
			{
				user.sendMessage(_("noDurability"));
			}
		}
		else
		{
			user.sendMessage(_("nothingInHand"));
		}
	}
}	