package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
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
			throw new Exception(_("muteExempt"));
		}
		long muteTimestamp = 0;

		if (args.length > 1)
		{
			final String time = getFinalArg(args, 1);
			muteTimestamp = Util.parseDateDiff(time, true);
			player.setMuted(true);
		}
		else
		{
			player.setMuted(!player.getMuted());
		}
		player.setMuteTimeout(muteTimestamp);
		final boolean muted = player.getMuted();
		sender.sendMessage(
				muted
				? (muteTimestamp > 0
				   ? _("mutedPlayerFor", player.getDisplayName(), Util.formatDateDiff(muteTimestamp))
				   : _("mutedPlayer", player.getDisplayName()))
				: _("unmutedPlayer", player.getDisplayName()));
		player.sendMessage(
				muted
				? (muteTimestamp > 0
				   ? _("playerMutedFor", Util.formatDateDiff(muteTimestamp))
				   : _("playerMuted"))
				: _("playerUnmuted"));
	}
}
