package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import org.bukkit.Server;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;


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
		if (!player.isOnline())
		{
			throw new NoSuchFieldException(_("playerNotFound"));
		}

		// Verify permission
		if (!player.isHidden() || user.isAuthorized("essentials.teleport.hidden"))
		{
			player.getTeleport().now(user, false, TeleportCause.COMMAND);
			user.sendMessage(_("teleporting"));
		}
		else
		{
			throw new NoSuchFieldException(_("playerNotFound"));
		}
	}
}
