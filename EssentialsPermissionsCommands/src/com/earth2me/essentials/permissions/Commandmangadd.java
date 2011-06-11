package com.earth2me.essentials.permissions;

import com.earth2me.essentials.commands.EssentialsCommand;
import com.earth2me.essentials.commands.NotEnoughArgumentsException;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public class Commandmangadd extends EssentialsCommand
{
	public Commandmangadd()
	{
		super("mangadd");
	}

	@Override
	protected void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}
		final String group = args[0];

		ess.getServer().dispatchCommand(sender, "/permissions g:"+group+" create");
	}
	
	
}
