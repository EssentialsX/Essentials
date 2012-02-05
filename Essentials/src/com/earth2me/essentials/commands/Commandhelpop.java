package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.Util;
import com.earth2me.essentials.api.IUser;
import com.earth2me.essentials.perm.Permissions;
import java.util.logging.Level;
import org.bukkit.entity.Player;


public class Commandhelpop extends EssentialsCommand
{
	@Override
	public void run(final IUser user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		final String message = _("helpOp", user.getDisplayName(), Util.stripColor(getFinalArg(args, 0)));
		logger.log(Level.INFO, message);
		for (Player onlinePlayer : server.getOnlinePlayers())
		{
			final IUser player = ess.getUser(onlinePlayer);
			if (!Permissions.HELPOP_RECEIVE.isAuthorized(player))
			{
				continue;
			}
			player.sendMessage(message);
		}
	}
}
