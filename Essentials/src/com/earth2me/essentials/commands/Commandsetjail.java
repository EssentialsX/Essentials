package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.Server;


public class Commandsetjail extends EssentialsCommand
{
	public Commandsetjail()
	{
		super("setjail");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		ess.getJails().setJail(args[0], user.getLocation());
		user.sendMessage(_("jailSet", Util.sanitizeString(args[0])));

	}
}
