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
	protected void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		extinguishPlayers(server, sender, args[0]);
	}

	@Override
	public void run(Server server, User user, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			user.setFireTicks(0);
			user.sendMessage(Util.i18n("extinguish"));
			return;
		}

		extinguishPlayers(server, user, commandLabel);
	}

	private void extinguishPlayers(Server server, CommandSender sender, String name) throws Exception
	{
		for (Player p : server.matchPlayer(name))
		{
			p.setFireTicks(0);
			sender.sendMessage(Util.format("extinguishOthers", p.getDisplayName()));
		}
	}
}
