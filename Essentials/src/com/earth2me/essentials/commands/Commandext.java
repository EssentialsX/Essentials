package com.earth2me.essentials.commands;

import org.bukkit.Server;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Commandext extends EssentialsCommand
{
	public Commandext()
	{
		super("ext");
	}

	@Override
	protected void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		extinguishPlayers(server, sender, args[0]);
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			user.setFireTicks(0);
			user.sendMessage(Util.i18n("extinguish"));
			return;
		}

		extinguishPlayers(server, user, commandLabel);
	}

	private void extinguishPlayers(final Server server, final CommandSender sender, final String name) throws Exception
	{
		for (Player matchPlayer : server.matchPlayer(name))
		{
			matchPlayer.setFireTicks(0);
			sender.sendMessage(Util.format("extinguishOthers", matchPlayer.getDisplayName()));
		}
	}
}
