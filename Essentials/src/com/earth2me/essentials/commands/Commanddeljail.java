package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import static com.earth2me.essentials.I18n.tl;
import org.bukkit.Server;


public class Commanddeljail extends EssentialsCommand
{
	public Commanddeljail()
	{
		super("deljail");
	}

	@Override
	protected void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		
		ess.getJails().removeJail(args[0]);
		sender.sendMessage(tl("deleteJail", args[0]));
	}
}
