package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Util;
import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.perm.Permissions;
import org.bukkit.command.CommandSender;


public class Commandbalance extends EssentialsCommand
{
	@Override
	protected void run(final CommandSender sender, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		sender.sendMessage(_("balance", Util.formatCurrency(getPlayer(args, 0, true).getMoney(), ess)));
	}

	@Override
	public void run(final IUser user, final String[] args) throws Exception
	{
		final double bal = (args.length < 1
							|| !user.isAuthorized(Permissions.BALANCE_OTHERS)
							? user
							: getPlayer(args, 0, true)).getMoney();
		user.sendMessage(_("balance", Util.formatCurrency(bal, ess)));
	}
}
