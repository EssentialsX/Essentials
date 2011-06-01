package com.earth2me.essentials.commands;

import com.earth2me.essentials.Charge;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.Server;


public class Commandback extends EssentialsCommand
{
	public Commandback()
	{
		super("back");
	}

	@Override
	protected void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		Charge charge = new Charge(this.getName(), ess);
		charge.isAffordableFor(user);
		user.sendMessage(Util.i18n("backUsageMsg"));
		user.getTeleport().back(charge);
	}
}
