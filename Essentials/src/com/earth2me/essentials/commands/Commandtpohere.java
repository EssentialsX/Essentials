package com.earth2me.essentials.commands;

import com.earth2me.essentials.OfflinePlayer;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.Server;


public class Commandtpohere extends EssentialsCommand
{
	public Commandtpohere()
	{
		super("tpohere");
	}

	@Override
	public void run(final Server server, final User user, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		//Just basically the old tphere command
		final User player = getPlayer(server, args, 0, true);

		// Check if user is offline
		if (player.getBase() instanceof OfflinePlayer)
		{
			throw new NoSuchFieldException(Util.i18n("playerNotFound"));
		}

		// Verify permission
		if (!player.isHidden() || user.isAuthorized("essentials.teleport.hidden"))
		{
			player.getTeleport().now(user, false);
			user.sendMessage(Util.i18n("teleporting"));
		}
		else
		{
			throw new NoSuchFieldException(Util.i18n("playerNotFound"));
		}
	}
}
