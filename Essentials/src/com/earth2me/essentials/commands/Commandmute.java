package com.earth2me.essentials.commands;

import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;


public class Commandmute extends EssentialsCommand
{
	public Commandmute()
	{
		super("mute");
	}

	@Override
	public void run(final Server server, final CommandSender sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		final User player = getPlayer(server, args, 0, true);
		if (!player.isMuted() && player.isAuthorized("essentials.mute.exempt"))
		{
			throw new Exception(Util.i18n("muteExempt"));
		}
		long muteTimestamp = 0;
		if (args.length > 1)
		{
			String time = getFinalArg(args, 1);
			muteTimestamp = Util.parseDateDiff(time, true);
		}
		player.setMuteTimeout(muteTimestamp);
		final boolean muted = player.toggleMuted();
		sender.sendMessage(
				muted
				? (muteTimestamp > 0
				   ? Util.format("mutedPlayerFor", player.getDisplayName(), Util.formatDateDiff(muteTimestamp))
				   : Util.format("mutedPlayer", player.getDisplayName()))
				: Util.format("unmutedPlayer", player.getDisplayName()));
		player.sendMessage(
				muted
				? (muteTimestamp > 0
				   ? Util.format("playerMutedFor", Util.formatDateDiff(muteTimestamp))
				   : Util.i18n("playerMuted"))
				: Util.i18n("playerUnmuted"));
	}
}
