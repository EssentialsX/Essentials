package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import java.util.Arrays;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;


public class Commandinvsee extends EssentialsCommand
{
	public Commandinvsee()
	{
		super("invsee");
	}

	@Override
	protected void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		final User invUser = getPlayer(server, args, 0);	
		user.setInvSee(true);
		user.openInventory(invUser.getInventory());		
	}
}
