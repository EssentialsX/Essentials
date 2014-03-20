package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n.tl;
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
		final User player = getPlayer(server, user, args, 0);

		if (user.getWorld() != player.getWorld() && ess.getSettings().isWorldTeleportPermissions()
			&& !user.isAuthorized("essentials.worlds." + user.getWorld().getName()))
		{
			throw new Exception(tl("noPerm", "essentials.worlds." + user.getWorld().getName()));
		}

		// Verify permission
		player.getTeleport().now(user.getBase(), false, TeleportCause.COMMAND);
	}
}
