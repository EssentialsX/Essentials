package com.earth2me.essentials.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;


public class Commandmute extends EssentialsCommand
{
	public Commandmute()
	{
		super("mute");
	}

	@Override
	public void run(Server server, CommandSender sender, String commandLabel, String[] args) throws Exception
	{
		if (args.length < 1)
		{
			throw new NotEnoughArgumentsException();
		}

		User p = getPlayer(server, args, 0, true);
		if (!p.isMuted() && p.isAuthorized("essentials.mute.exempt"))
		{
			sender.sendMessage(Util.i18n("muteExempt"));
			return;
		}
		long muteTimestamp = 0;
		if (args.length > 1)
		{
			String time = getFinalArg(args, 1);
			muteTimestamp = Util.parseDateDiff(time, true);
		}
		p.setMuteTimeout(muteTimestamp);
		charge(sender);
		boolean muted = p.toggleMuted();
		sender.sendMessage(
				muted
				? (muteTimestamp > 0
				   ? Util.format("mutedPlayerFor", p.getDisplayName(), Util.formatDateDiff(muteTimestamp))
				   : Util.format("mutedPlayer", p.getDisplayName()))
				: Util.format("unmutedPlayer", p.getDisplayName()));
		p.sendMessage(
				muted
				? (muteTimestamp > 0
				   ? Util.format("playerMutedFor", Util.formatDateDiff(muteTimestamp))
				   : Util.i18n("playerMuted"))
				: Util.i18n("playerUnmuted"));
	}
}
