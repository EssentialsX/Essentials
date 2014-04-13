package com.earth2me.essentials.commands;

import com.earth2me.essentials.CommandSource;
import com.earth2me.essentials.Console;
import static com.earth2me.essentials.I18n.tl;
import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.DateUtil;
import java.util.GregorianCalendar;
import java.util.logging.Level;
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
		if (!user.getBase().isOnline())
		{
			if (sender.isPlayer()
				&& !ess.getUser(sender.getPlayer()).isAuthorized("essentials.tempban.offline"))
			{
				sender.sendMessage(tl("tempbanExemptOffline"));
				return;
			}
		}
		else
		{
			if (user.isAuthorized("essentials.tempban.exempt") && sender.isPlayer())
			{
				sender.sendMessage(tl("tempbanExempt"));
				return;
			}
		}
		final String time = getFinalArg(args, 1);
		final long banTimestamp = DateUtil.parseDateDiff(time, true);

		final long maxBanLength = ess.getSettings().getMaxTempban() * 1000;
		if (maxBanLength > 0 && ((banTimestamp - GregorianCalendar.getInstance().getTimeInMillis()) > maxBanLength)
			&& sender.isPlayer() && !(ess.getUser(sender.getPlayer()).isAuthorized("essentials.tempban.unlimited")))
		{
			sender.sendMessage(tl("oversizedTempban"));
			throw new NoChargeException();
		}

		final String senderName = sender.isPlayer() ? sender.getPlayer().getDisplayName() : Console.NAME;
		final String banReason = tl("tempBanned", DateUtil.formatDateDiff(banTimestamp), senderName);
		user.setBanReason(banReason);
		user.setBanTimeout(banTimestamp);
		user.getBase().setBanned(true);
		user.getBase().kickPlayer(banReason);

		final String message = tl("playerBanned", senderName, user.getName(), banReason, DateUtil.formatDateDiff(banTimestamp));
		server.getLogger().log(Level.INFO, message);
		ess.broadcastMessage("essentials.ban.notify", message);
	}
}
