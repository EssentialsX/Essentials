package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.api.IUser;
import lombok.Cleanup;


public class Commandtpahere extends EssentialsCommand
{
	@Override
	public void run(final IUser user, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		@Cleanup
		final IUser player = getPlayer(args, 0);
		player.acquireReadLock();
		if (!player.getData().isTeleportEnabled())
		{
			throw new Exception(_("teleportDisabled", player.getDisplayName()));
		}
		player.requestTeleport(user, true);
		player.sendMessage(_("teleportHereRequest", user.getDisplayName()));
		player.sendMessage(_("typeTpaccept"));
		if (ess.getSettings().getTpaAcceptCancellation() != 0)
		{
			player.sendMessage(_("teleportRequestTimeoutInfo", ess.getSettings().getTpaAcceptCancellation()));
		}
		user.sendMessage(_("requestSent", player.getDisplayName()));
	}
}
