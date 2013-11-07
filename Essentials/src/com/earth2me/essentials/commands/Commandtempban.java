package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Console;
import static com.earth2me.essentials.I18n._;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.DateUtil;
import java.util.GregorianCalendar;
import org.bukkit.Server;


public class Commandtempban extends EssentialsCommand
{
	public Commandtempban()
	{
		super("tempban");
	}

	@Override
	public void run(final Server server, final CommandSource sender, final String commandLabel, final String[] args) throws Exception
	{
		if (args.length < 2)
		{
			throw new NotEnoughArgumentsException();
		}
		final User user = getPlayer(server, args, 0, true, true);
		if (!user.isOnline())
		{
			if (sender.isPlayer()
				&& !ess.getUser(sender.getPlayer()).isAuthorized("essentials.tempban.offline"))
			{
				sender.sendMessage(_("tempbanExemptOffline"));
				return;
			}
		}
		else
		{
			if (user.isAuthorized("essentials.tempban.exempt") && sender.isPlayer())
			{
				sender.sendMessage(_("tempbanExempt"));
				return;
			}
		}
		final String time = getFinalArg(args, 1);
		final long banTimestamp = DateUtil.parseDateDiff(time, true);

		final long maxBanLength = ess.getSettings().getMaxTempban() * 1000;
		if (maxBanLength > 0 && ((banTimestamp - GregorianCalendar.getInstance().getTimeInMillis()) > maxBanLength)
			&& sender.isPlayer() && !(ess.getUser(sender.getPlayer()).isAuthorized("essentials.tempban.unlimited")))
		{
			sender.sendMessage(_("oversizedTempban"));
			throw new NoChargeException();
		}

		final String senderName = sender.isPlayer() ? sender.getPlayer().getDisplayName() : Console.NAME;
		final String banReason = _("tempBanned", DateUtil.formatDateDiff(banTimestamp), senderName);
		user.setBanReason(banReason);
		user.setBanTimeout(banTimestamp);
		user.setBanned(true);
		user.kickPlayer(banReason);

		ess.broadcastMessage("essentials.ban.notify", _("playerBanned", senderName, user.getName(), banReason, DateUtil.formatDateDiff(banTimestamp)));
	}
}
