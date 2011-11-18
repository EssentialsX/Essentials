package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import java.util.logging.Level;


public class Commandhelpop extends EssentialsCommand
{
	public Commandhelpop()
	{
		super("helpop");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		final String message = Util.format("helpOp", user.getDisplayName(), getFinalArg(args, 0));
		logger.log(Level.INFO, message);
		for (Player onlinePlayer : server.getOnlinePlayers())
		{
			final User player = ess.getUser(onlinePlayer);
			if (!player.isAuthorized("essentials.helpop.receive"))
			{
				continue;
			}
			player.sendMessage(message);
		}
	}
}
