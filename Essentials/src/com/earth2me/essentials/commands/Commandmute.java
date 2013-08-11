package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.DateUtil;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


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

		final User player = getPlayer(server, sender, args, 0);
		if (sender instanceof Player && !player.isMuted() && player.isAuthorized("essentials.mute.exempt"))
		{
			throw new Exception(_("muteExempt"));
		}
		long muteTimestamp = 0;

		if (args.length > 1)
		{
			final String time = getFinalArg(args, 1);
			muteTimestamp = DateUtil.parseDateDiff(time, true);
			player.setMuted(true);
		}
		else
		{
			player.setMuted(!player.getMuted());
		}
		player.setMuteTimeout(muteTimestamp);
		final boolean muted = player.getMuted();
		String muteTime = DateUtil.formatDateDiff(muteTimestamp);
		
		if (muted)
		{
			if (muteTimestamp > 0)
			{
				sender.sendMessage(_("mutedPlayerFor", player.getDisplayName(), muteTime));
				player.sendMessage(_("playerMutedFor", muteTime));
			}
			else
			{
				sender.sendMessage(_("mutedPlayer", player.getDisplayName()));
				player.sendMessage(_("playerMuted"));
			}
			ess.broadcastMessage("essentials.mute.notify", _("muteNotify", sender.getName(), player.getName(), muteTime));
		}
		else
		{
			sender.sendMessage(_("unmutedPlayer", player.getDisplayName()));
			player.sendMessage(_("playerUnmuted"));
		}
	}
}
