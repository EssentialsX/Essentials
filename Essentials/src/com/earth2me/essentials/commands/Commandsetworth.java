package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.IUser;
import org.bukkit.inventory.ItemStack;


public class Commandsetworth extends EssentialsCommand
{
	@Override
	public void run(final IUser user, final String[] args) throws Exception
	{
		if (args.length < 2)
		{
			throw new NotEnoughArgumentsException();
		}

		ItemStack stack = ess.getItemDb().get(args[0]);
		ess.getWorth().setPrice(stack, Double.parseDouble(args[1]));
		user.sendMessage(_("worthSet"));
	}
}
