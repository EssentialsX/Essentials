package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Trade;
import com.earth2me.essentials.api.IUser;


public class Commandback extends EssentialsCommand
{
	@Override
	protected void run(final IUser user, final String[] args) throws Exception
	{
		final Trade charge = new Trade(commandName, ess);
		charge.isAffordableFor(user);
		user.sendMessage(_("backUsageMsg"));
		user.getTeleport().back(charge);
	}
}
