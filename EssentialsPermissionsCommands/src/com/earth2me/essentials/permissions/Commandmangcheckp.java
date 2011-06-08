package com.earth2me.essentials.permissions;

import com.earth2me.essentials.commands.EssentialsCommand;
import com.earth2me.essentials.commands.NotEnoughArgumentsException;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public class Commandmangcheckp extends EssentialsCommand
{
	public Commandmangcheckp()
	{
		super("mangcheckp");
	}

	@Override
	protected void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 2)
		{
			throw new NotEnoughArgumentsException();
		}
		final String target = args[0];
		final String perm = args[1];
		ess.getServer().dispatchCommand(sender, "/permissions g:"+target+" has "+perm+"");
	}
	
	
}
