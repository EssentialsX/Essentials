package com.earth2me.essentials.commands;

import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Util;
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
		if (muted)
		{
			if (muteTimestamp > 0)
			{
				sender.sendMessage(_("mutedPlayerFor", player.getDisplayName(), Util.formatDateDiff(muteTimestamp)));
				player.sendMessage(_("playerMutedFor", Util.formatDateDiff(muteTimestamp)));
			}
			else
			{
				sender.sendMessage(_("mutedPlayer", player.getDisplayName()));
				player.sendMessage(_("playerMuted"));
			}
			for (Player onlinePlayer : server.getOnlinePlayers())
			{
				final User user = ess.getUser(onlinePlayer);
				if (onlinePlayer != sender && user.isAuthorized("essentials.mute.notify"))
				{
					onlinePlayer.sendMessage(_("muteNotify", sender.getName(), player.getName()));
				}
			}
		}
		else
		{
			sender.sendMessage(_("unmutedPlayer", player.getDisplayName()));
			player.sendMessage(_("playerUnmuted"));
		}
	}
}