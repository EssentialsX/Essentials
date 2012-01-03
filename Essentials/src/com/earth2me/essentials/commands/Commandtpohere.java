package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.OfflinePlayer;
import com.earth2me.essentials.api.IUser;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;


public class Commandtpohere extends EssentialsCommand
{
	@Override
	public void run(final IUser user, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		//Just basically the old tphere command
		final IUser player = getPlayer(args, 0, true);

		// Check if user is offline
		if (player.getBase() instanceof OfflinePlayer)
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
